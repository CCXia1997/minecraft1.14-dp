package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class KnockbackEnchantment extends Enchantment
{
    protected KnockbackEnchantment(final Weight weight, final EquipmentSlot... slot) {
        super(weight, EnchantmentTarget.WEAPON, slot);
    }
    
    @Override
    public int getMinimumPower(final int integer) {
        return 5 + 20 * (integer - 1);
    }
    
    @Override
    public int getMaximumLevel() {
        return 2;
    }
}
