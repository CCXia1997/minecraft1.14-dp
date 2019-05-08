package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class RenameItemC2SPacket implements Packet<ServerPlayPacketListener>
{
    private String itemName;
    
    public RenameItemC2SPacket() {
    }
    
    public RenameItemC2SPacket(final String string) {
        this.itemName = string;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.itemName = buf.readString(32767);
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeString(this.itemName);
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onRenameItem(this);
    }
    
    public String getItemName() {
        return this.itemName;
    }
}
