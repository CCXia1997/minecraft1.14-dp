package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.SlotActionType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class ClickWindowC2SPacket implements Packet<ServerPlayPacketListener>
{
    private int syncId;
    private int slot;
    private int button;
    private short transactionId;
    private ItemStack stack;
    private SlotActionType actionType;
    
    public ClickWindowC2SPacket() {
        this.stack = ItemStack.EMPTY;
    }
    
    @Environment(EnvType.CLIENT)
    public ClickWindowC2SPacket(final int integer1, final int integer2, final int integer3, final SlotActionType slotActionType, final ItemStack stack, final short short6) {
        this.stack = ItemStack.EMPTY;
        this.syncId = integer1;
        this.slot = integer2;
        this.button = integer3;
        this.stack = stack.copy();
        this.transactionId = short6;
        this.actionType = slotActionType;
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onClickWindow(this);
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.syncId = buf.readByte();
        this.slot = buf.readShort();
        this.button = buf.readByte();
        this.transactionId = buf.readShort();
        this.actionType = buf.<SlotActionType>readEnumConstant(SlotActionType.class);
        this.stack = buf.readItemStack();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeByte(this.syncId);
        buf.writeShort(this.slot);
        buf.writeByte(this.button);
        buf.writeShort(this.transactionId);
        buf.writeEnumConstant(this.actionType);
        buf.writeItemStack(this.stack);
    }
    
    public int getSyncId() {
        return this.syncId;
    }
    
    public int getSlot() {
        return this.slot;
    }
    
    public int getButton() {
        return this.button;
    }
    
    public short getTransactionId() {
        return this.transactionId;
    }
    
    public ItemStack getStack() {
        return this.stack;
    }
    
    public SlotActionType getActionType() {
        return this.actionType;
    }
}
