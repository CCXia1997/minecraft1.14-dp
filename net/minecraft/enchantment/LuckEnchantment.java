package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class LuckEnchantment extends Enchantment
{
    protected LuckEnchantment(final Weight weight, final EnchantmentTarget type, final EquipmentSlot... slotTypes) {
        super(weight, type, slotTypes);
    }
    
    @Override
    public int getMinimumPower(final int integer) {
        return 15 + (integer - 1) * 9;
    }
    
    @Override
    public int getMaximumLevel() {
        return 3;
    }
    
    public boolean differs(final Enchantment other) {
        return super.differs(other) && other != Enchantments.t;
    }
}
