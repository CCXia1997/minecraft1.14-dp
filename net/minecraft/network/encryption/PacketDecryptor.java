package net.minecraft.network.encryption;

import java.util.List;
import io.netty.channel.ChannelHandlerContext;
import javax.crypto.Cipher;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.MessageToMessageDecoder;

public class PacketDecryptor extends MessageToMessageDecoder<ByteBuf>
{
    private final PacketEncryptionManager manager;
    
    public PacketDecryptor(final Cipher cipher) {
        this.manager = new PacketEncryptionManager(cipher);
    }
    
    protected void a(final ChannelHandlerContext channelHandlerContext, final ByteBuf byteBuf, final List<Object> list) throws Exception {
        list.add(this.manager.decrypt(channelHandlerContext, byteBuf));
    }
}
