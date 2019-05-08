package net.minecraft.item;

import java.util.AbstractList;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.effect.StatusEffect;

public class SuspiciousStewItem extends Item
{
    public SuspiciousStewItem(final Settings settings) {
        super(settings);
    }
    
    public static void addEffectToStew(final ItemStack stew, final StatusEffect effect, final int duration) {
        final CompoundTag compoundTag4 = stew.getOrCreateTag();
        final ListTag listTag5 = compoundTag4.getList("Effects", 9);
        final CompoundTag compoundTag5 = new CompoundTag();
        compoundTag5.putByte("EffectId", (byte)StatusEffect.getRawId(effect));
        compoundTag5.putInt("EffectDuration", duration);
        ((AbstractList<CompoundTag>)listTag5).add(compoundTag5);
        compoundTag4.put("Effects", listTag5);
    }
    
    @Override
    public ItemStack onItemFinishedUsing(final ItemStack stack, final World world, final LivingEntity livingEntity) {
        super.onItemFinishedUsing(stack, world, livingEntity);
        final CompoundTag compoundTag4 = stack.getTag();
        if (compoundTag4 != null && compoundTag4.containsKey("Effects", 9)) {
            final ListTag listTag5 = compoundTag4.getList("Effects", 10);
            for (int integer6 = 0; integer6 < listTag5.size(); ++integer6) {
                int integer7 = 160;
                final CompoundTag compoundTag5 = listTag5.getCompoundTag(integer6);
                if (compoundTag5.containsKey("EffectDuration", 3)) {
                    integer7 = compoundTag5.getInt("EffectDuration");
                }
                final StatusEffect statusEffect9 = StatusEffect.byRawId(compoundTag5.getByte("EffectId"));
                if (statusEffect9 != null) {
                    livingEntity.addPotionEffect(new StatusEffectInstance(statusEffect9, integer7));
                }
            }
        }
        return new ItemStack(Items.jA);
    }
}
