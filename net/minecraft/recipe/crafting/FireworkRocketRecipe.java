package net.minecraft.recipe.crafting;

import java.util.AbstractList;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.util.Identifier;
import net.minecraft.recipe.Ingredient;

public class FireworkRocketRecipe extends SpecialCraftingRecipe
{
    private static final Ingredient a;
    private static final Ingredient b;
    private static final Ingredient c;
    
    public FireworkRocketRecipe(final Identifier identifier) {
        super(identifier);
    }
    
    @Override
    public boolean matches(final CraftingInventory inv, final World world) {
        boolean boolean3 = false;
        int integer4 = 0;
        for (int integer5 = 0; integer5 < inv.getInvSize(); ++integer5) {
            final ItemStack itemStack6 = inv.getInvStack(integer5);
            if (!itemStack6.isEmpty()) {
                if (FireworkRocketRecipe.a.a(itemStack6)) {
                    if (boolean3) {
                        return false;
                    }
                    boolean3 = true;
                }
                else if (FireworkRocketRecipe.b.a(itemStack6)) {
                    if (++integer4 > 3) {
                        return false;
                    }
                }
                else if (!FireworkRocketRecipe.c.a(itemStack6)) {
                    return false;
                }
            }
        }
        return boolean3 && integer4 >= 1;
    }
    
    @Override
    public ItemStack craft(final CraftingInventory inv) {
        final ItemStack itemStack2 = new ItemStack(Items.nX, 3);
        final CompoundTag compoundTag3 = itemStack2.getOrCreateSubCompoundTag("Fireworks");
        final ListTag listTag4 = new ListTag();
        int integer5 = 0;
        for (int integer6 = 0; integer6 < inv.getInvSize(); ++integer6) {
            final ItemStack itemStack3 = inv.getInvStack(integer6);
            if (!itemStack3.isEmpty()) {
                if (FireworkRocketRecipe.b.a(itemStack3)) {
                    ++integer5;
                }
                else if (FireworkRocketRecipe.c.a(itemStack3)) {
                    final CompoundTag compoundTag4 = itemStack3.getSubCompoundTag("Explosion");
                    if (compoundTag4 != null) {
                        ((AbstractList<CompoundTag>)listTag4).add(compoundTag4);
                    }
                }
            }
        }
        compoundTag3.putByte("Flight", (byte)integer5);
        if (!listTag4.isEmpty()) {
            compoundTag3.put("Explosions", listTag4);
        }
        return itemStack2;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean fits(final int width, final int height) {
        return width * height >= 2;
    }
    
    @Override
    public ItemStack getOutput() {
        return new ItemStack(Items.nX);
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.FIREWORK_ROCKET;
    }
    
    static {
        a = Ingredient.ofItems(Items.kR);
        b = Ingredient.ofItems(Items.jI);
        c = Ingredient.ofItems(Items.nY);
    }
}
