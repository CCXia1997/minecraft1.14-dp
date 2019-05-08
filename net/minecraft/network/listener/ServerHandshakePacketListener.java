package net.minecraft.network.listener;

import net.minecraft.server.network.packet.HandshakeC2SPacket;

public interface ServerHandshakePacketListener extends PacketListener
{
    void onHandshake(final HandshakeC2SPacket arg1);
}
