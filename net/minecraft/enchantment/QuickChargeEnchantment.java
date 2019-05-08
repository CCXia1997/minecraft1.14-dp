package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class QuickChargeEnchantment extends Enchantment
{
    public QuickChargeEnchantment(final Weight weight, final EquipmentSlot... slot) {
        super(weight, EnchantmentTarget.CROSSBOW, slot);
    }
    
    @Override
    public int getMinimumPower(final int integer) {
        return 12 + (integer - 1) * 20;
    }
    
    @Override
    public int getMaximumLevel() {
        return 3;
    }
}
