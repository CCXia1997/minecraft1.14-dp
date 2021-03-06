package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.network.NetworkEncryptionUtils;
import net.minecraft.util.PacketByteBuf;
import java.security.PublicKey;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.Packet;

public class LoginHelloS2CPacket implements Packet<ClientLoginPacketListener>
{
    private String serverId;
    private PublicKey publicKey;
    private byte[] nonce;
    
    public LoginHelloS2CPacket() {
    }
    
    public LoginHelloS2CPacket(final String serverId, final PublicKey publicKey, final byte[] nonce) {
        this.serverId = serverId;
        this.publicKey = publicKey;
        this.nonce = nonce;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.serverId = buf.readString(20);
        this.publicKey = NetworkEncryptionUtils.readEncodedPublicKey(buf.readByteArray());
        this.nonce = buf.readByteArray();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeString(this.serverId);
        buf.writeByteArray(this.publicKey.getEncoded());
        buf.writeByteArray(this.nonce);
    }
    
    @Override
    public void apply(final ClientLoginPacketListener listener) {
        listener.onHello(this);
    }
    
    @Environment(EnvType.CLIENT)
    public String getServerId() {
        return this.serverId;
    }
    
    @Environment(EnvType.CLIENT)
    public PublicKey getPublicKey() {
        return this.publicKey;
    }
    
    @Environment(EnvType.CLIENT)
    public byte[] getNonce() {
        return this.nonce;
    }
}
