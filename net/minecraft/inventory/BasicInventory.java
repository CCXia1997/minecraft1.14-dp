package net.minecraft.inventory;

import net.minecraft.recipe.RecipeFinder;
import net.minecraft.entity.player.PlayerEntity;
import java.util.Iterator;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.recipe.RecipeInputProvider;

public class BasicInventory implements Inventory, RecipeInputProvider
{
    private final int size;
    private final DefaultedList<ItemStack> stackList;
    private List<InventoryListener> listeners;
    
    public BasicInventory(final int integer) {
        this.size = integer;
        this.stackList = DefaultedList.<ItemStack>create(integer, ItemStack.EMPTY);
    }
    
    public BasicInventory(final ItemStack... arr) {
        this.size = arr.length;
        this.stackList = DefaultedList.<ItemStack>create(ItemStack.EMPTY, arr);
    }
    
    public void addListener(final InventoryListener inventoryListener) {
        if (this.listeners == null) {
            this.listeners = Lists.newArrayList();
        }
        this.listeners.add(inventoryListener);
    }
    
    public void removeListener(final InventoryListener inventoryListener) {
        this.listeners.remove(inventoryListener);
    }
    
    @Override
    public ItemStack getInvStack(final int slot) {
        if (slot < 0 || slot >= this.stackList.size()) {
            return ItemStack.EMPTY;
        }
        return this.stackList.get(slot);
    }
    
    @Override
    public ItemStack takeInvStack(final int slot, final int integer2) {
        final ItemStack itemStack3 = Inventories.splitStack(this.stackList, slot, integer2);
        if (!itemStack3.isEmpty()) {
            this.markDirty();
        }
        return itemStack3;
    }
    
    public ItemStack add(final ItemStack itemStack) {
        final ItemStack itemStack2 = itemStack.copy();
        for (int integer3 = 0; integer3 < this.size; ++integer3) {
            final ItemStack itemStack3 = this.getInvStack(integer3);
            if (itemStack3.isEmpty()) {
                this.setInvStack(integer3, itemStack2);
                this.markDirty();
                return ItemStack.EMPTY;
            }
            if (ItemStack.areEqualIgnoreTags(itemStack3, itemStack2)) {
                final int integer4 = Math.min(this.getInvMaxStackAmount(), itemStack3.getMaxAmount());
                final int integer5 = Math.min(itemStack2.getAmount(), integer4 - itemStack3.getAmount());
                if (integer5 > 0) {
                    itemStack3.addAmount(integer5);
                    itemStack2.subtractAmount(integer5);
                    if (itemStack2.isEmpty()) {
                        this.markDirty();
                        return ItemStack.EMPTY;
                    }
                }
            }
        }
        if (itemStack2.getAmount() != itemStack.getAmount()) {
            this.markDirty();
        }
        return itemStack2;
    }
    
    @Override
    public ItemStack removeInvStack(final int slot) {
        final ItemStack itemStack2 = this.stackList.get(slot);
        if (itemStack2.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.stackList.set(slot, ItemStack.EMPTY);
        return itemStack2;
    }
    
    @Override
    public void setInvStack(final int slot, final ItemStack itemStack) {
        this.stackList.set(slot, itemStack);
        if (!itemStack.isEmpty() && itemStack.getAmount() > this.getInvMaxStackAmount()) {
            itemStack.setAmount(this.getInvMaxStackAmount());
        }
        this.markDirty();
    }
    
    @Override
    public int getInvSize() {
        return this.size;
    }
    
    @Override
    public boolean isInvEmpty() {
        for (final ItemStack itemStack2 : this.stackList) {
            if (!itemStack2.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void markDirty() {
        if (this.listeners != null) {
            for (final InventoryListener inventoryListener2 : this.listeners) {
                inventoryListener2.onInvChange(this);
            }
        }
    }
    
    @Override
    public boolean canPlayerUseInv(final PlayerEntity playerEntity) {
        return true;
    }
    
    @Override
    public void clear() {
        this.stackList.clear();
    }
    
    @Override
    public void provideRecipeInputs(final RecipeFinder recipeFinder) {
        for (final ItemStack itemStack3 : this.stackList) {
            recipeFinder.addItem(itemStack3);
        }
    }
}
