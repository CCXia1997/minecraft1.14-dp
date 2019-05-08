package net.minecraft.network.listener;

import net.minecraft.client.network.packet.QueryPongS2CPacket;
import net.minecraft.client.network.packet.QueryResponseS2CPacket;

public interface ClientQueryPacketListener extends PacketListener
{
    void onResponse(final QueryResponseS2CPacket arg1);
    
    void onPong(final QueryPongS2CPacket arg1);
}
