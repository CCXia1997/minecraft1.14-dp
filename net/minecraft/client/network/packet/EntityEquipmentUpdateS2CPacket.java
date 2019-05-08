package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class EntityEquipmentUpdateS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int id;
    private EquipmentSlot slot;
    private ItemStack stack;
    
    public EntityEquipmentUpdateS2CPacket() {
        this.stack = ItemStack.EMPTY;
    }
    
    public EntityEquipmentUpdateS2CPacket(final int id, final EquipmentSlot slot, final ItemStack itemStack) {
        this.stack = ItemStack.EMPTY;
        this.id = id;
        this.slot = slot;
        this.stack = itemStack.copy();
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.id = buf.readVarInt();
        this.slot = buf.<EquipmentSlot>readEnumConstant(EquipmentSlot.class);
        this.stack = buf.readItemStack();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.id);
        buf.writeEnumConstant(this.slot);
        buf.writeItemStack(this.stack);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onEquipmentUpdate(this);
    }
    
    @Environment(EnvType.CLIENT)
    public ItemStack getStack() {
        return this.stack;
    }
    
    @Environment(EnvType.CLIENT)
    public int getId() {
        return this.id;
    }
    
    @Environment(EnvType.CLIENT)
    public EquipmentSlot getSlot() {
        return this.slot;
    }
}
