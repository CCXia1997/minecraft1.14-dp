package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class SilkTouchEnchantment extends Enchantment
{
    protected SilkTouchEnchantment(final Weight weight, final EquipmentSlot... slotTypes) {
        super(weight, EnchantmentTarget.BREAKER, slotTypes);
    }
    
    @Override
    public int getMinimumPower(final int integer) {
        return 15;
    }
    
    @Override
    public int getMaximumLevel() {
        return 1;
    }
    
    public boolean differs(final Enchantment other) {
        return super.differs(other) && other != Enchantments.v;
    }
}
