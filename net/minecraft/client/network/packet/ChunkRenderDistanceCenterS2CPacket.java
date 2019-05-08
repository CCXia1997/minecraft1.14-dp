package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class ChunkRenderDistanceCenterS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int chunkX;
    private int chunkZ;
    
    public ChunkRenderDistanceCenterS2CPacket() {
    }
    
    public ChunkRenderDistanceCenterS2CPacket(final int x, final int z) {
        this.chunkX = x;
        this.chunkZ = z;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.chunkX = buf.readVarInt();
        this.chunkZ = buf.readVarInt();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.chunkX);
        buf.writeVarInt(this.chunkZ);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.handleChunkRenderDistanceCenter(this);
    }
    
    @Environment(EnvType.CLIENT)
    public int getChunkX() {
        return this.chunkX;
    }
    
    @Environment(EnvType.CLIENT)
    public int getChunkZ() {
        return this.chunkZ;
    }
}
