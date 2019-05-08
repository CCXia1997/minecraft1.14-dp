package net.minecraft.client.gui.container;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.Item;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BlastFurnaceRecipeBookScreen extends AbstractFurnaceRecipeBookScreen
{
    @Override
    protected boolean isFilteringCraftable() {
        return this.recipeBook.isBlastFurnaceFilteringCraftable();
    }
    
    @Override
    protected void setFilteringCraftable(final boolean boolean1) {
        this.recipeBook.setBlastFurnaceFilteringCraftable(boolean1);
    }
    
    @Override
    protected boolean isGuiOpen() {
        return this.recipeBook.isBlastFurnaceGuiOpen();
    }
    
    @Override
    protected void setGuiOpen(final boolean boolean1) {
        this.recipeBook.setBlastFurnaceGuiOpen(boolean1);
    }
    
    @Override
    protected String getToggleCraftableButtonText() {
        return "gui.recipebook.toggleRecipes.blastable";
    }
    
    @Override
    protected Set<Item> getAllowedFuels() {
        return AbstractFurnaceBlockEntity.createFuelTimeMap().keySet();
    }
}
