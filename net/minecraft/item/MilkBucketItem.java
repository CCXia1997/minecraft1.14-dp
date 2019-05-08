package net.minecraft.item;

import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public class MilkBucketItem extends Item
{
    public MilkBucketItem(final Settings settings) {
        super(settings);
    }
    
    @Override
    public ItemStack onItemFinishedUsing(final ItemStack stack, final World world, final LivingEntity livingEntity) {
        if (livingEntity instanceof ServerPlayerEntity) {
            final ServerPlayerEntity serverPlayerEntity4 = (ServerPlayerEntity)livingEntity;
            Criterions.CONSUME_ITEM.handle(serverPlayerEntity4, stack);
            serverPlayerEntity4.incrementStat(Stats.c.getOrCreateStat(this));
        }
        if (livingEntity instanceof PlayerEntity && !((PlayerEntity)livingEntity).abilities.creativeMode) {
            stack.subtractAmount(1);
        }
        if (!world.isClient) {
            livingEntity.clearPotionEffects();
        }
        if (stack.isEmpty()) {
            return new ItemStack(Items.kx);
        }
        return stack;
    }
    
    @Override
    public int getMaxUseTime(final ItemStack stack) {
        return 32;
    }
    
    @Override
    public UseAction getUseAction(final ItemStack stack) {
        return UseAction.c;
    }
    
    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        player.setCurrentHand(hand);
        return new TypedActionResult<ItemStack>(ActionResult.a, player.getStackInHand(hand));
    }
}
