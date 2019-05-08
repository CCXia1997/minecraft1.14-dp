package net.minecraft.container;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;

public class HopperContainer extends Container
{
    private final Inventory inventory;
    
    public HopperContainer(final int syncId, final PlayerInventory playerInventory) {
        this(syncId, playerInventory, new BasicInventory(5));
    }
    
    public HopperContainer(final int syncId, final PlayerInventory playerInventory, final Inventory inventory) {
        super(ContainerType.HOPPER, syncId);
        Container.checkContainerSize(this.inventory = inventory, 5);
        inventory.onInvOpen(playerInventory.player);
        final int integer4 = 51;
        for (int integer5 = 0; integer5 < 5; ++integer5) {
            this.addSlot(new Slot(inventory, integer5, 44 + integer5 * 18, 20));
        }
        for (int integer5 = 0; integer5 < 3; ++integer5) {
            for (int integer6 = 0; integer6 < 9; ++integer6) {
                this.addSlot(new Slot(playerInventory, integer6 + integer5 * 9 + 9, 8 + integer6 * 18, integer5 * 18 + 51));
            }
        }
        for (int integer5 = 0; integer5 < 9; ++integer5) {
            this.addSlot(new Slot(playerInventory, integer5, 8 + integer5 * 18, 109));
        }
    }
    
    @Override
    public boolean canUse(final PlayerEntity player) {
        return this.inventory.canPlayerUseInv(player);
    }
    
    @Override
    public ItemStack transferSlot(final PlayerEntity player, final int invSlot) {
        ItemStack itemStack3 = ItemStack.EMPTY;
        final Slot slot4 = this.slotList.get(invSlot);
        if (slot4 != null && slot4.hasStack()) {
            final ItemStack itemStack4 = slot4.getStack();
            itemStack3 = itemStack4.copy();
            if (invSlot < this.inventory.getInvSize()) {
                if (!this.insertItem(itemStack4, this.inventory.getInvSize(), this.slotList.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.insertItem(itemStack4, 0, this.inventory.getInvSize(), false)) {
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
        this.inventory.onInvClose(player);
    }
}
