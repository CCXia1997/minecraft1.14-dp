package net.minecraft.entity.ai.goal;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.ai.RangedAttacker;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.Entity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import java.util.EnumSet;

public class CrossbowAttackGoal<T extends HostileEntity> extends Goal
{
    private final T entity;
    private Stage stage;
    private final double c;
    private final float d;
    private int e;
    private int f;
    
    public CrossbowAttackGoal(final T entity, final double double2, final float float4) {
        this.stage = Stage.a;
        this.entity = entity;
        this.c = double2;
        this.d = float4 * float4;
        this.setControls(EnumSet.<Control>of(Control.a, Control.b));
    }
    
    @Override
    public boolean canStart() {
        return this.h() && this.isEntityHoldingCrossbow();
    }
    
    private boolean isEntityHoldingCrossbow() {
        return ((MobEntity)this.entity).isHolding(Items.py);
    }
    
    @Override
    public boolean shouldContinue() {
        return this.h() && (this.canStart() || !((MobEntity)this.entity).getNavigation().isIdle()) && this.isEntityHoldingCrossbow();
    }
    
    private boolean h() {
        return ((MobEntity)this.entity).getTarget() != null && ((MobEntity)this.entity).getTarget().isAlive();
    }
    
    @Override
    public void stop() {
        super.stop();
        ((MobEntity)this.entity).setAttacking(false);
        ((MobEntity)this.entity).setTarget(null);
        this.e = 0;
        if (((LivingEntity)this.entity).isUsingItem()) {
            ((LivingEntity)this.entity).clearActiveItem();
            ((CrossbowUser)this.entity).setCharging(false);
            CrossbowItem.setCharged(((LivingEntity)this.entity).getActiveItem(), false);
        }
    }
    
    @Override
    public void tick() {
        final LivingEntity livingEntity1 = ((MobEntity)this.entity).getTarget();
        if (livingEntity1 == null) {
            return;
        }
        final boolean boolean2 = ((MobEntity)this.entity).getVisibilityCache().canSee(livingEntity1);
        final boolean boolean3 = this.e > 0;
        if (boolean2 != boolean3) {
            this.e = 0;
        }
        if (boolean2) {
            ++this.e;
        }
        else {
            --this.e;
        }
        final double double4 = ((Entity)this.entity).squaredDistanceTo(livingEntity1);
        final boolean boolean4 = (double4 > this.d || this.e < 5) && this.f == 0;
        if (boolean4) {
            ((MobEntity)this.entity).getNavigation().startMovingTo(livingEntity1, this.isUncharged() ? this.c : (this.c * 0.5));
        }
        else {
            ((MobEntity)this.entity).getNavigation().stop();
        }
        ((MobEntity)this.entity).getLookControl().lookAt(livingEntity1, 30.0f, 30.0f);
        if (this.stage == Stage.a) {
            if (!boolean4) {
                ((LivingEntity)this.entity).setCurrentHand(ProjectileUtil.getHandPossiblyHolding((LivingEntity)this.entity, Items.py));
                this.stage = Stage.b;
                ((CrossbowUser)this.entity).setCharging(true);
            }
        }
        else if (this.stage == Stage.b) {
            if (!((LivingEntity)this.entity).isUsingItem()) {
                this.stage = Stage.a;
            }
            final int integer7 = ((LivingEntity)this.entity).getItemUseTime();
            final ItemStack itemStack8 = ((LivingEntity)this.entity).getActiveItem();
            if (integer7 >= CrossbowItem.getPullTime(itemStack8)) {
                ((LivingEntity)this.entity).stopUsingItem();
                this.stage = Stage.c;
                this.f = 20 + ((LivingEntity)this.entity).getRand().nextInt(20);
                ((CrossbowUser)this.entity).setCharging(false);
            }
        }
        else if (this.stage == Stage.c) {
            --this.f;
            if (this.f == 0) {
                this.stage = Stage.d;
            }
        }
        else if (this.stage == Stage.d && boolean2) {
            ((RangedAttacker)this.entity).attack(livingEntity1, 1.0f);
            final ItemStack itemStack9 = ((LivingEntity)this.entity).getStackInHand(ProjectileUtil.getHandPossiblyHolding((LivingEntity)this.entity, Items.py));
            CrossbowItem.setCharged(itemStack9, false);
            this.stage = Stage.a;
        }
    }
    
    private boolean isUncharged() {
        return this.stage == Stage.a;
    }
    
    enum Stage
    {
        a, 
        b, 
        c, 
        d;
    }
}
