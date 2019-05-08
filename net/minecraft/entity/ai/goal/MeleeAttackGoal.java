package net.minecraft.entity.ai.goal;

import net.minecraft.util.Hand;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity;
import java.util.EnumSet;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntityWithAi;

public class MeleeAttackGoal extends Goal
{
    protected final MobEntityWithAi entity;
    protected int ticksUntilAttack;
    private final double d;
    private final boolean e;
    private Path f;
    private int g;
    private double h;
    private double i;
    private double j;
    protected final int c = 20;
    private long k;
    
    public MeleeAttackGoal(final MobEntityWithAi entity, final double double2, final boolean boolean4) {
        this.entity = entity;
        this.d = double2;
        this.e = boolean4;
        this.setControls(EnumSet.<Control>of(Control.a, Control.b));
    }
    
    @Override
    public boolean canStart() {
        final long long1 = this.entity.world.getTime();
        if (long1 - this.k < 20L) {
            return false;
        }
        this.k = long1;
        final LivingEntity livingEntity3 = this.entity.getTarget();
        if (livingEntity3 == null) {
            return false;
        }
        if (!livingEntity3.isAlive()) {
            return false;
        }
        this.f = this.entity.getNavigation().findPathTo(livingEntity3);
        return this.f != null || this.getSquaredMaxAttackDistance(livingEntity3) >= this.entity.squaredDistanceTo(livingEntity3.x, livingEntity3.getBoundingBox().minY, livingEntity3.z);
    }
    
    @Override
    public boolean shouldContinue() {
        final LivingEntity livingEntity1 = this.entity.getTarget();
        if (livingEntity1 == null) {
            return false;
        }
        if (!livingEntity1.isAlive()) {
            return false;
        }
        if (!this.e) {
            return !this.entity.getNavigation().isIdle();
        }
        return this.entity.isInWalkTargetRange(new BlockPos(livingEntity1)) && (!(livingEntity1 instanceof PlayerEntity) || (!livingEntity1.isSpectator() && !((PlayerEntity)livingEntity1).isCreative()));
    }
    
    @Override
    public void start() {
        this.entity.getNavigation().startMovingAlong(this.f, this.d);
        this.entity.setAttacking(true);
        this.g = 0;
    }
    
    @Override
    public void stop() {
        final LivingEntity livingEntity1 = this.entity.getTarget();
        if (!EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(livingEntity1)) {
            this.entity.setTarget(null);
        }
        this.entity.setAttacking(false);
        this.entity.getNavigation().stop();
    }
    
    @Override
    public void tick() {
        final LivingEntity livingEntity1 = this.entity.getTarget();
        this.entity.getLookControl().lookAt(livingEntity1, 30.0f, 30.0f);
        final double double2 = this.entity.squaredDistanceTo(livingEntity1.x, livingEntity1.getBoundingBox().minY, livingEntity1.z);
        --this.g;
        if ((this.e || this.entity.getVisibilityCache().canSee(livingEntity1)) && this.g <= 0 && ((this.h == 0.0 && this.i == 0.0 && this.j == 0.0) || livingEntity1.squaredDistanceTo(this.h, this.i, this.j) >= 1.0 || this.entity.getRand().nextFloat() < 0.05f)) {
            this.h = livingEntity1.x;
            this.i = livingEntity1.getBoundingBox().minY;
            this.j = livingEntity1.z;
            this.g = 4 + this.entity.getRand().nextInt(7);
            if (double2 > 1024.0) {
                this.g += 10;
            }
            else if (double2 > 256.0) {
                this.g += 5;
            }
            if (!this.entity.getNavigation().startMovingTo(livingEntity1, this.d)) {
                this.g += 15;
            }
        }
        this.ticksUntilAttack = Math.max(this.ticksUntilAttack - 1, 0);
        this.attack(livingEntity1, double2);
    }
    
    protected void attack(final LivingEntity target, final double squaredDistance) {
        final double double4 = this.getSquaredMaxAttackDistance(target);
        if (squaredDistance <= double4 && this.ticksUntilAttack <= 0) {
            this.ticksUntilAttack = 20;
            this.entity.swingHand(Hand.a);
            this.entity.tryAttack(target);
        }
    }
    
    protected double getSquaredMaxAttackDistance(final LivingEntity entity) {
        return this.entity.getWidth() * 2.0f * (this.entity.getWidth() * 2.0f) + entity.getWidth();
    }
}
