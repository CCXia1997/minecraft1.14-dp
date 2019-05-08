package net.minecraft.recipe.crafting;

import net.minecraft.util.SystemUtil;
import com.google.common.collect.Maps;
import java.util.HashMap;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.DyeItem;
import net.minecraft.world.World;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.util.Identifier;
import net.minecraft.item.FireworkItem;
import net.minecraft.item.Item;
import java.util.Map;
import net.minecraft.recipe.Ingredient;

public class FireworkStarRecipe extends SpecialCraftingRecipe
{
    private static final Ingredient a;
    private static final Ingredient b;
    private static final Ingredient c;
    private static final Map<Item, FireworkItem.Type> d;
    private static final Ingredient e;
    
    public FireworkStarRecipe(final Identifier identifier) {
        super(identifier);
    }
    
    @Override
    public boolean matches(final CraftingInventory inv, final World world) {
        boolean boolean3 = false;
        boolean boolean4 = false;
        boolean boolean5 = false;
        boolean boolean6 = false;
        boolean boolean7 = false;
        for (int integer8 = 0; integer8 < inv.getInvSize(); ++integer8) {
            final ItemStack itemStack9 = inv.getInvStack(integer8);
            if (!itemStack9.isEmpty()) {
                if (FireworkStarRecipe.a.a(itemStack9)) {
                    if (boolean5) {
                        return false;
                    }
                    boolean5 = true;
                }
                else if (FireworkStarRecipe.c.a(itemStack9)) {
                    if (boolean7) {
                        return false;
                    }
                    boolean7 = true;
                }
                else if (FireworkStarRecipe.b.a(itemStack9)) {
                    if (boolean6) {
                        return false;
                    }
                    boolean6 = true;
                }
                else if (FireworkStarRecipe.e.a(itemStack9)) {
                    if (boolean3) {
                        return false;
                    }
                    boolean3 = true;
                }
                else {
                    if (!(itemStack9.getItem() instanceof DyeItem)) {
                        return false;
                    }
                    boolean4 = true;
                }
            }
        }
        return boolean3 && boolean4;
    }
    
    @Override
    public ItemStack craft(final CraftingInventory inv) {
        final ItemStack itemStack2 = new ItemStack(Items.nY);
        final CompoundTag compoundTag3 = itemStack2.getOrCreateSubCompoundTag("Explosion");
        FireworkItem.Type type4 = FireworkItem.Type.a;
        final List<Integer> list5 = Lists.newArrayList();
        for (int integer6 = 0; integer6 < inv.getInvSize(); ++integer6) {
            final ItemStack itemStack3 = inv.getInvStack(integer6);
            if (!itemStack3.isEmpty()) {
                if (FireworkStarRecipe.a.a(itemStack3)) {
                    type4 = FireworkStarRecipe.d.get(itemStack3.getItem());
                }
                else if (FireworkStarRecipe.c.a(itemStack3)) {
                    compoundTag3.putBoolean("Flicker", true);
                }
                else if (FireworkStarRecipe.b.a(itemStack3)) {
                    compoundTag3.putBoolean("Trail", true);
                }
                else if (itemStack3.getItem() instanceof DyeItem) {
                    list5.add(((DyeItem)itemStack3.getItem()).getColor().getFireworkColor());
                }
            }
        }
        compoundTag3.putIntArray("Colors", list5);
        compoundTag3.putByte("Type", (byte)type4.getId());
        return itemStack2;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean fits(final int width, final int height) {
        return width * height >= 2;
    }
    
    @Override
    public ItemStack getOutput() {
        return new ItemStack(Items.nY);
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.FIREWORK_STAR;
    }
    
    static {
        a = Ingredient.ofItems(Items.nC, Items.jH, Items.mj, Items.SKELETON_SKULL, Items.WITHER_SKELETON_SKULL, Items.CREEPER_HEAD, Items.PLAYER_HEAD, Items.DRAGON_HEAD, Items.ZOMBIE_HEAD);
        b = Ingredient.ofItems(Items.jj);
        c = Ingredient.ofItems(Items.la);
        d = SystemUtil.<Map<Item, FireworkItem.Type>>consume(Maps.newHashMap(), hashMap -> {
            hashMap.put(Items.nC, FireworkItem.Type.b);
            hashMap.put(Items.jH, FireworkItem.Type.e);
            hashMap.put(Items.mj, FireworkItem.Type.c);
            hashMap.put(Items.SKELETON_SKULL, FireworkItem.Type.d);
            hashMap.put(Items.WITHER_SKELETON_SKULL, FireworkItem.Type.d);
            hashMap.put(Items.CREEPER_HEAD, FireworkItem.Type.d);
            hashMap.put(Items.PLAYER_HEAD, FireworkItem.Type.d);
            hashMap.put(Items.DRAGON_HEAD, FireworkItem.Type.d);
            hashMap.put(Items.ZOMBIE_HEAD, FireworkItem.Type.d);
            return;
        });
        e = Ingredient.ofItems(Items.jI);
    }
}
