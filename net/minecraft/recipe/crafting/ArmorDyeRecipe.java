package net.minecraft.recipe.crafting;

import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import java.util.List;
import net.minecraft.item.DyeItem;
import net.minecraft.item.DyeableItem;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.util.Identifier;

public class ArmorDyeRecipe extends SpecialCraftingRecipe
{
    public ArmorDyeRecipe(final Identifier identifier) {
        super(identifier);
    }
    
    @Override
    public boolean matches(final CraftingInventory inv, final World world) {
        ItemStack itemStack3 = ItemStack.EMPTY;
        final List<ItemStack> list4 = Lists.newArrayList();
        for (int integer5 = 0; integer5 < inv.getInvSize(); ++integer5) {
            final ItemStack itemStack4 = inv.getInvStack(integer5);
            if (!itemStack4.isEmpty()) {
                if (itemStack4.getItem() instanceof DyeableItem) {
                    if (!itemStack3.isEmpty()) {
                        return false;
                    }
                    itemStack3 = itemStack4;
                }
                else {
                    if (!(itemStack4.getItem() instanceof DyeItem)) {
                        return false;
                    }
                    list4.add(itemStack4);
                }
            }
        }
        return !itemStack3.isEmpty() && !list4.isEmpty();
    }
    
    @Override
    public ItemStack craft(final CraftingInventory inv) {
        final List<DyeItem> list2 = Lists.newArrayList();
        ItemStack itemStack3 = ItemStack.EMPTY;
        for (int integer4 = 0; integer4 < inv.getInvSize(); ++integer4) {
            final ItemStack itemStack4 = inv.getInvStack(integer4);
            if (!itemStack4.isEmpty()) {
                final Item item6 = itemStack4.getItem();
                if (item6 instanceof DyeableItem) {
                    if (!itemStack3.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    itemStack3 = itemStack4.copy();
                }
                else {
                    if (!(item6 instanceof DyeItem)) {
                        return ItemStack.EMPTY;
                    }
                    list2.add((DyeItem)item6);
                }
            }
        }
        if (itemStack3.isEmpty() || list2.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return DyeableItem.applyDyes(itemStack3, list2);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean fits(final int width, final int height) {
        return width * height >= 2;
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.ARMOR_DYE;
    }
}
