package net.minecraft.client.gui.container;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.Item;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SmokerRecipeBookScreen extends AbstractFurnaceRecipeBookScreen
{
    @Override
    protected boolean isFilteringCraftable() {
        return this.recipeBook.isSmokerFilteringCraftable();
    }
    
    @Override
    protected void setFilteringCraftable(final boolean boolean1) {
        this.recipeBook.setSmokerFilteringCraftable(boolean1);
    }
    
    @Override
    protected boolean isGuiOpen() {
        return this.recipeBook.isSmokerGuiOpen();
    }
    
    @Override
    protected void setGuiOpen(final boolean boolean1) {
        this.recipeBook.setSmokerGuiOpen(boolean1);
    }
    
    @Override
    protected String getToggleCraftableButtonText() {
        return "gui.recipebook.toggleRecipes.smokable";
    }
    
    @Override
    protected Set<Item> getAllowedFuels() {
        return AbstractFurnaceBlockEntity.createFuelTimeMap().keySet();
    }
}
