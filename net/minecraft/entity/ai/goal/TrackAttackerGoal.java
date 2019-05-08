package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.TargetPredicate;
import java.util.EnumSet;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;

public class TrackAttackerGoal extends TrackTargetGoal
{
    private final TameableEntity a;
    private LivingEntity b;
    private int c;
    
    public TrackAttackerGoal(final TameableEntity tameableEntity) {
        super(tameableEntity, false);
        this.a = tameableEntity;
        this.setControls(EnumSet.<Control>of(Control.d));
    }
    
    @Override
    public boolean canStart() {
        if (!this.a.isTamed()) {
            return false;
        }
        final LivingEntity livingEntity1 = this.a.getOwner();
        if (livingEntity1 == null) {
            return false;
        }
        this.b = livingEntity1.getAttacker();
        final int integer2 = livingEntity1.getLastAttackedTime();
        return integer2 != this.c && this.canTrack(this.b, TargetPredicate.DEFAULT) && this.a.canAttackWithOwner(this.b, livingEntity1);
    }
    
    @Override
    public void start() {
        this.entity.setTarget(this.b);
        final LivingEntity livingEntity1 = this.a.getOwner();
        if (livingEntity1 != null) {
            this.c = livingEntity1.getLastAttackedTime();
        }
        super.start();
    }
}
