package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class FireAspectEnchantment extends Enchantment
{
    protected FireAspectEnchantment(final Weight weight, final EquipmentSlot... slotTypes) {
        super(weight, EnchantmentTarget.WEAPON, slotTypes);
    }
    
    @Override
    public int getMinimumPower(final int integer) {
        return 10 + 20 * (integer - 1);
    }
    
    @Override
    public int getMaximumLevel() {
        return 2;
    }
}
