package net.minecraft.network;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import net.minecraft.util.PacketByteBuf;
import java.util.List;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.util.zip.Inflater;
import io.netty.handler.codec.ByteToMessageDecoder;

public class PacketInflater extends ByteToMessageDecoder
{
    private final Inflater inflater;
    private int minCompressedSize;
    
    public PacketInflater(final int integer) {
        this.minCompressedSize = integer;
        this.inflater = new Inflater();
    }
    
    protected void decode(final ChannelHandlerContext channelHandlerContext, final ByteBuf byteBuf, final List<Object> list) throws Exception {
        if (byteBuf.readableBytes() == 0) {
            return;
        }
        final PacketByteBuf packetByteBuf4 = new PacketByteBuf(byteBuf);
        final int integer5 = packetByteBuf4.readVarInt();
        if (integer5 == 0) {
            list.add(packetByteBuf4.readBytes(packetByteBuf4.readableBytes()));
        }
        else {
            if (integer5 < this.minCompressedSize) {
                throw new DecoderException("Badly compressed packet - size of " + integer5 + " is below server threshold of " + this.minCompressedSize);
            }
            if (integer5 > 2097152) {
                throw new DecoderException("Badly compressed packet - size of " + integer5 + " is larger than protocol maximum of " + 2097152);
            }
            final byte[] arr6 = new byte[packetByteBuf4.readableBytes()];
            packetByteBuf4.readBytes(arr6);
            this.inflater.setInput(arr6);
            final byte[] arr7 = new byte[integer5];
            this.inflater.inflate(arr7);
            list.add(Unpooled.wrappedBuffer(arr7));
            this.inflater.reset();
        }
    }
    
    public void setCompressionThreshold(final int integer) {
        this.minCompressedSize = integer;
    }
}
