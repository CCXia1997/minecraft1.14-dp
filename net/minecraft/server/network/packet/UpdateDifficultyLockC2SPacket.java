package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class UpdateDifficultyLockC2SPacket implements Packet<ServerPlayPacketListener>
{
    private boolean difficultyLocked;
    
    public UpdateDifficultyLockC2SPacket() {
    }
    
    @Environment(EnvType.CLIENT)
    public UpdateDifficultyLockC2SPacket(final boolean boolean1) {
        this.difficultyLocked = boolean1;
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onUpdateDifficultyLock(this);
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.difficultyLocked = buf.readBoolean();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeBoolean(this.difficultyLocked);
    }
    
    public boolean isDifficultyLocked() {
        return this.difficultyLocked;
    }
}
