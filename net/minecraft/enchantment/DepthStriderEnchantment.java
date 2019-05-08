package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class DepthStriderEnchantment extends Enchantment
{
    public DepthStriderEnchantment(final Weight weight, final EquipmentSlot... slotTypes) {
        super(weight, EnchantmentTarget.FEET, slotTypes);
    }
    
    @Override
    public int getMinimumPower(final int integer) {
        return integer * 10;
    }
    
    @Override
    public int getMaximumLevel() {
        return 3;
    }
    
    public boolean differs(final Enchantment other) {
        return super.differs(other) && other != Enchantments.j;
    }
}
