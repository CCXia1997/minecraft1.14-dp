package net.minecraft.network.encryption;

import io.netty.channel.ChannelHandlerContext;
import javax.crypto.Cipher;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncryptor extends MessageToByteEncoder<ByteBuf>
{
    private final PacketEncryptionManager manager;
    
    public PacketEncryptor(final Cipher cipher) {
        this.manager = new PacketEncryptionManager(cipher);
    }
    
    protected void a(final ChannelHandlerContext channelHandlerContext, final ByteBuf byteBuf2, final ByteBuf byteBuf3) throws Exception {
        this.manager.encrypt(byteBuf2, byteBuf3);
    }
}
