package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class GuiCloseS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int id;
    
    public GuiCloseS2CPacket() {
    }
    
    public GuiCloseS2CPacket(final int integer) {
        this.id = integer;
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onGuiClose(this);
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.id = buf.readUnsignedByte();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeByte(this.id);
    }
}
