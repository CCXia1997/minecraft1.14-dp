package net.minecraft.recipe.crafting;

import net.minecraft.item.Items;
import net.minecraft.item.ItemProvider;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import java.util.List;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.item.DyeItem;
import net.minecraft.world.World;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.util.Identifier;
import net.minecraft.recipe.Ingredient;

public class FireworkStarFadeRecipe extends SpecialCraftingRecipe
{
    private static final Ingredient a;
    
    public FireworkStarFadeRecipe(final Identifier identifier) {
        super(identifier);
    }
    
    @Override
    public boolean matches(final CraftingInventory inv, final World world) {
        boolean boolean3 = false;
        boolean boolean4 = false;
        for (int integer5 = 0; integer5 < inv.getInvSize(); ++integer5) {
            final ItemStack itemStack6 = inv.getInvStack(integer5);
            if (!itemStack6.isEmpty()) {
                if (itemStack6.getItem() instanceof DyeItem) {
                    boolean3 = true;
                }
                else {
                    if (!FireworkStarFadeRecipe.a.a(itemStack6)) {
                        return false;
                    }
                    if (boolean4) {
                        return false;
                    }
                    boolean4 = true;
                }
            }
        }
        return boolean4 && boolean3;
    }
    
    @Override
    public ItemStack craft(final CraftingInventory inv) {
        final List<Integer> list2 = Lists.newArrayList();
        ItemStack itemStack3 = null;
        for (int integer4 = 0; integer4 < inv.getInvSize(); ++integer4) {
            final ItemStack itemStack4 = inv.getInvStack(integer4);
            final Item item6 = itemStack4.getItem();
            if (item6 instanceof DyeItem) {
                list2.add(((DyeItem)item6).getColor().getFireworkColor());
            }
            else if (FireworkStarFadeRecipe.a.a(itemStack4)) {
                itemStack3 = itemStack4.copy();
                itemStack3.setAmount(1);
            }
        }
        if (itemStack3 == null || list2.isEmpty()) {
            return ItemStack.EMPTY;
        }
        itemStack3.getOrCreateSubCompoundTag("Explosion").putIntArray("FadeColors", list2);
        return itemStack3;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean fits(final int width, final int height) {
        return width * height >= 2;
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.FIREWORK_STAR_FADE;
    }
    
    static {
        a = Ingredient.ofItems(Items.nY);
    }
}
