package net.minecraft.container;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;

public class Generic3x3Container extends Container
{
    private final Inventory inventory;
    
    public Generic3x3Container(final int syncId, final PlayerInventory playerInventory) {
        this(syncId, playerInventory, new BasicInventory(9));
    }
    
    public Generic3x3Container(final int syncId, final PlayerInventory playerInventory, final Inventory inventory) {
        super(ContainerType.GENERIC_3X3, syncId);
        Container.checkContainerSize(inventory, 9);
        (this.inventory = inventory).onInvOpen(playerInventory.player);
        for (int integer4 = 0; integer4 < 3; ++integer4) {
            for (int integer5 = 0; integer5 < 3; ++integer5) {
                this.addSlot(new Slot(inventory, integer5 + integer4 * 3, 62 + integer5 * 18, 17 + integer4 * 18));
            }
        }
        for (int integer4 = 0; integer4 < 3; ++integer4) {
            for (int integer5 = 0; integer5 < 9; ++integer5) {
                this.addSlot(new Slot(playerInventory, integer5 + integer4 * 9 + 9, 8 + integer5 * 18, 84 + integer4 * 18));
            }
        }
        for (int integer4 = 0; integer4 < 9; ++integer4) {
            this.addSlot(new Slot(playerInventory, integer4, 8 + integer4 * 18, 142));
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
            if (invSlot < 9) {
                if (!this.insertItem(itemStack4, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.insertItem(itemStack4, 0, 9, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack4.isEmpty()) {
                slot4.setStack(ItemStack.EMPTY);
            }
            else {
                slot4.markDirty();
            }
            if (itemStack4.getAmount() == itemStack3.getAmount()) {
                return ItemStack.EMPTY;
            }
            slot4.onTakeItem(player, itemStack4);
        }
        return itemStack3;
    }
    
    @Override
    public void close(final PlayerEntity player) {
        super.close(player);
        this.inventory.onInvClose(player);
    }
}
