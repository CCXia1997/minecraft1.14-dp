package net.minecraft.network.listener;

import net.minecraft.server.network.packet.LoginQueryResponseC2SPacket;
import net.minecraft.server.network.packet.LoginKeyC2SPacket;
import net.minecraft.server.network.packet.LoginHelloC2SPacket;

public interface ServerLoginPacketListener extends PacketListener
{
    void onHello(final LoginHelloC2SPacket arg1);
    
    void onKey(final LoginKeyC2SPacket arg1);
    
    void onQueryResponse(final LoginQueryResponseC2SPacket arg1);
}
