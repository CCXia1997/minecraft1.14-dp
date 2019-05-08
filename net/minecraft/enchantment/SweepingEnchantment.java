package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class SweepingEnchantment extends Enchantment
{
    public SweepingEnchantment(final Weight weight, final EquipmentSlot... slotTypes) {
        super(weight, EnchantmentTarget.WEAPON, slotTypes);
    }
    
    @Override
    public int getMinimumPower(final int integer) {
        return 5 + (integer - 1) * 9;
    }
    
    @Override
    public int getMaximumLevel() {
        return 3;
    }
    
    public static float getMultiplier(final int integer) {
        return 1.0f - 1.0f / (integer + 1);
    }
}
