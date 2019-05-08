package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class ChannelingEnchantment extends Enchantment
{
    public ChannelingEnchantment(final Weight weight, final EquipmentSlot... slotTypes) {
        super(weight, EnchantmentTarget.TRIDENT, slotTypes);
    }
    
    @Override
    public int getMinimumPower(final int integer) {
        return 25;
    }
    
    @Override
    public int getMaximumLevel() {
        return 1;
    }
    
    public boolean differs(final Enchantment other) {
        return super.differs(other);
    }
}
