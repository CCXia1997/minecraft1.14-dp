package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.ai.TargetPredicate;
import javax.annotation.Nullable;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;

public abstract class TrackTargetGoal extends Goal
{
    protected final MobEntity entity;
    protected final boolean checkVisibility;
    private final boolean checkCanNavigate;
    private int canNavigateFlag;
    private int checkCanNavigateCooldown;
    private int timeWithoutVisibility;
    protected LivingEntity target;
    protected int maxTimeWithoutVisibility;
    
    public TrackTargetGoal(final MobEntity mobEntity, final boolean boolean2) {
        this(mobEntity, boolean2, false);
    }
    
    public TrackTargetGoal(final MobEntity mobEntity, final boolean checkVisibility, final boolean checkNavigable) {
        this.maxTimeWithoutVisibility = 60;
        this.entity = mobEntity;
        this.checkVisibility = checkVisibility;
        this.checkCanNavigate = checkNavigable;
    }
    
    @Override
    public boolean shouldContinue() {
        LivingEntity livingEntity1 = this.entity.getTarget();
        if (livingEntity1 == null) {
            livingEntity1 = this.target;
        }
        if (livingEntity1 == null) {
            return false;
        }
        if (!livingEntity1.isAlive()) {
            return false;
        }
        final AbstractTeam abstractTeam2 = this.entity.getScoreboardTeam();
        final AbstractTeam abstractTeam3 = livingEntity1.getScoreboardTeam();
        if (abstractTeam2 != null && abstractTeam3 == abstractTeam2) {
            return false;
        }
        final double double4 = this.getFollowRange();
        if (this.entity.squaredDistanceTo(livingEntity1) > double4 * double4) {
            return false;
        }
        if (this.checkVisibility) {
            if (this.entity.getVisibilityCache().canSee(livingEntity1)) {
                this.timeWithoutVisibility = 0;
            }
            else if (++this.timeWithoutVisibility > this.maxTimeWithoutVisibility) {
                return false;
            }
        }
        if (livingEntity1 instanceof PlayerEntity && ((PlayerEntity)livingEntity1).abilities.invulnerable) {
            return false;
        }
        this.entity.setTarget(livingEntity1);
        return true;
    }
    
    protected double getFollowRange() {
        final EntityAttributeInstance entityAttributeInstance1 = this.entity.getAttributeInstance(EntityAttributes.FOLLOW_RANGE);
        return (entityAttributeInstance1 == null) ? 16.0 : entityAttributeInstance1.getValue();
    }
    
    @Override
    public void start() {
        this.canNavigateFlag = 0;
        this.checkCanNavigateCooldown = 0;
        this.timeWithoutVisibility = 0;
    }
    
    @Override
    public void stop() {
        this.entity.setTarget(null);
        this.target = null;
    }
    
    protected boolean canTrack(@Nullable final LivingEntity target, final TargetPredicate targetPredicate) {
        if (target == null) {
            return false;
        }
        if (!targetPredicate.test(this.entity, target)) {
            return false;
        }
        if (!this.entity.isInWalkTargetRange(new BlockPos(target))) {
            return false;
        }
        if (this.checkCanNavigate) {
            if (--this.checkCanNavigateCooldown <= 0) {
                this.canNavigateFlag = 0;
            }
            if (this.canNavigateFlag == 0) {
                this.canNavigateFlag = (this.canNavigateToEntity(target) ? 1 : 2);
            }
            if (this.canNavigateFlag == 2) {
                return false;
            }
        }
        return true;
    }
    
    private boolean canNavigateToEntity(final LivingEntity livingEntity) {
        this.checkCanNavigateCooldown = 10 + this.entity.getRand().nextInt(5);
        final Path path2 = this.entity.getNavigation().findPathTo(livingEntity);
        if (path2 == null) {
            return false;
        }
        final PathNode pathNode3 = path2.getEnd();
        if (pathNode3 == null) {
            return false;
        }
        final int integer4 = pathNode3.x - MathHelper.floor(livingEntity.x);
        final int integer5 = pathNode3.z - MathHelper.floor(livingEntity.z);
        return integer4 * integer4 + integer5 * integer5 <= 2.25;
    }
    
    public TrackTargetGoal setMaxTimeWithoutVisibility(final int integer) {
        this.maxTimeWithoutVisibility = integer;
        return this;
    }
}
