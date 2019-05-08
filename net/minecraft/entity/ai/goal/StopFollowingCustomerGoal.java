package net.minecraft.entity.ai.goal;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.Entity;
import java.util.EnumSet;
import net.minecraft.entity.passive.AbstractTraderEntity;

public class StopFollowingCustomerGoal extends Goal
{
    private final AbstractTraderEntity a;
    
    public StopFollowingCustomerGoal(final AbstractTraderEntity abstractTraderEntity) {
        this.a = abstractTraderEntity;
        this.setControls(EnumSet.<Control>of(Control.c, Control.a));
    }
    
    @Override
    public boolean canStart() {
        if (!this.a.isAlive()) {
            return false;
        }
        if (this.a.isInsideWater()) {
            return false;
        }
        if (!this.a.onGround) {
            return false;
        }
        if (this.a.velocityModified) {
            return false;
        }
        final PlayerEntity playerEntity1 = this.a.getCurrentCustomer();
        return playerEntity1 != null && this.a.squaredDistanceTo(playerEntity1) <= 16.0 && playerEntity1.container != null;
    }
    
    @Override
    public void start() {
        this.a.getNavigation().stop();
    }
    
    @Override
    public void stop() {
        this.a.setCurrentCustomer(null);
    }
}
