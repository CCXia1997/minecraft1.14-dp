package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.raid.Raid;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.LivingEntity;

public class StartRaidTask extends Task<LivingEntity>
{
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of();
    }
    
    @Override
    protected boolean shouldRun(final ServerWorld world, final LivingEntity entity) {
        return world.random.nextInt(20) == 0;
    }
    
    @Override
    protected void run(final ServerWorld world, final LivingEntity entity, final long time) {
        final Brain<?> brain5 = entity.getBrain();
        final Raid raid6 = world.getRaidAt(new BlockPos(entity));
        if (raid6 != null) {
            if (!raid6.hasSpawned() || raid6.isPreRaid()) {
                brain5.setDefaultActivity(Activity.i);
                brain5.resetPossibleActivities(Activity.i);
            }
            else {
                brain5.setDefaultActivity(Activity.h);
                brain5.resetPossibleActivities(Activity.h);
            }
        }
    }
}
