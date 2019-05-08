package net.minecraft.network.listener;

import net.minecraft.client.network.packet.LoginQueryRequestS2CPacket;
import net.minecraft.client.network.packet.LoginCompressionS2CPacket;
import net.minecraft.client.network.packet.LoginDisconnectS2CPacket;
import net.minecraft.client.network.packet.LoginSuccessS2CPacket;
import net.minecraft.client.network.packet.LoginHelloS2CPacket;

public interface ClientLoginPacketListener extends PacketListener
{
    void onHello(final LoginHelloS2CPacket arg1);
    
    void onLoginSuccess(final LoginSuccessS2CPacket arg1);
    
    void onDisconnect(final LoginDisconnectS2CPacket arg1);
    
    void onCompression(final LoginCompressionS2CPacket arg1);
    
    void onQueryRequest(final LoginQueryRequestS2CPacket arg1);
}
