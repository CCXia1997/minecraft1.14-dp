package net.minecraft.container;

import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.cooking.CookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.entity.player.PlayerInventory;

public class FurnaceContainer extends AbstractFurnaceContainer
{
    public FurnaceContainer(final int syncId, final PlayerInventory playerInventory) {
        super(ContainerType.FURNACE, RecipeType.SMELTING, syncId, playerInventory);
    }
    
    public FurnaceContainer(final int syncId, final PlayerInventory playerInventory, final Inventory inventory, final PropertyDelegate propertyDelegate) {
        super(ContainerType.FURNACE, RecipeType.SMELTING, syncId, playerInventory, inventory, propertyDelegate);
    }
}
