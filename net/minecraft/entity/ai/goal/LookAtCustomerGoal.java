package net.minecraft.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.passive.AbstractTraderEntity;

public class LookAtCustomerGoal extends LookAtEntityGoal
{
    private final AbstractTraderEntity trader;
    
    public LookAtCustomerGoal(final AbstractTraderEntity owner) {
        super(owner, PlayerEntity.class, 8.0f);
        this.trader = owner;
    }
    
    @Override
    public boolean canStart() {
        if (this.trader.hasCustomer()) {
            this.target = this.trader.getCurrentCustomer();
            return true;
        }
        return false;
    }
}
