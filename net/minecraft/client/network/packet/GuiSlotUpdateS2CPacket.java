package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class GuiSlotUpdateS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int id;
    private int slot;
    private ItemStack itemStack;
    
    public GuiSlotUpdateS2CPacket() {
        this.itemStack = ItemStack.EMPTY;
    }
    
    public GuiSlotUpdateS2CPacket(final int id, final int slot, final ItemStack itemStack) {
        this.itemStack = ItemStack.EMPTY;
        this.id = id;
        this.slot = slot;
        this.itemStack = itemStack.copy();
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onGuiSlotUpdate(this);
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.id = buf.readByte();
        this.slot = buf.readShort();
        this.itemStack = buf.readItemStack();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeByte(this.id);
        buf.writeShort(this.slot);
        buf.writeItemStack(this.itemStack);
    }
    
    @Environment(EnvType.CLIENT)
    public int getId() {
        return this.id;
    }
    
    @Environment(EnvType.CLIENT)
    public int getSlot() {
        return this.slot;
    }
    
    @Environment(EnvType.CLIENT)
    public ItemStack getItemStack() {
        return this.itemStack;
    }
}
