package net.minecraft.network;

import org.apache.logging.log4j.LogManager;
import io.netty.buffer.Unpooled;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.server.MinecraftServer;
import java.nio.charset.StandardCharsets;
import java.net.InetSocketAddress;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.server.ServerNetworkIo;
import org.apache.logging.log4j.Logger;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class LegacyQueryHandler extends ChannelInboundHandlerAdapter
{
    private static final Logger LOGGER;
    private final ServerNetworkIo networkIo;
    
    public LegacyQueryHandler(final ServerNetworkIo networkIo) {
        this.networkIo = networkIo;
    }
    
    public void channelRead(final ChannelHandlerContext channelHandlerContext, final Object object) throws Exception {
        final ByteBuf byteBuf3 = (ByteBuf)object;
        byteBuf3.markReaderIndex();
        boolean boolean4 = true;
        try {
            if (byteBuf3.readUnsignedByte() != 254) {
                return;
            }
            final InetSocketAddress inetSocketAddress5 = (InetSocketAddress)channelHandlerContext.channel().remoteAddress();
            final MinecraftServer minecraftServer6 = this.networkIo.getServer();
            final int integer7 = byteBuf3.readableBytes();
            switch (integer7) {
                case 0: {
                    LegacyQueryHandler.LOGGER.debug("Ping: (<1.3.x) from {}:{}", inetSocketAddress5.getAddress(), inetSocketAddress5.getPort());
                    final String string8 = String.format("%s§%d§%d", minecraftServer6.getServerMotd(), minecraftServer6.getCurrentPlayerCount(), minecraftServer6.getMaxPlayerCount());
                    this.reply(channelHandlerContext, this.toBuffer(string8));
                    break;
                }
                case 1: {
                    if (byteBuf3.readUnsignedByte() != 1) {
                        return;
                    }
                    LegacyQueryHandler.LOGGER.debug("Ping: (1.4-1.5.x) from {}:{}", inetSocketAddress5.getAddress(), inetSocketAddress5.getPort());
                    final String string8 = String.format("§1\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", 127, minecraftServer6.getVersion(), minecraftServer6.getServerMotd(), minecraftServer6.getCurrentPlayerCount(), minecraftServer6.getMaxPlayerCount());
                    this.reply(channelHandlerContext, this.toBuffer(string8));
                    break;
                }
                default: {
                    boolean boolean5 = byteBuf3.readUnsignedByte() == 1;
                    boolean5 &= (byteBuf3.readUnsignedByte() == 250);
                    boolean5 &= "MC|PingHost".equals(new String(byteBuf3.readBytes(byteBuf3.readShort() * 2).array(), StandardCharsets.UTF_16BE));
                    final int integer8 = byteBuf3.readUnsignedShort();
                    boolean5 &= (byteBuf3.readUnsignedByte() >= 73);
                    boolean5 &= (3 + byteBuf3.readBytes(byteBuf3.readShort() * 2).array().length + 4 == integer8);
                    boolean5 &= (byteBuf3.readInt() <= 65535);
                    boolean5 &= (byteBuf3.readableBytes() == 0);
                    if (!boolean5) {
                        return;
                    }
                    LegacyQueryHandler.LOGGER.debug("Ping: (1.6) from {}:{}", inetSocketAddress5.getAddress(), inetSocketAddress5.getPort());
                    final String string9 = String.format("§1\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", 127, minecraftServer6.getVersion(), minecraftServer6.getServerMotd(), minecraftServer6.getCurrentPlayerCount(), minecraftServer6.getMaxPlayerCount());
                    final ByteBuf byteBuf4 = this.toBuffer(string9);
                    try {
                        this.reply(channelHandlerContext, byteBuf4);
                    }
                    finally {
                        byteBuf4.release();
                    }
                    break;
                }
            }
            byteBuf3.release();
            boolean4 = false;
        }
        catch (RuntimeException ex) {}
        finally {
            if (boolean4) {
                byteBuf3.resetReaderIndex();
                channelHandlerContext.channel().pipeline().remove("legacy_query");
                channelHandlerContext.fireChannelRead(object);
            }
        }
    }
    
    private void reply(final ChannelHandlerContext ctx, final ByteBuf buf) {
        ctx.pipeline().firstContext().writeAndFlush(buf).addListener((GenericFutureListener)ChannelFutureListener.CLOSE);
    }
    
    private ByteBuf toBuffer(final String s) {
        final ByteBuf byteBuf2 = Unpooled.buffer();
        byteBuf2.writeByte(255);
        final char[] arr3 = s.toCharArray();
        byteBuf2.writeShort(arr3.length);
        for (final char character7 : arr3) {
            byteBuf2.writeChar((int)character7);
        }
        return byteBuf2;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
