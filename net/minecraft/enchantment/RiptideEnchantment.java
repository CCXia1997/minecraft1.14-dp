package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class RiptideEnchantment extends Enchantment
{
    public RiptideEnchantment(final Weight weight, final EquipmentSlot... slotTypes) {
        super(weight, EnchantmentTarget.TRIDENT, slotTypes);
    }
    
    @Override
    public int getMinimumPower(final int integer) {
        return 10 + integer * 7;
    }
    
    @Override
    public int getMaximumLevel() {
        return 3;
    }
    
    public boolean differs(final Enchantment other) {
        return super.differs(other) && other != Enchantments.C && other != Enchantments.F;
    }
}
