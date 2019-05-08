package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public class ChorusFruitItem extends Item
{
    public ChorusFruitItem(final Settings settings) {
        super(settings);
    }
    
    @Override
    public ItemStack onItemFinishedUsing(final ItemStack stack, final World world, final LivingEntity livingEntity) {
        final ItemStack itemStack4 = super.onItemFinishedUsing(stack, world, livingEntity);
        if (!world.isClient) {
            final double double5 = livingEntity.x;
            final double double6 = livingEntity.y;
            final double double7 = livingEntity.z;
            for (int integer11 = 0; integer11 < 16; ++integer11) {
                final double double8 = livingEntity.x + (livingEntity.getRand().nextDouble() - 0.5) * 16.0;
                final double double9 = MathHelper.clamp(livingEntity.y + (livingEntity.getRand().nextInt(16) - 8), 0.0, world.getEffectiveHeight() - 1);
                final double double10 = livingEntity.z + (livingEntity.getRand().nextDouble() - 0.5) * 16.0;
                if (livingEntity.hasVehicle()) {
                    livingEntity.stopRiding();
                }
                if (livingEntity.teleport(double8, double9, double10, true)) {
                    world.playSound(null, double5, double6, double7, SoundEvents.aW, SoundCategory.h, 1.0f, 1.0f);
                    livingEntity.playSound(SoundEvents.aW, 1.0f, 1.0f);
                    break;
                }
            }
            if (livingEntity instanceof PlayerEntity) {
                ((PlayerEntity)livingEntity).getItemCooldownManager().set(this, 20);
            }
        }
        return itemStack4;
    }
}
