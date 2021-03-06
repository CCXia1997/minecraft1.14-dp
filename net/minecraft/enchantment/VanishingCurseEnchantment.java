package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class VanishingCurseEnchantment extends Enchantment
{
    public VanishingCurseEnchantment(final Weight weight, final EquipmentSlot... slotTypes) {
        super(weight, EnchantmentTarget.ALL, slotTypes);
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
