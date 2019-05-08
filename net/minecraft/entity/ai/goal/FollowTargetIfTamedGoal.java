package net.minecraft.entity.ai.goal;

import net.minecraft.entity.mob.MobEntity;
import javax.annotation.Nullable;
import java.util.function.Predicate;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.LivingEntity;

public class FollowTargetIfTamedGoal<T extends LivingEntity> extends FollowTargetGoal<T>
{
    private final TameableEntity i;
    
    public FollowTargetIfTamedGoal(final TameableEntity tameableEntity, final Class<T> class2, final boolean boolean3, @Nullable final Predicate<LivingEntity> predicate) {
        super(tameableEntity, class2, 10, boolean3, false, predicate);
        this.i = tameableEntity;
    }
    
    @Override
    public boolean canStart() {
        return !this.i.isTamed() && super.canStart();
    }
    
    @Override
    public boolean shouldContinue() {
        if (this.targetPredicate != null) {
            return this.targetPredicate.test(this.entity, this.targetEntity);
        }
        return super.shouldContinue();
    }
}
