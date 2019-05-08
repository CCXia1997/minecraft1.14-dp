package net.minecraft.entity.ai.goal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.util.math.Vec3d;
import java.util.EnumSet;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.entity.ai.TargetPredicate;
import java.util.function.Predicate;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.LivingEntity;

public class FleeEntityGoal<T extends LivingEntity> extends Goal
{
    protected final MobEntityWithAi fleeingEntity;
    private final double fleeSlowSpeed;
    private final double fleeFastSpeed;
    protected T targetEntity;
    protected final float fleeDistance;
    protected Path fleePath;
    protected final EntityNavigation fleeingEntityNavigation;
    protected final Class<T> classToFleeFrom;
    protected final Predicate<LivingEntity> g;
    protected final Predicate<LivingEntity> h;
    private final TargetPredicate withinRangePredicate;
    
    public FleeEntityGoal(final MobEntityWithAi fleeingEntity, final Class<T> classToFleeFrom, final float fleeDistance, final double fleeSlowSpeed, final double fleeFastSpeed) {
        this(fleeingEntity, classToFleeFrom, livingEntity -> true, fleeDistance, fleeSlowSpeed, fleeFastSpeed, (Predicate)EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR::test);
    }
    
    public FleeEntityGoal(final MobEntityWithAi fleeingEntity, final Class<T> classToFleeFrom, final Predicate<LivingEntity> predicate3, final float fleeDistance, final double fleeSlowSpeed, final double fleeFastSpeed, final Predicate<LivingEntity> predicate9) {
        this.fleeingEntity = fleeingEntity;
        this.classToFleeFrom = classToFleeFrom;
        this.g = predicate3;
        this.fleeDistance = fleeDistance;
        this.fleeSlowSpeed = fleeSlowSpeed;
        this.fleeFastSpeed = fleeFastSpeed;
        this.h = predicate9;
        this.fleeingEntityNavigation = fleeingEntity.getNavigation();
        this.setControls(EnumSet.<Control>of(Control.a));
        this.withinRangePredicate = new TargetPredicate().setBaseMaxDistance(fleeDistance).setPredicate(predicate9.and(predicate3));
    }
    
    public FleeEntityGoal(final MobEntityWithAi fleeingEntity, final Class<T> classToFleeFrom, final float fleeDistance, final double fleeSlowSpeed, final double fleeFastSpeed, final Predicate<LivingEntity> predicate8) {
        this(fleeingEntity, classToFleeFrom, livingEntity -> true, fleeDistance, fleeSlowSpeed, fleeFastSpeed, predicate8);
    }
    
    @Override
    public boolean canStart() {
        this.targetEntity = this.fleeingEntity.world.<T>getClosestEntity(this.classToFleeFrom, this.withinRangePredicate, (LivingEntity)this.fleeingEntity, this.fleeingEntity.x, this.fleeingEntity.y, this.fleeingEntity.z, this.fleeingEntity.getBoundingBox().expand(this.fleeDistance, 3.0, this.fleeDistance));
        if (this.targetEntity == null) {
            return false;
        }
        final Vec3d vec3d1 = PathfindingUtil.b(this.fleeingEntity, 16, 7, new Vec3d(this.targetEntity.x, this.targetEntity.y, this.targetEntity.z));
        if (vec3d1 == null) {
            return false;
        }
        if (this.targetEntity.squaredDistanceTo(vec3d1.x, vec3d1.y, vec3d1.z) < this.targetEntity.squaredDistanceTo(this.fleeingEntity)) {
            return false;
        }
        this.fleePath = this.fleeingEntityNavigation.findPathTo(vec3d1.x, vec3d1.y, vec3d1.z);
        return this.fleePath != null;
    }
    
    @Override
    public boolean shouldContinue() {
        return !this.fleeingEntityNavigation.isIdle();
    }
    
    @Override
    public void start() {
        this.fleeingEntityNavigation.startMovingAlong(this.fleePath, this.fleeSlowSpeed);
    }
    
    @Override
    public void stop() {
        this.targetEntity = null;
    }
    
    @Override
    public void tick() {
        if (this.fleeingEntity.squaredDistanceTo(this.targetEntity) < 49.0) {
            this.fleeingEntity.getNavigation().setSpeed(this.fleeFastSpeed);
        }
        else {
            this.fleeingEntity.getNavigation().setSpeed(this.fleeSlowSpeed);
        }
    }
}
