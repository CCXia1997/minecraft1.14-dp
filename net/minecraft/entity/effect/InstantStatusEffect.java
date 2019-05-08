package net.minecraft.entity.effect;

public class InstantStatusEffect extends StatusEffect
{
    public InstantStatusEffect(final StatusEffectType statusEffectType, final int integer) {
        super(statusEffectType, integer);
    }
    
    @Override
    public boolean isInstant() {
        return true;
    }
    
    @Override
    public boolean canApplyUpdateEffect(final int duration, final int integer2) {
        return duration >= 1;
    }
}
