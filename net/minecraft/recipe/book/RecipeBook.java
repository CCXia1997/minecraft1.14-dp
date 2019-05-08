package net.minecraft.recipe.book;

import net.minecraft.container.SmokerContainer;
import net.minecraft.container.BlastFurnaceContainer;
import net.minecraft.container.FurnaceContainer;
import net.minecraft.container.CraftingContainer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import javax.annotation.Nullable;
import net.minecraft.recipe.Recipe;
import java.util.Collection;
import com.google.common.collect.Sets;
import net.minecraft.util.Identifier;
import java.util.Set;

public class RecipeBook
{
    protected final Set<Identifier> recipes;
    protected final Set<Identifier> toBeDisplayed;
    protected boolean guiOpen;
    protected boolean filteringCraftable;
    protected boolean furnaceGuiOpen;
    protected boolean furnaceFilteringCraftable;
    protected boolean blastFurnaceGuiOpen;
    protected boolean blastFurnaceFilteringCraftable;
    protected boolean smokerGuiOpen;
    protected boolean smokerFilteringCraftable;
    
    public RecipeBook() {
        this.recipes = Sets.newHashSet();
        this.toBeDisplayed = Sets.newHashSet();
    }
    
    public void copyFrom(final RecipeBook recipeBook) {
        this.recipes.clear();
        this.toBeDisplayed.clear();
        this.recipes.addAll(recipeBook.recipes);
        this.toBeDisplayed.addAll(recipeBook.toBeDisplayed);
    }
    
    public void add(final Recipe<?> recipe) {
        if (!recipe.isIgnoredInRecipeBook()) {
            this.add(recipe.getId());
        }
    }
    
    protected void add(final Identifier identifier) {
        this.recipes.add(identifier);
    }
    
    public boolean contains(@Nullable final Recipe<?> recipe) {
        return recipe != null && this.recipes.contains(recipe.getId());
    }
    
    @Environment(EnvType.CLIENT)
    public void remove(final Recipe<?> recipe) {
        this.remove(recipe.getId());
    }
    
    protected void remove(final Identifier identifier) {
        this.recipes.remove(identifier);
        this.toBeDisplayed.remove(identifier);
    }
    
    @Environment(EnvType.CLIENT)
    public boolean shouldDisplay(final Recipe<?> recipe) {
        return this.toBeDisplayed.contains(recipe.getId());
    }
    
    public void onRecipeDisplayed(final Recipe<?> recipe) {
        this.toBeDisplayed.remove(recipe.getId());
    }
    
    public void display(final Recipe<?> recipe) {
        this.display(recipe.getId());
    }
    
    protected void display(final Identifier identifier) {
        this.toBeDisplayed.add(identifier);
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isGuiOpen() {
        return this.guiOpen;
    }
    
    public void setGuiOpen(final boolean boolean1) {
        this.guiOpen = boolean1;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isFilteringCraftable(final CraftingContainer<?> craftingContainer) {
        if (craftingContainer instanceof FurnaceContainer) {
            return this.furnaceFilteringCraftable;
        }
        if (craftingContainer instanceof BlastFurnaceContainer) {
            return this.blastFurnaceFilteringCraftable;
        }
        if (craftingContainer instanceof SmokerContainer) {
            return this.smokerFilteringCraftable;
        }
        return this.filteringCraftable;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isFilteringCraftable() {
        return this.filteringCraftable;
    }
    
    public void setFilteringCraftable(final boolean boolean1) {
        this.filteringCraftable = boolean1;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isFurnaceGuiOpen() {
        return this.furnaceGuiOpen;
    }
    
    public void setFurnaceGuiOpen(final boolean boolean1) {
        this.furnaceGuiOpen = boolean1;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isFurnaceFilteringCraftable() {
        return this.furnaceFilteringCraftable;
    }
    
    public void setFurnaceFilteringCraftable(final boolean boolean1) {
        this.furnaceFilteringCraftable = boolean1;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isBlastFurnaceGuiOpen() {
        return this.blastFurnaceGuiOpen;
    }
    
    public void setBlastFurnaceGuiOpen(final boolean boolean1) {
        this.blastFurnaceGuiOpen = boolean1;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isBlastFurnaceFilteringCraftable() {
        return this.blastFurnaceFilteringCraftable;
    }
    
    public void setBlastFurnaceFilteringCraftable(final boolean boolean1) {
        this.blastFurnaceFilteringCraftable = boolean1;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isSmokerGuiOpen() {
        return this.smokerGuiOpen;
    }
    
    public void setSmokerGuiOpen(final boolean boolean1) {
        this.smokerGuiOpen = boolean1;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isSmokerFilteringCraftable() {
        return this.smokerFilteringCraftable;
    }
    
    public void setSmokerFilteringCraftable(final boolean boolean1) {
        this.smokerFilteringCraftable = boolean1;
    }
}
