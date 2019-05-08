package net.minecraft.entity.effect;

import net.minecraft.entity.attribute.AbstractEntityAttributeContainer;
import net.minecraft.entity.LivingEntity;

public class AbsorptionStatusEffect extends StatusEffect
{
    protected AbsorptionStatusEffect(final StatusEffectType statusEffectType, final int integer) {
        super(statusEffectType, integer);
    }
    
    @Override
    public void a(final LivingEntity livingEntity, final AbstractEntityAttributeContainer abstractEntityAttributeContainer, final int integer) {
        livingEntity.setAbsorptionAmount(livingEntity.getAbsorptionAmount() - 4 * (integer + 1));
        super.a(livingEntity, abstractEntityAttributeContainer, integer);
    }
    
    @Override
    public void b(final LivingEntity livingEntity, final AbstractEntityAttributeContainer abstractEntityAttributeContainer, final int integer) {
        livingEntity.setAbsorptionAmount(livingEntity.getAbsorptionAmount() + 4 * (integer + 1));
        super.b(livingEntity, abstractEntityAttributeContainer, integer);
    }
}
