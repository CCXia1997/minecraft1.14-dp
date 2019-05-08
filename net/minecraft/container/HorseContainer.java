package net.minecraft.container;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.inventory.Inventory;

public class HorseContainer extends Container
{
    private final Inventory playerInv;
    private final HorseBaseEntity entity;
    
    public HorseContainer(final int syncId, final PlayerInventory playerInventory, final Inventory inventory, final HorseBaseEntity horseBaseEntity) {
        super(null, syncId);
        this.playerInv = inventory;
        this.entity = horseBaseEntity;
        final int integer5 = 3;
        inventory.onInvOpen(playerInventory.player);
        final int integer6 = -18;
        this.addSlot(new Slot(inventory, 0, 8, 18) {
            @Override
            public boolean canInsert(final ItemStack stack) {
                return stack.getItem() == Items.kB && !this.hasStack() && horseBaseEntity.canBeSaddled();
            }
            
            @Environment(EnvType.CLIENT)
            @Override
            public boolean doDrawHoveringEffect() {
                return horseBaseEntity.canBeSaddled();
            }
        });
        this.addSlot(new Slot(inventory, 1, 8, 36) {
            @Override
            public boolean canInsert(final ItemStack stack) {
                return horseBaseEntity.canEquip(stack);
            }
            
            @Environment(EnvType.CLIENT)
            @Override
            public boolean doDrawHoveringEffect() {
                return horseBaseEntity.canEquip();
            }
            
            @Override
            public int getMaxStackAmount() {
                return 1;
            }
        });
        if (horseBaseEntity instanceof AbstractDonkeyEntity && ((AbstractDonkeyEntity)horseBaseEntity).hasChest()) {
            for (int integer7 = 0; integer7 < 3; ++integer7) {
                for (int integer8 = 0; integer8 < ((AbstractDonkeyEntity)horseBaseEntity).dZ(); ++integer8) {
                    this.addSlot(new Slot(inventory, 2 + integer8 + integer7 * ((AbstractDonkeyEntity)horseBaseEntity).dZ(), 80 + integer8 * 18, 18 + integer7 * 18));
                }
            }
        }
        for (int integer7 = 0; integer7 < 3; ++integer7) {
            for (int integer8 = 0; integer8 < 9; ++integer8) {
                this.addSlot(new Slot(playerInventory, integer8 + integer7 * 9 + 9, 8 + integer8 * 18, 102 + integer7 * 18 - 18));
            }
        }
        for (int integer7 = 0; integer7 < 9; ++integer7) {
            this.addSlot(new Slot(playerInventory, integer7, 8 + integer7 * 18, 142));
        }
    }
    
    @Override
    public boolean canUse(final PlayerEntity player) {
        return this.playerInv.canPlayerUseInv(player) && this.entity.isAlive() && this.entity.distanceTo(player) < 8.0f;
    }
    
    @Override
    public ItemStack transferSlot(final PlayerEntity player, final int invSlot) {
        ItemStack itemStack3 = ItemStack.EMPTY;
        final Slot slot4 = this.slotList.get(invSlot);
        if (slot4 != null && slot4.hasStack()) {
            final ItemStack itemStack4 = slot4.getStack();
            itemStack3 = itemStack4.copy();
            if (invSlot < this.playerInv.getInvSize()) {
                if (!this.insertItem(itemStack4, this.playerInv.getInvSize(), this.slotList.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (this.getSlot(1).canInsert(itemStack4) && !this.getSlot(1).hasStack()) {
                if (!this.insertItem(itemStack4, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (this.getSlot(0).canInsert(itemStack4)) {
                if (!this.insertItem(itemStack4, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (this.playerInv.getInvSize() <= 2 || !this.insertItem(itemStack4, 2, this.playerInv.getInvSize(), false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack4.isEmpty()) {
                slot4.setStack(ItemStack.EMPTY);
            }
            else {
                slot4.markDirty();
            }
        }
        return itemStack3;
    }
    
    @Override
    public void close(final PlayerEntity player) {
        super.close(player);
        this.playerInv.onInvClose(player);
    }
}
