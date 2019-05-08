package net.minecraft.container;

import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;

public interface ContainerListener
{
    void onContainerRegistered(final Container arg1, final DefaultedList<ItemStack> arg2);
    
    void onContainerSlotUpdate(final Container arg1, final int arg2, final ItemStack arg3);
    
    void onContainerPropertyUpdate(final Container arg1, final int arg2, final int arg3);
}
