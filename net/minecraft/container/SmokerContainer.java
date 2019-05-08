package net.minecraft.container;

import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.cooking.CookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.entity.player.PlayerInventory;

public class SmokerContainer extends AbstractFurnaceContainer
{
    public SmokerContainer(final int syncId, final PlayerInventory playerInventory) {
        super(ContainerType.SMOKER, RecipeType.SMOKING, syncId, playerInventory);
    }
    
    public SmokerContainer(final int syncId, final PlayerInventory playerInventory, final Inventory inventory, final PropertyDelegate propertyDelegate) {
        super(ContainerType.SMOKER, RecipeType.SMOKING, syncId, playerInventory, inventory, propertyDelegate);
    }
}
