package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.listener.ServerQueryPacketListener;
import net.minecraft.network.Packet;

public class QueryPingC2SPacket implements Packet<ServerQueryPacketListener>
{
    private long startTime;
    
    public QueryPingC2SPacket() {
    }
    
    @Environment(EnvType.CLIENT)
    public QueryPingC2SPacket(final long long1) {
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
    public void apply(final ServerQueryPacketListener listener) {
        listener.onPing(this);
    }
    
    public long getStartTime() {
        return this.startTime;
    }
}
