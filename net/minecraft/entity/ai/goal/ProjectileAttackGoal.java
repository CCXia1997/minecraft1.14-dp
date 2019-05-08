package net.minecraft.entity.ai.goal;

import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttacker;
import net.minecraft.entity.mob.MobEntity;

public class ProjectileAttackGoal extends Goal
{
    private final MobEntity mobEntity;
    private final RangedAttacker rangedAttacker;
    private LivingEntity target;
    private int d;
    private final double e;
    private int f;
    private final int g;
    private final int h;
    private final float i;
    private final float j;
    
    public ProjectileAttackGoal(final RangedAttacker rangedAttacker, final double double2, final int integer, final float float5) {
        this(rangedAttacker, double2, integer, integer, float5);
    }
    
    public ProjectileAttackGoal(final RangedAttacker entity, final double double2, final int integer4, final int integer5, final float float6) {
        this.d = -1;
        if (!(entity instanceof LivingEntity)) {
            throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
        }
        this.rangedAttacker = entity;
        this.mobEntity = (MobEntity)entity;
        this.e = double2;
        this.g = integer4;
        this.h = integer5;
        this.i = float6;
        this.j = float6 * float6;
        this.setControls(EnumSet.<Control>of(Control.a, Control.b));
    }
    
    @Override
    public boolean canStart() {
        final LivingEntity livingEntity1 = this.mobEntity.getTarget();
        if (livingEntity1 == null || !livingEntity1.isAlive()) {
            return false;
        }
        this.target = livingEntity1;
        return true;
    }
    
    @Override
    public boolean shouldContinue() {
        return this.canStart() || !this.mobEntity.getNavigation().isIdle();
    }
    
    @Override
    public void stop() {
        this.target = null;
        this.f = 0;
        this.d = -1;
    }
    
    @Override
    public void tick() {
        final double double1 = this.mobEntity.squaredDistanceTo(this.target.x, this.target.getBoundingBox().minY, this.target.z);
        final boolean boolean3 = this.mobEntity.getVisibilityCache().canSee(this.target);
        if (boolean3) {
            ++this.f;
        }
        else {
            this.f = 0;
        }
        if (double1 > this.j || this.f < 5) {
            this.mobEntity.getNavigation().startMovingTo(this.target, this.e);
        }
        else {
            this.mobEntity.getNavigation().stop();
        }
        this.mobEntity.getLookControl().lookAt(this.target, 30.0f, 30.0f);
        final int d = this.d - 1;
        this.d = d;
        if (d == 0) {
            if (!boolean3) {
                return;
            }
            float float5;
            final float float4 = float5 = MathHelper.sqrt(double1) / this.i;
            float5 = MathHelper.clamp(float5, 0.1f, 1.0f);
            this.rangedAttacker.attack(this.target, float5);
            this.d = MathHelper.floor(float4 * (this.h - this.g) + this.g);
        }
        else if (this.d < 0) {
            final float float4 = MathHelper.sqrt(double1) / this.i;
            this.d = MathHelper.floor(float4 * (this.h - this.g) + this.g);
        }
    }
}
