package net.minecraft.entity.ai.goal;

import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.util.math.Vec3d;
import java.util.EnumSet;
import net.minecraft.entity.mob.MobEntityWithAi;

public class GoToWalkTargetGoal extends Goal
{
    private final MobEntityWithAi mob;
    private double x;
    private double y;
    private double z;
    private final double speed;
    
    public GoToWalkTargetGoal(final MobEntityWithAi mobEntityWithAi, final double double2) {
        this.mob = mobEntityWithAi;
        this.speed = double2;
        this.setControls(EnumSet.<Control>of(Control.a));
    }
    
    @Override
    public boolean canStart() {
        if (this.mob.isInWalkTargetRange()) {
            return false;
        }
        final BlockPos blockPos1 = this.mob.getWalkTarget();
        final Vec3d vec3d2 = PathfindingUtil.a(this.mob, 16, 7, new Vec3d(blockPos1.getX(), blockPos1.getY(), blockPos1.getZ()));
        if (vec3d2 == null) {
            return false;
        }
        this.x = vec3d2.x;
        this.y = vec3d2.y;
        this.z = vec3d2.z;
        return true;
    }
    
    @Override
    public boolean shouldContinue() {
        return !this.mob.getNavigation().isIdle();
    }
    
    @Override
    public void start() {
        this.mob.getNavigation().startMovingTo(this.x, this.y, this.z, this.speed);
    }
}
