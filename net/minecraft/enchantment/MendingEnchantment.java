package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class MendingEnchantment extends Enchantment
{
    public MendingEnchantment(final Weight weight, final EquipmentSlot... slotTypes) {
        super(weight, EnchantmentTarget.TOOL, slotTypes);
    }
    
    @Override
    public int getMinimumPower(final int integer) {
        return integer * 25;
    }
    
    @Override
    public boolean isTreasure() {
        return true;
    }
    
    @Override
    public int getMaximumLevel() {
        return 1;
    }
}
