package net.minecraft.item;

import net.minecraft.potion.Potion;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.DefaultedList;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.TextComponent;
import javax.annotation.Nullable;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import java.util.Iterator;
import java.util.List;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;

public class PotionItem extends Item
{
    public PotionItem(final Settings settings) {
        super(settings);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public ItemStack getDefaultStack() {
        return PotionUtil.setPotion(super.getDefaultStack(), Potions.b);
    }
    
    @Override
    public ItemStack onItemFinishedUsing(final ItemStack stack, final World world, final LivingEntity livingEntity) {
        final PlayerEntity playerEntity4 = (livingEntity instanceof PlayerEntity) ? ((PlayerEntity)livingEntity) : null;
        if (playerEntity4 == null || !playerEntity4.abilities.creativeMode) {
            stack.subtractAmount(1);
        }
        if (playerEntity4 instanceof ServerPlayerEntity) {
            Criterions.CONSUME_ITEM.handle((ServerPlayerEntity)playerEntity4, stack);
        }
        if (!world.isClient) {
            final List<StatusEffectInstance> list5 = PotionUtil.getPotionEffects(stack);
            for (final StatusEffectInstance statusEffectInstance7 : list5) {
                if (statusEffectInstance7.getEffectType().isInstant()) {
                    statusEffectInstance7.getEffectType().applyInstantEffect(playerEntity4, playerEntity4, livingEntity, statusEffectInstance7.getAmplifier(), 1.0);
                }
                else {
                    livingEntity.addPotionEffect(new StatusEffectInstance(statusEffectInstance7));
                }
            }
        }
        if (playerEntity4 != null) {
            playerEntity4.incrementStat(Stats.c.getOrCreateStat(this));
        }
        if (playerEntity4 == null || !playerEntity4.abilities.creativeMode) {
            if (stack.isEmpty()) {
                return new ItemStack(Items.mm);
            }
            if (playerEntity4 != null) {
                playerEntity4.inventory.insertStack(new ItemStack(Items.mm));
            }
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
    
    @Override
    public String getTranslationKey(final ItemStack stack) {
        return PotionUtil.getPotion(stack).getName(this.getTranslationKey() + ".effect.");
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void buildTooltip(final ItemStack stack, @Nullable final World world, final List<TextComponent> tooltip, final TooltipContext options) {
        PotionUtil.buildTooltip(stack, tooltip, 1.0f);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean hasEnchantmentGlint(final ItemStack stack) {
        return super.hasEnchantmentGlint(stack) || !PotionUtil.getPotionEffects(stack).isEmpty();
    }
    
    @Override
    public void appendItemsForGroup(final ItemGroup itemGroup, final DefaultedList<ItemStack> defaultedList) {
        if (this.isInItemGroup(itemGroup)) {
            for (final Potion potion4 : Registry.POTION) {
                if (potion4 != Potions.a) {
                    defaultedList.add(PotionUtil.setPotion(new ItemStack(this), potion4));
                }
            }
        }
    }
}
