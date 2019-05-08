package net.minecraft.item;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Hand;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class NameTagItem extends Item
{
    public NameTagItem(final Settings settings) {
        super(settings);
    }
    
    @Override
    public boolean interactWithEntity(final ItemStack stack, final PlayerEntity player, final LivingEntity target, final Hand hand) {
        if (stack.hasDisplayName() && !(target instanceof PlayerEntity)) {
            if (target.isAlive()) {
                target.setCustomName(stack.getDisplayName());
                if (target instanceof MobEntity) {
                    ((MobEntity)target).setPersistent();
                }
                stack.subtractAmount(1);
            }
            return true;
        }
        return false;
    }
}
