package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class ChunkLoadDistanceS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int distance;
    
    public ChunkLoadDistanceS2CPacket() {
    }
    
    public ChunkLoadDistanceS2CPacket(final int integer) {
        this.distance = integer;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.distance = buf.readVarInt();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.distance);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.handleChunkLoadDistance(this);
    }
    
    @Environment(EnvType.CLIENT)
    public int getDistance() {
        return this.distance;
    }
}
