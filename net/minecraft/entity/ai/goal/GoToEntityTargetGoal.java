package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntityWithAi;

public class GoToEntityTargetGoal extends Goal
{
    private final MobEntityWithAi owner;
    private LivingEntity target;
    private double c;
    private double d;
    private double e;
    private final double f;
    private final float maxDistance;
    
    public GoToEntityTargetGoal(final MobEntityWithAi mobEntityWithAi, final double double2, final float float4) {
        this.owner = mobEntityWithAi;
        this.f = double2;
        this.maxDistance = float4;
        this.setControls(EnumSet.<Control>of(Control.a));
    }
    
    @Override
    public boolean canStart() {
        this.target = this.owner.getTarget();
        if (this.target == null) {
            return false;
        }
        if (this.target.squaredDistanceTo(this.owner) > this.maxDistance * this.maxDistance) {
            return false;
        }
        final Vec3d vec3d1 = PathfindingUtil.a(this.owner, 16, 7, new Vec3d(this.target.x, this.target.y, this.target.z));
        if (vec3d1 == null) {
            return false;
        }
        this.c = vec3d1.x;
        this.d = vec3d1.y;
        this.e = vec3d1.z;
        return true;
    }
    
    @Override
    public boolean shouldContinue() {
        return !this.owner.getNavigation().isIdle() && this.target.isAlive() && this.target.squaredDistanceTo(this.owner) < this.maxDistance * this.maxDistance;
    }
    
    @Override
    public void stop() {
        this.target = null;
    }
    
    @Override
    public void start() {
        this.owner.getNavigation().startMovingTo(this.c, this.d, this.e, this.f);
    }
}
