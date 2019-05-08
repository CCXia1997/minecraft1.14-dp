package net.minecraft.entity.ai.goal;

import net.minecraft.entity.Entity;
import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.BlockView;

public class AttackGoal extends Goal
{
    private final BlockView world;
    private final MobEntity mob;
    private LivingEntity target;
    private int cooldown;
    
    public AttackGoal(final MobEntity mobEntity) {
        this.mob = mobEntity;
        this.world = mobEntity.world;
        this.setControls(EnumSet.<Control>of(Control.a, Control.b));
    }
    
    @Override
    public boolean canStart() {
        final LivingEntity livingEntity1 = this.mob.getTarget();
        if (livingEntity1 == null) {
            return false;
        }
        this.target = livingEntity1;
        return true;
    }
    
    @Override
    public boolean shouldContinue() {
        return this.target.isAlive() && this.mob.squaredDistanceTo(this.target) <= 225.0 && (!this.mob.getNavigation().isIdle() || this.canStart());
    }
    
    @Override
    public void stop() {
        this.target = null;
        this.mob.getNavigation().stop();
    }
    
    @Override
    public void tick() {
        this.mob.getLookControl().lookAt(this.target, 30.0f, 30.0f);
        final double double1 = this.mob.getWidth() * 2.0f * (this.mob.getWidth() * 2.0f);
        final double double2 = this.mob.squaredDistanceTo(this.target.x, this.target.getBoundingBox().minY, this.target.z);
        double double3 = 0.8;
        if (double2 > double1 && double2 < 16.0) {
            double3 = 1.33;
        }
        else if (double2 < 225.0) {
            double3 = 0.6;
        }
        this.mob.getNavigation().startMovingTo(this.target, double3);
        this.cooldown = Math.max(this.cooldown - 1, 0);
        if (double2 > double1) {
            return;
        }
        if (this.cooldown > 0) {
            return;
        }
        this.cooldown = 20;
        this.mob.tryAttack(this.target);
    }
}
