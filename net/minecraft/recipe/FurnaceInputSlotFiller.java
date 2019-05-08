package net.minecraft.recipe;

import net.minecraft.container.Slot;
import java.util.Iterator;
import net.minecraft.item.ItemStack;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.container.CraftingContainer;
import net.minecraft.inventory.Inventory;

public class FurnaceInputSlotFiller<C extends Inventory> extends InputSlotFiller<C>
{
    private boolean slotMatchesRecipe;
    
    public FurnaceInputSlotFiller(final CraftingContainer<C> craftingContainer) {
        super(craftingContainer);
    }
    
    @Override
    protected void fillInputSlots(final Recipe<C> recipe, final boolean craftAll) {
        this.slotMatchesRecipe = this.craftingContainer.matches(recipe);
        final int integer3 = this.recipeFinder.countRecipeCrafts(recipe, null);
        if (this.slotMatchesRecipe) {
            final ItemStack itemStack4 = this.craftingContainer.getSlot(0).getStack();
            if (itemStack4.isEmpty() || integer3 <= itemStack4.getAmount()) {
                return;
            }
        }
        final int integer4 = this.getAmountToFill(craftAll, integer3, this.slotMatchesRecipe);
        final IntList intList5 = (IntList)new IntArrayList();
        if (!this.recipeFinder.findRecipe(recipe, intList5, integer4)) {
            return;
        }
        if (!this.slotMatchesRecipe) {
            this.returnSlot(this.craftingContainer.getCraftingResultSlotIndex());
            this.returnSlot(0);
        }
        this.fillInputSlot(integer4, intList5);
    }
    
    @Override
    protected void returnInputs() {
        this.returnSlot(this.craftingContainer.getCraftingResultSlotIndex());
        super.returnInputs();
    }
    
    protected void fillInputSlot(final int limit, final IntList inputs) {
        final Iterator<Integer> iterator3 = (Iterator<Integer>)inputs.iterator();
        final Slot slot4 = this.craftingContainer.getSlot(0);
        final ItemStack itemStack5 = RecipeFinder.getStackFromId(iterator3.next());
        if (itemStack5.isEmpty()) {
            return;
        }
        int integer6 = Math.min(itemStack5.getMaxAmount(), limit);
        if (this.slotMatchesRecipe) {
            integer6 -= slot4.getStack().getAmount();
        }
        for (int integer7 = 0; integer7 < integer6; ++integer7) {
            this.fillInputSlot(slot4, itemStack5);
        }
    }
}
