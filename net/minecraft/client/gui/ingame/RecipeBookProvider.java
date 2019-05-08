package net.minecraft.client.gui.ingame;

import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface RecipeBookProvider
{
    void refreshRecipeBook();
    
    RecipeBookGui getRecipeBookGui();
}
