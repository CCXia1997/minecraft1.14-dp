package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.scoreboard.ServerScoreboard;
import javax.annotation.Nullable;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class ScoreboardPlayerUpdateS2CPacket implements Packet<ClientPlayPacketListener>
{
    private String playerName;
    @Nullable
    private String objectiveName;
    private int score;
    private ServerScoreboard.UpdateMode mode;
    
    public ScoreboardPlayerUpdateS2CPacket() {
        this.playerName = "";
    }
    
    public ScoreboardPlayerUpdateS2CPacket(final ServerScoreboard.UpdateMode updateMode, @Nullable final String string2, final String string3, final int integer) {
        this.playerName = "";
        if (updateMode != ServerScoreboard.UpdateMode.b && string2 == null) {
            throw new IllegalArgumentException("Need an objective name");
        }
        this.playerName = string3;
        this.objectiveName = string2;
        this.score = integer;
        this.mode = updateMode;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.playerName = buf.readString(40);
        this.mode = buf.<ServerScoreboard.UpdateMode>readEnumConstant(ServerScoreboard.UpdateMode.class);
        final String string2 = buf.readString(16);
        this.objectiveName = (Objects.equals(string2, "") ? null : string2);
        if (this.mode != ServerScoreboard.UpdateMode.b) {
            this.score = buf.readVarInt();
        }
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeString(this.playerName);
        buf.writeEnumConstant(this.mode);
        buf.writeString((this.objectiveName == null) ? "" : this.objectiveName);
        if (this.mode != ServerScoreboard.UpdateMode.b) {
            buf.writeVarInt(this.score);
        }
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onScoreboardPlayerUpdate(this);
    }
    
    @Environment(EnvType.CLIENT)
    public String getPlayerName() {
        return this.playerName;
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public String getObjectiveName() {
        return this.objectiveName;
    }
    
    @Environment(EnvType.CLIENT)
    public int getScore() {
        return this.score;
    }
    
    @Environment(EnvType.CLIENT)
    public ServerScoreboard.UpdateMode getUpdateMode() {
        return this.mode;
    }
}
