package net.minecraft.entity.effect;

import net.minecraft.entity.attribute.AbstractEntityAttributeContainer;
import net.minecraft.entity.LivingEntity;

public class HealthBoostStatusEffect extends StatusEffect
{
    public HealthBoostStatusEffect(final StatusEffectType statusEffectType, final int integer) {
        super(statusEffectType, integer);
    }
    
    @Override
    public void a(final LivingEntity livingEntity, final AbstractEntityAttributeContainer abstractEntityAttributeContainer, final int integer) {
        super.a(livingEntity, abstractEntityAttributeContainer, integer);
        if (livingEntity.getHealth() > livingEntity.getHealthMaximum()) {
            livingEntity.setHealth(livingEntity.getHealthMaximum());
        }
    }
}
