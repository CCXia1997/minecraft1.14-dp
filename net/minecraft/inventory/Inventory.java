package net.minecraft.inventory;

import java.util.Set;
import net.minecraft.item.Item;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Clearable;

public interface Inventory extends Clearable
{
    int getInvSize();
    
    boolean isInvEmpty();
    
    ItemStack getInvStack(final int arg1);
    
    ItemStack takeInvStack(final int arg1, final int arg2);
    
    ItemStack removeInvStack(final int arg1);
    
    void setInvStack(final int arg1, final ItemStack arg2);
    
    default int getInvMaxStackAmount() {
        return 64;
    }
    
    void markDirty();
    
    boolean canPlayerUseInv(final PlayerEntity arg1);
    
    default void onInvOpen(final PlayerEntity playerEntity) {
    }
    
    default void onInvClose(final PlayerEntity playerEntity) {
    }
    
    default boolean isValidInvStack(final int slot, final ItemStack itemStack) {
        return true;
    }
    
    default int getInvAmountOf(final Item item) {
        int integer2 = 0;
        for (int integer3 = 0; integer3 < this.getInvSize(); ++integer3) {
            final ItemStack itemStack4 = this.getInvStack(integer3);
            if (itemStack4.getItem().equals(item)) {
                integer2 += itemStack4.getAmount();
            }
        }
        return integer2;
    }
    
    default boolean containsAnyInInv(final Set<Item> set) {
        for (int integer2 = 0; integer2 < this.getInvSize(); ++integer2) {
            final ItemStack itemStack3 = this.getInvStack(integer2);
            if (set.contains(itemStack3.getItem()) && itemStack3.getAmount() > 0) {
                return true;
            }
        }
        return false;
    }
}
