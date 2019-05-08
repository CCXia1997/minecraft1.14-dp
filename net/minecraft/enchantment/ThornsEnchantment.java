package net.minecraft.enchantment;

import java.util.Map;
import java.util.Random;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EquipmentSlot;

public class ThornsEnchantment extends Enchantment
{
    public ThornsEnchantment(final Weight weight, final EquipmentSlot... slotTypes) {
        super(weight, EnchantmentTarget.CHEST, slotTypes);
    }
    
    @Override
    public int getMinimumPower(final int integer) {
        return 10 + 20 * (integer - 1);
    }
    
    @Override
    public int getMaximumLevel() {
        return 3;
    }
    
    @Override
    public boolean isAcceptableItem(final ItemStack stack) {
        return stack.getItem() instanceof ArmorItem || super.isAcceptableItem(stack);
    }
    
    @Override
    public void onUserDamaged(final LivingEntity user, final Entity attacker, final int level) {
        final Random random4 = user.getRand();
        final Map.Entry<EquipmentSlot, ItemStack> entry5 = EnchantmentHelper.getRandomEnchantedEquipment(Enchantments.h, user);
        if (shouldDamageAttacker(level, random4)) {
            if (attacker != null) {
                attacker.damage(DamageSource.thorns(user), (float)getDamageAmount(level, random4));
            }
            if (entry5 != null) {
                entry5.getValue().<LivingEntity>applyDamage(3, user, livingEntity -> livingEntity.sendEquipmentBreakStatus(entry5.getKey()));
            }
        }
        else if (entry5 != null) {
            entry5.getValue().<LivingEntity>applyDamage(1, user, livingEntity -> livingEntity.sendEquipmentBreakStatus(entry5.getKey()));
        }
    }
    
    public static boolean shouldDamageAttacker(final int level, final Random random) {
        return level > 0 && random.nextFloat() < 0.15f * level;
    }
    
    public static int getDamageAmount(final int level, final Random random) {
        if (level > 10) {
            return level - 10;
        }
        return 1 + random.nextInt(4);
    }
}
