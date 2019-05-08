package net.minecraft.recipe.crafting;

import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.DyeItem;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.world.World;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.util.Identifier;

public class ShulkerBoxColoringRecipe extends SpecialCraftingRecipe
{
    public ShulkerBoxColoringRecipe(final Identifier identifier) {
        super(identifier);
    }
    
    @Override
    public boolean matches(final CraftingInventory inv, final World world) {
        int integer3 = 0;
        int integer4 = 0;
        for (int integer5 = 0; integer5 < inv.getInvSize(); ++integer5) {
            final ItemStack itemStack6 = inv.getInvStack(integer5);
            if (!itemStack6.isEmpty()) {
                if (Block.getBlockFromItem(itemStack6.getItem()) instanceof ShulkerBoxBlock) {
                    ++integer3;
                }
                else {
                    if (!(itemStack6.getItem() instanceof DyeItem)) {
                        return false;
                    }
                    ++integer4;
                }
                if (integer4 > 1 || integer3 > 1) {
                    return false;
                }
            }
        }
        return integer3 == 1 && integer4 == 1;
    }
    
    @Override
    public ItemStack craft(final CraftingInventory inv) {
        ItemStack itemStack2 = ItemStack.EMPTY;
        DyeItem dyeItem3 = (DyeItem)Items.lA;
        for (int integer4 = 0; integer4 < inv.getInvSize(); ++integer4) {
            final ItemStack itemStack3 = inv.getInvStack(integer4);
            if (!itemStack3.isEmpty()) {
                final Item item6 = itemStack3.getItem();
                if (Block.getBlockFromItem(item6) instanceof ShulkerBoxBlock) {
                    itemStack2 = itemStack3;
                }
                else if (item6 instanceof DyeItem) {
                    dyeItem3 = (DyeItem)item6;
                }
            }
        }
        final ItemStack itemStack4 = ShulkerBoxBlock.getItemStack(dyeItem3.getColor());
        if (itemStack2.hasTag()) {
            itemStack4.setTag(itemStack2.getTag().copy());
        }
        return itemStack4;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean fits(final int width, final int height) {
        return width * height >= 2;
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SHULKER_BOX;
    }
}
