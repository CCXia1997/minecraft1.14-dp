package net.minecraft.container;

import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Inventory;

public class ShulkerBoxSlot extends Slot
{
    public ShulkerBoxSlot(final Inventory inventory, final int invSlot, final int xPosition, final int yPosition) {
        super(inventory, invSlot, xPosition, yPosition);
    }
    
    @Override
    public boolean canInsert(final ItemStack stack) {
        return !(Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock);
    }
}
