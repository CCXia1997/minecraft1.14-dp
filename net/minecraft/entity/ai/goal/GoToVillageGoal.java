package net.minecraft.entity.ai.goal;

import java.util.Random;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.world.Heightmap;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.mob.MobEntityWithAi;

public class GoToVillageGoal extends Goal
{
    private final MobEntityWithAi owner;
    private final int searchRange;
    @Nullable
    private BlockPos targetPosition;
    
    public GoToVillageGoal(final MobEntityWithAi mobEntityWithAi, final int integer) {
        this.owner = mobEntityWithAi;
        this.searchRange = integer;
        this.setControls(EnumSet.<Control>of(Control.a));
    }
    
    @Override
    public boolean canStart() {
        if (this.owner.hasPassengers()) {
            return false;
        }
        if (this.owner.world.isDaylight()) {
            return false;
        }
        if (this.owner.getRand().nextInt(this.searchRange) != 0) {
            return false;
        }
        final ServerWorld serverWorld1 = (ServerWorld)this.owner.world;
        final BlockPos blockPos2 = new BlockPos(this.owner);
        if (!serverWorld1.isNearOccupiedPointOfInterest(blockPos2, 6)) {
            return false;
        }
        final Vec3d vec3d3 = PathfindingUtil.findTargetStraight(this.owner, 15, 7, blockPos -> -serverWorld1.getOccupiedPointOfInterestDistance(ChunkSectionPos.from(blockPos)));
        this.targetPosition = ((vec3d3 == null) ? null : new BlockPos(vec3d3));
        return this.targetPosition != null;
    }
    
    @Override
    public boolean shouldContinue() {
        return this.targetPosition != null && !this.owner.getNavigation().isIdle() && this.owner.getNavigation().getTargetPos().equals(this.targetPosition);
    }
    
    @Override
    public void tick() {
        if (this.targetPosition == null) {
            return;
        }
        final EntityNavigation entityNavigation1 = this.owner.getNavigation();
        if (entityNavigation1.isIdle() && !this.targetPosition.isWithinDistance(this.owner.getPos(), 10.0)) {
            Vec3d vec3d2 = new Vec3d(this.targetPosition);
            final Vec3d vec3d3 = new Vec3d(this.owner.x, this.owner.y, this.owner.z);
            final Vec3d vec3d4 = vec3d3.subtract(vec3d2);
            vec3d2 = vec3d4.multiply(0.4).add(vec3d2);
            final Vec3d vec3d5 = vec3d2.subtract(vec3d3).normalize().multiply(10.0).add(vec3d3);
            BlockPos blockPos6 = new BlockPos((int)vec3d5.x, (int)vec3d5.y, (int)vec3d5.z);
            blockPos6 = this.owner.world.getTopPosition(Heightmap.Type.f, blockPos6);
            if (!entityNavigation1.startMovingTo(blockPos6.getX(), blockPos6.getY(), blockPos6.getZ(), 1.0)) {
                this.findOtherWaypoint();
            }
        }
    }
    
    private void findOtherWaypoint() {
        final Random random1 = this.owner.getRand();
        final BlockPos blockPos2 = this.owner.world.getTopPosition(Heightmap.Type.f, new BlockPos(this.owner).add(-8 + random1.nextInt(16), 0, -8 + random1.nextInt(16)));
        this.owner.getNavigation().startMovingTo(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ(), 1.0);
    }
}
