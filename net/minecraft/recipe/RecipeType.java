package net.minecraft.recipe;

import java.util.Optional;
import net.minecraft.inventory.Inventory;
import net.minecraft.world.World;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.recipe.cooking.CampfireCookingRecipe;
import net.minecraft.recipe.cooking.SmokingRecipe;
import net.minecraft.recipe.cooking.BlastingRecipe;
import net.minecraft.recipe.cooking.SmeltingRecipe;
import net.minecraft.recipe.crafting.CraftingRecipe;

public interface RecipeType<T extends Recipe<?>>
{
    public static final RecipeType<CraftingRecipe> CRAFTING = RecipeType.<CraftingRecipe>register("crafting");
    public static final RecipeType<SmeltingRecipe> SMELTING = RecipeType.<SmeltingRecipe>register("smelting");
    public static final RecipeType<BlastingRecipe> BLASTING = RecipeType.<BlastingRecipe>register("blasting");
    public static final RecipeType<SmokingRecipe> SMOKING = RecipeType.<SmokingRecipe>register("smoking");
    public static final RecipeType<CampfireCookingRecipe> CAMPFIRE_COOKING = RecipeType.<CampfireCookingRecipe>register("campfire_cooking");
    public static final RecipeType<StonecuttingRecipe> f = RecipeType.<StonecuttingRecipe>register("stonecutting");
    
    default <T extends Recipe<?>> RecipeType<T> register(final String string) {
        return Registry.register(Registry.RECIPE_TYPE, new Identifier(string), new RecipeType<T>() {
            @Override
            public String toString() {
                return string;
            }
        });
    }
    
    default <C extends Inventory> Optional<T> get(final Recipe<C> recipe, final World world, final C inventory) {
        return (Optional<T>)(recipe.matches(inventory, world) ? Optional.<Recipe<C>>of(recipe) : Optional.<Recipe<C>>empty());
    }
}
