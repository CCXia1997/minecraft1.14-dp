package net.minecraft.network;

import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.LogManager;
import net.minecraft.util.PacketByteBuf;
import java.io.IOException;
import io.netty.util.AttributeKey;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.Logger;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder<Packet<?>>
{
    private static final Logger LOGGER;
    private static final Marker MARKER;
    private final NetworkSide side;
    
    public PacketEncoder(final NetworkSide networkSide) {
        this.side = networkSide;
    }
    
    protected void a(final ChannelHandlerContext channelHandlerContext, final Packet<?> packet, final ByteBuf byteBuf) throws Exception {
        final NetworkState networkState4 = (NetworkState)channelHandlerContext.channel().attr((AttributeKey)ClientConnection.ATTR_KEY_PROTOCOL).get();
        if (networkState4 == null) {
            throw new RuntimeException("ConnectionProtocol unknown: " + packet);
        }
        final Integer integer5 = networkState4.getPacketId(this.side, packet);
        if (PacketEncoder.LOGGER.isDebugEnabled()) {
            PacketEncoder.LOGGER.debug(PacketEncoder.MARKER, "OUT: [{}:{}] {}", channelHandlerContext.channel().attr((AttributeKey)ClientConnection.ATTR_KEY_PROTOCOL).get(), integer5, packet.getClass().getName());
        }
        if (integer5 == null) {
            throw new IOException("Can't serialize unregistered packet");
        }
        final PacketByteBuf packetByteBuf6 = new PacketByteBuf(byteBuf);
        packetByteBuf6.writeVarInt(integer5);
        try {
            packet.write(packetByteBuf6);
        }
        catch (Throwable throwable7) {
            PacketEncoder.LOGGER.error(throwable7);
            if (packet.isErrorFatal()) {
                throw new PacketEncoderException(throwable7);
            }
            throw throwable7;
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
        MARKER = MarkerManager.getMarker("PACKET_SENT", ClientConnection.MARKER_NETWORK_PACKETS);
    }
}
