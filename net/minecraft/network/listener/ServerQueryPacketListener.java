package net.minecraft.network.listener;

import net.minecraft.server.network.packet.QueryRequestC2SPacket;
import net.minecraft.server.network.packet.QueryPingC2SPacket;

public interface ServerQueryPacketListener extends PacketListener
{
    void onPing(final QueryPingC2SPacket arg1);
    
    void onRequest(final QueryRequestC2SPacket arg1);
}
