package net.minecraft.item;

import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import com.google.common.collect.Multimap;
import net.minecraft.world.BlockView;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Hand;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.UseAction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;

public class TridentItem extends Item
{
    public TridentItem(final Settings settings) {
        super(settings);
        this.addProperty(new Identifier("throwing"), (itemStack, world, livingEntity) -> (livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack) ? 1.0f : 0.0f);
    }
    
    @Override
    public boolean beforeBlockBreak(final BlockState blockState, final World world, final BlockPos position, final PlayerEntity player) {
        return !player.isCreative();
    }
    
    @Override
    public UseAction getUseAction(final ItemStack stack) {
        return UseAction.f;
    }
    
    @Override
    public int getMaxUseTime(final ItemStack stack) {
        return 72000;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean hasEnchantmentGlint(final ItemStack stack) {
        return false;
    }
    
    @Override
    public void onItemStopUsing(final ItemStack stack, final World world, final LivingEntity player, final int integer) {
        if (!(player instanceof PlayerEntity)) {
            return;
        }
        final PlayerEntity playerEntity2 = (PlayerEntity)player;
        final int integer2 = this.getMaxUseTime(stack) - integer;
        if (integer2 < 10) {
            return;
        }
        final int integer3 = EnchantmentHelper.getRiptide(stack);
        if (integer3 > 0 && !playerEntity2.isInsideWaterOrRain()) {
            return;
        }
        if (!world.isClient) {
            stack.<PlayerEntity>applyDamage(1, playerEntity2, playerEntity -> playerEntity.sendToolBreakStatus(player.getActiveHand()));
            if (integer3 == 0) {
                final TridentEntity tridentEntity8 = new TridentEntity(world, playerEntity2, stack);
                tridentEntity8.a(playerEntity2, playerEntity2.pitch, playerEntity2.yaw, 0.0f, 2.5f + integer3 * 0.5f, 1.0f);
                if (playerEntity2.abilities.creativeMode) {
                    tridentEntity8.pickupType = ProjectileEntity.PickupType.CREATIVE_PICKUP;
                }
                world.spawnEntity(tridentEntity8);
                world.playSoundFromEntity(null, tridentEntity8, SoundEvents.lL, SoundCategory.h, 1.0f, 1.0f);
                if (!playerEntity2.abilities.creativeMode) {
                    playerEntity2.inventory.removeOne(stack);
                }
            }
        }
        playerEntity2.incrementStat(Stats.c.getOrCreateStat(this));
        if (integer3 > 0) {
            final float float8 = playerEntity2.yaw;
            final float float9 = playerEntity2.pitch;
            float float10 = -MathHelper.sin(float8 * 0.017453292f) * MathHelper.cos(float9 * 0.017453292f);
            float float11 = -MathHelper.sin(float9 * 0.017453292f);
            float float12 = MathHelper.cos(float8 * 0.017453292f) * MathHelper.cos(float9 * 0.017453292f);
            final float float13 = MathHelper.sqrt(float10 * float10 + float11 * float11 + float12 * float12);
            final float float14 = 3.0f * ((1.0f + integer3) / 4.0f);
            float10 *= float14 / float13;
            float11 *= float14 / float13;
            float12 *= float14 / float13;
            playerEntity2.addVelocity(float10, float11, float12);
            playerEntity2.p(20);
            if (playerEntity2.onGround) {
                final float float15 = 1.1999999f;
                playerEntity2.move(MovementType.a, new Vec3d(0.0, 1.1999999284744263, 0.0));
            }
            SoundEvent soundEvent15;
            if (integer3 >= 3) {
                soundEvent15 = SoundEvents.lK;
            }
            else if (integer3 == 2) {
                soundEvent15 = SoundEvents.lJ;
            }
            else {
                soundEvent15 = SoundEvents.lI;
            }
            world.playSoundFromEntity(null, playerEntity2, soundEvent15, SoundCategory.h, 1.0f, 1.0f);
        }
    }
    
    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack4 = player.getStackInHand(hand);
        if (itemStack4.getDamage() >= itemStack4.getDurability()) {
            return new TypedActionResult<ItemStack>(ActionResult.c, itemStack4);
        }
        if (EnchantmentHelper.getRiptide(itemStack4) > 0 && !player.isInsideWaterOrRain()) {
            return new TypedActionResult<ItemStack>(ActionResult.c, itemStack4);
        }
        player.setCurrentHand(hand);
        return new TypedActionResult<ItemStack>(ActionResult.a, itemStack4);
    }
    
    @Override
    public boolean onEntityDamaged(final ItemStack stack, final LivingEntity target, final LivingEntity livingEntity) {
        stack.<LivingEntity>applyDamage(1, livingEntity, livingEntity -> livingEntity.sendEquipmentBreakStatus(EquipmentSlot.HAND_MAIN));
        return true;
    }
    
    @Override
    public boolean onBlockBroken(final ItemStack stack, final World world, final BlockState state, final BlockPos pos, final LivingEntity livingEntity) {
        if (state.getHardness(world, pos) != 0.0) {
            stack.<LivingEntity>applyDamage(2, livingEntity, livingEntity -> livingEntity.sendEquipmentBreakStatus(EquipmentSlot.HAND_MAIN));
        }
        return true;
    }
    
    @Override
    public Multimap<String, EntityAttributeModifier> getAttributeModifiers(final EquipmentSlot equiptmentSlot) {
        final Multimap<String, EntityAttributeModifier> multimap2 = super.getAttributeModifiers(equiptmentSlot);
        if (equiptmentSlot == EquipmentSlot.HAND_MAIN) {
            multimap2.put(EntityAttributes.ATTACK_DAMAGE.getId(), new EntityAttributeModifier(TridentItem.MODIFIER_DAMAGE, "Tool modifier", 8.0, EntityAttributeModifier.Operation.a));
            multimap2.put(EntityAttributes.ATTACK_SPEED.getId(), new EntityAttributeModifier(TridentItem.MODIFIER_SWING_SPEED, "Tool modifier", -2.9000000953674316, EntityAttributeModifier.Operation.a));
        }
        return multimap2;
    }
    
    @Override
    public int getEnchantability() {
        return 1;
    }
}
