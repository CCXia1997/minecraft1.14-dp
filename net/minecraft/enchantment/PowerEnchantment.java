package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class PowerEnchantment extends Enchantment
{
    public PowerEnchantment(final Weight weight, final EquipmentSlot... slotTypes) {
        super(weight, EnchantmentTarget.BOW, slotTypes);
    }
    
    @Override
    public int getMinimumPower(final int integer) {
        return 1 + (integer - 1) * 10;
    }
    
    @Override
    public int getMaximumLevel() {
        return 5;
    }
}
