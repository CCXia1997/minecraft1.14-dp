package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import javax.annotation.Nullable;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class ScoreboardDisplayS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int slot;
    private String name;
    
    public ScoreboardDisplayS2CPacket() {
    }
    
    public ScoreboardDisplayS2CPacket(final int slot, @Nullable final ScoreboardObjective scoreboardObjective) {
        this.slot = slot;
        if (scoreboardObjective == null) {
            this.name = "";
        }
        else {
            this.name = scoreboardObjective.getName();
        }
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.slot = buf.readByte();
        this.name = buf.readString(16);
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeByte(this.slot);
        buf.writeString(this.name);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onScoreboardDisplay(this);
    }
    
    @Environment(EnvType.CLIENT)
    public int getSlot() {
        return this.slot;
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public String getName() {
        return Objects.equals(this.name, "") ? null : this.name;
    }
}
