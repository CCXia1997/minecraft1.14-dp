package net.minecraft.inventory;

import java.util.AbstractList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.block.entity.EnderChestBlockEntity;

public class EnderChestInventory extends BasicInventory
{
    private EnderChestBlockEntity currentBlockEntity;
    
    public EnderChestInventory() {
        super(27);
    }
    
    public void setCurrentBlockEntity(final EnderChestBlockEntity enderChestBlockEntity) {
        this.currentBlockEntity = enderChestBlockEntity;
    }
    
    public void readTags(final ListTag listTag) {
        for (int integer2 = 0; integer2 < this.getInvSize(); ++integer2) {
            this.setInvStack(integer2, ItemStack.EMPTY);
        }
        for (int integer2 = 0; integer2 < listTag.size(); ++integer2) {
            final CompoundTag compoundTag3 = listTag.getCompoundTag(integer2);
            final int integer3 = compoundTag3.getByte("Slot") & 0xFF;
            if (integer3 >= 0 && integer3 < this.getInvSize()) {
                this.setInvStack(integer3, ItemStack.fromTag(compoundTag3));
            }
        }
    }
    
    public ListTag getTags() {
        final ListTag listTag1 = new ListTag();
        for (int integer2 = 0; integer2 < this.getInvSize(); ++integer2) {
            final ItemStack itemStack3 = this.getInvStack(integer2);
            if (!itemStack3.isEmpty()) {
                final CompoundTag compoundTag4 = new CompoundTag();
                compoundTag4.putByte("Slot", (byte)integer2);
                itemStack3.toTag(compoundTag4);
                ((AbstractList<CompoundTag>)listTag1).add(compoundTag4);
            }
        }
        return listTag1;
    }
    
    @Override
    public boolean canPlayerUseInv(final PlayerEntity playerEntity) {
        return (this.currentBlockEntity == null || this.currentBlockEntity.canPlayerUse(playerEntity)) && super.canPlayerUseInv(playerEntity);
    }
    
    @Override
    public void onInvOpen(final PlayerEntity playerEntity) {
        if (this.currentBlockEntity != null) {
            this.currentBlockEntity.onOpen();
        }
        super.onInvOpen(playerEntity);
    }
    
    @Override
    public void onInvClose(final PlayerEntity playerEntity) {
        if (this.currentBlockEntity != null) {
            this.currentBlockEntity.onClose();
        }
        super.onInvClose(playerEntity);
        this.currentBlockEntity = null;
    }
}
