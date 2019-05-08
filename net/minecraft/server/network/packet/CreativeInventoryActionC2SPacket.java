package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class CreativeInventoryActionC2SPacket implements Packet<ServerPlayPacketListener>
{
    private int slot;
    private ItemStack stack;
    
    public CreativeInventoryActionC2SPacket() {
        this.stack = ItemStack.EMPTY;
    }
    
    @Environment(EnvType.CLIENT)
    public CreativeInventoryActionC2SPacket(final int integer, final ItemStack itemStack) {
        this.stack = ItemStack.EMPTY;
        this.slot = integer;
        this.stack = itemStack.copy();
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onCreativeInventoryAction(this);
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.slot = buf.readShort();
        this.stack = buf.readItemStack();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeShort(this.slot);
        buf.writeItemStack(this.stack);
    }
    
    public int getSlot() {
        return this.slot;
    }
    
    public ItemStack getItemStack() {
        return this.stack;
    }
}
