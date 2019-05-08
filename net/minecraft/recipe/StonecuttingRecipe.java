package net.minecraft.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemProvider;
import net.minecraft.block.Blocks;
import net.minecraft.world.World;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class StonecuttingRecipe extends CuttingRecipe
{
    public StonecuttingRecipe(final Identifier identifier, final String string, final Ingredient ingredient, final ItemStack itemStack) {
        super(RecipeType.f, RecipeSerializer.s, identifier, string, ingredient, itemStack);
    }
    
    @Override
    public boolean matches(final Inventory inv, final World world) {
        return this.ingredient.a(inv.getInvStack(0));
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public ItemStack getRecipeKindIcon() {
        return new ItemStack(Blocks.lS);
    }
}
