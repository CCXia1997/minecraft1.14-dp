package net.minecraft.server.network;

import net.minecraft.text.TextComponent;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.Packet;
import net.minecraft.client.network.packet.LoginDisconnectS2CPacket;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.SharedConstants;
import net.minecraft.network.NetworkState;
import net.minecraft.server.network.packet.HandshakeC2SPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.listener.ServerHandshakePacketListener;

public class ServerHandshakeNetworkHandler implements ServerHandshakePacketListener
{
    private final MinecraftServer server;
    private final ClientConnection client;
    
    public ServerHandshakeNetworkHandler(final MinecraftServer minecraftServer, final ClientConnection clientConnection) {
        this.server = minecraftServer;
        this.client = clientConnection;
    }
    
    @Override
    public void onHandshake(final HandshakeC2SPacket handshakeC2SPacket) {
        switch (handshakeC2SPacket.getIntendedState()) {
            case LOGIN: {
                this.client.setState(NetworkState.LOGIN);
                if (handshakeC2SPacket.getProtocolVersion() > SharedConstants.getGameVersion().getProtocolVersion()) {
                    final TextComponent textComponent2 = new TranslatableTextComponent("multiplayer.disconnect.outdated_server", new Object[] { SharedConstants.getGameVersion().getName() });
                    this.client.send(new LoginDisconnectS2CPacket(textComponent2));
                    this.client.disconnect(textComponent2);
                    break;
                }
                if (handshakeC2SPacket.getProtocolVersion() < SharedConstants.getGameVersion().getProtocolVersion()) {
                    final TextComponent textComponent2 = new TranslatableTextComponent("multiplayer.disconnect.outdated_client", new Object[] { SharedConstants.getGameVersion().getName() });
                    this.client.send(new LoginDisconnectS2CPacket(textComponent2));
                    this.client.disconnect(textComponent2);
                    break;
                }
                this.client.setPacketListener(new ServerLoginNetworkHandler(this.server, this.client));
                break;
            }
            case STATUS: {
                this.client.setState(NetworkState.STATUS);
                this.client.setPacketListener(new ServerQueryNetworkHandler(this.server, this.client));
                break;
            }
            default: {
                throw new UnsupportedOperationException("Invalid intention " + handshakeC2SPacket.getIntendedState());
            }
        }
    }
    
    @Override
    public void onDisconnected(final TextComponent reason) {
    }
}
