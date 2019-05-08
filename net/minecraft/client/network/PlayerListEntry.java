package net.minecraft.client.network;

import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.Team;
import javax.annotation.Nullable;
import com.google.common.base.MoreObjects;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.network.packet.PlayerListS2CPacket;
import com.google.common.collect.Maps;
import net.minecraft.text.TextComponent;
import net.minecraft.world.GameMode;
import net.minecraft.util.Identifier;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import java.util.Map;
import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class PlayerListEntry
{
    private final GameProfile profile;
    private final Map<MinecraftProfileTexture.Type, Identifier> textures;
    private GameMode gameMode;
    private int latency;
    private boolean texturesLoaded;
    private String model;
    private TextComponent displayName;
    private int h;
    private int i;
    private long j;
    private long k;
    private long l;
    
    public PlayerListEntry(final GameProfile profile) {
        this.textures = Maps.newEnumMap(MinecraftProfileTexture.Type.class);
        this.profile = profile;
    }
    
    public PlayerListEntry(final PlayerListS2CPacket.Entry playerListPacketEntry) {
        this.textures = Maps.newEnumMap(MinecraftProfileTexture.Type.class);
        this.profile = playerListPacketEntry.getProfile();
        this.gameMode = playerListPacketEntry.getGameMode();
        this.latency = playerListPacketEntry.getLatency();
        this.displayName = playerListPacketEntry.getDisplayName();
    }
    
    public GameProfile getProfile() {
        return this.profile;
    }
    
    public GameMode getGameMode() {
        return this.gameMode;
    }
    
    protected void setGameMode(final GameMode gameMode) {
        this.gameMode = gameMode;
    }
    
    public int getLatency() {
        return this.latency;
    }
    
    protected void setLatency(final int latency) {
        this.latency = latency;
    }
    
    public boolean hasSkinTexture() {
        return this.getSkinTexture() != null;
    }
    
    public String getModel() {
        if (this.model == null) {
            return DefaultSkinHelper.getModel(this.profile.getId());
        }
        return this.model;
    }
    
    public Identifier getSkinTexture() {
        this.loadTextures();
        return MoreObjects.<Identifier>firstNonNull(this.textures.get(MinecraftProfileTexture.Type.SKIN), DefaultSkinHelper.getTexture(this.profile.getId()));
    }
    
    @Nullable
    public Identifier getCapeTexture() {
        this.loadTextures();
        return this.textures.get(MinecraftProfileTexture.Type.CAPE);
    }
    
    @Nullable
    public Identifier getElytraTexture() {
        this.loadTextures();
        return this.textures.get(MinecraftProfileTexture.Type.ELYTRA);
    }
    
    @Nullable
    public Team getScoreboardTeam() {
        return MinecraftClient.getInstance().world.getScoreboard().getPlayerTeam(this.getProfile().getName());
    }
    
    protected void loadTextures() {
        synchronized (this) {
            if (!this.texturesLoaded) {
                this.texturesLoaded = true;
                MinecraftClient.getInstance().getSkinProvider().loadSkin(this.profile, (type, identifier, minecraftProfileTexture) -> {
                    switch (type) {
                        case SKIN: {
                            this.textures.put(MinecraftProfileTexture.Type.SKIN, identifier);
                            this.model = minecraftProfileTexture.getMetadata("model");
                            if (this.model == null) {
                                this.model = "default";
                                break;
                            }
                            else {
                                break;
                            }
                            break;
                        }
                        case CAPE: {
                            this.textures.put(MinecraftProfileTexture.Type.CAPE, identifier);
                            break;
                        }
                        case ELYTRA: {
                            this.textures.put(MinecraftProfileTexture.Type.ELYTRA, identifier);
                            break;
                        }
                    }
                }, true);
            }
        }
    }
    
    public void setDisplayName(@Nullable final TextComponent displayName) {
        this.displayName = displayName;
    }
    
    @Nullable
    public TextComponent getDisplayName() {
        return this.displayName;
    }
    
    public int m() {
        return this.h;
    }
    
    public void b(final int integer) {
        this.h = integer;
    }
    
    public int n() {
        return this.i;
    }
    
    public void c(final int integer) {
        this.i = integer;
    }
    
    public long o() {
        return this.j;
    }
    
    public void a(final long long1) {
        this.j = long1;
    }
    
    public long p() {
        return this.k;
    }
    
    public void b(final long long1) {
        this.k = long1;
    }
    
    public long q() {
        return this.l;
    }
    
    public void c(final long long1) {
        this.l = long1;
    }
}
