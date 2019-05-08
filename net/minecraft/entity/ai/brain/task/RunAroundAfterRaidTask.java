package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.raid.Raid;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;

public class RunAroundAfterRaidTask extends FindWalkTargetTask
{
    public RunAroundAfterRaidTask(final float walkSpeed) {
        super(walkSpeed);
    }
    
    @Override
    protected boolean shouldRun(final ServerWorld world, final MobEntityWithAi entity) {
        final Raid raid3 = world.getRaidAt(new BlockPos(entity));
        return raid3 != null && raid3.hasWon() && super.shouldRun(world, entity);
    }
}
