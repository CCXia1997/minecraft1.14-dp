package net.minecraft.enchantment;

import net.minecraft.item.ArmorItem;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EquipmentSlot;

public class UnbreakingEnchantment extends Enchantment
{
    protected UnbreakingEnchantment(final Weight weight, final EquipmentSlot... slotTypes) {
        super(weight, EnchantmentTarget.TOOL, slotTypes);
    }
    
    @Override
    public int getMinimumPower(final int integer) {
        return 5 + (integer - 1) * 8;
    }
    
    @Override
    public int getMaximumLevel() {
        return 3;
    }
    
    @Override
    public boolean isAcceptableItem(final ItemStack stack) {
        return stack.hasDurability() || super.isAcceptableItem(stack);
    }
    
    public static boolean shouldPreventDamage(final ItemStack item, final int level, final Random random) {
        return (!(item.getItem() instanceof ArmorItem) || random.nextFloat() >= 0.6f) && random.nextInt(level + 1) > 0;
    }
}
