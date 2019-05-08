package net.minecraft.entity.ai.goal;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.ai.PathfindingUtil;
import java.util.EnumSet;
import net.minecraft.entity.passive.HorseBaseEntity;

public class HorseBondWithPlayerGoal extends Goal
{
    private final HorseBaseEntity owner;
    private final double speed;
    private double targetX;
    private double targetY;
    private double targetZ;
    
    public HorseBondWithPlayerGoal(final HorseBaseEntity owner, final double speed) {
        this.owner = owner;
        this.speed = speed;
        this.setControls(EnumSet.<Control>of(Control.a));
    }
    
    @Override
    public boolean canStart() {
        if (this.owner.isTame() || !this.owner.hasPassengers()) {
            return false;
        }
        final Vec3d vec3d1 = PathfindingUtil.findTarget(this.owner, 5, 4);
        if (vec3d1 == null) {
            return false;
        }
        this.targetX = vec3d1.x;
        this.targetY = vec3d1.y;
        this.targetZ = vec3d1.z;
        return true;
    }
    
    @Override
    public void start() {
        this.owner.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
    }
    
    @Override
    public boolean shouldContinue() {
        return !this.owner.isTame() && !this.owner.getNavigation().isIdle() && this.owner.hasPassengers();
    }
    
    @Override
    public void tick() {
        if (!this.owner.isTame() && this.owner.getRand().nextInt(50) == 0) {
            final Entity entity1 = this.owner.getPassengerList().get(0);
            if (entity1 == null) {
                return;
            }
            if (entity1 instanceof PlayerEntity) {
                final int integer2 = this.owner.getTemper();
                final int integer3 = this.owner.getMaxTemper();
                if (integer3 > 0 && this.owner.getRand().nextInt(integer3) < integer2) {
                    this.owner.bondWithPlayer((PlayerEntity)entity1);
                    return;
                }
                this.owner.addTemper(5);
            }
            this.owner.removeAllPassengers();
            this.owner.playAngrySound();
            this.owner.world.sendEntityStatus(this.owner, (byte)6);
        }
    }
}
