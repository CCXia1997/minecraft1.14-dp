package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.LivingEntity;

public class StopPanicingTask extends Task<LivingEntity>
{
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of();
    }
    
    @Override
    protected void run(final ServerWorld world, final LivingEntity entity, final long time) {
        final boolean boolean5 = PanicTask.wasHurt(entity) || PanicTask.isHostileNearby(entity) || wasHurtByNearbyEntity(entity);
        if (!boolean5) {
            entity.getBrain().forget(MemoryModuleType.r);
            entity.getBrain().forget(MemoryModuleType.s);
            entity.getBrain().refreshActivities(world.getTimeOfDay(), world.getTime());
        }
    }
    
    private static boolean wasHurtByNearbyEntity(final LivingEntity entity) {
        return entity.getBrain().<LivingEntity>getOptionalMemory(MemoryModuleType.s).filter(livingEntity2 -> livingEntity2.squaredDistanceTo(entity) <= 36.0).isPresent();
    }
}
