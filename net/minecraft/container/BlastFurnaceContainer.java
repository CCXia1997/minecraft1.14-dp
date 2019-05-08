package net.minecraft.container;

import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.cooking.CookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.entity.player.PlayerInventory;

public class BlastFurnaceContainer extends AbstractFurnaceContainer
{
    public BlastFurnaceContainer(final int syncId, final PlayerInventory playerInventory) {
        super(ContainerType.BLAST_FURNACE, RecipeType.BLASTING, syncId, playerInventory);
    }
    
    public BlastFurnaceContainer(final int syncId, final PlayerInventory playerInventory, final Inventory inventory, final PropertyDelegate propertyDelegate) {
        super(ContainerType.BLAST_FURNACE, RecipeType.BLASTING, syncId, playerInventory, inventory, propertyDelegate);
    }
}
