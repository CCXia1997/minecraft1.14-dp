package net.minecraft.client.recipe.book;

import java.util.Collections;
import net.minecraft.container.SmokerContainer;
import net.minecraft.container.BlastFurnaceContainer;
import net.minecraft.container.FurnaceContainer;
import net.minecraft.container.PlayerContainer;
import net.minecraft.container.CraftingTableContainer;
import net.minecraft.container.CraftingContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.recipe.RecipeType;
import java.util.Iterator;
import com.google.common.collect.Table;
import net.minecraft.recipe.Recipe;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.recipe.RecipeManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.book.RecipeBook;

@Environment(EnvType.CLIENT)
public class ClientRecipeBook extends RecipeBook
{
    private final RecipeManager manager;
    private final Map<RecipeBookGroup, List<RecipeResultCollection>> resultsByGroup;
    private final List<RecipeResultCollection> orderedResults;
    
    public ClientRecipeBook(final RecipeManager recipeManager) {
        this.resultsByGroup = Maps.newHashMap();
        this.orderedResults = Lists.newArrayList();
        this.manager = recipeManager;
    }
    
    public void reload() {
        this.orderedResults.clear();
        this.resultsByGroup.clear();
        final Table<RecipeBookGroup, String, RecipeResultCollection> table1 = HashBasedTable.create();
        for (final Recipe<?> recipe3 : this.manager.values()) {
            if (recipe3.isIgnoredInRecipeBook()) {
                continue;
            }
            final RecipeBookGroup recipeBookGroup4 = getGroupForRecipe(recipe3);
            final String string5 = recipe3.getGroup();
            RecipeResultCollection recipeResultCollection6;
            if (string5.isEmpty()) {
                recipeResultCollection6 = this.addGroup(recipeBookGroup4);
            }
            else {
                recipeResultCollection6 = table1.get(recipeBookGroup4, string5);
                if (recipeResultCollection6 == null) {
                    recipeResultCollection6 = this.addGroup(recipeBookGroup4);
                    table1.put(recipeBookGroup4, string5, recipeResultCollection6);
                }
            }
            recipeResultCollection6.addRecipe(recipe3);
        }
    }
    
    private RecipeResultCollection addGroup(final RecipeBookGroup group) {
        final RecipeResultCollection recipeResultCollection2 = new RecipeResultCollection();
        this.orderedResults.add(recipeResultCollection2);
        this.resultsByGroup.computeIfAbsent(group, recipeBookGroup -> Lists.newArrayList()).add(recipeResultCollection2);
        if (group == RecipeBookGroup.h || group == RecipeBookGroup.g || group == RecipeBookGroup.i) {
            this.addGroupResults(RecipeBookGroup.f, recipeResultCollection2);
        }
        else if (group == RecipeBookGroup.k || group == RecipeBookGroup.l) {
            this.addGroupResults(RecipeBookGroup.j, recipeResultCollection2);
        }
        else if (group == RecipeBookGroup.n) {
            this.addGroupResults(RecipeBookGroup.m, recipeResultCollection2);
        }
        else if (group == RecipeBookGroup.o) {
            this.addGroupResults(RecipeBookGroup.o, recipeResultCollection2);
        }
        else if (group == RecipeBookGroup.p) {
            this.addGroupResults(RecipeBookGroup.p, recipeResultCollection2);
        }
        else {
            this.addGroupResults(RecipeBookGroup.a, recipeResultCollection2);
        }
        return recipeResultCollection2;
    }
    
    private void addGroupResults(final RecipeBookGroup group, final RecipeResultCollection results) {
        this.resultsByGroup.computeIfAbsent(group, recipeBookGroup -> Lists.newArrayList()).add(results);
    }
    
    private static RecipeBookGroup getGroupForRecipe(final Recipe<?> recipe) {
        final RecipeType<?> recipeType2 = recipe.getType();
        if (recipeType2 == RecipeType.SMELTING) {
            if (recipe.getOutput().getItem().isFood()) {
                return RecipeBookGroup.g;
            }
            if (recipe.getOutput().getItem() instanceof BlockItem) {
                return RecipeBookGroup.h;
            }
            return RecipeBookGroup.i;
        }
        else if (recipeType2 == RecipeType.BLASTING) {
            if (recipe.getOutput().getItem() instanceof BlockItem) {
                return RecipeBookGroup.k;
            }
            return RecipeBookGroup.l;
        }
        else {
            if (recipeType2 == RecipeType.SMOKING) {
                return RecipeBookGroup.n;
            }
            if (recipeType2 == RecipeType.f) {
                return RecipeBookGroup.o;
            }
            if (recipeType2 == RecipeType.CAMPFIRE_COOKING) {
                return RecipeBookGroup.p;
            }
            final ItemStack itemStack3 = recipe.getOutput();
            final ItemGroup itemGroup4 = itemStack3.getItem().getItemGroup();
            if (itemGroup4 == ItemGroup.BUILDING_BLOCKS) {
                return RecipeBookGroup.b;
            }
            if (itemGroup4 == ItemGroup.TOOLS || itemGroup4 == ItemGroup.COMBAT) {
                return RecipeBookGroup.d;
            }
            if (itemGroup4 == ItemGroup.REDSTONE) {
                return RecipeBookGroup.c;
            }
            return RecipeBookGroup.e;
        }
    }
    
    public static List<RecipeBookGroup> getGroupsForContainer(final CraftingContainer<?> craftingContainer) {
        if (craftingContainer instanceof CraftingTableContainer || craftingContainer instanceof PlayerContainer) {
            return Lists.<RecipeBookGroup>newArrayList(RecipeBookGroup.a, RecipeBookGroup.d, RecipeBookGroup.b, RecipeBookGroup.e, RecipeBookGroup.c);
        }
        if (craftingContainer instanceof FurnaceContainer) {
            return Lists.<RecipeBookGroup>newArrayList(RecipeBookGroup.f, RecipeBookGroup.g, RecipeBookGroup.h, RecipeBookGroup.i);
        }
        if (craftingContainer instanceof BlastFurnaceContainer) {
            return Lists.<RecipeBookGroup>newArrayList(RecipeBookGroup.j, RecipeBookGroup.k, RecipeBookGroup.l);
        }
        if (craftingContainer instanceof SmokerContainer) {
            return Lists.<RecipeBookGroup>newArrayList(RecipeBookGroup.m, RecipeBookGroup.n);
        }
        return Lists.newArrayList();
    }
    
    public List<RecipeResultCollection> getOrderedResults() {
        return this.orderedResults;
    }
    
    public List<RecipeResultCollection> getResultsForGroup(final RecipeBookGroup recipeBookGroup) {
        return this.resultsByGroup.getOrDefault(recipeBookGroup, Collections.<RecipeResultCollection>emptyList());
    }
}
