package net.minecraft.recipe.crafting;

import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.block.FlowerBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tag.ItemTags;
import net.minecraft.block.Blocks;
import net.minecraft.world.World;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.util.Identifier;

public class SuspiciousStewRecipe extends SpecialCraftingRecipe
{
    public SuspiciousStewRecipe(final Identifier identifier) {
        super(identifier);
    }
    
    @Override
    public boolean matches(final CraftingInventory inv, final World world) {
        boolean boolean3 = false;
        boolean boolean4 = false;
        boolean boolean5 = false;
        boolean boolean6 = false;
        for (int integer7 = 0; integer7 < inv.getInvSize(); ++integer7) {
            final ItemStack itemStack8 = inv.getInvStack(integer7);
            if (!itemStack8.isEmpty()) {
                if (itemStack8.getItem() == Blocks.bB.getItem() && !boolean5) {
                    boolean5 = true;
                }
                else if (itemStack8.getItem() == Blocks.bC.getItem() && !boolean4) {
                    boolean4 = true;
                }
                else if (itemStack8.getItem().matches(ItemTags.E) && !boolean3) {
                    boolean3 = true;
                }
                else {
                    if (itemStack8.getItem() != Items.jA || boolean6) {
                        return false;
                    }
                    boolean6 = true;
                }
            }
        }
        return boolean3 && boolean5 && boolean4 && boolean6;
    }
    
    @Override
    public ItemStack craft(final CraftingInventory inv) {
        ItemStack itemStack2 = ItemStack.EMPTY;
        for (int integer3 = 0; integer3 < inv.getInvSize(); ++integer3) {
            final ItemStack itemStack3 = inv.getInvStack(integer3);
            if (!itemStack3.isEmpty()) {
                if (itemStack3.getItem().matches(ItemTags.E)) {
                    itemStack2 = itemStack3;
                    break;
                }
            }
        }
        final ItemStack itemStack4 = new ItemStack(Items.pz, 1);
        if (itemStack2.getItem() instanceof BlockItem && ((BlockItem)itemStack2.getItem()).getBlock() instanceof FlowerBlock) {
            final FlowerBlock flowerBlock4 = (FlowerBlock)((BlockItem)itemStack2.getItem()).getBlock();
            final StatusEffect statusEffect5 = flowerBlock4.getEffectInStew();
            SuspiciousStewItem.addEffectToStew(itemStack4, statusEffect5, flowerBlock4.getEffectInStewDuration());
        }
        return itemStack4;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean fits(final int width, final int height) {
        return width >= 2 && height >= 2;
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SUSPICIOUS_STEW;
    }
}
