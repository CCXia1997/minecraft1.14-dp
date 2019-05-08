package net.minecraft.client.sortme;

import org.apache.logging.log4j.LogManager;
import java.util.Iterator;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.minecraft.util.math.MathHelper;
import com.google.common.collect.Iterables;
import java.nio.charset.StandardCharsets;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.channel.ChannelFutureListener;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.bootstrap.Bootstrap;
import java.net.UnknownHostException;
import net.minecraft.server.network.packet.QueryRequestC2SPacket;
import net.minecraft.server.network.packet.HandshakeC2SPacket;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.client.network.packet.QueryPongS2CPacket;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.ServerMetadata;
import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.QueryPingC2SPacket;
import net.minecraft.util.SystemUtil;
import org.apache.commons.lang3.ArrayUtils;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.client.network.packet.QueryResponseS2CPacket;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.client.resource.language.I18n;
import java.net.InetAddress;
import net.minecraft.network.ServerAddress;
import net.minecraft.client.options.ServerEntry;
import java.util.Collections;
import com.google.common.collect.Lists;
import net.minecraft.network.ClientConnection;
import java.util.List;
import org.apache.logging.log4j.Logger;
import com.google.common.base.Splitter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ServerEntryNetworkPart
{
    private static final Splitter ZERO_SPLITTER;
    private static final Logger LOGGER;
    private final List<ClientConnection> clientConnections;
    
    public ServerEntryNetworkPart() {
        this.clientConnections = Collections.<ClientConnection>synchronizedList(Lists.newArrayList());
    }
    
    public void a(final ServerEntry entry) throws UnknownHostException {
        final ServerAddress serverAddress2 = ServerAddress.parse(entry.address);
        final ClientConnection clientConnection3 = ClientConnection.connect(InetAddress.getByName(serverAddress2.getAddress()), serverAddress2.getPort(), false);
        this.clientConnections.add(clientConnection3);
        entry.label = I18n.translate("multiplayer.status.pinging");
        entry.ping = -1L;
        entry.playerListSummary = null;
        clientConnection3.setPacketListener(new ClientQueryPacketListener() {
            private boolean d;
            private boolean e;
            private long f;
            
            @Override
            public void onResponse(final QueryResponseS2CPacket queryResponseS2CPacket) {
                if (this.e) {
                    clientConnection3.disconnect(new TranslatableTextComponent("multiplayer.status.unrequested", new Object[0]));
                    return;
                }
                this.e = true;
                final ServerMetadata serverMetadata2 = queryResponseS2CPacket.getServerMetadata();
                if (serverMetadata2.getDescription() != null) {
                    entry.label = serverMetadata2.getDescription().getFormattedText();
                }
                else {
                    entry.label = "";
                }
                if (serverMetadata2.getVersion() != null) {
                    entry.version = serverMetadata2.getVersion().getGameVersion();
                    entry.protocolVersion = serverMetadata2.getVersion().getProtocolVersion();
                }
                else {
                    entry.version = I18n.translate("multiplayer.status.old");
                    entry.protocolVersion = 0;
                }
                if (serverMetadata2.getPlayers() != null) {
                    entry.playerCountLabel = TextFormat.h + "" + serverMetadata2.getPlayers().getOnlinePlayerCount() + "" + TextFormat.i + "/" + TextFormat.h + serverMetadata2.getPlayers().getPlayerLimit();
                    if (ArrayUtils.isNotEmpty((Object[])serverMetadata2.getPlayers().getSample())) {
                        final StringBuilder stringBuilder3 = new StringBuilder();
                        for (final GameProfile gameProfile7 : serverMetadata2.getPlayers().getSample()) {
                            if (stringBuilder3.length() > 0) {
                                stringBuilder3.append("\n");
                            }
                            stringBuilder3.append(gameProfile7.getName());
                        }
                        if (serverMetadata2.getPlayers().getSample().length < serverMetadata2.getPlayers().getOnlinePlayerCount()) {
                            if (stringBuilder3.length() > 0) {
                                stringBuilder3.append("\n");
                            }
                            stringBuilder3.append(I18n.translate("multiplayer.status.and_more", serverMetadata2.getPlayers().getOnlinePlayerCount() - serverMetadata2.getPlayers().getSample().length));
                        }
                        entry.playerListSummary = stringBuilder3.toString();
                    }
                }
                else {
                    entry.playerCountLabel = TextFormat.i + I18n.translate("multiplayer.status.unknown");
                }
                if (serverMetadata2.getFavicon() != null) {
                    final String string3 = serverMetadata2.getFavicon();
                    if (string3.startsWith("data:image/png;base64,")) {
                        entry.setIcon(string3.substring("data:image/png;base64,".length()));
                    }
                    else {
                        ServerEntryNetworkPart.LOGGER.error("Invalid server icon (unknown format)");
                    }
                }
                else {
                    entry.setIcon(null);
                }
                this.f = SystemUtil.getMeasuringTimeMs();
                clientConnection3.send(new QueryPingC2SPacket(this.f));
                this.d = true;
            }
            
            @Override
            public void onPong(final QueryPongS2CPacket queryPongS2CPacket) {
                final long long2 = this.f;
                final long long3 = SystemUtil.getMeasuringTimeMs();
                entry.ping = long3 - long2;
                clientConnection3.disconnect(new TranslatableTextComponent("multiplayer.status.finished", new Object[0]));
            }
            
            @Override
            public void onDisconnected(final TextComponent reason) {
                if (!this.d) {
                    ServerEntryNetworkPart.LOGGER.error("Can't ping {}: {}", entry.address, reason.getString());
                    entry.label = TextFormat.e + I18n.translate("multiplayer.status.cannot_connect");
                    entry.playerCountLabel = "";
                    ServerEntryNetworkPart.this.ping(entry);
                }
            }
        });
        try {
            clientConnection3.send(new HandshakeC2SPacket(serverAddress2.getAddress(), serverAddress2.getPort(), NetworkState.STATUS));
            clientConnection3.send(new QueryRequestC2SPacket());
        }
        catch (Throwable throwable4) {
            ServerEntryNetworkPart.LOGGER.error(throwable4);
        }
    }
    
    private void ping(final ServerEntry serverEntry) {
        final ServerAddress serverAddress2 = ServerAddress.parse(serverEntry.address);
        ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)ClientConnection.CLIENT_IO_GROUP.get())).handler((ChannelHandler)new ChannelInitializer<Channel>() {
            protected void initChannel(final Channel channel) throws Exception {
                try {
                    channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                }
                catch (ChannelException ex) {}
                channel.pipeline().addLast(new ChannelHandler[] { (ChannelHandler)new SimpleChannelInboundHandler<ByteBuf>() {
                        public void channelActive(final ChannelHandlerContext channelHandlerContext) throws Exception {
                            super.channelActive(channelHandlerContext);
                            final ByteBuf byteBuf2 = Unpooled.buffer();
                            try {
                                byteBuf2.writeByte(254);
                                byteBuf2.writeByte(1);
                                byteBuf2.writeByte(250);
                                char[] arr3 = "MC|PingHost".toCharArray();
                                byteBuf2.writeShort(arr3.length);
                                for (final char character7 : arr3) {
                                    byteBuf2.writeChar((int)character7);
                                }
                                byteBuf2.writeShort(7 + 2 * serverAddress2.getAddress().length());
                                byteBuf2.writeByte(127);
                                arr3 = serverAddress2.getAddress().toCharArray();
                                byteBuf2.writeShort(arr3.length);
                                for (final char character7 : arr3) {
                                    byteBuf2.writeChar((int)character7);
                                }
                                byteBuf2.writeInt(serverAddress2.getPort());
                                channelHandlerContext.channel().writeAndFlush(byteBuf2).addListener((GenericFutureListener)ChannelFutureListener.CLOSE_ON_FAILURE);
                            }
                            finally {
                                byteBuf2.release();
                            }
                        }
                        
                        protected void a(final ChannelHandlerContext channelHandlerContext, final ByteBuf byteBuf) throws Exception {
                            final short short3 = byteBuf.readUnsignedByte();
                            if (short3 == 255) {
                                final String string4 = new String(byteBuf.readBytes(byteBuf.readShort() * 2).array(), StandardCharsets.UTF_16BE);
                                final String[] arr5 = Iterables.<String>toArray(ServerEntryNetworkPart.ZERO_SPLITTER.split(string4), String.class);
                                if ("§1".equals(arr5[0])) {
                                    final int integer6 = MathHelper.parseInt(arr5[1], 0);
                                    final String string5 = arr5[2];
                                    final String string6 = arr5[3];
                                    final int integer7 = MathHelper.parseInt(arr5[4], -1);
                                    final int integer8 = MathHelper.parseInt(arr5[5], -1);
                                    serverEntry.protocolVersion = -1;
                                    serverEntry.version = string5;
                                    serverEntry.label = string6;
                                    serverEntry.playerCountLabel = TextFormat.h + "" + integer7 + "" + TextFormat.i + "/" + TextFormat.h + integer8;
                                }
                            }
                            channelHandlerContext.close();
                        }
                        
                        public void exceptionCaught(final ChannelHandlerContext channelHandlerContext, final Throwable throwable) throws Exception {
                            channelHandlerContext.close();
                        }
                    } });
            }
        })).channel((Class)NioSocketChannel.class)).connect(serverAddress2.getAddress(), serverAddress2.getPort());
    }
    
    public void a() {
        synchronized (this.clientConnections) {
            final Iterator<ClientConnection> iterator2 = this.clientConnections.iterator();
            while (iterator2.hasNext()) {
                final ClientConnection clientConnection3 = iterator2.next();
                if (clientConnection3.isOpen()) {
                    clientConnection3.tick();
                }
                else {
                    iterator2.remove();
                    clientConnection3.handleDisconnection();
                }
            }
        }
    }
    
    public void b() {
        synchronized (this.clientConnections) {
            final Iterator<ClientConnection> iterator2 = this.clientConnections.iterator();
            while (iterator2.hasNext()) {
                final ClientConnection clientConnection3 = iterator2.next();
                if (clientConnection3.isOpen()) {
                    iterator2.remove();
                    clientConnection3.disconnect(new TranslatableTextComponent("multiplayer.status.cancelled", new Object[0]));
                }
            }
        }
    }
    
    static {
        ZERO_SPLITTER = Splitter.on('\0').limit(6);
        LOGGER = LogManager.getLogger();
    }
}
