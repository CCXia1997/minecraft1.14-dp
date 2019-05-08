package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.mob.MobEntityWithAi;

public class WanderAroundPointOfInterestGoal extends WanderAroundGoal
{
    public WanderAroundPointOfInterestGoal(final MobEntityWithAi owner, final double speed) {
        super(owner, speed, 10);
    }
    
    @Override
    public boolean canStart() {
        final ServerWorld serverWorld1 = (ServerWorld)this.owner.world;
        final BlockPos blockPos2 = new BlockPos(this.owner);
        return !serverWorld1.isNearOccupiedPointOfInterest(blockPos2) && super.canStart();
    }
    
    @Nullable
    @Override
    protected Vec3d getWanderTarget() {
        final ServerWorld serverWorld1 = (ServerWorld)this.owner.world;
        final BlockPos blockPos2 = new BlockPos(this.owner);
        final ChunkSectionPos chunkSectionPos3 = ChunkSectionPos.from(blockPos2);
        final ChunkSectionPos chunkSectionPos4 = LookTargetUtil.getPosClosestToOccupiedPointOfInterest(serverWorld1, chunkSectionPos3, 2);
        if (chunkSectionPos4 != chunkSectionPos3) {
            final BlockPos blockPos3 = chunkSectionPos4.getCenterPos();
            return PathfindingUtil.a(this.owner, 10, 7, new Vec3d(blockPos3.getX(), blockPos3.getY(), blockPos3.getZ()));
        }
        return null;
    }
}
