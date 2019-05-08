package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class UpdateSelectedSlotC2SPacket implements Packet<ServerPlayPacketListener>
{
    private int selectedSlot;
    
    public UpdateSelectedSlotC2SPacket() {
    }
    
    @Environment(EnvType.CLIENT)
    public UpdateSelectedSlotC2SPacket(final int integer) {
        this.selectedSlot = integer;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.selectedSlot = buf.readShort();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeShort(this.selectedSlot);
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onUpdateSelectedSlot(this);
    }
    
    public int getSelectedSlot() {
        return this.selectedSlot;
    }
}
