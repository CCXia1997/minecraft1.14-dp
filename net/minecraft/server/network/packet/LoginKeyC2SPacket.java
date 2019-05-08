package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.security.PrivateKey;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.security.Key;
import net.minecraft.network.NetworkEncryptionUtils;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.network.Packet;

public class LoginKeyC2SPacket implements Packet<ServerLoginPacketListener>
{
    private byte[] encryptedSecretKey;
    private byte[] encryptedNonce;
    
    public LoginKeyC2SPacket() {
        this.encryptedSecretKey = new byte[0];
        this.encryptedNonce = new byte[0];
    }
    
    @Environment(EnvType.CLIENT)
    public LoginKeyC2SPacket(final SecretKey secretKey, final PublicKey publicKey, final byte[] arr) {
        this.encryptedSecretKey = new byte[0];
        this.encryptedNonce = new byte[0];
        this.encryptedSecretKey = NetworkEncryptionUtils.encrypt(publicKey, secretKey.getEncoded());
        this.encryptedNonce = NetworkEncryptionUtils.encrypt(publicKey, arr);
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.encryptedSecretKey = buf.readByteArray();
        this.encryptedNonce = buf.readByteArray();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeByteArray(this.encryptedSecretKey);
        buf.writeByteArray(this.encryptedNonce);
    }
    
    @Override
    public void apply(final ServerLoginPacketListener listener) {
        listener.onKey(this);
    }
    
    public SecretKey decryptSecretKey(final PrivateKey privateKey) {
        return NetworkEncryptionUtils.decryptSecretKey(privateKey, this.encryptedSecretKey);
    }
    
    public byte[] decryptNonce(final PrivateKey privateKey) {
        if (privateKey == null) {
            return this.encryptedNonce;
        }
        return NetworkEncryptionUtils.decrypt(privateKey, this.encryptedNonce);
    }
}
