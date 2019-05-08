package net.minecraft.network;

import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.LogManager;
import java.io.IOException;
import io.netty.util.AttributeKey;
import net.minecraft.util.PacketByteBuf;
import java.util.List;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.Logger;
import io.netty.handler.codec.ByteToMessageDecoder;

public class DecoderHandler extends ByteToMessageDecoder
{
    private static final Logger LOGGER;
    private static final Marker MARKER;
    private final NetworkSide side;
    
    public DecoderHandler(final NetworkSide networkSide) {
        this.side = networkSide;
    }
    
    protected void decode(final ChannelHandlerContext channelHandlerContext, final ByteBuf byteBuf, final List<Object> list) throws Exception {
        if (byteBuf.readableBytes() == 0) {
            return;
        }
        final PacketByteBuf packetByteBuf4 = new PacketByteBuf(byteBuf);
        final int integer5 = packetByteBuf4.readVarInt();
        final Packet<?> packet6 = ((NetworkState)channelHandlerContext.channel().attr((AttributeKey)ClientConnection.ATTR_KEY_PROTOCOL).get()).getPacketHandler(this.side, integer5);
        if (packet6 == null) {
            throw new IOException("Bad packet id " + integer5);
        }
        packet6.read(packetByteBuf4);
        if (packetByteBuf4.readableBytes() > 0) {
            throw new IOException("Packet " + ((NetworkState)channelHandlerContext.channel().attr((AttributeKey)ClientConnection.ATTR_KEY_PROTOCOL).get()).getId() + "/" + integer5 + " (" + packet6.getClass().getSimpleName() + ") was larger than I expected, found " + packetByteBuf4.readableBytes() + " bytes extra whilst reading packet " + integer5);
        }
        list.add(packet6);
        if (DecoderHandler.LOGGER.isDebugEnabled()) {
            DecoderHandler.LOGGER.debug(DecoderHandler.MARKER, " IN: [{}:{}] {}", channelHandlerContext.channel().attr((AttributeKey)ClientConnection.ATTR_KEY_PROTOCOL).get(), integer5, packet6.getClass().getName());
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
        MARKER = MarkerManager.getMarker("PACKET_RECEIVED", ClientConnection.MARKER_NETWORK_PACKETS);
    }
}
