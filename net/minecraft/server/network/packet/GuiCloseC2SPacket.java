package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class GuiCloseC2SPacket implements Packet<ServerPlayPacketListener>
{
    private int id;
    
    public GuiCloseC2SPacket() {
    }
    
    @Environment(EnvType.CLIENT)
    public GuiCloseC2SPacket(final int integer) {
        this.id = integer;
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onGuiClose(this);
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.id = buf.readByte();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeByte(this.id);
    }
}
