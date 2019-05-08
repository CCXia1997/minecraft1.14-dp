package net.minecraft.item;

import java.util.AbstractList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Collection;
import net.minecraft.text.TextFormat;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.TextComponent;
import javax.annotation.Nullable;
import net.minecraft.util.UseAction;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Random;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.entity.Entity;
import net.minecraft.client.util.math.Quaternion;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.FireworkEntity;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import java.util.function.Predicate;
import net.minecraft.util.Identifier;

public class CrossbowItem extends BaseBowItem
{
    private boolean c;
    private boolean d;
    
    public CrossbowItem(final Settings settings) {
        super(settings);
        this.c = false;
        this.d = false;
        this.addProperty(new Identifier("pull"), (itemStack, world, livingEntity) -> {
            if (livingEntity == null || itemStack.getItem() != this) {
                return 0.0f;
            }
            else if (isCharged(itemStack)) {
                return 0.0f;
            }
            else {
                return (itemStack.getMaxUseTime() - livingEntity.getItemUseTimeLeft()) / (float)getPullTime(itemStack);
            }
        });
        this.addProperty(new Identifier("pulling"), (itemStack, world, livingEntity) -> (livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack && !isCharged(itemStack)) ? 1.0f : 0.0f);
        this.addProperty(new Identifier("charged"), (itemStack, world, livingEntity) -> (livingEntity != null && isCharged(itemStack)) ? 1.0f : 0.0f);
        this.addProperty(new Identifier("firework"), (itemStack, world, livingEntity) -> (livingEntity != null && isCharged(itemStack) && hasProjectile(itemStack, Items.nX)) ? 1.0f : 0.0f);
    }
    
    @Override
    public Predicate<ItemStack> getHeldProjectilePredicate() {
        return CrossbowItem.IS_CROSSBOW_PROJECTILE;
    }
    
    @Override
    public Predicate<ItemStack> getInventoryProjectilePredicate() {
        return CrossbowItem.IS_BOW_PROJECTILE;
    }
    
    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack4 = player.getStackInHand(hand);
        if (isCharged(itemStack4)) {
            shootAllProjectiles(world, player, hand, itemStack4, l(itemStack4), 1.0f);
            setCharged(itemStack4, false);
            return new TypedActionResult<ItemStack>(ActionResult.a, itemStack4);
        }
        if (!player.getArrowType(itemStack4).isEmpty()) {
            if (!isCharged(itemStack4)) {
                this.c = false;
                this.d = false;
                player.setCurrentHand(hand);
            }
            return new TypedActionResult<ItemStack>(ActionResult.a, itemStack4);
        }
        return new TypedActionResult<ItemStack>(ActionResult.c, itemStack4);
    }
    
    @Override
    public void onItemStopUsing(final ItemStack stack, final World world, final LivingEntity player, final int integer) {
        final int integer2 = this.getMaxUseTime(stack) - integer;
        final float float6 = a(integer2, stack);
        if (float6 >= 1.0f && !isCharged(stack)) {
            a(player, stack);
            setCharged(stack, true);
            final SoundCategory soundCategory7 = (player instanceof PlayerEntity) ? SoundCategory.h : SoundCategory.f;
            world.playSound(null, player.x, player.y, player.z, SoundEvents.bB, soundCategory7, 1.0f, 1.0f / (CrossbowItem.random.nextFloat() * 0.5f + 1.0f) + 0.2f);
        }
    }
    
    private static void a(final LivingEntity livingEntity, final ItemStack itemStack) {
        final int integer3 = EnchantmentHelper.getLevel(Enchantments.G, itemStack);
        final int integer4 = (integer3 == 0) ? 1 : 3;
        final boolean boolean5 = livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).abilities.creativeMode;
        ItemStack itemStack2 = livingEntity.getArrowType(itemStack);
        ItemStack itemStack3 = itemStack2.copy();
        for (int integer5 = 0; integer5 < integer4; ++integer5) {
            if (integer5 > 0) {
                itemStack2 = itemStack3.copy();
            }
            if (itemStack2.isEmpty() && boolean5) {
                itemStack2 = new ItemStack(Items.jg);
                itemStack3 = itemStack2.copy();
            }
            a(livingEntity, itemStack, itemStack2, integer5 > 0, boolean5);
        }
    }
    
    private static void a(final LivingEntity livingEntity, final ItemStack itemStack2, final ItemStack itemStack3, final boolean boolean4, final boolean boolean5) {
        final boolean boolean6 = boolean5 && itemStack3.getItem() instanceof ArrowItem;
        ItemStack itemStack4;
        if (!boolean6 && !boolean5 && !boolean4) {
            itemStack4 = itemStack3.split(1);
            if (itemStack3.isEmpty() && livingEntity instanceof PlayerEntity) {
                ((PlayerEntity)livingEntity).inventory.removeOne(itemStack3);
            }
        }
        else {
            itemStack4 = itemStack3.copy();
        }
        storeChargedProjectile(itemStack2, itemStack4);
    }
    
    public static boolean isCharged(final ItemStack stack) {
        final CompoundTag compoundTag2 = stack.getTag();
        return compoundTag2 != null && compoundTag2.getBoolean("Charged");
    }
    
    public static void setCharged(final ItemStack stack, final boolean charged) {
        final CompoundTag compoundTag3 = stack.getOrCreateTag();
        compoundTag3.putBoolean("Charged", charged);
    }
    
    private static void storeChargedProjectile(final ItemStack itemStack1, final ItemStack itemStack2) {
        final CompoundTag compoundTag3 = itemStack1.getOrCreateTag();
        ListTag listTag4;
        if (compoundTag3.containsKey("ChargedProjectiles", 9)) {
            listTag4 = compoundTag3.getList("ChargedProjectiles", 10);
        }
        else {
            listTag4 = new ListTag();
        }
        final CompoundTag compoundTag4 = new CompoundTag();
        itemStack2.toTag(compoundTag4);
        ((AbstractList<CompoundTag>)listTag4).add(compoundTag4);
        compoundTag3.put("ChargedProjectiles", listTag4);
    }
    
    private static List<ItemStack> getChargedProjectiles(final ItemStack itemStack) {
        final List<ItemStack> list2 = Lists.newArrayList();
        final CompoundTag compoundTag3 = itemStack.getTag();
        if (compoundTag3 != null && compoundTag3.containsKey("ChargedProjectiles", 9)) {
            final ListTag listTag4 = compoundTag3.getList("ChargedProjectiles", 10);
            if (listTag4 != null) {
                for (int integer5 = 0; integer5 < listTag4.size(); ++integer5) {
                    final CompoundTag compoundTag4 = listTag4.getCompoundTag(integer5);
                    list2.add(ItemStack.fromTag(compoundTag4));
                }
            }
        }
        return list2;
    }
    
    private static void clearProjectiles(final ItemStack itemStack) {
        final CompoundTag compoundTag2 = itemStack.getTag();
        if (compoundTag2 != null) {
            final ListTag listTag3 = compoundTag2.getList("ChargedProjectiles", 9);
            listTag3.clear();
            compoundTag2.put("ChargedProjectiles", listTag3);
        }
    }
    
    private static boolean hasProjectile(final ItemStack itemStack, final Item item) {
        return getChargedProjectiles(itemStack).stream().anyMatch(itemStack -> itemStack.getItem() == item);
    }
    
    private static void shoot(final World world, final LivingEntity livingEntity, final Hand hand, final ItemStack itemStack4, final ItemStack itemStack5, final float float6, final boolean boolean7, final float float8, final float float9, final float float10) {
        if (world.isClient) {
            return;
        }
        final boolean boolean8 = itemStack5.getItem() == Items.nX;
        Projectile projectile12;
        if (boolean8) {
            projectile12 = new FireworkEntity(world, itemStack5, livingEntity.x, livingEntity.y + livingEntity.getStandingEyeHeight() - 0.15000000596046448, livingEntity.z, true);
        }
        else {
            projectile12 = a(world, livingEntity, itemStack4, itemStack5);
            if (boolean7 || float10 != 0.0f) {
                ((ProjectileEntity)projectile12).pickupType = ProjectileEntity.PickupType.CREATIVE_PICKUP;
            }
        }
        if (livingEntity instanceof CrossbowUser) {
            final CrossbowUser crossbowUser13 = (CrossbowUser)livingEntity;
            crossbowUser13.shoot(crossbowUser13.getTarget(), itemStack4, projectile12, float10);
        }
        else {
            final Vec3d vec3d13 = livingEntity.getOppositeRotationVector(1.0f);
            final Quaternion quaternion14 = new Quaternion(new Vector3f(vec3d13), float10, true);
            final Vec3d vec3d14 = livingEntity.getRotationVec(1.0f);
            final Vector3f vector3f16 = new Vector3f(vec3d14);
            vector3f16.a(quaternion14);
            projectile12.setVelocity(vector3f16.x(), vector3f16.y(), vector3f16.z(), float8, float9);
        }
        itemStack4.<LivingEntity>applyDamage(boolean8 ? 3 : 1, livingEntity, livingEntity -> livingEntity.sendToolBreakStatus(hand));
        world.spawnEntity((Entity)projectile12);
        world.playSound(null, livingEntity.x, livingEntity.y, livingEntity.z, SoundEvents.bH, SoundCategory.h, 1.0f, float6);
    }
    
    private static ProjectileEntity a(final World world, final LivingEntity livingEntity, final ItemStack itemStack3, final ItemStack itemStack4) {
        final ArrowItem arrowItem5 = (ArrowItem)((itemStack4.getItem() instanceof ArrowItem) ? itemStack4.getItem() : Items.jg);
        final ProjectileEntity projectileEntity6 = arrowItem5.createProjectile(world, itemStack4, livingEntity);
        if (livingEntity instanceof PlayerEntity) {
            projectileEntity6.setCritical(true);
        }
        projectileEntity6.setSound(SoundEvents.bA);
        projectileEntity6.setShotFromCrossbow(true);
        final int integer7 = EnchantmentHelper.getLevel(Enchantments.I, itemStack3);
        if (integer7 > 0) {
            projectileEntity6.setPierceLevel((byte)integer7);
        }
        return projectileEntity6;
    }
    
    public static void shootAllProjectiles(final World world, final LivingEntity livingEntity, final Hand hand, final ItemStack itemStack, final float float5, final float float6) {
        final List<ItemStack> list7 = getChargedProjectiles(itemStack);
        final float[] arr8 = a(livingEntity.getRand());
        for (int integer9 = 0; integer9 < list7.size(); ++integer9) {
            final ItemStack itemStack2 = list7.get(integer9);
            final boolean boolean11 = livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).abilities.creativeMode;
            if (!itemStack2.isEmpty()) {
                if (integer9 == 0) {
                    shoot(world, livingEntity, hand, itemStack, itemStack2, arr8[integer9], boolean11, float5, float6, 0.0f);
                }
                else if (integer9 == 1) {
                    shoot(world, livingEntity, hand, itemStack, itemStack2, arr8[integer9], boolean11, float5, float6, -10.0f);
                }
                else if (integer9 == 2) {
                    shoot(world, livingEntity, hand, itemStack, itemStack2, arr8[integer9], boolean11, float5, float6, 10.0f);
                }
            }
        }
        a(world, livingEntity, itemStack);
    }
    
    private static float[] a(final Random random) {
        final boolean boolean2 = random.nextBoolean();
        return new float[] { 1.0f, a(boolean2), a(!boolean2) };
    }
    
    private static float a(final boolean boolean1) {
        final float float2 = boolean1 ? 0.63f : 0.43f;
        return 1.0f / (CrossbowItem.random.nextFloat() * 0.5f + 1.8f) + float2;
    }
    
    private static void a(final World world, final LivingEntity livingEntity, final ItemStack itemStack) {
        if (livingEntity instanceof ServerPlayerEntity) {
            final ServerPlayerEntity serverPlayerEntity4 = (ServerPlayerEntity)livingEntity;
            if (!world.isClient) {
                Criterions.SHOT_CROSSBOW.trigger(serverPlayerEntity4, itemStack);
            }
            serverPlayerEntity4.incrementStat(Stats.c.getOrCreateStat(itemStack.getItem()));
        }
        clearProjectiles(itemStack);
    }
    
    @Override
    public void onUsingTick(final World world, final LivingEntity entity, final ItemStack stack, final int timeLeft) {
        if (!world.isClient) {
            final int integer5 = EnchantmentHelper.getLevel(Enchantments.H, stack);
            final SoundEvent soundEvent6 = this.getChargeSound(integer5);
            final SoundEvent soundEvent7 = (integer5 == 0) ? SoundEvents.bC : null;
            final float float8 = (stack.getMaxUseTime() - timeLeft) / (float)getPullTime(stack);
            if (float8 < 0.2f) {
                this.c = false;
                this.d = false;
            }
            if (float8 >= 0.2f && !this.c) {
                this.c = true;
                world.playSound(null, entity.x, entity.y, entity.z, soundEvent6, SoundCategory.h, 0.5f, 1.0f);
            }
            if (float8 >= 0.5f && soundEvent7 != null && !this.d) {
                this.d = true;
                world.playSound(null, entity.x, entity.y, entity.z, soundEvent7, SoundCategory.h, 0.5f, 1.0f);
            }
        }
    }
    
    @Override
    public int getMaxUseTime(final ItemStack stack) {
        return getPullTime(stack) + 3;
    }
    
    public static int getPullTime(final ItemStack stack) {
        final int integer2 = EnchantmentHelper.getLevel(Enchantments.H, stack);
        return (integer2 == 0) ? 25 : (25 - 5 * integer2);
    }
    
    @Override
    public UseAction getUseAction(final ItemStack stack) {
        return UseAction.g;
    }
    
    private SoundEvent getChargeSound(final int quickChargeLevel) {
        switch (quickChargeLevel) {
            case 1: {
                return SoundEvents.bE;
            }
            case 2: {
                return SoundEvents.bF;
            }
            case 3: {
                return SoundEvents.bG;
            }
            default: {
                return SoundEvents.bD;
            }
        }
    }
    
    private static float a(final int integer, final ItemStack itemStack) {
        float float3 = integer / (float)getPullTime(itemStack);
        if (float3 > 1.0f) {
            float3 = 1.0f;
        }
        return float3;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void buildTooltip(final ItemStack stack, @Nullable final World world, final List<TextComponent> tooltip, final TooltipContext options) {
        final List<ItemStack> list5 = getChargedProjectiles(stack);
        if (!isCharged(stack) || list5.isEmpty()) {
            return;
        }
        final ItemStack itemStack6 = list5.get(0);
        tooltip.add(new TranslatableTextComponent("item.minecraft.crossbow.projectile", new Object[0]).append(" ").append(itemStack6.toTextComponent()));
        if (options.isAdvanced() && itemStack6.getItem() == Items.nX) {
            final List<TextComponent> list6 = Lists.newArrayList();
            Items.nX.buildTooltip(itemStack6, world, list6, options);
            if (!list6.isEmpty()) {
                for (int integer8 = 0; integer8 < list6.size(); ++integer8) {
                    list6.set(integer8, new StringTextComponent("  ").append(list6.get(integer8)).applyFormat(TextFormat.h));
                }
                tooltip.addAll(list6);
            }
        }
    }
    
    private static float l(final ItemStack itemStack) {
        if (itemStack.getItem() == Items.py && hasProjectile(itemStack, Items.nX)) {
            return 1.6f;
        }
        return 3.15f;
    }
}
