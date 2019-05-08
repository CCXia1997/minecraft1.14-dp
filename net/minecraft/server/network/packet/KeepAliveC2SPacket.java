package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class KeepAliveC2SPacket implements Packet<ServerPlayPacketListener>
{
    private long id;
    
    public KeepAliveC2SPacket() {
    }
    
    @Environment(EnvType.CLIENT)
    public KeepAliveC2SPacket(final long id) {
        this.id = id;
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
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
    
    public long getId() {
        return this.id;
    }
}
