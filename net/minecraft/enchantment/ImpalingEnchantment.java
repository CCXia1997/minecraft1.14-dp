package net.minecraft.enchantment;

import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;

public class ImpalingEnchantment extends Enchantment
{
    public ImpalingEnchantment(final Weight weight, final EquipmentSlot... slotTypes) {
        super(weight, EnchantmentTarget.TRIDENT, slotTypes);
    }
    
    @Override
    public int getMinimumPower(final int integer) {
        return 1 + (integer - 1) * 8;
    }
    
    @Override
    public int getMaximumLevel() {
        return 5;
    }
    
    @Override
    public float getAttackDamage(final int level, final EntityGroup group) {
        if (group == EntityGroup.AQUATIC) {
            return level * 2.5f;
        }
        return 0.0f;
    }
}
