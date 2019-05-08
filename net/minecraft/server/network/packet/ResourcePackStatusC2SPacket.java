package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class ResourcePackStatusC2SPacket implements Packet<ServerPlayPacketListener>
{
    private Status status;
    
    public ResourcePackStatusC2SPacket() {
    }
    
    public ResourcePackStatusC2SPacket(final Status status) {
        this.status = status;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.status = buf.<Status>readEnumConstant(Status.class);
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeEnumConstant(this.status);
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onResourcePackStatus(this);
    }
    
    public enum Status
    {
        a, 
        b, 
        c, 
        d;
    }
}
