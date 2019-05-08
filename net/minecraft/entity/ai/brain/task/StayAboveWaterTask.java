package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.mob.MobEntity;

public class StayAboveWaterTask extends Task<MobEntity>
{
    private final float minWaterHeight;
    private final float chance;
    
    public StayAboveWaterTask(final float minWaterHeight, final float chance) {
        this.minWaterHeight = minWaterHeight;
        this.chance = chance;
    }
    
    @Override
    protected boolean shouldRun(final ServerWorld world, final MobEntity entity) {
        return (entity.isInsideWater() && entity.getWaterHeight() > this.minWaterHeight) || entity.isTouchingLava();
    }
    
    @Override
    protected boolean shouldKeepRunning(final ServerWorld world, final MobEntity entity, final long time) {
        return this.shouldRun(world, entity);
    }
    
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of();
    }
    
    @Override
    protected void keepRunning(final ServerWorld world, final MobEntity entity, final long time) {
        if (entity.getRand().nextFloat() < this.chance) {
            entity.getJumpControl().setActive();
        }
    }
}
