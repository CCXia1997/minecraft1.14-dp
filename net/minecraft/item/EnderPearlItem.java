package net.minecraft.item;

import net.minecraft.util.ActionResult;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.thrown.ThrownEnderpearlEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class EnderPearlItem extends Item
{
    public EnderPearlItem(final Settings settings) {
        super(settings);
    }
    
    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack4 = player.getStackInHand(hand);
        if (!player.abilities.creativeMode) {
            itemStack4.subtractAmount(1);
        }
        world.playSound(null, player.x, player.y, player.z, SoundEvents.cP, SoundCategory.g, 0.5f, 0.4f / (EnderPearlItem.random.nextFloat() * 0.4f + 0.8f));
        player.getItemCooldownManager().set(this, 20);
        if (!world.isClient) {
            final ThrownEnderpearlEntity thrownEnderpearlEntity5 = new ThrownEnderpearlEntity(world, player);
            thrownEnderpearlEntity5.setItem(itemStack4);
            thrownEnderpearlEntity5.a(player, player.pitch, player.yaw, 0.0f, 1.5f, 1.0f);
            world.spawnEntity(thrownEnderpearlEntity5);
        }
        player.incrementStat(Stats.c.getOrCreateStat(this));
        return new TypedActionResult<ItemStack>(ActionResult.a, itemStack4);
    }
}
