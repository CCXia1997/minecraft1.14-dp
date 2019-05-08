package net.minecraft.recipe.crafting;

import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeSerializer;
import java.util.Iterator;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.item.FilledMapItem;
import net.minecraft.world.World;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.item.Items;
import net.minecraft.item.ItemProvider;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

public class MapExtendingRecipe extends ShapedRecipe
{
    public MapExtendingRecipe(final Identifier identifier) {
        super(identifier, "", 3, 3, DefaultedList.<Ingredient>create(Ingredient.EMPTY, Ingredient.ofItems(Items.kR), Ingredient.ofItems(Items.kR), Ingredient.ofItems(Items.kR), Ingredient.ofItems(Items.kR), Ingredient.ofItems(Items.lV), Ingredient.ofItems(Items.kR), Ingredient.ofItems(Items.kR), Ingredient.ofItems(Items.kR), Ingredient.ofItems(Items.kR)), new ItemStack(Items.nM));
    }
    
    @Override
    public boolean matches(final CraftingInventory inv, final World world) {
        if (!super.matches(inv, world)) {
            return false;
        }
        ItemStack itemStack3 = ItemStack.EMPTY;
        for (int integer4 = 0; integer4 < inv.getInvSize() && itemStack3.isEmpty(); ++integer4) {
            final ItemStack itemStack4 = inv.getInvStack(integer4);
            if (itemStack4.getItem() == Items.lV) {
                itemStack3 = itemStack4;
            }
        }
        if (itemStack3.isEmpty()) {
            return false;
        }
        final MapState mapState4 = FilledMapItem.getOrCreateMapState(itemStack3, world);
        return mapState4 != null && !this.matches(mapState4) && mapState4.scale < 4;
    }
    
    private boolean matches(final MapState state) {
        if (state.icons != null) {
            for (final MapIcon mapIcon3 : state.icons.values()) {
                if (mapIcon3.getType() == MapIcon.Type.i || mapIcon3.getType() == MapIcon.Type.j) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public ItemStack craft(final CraftingInventory inv) {
        ItemStack itemStack2 = ItemStack.EMPTY;
        for (int integer3 = 0; integer3 < inv.getInvSize() && itemStack2.isEmpty(); ++integer3) {
            final ItemStack itemStack3 = inv.getInvStack(integer3);
            if (itemStack3.getItem() == Items.lV) {
                itemStack2 = itemStack3;
            }
        }
        itemStack2 = itemStack2.copy();
        itemStack2.setAmount(1);
        itemStack2.getOrCreateTag().putInt("map_scale_direction", 1);
        return itemStack2;
    }
    
    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.MAP_EXTENDING;
    }
}
