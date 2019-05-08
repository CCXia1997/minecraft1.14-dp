package net.minecraft.recipe;

import net.minecraft.util.Identifier;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemProvider;
import net.minecraft.util.DefaultedList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.inventory.Inventory;

public interface Recipe<C extends Inventory>
{
    boolean matches(final C arg1, final World arg2);
    
    ItemStack craft(final C arg1);
    
    @Environment(EnvType.CLIENT)
    boolean fits(final int arg1, final int arg2);
    
    ItemStack getOutput();
    
    default DefaultedList<ItemStack> getRemainingStacks(final C inventory) {
        final DefaultedList<ItemStack> defaultedList2 = DefaultedList.<ItemStack>create(inventory.getInvSize(), ItemStack.EMPTY);
        for (int integer3 = 0; integer3 < defaultedList2.size(); ++integer3) {
            final Item item4 = inventory.getInvStack(integer3).getItem();
            if (item4.hasRecipeRemainder()) {
                defaultedList2.set(integer3, new ItemStack(item4.getRecipeRemainder()));
            }
        }
        return defaultedList2;
    }
    
    default DefaultedList<Ingredient> getPreviewInputs() {
        return DefaultedList.<Ingredient>create();
    }
    
    default boolean isIgnoredInRecipeBook() {
        return false;
    }
    
    @Environment(EnvType.CLIENT)
    default String getGroup() {
        return "";
    }
    
    @Environment(EnvType.CLIENT)
    default ItemStack getRecipeKindIcon() {
        return new ItemStack(Blocks.bT);
    }
    
    Identifier getId();
    
    RecipeSerializer<?> getSerializer();
    
    RecipeType<?> getType();
}
