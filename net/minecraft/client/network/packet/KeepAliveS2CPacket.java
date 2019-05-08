package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class KeepAliveS2CPacket implements Packet<ClientPlayPacketListener>
{
    private long id;
    
    public KeepAliveS2CPacket() {
    }
    
    public KeepAliveS2CPacket(final long id) {
        this.id = id;
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onKeepAlive(this);
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.id = buf.readLong();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeLong(this.id);
    }
    
    @Environment(EnvType.CLIENT)
    public long getId() {
        return this.id;
    }
}
