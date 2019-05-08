package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class AquaAffinityEnchantment extends Enchantment
{
    public AquaAffinityEnchantment(final Weight weight, final EquipmentSlot... slotTypes) {
        super(weight, EnchantmentTarget.HELM, slotTypes);
    }
    
    @Override
    public int getMinimumPower(final int integer) {
        return 1;
    }
    
    @Override
    public int getMaximumLevel() {
        return 1;
    }
}
