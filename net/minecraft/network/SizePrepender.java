package net.minecraft.network;

import net.minecraft.util.PacketByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.MessageToByteEncoder;

@ChannelHandler.Sharable
public class SizePrepender extends MessageToByteEncoder<ByteBuf>
{
    protected void a(final ChannelHandlerContext channelHandlerContext, final ByteBuf byteBuf2, final ByteBuf byteBuf3) throws Exception {
        final int integer4 = byteBuf2.readableBytes();
        final int integer5 = PacketByteBuf.getVarIntSizeBytes(integer4);
        if (integer5 > 3) {
            throw new IllegalArgumentException("unable to fit " + integer4 + " into " + 3);
        }
        final PacketByteBuf packetByteBuf6 = new PacketByteBuf(byteBuf3);
        packetByteBuf6.ensureWritable(integer5 + integer4);
        packetByteBuf6.writeVarInt(integer4);
        packetByteBuf6.writeBytes(byteBuf2, byteBuf2.readerIndex(), integer4);
    }
}
