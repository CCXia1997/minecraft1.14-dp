package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.server.world.ServerWorld;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.mob.MobEntity;

public class LookAroundTask extends Task<MobEntity>
{
    public LookAroundTask(final int minRunTime, final int maxRunTime) {
        super(minRunTime, maxRunTime);
    }
    
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.l, MemoryModuleState.a));
    }
    
    @Override
    protected boolean shouldKeepRunning(final ServerWorld world, final MobEntity entity, final long time) {
        return entity.getBrain().<LookTarget>getOptionalMemory(MemoryModuleType.l).filter(lookTarget -> lookTarget.isSeenBy(entity)).isPresent();
    }
    
    @Override
    protected void finishRunning(final ServerWorld serverWorld, final MobEntity mobEntity, final long time) {
        mobEntity.getBrain().forget(MemoryModuleType.l);
    }
    
    @Override
    protected void keepRunning(final ServerWorld world, final MobEntity entity, final long time) {
        entity.getBrain().<LookTarget>getOptionalMemory(MemoryModuleType.l).ifPresent(lookTarget -> entity.getLookControl().a(lookTarget.getPos()));
    }
}
