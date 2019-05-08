package net.minecraft.server.network;

import net.minecraft.text.TextComponent;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.server.network.packet.HandshakeC2SPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.listener.ServerHandshakePacketListener;

@Environment(EnvType.CLIENT)
public class IntegratedServerHandshakeNetworkHandler implements ServerHandshakePacketListener
{
    private final MinecraftServer server;
    private final ClientConnection client;
    
    public IntegratedServerHandshakeNetworkHandler(final MinecraftServer minecraftServer, final ClientConnection clientConnection) {
        this.server = minecraftServer;
        this.client = clientConnection;
    }
    
    @Override
    public void onHandshake(final HandshakeC2SPacket handshakeC2SPacket) {
        this.client.setState(handshakeC2SPacket.getIntendedState());
        this.client.setPacketListener(new ServerLoginNetworkHandler(this.server, this.client));
    }
    
    @Override
    public void onDisconnected(final TextComponent reason) {
    }
}
