package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.raid.Raid;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import com.mojang.datafixers.util.Pair;
import java.util.Set;

public class SeekSkyAfterRaidWinTask extends SeekSkyTask
{
    public SeekSkyAfterRaidWinTask(final float speed) {
        super(speed);
    }
    
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.k, MemoryModuleState.b));
    }
    
    @Override
    protected boolean shouldRun(final ServerWorld world, final LivingEntity entity) {
        final Raid raid3 = world.getRaidAt(new BlockPos(entity));
        return raid3 != null && raid3.hasWon() && super.shouldRun(world, entity);
    }
}
