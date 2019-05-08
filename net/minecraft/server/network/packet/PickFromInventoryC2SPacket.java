package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class PickFromInventoryC2SPacket implements Packet<ServerPlayPacketListener>
{
    private int slot;
    
    public PickFromInventoryC2SPacket() {
    }
    
    @Environment(EnvType.CLIENT)
    public PickFromInventoryC2SPacket(final int slot) {
        this.slot = slot;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.slot = buf.readVarInt();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.slot);
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onPickFromInventory(this);
    }
    
    public int getSlot() {
        return this.slot;
    }
}
