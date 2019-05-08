package net.minecraft.recipe.crafting;

import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.effect.StatusEffectInstance;
import java.util.Collection;
import net.minecraft.potion.PotionUtil;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.util.Identifier;

public class TippedArrowRecipe extends SpecialCraftingRecipe
{
    public TippedArrowRecipe(final Identifier identifier) {
        super(identifier);
    }
    
    @Override
    public boolean matches(final CraftingInventory inv, final World world) {
        if (inv.getWidth() != 3 || inv.getHeight() != 3) {
            return false;
        }
        for (int integer3 = 0; integer3 < inv.getWidth(); ++integer3) {
            for (int integer4 = 0; integer4 < inv.getHeight(); ++integer4) {
                final ItemStack itemStack5 = inv.getInvStack(integer3 + integer4 * inv.getWidth());
                if (itemStack5.isEmpty()) {
                    return false;
                }
                final Item item6 = itemStack5.getItem();
                if (integer3 == 1 && integer4 == 1) {
                    if (item6 != Items.oV) {
                        return false;
                    }
                }
                else if (item6 != Items.jg) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public ItemStack craft(final CraftingInventory inv) {
        final ItemStack itemStack2 = inv.getInvStack(1 + inv.getWidth());
        if (itemStack2.getItem() != Items.oV) {
            return ItemStack.EMPTY;
        }
        final ItemStack itemStack3 = new ItemStack(Items.oU, 8);
        PotionUtil.setPotion(itemStack3, PotionUtil.getPotion(itemStack2));
        PotionUtil.setCustomPotionEffects(itemStack3, PotionUtil.getCustomPotionEffects(itemStack2));
        return itemStack3;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean fits(final int width, final int height) {
        return width >= 2 && height >= 2;
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.TIPPED_ARROW;
    }
}
