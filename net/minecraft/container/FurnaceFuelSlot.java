package net.minecraft.container;

import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Inventory;

public class FurnaceFuelSlot extends Slot
{
    private final AbstractFurnaceContainer container;
    
    public FurnaceFuelSlot(final AbstractFurnaceContainer abstractFurnaceContainer, final Inventory inventory, final int invSlot, final int xPosition, final int yPosition) {
        super(inventory, invSlot, xPosition, yPosition);
        this.container = abstractFurnaceContainer;
    }
    
    @Override
    public boolean canInsert(final ItemStack stack) {
        return this.container.isFuel(stack) || isBucket(stack);
    }
    
    @Override
    public int getMaxStackAmount(final ItemStack itemStack) {
        return isBucket(itemStack) ? 1 : super.getMaxStackAmount(itemStack);
    }
    
    public static boolean isBucket(final ItemStack stack) {
        return stack.getItem() == Items.kx;
    }
}
