package net.minecraft.recipe;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.entity.player.PlayerEntity;
import javax.annotation.Nullable;

public interface RecipeUnlocker
{
    void setLastRecipe(@Nullable final Recipe<?> arg1);
    
    @Nullable
    Recipe<?> getLastRecipe();
    
    default void unlockLastRecipe(final PlayerEntity player) {
        final Recipe<?> recipe2 = this.getLastRecipe();
        if (recipe2 != null && !recipe2.isIgnoredInRecipeBook()) {
            player.unlockRecipes(Collections.<Recipe<?>>singleton(recipe2));
            this.setLastRecipe(null);
        }
    }
    
    default boolean shouldCraftRecipe(final World world, final ServerPlayerEntity player, final Recipe<?> recipe) {
        if (recipe.isIgnoredInRecipeBook() || !world.getGameRules().getBoolean("doLimitedCrafting") || player.getRecipeBook().contains(recipe)) {
            this.setLastRecipe(recipe);
            return true;
        }
        return false;
    }
}
