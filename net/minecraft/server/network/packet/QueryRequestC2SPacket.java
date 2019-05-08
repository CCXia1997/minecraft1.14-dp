package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.network.listener.ServerQueryPacketListener;
import net.minecraft.network.Packet;

public class QueryRequestC2SPacket implements Packet<ServerQueryPacketListener>
{
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
    }
    
    @Override
    public void apply(final ServerQueryPacketListener listener) {
        listener.onRequest(this);
    }
}
