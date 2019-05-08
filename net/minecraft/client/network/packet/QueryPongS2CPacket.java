package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.Packet;

public class QueryPongS2CPacket implements Packet<ClientQueryPacketListener>
{
    private long startTime;
    
    public QueryPongS2CPacket() {
    }
    
    public QueryPongS2CPacket(final long long1) {
        this.startTime = long1;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.startTime = buf.readLong();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeLong(this.startTime);
    }
    
    @Override
    public void apply(final ClientQueryPacketListener listener) {
        listener.onPong(this);
    }
}
