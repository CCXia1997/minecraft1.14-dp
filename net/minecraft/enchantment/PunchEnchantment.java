package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class PunchEnchantment extends Enchantment
{
    public PunchEnchantment(final Weight weight, final EquipmentSlot... slotTypes) {
        super(weight, EnchantmentTarget.BOW, slotTypes);
    }
    
    @Override
    public int getMinimumPower(final int integer) {
        return 12 + (integer - 1) * 20;
    }
    
    @Override
    public int getMaximumLevel() {
        return 2;
    }
}
