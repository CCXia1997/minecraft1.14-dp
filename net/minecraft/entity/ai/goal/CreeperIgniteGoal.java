package net.minecraft.entity.ai.goal;

import net.minecraft.entity.Entity;
import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CreeperEntity;

public class CreeperIgniteGoal extends Goal
{
    private final CreeperEntity owner;
    private LivingEntity target;
    
    public CreeperIgniteGoal(final CreeperEntity owner) {
        this.owner = owner;
        this.setControls(EnumSet.<Control>of(Control.a));
    }
    
    @Override
    public boolean canStart() {
        final LivingEntity livingEntity1 = this.owner.getTarget();
        return this.owner.getFuseSpeed() > 0 || (livingEntity1 != null && this.owner.squaredDistanceTo(livingEntity1) < 9.0);
    }
    
    @Override
    public void start() {
        this.owner.getNavigation().stop();
        this.target = this.owner.getTarget();
    }
    
    @Override
    public void stop() {
        this.target = null;
    }
    
    @Override
    public void tick() {
        if (this.target == null) {
            this.owner.setFuseSpeed(-1);
            return;
        }
        if (this.owner.squaredDistanceTo(this.target) > 49.0) {
            this.owner.setFuseSpeed(-1);
            return;
        }
        if (!this.owner.getVisibilityCache().canSee(this.target)) {
            this.owner.setFuseSpeed(-1);
            return;
        }
        this.owner.setFuseSpeed(1);
    }
}
