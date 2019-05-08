package net.minecraft.world.loot;

import net.minecraft.world.loot.context.LootContext;
import net.minecraft.item.ItemStack;
import java.util.function.Consumer;

public interface LootChoice
{
    int getWeight(final float arg1);
    
    void drop(final Consumer<ItemStack> arg1, final LootContext arg2);
}
