package net.minecraft.world.loot;

import net.minecraft.world.loot.condition.LootCondition;

public interface ConditionConsumerBuilder<T>
{
    T withCondition(final LootCondition.Builder arg1);
    
    T getThis();
}
