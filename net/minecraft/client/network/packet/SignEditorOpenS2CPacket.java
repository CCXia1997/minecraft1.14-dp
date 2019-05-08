package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class SignEditorOpenS2CPacket implements Packet<ClientPlayPacketListener>
{
    private BlockPos pos;
    
    public SignEditorOpenS2CPacket() {
    }
    
    public SignEditorOpenS2CPacket(final BlockPos blockPos) {
        this.pos = blockPos;
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onSignEditorOpen(this);
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.pos = buf.readBlockPos();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeBlockPos(this.pos);
    }
    
    @Environment(EnvType.CLIENT)
    public BlockPos getPos() {
        return this.pos;
    }
}
