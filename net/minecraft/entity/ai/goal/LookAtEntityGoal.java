package net.minecraft.entity.ai.goal;

import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.entity.player.PlayerEntity;
import java.util.EnumSet;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;

public class LookAtEntityGoal extends Goal
{
    protected final MobEntity owner;
    protected Entity target;
    protected final float range;
    private int lookTime;
    private final float chance;
    protected final Class<? extends LivingEntity> targetType;
    protected final TargetPredicate targetPredicate;
    
    public LookAtEntityGoal(final MobEntity owner, final Class<? extends LivingEntity> targetType, final float range) {
        this(owner, targetType, range, 0.02f);
    }
    
    public LookAtEntityGoal(final MobEntity owner, final Class<? extends LivingEntity> targetType, final float range, final float chance) {
        this.owner = owner;
        this.targetType = targetType;
        this.range = range;
        this.chance = chance;
        this.setControls(EnumSet.<Control>of(Control.b));
        if (targetType == PlayerEntity.class) {
            this.targetPredicate = new TargetPredicate().setBaseMaxDistance(range).includeTeammates().includeInvulnerable().ignoreEntityTargetRules().setPredicate(livingEntity -> EntityPredicates.rides(owner).test(livingEntity));
        }
        else {
            this.targetPredicate = new TargetPredicate().setBaseMaxDistance(range).includeTeammates().includeInvulnerable().ignoreEntityTargetRules();
        }
    }
    
    @Override
    public boolean canStart() {
        if (this.owner.getRand().nextFloat() >= this.chance) {
            return false;
        }
        if (this.owner.getTarget() != null) {
            this.target = this.owner.getTarget();
        }
        if (this.targetType == PlayerEntity.class) {
            this.target = this.owner.world.getClosestPlayer(this.targetPredicate, this.owner, this.owner.x, this.owner.y + this.owner.getStandingEyeHeight(), this.owner.z);
        }
        else {
            this.target = this.owner.world.<Entity>getClosestEntity(this.targetType, this.targetPredicate, (LivingEntity)this.owner, this.owner.x, this.owner.y + this.owner.getStandingEyeHeight(), this.owner.z, this.owner.getBoundingBox().expand(this.range, 3.0, this.range));
        }
        return this.target != null;
    }
    
    @Override
    public boolean shouldContinue() {
        return this.target.isAlive() && this.owner.squaredDistanceTo(this.target) <= this.range * this.range && this.lookTime > 0;
    }
    
    @Override
    public void start() {
        this.lookTime = 40 + this.owner.getRand().nextInt(40);
    }
    
    @Override
    public void stop() {
        this.target = null;
    }
    
    @Override
    public void tick() {
        this.owner.getLookControl().a(this.target.x, this.target.y + this.target.getStandingEyeHeight(), this.target.z);
        --this.lookTime;
    }
}
