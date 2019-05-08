package net.minecraft.item;

import net.minecraft.util.ActionResult;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class SnowballItem extends Item
{
    public SnowballItem(final Settings settings) {
        super(settings);
    }
    
    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack4 = player.getStackInHand(hand);
        if (!player.abilities.creativeMode) {
            itemStack4.subtractAmount(1);
        }
        world.playSound(null, player.x, player.y, player.z, SoundEvents.kS, SoundCategory.g, 0.5f, 0.4f / (SnowballItem.random.nextFloat() * 0.4f + 0.8f));
        if (!world.isClient) {
            final SnowballEntity snowballEntity5 = new SnowballEntity(world, player);
            snowballEntity5.setItem(itemStack4);
            snowballEntity5.a(player, player.pitch, player.yaw, 0.0f, 1.5f, 1.0f);
            world.spawnEntity(snowballEntity5);
        }
        player.incrementStat(Stats.c.getOrCreateStat(this));
        return new TypedActionResult<ItemStack>(ActionResult.a, itemStack4);
    }
}
