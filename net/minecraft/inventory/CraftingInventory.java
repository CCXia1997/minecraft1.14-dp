package net.minecraft.inventory;

import net.minecraft.recipe.RecipeFinder;
import net.minecraft.entity.player.PlayerEntity;
import java.util.List;
import java.util.Iterator;
import net.minecraft.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.recipe.RecipeInputProvider;

public class CraftingInventory implements Inventory, RecipeInputProvider
{
    private final DefaultedList<ItemStack> stacks;
    private final int width;
    private final int height;
    private final Container container;
    
    public CraftingInventory(final Container container, final int width, final int height) {
        this.stacks = DefaultedList.<ItemStack>create(width * height, ItemStack.EMPTY);
        this.container = container;
        this.width = width;
        this.height = height;
    }
    
    @Override
    public int getInvSize() {
        return this.stacks.size();
    }
    
    @Override
    public boolean isInvEmpty() {
        for (final ItemStack itemStack2 : this.stacks) {
            if (!itemStack2.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public ItemStack getInvStack(final int slot) {
        if (slot >= this.getInvSize()) {
            return ItemStack.EMPTY;
        }
        return this.stacks.get(slot);
    }
    
    @Override
    public ItemStack removeInvStack(final int slot) {
        return Inventories.removeStack(this.stacks, slot);
    }
    
    @Override
    public ItemStack takeInvStack(final int slot, final int integer2) {
        final ItemStack itemStack3 = Inventories.splitStack(this.stacks, slot, integer2);
        if (!itemStack3.isEmpty()) {
            this.container.onContentChanged(this);
        }
        return itemStack3;
    }
    
    @Override
    public void setInvStack(final int slot, final ItemStack itemStack) {
        this.stacks.set(slot, itemStack);
        this.container.onContentChanged(this);
    }
    
    @Override
    public void markDirty() {
    }
    
    @Override
    public boolean canPlayerUseInv(final PlayerEntity playerEntity) {
        return true;
    }
    
    @Override
    public void clear() {
        this.stacks.clear();
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    @Override
    public void provideRecipeInputs(final RecipeFinder recipeFinder) {
        for (final ItemStack itemStack3 : this.stacks) {
            recipeFinder.addNormalItem(itemStack3);
        }
    }
}
