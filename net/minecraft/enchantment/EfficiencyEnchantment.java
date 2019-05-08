package net.minecraft.enchantment;

import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EquipmentSlot;

public class EfficiencyEnchantment extends Enchantment
{
    protected EfficiencyEnchantment(final Weight weight, final EquipmentSlot... slotTypes) {
        super(weight, EnchantmentTarget.BREAKER, slotTypes);
    }
    
    @Override
    public int getMinimumPower(final int integer) {
        return 1 + 10 * (integer - 1);
    }
    
    @Override
    public int getMaximumLevel() {
        return 5;
    }
    
    @Override
    public boolean isAcceptableItem(final ItemStack stack) {
        return stack.getItem() == Items.lW || super.isAcceptableItem(stack);
    }
}
