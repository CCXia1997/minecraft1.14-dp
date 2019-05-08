package net.minecraft.entity.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;

public class DamageModifierStatusEffect extends StatusEffect
{
    protected final double modifier;
    
    protected DamageModifierStatusEffect(final StatusEffectType statusEffectType, final int color, final double double3) {
        super(statusEffectType, color);
        this.modifier = double3;
    }
    
    @Override
    public double a(final int integer, final EntityAttributeModifier entityAttributeModifier) {
        return this.modifier * (integer + 1);
    }
}
