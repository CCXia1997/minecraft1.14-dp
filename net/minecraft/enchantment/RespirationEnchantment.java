package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class RespirationEnchantment extends Enchantment
{
    public RespirationEnchantment(final Weight weight, final EquipmentSlot... slotTypes) {
        super(weight, EnchantmentTarget.HELM, slotTypes);
    }
    
    @Override
    public int getMinimumPower(final int integer) {
        return 10 * integer;
    }
    
    @Override
    public int getMaximumLevel() {
        return 3;
    }
}
