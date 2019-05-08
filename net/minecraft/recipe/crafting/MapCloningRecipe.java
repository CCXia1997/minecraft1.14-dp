package net.minecraft.recipe.crafting;

import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.util.Identifier;

public class MapCloningRecipe extends SpecialCraftingRecipe
{
    public MapCloningRecipe(final Identifier identifier) {
        super(identifier);
    }
    
    @Override
    public boolean matches(final CraftingInventory inv, final World world) {
        int integer3 = 0;
        ItemStack itemStack4 = ItemStack.EMPTY;
        for (int integer4 = 0; integer4 < inv.getInvSize(); ++integer4) {
            final ItemStack itemStack5 = inv.getInvStack(integer4);
            if (!itemStack5.isEmpty()) {
                if (itemStack5.getItem() == Items.lV) {
                    if (!itemStack4.isEmpty()) {
                        return false;
                    }
                    itemStack4 = itemStack5;
                }
                else {
                    if (itemStack5.getItem() != Items.nM) {
                        return false;
                    }
                    ++integer3;
                }
            }
        }
        return !itemStack4.isEmpty() && integer3 > 0;
    }
    
    @Override
    public ItemStack craft(final CraftingInventory inv) {
        int integer2 = 0;
        ItemStack itemStack3 = ItemStack.EMPTY;
        for (int integer3 = 0; integer3 < inv.getInvSize(); ++integer3) {
            final ItemStack itemStack4 = inv.getInvStack(integer3);
            if (!itemStack4.isEmpty()) {
                if (itemStack4.getItem() == Items.lV) {
                    if (!itemStack3.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    itemStack3 = itemStack4;
                }
                else {
                    if (itemStack4.getItem() != Items.nM) {
                        return ItemStack.EMPTY;
                    }
                    ++integer2;
                }
            }
        }
        if (itemStack3.isEmpty() || integer2 < 1) {
            return ItemStack.EMPTY;
        }
        final ItemStack itemStack5 = itemStack3.copy();
        itemStack5.setAmount(integer2 + 1);
        return itemStack5;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean fits(final int width, final int height) {
        return width >= 3 && height >= 3;
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.MAP_CLONING;
    }
}
