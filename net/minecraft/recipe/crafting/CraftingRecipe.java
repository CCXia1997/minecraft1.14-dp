package net.minecraft.recipe.crafting;

import net.minecraft.recipe.RecipeType;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.recipe.Recipe;

public interface CraftingRecipe extends Recipe<CraftingInventory>
{
    default RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }
}
