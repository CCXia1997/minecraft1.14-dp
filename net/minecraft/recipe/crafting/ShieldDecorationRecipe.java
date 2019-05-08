package net.minecraft.recipe.crafting;

import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.item.Items;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.util.Identifier;

public class ShieldDecorationRecipe extends SpecialCraftingRecipe
{
    public ShieldDecorationRecipe(final Identifier identifier) {
        super(identifier);
    }
    
    @Override
    public boolean matches(final CraftingInventory inv, final World world) {
        ItemStack itemStack3 = ItemStack.EMPTY;
        ItemStack itemStack4 = ItemStack.EMPTY;
        for (int integer5 = 0; integer5 < inv.getInvSize(); ++integer5) {
            final ItemStack itemStack5 = inv.getInvStack(integer5);
            if (!itemStack5.isEmpty()) {
                if (itemStack5.getItem() instanceof BannerItem) {
                    if (!itemStack4.isEmpty()) {
                        return false;
                    }
                    itemStack4 = itemStack5;
                }
                else {
                    if (itemStack5.getItem() != Items.oW) {
                        return false;
                    }
                    if (!itemStack3.isEmpty()) {
                        return false;
                    }
                    if (itemStack5.getSubCompoundTag("BlockEntityTag") != null) {
                        return false;
                    }
                    itemStack3 = itemStack5;
                }
            }
        }
        return !itemStack3.isEmpty() && !itemStack4.isEmpty();
    }
    
    @Override
    public ItemStack craft(final CraftingInventory inv) {
        ItemStack itemStack2 = ItemStack.EMPTY;
        ItemStack itemStack3 = ItemStack.EMPTY;
        for (int integer4 = 0; integer4 < inv.getInvSize(); ++integer4) {
            final ItemStack itemStack4 = inv.getInvStack(integer4);
            if (!itemStack4.isEmpty()) {
                if (itemStack4.getItem() instanceof BannerItem) {
                    itemStack2 = itemStack4;
                }
                else if (itemStack4.getItem() == Items.oW) {
                    itemStack3 = itemStack4.copy();
                }
            }
        }
        if (itemStack3.isEmpty()) {
            return itemStack3;
        }
        final CompoundTag compoundTag4 = itemStack2.getSubCompoundTag("BlockEntityTag");
        final CompoundTag compoundTag5 = (compoundTag4 == null) ? new CompoundTag() : compoundTag4.copy();
        compoundTag5.putInt("Base", ((BannerItem)itemStack2.getItem()).getColor().getId());
        itemStack3.setChildTag("BlockEntityTag", compoundTag5);
        return itemStack3;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean fits(final int width, final int height) {
        return width * height >= 2;
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SHIELD_DECORATION;
    }
}
