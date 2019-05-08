package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.server.world.ServerWorld;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.LivingEntity;

public class WakeUpTask extends Task<LivingEntity>
{
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of();
    }
    
    @Override
    protected boolean shouldRun(final ServerWorld world, final LivingEntity entity) {
        return !entity.getBrain().hasActivity(Activity.e) && entity.isSleeping();
    }
    
    @Override
    protected void run(final ServerWorld world, final LivingEntity entity, final long time) {
        entity.wakeUp();
    }
}
