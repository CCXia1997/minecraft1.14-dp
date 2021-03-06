package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.Packet;

public class LoginCompressionS2CPacket implements Packet<ClientLoginPacketListener>
{
    private int compressionThreshold;
    
    public LoginCompressionS2CPacket() {
    }
    
    public LoginCompressionS2CPacket(final int compressionThreshold) {
        this.compressionThreshold = compressionThreshold;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.compressionThreshold = buf.readVarInt();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.compressionThreshold);
    }
    
    @Override
    public void apply(final ClientLoginPacketListener listener) {
        listener.onCompression(this);
    }
    
    @Environment(EnvType.CLIENT)
    public int getCompressionThreshold() {
        return this.compressionThreshold;
    }
}
