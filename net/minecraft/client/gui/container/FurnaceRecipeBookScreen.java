package net.minecraft.client.gui.container;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.Item;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FurnaceRecipeBookScreen extends AbstractFurnaceRecipeBookScreen
{
    @Override
    protected boolean isFilteringCraftable() {
        return this.recipeBook.isFurnaceFilteringCraftable();
    }
    
    @Override
    protected void setFilteringCraftable(final boolean boolean1) {
        this.recipeBook.setFurnaceFilteringCraftable(boolean1);
    }
    
    @Override
    protected boolean isGuiOpen() {
        return this.recipeBook.isFurnaceGuiOpen();
    }
    
    @Override
    protected void setGuiOpen(final boolean boolean1) {
        this.recipeBook.setFurnaceGuiOpen(boolean1);
    }
    
    @Override
    protected String getToggleCraftableButtonText() {
        return "gui.recipebook.toggleRecipes.smeltable";
    }
    
    @Override
    protected Set<Item> getAllowedFuels() {
        return AbstractFurnaceBlockEntity.createFuelTimeMap().keySet();
    }
}
