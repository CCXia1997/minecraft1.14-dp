package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class InfinityEnchantment extends Enchantment
{
    public InfinityEnchantment(final Weight weight, final EquipmentSlot... slotTypes) {
        super(weight, EnchantmentTarget.BOW, slotTypes);
    }
    
    @Override
    public int getMinimumPower(final int integer) {
        return 20;
    }
    
    @Override
    public int getMaximumLevel() {
        return 1;
    }
    
    public boolean differs(final Enchantment other) {
        return !(other instanceof MendingEnchantment) && super.differs(other);
    }
}
