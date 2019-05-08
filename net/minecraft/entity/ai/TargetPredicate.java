package net.minecraft.entity.ai;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.Entity;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import java.util.function.Predicate;

public class TargetPredicate
{
    public static final TargetPredicate DEFAULT;
    private double baseMaxDistance;
    private boolean includeInvulnerable;
    private boolean includeTeammates;
    private boolean includeHidden;
    private boolean ignoreEntityTargetRules;
    private boolean useDistanceScalingFactor;
    private Predicate<LivingEntity> predicate;
    
    public TargetPredicate() {
        this.baseMaxDistance = -1.0;
        this.useDistanceScalingFactor = true;
    }
    
    public TargetPredicate setBaseMaxDistance(final double baseMaxDistance) {
        this.baseMaxDistance = baseMaxDistance;
        return this;
    }
    
    public TargetPredicate includeInvulnerable() {
        this.includeInvulnerable = true;
        return this;
    }
    
    public TargetPredicate includeTeammates() {
        this.includeTeammates = true;
        return this;
    }
    
    public TargetPredicate includeHidden() {
        this.includeHidden = true;
        return this;
    }
    
    public TargetPredicate ignoreEntityTargetRules() {
        this.ignoreEntityTargetRules = true;
        return this;
    }
    
    public TargetPredicate ignoreDistanceScalingFactor() {
        this.useDistanceScalingFactor = false;
        return this;
    }
    
    public TargetPredicate setPredicate(@Nullable final Predicate<LivingEntity> predicate) {
        this.predicate = predicate;
        return this;
    }
    
    public boolean test(@Nullable final LivingEntity baseEntity, final LivingEntity targetEntity) {
        if (baseEntity == targetEntity) {
            return false;
        }
        if (targetEntity.isSpectator()) {
            return false;
        }
        if (!targetEntity.isAlive()) {
            return false;
        }
        if (!this.includeInvulnerable && targetEntity.isInvulnerable()) {
            return false;
        }
        if (this.predicate != null && !this.predicate.test(targetEntity)) {
            return false;
        }
        if (baseEntity != null) {
            if (!this.ignoreEntityTargetRules) {
                if (!baseEntity.canTarget(targetEntity)) {
                    return false;
                }
                if (!baseEntity.canTarget(targetEntity.getType())) {
                    return false;
                }
            }
            if (!this.includeTeammates && baseEntity.isTeammate(targetEntity)) {
                return false;
            }
            if (this.baseMaxDistance > 0.0) {
                final double double3 = this.useDistanceScalingFactor ? targetEntity.getAttackDistanceScalingFactor(baseEntity) : 1.0;
                final double double4 = this.baseMaxDistance * double3;
                final double double5 = baseEntity.squaredDistanceTo(targetEntity.x, targetEntity.y, targetEntity.z);
                if (double5 > double4 * double4) {
                    return false;
                }
            }
            if (!this.includeHidden && baseEntity instanceof MobEntity && !((MobEntity)baseEntity).getVisibilityCache().canSee(targetEntity)) {
                return false;
            }
        }
        return true;
    }
    
    static {
        DEFAULT = new TargetPredicate();
    }
}
