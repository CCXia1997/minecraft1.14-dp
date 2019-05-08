package net.minecraft.server.network;

import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.client.network.packet.QueryPongS2CPacket;
import net.minecraft.server.network.packet.QueryPingC2SPacket;
import net.minecraft.network.Packet;
import net.minecraft.client.network.packet.QueryResponseS2CPacket;
import net.minecraft.server.network.packet.QueryRequestC2SPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TextComponent;
import net.minecraft.network.listener.ServerQueryPacketListener;

public class ServerQueryNetworkHandler implements ServerQueryPacketListener
{
    private static final TextComponent REQUEST_HANDLED;
    private final MinecraftServer server;
    private final ClientConnection client;
    private boolean responseSent;
    
    public ServerQueryNetworkHandler(final MinecraftServer minecraftServer, final ClientConnection clientConnection) {
        this.server = minecraftServer;
        this.client = clientConnection;
    }
    
    @Override
    public void onDisconnected(final TextComponent reason) {
    }
    
    @Override
    public void onRequest(final QueryRequestC2SPacket queryRequestC2SPacket) {
        if (this.responseSent) {
            this.client.disconnect(ServerQueryNetworkHandler.REQUEST_HANDLED);
            return;
        }
        this.responseSent = true;
        this.client.send(new QueryResponseS2CPacket(this.server.getServerMetadata()));
    }
    
    @Override
    public void onPing(final QueryPingC2SPacket queryPingC2SPacket) {
        this.client.send(new QueryPongS2CPacket(queryPingC2SPacket.getStartTime()));
        this.client.disconnect(ServerQueryNetworkHandler.REQUEST_HANDLED);
    }
    
    static {
        REQUEST_HANDLED = new TranslatableTextComponent("multiplayer.status.request_handled", new Object[0]);
    }
}
