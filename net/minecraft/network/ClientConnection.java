package net.minecraft.network;

import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.LogManager;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraft.network.encryption.PacketEncryptor;
import net.minecraft.network.encryption.PacketDecryptor;
import java.security.Key;
import javax.crypto.SecretKey;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.ChannelHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.epoll.Epoll;
import java.net.InetAddress;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.local.LocalChannel;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.client.network.packet.DisconnectS2CPacket;
import io.netty.handler.timeout.TimeoutException;
import net.minecraft.text.TranslatableTextComponent;
import io.netty.channel.ChannelHandlerContext;
import com.google.common.collect.Queues;
import net.minecraft.text.TextComponent;
import net.minecraft.network.listener.PacketListener;
import java.net.SocketAddress;
import io.netty.channel.Channel;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Queue;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import net.minecraft.util.Lazy;
import io.netty.util.AttributeKey;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.Logger;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientConnection extends SimpleChannelInboundHandler<Packet<?>>
{
    private static final Logger LOGGER;
    public static final Marker MARKER_NETWORK;
    public static final Marker MARKER_NETWORK_PACKETS;
    public static final AttributeKey<NetworkState> ATTR_KEY_PROTOCOL;
    public static final Lazy<NioEventLoopGroup> CLIENT_IO_GROUP;
    public static final Lazy<EpollEventLoopGroup> CLIENT_IO_GROUP_EPOLL;
    public static final Lazy<DefaultEventLoopGroup> CLIENT_IO_GROUP_LOCAL;
    private final NetworkSide side;
    private final Queue<PacketWrapper> packetQueue;
    private final ReentrantReadWriteLock lock;
    private Channel channel;
    private SocketAddress address;
    private PacketListener packetListener;
    private TextComponent disconnectReason;
    private boolean encrypted;
    private boolean disconnected;
    private int packetsReceivedCounter;
    private int packetsSentCounter;
    private float avgPacketsReceived;
    private float avgPacketsSent;
    private int ticks;
    private boolean v;
    
    public ClientConnection(final NetworkSide networkSide) {
        this.packetQueue = Queues.newConcurrentLinkedQueue();
        this.lock = new ReentrantReadWriteLock();
        this.side = networkSide;
    }
    
    public void channelActive(final ChannelHandlerContext channelHandlerContext) throws Exception {
        super.channelActive(channelHandlerContext);
        this.channel = channelHandlerContext.channel();
        this.address = this.channel.remoteAddress();
        try {
            this.setState(NetworkState.HANDSHAKING);
        }
        catch (Throwable throwable2) {
            ClientConnection.LOGGER.fatal(throwable2);
        }
    }
    
    public void setState(final NetworkState networkState) {
        this.channel.attr((AttributeKey)ClientConnection.ATTR_KEY_PROTOCOL).set(networkState);
        this.channel.config().setAutoRead(true);
        ClientConnection.LOGGER.debug("Enabled auto read");
    }
    
    public void channelInactive(final ChannelHandlerContext channelHandlerContext) throws Exception {
        this.disconnect(new TranslatableTextComponent("disconnect.endOfStream", new Object[0]));
    }
    
    public void exceptionCaught(final ChannelHandlerContext channelHandlerContext, final Throwable throwable) {
        if (throwable instanceof PacketEncoderException) {
            ClientConnection.LOGGER.debug("Skipping packet due to errors", throwable.getCause());
            return;
        }
        final boolean boolean3 = !this.v;
        this.v = true;
        if (!this.channel.isOpen()) {
            return;
        }
        if (throwable instanceof TimeoutException) {
            ClientConnection.LOGGER.debug("Timeout", throwable);
            this.disconnect(new TranslatableTextComponent("disconnect.timeout", new Object[0]));
        }
        else {
            final TextComponent textComponent4 = new TranslatableTextComponent("disconnect.genericReason", new Object[] { "Internal Exception: " + throwable });
            if (boolean3) {
                ClientConnection.LOGGER.debug("Failed to sent packet", throwable);
                this.send(new DisconnectS2CPacket(textComponent4), (future -> this.disconnect(textComponent4)));
                this.disableAutoRead();
            }
            else {
                ClientConnection.LOGGER.debug("Double fault", throwable);
                this.disconnect(textComponent4);
            }
        }
    }
    
    protected void a(final ChannelHandlerContext channelHandlerContext, final Packet<?> packet) throws Exception {
        if (this.channel.isOpen()) {
            try {
                ClientConnection.handlePacket(packet, this.packetListener);
            }
            catch (OffThreadException ex) {}
            ++this.packetsReceivedCounter;
        }
    }
    
    private static <T extends PacketListener> void handlePacket(final Packet<T> packet, final PacketListener listener) {
        packet.apply((T)listener);
    }
    
    public void setPacketListener(final PacketListener packetListener) {
        Validate.notNull(packetListener, "packetListener", new Object[0]);
        ClientConnection.LOGGER.debug("Set listener of {} to {}", this, packetListener);
        this.packetListener = packetListener;
    }
    
    public void send(final Packet<?> packet) {
        this.send(packet, null);
    }
    
    public void send(final Packet<?> packet, @Nullable final GenericFutureListener<? extends Future<? super Void>> listener) {
        if (this.isOpen()) {
            this.sendQueuedPackets();
            this.sendImmediately(packet, listener);
        }
        else {
            this.lock.writeLock().lock();
            try {
                this.packetQueue.add(new PacketWrapper(packet, listener));
            }
            finally {
                this.lock.writeLock().unlock();
            }
        }
    }
    
    private void sendImmediately(final Packet<?> packet, @Nullable final GenericFutureListener<? extends Future<? super Void>> listener) {
        final NetworkState networkState3 = NetworkState.getPacketHandlerState(packet);
        final NetworkState networkState4 = (NetworkState)this.channel.attr((AttributeKey)ClientConnection.ATTR_KEY_PROTOCOL).get();
        ++this.packetsSentCounter;
        if (networkState4 != networkState3) {
            ClientConnection.LOGGER.debug("Disabled auto read");
            this.channel.config().setAutoRead(false);
        }
        if (this.channel.eventLoop().inEventLoop()) {
            if (networkState3 != networkState4) {
                this.setState(networkState3);
            }
            final ChannelFuture channelFuture5 = this.channel.writeAndFlush(packet);
            if (listener != null) {
                channelFuture5.addListener((GenericFutureListener)listener);
            }
            channelFuture5.addListener((GenericFutureListener)ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
        else {
            final NetworkState state;
            final ChannelFuture channelFuture6;
            this.channel.eventLoop().execute(() -> {
                if (state != networkState4) {
                    this.setState(state);
                }
                channelFuture6 = this.channel.writeAndFlush(packet);
                if (listener != null) {
                    channelFuture6.addListener((GenericFutureListener)listener);
                }
                channelFuture6.addListener((GenericFutureListener)ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            });
        }
    }
    
    private void sendQueuedPackets() {
        if (this.channel == null || !this.channel.isOpen()) {
            return;
        }
        this.lock.readLock().lock();
        try {
            while (!this.packetQueue.isEmpty()) {
                final PacketWrapper packetWrapper1 = this.packetQueue.poll();
                this.sendImmediately(packetWrapper1.packet, packetWrapper1.listener);
            }
        }
        finally {
            this.lock.readLock().unlock();
        }
    }
    
    public void tick() {
        this.sendQueuedPackets();
        if (this.packetListener instanceof ServerLoginNetworkHandler) {
            ((ServerLoginNetworkHandler)this.packetListener).tick();
        }
        if (this.packetListener instanceof ServerPlayNetworkHandler) {
            ((ServerPlayNetworkHandler)this.packetListener).tick();
        }
        if (this.channel != null) {
            this.channel.flush();
        }
        if (this.ticks++ % 20 == 0) {
            this.avgPacketsSent = this.avgPacketsSent * 0.75f + this.packetsSentCounter * 0.25f;
            this.avgPacketsReceived = this.avgPacketsReceived * 0.75f + this.packetsReceivedCounter * 0.25f;
            this.packetsSentCounter = 0;
            this.packetsReceivedCounter = 0;
        }
    }
    
    public SocketAddress getAddress() {
        return this.address;
    }
    
    public void disconnect(final TextComponent disconnectReason) {
        if (this.channel.isOpen()) {
            this.channel.close().awaitUninterruptibly();
            this.disconnectReason = disconnectReason;
        }
    }
    
    public boolean isLocal() {
        return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
    }
    
    @Environment(EnvType.CLIENT)
    public static ClientConnection connect(final InetAddress address, final int port, final boolean shouldUseNativeTransport) {
        final ClientConnection clientConnection4 = new ClientConnection(NetworkSide.CLIENT);
        Class<? extends SocketChannel> class5;
        Lazy<? extends EventLoopGroup> lazy6;
        if (Epoll.isAvailable() && shouldUseNativeTransport) {
            class5 = EpollSocketChannel.class;
            lazy6 = ClientConnection.CLIENT_IO_GROUP_EPOLL;
        }
        else {
            class5 = NioSocketChannel.class;
            lazy6 = ClientConnection.CLIENT_IO_GROUP;
        }
        ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)lazy6.get())).handler((ChannelHandler)new ChannelInitializer<Channel>() {
            protected void initChannel(final Channel channel) throws Exception {
                try {
                    channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                }
                catch (ChannelException ex) {}
                channel.pipeline().addLast("timeout", (ChannelHandler)new ReadTimeoutHandler(30)).addLast("splitter", (ChannelHandler)new SplitterHandler()).addLast("decoder", (ChannelHandler)new DecoderHandler(NetworkSide.CLIENT)).addLast("prepender", (ChannelHandler)new SizePrepender()).addLast("encoder", (ChannelHandler)new PacketEncoder(NetworkSide.SERVER)).addLast("packet_handler", (ChannelHandler)clientConnection4);
            }
        })).channel((Class)class5)).connect(address, port).syncUninterruptibly();
        return clientConnection4;
    }
    
    @Environment(EnvType.CLIENT)
    public static ClientConnection connect(final SocketAddress address) {
        final ClientConnection clientConnection2 = new ClientConnection(NetworkSide.CLIENT);
        ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)ClientConnection.CLIENT_IO_GROUP_LOCAL.get())).handler((ChannelHandler)new ChannelInitializer<Channel>() {
            protected void initChannel(final Channel channel) throws Exception {
                channel.pipeline().addLast("packet_handler", (ChannelHandler)clientConnection2);
            }
        })).channel((Class)LocalChannel.class)).connect(address).syncUninterruptibly();
        return clientConnection2;
    }
    
    public void setupEncryption(final SecretKey secretKey) {
        this.encrypted = true;
        this.channel.pipeline().addBefore("splitter", "decrypt", (ChannelHandler)new PacketDecryptor(NetworkEncryptionUtils.cipherFromKey(2, secretKey)));
        this.channel.pipeline().addBefore("prepender", "encrypt", (ChannelHandler)new PacketEncryptor(NetworkEncryptionUtils.cipherFromKey(1, secretKey)));
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isEncrypted() {
        return this.encrypted;
    }
    
    public boolean isOpen() {
        return this.channel != null && this.channel.isOpen();
    }
    
    public boolean hasChannel() {
        return this.channel == null;
    }
    
    public PacketListener getPacketListener() {
        return this.packetListener;
    }
    
    @Nullable
    public TextComponent getDisconnectReason() {
        return this.disconnectReason;
    }
    
    public void disableAutoRead() {
        this.channel.config().setAutoRead(false);
    }
    
    public void setMinCompressedSize(final int integer) {
        if (integer >= 0) {
            if (this.channel.pipeline().get("decompress") instanceof PacketInflater) {
                ((PacketInflater)this.channel.pipeline().get("decompress")).setCompressionThreshold(integer);
            }
            else {
                this.channel.pipeline().addBefore("decoder", "decompress", (ChannelHandler)new PacketInflater(integer));
            }
            if (this.channel.pipeline().get("compress") instanceof PacketDeflater) {
                ((PacketDeflater)this.channel.pipeline().get("compress")).setCompressionThreshold(integer);
            }
            else {
                this.channel.pipeline().addBefore("encoder", "compress", (ChannelHandler)new PacketDeflater(integer));
            }
        }
        else {
            if (this.channel.pipeline().get("decompress") instanceof PacketInflater) {
                this.channel.pipeline().remove("decompress");
            }
            if (this.channel.pipeline().get("compress") instanceof PacketDeflater) {
                this.channel.pipeline().remove("compress");
            }
        }
    }
    
    public void handleDisconnection() {
        if (this.channel == null || this.channel.isOpen()) {
            return;
        }
        if (this.disconnected) {
            ClientConnection.LOGGER.warn("handleDisconnection() called twice");
        }
        else {
            this.disconnected = true;
            if (this.getDisconnectReason() != null) {
                this.getPacketListener().onDisconnected(this.getDisconnectReason());
            }
            else if (this.getPacketListener() != null) {
                this.getPacketListener().onDisconnected(new TranslatableTextComponent("multiplayer.disconnect.generic", new Object[0]));
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    public float getAveragePacketsReceived() {
        return this.avgPacketsReceived;
    }
    
    @Environment(EnvType.CLIENT)
    public float getAveragePacketsSent() {
        return this.avgPacketsSent;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        MARKER_NETWORK = MarkerManager.getMarker("NETWORK");
        MARKER_NETWORK_PACKETS = MarkerManager.getMarker("NETWORK_PACKETS", ClientConnection.MARKER_NETWORK);
        ATTR_KEY_PROTOCOL = AttributeKey.valueOf("protocol");
        final NioEventLoopGroup nioEventLoopGroup;
        CLIENT_IO_GROUP = new Lazy<NioEventLoopGroup>(() -> {
            new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build());
            return nioEventLoopGroup;
        });
        final EpollEventLoopGroup epollEventLoopGroup;
        CLIENT_IO_GROUP_EPOLL = new Lazy<EpollEventLoopGroup>(() -> {
            new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build());
            return epollEventLoopGroup;
        });
        final DefaultEventLoopGroup defaultEventLoopGroup;
        CLIENT_IO_GROUP_LOCAL = new Lazy<DefaultEventLoopGroup>(() -> {
            new DefaultEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Client IO #%d").setDaemon(true).build());
            return defaultEventLoopGroup;
        });
    }
    
    static class PacketWrapper
    {
        private final Packet<?> packet;
        @Nullable
        private final GenericFutureListener<? extends Future<? super Void>> listener;
        
        public PacketWrapper(final Packet<?> packet, @Nullable final GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
            this.packet = packet;
            this.listener = genericFutureListener;
        }
    }
}
