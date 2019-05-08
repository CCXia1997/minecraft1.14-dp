package net.minecraft.item;

import net.minecraft.util.ActionResult;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.thrown.ThrownExperienceBottleEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class ExperienceBottleItem extends Item
{
    public ExperienceBottleItem(final Settings settings) {
        super(settings);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean hasEnchantmentGlint(final ItemStack stack) {
        return true;
    }
    
    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack4 = player.getStackInHand(hand);
        if (!player.abilities.creativeMode) {
            itemStack4.subtractAmount(1);
        }
        world.playSound(null, player.x, player.y, player.z, SoundEvents.dc, SoundCategory.g, 0.5f, 0.4f / (ExperienceBottleItem.random.nextFloat() * 0.4f + 0.8f));
        if (!world.isClient) {
            final ThrownExperienceBottleEntity thrownExperienceBottleEntity5 = new ThrownExperienceBottleEntity(world, player);
            thrownExperienceBottleEntity5.setItem(itemStack4);
            thrownExperienceBottleEntity5.a(player, player.pitch, player.yaw, -20.0f, 0.7f, 1.0f);
            world.spawnEntity(thrownExperienceBottleEntity5);
        }
        player.incrementStat(Stats.c.getOrCreateStat(this));
        return new TypedActionResult<ItemStack>(ActionResult.a, itemStack4);
    }
}
