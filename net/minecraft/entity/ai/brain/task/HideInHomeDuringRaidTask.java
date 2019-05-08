package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.raid.Raid;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public class HideInHomeDuringRaidTask extends HideInHomeTask
{
    public HideInHomeDuringRaidTask(final int maxDistance, final float walkSpeed) {
        super(maxDistance, walkSpeed, 1);
    }
    
    @Override
    protected boolean shouldRun(final ServerWorld world, final LivingEntity entity) {
        final Raid raid3 = world.getRaidAt(new BlockPos(entity));
        return super.shouldRun(world, entity) && raid3 != null && raid3.isActive() && !raid3.hasWon() && !raid3.hasLost();
    }
}
