package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.Difficulty;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class UpdateDifficultyC2SPacket implements Packet<ServerPlayPacketListener>
{
    private Difficulty difficulty;
    
    public UpdateDifficultyC2SPacket() {
    }
    
    public UpdateDifficultyC2SPacket(final Difficulty difficulty) {
        this.difficulty = difficulty;
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onUpdateDifficulty(this);
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.difficulty = Difficulty.getDifficulty(buf.readUnsignedByte());
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeByte(this.difficulty.getId());
    }
    
    public Difficulty getDifficulty() {
        return this.difficulty;
    }
}
