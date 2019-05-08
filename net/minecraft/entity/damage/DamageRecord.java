package net.minecraft.entity.damage;

import net.minecraft.text.TextComponent;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;

public class DamageRecord
{
    private final DamageSource damageSource;
    private final int entityAge;
    private final float damage;
    private final float entityHealth;
    private final String fallDeathSuffix;
    private final float fallDistance;
    
    public DamageRecord(final DamageSource damageSource, final int entityAge, final float entityOriginalHealth, final float damage, final String fallDeathSuffix, final float float6) {
        this.damageSource = damageSource;
        this.entityAge = entityAge;
        this.damage = damage;
        this.entityHealth = entityOriginalHealth;
        this.fallDeathSuffix = fallDeathSuffix;
        this.fallDistance = float6;
    }
    
    public DamageSource getDamageSource() {
        return this.damageSource;
    }
    
    public float getDamage() {
        return this.damage;
    }
    
    public boolean isAttackerLiving() {
        return this.damageSource.getAttacker() instanceof LivingEntity;
    }
    
    @Nullable
    public String getFallDeathSuffix() {
        return this.fallDeathSuffix;
    }
    
    @Nullable
    public TextComponent getAttackerName() {
        return (this.getDamageSource().getAttacker() == null) ? null : this.getDamageSource().getAttacker().getDisplayName();
    }
    
    public float getFallDistance() {
        if (this.damageSource == DamageSource.OUT_OF_WORLD) {
            return Float.MAX_VALUE;
        }
        return this.fallDistance;
    }
}
