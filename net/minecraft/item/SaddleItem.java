package net.minecraft.item;

import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.Hand;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class SaddleItem extends Item
{
    public SaddleItem(final Settings settings) {
        super(settings);
    }
    
    @Override
    public boolean interactWithEntity(final ItemStack stack, final PlayerEntity player, final LivingEntity target, final Hand hand) {
        if (target instanceof PigEntity) {
            final PigEntity pigEntity5 = (PigEntity)target;
            if (pigEntity5.isAlive() && !pigEntity5.isSaddled() && !pigEntity5.isChild()) {
                pigEntity5.setSaddled(true);
                pigEntity5.world.playSound(player, pigEntity5.x, pigEntity5.y, pigEntity5.z, SoundEvents.iq, SoundCategory.g, 0.5f, 1.0f);
                stack.subtractAmount(1);
            }
            return true;
        }
        return false;
    }
}
