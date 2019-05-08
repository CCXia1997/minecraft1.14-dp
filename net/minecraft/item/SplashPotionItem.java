package net.minecraft.item;

import net.minecraft.util.ActionResult;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.thrown.ThrownPotionEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class SplashPotionItem extends PotionItem
{
    public SplashPotionItem(final Settings settings) {
        super(settings);
    }
    
    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack4 = player.getStackInHand(hand);
        final ItemStack itemStack5 = player.abilities.creativeMode ? itemStack4.copy() : itemStack4.split(1);
        world.playSound(null, player.x, player.y, player.z, SoundEvents.lh, SoundCategory.h, 0.5f, 0.4f / (SplashPotionItem.random.nextFloat() * 0.4f + 0.8f));
        if (!world.isClient) {
            final ThrownPotionEntity thrownPotionEntity6 = new ThrownPotionEntity(world, player);
            thrownPotionEntity6.setItemStack(itemStack5);
            thrownPotionEntity6.a(player, player.pitch, player.yaw, -20.0f, 0.5f, 1.0f);
            world.spawnEntity(thrownPotionEntity6);
        }
        player.incrementStat(Stats.c.getOrCreateStat(this));
        return new TypedActionResult<ItemStack>(ActionResult.a, itemStack4);
    }
}
