package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class HeldItemChangeS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int slot;
    
    public HeldItemChangeS2CPacket() {
    }
    
    public HeldItemChangeS2CPacket(final int integer) {
        this.slot = integer;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.slot = buf.readByte();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeByte(this.slot);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onHeldItemChange(this);
    }
    
    @Environment(EnvType.CLIENT)
    public int getSlot() {
        return this.slot;
    }
}
