package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class ResourcePackSendS2CPacket implements Packet<ClientPlayPacketListener>
{
    private String url;
    private String hash;
    
    public ResourcePackSendS2CPacket() {
    }
    
    public ResourcePackSendS2CPacket(final String url, final String string2) {
        this.url = url;
        this.hash = string2;
        if (string2.length() > 40) {
            throw new IllegalArgumentException("Hash is too long (max 40, was " + string2.length() + ")");
        }
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.url = buf.readString(32767);
        this.hash = buf.readString(40);
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeString(this.url);
        buf.writeString(this.hash);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onResourcePackSend(this);
    }
    
    @Environment(EnvType.CLIENT)
    public String getURL() {
        return this.url;
    }
    
    @Environment(EnvType.CLIENT)
    public String getSHA1() {
        return this.hash;
    }
}
