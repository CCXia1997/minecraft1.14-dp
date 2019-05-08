package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;

public class GoToEntityGoal extends LookAtEntityGoal
{
    public GoToEntityGoal(final MobEntity owner, final Class<? extends LivingEntity> targetType, final float range, final float chance) {
        super(owner, targetType, range, chance);
        this.setControls(EnumSet.<Control>of(Control.b, Control.a));
    }
}
