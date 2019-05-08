package net.minecraft.world.loot;

import net.minecraft.world.loot.function.LootFunction;

public interface FunctionConsumerBuilder<T>
{
    T withFunction(final LootFunction.Builder arg1);
    
    T getThis();
}
