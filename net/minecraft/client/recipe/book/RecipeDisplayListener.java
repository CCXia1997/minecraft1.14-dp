package net.minecraft.client.recipe.book;

import net.minecraft.recipe.Recipe;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface RecipeDisplayListener
{
    void onRecipesDisplayed(final List<Recipe<?>> arg1);
}
