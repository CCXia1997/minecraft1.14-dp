package net.minecraft.network.encryption;

import javax.crypto.ShortBufferException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.buffer.ByteBuf;
import javax.crypto.Cipher;

public class PacketEncryptionManager
{
    private final Cipher cipher;
    private byte[] conversionBuffer;
    private byte[] encryptionBuffer;
    
    protected PacketEncryptionManager(final Cipher cipher) {
        this.conversionBuffer = new byte[0];
        this.encryptionBuffer = new byte[0];
        this.cipher = cipher;
    }
    
    private byte[] toByteArray(final ByteBuf byteBuf) {
        final int integer2 = byteBuf.readableBytes();
        if (this.conversionBuffer.length < integer2) {
            this.conversionBuffer = new byte[integer2];
        }
        byteBuf.readBytes(this.conversionBuffer, 0, integer2);
        return this.conversionBuffer;
    }
    
    protected ByteBuf decrypt(final ChannelHandlerContext context, final ByteBuf byteBuf) throws ShortBufferException {
        final int integer3 = byteBuf.readableBytes();
        final byte[] arr4 = this.toByteArray(byteBuf);
        final ByteBuf byteBuf2 = context.alloc().heapBuffer(this.cipher.getOutputSize(integer3));
        byteBuf2.writerIndex(this.cipher.update(arr4, 0, integer3, byteBuf2.array(), byteBuf2.arrayOffset()));
        return byteBuf2;
    }
    
    protected void encrypt(final ByteBuf buffer, final ByteBuf byteBuf2) throws ShortBufferException {
        final int integer3 = buffer.readableBytes();
        final byte[] arr4 = this.toByteArray(buffer);
        final int integer4 = this.cipher.getOutputSize(integer3);
        if (this.encryptionBuffer.length < integer4) {
            this.encryptionBuffer = new byte[integer4];
        }
        byteBuf2.writeBytes(this.encryptionBuffer, 0, this.cipher.update(arr4, 0, integer3, this.encryptionBuffer));
    }
}
