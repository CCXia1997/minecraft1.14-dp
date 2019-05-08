package net.minecraft.network;

import io.netty.handler.codec.CorruptedFrameException;
import net.minecraft.util.PacketByteBuf;
import io.netty.buffer.Unpooled;
import java.util.List;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class SplitterHandler extends ByteToMessageDecoder
{
    protected void decode(final ChannelHandlerContext channelHandlerContext, final ByteBuf byteBuf, final List<Object> list) throws Exception {
        byteBuf.markReaderIndex();
        final byte[] arr4 = new byte[3];
        for (int integer5 = 0; integer5 < arr4.length; ++integer5) {
            if (!byteBuf.isReadable()) {
                byteBuf.resetReaderIndex();
                return;
            }
            arr4[integer5] = byteBuf.readByte();
            if (arr4[integer5] >= 0) {
                final PacketByteBuf packetByteBuf6 = new PacketByteBuf(Unpooled.wrappedBuffer(arr4));
                try {
                    final int integer6 = packetByteBuf6.readVarInt();
                    if (byteBuf.readableBytes() < integer6) {
                        byteBuf.resetReaderIndex();
                        return;
                    }
                    list.add(byteBuf.readBytes(integer6));
                    return;
                }
                finally {
                    packetByteBuf6.release();
                }
            }
        }
        throw new CorruptedFrameException("length wider than 21-bit");
    }
}
