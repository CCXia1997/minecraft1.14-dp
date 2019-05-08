package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishHookEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class FishingRodItem extends Item
{
    public FishingRodItem(final Settings settings) {
        super(settings);
        boolean boolean4;
        boolean boolean5;
        this.addProperty(new Identifier("cast"), (itemStack, world, livingEntity) -> {
            if (livingEntity == null) {
                return 0.0f;
            }
            else {
                boolean4 = (livingEntity.getMainHandStack() == itemStack);
                boolean5 = (livingEntity.getOffHandStack() == itemStack);
                if (livingEntity.getMainHandStack().getItem() instanceof FishingRodItem) {
                    boolean5 = false;
                }
                return ((boolean4 || boolean5) && livingEntity instanceof PlayerEntity && livingEntity.fishHook != null) ? 1.0f : 0.0f;
            }
        });
    }
    
    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack4 = player.getStackInHand(hand);
        if (player.fishHook != null) {
            if (!world.isClient) {
                final int integer5 = player.fishHook.b(itemStack4);
                itemStack4.<PlayerEntity>applyDamage(integer5, player, playerEntity -> playerEntity.sendToolBreakStatus(hand));
            }
            player.swingHand(hand);
            world.playSound(null, player.x, player.y, player.z, SoundEvents.aj, SoundCategory.g, 1.0f, 0.4f / (FishingRodItem.random.nextFloat() * 0.4f + 0.8f));
        }
        else {
            world.playSound(null, player.x, player.y, player.z, SoundEvents.al, SoundCategory.g, 0.5f, 0.4f / (FishingRodItem.random.nextFloat() * 0.4f + 0.8f));
            if (!world.isClient) {
                final int integer5 = EnchantmentHelper.getLure(itemStack4);
                final int integer6 = EnchantmentHelper.getLuckOfTheSea(itemStack4);
                world.spawnEntity(new FishHookEntity(player, world, integer6, integer5));
            }
            player.swingHand(hand);
            player.incrementStat(Stats.c.getOrCreateStat(this));
        }
        return new TypedActionResult<ItemStack>(ActionResult.a, itemStack4);
    }
    
    @Override
    public int getEnchantability() {
        return 1;
    }
}
