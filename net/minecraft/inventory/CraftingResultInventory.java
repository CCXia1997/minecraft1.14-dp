package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import java.util.List;
import java.util.Iterator;
import net.minecraft.recipe.Recipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.recipe.RecipeUnlocker;

public class CraftingResultInventory implements Inventory, RecipeUnlocker
{
    private final DefaultedList<ItemStack> stack;
    private Recipe<?> lastRecipe;
    
    public CraftingResultInventory() {
        this.stack = DefaultedList.<ItemStack>create(1, ItemStack.EMPTY);
    }
    
    @Override
    public int getInvSize() {
        return 1;
    }
    
    @Override
    public boolean isInvEmpty() {
        for (final ItemStack itemStack2 : this.stack) {
            if (!itemStack2.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public ItemStack getInvStack(final int slot) {
        return this.stack.get(0);
    }
    
    @Override
    public ItemStack takeInvStack(final int slot, final int integer2) {
        return Inventories.removeStack(this.stack, 0);
    }
    
    @Override
    public ItemStack removeInvStack(final int slot) {
        return Inventories.removeStack(this.stack, 0);
    }
    
    @Override
    public void setInvStack(final int slot, final ItemStack itemStack) {
        this.stack.set(0, itemStack);
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
        this.stack.clear();
    }
    
    @Override
    public void setLastRecipe(@Nullable final Recipe<?> recipe) {
        this.lastRecipe = recipe;
    }
    
    @Nullable
    @Override
    public Recipe<?> getLastRecipe() {
        return this.lastRecipe;
    }
}
