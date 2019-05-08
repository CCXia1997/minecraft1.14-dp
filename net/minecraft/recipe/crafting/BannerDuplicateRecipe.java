package net.minecraft.recipe.crafting;

import net.minecraft.inventory.Inventory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.item.ItemProvider;
import net.minecraft.util.DefaultedList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.item.BannerItem;
import net.minecraft.world.World;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.util.Identifier;

public class BannerDuplicateRecipe extends SpecialCraftingRecipe
{
    public BannerDuplicateRecipe(final Identifier identifier) {
        super(identifier);
    }
    
    @Override
    public boolean matches(final CraftingInventory inv, final World world) {
        DyeColor dyeColor3 = null;
        ItemStack itemStack4 = null;
        ItemStack itemStack5 = null;
        for (int integer6 = 0; integer6 < inv.getInvSize(); ++integer6) {
            final ItemStack itemStack6 = inv.getInvStack(integer6);
            final Item item8 = itemStack6.getItem();
            if (item8 instanceof BannerItem) {
                final BannerItem bannerItem9 = (BannerItem)item8;
                if (dyeColor3 == null) {
                    dyeColor3 = bannerItem9.getColor();
                }
                else if (dyeColor3 != bannerItem9.getColor()) {
                    return false;
                }
                final int integer7 = BannerBlockEntity.getPatternCount(itemStack6);
                if (integer7 > 6) {
                    return false;
                }
                if (integer7 > 0) {
                    if (itemStack4 != null) {
                        return false;
                    }
                    itemStack4 = itemStack6;
                }
                else {
                    if (itemStack5 != null) {
                        return false;
                    }
                    itemStack5 = itemStack6;
                }
            }
        }
        return itemStack4 != null && itemStack5 != null;
    }
    
    @Override
    public ItemStack craft(final CraftingInventory inv) {
        for (int integer2 = 0; integer2 < inv.getInvSize(); ++integer2) {
            final ItemStack itemStack3 = inv.getInvStack(integer2);
            if (!itemStack3.isEmpty()) {
                final int integer3 = BannerBlockEntity.getPatternCount(itemStack3);
                if (integer3 > 0 && integer3 <= 6) {
                    final ItemStack itemStack4 = itemStack3.copy();
                    itemStack4.setAmount(1);
                    return itemStack4;
                }
            }
        }
        return ItemStack.EMPTY;
    }
    
    @Override
    public DefaultedList<ItemStack> getRemainingStacks(final CraftingInventory craftingInventory) {
        final DefaultedList<ItemStack> defaultedList2 = DefaultedList.<ItemStack>create(craftingInventory.getInvSize(), ItemStack.EMPTY);
        for (int integer3 = 0; integer3 < defaultedList2.size(); ++integer3) {
            final ItemStack itemStack4 = craftingInventory.getInvStack(integer3);
            if (!itemStack4.isEmpty()) {
                if (itemStack4.getItem().hasRecipeRemainder()) {
                    defaultedList2.set(integer3, new ItemStack(itemStack4.getItem().getRecipeRemainder()));
                }
                else if (itemStack4.hasTag() && BannerBlockEntity.getPatternCount(itemStack4) > 0) {
                    final ItemStack itemStack5 = itemStack4.copy();
                    itemStack5.setAmount(1);
                    defaultedList2.set(integer3, itemStack5);
                }
            }
        }
        return defaultedList2;
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.BANNER_DUPLICATE;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean fits(final int width, final int height) {
        return width * height >= 2;
    }
}
