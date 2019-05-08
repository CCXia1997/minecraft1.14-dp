package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Iterator;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.scoreboard.Team;
import com.google.common.collect.Lists;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.text.StringTextComponent;
import java.util.Collection;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TextComponent;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class TeamS2CPacket implements Packet<ClientPlayPacketListener>
{
    private String teamName;
    private TextComponent displayName;
    private TextComponent prefix;
    private TextComponent suffix;
    private String nameTagVisibilityRule;
    private String collisionRule;
    private TextFormat color;
    private final Collection<String> playerList;
    private int mode;
    private int flags;
    
    public TeamS2CPacket() {
        this.teamName = "";
        this.displayName = new StringTextComponent("");
        this.prefix = new StringTextComponent("");
        this.suffix = new StringTextComponent("");
        this.nameTagVisibilityRule = AbstractTeam.VisibilityRule.ALWAYS.name;
        this.collisionRule = AbstractTeam.CollisionRule.ALWAYS.name;
        this.color = TextFormat.RESET;
        this.playerList = Lists.newArrayList();
    }
    
    public TeamS2CPacket(final Team team, final int mode) {
        this.teamName = "";
        this.displayName = new StringTextComponent("");
        this.prefix = new StringTextComponent("");
        this.suffix = new StringTextComponent("");
        this.nameTagVisibilityRule = AbstractTeam.VisibilityRule.ALWAYS.name;
        this.collisionRule = AbstractTeam.CollisionRule.ALWAYS.name;
        this.color = TextFormat.RESET;
        this.playerList = Lists.newArrayList();
        this.teamName = team.getName();
        this.mode = mode;
        if (mode == 0 || mode == 2) {
            this.displayName = team.getDisplayName();
            this.flags = team.getFriendlyFlagsBitwise();
            this.nameTagVisibilityRule = team.getNameTagVisibilityRule().name;
            this.collisionRule = team.getCollisionRule().name;
            this.color = team.getColor();
            this.prefix = team.getPrefix();
            this.suffix = team.getSuffix();
        }
        if (mode == 0) {
            this.playerList.addAll(team.getPlayerList());
        }
    }
    
    public TeamS2CPacket(final Team team, final Collection<String> playerList, final int integer) {
        this.teamName = "";
        this.displayName = new StringTextComponent("");
        this.prefix = new StringTextComponent("");
        this.suffix = new StringTextComponent("");
        this.nameTagVisibilityRule = AbstractTeam.VisibilityRule.ALWAYS.name;
        this.collisionRule = AbstractTeam.CollisionRule.ALWAYS.name;
        this.color = TextFormat.RESET;
        this.playerList = Lists.newArrayList();
        if (integer != 3 && integer != 4) {
            throw new IllegalArgumentException("Method must be join or leave for player constructor");
        }
        if (playerList == null || playerList.isEmpty()) {
            throw new IllegalArgumentException("Players cannot be null/empty");
        }
        this.mode = integer;
        this.teamName = team.getName();
        this.playerList.addAll(playerList);
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.teamName = buf.readString(16);
        this.mode = buf.readByte();
        if (this.mode == 0 || this.mode == 2) {
            this.displayName = buf.readTextComponent();
            this.flags = buf.readByte();
            this.nameTagVisibilityRule = buf.readString(40);
            this.collisionRule = buf.readString(40);
            this.color = buf.<TextFormat>readEnumConstant(TextFormat.class);
            this.prefix = buf.readTextComponent();
            this.suffix = buf.readTextComponent();
        }
        if (this.mode == 0 || this.mode == 3 || this.mode == 4) {
            for (int integer2 = buf.readVarInt(), integer3 = 0; integer3 < integer2; ++integer3) {
                this.playerList.add(buf.readString(40));
            }
        }
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeString(this.teamName);
        buf.writeByte(this.mode);
        if (this.mode == 0 || this.mode == 2) {
            buf.writeTextComponent(this.displayName);
            buf.writeByte(this.flags);
            buf.writeString(this.nameTagVisibilityRule);
            buf.writeString(this.collisionRule);
            buf.writeEnumConstant(this.color);
            buf.writeTextComponent(this.prefix);
            buf.writeTextComponent(this.suffix);
        }
        if (this.mode == 0 || this.mode == 3 || this.mode == 4) {
            buf.writeVarInt(this.playerList.size());
            for (final String string3 : this.playerList) {
                buf.writeString(string3);
            }
        }
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onTeam(this);
    }
    
    @Environment(EnvType.CLIENT)
    public String getTeamName() {
        return this.teamName;
    }
    
    @Environment(EnvType.CLIENT)
    public TextComponent getDisplayName() {
        return this.displayName;
    }
    
    @Environment(EnvType.CLIENT)
    public Collection<String> getPlayerList() {
        return this.playerList;
    }
    
    @Environment(EnvType.CLIENT)
    public int getMode() {
        return this.mode;
    }
    
    @Environment(EnvType.CLIENT)
    public int getFlags() {
        return this.flags;
    }
    
    @Environment(EnvType.CLIENT)
    public TextFormat getPlayerPrefix() {
        return this.color;
    }
    
    @Environment(EnvType.CLIENT)
    public String getNameTagVisibilityRule() {
        return this.nameTagVisibilityRule;
    }
    
    @Environment(EnvType.CLIENT)
    public String getCollisionRule() {
        return this.collisionRule;
    }
    
    @Environment(EnvType.CLIENT)
    public TextComponent getPrefix() {
        return this.prefix;
    }
    
    @Environment(EnvType.CLIENT)
    public TextComponent getSuffix() {
        return this.suffix;
    }
}
