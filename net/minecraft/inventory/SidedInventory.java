package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

public interface SidedInventory extends Inventory
{
    int[] getInvAvailableSlots(final Direction arg1);
    
    boolean canInsertInvStack(final int arg1, final ItemStack arg2, @Nullable final Direction arg3);
    
    boolean canExtractInvStack(final int arg1, final ItemStack arg2, final Direction arg3);
}
