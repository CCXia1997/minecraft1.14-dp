package net.minecraft.client.recipe.book;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum RecipeBookGroup
{
    a(new ItemStack[] { new ItemStack(Items.kX) }), 
    b(new ItemStack[] { new ItemStack(Blocks.bF) }), 
    c(new ItemStack[] { new ItemStack(Items.kC) }), 
    d(new ItemStack[] { new ItemStack(Items.jc), new ItemStack(Items.jC) }), 
    e(new ItemStack[] { new ItemStack(Items.kz), new ItemStack(Items.je) }), 
    f(new ItemStack[] { new ItemStack(Items.kX) }), 
    g(new ItemStack[] { new ItemStack(Items.km) }), 
    h(new ItemStack[] { new ItemStack(Blocks.b) }), 
    i(new ItemStack[] { new ItemStack(Items.kz), new ItemStack(Items.nF) }), 
    j(new ItemStack[] { new ItemStack(Items.kX) }), 
    k(new ItemStack[] { new ItemStack(Blocks.cw) }), 
    l(new ItemStack[] { new ItemStack(Items.ja), new ItemStack(Items.kj) }), 
    m(new ItemStack[] { new ItemStack(Items.kX) }), 
    n(new ItemStack[] { new ItemStack(Items.km) }), 
    o(new ItemStack[] { new ItemStack(Items.CHISELED_STONE_BRICKS) }), 
    p(new ItemStack[] { new ItemStack(Items.km) });
    
    private final List<ItemStack> icons;
    
    private RecipeBookGroup(final ItemStack[] entries) {
        this.icons = ImmutableList.<ItemStack>copyOf(entries);
    }
    
    public List<ItemStack> getIcons() {
        return this.icons;
    }
}
