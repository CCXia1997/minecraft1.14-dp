package net.minecraft.client.network.packet;

import javax.annotation.Nullable;
import net.minecraft.network.listener.PacketListener;
import com.google.common.base.MoreObjects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.text.TextComponent;
import net.minecraft.world.GameMode;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.GameProfile;
import net.minecraft.util.PacketByteBuf;
import java.util.Iterator;
import net.minecraft.server.network.ServerPlayerEntity;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class PlayerListS2CPacket implements Packet<ClientPlayPacketListener>
{
    private Action action;
    private final List<Entry> entries;
    
    public PlayerListS2CPacket() {
        this.entries = Lists.newArrayList();
    }
    
    public PlayerListS2CPacket(final Action action, final ServerPlayerEntity... arr) {
        this.entries = Lists.newArrayList();
        this.action = action;
        for (final ServerPlayerEntity serverPlayerEntity6 : arr) {
            this.entries.add(new Entry(serverPlayerEntity6.getGameProfile(), serverPlayerEntity6.f, serverPlayerEntity6.interactionManager.getGameMode(), serverPlayerEntity6.G()));
        }
    }
    
    public PlayerListS2CPacket(final Action action, final Iterable<ServerPlayerEntity> iterable) {
        this.entries = Lists.newArrayList();
        this.action = action;
        for (final ServerPlayerEntity serverPlayerEntity4 : iterable) {
            this.entries.add(new Entry(serverPlayerEntity4.getGameProfile(), serverPlayerEntity4.f, serverPlayerEntity4.interactionManager.getGameMode(), serverPlayerEntity4.G()));
        }
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.action = buf.<Action>readEnumConstant(Action.class);
        for (int integer2 = buf.readVarInt(), integer3 = 0; integer3 < integer2; ++integer3) {
            GameProfile gameProfile4 = null;
            int integer4 = 0;
            GameMode gameMode6 = null;
            TextComponent textComponent7 = null;
            switch (this.action) {
                case ADD: {
                    gameProfile4 = new GameProfile(buf.readUuid(), buf.readString(16));
                    for (int integer5 = buf.readVarInt(), integer6 = 0; integer6 < integer5; ++integer6) {
                        final String string10 = buf.readString(32767);
                        final String string11 = buf.readString(32767);
                        if (buf.readBoolean()) {
                            gameProfile4.getProperties().put(string10, new Property(string10, string11, buf.readString(32767)));
                        }
                        else {
                            gameProfile4.getProperties().put(string10, new Property(string10, string11));
                        }
                    }
                    gameMode6 = GameMode.byId(buf.readVarInt());
                    integer4 = buf.readVarInt();
                    if (buf.readBoolean()) {
                        textComponent7 = buf.readTextComponent();
                        break;
                    }
                    break;
                }
                case UPDATE_GAMEMODE: {
                    gameProfile4 = new GameProfile(buf.readUuid(), (String)null);
                    gameMode6 = GameMode.byId(buf.readVarInt());
                    break;
                }
                case UPDATE_LATENCY: {
                    gameProfile4 = new GameProfile(buf.readUuid(), (String)null);
                    integer4 = buf.readVarInt();
                    break;
                }
                case UPDATE_DISPLAY_NAME: {
                    gameProfile4 = new GameProfile(buf.readUuid(), (String)null);
                    if (buf.readBoolean()) {
                        textComponent7 = buf.readTextComponent();
                        break;
                    }
                    break;
                }
                case REMOVE: {
                    gameProfile4 = new GameProfile(buf.readUuid(), (String)null);
                    break;
                }
            }
            this.entries.add(new Entry(gameProfile4, integer4, gameMode6, textComponent7));
        }
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeEnumConstant(this.action);
        buf.writeVarInt(this.entries.size());
        for (final Entry entry3 : this.entries) {
            switch (this.action) {
                case ADD: {
                    buf.writeUuid(entry3.getProfile().getId());
                    buf.writeString(entry3.getProfile().getName());
                    buf.writeVarInt(entry3.getProfile().getProperties().size());
                    for (final Property property5 : entry3.getProfile().getProperties().values()) {
                        buf.writeString(property5.getName());
                        buf.writeString(property5.getValue());
                        if (property5.hasSignature()) {
                            buf.writeBoolean(true);
                            buf.writeString(property5.getSignature());
                        }
                        else {
                            buf.writeBoolean(false);
                        }
                    }
                    buf.writeVarInt(entry3.getGameMode().getId());
                    buf.writeVarInt(entry3.getLatency());
                    if (entry3.getDisplayName() == null) {
                        buf.writeBoolean(false);
                        continue;
                    }
                    buf.writeBoolean(true);
                    buf.writeTextComponent(entry3.getDisplayName());
                    continue;
                }
                case UPDATE_GAMEMODE: {
                    buf.writeUuid(entry3.getProfile().getId());
                    buf.writeVarInt(entry3.getGameMode().getId());
                    continue;
                }
                case UPDATE_LATENCY: {
                    buf.writeUuid(entry3.getProfile().getId());
                    buf.writeVarInt(entry3.getLatency());
                    continue;
                }
                case UPDATE_DISPLAY_NAME: {
                    buf.writeUuid(entry3.getProfile().getId());
                    if (entry3.getDisplayName() == null) {
                        buf.writeBoolean(false);
                        continue;
                    }
                    buf.writeBoolean(true);
                    buf.writeTextComponent(entry3.getDisplayName());
                    continue;
                }
                case REMOVE: {
                    buf.writeUuid(entry3.getProfile().getId());
                    continue;
                }
            }
        }
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onPlayerList(this);
    }
    
    @Environment(EnvType.CLIENT)
    public List<Entry> getEntries() {
        return this.entries;
    }
    
    @Environment(EnvType.CLIENT)
    public Action getAction() {
        return this.action;
    }
    
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("action", this.action).add("entries", this.entries).toString();
    }
    
    public enum Action
    {
        ADD, 
        UPDATE_GAMEMODE, 
        UPDATE_LATENCY, 
        UPDATE_DISPLAY_NAME, 
        REMOVE;
    }
    
    public class Entry
    {
        private final int latency;
        private final GameMode gameMode;
        private final GameProfile profile;
        private final TextComponent displayName;
        
        public Entry(final GameProfile gameProfile, final int integer, @Nullable final GameMode gameMode, @Nullable final TextComponent textComponent) {
            this.profile = gameProfile;
            this.latency = integer;
            this.gameMode = gameMode;
            this.displayName = textComponent;
        }
        
        public GameProfile getProfile() {
            return this.profile;
        }
        
        public int getLatency() {
            return this.latency;
        }
        
        public GameMode getGameMode() {
            return this.gameMode;
        }
        
        @Nullable
        public TextComponent getDisplayName() {
            return this.displayName;
        }
        
        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).add("latency", this.latency).add("gameMode", this.gameMode).add("profile", this.profile).add("displayName", (this.displayName == null) ? null : TextComponent.Serializer.toJsonString(this.displayName)).toString();
        }
    }
}
