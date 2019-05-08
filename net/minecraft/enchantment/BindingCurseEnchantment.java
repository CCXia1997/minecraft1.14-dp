package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class BindingCurseEnchantment extends Enchantment
{
    public BindingCurseEnchantment(final Weight weight, final EquipmentSlot... slotTypes) {
        super(weight, EnchantmentTarget.WEARABLE, slotTypes);
    }
    
    @Override
    public int getMinimumPower(final int integer) {
        return 25;
    }
    
    @Override
    public int getMaximumLevel() {
        return 1;
    }
    
    @Override
    public boolean isTreasure() {
        return true;
    }
    
    @Override
    public boolean isCursed() {
        return true;
    }
}
