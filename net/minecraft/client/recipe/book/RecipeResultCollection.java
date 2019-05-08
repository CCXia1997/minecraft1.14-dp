package net.minecraft.client.recipe.book;

import net.minecraft.item.ItemStack;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.recipe.RecipeFinder;
import java.util.Iterator;
import net.minecraft.recipe.book.RecipeBook;
import com.google.common.collect.Sets;
import com.google.common.collect.Lists;
import java.util.Set;
import net.minecraft.recipe.Recipe;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RecipeResultCollection
{
    private final List<Recipe<?>> allRecipes;
    private final Set<Recipe<?>> craftableResults;
    private final Set<Recipe<?>> fittableResults;
    private final Set<Recipe<?>> allResults;
    private boolean e;
    
    public RecipeResultCollection() {
        this.allRecipes = Lists.newArrayList();
        this.craftableResults = Sets.newHashSet();
        this.fittableResults = Sets.newHashSet();
        this.allResults = Sets.newHashSet();
        this.e = true;
    }
    
    public boolean isInitialized() {
        return !this.allResults.isEmpty();
    }
    
    public void initialize(final RecipeBook recipeBook) {
        for (final Recipe<?> recipe3 : this.allRecipes) {
            if (recipeBook.contains(recipe3)) {
                this.allResults.add(recipe3);
            }
        }
    }
    
    public void computeCraftables(final RecipeFinder recipeFinder, final int gridWidth, final int gridHeight, final RecipeBook recipeBook) {
        for (int integer5 = 0; integer5 < this.allRecipes.size(); ++integer5) {
            final Recipe<?> recipe6 = this.allRecipes.get(integer5);
            final boolean boolean7 = recipe6.fits(gridWidth, gridHeight) && recipeBook.contains(recipe6);
            if (boolean7) {
                this.fittableResults.add(recipe6);
            }
            else {
                this.fittableResults.remove(recipe6);
            }
            if (boolean7 && recipeFinder.findRecipe(recipe6, null)) {
                this.craftableResults.add(recipe6);
            }
            else {
                this.craftableResults.remove(recipe6);
            }
        }
    }
    
    public boolean isCraftable(final Recipe<?> recipe) {
        return this.craftableResults.contains(recipe);
    }
    
    public boolean hasCraftableResults() {
        return !this.craftableResults.isEmpty();
    }
    
    public boolean hasFittableResults() {
        return !this.fittableResults.isEmpty();
    }
    
    public List<Recipe<?>> getAllRecipes() {
        return this.allRecipes;
    }
    
    public List<Recipe<?>> getResults(final boolean craftableOnly) {
        final List<Recipe<?>> list2 = Lists.newArrayList();
        final Set<Recipe<?>> set3 = craftableOnly ? this.craftableResults : this.fittableResults;
        for (final Recipe<?> recipe5 : this.allRecipes) {
            if (set3.contains(recipe5)) {
                list2.add(recipe5);
            }
        }
        return list2;
    }
    
    public List<Recipe<?>> getResultsExclusive(final boolean isCraftable) {
        final List<Recipe<?>> list2 = Lists.newArrayList();
        for (final Recipe<?> recipe4 : this.allRecipes) {
            if (this.fittableResults.contains(recipe4) && this.craftableResults.contains(recipe4) == isCraftable) {
                list2.add(recipe4);
            }
        }
        return list2;
    }
    
    public void addRecipe(final Recipe<?> recipe) {
        this.allRecipes.add(recipe);
        if (this.e) {
            final ItemStack itemStack2 = this.allRecipes.get(0).getOutput();
            final ItemStack itemStack3 = recipe.getOutput();
            this.e = (ItemStack.areEqualIgnoreTags(itemStack2, itemStack3) && ItemStack.areTagsEqual(itemStack2, itemStack3));
        }
    }
    
    public boolean e() {
        return this.e;
    }
}
