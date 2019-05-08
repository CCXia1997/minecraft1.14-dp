package net.minecraft.entity.ai.goal;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BoundingBox;
import java.util.EnumSet;
import javax.annotation.Nullable;
import java.util.function.Predicate;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.LivingEntity;

public class FollowTargetGoal<T extends LivingEntity> extends TrackTargetGoal
{
    protected final Class<T> targetClass;
    protected final int reciprocalChance;
    protected LivingEntity targetEntity;
    protected TargetPredicate targetPredicate;
    
    public FollowTargetGoal(final MobEntity mobEntity, final Class<T> targetEntityClass, final boolean checkVisibility) {
        this(mobEntity, targetEntityClass, checkVisibility, false);
    }
    
    public FollowTargetGoal(final MobEntity mobEntity, final Class<T> targetEntityClass, final boolean checkVisibility, final boolean checkCanNavigate) {
        this(mobEntity, targetEntityClass, 10, checkVisibility, checkCanNavigate, null);
    }
    
    public FollowTargetGoal(final MobEntity mobEntity, final Class<T> targetEntityClass, final int reciprocalChance, final boolean checkVisibility, final boolean checkCanNavigate, @Nullable final Predicate<LivingEntity> targetPredicate) {
        super(mobEntity, checkVisibility, checkCanNavigate);
        this.targetClass = targetEntityClass;
        this.reciprocalChance = reciprocalChance;
        this.setControls(EnumSet.<Control>of(Control.d));
        this.targetPredicate = new TargetPredicate().setBaseMaxDistance(this.getFollowRange()).setPredicate(targetPredicate);
    }
    
    @Override
    public boolean canStart() {
        if (this.reciprocalChance > 0 && this.entity.getRand().nextInt(this.reciprocalChance) != 0) {
            return false;
        }
        this.findClosestTarget();
        return this.targetEntity != null;
    }
    
    protected BoundingBox getSearchBox(final double distance) {
        return this.entity.getBoundingBox().expand(distance, 4.0, distance);
    }
    
    protected void findClosestTarget() {
        if (this.targetClass == PlayerEntity.class || this.targetClass == ServerPlayerEntity.class) {
            this.targetEntity = this.entity.world.getClosestPlayer(this.targetPredicate, this.entity, this.entity.x, this.entity.y + this.entity.getStandingEyeHeight(), this.entity.z);
        }
        else {
            this.targetEntity = this.entity.world.<LivingEntity>getClosestEntity(this.targetClass, this.targetPredicate, (LivingEntity)this.entity, this.entity.x, this.entity.y + this.entity.getStandingEyeHeight(), this.entity.z, this.getSearchBox(this.getFollowRange()));
        }
    }
    
    @Override
    public void start() {
        this.entity.setTarget(this.targetEntity);
        super.start();
    }
}
