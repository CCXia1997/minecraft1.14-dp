package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Iterator;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.DefaultedList;
import net.minecraft.item.ItemStack;
import java.util.List;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class InventoryS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int guiId;
    private List<ItemStack> slotStackList;
    
    public InventoryS2CPacket() {
    }
    
    public InventoryS2CPacket(final int guiId, final DefaultedList<ItemStack> defaultedList) {
        this.guiId = guiId;
        this.slotStackList = DefaultedList.<ItemStack>create(defaultedList.size(), ItemStack.EMPTY);
        for (int integer3 = 0; integer3 < this.slotStackList.size(); ++integer3) {
            this.slotStackList.set(integer3, defaultedList.get(integer3).copy());
        }
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.guiId = buf.readUnsignedByte();
        final int integer2 = buf.readShort();
        this.slotStackList = DefaultedList.<ItemStack>create(integer2, ItemStack.EMPTY);
        for (int integer3 = 0; integer3 < integer2; ++integer3) {
            this.slotStackList.set(integer3, buf.readItemStack());
        }
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeByte(this.guiId);
        buf.writeShort(this.slotStackList.size());
        for (final ItemStack itemStack3 : this.slotStackList) {
            buf.writeItemStack(itemStack3);
        }
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onInventory(this);
    }
    
    @Environment(EnvType.CLIENT)
    public int getGuiId() {
        return this.guiId;
    }
    
    @Environment(EnvType.CLIENT)
    public List<ItemStack> getSlotStacks() {
        return this.slotStackList;
    }
}
