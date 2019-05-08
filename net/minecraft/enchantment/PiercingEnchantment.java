package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class PiercingEnchantment extends Enchantment
{
    public PiercingEnchantment(final Weight weight, final EquipmentSlot... slotTypes) {
        super(weight, EnchantmentTarget.CROSSBOW, slotTypes);
    }
    
    @Override
    public int getMinimumPower(final int integer) {
        return 1 + (integer - 1) * 10;
    }
    
    @Override
    public int getMaximumLevel() {
        return 4;
    }
    
    public boolean differs(final Enchantment other) {
        return super.differs(other) && other != Enchantments.G;
    }
}
