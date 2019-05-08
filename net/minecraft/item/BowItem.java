package net.minecraft.item;

import java.util.function.Predicate;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.util.Identifier;

public class BowItem extends BaseBowItem
{
    public BowItem(final Settings settings) {
        super(settings);
        this.addProperty(new Identifier("pull"), (itemStack, world, livingEntity) -> {
            if (livingEntity == null) {
                return 0.0f;
            }
            else if (livingEntity.getActiveItem().getItem() != Items.jf) {
                return 0.0f;
            }
            else {
                return (itemStack.getMaxUseTime() - livingEntity.getItemUseTimeLeft()) / 20.0f;
            }
        });
        this.addProperty(new Identifier("pulling"), (itemStack, world, livingEntity) -> (livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack) ? 1.0f : 0.0f);
    }
    
    @Override
    public void onItemStopUsing(final ItemStack stack, final World world, final LivingEntity player, final int integer) {
        if (!(player instanceof PlayerEntity)) {
            return;
        }
        final PlayerEntity playerEntity3 = (PlayerEntity)player;
        final boolean boolean6 = playerEntity3.abilities.creativeMode || EnchantmentHelper.getLevel(Enchantments.z, stack) > 0;
        ItemStack itemStack7 = playerEntity3.getArrowType(stack);
        if (itemStack7.isEmpty() && !boolean6) {
            return;
        }
        if (itemStack7.isEmpty()) {
            itemStack7 = new ItemStack(Items.jg);
        }
        final int integer2 = this.getMaxUseTime(stack) - integer;
        final float float9 = a(integer2);
        if (float9 < 0.1) {
            return;
        }
        final boolean boolean7 = boolean6 && itemStack7.getItem() == Items.jg;
        if (!world.isClient) {
            final ArrowItem arrowItem11 = (ArrowItem)((itemStack7.getItem() instanceof ArrowItem) ? itemStack7.getItem() : Items.jg);
            final ProjectileEntity projectileEntity12 = arrowItem11.createProjectile(world, itemStack7, playerEntity3);
            projectileEntity12.a(playerEntity3, playerEntity3.pitch, playerEntity3.yaw, 0.0f, float9 * 3.0f, 1.0f);
            if (float9 == 1.0f) {
                projectileEntity12.setCritical(true);
            }
            final int integer3 = EnchantmentHelper.getLevel(Enchantments.w, stack);
            if (integer3 > 0) {
                projectileEntity12.setDamage(projectileEntity12.getDamage() + integer3 * 0.5 + 0.5);
            }
            final int integer4 = EnchantmentHelper.getLevel(Enchantments.x, stack);
            if (integer4 > 0) {
                projectileEntity12.a(integer4);
            }
            if (EnchantmentHelper.getLevel(Enchantments.y, stack) > 0) {
                projectileEntity12.setOnFireFor(100);
            }
            stack.<PlayerEntity>applyDamage(1, playerEntity3, playerEntity2 -> playerEntity2.sendToolBreakStatus(playerEntity3.getActiveHand()));
            if (boolean7 || (playerEntity3.abilities.creativeMode && (itemStack7.getItem() == Items.oT || itemStack7.getItem() == Items.oU))) {
                projectileEntity12.pickupType = ProjectileEntity.PickupType.CREATIVE_PICKUP;
            }
            world.spawnEntity(projectileEntity12);
        }
        world.playSound(null, playerEntity3.x, playerEntity3.y, playerEntity3.z, SoundEvents.D, SoundCategory.h, 1.0f, 1.0f / (BowItem.random.nextFloat() * 0.4f + 1.2f) + float9 * 0.5f);
        if (!boolean7 && !playerEntity3.abilities.creativeMode) {
            itemStack7.subtractAmount(1);
            if (itemStack7.isEmpty()) {
                playerEntity3.inventory.removeOne(itemStack7);
            }
        }
        playerEntity3.incrementStat(Stats.c.getOrCreateStat(this));
    }
    
    public static float a(final int integer) {
        float float2 = integer / 20.0f;
        float2 = (float2 * float2 + float2 * 2.0f) / 3.0f;
        if (float2 > 1.0f) {
            float2 = 1.0f;
        }
        return float2;
    }
    
    @Override
    public int getMaxUseTime(final ItemStack stack) {
        return 72000;
    }
    
    @Override
    public UseAction getUseAction(final ItemStack stack) {
        return UseAction.e;
    }
    
    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack4 = player.getStackInHand(hand);
        final boolean boolean5 = !player.getArrowType(itemStack4).isEmpty();
        if (player.abilities.creativeMode || boolean5) {
            player.setCurrentHand(hand);
            return new TypedActionResult<ItemStack>(ActionResult.a, itemStack4);
        }
        if (boolean5) {
            return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack4);
        }
        return new TypedActionResult<ItemStack>(ActionResult.c, itemStack4);
    }
    
    @Override
    public Predicate<ItemStack> getInventoryProjectilePredicate() {
        return BowItem.IS_BOW_PROJECTILE;
    }
}
