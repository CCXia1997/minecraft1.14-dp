package net.minecraft.enchantment;

import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.EquipmentSlot;

public class ProtectionEnchantment extends Enchantment
{
    public final Type type;
    
    public ProtectionEnchantment(final Weight weight, final Type type, final EquipmentSlot... slotTypes) {
        super(weight, EnchantmentTarget.ARMOR, slotTypes);
        this.type = type;
        if (type == Type.FALL) {
            this.type = EnchantmentTarget.FEET;
        }
    }
    
    @Override
    public int getMinimumPower(final int integer) {
        return this.type.getBasePower() + (integer - 1) * this.type.getPowerPerLevel();
    }
    
    @Override
    public int getMaximumLevel() {
        return 4;
    }
    
    @Override
    public int getProtectionAmount(final int level, final DamageSource source) {
        if (source.doesDamageToCreative()) {
            return 0;
        }
        if (this.type == Type.ALL) {
            return level;
        }
        if (this.type == Type.FIRE && source.isFire()) {
            return level * 2;
        }
        if (this.type == Type.FALL && source == DamageSource.FALL) {
            return level * 3;
        }
        if (this.type == Type.EXPLOSION && source.isExplosive()) {
            return level * 2;
        }
        if (this.type == Type.PROJECTILE && source.isProjectile()) {
            return level * 2;
        }
        return 0;
    }
    
    public boolean differs(final Enchantment other) {
        if (other instanceof ProtectionEnchantment) {
            final ProtectionEnchantment protectionEnchantment2 = (ProtectionEnchantment)other;
            return this.type != protectionEnchantment2.type;
        }
        return super.differs(other);
    }
    
    public static int transformFireDuration(final LivingEntity entity, int duration) {
        final int integer3 = EnchantmentHelper.getEquipmentLevel(Enchantments.b, entity);
        if (integer3 > 0) {
            duration -= MathHelper.floor(duration * (integer3 * 0.15f));
        }
        return duration;
    }
    
    public static double transformExplosionKnockback(final LivingEntity entity, double velocity) {
        final int integer4 = EnchantmentHelper.getEquipmentLevel(Enchantments.d, entity);
        if (integer4 > 0) {
            velocity -= MathHelper.floor(velocity * (integer4 * 0.15f));
        }
        return velocity;
    }
    
    public enum Type
    {
        ALL("all", 1, 11), 
        FIRE("fire", 10, 8), 
        FALL("fall", 5, 6), 
        EXPLOSION("explosion", 5, 8), 
        PROJECTILE("projectile", 3, 6);
        
        private final String name;
        private final int basePower;
        private final int powerPerLevel;
        
        private Type(final String name, final int integer2, final int integer3) {
            this.name = name;
            this.basePower = integer2;
            this.powerPerLevel = integer3;
        }
        
        public int getBasePower() {
            return this.basePower;
        }
        
        public int getPowerPerLevel() {
            return this.powerPerLevel;
        }
    }
}
