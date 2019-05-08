package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.util.math.Vec3d;
import java.util.EnumSet;
import net.minecraft.entity.mob.MobEntityWithAi;

public class WanderAroundGoal extends Goal
{
    protected final MobEntityWithAi owner;
    protected double targetX;
    protected double targetY;
    protected double targetZ;
    protected final double speed;
    protected int chance;
    protected boolean ignoringChance;
    
    public WanderAroundGoal(final MobEntityWithAi owner, final double speed) {
        this(owner, speed, 120);
    }
    
    public WanderAroundGoal(final MobEntityWithAi owner, final double speed, final int chance) {
        this.owner = owner;
        this.speed = speed;
        this.chance = chance;
        this.setControls(EnumSet.<Control>of(Control.a));
    }
    
    @Override
    public boolean canStart() {
        if (this.owner.hasPassengers()) {
            return false;
        }
        if (!this.ignoringChance) {
            if (this.owner.getDespawnCounter() >= 100) {
                return false;
            }
            if (this.owner.getRand().nextInt(this.chance) != 0) {
                return false;
            }
        }
        final Vec3d vec3d1 = this.getWanderTarget();
        if (vec3d1 == null) {
            return false;
        }
        this.targetX = vec3d1.x;
        this.targetY = vec3d1.y;
        this.targetZ = vec3d1.z;
        this.ignoringChance = false;
        return true;
    }
    
    @Nullable
    protected Vec3d getWanderTarget() {
        return PathfindingUtil.findTarget(this.owner, 10, 7);
    }
    
    @Override
    public boolean shouldContinue() {
        return !this.owner.getNavigation().isIdle();
    }
    
    @Override
    public void start() {
        this.owner.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
    }
    
    public void ignoreChanceOnce() {
        this.ignoringChance = true;
    }
    
    public void setChance(final int chance) {
        this.chance = chance;
    }
}
