package net.minecraft.network;

import net.minecraft.util.PacketByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.util.zip.Deflater;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketDeflater extends MessageToByteEncoder<ByteBuf>
{
    private final byte[] deflateBuffer;
    private final Deflater deflater;
    private int compressionThreshold;
    
    public PacketDeflater(final int integer) {
        this.deflateBuffer = new byte[8192];
        this.compressionThreshold = integer;
        this.deflater = new Deflater();
    }
    
    protected void a(final ChannelHandlerContext channelHandlerContext, final ByteBuf byteBuf2, final ByteBuf byteBuf3) throws Exception {
        final int integer4 = byteBuf2.readableBytes();
        final PacketByteBuf packetByteBuf5 = new PacketByteBuf(byteBuf3);
        if (integer4 < this.compressionThreshold) {
            packetByteBuf5.writeVarInt(0);
            packetByteBuf5.writeBytes(byteBuf2);
        }
        else {
            final byte[] arr6 = new byte[integer4];
            byteBuf2.readBytes(arr6);
            packetByteBuf5.writeVarInt(arr6.length);
            this.deflater.setInput(arr6, 0, integer4);
            this.deflater.finish();
            while (!this.deflater.finished()) {
                final int integer5 = this.deflater.deflate(this.deflateBuffer);
                packetByteBuf5.writeBytes(this.deflateBuffer, 0, integer5);
            }
            this.deflater.reset();
        }
    }
    
    public void setCompressionThreshold(final int compressionThreshold) {
        this.compressionThreshold = compressionThreshold;
    }
}
