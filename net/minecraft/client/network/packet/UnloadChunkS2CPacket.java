package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class UnloadChunkS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int x;
    private int z;
    
    public UnloadChunkS2CPacket() {
    }
    
    public UnloadChunkS2CPacket(final int integer1, final int integer2) {
        this.x = integer1;
        this.z = integer2;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.x = buf.readInt();
        this.z = buf.readInt();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeInt(this.x);
        buf.writeInt(this.z);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onUnloadChunk(this);
    }
    
    @Environment(EnvType.CLIENT)
    public int getX() {
        return this.x;
    }
    
    @Environment(EnvType.CLIENT)
    public int getZ() {
        return this.z;
    }
}
