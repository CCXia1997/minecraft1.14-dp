package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import java.util.Set;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.entity.LivingEntity;

public class RandomTask<E extends LivingEntity> extends CompositeTask<E>
{
    public RandomTask(final List<Pair<Task<? super E>, Integer>> tasks) {
        this((Set)ImmutableSet.of(), tasks);
    }
    
    public RandomTask(final Set<Pair<MemoryModuleType<?>, MemoryModuleState>> requiredMemoryState, final List<Pair<Task<? super E>, Integer>> tasks) {
        super(requiredMemoryState, ImmutableSet.of(), Order.b, RunMode.a, tasks);
    }
}
