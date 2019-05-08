package net.minecraft.recipe.crafting;

import net.minecraft.inventory.Inventory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.DefaultedList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.util.Identifier;

public class BookCloningRecipe extends SpecialCraftingRecipe
{
    public BookCloningRecipe(final Identifier identifier) {
        super(identifier);
    }
    
    @Override
    public boolean matches(final CraftingInventory inv, final World world) {
        int integer3 = 0;
        ItemStack itemStack4 = ItemStack.EMPTY;
        for (int integer4 = 0; integer4 < inv.getInvSize(); ++integer4) {
            final ItemStack itemStack5 = inv.getInvStack(integer4);
            if (!itemStack5.isEmpty()) {
                if (itemStack5.getItem() == Items.nE) {
                    if (!itemStack4.isEmpty()) {
                        return false;
                    }
                    itemStack4 = itemStack5;
                }
                else {
                    if (itemStack5.getItem() != Items.nD) {
                        return false;
                    }
                    ++integer3;
                }
            }
        }
        return !itemStack4.isEmpty() && itemStack4.hasTag() && integer3 > 0;
    }
    
    @Override
    public ItemStack craft(final CraftingInventory inv) {
        int integer2 = 0;
        ItemStack itemStack3 = ItemStack.EMPTY;
        for (int integer3 = 0; integer3 < inv.getInvSize(); ++integer3) {
            final ItemStack itemStack4 = inv.getInvStack(integer3);
            if (!itemStack4.isEmpty()) {
                if (itemStack4.getItem() == Items.nE) {
                    if (!itemStack3.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    itemStack3 = itemStack4;
                }
                else {
                    if (itemStack4.getItem() != Items.nD) {
                        return ItemStack.EMPTY;
                    }
                    ++integer2;
                }
            }
        }
        if (itemStack3.isEmpty() || !itemStack3.hasTag() || integer2 < 1 || WrittenBookItem.getBookGeneration(itemStack3) >= 2) {
            return ItemStack.EMPTY;
        }
        final ItemStack itemStack5 = new ItemStack(Items.nE, integer2);
        final CompoundTag compoundTag5 = itemStack3.getTag().copy();
        compoundTag5.putInt("generation", WrittenBookItem.getBookGeneration(itemStack3) + 1);
        itemStack5.setTag(compoundTag5);
        return itemStack5;
    }
    
    @Override
    public DefaultedList<ItemStack> getRemainingStacks(final CraftingInventory craftingInventory) {
        final DefaultedList<ItemStack> defaultedList2 = DefaultedList.<ItemStack>create(craftingInventory.getInvSize(), ItemStack.EMPTY);
        for (int integer3 = 0; integer3 < defaultedList2.size(); ++integer3) {
            final ItemStack itemStack4 = craftingInventory.getInvStack(integer3);
            if (itemStack4.getItem().hasRecipeRemainder()) {
                defaultedList2.set(integer3, new ItemStack(itemStack4.getItem().getRecipeRemainder()));
            }
            else if (itemStack4.getItem() instanceof WrittenBookItem) {
                final ItemStack itemStack5 = itemStack4.copy();
                itemStack5.setAmount(1);
                defaultedList2.set(integer3, itemStack5);
                break;
            }
        }
        return defaultedList2;
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.BOOK_CLONING;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean fits(final int width, final int height) {
        return width >= 3 && height >= 3;
    }
}
