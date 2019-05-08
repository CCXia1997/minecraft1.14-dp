package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.server.world.ServerWorld;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.LivingEntity;

public class PanicTask extends Task<LivingEntity>
{
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of();
    }
    
    @Override
    protected void run(final ServerWorld world, final LivingEntity entity, final long time) {
        if (wasHurt(entity) || isHostileNearby(entity)) {
            final Brain<?> brain5 = entity.getBrain();
            if (!brain5.hasActivity(Activity.g)) {
                brain5.forget(MemoryModuleType.o);
                brain5.forget(MemoryModuleType.k);
                brain5.forget(MemoryModuleType.l);
                brain5.forget(MemoryModuleType.n);
                brain5.forget(MemoryModuleType.m);
            }
            brain5.resetPossibleActivities(Activity.g);
        }
    }
    
    public static boolean isHostileNearby(final LivingEntity entity) {
        return entity.getBrain().hasMemoryModule(MemoryModuleType.t);
    }
    
    public static boolean wasHurt(final LivingEntity entity) {
        return entity.getBrain().hasMemoryModule(MemoryModuleType.r);
    }
}
