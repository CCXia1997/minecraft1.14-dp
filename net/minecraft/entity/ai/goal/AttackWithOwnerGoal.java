package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.TargetPredicate;
import java.util.EnumSet;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;

public class AttackWithOwnerGoal extends TrackTargetGoal
{
    private final TameableEntity owner;
    private LivingEntity attacking;
    private int lastAttackTime;
    
    public AttackWithOwnerGoal(final TameableEntity tameableEntity) {
        super(tameableEntity, false);
        this.owner = tameableEntity;
        this.setControls(EnumSet.<Control>of(Control.d));
    }
    
    @Override
    public boolean canStart() {
        if (!this.owner.isTamed()) {
            return false;
        }
        final LivingEntity livingEntity1 = this.owner.getOwner();
        if (livingEntity1 == null) {
            return false;
        }
        this.attacking = livingEntity1.getAttacking();
        final int integer2 = livingEntity1.getLastAttackTime();
        return integer2 != this.lastAttackTime && this.canTrack(this.attacking, TargetPredicate.DEFAULT) && this.owner.canAttackWithOwner(this.attacking, livingEntity1);
    }
    
    @Override
    public void start() {
        this.entity.setTarget(this.attacking);
        final LivingEntity livingEntity1 = this.owner.getOwner();
        if (livingEntity1 != null) {
            this.lastAttackTime = livingEntity1.getLastAttackTime();
        }
        super.start();
    }
}
