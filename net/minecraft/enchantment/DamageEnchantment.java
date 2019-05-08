package net.minecraft.enchantment;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;

public class DamageEnchantment extends Enchantment
{
    private static final String[] typeNames;
    private static final int[] e;
    private static final int[] f;
    private static final int[] g;
    public final int typeIndex;
    
    public DamageEnchantment(final Weight weight, final int typeIndex, final EquipmentSlot... slots) {
        super(weight, EnchantmentTarget.WEAPON, slots);
        this.typeIndex = typeIndex;
    }
    
    @Override
    public int getMinimumPower(final int integer) {
        return DamageEnchantment.e[this.typeIndex] + (integer - 1) * DamageEnchantment.f[this.typeIndex];
    }
    
    @Override
    public int getMaximumLevel() {
        return 5;
    }
    
    @Override
    public float getAttackDamage(final int level, final EntityGroup group) {
        if (this.typeIndex == 0) {
            return 1.0f + Math.max(0, level - 1) * 0.5f;
        }
        if (this.typeIndex == 1 && group == EntityGroup.UNDEAD) {
            return level * 2.5f;
        }
        if (this.typeIndex == 2 && group == EntityGroup.ARTHROPOD) {
            return level * 2.5f;
        }
        return 0.0f;
    }
    
    public boolean differs(final Enchantment other) {
        return !(other instanceof DamageEnchantment);
    }
    
    @Override
    public boolean isAcceptableItem(final ItemStack stack) {
        return stack.getItem() instanceof AxeItem || super.isAcceptableItem(stack);
    }
    
    @Override
    public void onTargetDamaged(final LivingEntity user, final Entity target, final int level) {
        if (target instanceof LivingEntity) {
            final LivingEntity livingEntity4 = (LivingEntity)target;
            if (this.typeIndex == 2 && livingEntity4.getGroup() == EntityGroup.ARTHROPOD) {
                final int integer5 = 20 + user.getRand().nextInt(10 * level);
                livingEntity4.addPotionEffect(new StatusEffectInstance(StatusEffects.b, integer5, 3));
            }
        }
    }
    
    static {
        typeNames = new String[] { "all", "undead", "arthropods" };
        e = new int[] { 1, 5, 5 };
        f = new int[] { 11, 8, 8 };
        g = new int[] { 20, 20, 20 };
    }
}
