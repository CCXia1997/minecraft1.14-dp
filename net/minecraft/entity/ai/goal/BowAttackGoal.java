package net.minecraft.entity.ai.goal;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.item.BowItem;
import net.minecraft.entity.ai.RangedAttacker;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import java.util.EnumSet;

public class BowAttackGoal<T extends HostileEntity> extends Goal
{
    private final T a;
    private final double b;
    private int c;
    private final float d;
    private int e;
    private int f;
    private boolean g;
    private boolean h;
    private int i;
    
    public BowAttackGoal(final T hostileEntity, final double double2, final int integer, final float float5) {
        this.e = -1;
        this.i = -1;
        this.a = hostileEntity;
        this.b = double2;
        this.c = integer;
        this.d = float5 * float5;
        this.setControls(EnumSet.<Control>of(Control.a, Control.b));
    }
    
    public void setAttackInterval(final int integer) {
        this.c = integer;
    }
    
    @Override
    public boolean canStart() {
        return ((MobEntity)this.a).getTarget() != null && this.isHoldingBow();
    }
    
    protected boolean isHoldingBow() {
        return ((MobEntity)this.a).isHolding(Items.jf);
    }
    
    @Override
    public boolean shouldContinue() {
        return (this.canStart() || !((MobEntity)this.a).getNavigation().isIdle()) && this.isHoldingBow();
    }
    
    @Override
    public void start() {
        super.start();
        ((MobEntity)this.a).setAttacking(true);
    }
    
    @Override
    public void stop() {
        super.stop();
        ((MobEntity)this.a).setAttacking(false);
        this.f = 0;
        this.e = -1;
        ((LivingEntity)this.a).clearActiveItem();
    }
    
    @Override
    public void tick() {
        final LivingEntity livingEntity1 = ((MobEntity)this.a).getTarget();
        if (livingEntity1 == null) {
            return;
        }
        final double double2 = ((Entity)this.a).squaredDistanceTo(livingEntity1.x, livingEntity1.getBoundingBox().minY, livingEntity1.z);
        final boolean boolean4 = ((MobEntity)this.a).getVisibilityCache().canSee(livingEntity1);
        final boolean boolean5 = this.f > 0;
        if (boolean4 != boolean5) {
            this.f = 0;
        }
        if (boolean4) {
            ++this.f;
        }
        else {
            --this.f;
        }
        if (double2 > this.d || this.f < 20) {
            ((MobEntity)this.a).getNavigation().startMovingTo(livingEntity1, this.b);
            this.i = -1;
        }
        else {
            ((MobEntity)this.a).getNavigation().stop();
            ++this.i;
        }
        if (this.i >= 20) {
            if (((LivingEntity)this.a).getRand().nextFloat() < 0.3) {
                this.g = !this.g;
            }
            if (((LivingEntity)this.a).getRand().nextFloat() < 0.3) {
                this.h = !this.h;
            }
            this.i = 0;
        }
        if (this.i > -1) {
            if (double2 > this.d * 0.75f) {
                this.h = false;
            }
            else if (double2 < this.d * 0.25f) {
                this.h = true;
            }
            ((MobEntity)this.a).getMoveControl().a(this.h ? -0.5f : 0.5f, this.g ? 0.5f : -0.5f);
            ((MobEntity)this.a).lookAtEntity(livingEntity1, 30.0f, 30.0f);
        }
        else {
            ((MobEntity)this.a).getLookControl().lookAt(livingEntity1, 30.0f, 30.0f);
        }
        if (((LivingEntity)this.a).isUsingItem()) {
            if (!boolean4 && this.f < -60) {
                ((LivingEntity)this.a).clearActiveItem();
            }
            else if (boolean4) {
                final int integer6 = ((LivingEntity)this.a).getItemUseTime();
                if (integer6 >= 20) {
                    ((LivingEntity)this.a).clearActiveItem();
                    ((RangedAttacker)this.a).attack(livingEntity1, BowItem.a(integer6));
                    this.e = this.c;
                }
            }
        }
        else {
            final int e = this.e - 1;
            this.e = e;
            if (e <= 0 && this.f >= -60) {
                ((LivingEntity)this.a).setCurrentHand(ProjectileUtil.getHandPossiblyHolding((LivingEntity)this.a, Items.jf));
            }
        }
    }
}
