package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class ClientStatusC2SPacket implements Packet<ServerPlayPacketListener>
{
    private Mode mode;
    
    public ClientStatusC2SPacket() {
    }
    
    public ClientStatusC2SPacket(final Mode mode) {
        this.mode = mode;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.mode = buf.<Mode>readEnumConstant(Mode.class);
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeEnumConstant(this.mode);
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onClientStatus(this);
    }
    
    public Mode getMode() {
        return this.mode;
    }
    
    public enum Mode
    {
        a, 
        b;
    }
}
