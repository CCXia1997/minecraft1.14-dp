package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public class MushroomStewItem extends Item
{
    public MushroomStewItem(final Settings settings) {
        super(settings);
    }
    
    @Override
    public ItemStack onItemFinishedUsing(final ItemStack stack, final World world, final LivingEntity livingEntity) {
        super.onItemFinishedUsing(stack, world, livingEntity);
        return new ItemStack(Items.jA);
    }
}
