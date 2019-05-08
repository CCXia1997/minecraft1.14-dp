package net.minecraft.container;

import net.minecraft.util.DefaultedList;
import net.minecraft.recipe.crafting.CraftingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;

public class CraftingResultSlot extends Slot
{
    private final CraftingInventory craftingInv;
    private final PlayerEntity player;
    private int amount;
    
    public CraftingResultSlot(final PlayerEntity player, final CraftingInventory craftingInv, final Inventory inventory, final int invSlot, final int xPosition, final int yPosition) {
        super(inventory, invSlot, xPosition, yPosition);
        this.player = player;
        this.craftingInv = craftingInv;
    }
    
    @Override
    public boolean canInsert(final ItemStack stack) {
        return false;
    }
    
    @Override
    public ItemStack takeStack(final int amount) {
        if (this.hasStack()) {
            this.amount += Math.min(amount, this.getStack().getAmount());
        }
        return super.takeStack(amount);
    }
    
    @Override
    protected void onCrafted(final ItemStack stack, final int amount) {
        this.amount += amount;
        this.onCrafted(stack);
    }
    
    @Override
    protected void onTake(final int amount) {
        this.amount += amount;
    }
    
    @Override
    protected void onCrafted(final ItemStack stack) {
        if (this.amount > 0) {
            stack.onCrafted(this.player.world, this.player, this.amount);
        }
        if (this.inventory instanceof RecipeUnlocker) {
            ((RecipeUnlocker)this.inventory).unlockLastRecipe(this.player);
        }
        this.amount = 0;
    }
    
    @Override
    public ItemStack onTakeItem(final PlayerEntity player, final ItemStack stack) {
        this.onCrafted(stack);
        final DefaultedList<ItemStack> defaultedList3 = player.world.getRecipeManager().<CraftingInventory, CraftingRecipe>getRemainingStacks(RecipeType.CRAFTING, this.craftingInv, player.world);
        for (int integer4 = 0; integer4 < defaultedList3.size(); ++integer4) {
            ItemStack itemStack5 = this.craftingInv.getInvStack(integer4);
            final ItemStack itemStack6 = defaultedList3.get(integer4);
            if (!itemStack5.isEmpty()) {
                this.craftingInv.takeInvStack(integer4, 1);
                itemStack5 = this.craftingInv.getInvStack(integer4);
            }
            if (!itemStack6.isEmpty()) {
                if (itemStack5.isEmpty()) {
                    this.craftingInv.setInvStack(integer4, itemStack6);
                }
                else if (ItemStack.areEqualIgnoreTags(itemStack5, itemStack6) && ItemStack.areTagsEqual(itemStack5, itemStack6)) {
                    itemStack6.addAmount(itemStack5.getAmount());
                    this.craftingInv.setInvStack(integer4, itemStack6);
                }
                else if (!this.player.inventory.insertStack(itemStack6)) {
                    this.player.dropItem(itemStack6, false);
                }
            }
        }
        return stack;
    }
}
