package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class MultishotEnchantment extends Enchantment
{
    public MultishotEnchantment(final Weight weight, final EquipmentSlot... slotTypes) {
        super(weight, EnchantmentTarget.CROSSBOW, slotTypes);
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
        return super.differs(other) && other != Enchantments.I;
    }
}
