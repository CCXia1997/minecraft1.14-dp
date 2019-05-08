package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.LivingEntity;

public class WaitTask extends Task<LivingEntity>
{
    public WaitTask(final int minRunTime, final int maxRunTime) {
        super(minRunTime, maxRunTime);
    }
    
    @Override
    protected boolean shouldKeepRunning(final ServerWorld world, final LivingEntity entity, final long time) {
        return true;
    }
    
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of();
    }
}
