package net.minecraft.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity;
import java.util.EnumSet;
import net.minecraft.entity.passive.TameableEntity;

public class SitGoal extends Goal
{
    private final TameableEntity entity;
    private boolean enabledWithOwner;
    
    public SitGoal(final TameableEntity tameableEntity) {
        this.entity = tameableEntity;
        this.setControls(EnumSet.<Control>of(Control.c, Control.a));
    }
    
    @Override
    public boolean canStart() {
        if (!this.entity.isTamed()) {
            return false;
        }
        if (this.entity.isInsideWaterOrBubbleColumn()) {
            return false;
        }
        if (!this.entity.onGround) {
            return false;
        }
        final LivingEntity livingEntity1 = this.entity.getOwner();
        return livingEntity1 == null || ((this.entity.squaredDistanceTo(livingEntity1) >= 144.0 || livingEntity1.getAttacker() == null) && this.enabledWithOwner);
    }
    
    @Override
    public void start() {
        this.entity.getNavigation().stop();
        this.entity.setSitting(true);
    }
    
    @Override
    public void stop() {
        this.entity.setSitting(false);
    }
    
    public void setEnabledWithOwner(final boolean boolean1) {
        this.enabledWithOwner = boolean1;
    }
}
