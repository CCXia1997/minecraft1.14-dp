package net.minecraft.entity.ai.goal;

import net.minecraft.entity.mob.MobEntity;
import javax.annotation.Nullable;
import java.util.function.Predicate;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.entity.LivingEntity;

public class DisableableFollowTargetGoal<T extends LivingEntity> extends FollowTargetGoal<T>
{
    private boolean enabled;
    
    public DisableableFollowTargetGoal(final RaiderEntity raiderEntity, final Class<T> class2, final int integer, final boolean boolean4, final boolean boolean5, @Nullable final Predicate<LivingEntity> predicate) {
        super(raiderEntity, class2, integer, boolean4, boolean5, predicate);
        this.enabled = true;
    }
    
    public void setEnabled(final boolean boolean1) {
        this.enabled = boolean1;
    }
    
    @Override
    public boolean canStart() {
        return this.enabled && super.canStart();
    }
}
