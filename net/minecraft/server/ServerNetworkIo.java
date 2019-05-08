package net.minecraft.server;

import org.apache.logging.log4j.LogManager;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraft.text.TextComponent;
import net.minecraft.util.crash.CrashReportSection;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.Packet;
import net.minecraft.client.network.packet.DisconnectS2CPacket;
import net.minecraft.text.StringTextComponent;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import java.util.Iterator;
import io.netty.channel.local.LocalAddress;
import net.minecraft.server.network.IntegratedServerHandshakeNetworkHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import io.netty.channel.local.LocalServerChannel;
import java.net.SocketAddress;
import java.io.IOException;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.EventLoopGroup;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.server.network.ServerHandshakeNetworkHandler;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.SizePrepender;
import net.minecraft.network.DecoderHandler;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.SplitterHandler;
import net.minecraft.network.LegacyQueryHandler;
import io.netty.channel.ChannelHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.Epoll;
import javax.annotation.Nullable;
import java.net.InetAddress;
import java.util.Collections;
import com.google.common.collect.Lists;
import net.minecraft.network.ClientConnection;
import io.netty.channel.ChannelFuture;
import java.util.List;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import net.minecraft.util.Lazy;
import org.apache.logging.log4j.Logger;

public class ServerNetworkIo
{
    private static final Logger LOGGER;
    public static final Lazy<NioEventLoopGroup> DEFAULT_CHANNEL;
    public static final Lazy<EpollEventLoopGroup> EPOLL_CHANNEL;
    private final MinecraftServer server;
    public volatile boolean active;
    private final List<ChannelFuture> channels;
    private final List<ClientConnection> connections;
    
    public ServerNetworkIo(final MinecraftServer minecraftServer) {
        this.channels = Collections.<ChannelFuture>synchronizedList(Lists.newArrayList());
        this.connections = Collections.<ClientConnection>synchronizedList(Lists.newArrayList());
        this.server = minecraftServer;
        this.active = true;
    }
    
    public void bind(@Nullable final InetAddress address, final int port) throws IOException {
        synchronized (this.channels) {
            Class<? extends ServerSocketChannel> class4;
            Lazy<? extends EventLoopGroup> lazy5;
            if (Epoll.isAvailable() && this.server.isUsingNativeTransport()) {
                class4 = EpollServerSocketChannel.class;
                lazy5 = ServerNetworkIo.EPOLL_CHANNEL;
                ServerNetworkIo.LOGGER.info("Using epoll channel type");
            }
            else {
                class4 = NioServerSocketChannel.class;
                lazy5 = ServerNetworkIo.DEFAULT_CHANNEL;
                ServerNetworkIo.LOGGER.info("Using default channel type");
            }
            this.channels.add(((ServerBootstrap)((ServerBootstrap)new ServerBootstrap().channel((Class)class4)).childHandler((ChannelHandler)new ChannelInitializer<Channel>() {
                protected void initChannel(final Channel channel) throws Exception {
                    try {
                        channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                    }
                    catch (ChannelException ex) {}
                    channel.pipeline().addLast("timeout", (ChannelHandler)new ReadTimeoutHandler(30)).addLast("legacy_query", (ChannelHandler)new LegacyQueryHandler(ServerNetworkIo.this)).addLast("splitter", (ChannelHandler)new SplitterHandler()).addLast("decoder", (ChannelHandler)new DecoderHandler(NetworkSide.SERVER)).addLast("prepender", (ChannelHandler)new SizePrepender()).addLast("encoder", (ChannelHandler)new PacketEncoder(NetworkSide.CLIENT));
                    final ClientConnection clientConnection2 = new ClientConnection(NetworkSide.SERVER);
                    ServerNetworkIo.this.connections.add(clientConnection2);
                    channel.pipeline().addLast("packet_handler", (ChannelHandler)clientConnection2);
                    clientConnection2.setPacketListener(new ServerHandshakeNetworkHandler(ServerNetworkIo.this.server, clientConnection2));
                }
            }).group((EventLoopGroup)lazy5.get()).localAddress(address, port)).bind().syncUninterruptibly());
        }
    }
    
    @Environment(EnvType.CLIENT)
    public SocketAddress bindLocal() {
        final ChannelFuture channelFuture1;
        synchronized (this.channels) {
            channelFuture1 = ((ServerBootstrap)((ServerBootstrap)new ServerBootstrap().channel((Class)LocalServerChannel.class)).childHandler((ChannelHandler)new ChannelInitializer<Channel>() {
                protected void initChannel(final Channel channel) throws Exception {
                    final ClientConnection clientConnection2 = new ClientConnection(NetworkSide.SERVER);
                    clientConnection2.setPacketListener(new IntegratedServerHandshakeNetworkHandler(ServerNetworkIo.this.server, clientConnection2));
                    ServerNetworkIo.this.connections.add(clientConnection2);
                    channel.pipeline().addLast("packet_handler", (ChannelHandler)clientConnection2);
                }
            }).group((EventLoopGroup)ServerNetworkIo.DEFAULT_CHANNEL.get()).localAddress((SocketAddress)LocalAddress.ANY)).bind().syncUninterruptibly();
            this.channels.add(channelFuture1);
        }
        return channelFuture1.channel().localAddress();
    }
    
    public void stop() {
        this.active = false;
        for (final ChannelFuture channelFuture2 : this.channels) {
            try {
                channelFuture2.channel().close().sync();
            }
            catch (InterruptedException interruptedException3) {
                ServerNetworkIo.LOGGER.error("Interrupted whilst closing channel");
            }
        }
    }
    
    public void tick() {
        synchronized (this.connections) {
            final Iterator<ClientConnection> iterator2 = this.connections.iterator();
            while (iterator2.hasNext()) {
                final ClientConnection clientConnection3 = iterator2.next();
                if (clientConnection3.hasChannel()) {
                    continue;
                }
                if (clientConnection3.isOpen()) {
                    try {
                        clientConnection3.tick();
                    }
                    catch (Exception exception4) {
                        if (clientConnection3.isLocal()) {
                            final CrashReport crashReport5 = CrashReport.create(exception4, "Ticking memory connection");
                            final CrashReportSection crashReportSection6 = crashReport5.addElement("Ticking connection");
                            crashReportSection6.add("Connection", clientConnection3::toString);
                            throw new CrashException(crashReport5);
                        }
                        ServerNetworkIo.LOGGER.warn("Failed to handle packet for {}", clientConnection3.getAddress(), exception4);
                        final TextComponent textComponent5 = new StringTextComponent("Internal server error");
                        clientConnection3.send(new DisconnectS2CPacket(textComponent5), (future -> clientConnection3.disconnect(textComponent5)));
                        clientConnection3.disableAutoRead();
                    }
                }
                else {
                    iterator2.remove();
                    clientConnection3.handleDisconnection();
                }
            }
        }
    }
    
    public MinecraftServer getServer() {
        return this.server;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        final NioEventLoopGroup nioEventLoopGroup;
        DEFAULT_CHANNEL = new Lazy<NioEventLoopGroup>(() -> {
            new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Server IO #%d").setDaemon(true).build());
            return nioEventLoopGroup;
        });
        final EpollEventLoopGroup epollEventLoopGroup;
        EPOLL_CHANNEL = new Lazy<EpollEventLoopGroup>(() -> {
            new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).build());
            return epollEventLoopGroup;
        });
    }
}
