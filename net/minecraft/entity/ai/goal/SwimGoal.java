package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.mob.MobEntity;

public class SwimGoal extends Goal
{
    private final MobEntity entityMob;
    
    public SwimGoal(final MobEntity mobEntity) {
        this.entityMob = mobEntity;
        this.setControls(EnumSet.<Control>of(Control.c));
        mobEntity.getNavigation().setCanSwim(true);
    }
    
    @Override
    public boolean canStart() {
        final double double1 = (this.entityMob.getStandingEyeHeight() < 0.4) ? 0.2 : 0.4;
        return (this.entityMob.isInsideWater() && this.entityMob.getWaterHeight() > double1) || this.entityMob.isTouchingLava();
    }
    
    @Override
    public void tick() {
        if (this.entityMob.getRand().nextFloat() < 0.8f) {
            this.entityMob.getJumpControl().setActive();
        }
    }
}
