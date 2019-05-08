package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.Difficulty;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class DifficultyS2CPacket implements Packet<ClientPlayPacketListener>
{
    private Difficulty difficulty;
    private boolean difficultyLocked;
    
    public DifficultyS2CPacket() {
    }
    
    public DifficultyS2CPacket(final Difficulty difficulty, final boolean difficultyLocked) {
        this.difficulty = difficulty;
        this.difficultyLocked = difficultyLocked;
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onDifficulty(this);
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.difficulty = Difficulty.getDifficulty(buf.readUnsignedByte());
        this.difficultyLocked = buf.readBoolean();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeByte(this.difficulty.getId());
        buf.writeBoolean(this.difficultyLocked);
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isDifficultyLocked() {
        return this.difficultyLocked;
    }
    
    @Environment(EnvType.CLIENT)
    public Difficulty getDifficulty() {
        return this.difficulty;
    }
}
