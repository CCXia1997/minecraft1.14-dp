package net.minecraft.entity.player;

import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import com.google.common.collect.ImmutableMap;
import net.minecraft.text.Style;
import net.minecraft.advancement.criterion.Criterions;
import java.util.function.Predicate;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.BaseBowItem;
import net.minecraft.util.AbsoluteHand;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ArmorItem;
import net.minecraft.client.render.entity.PlayerModelPart;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import net.minecraft.text.event.ClickEvent;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.entity.passive.TameableEntity;
import com.google.common.collect.Lists;
import net.minecraft.text.StringTextComponent;
import net.minecraft.world.GameMode;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.world.BlockView;
import net.minecraft.recipe.Recipe;
import java.util.Collection;
import net.minecraft.util.Identifier;
import net.minecraft.text.TextComponent;
import net.minecraft.block.Block;
import net.minecraft.block.BedBlock;
import net.minecraft.world.ViewableWorld;
import java.util.Optional;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.state.property.Property;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.util.math.Direction;
import net.minecraft.util.Void;
import com.mojang.datafixers.util.Either;
import java.util.Iterator;
import net.minecraft.util.math.Vec3d;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.network.Packet;
import net.minecraft.client.network.packet.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.entity.EntityGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.village.TraderOfferList;
import java.util.OptionalInt;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.inventory.Inventory;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.util.Hand;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.item.AxeItem;
import net.minecraft.nbt.Tag;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.ListTag;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.stat.Stat;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.ParrotEntity;
import java.util.List;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.Entity;
import net.minecraft.world.Difficulty;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.MathHelper;
import net.minecraft.stat.Stats;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import javax.annotation.Nullable;
import net.minecraft.entity.projectile.FishHookEntity;
import net.minecraft.item.ItemStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.mojang.authlib.GameProfile;
import net.minecraft.util.math.BlockPos;
import net.minecraft.container.Container;
import net.minecraft.container.PlayerContainer;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.EntityPose;
import java.util.Map;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.LivingEntity;

public abstract class PlayerEntity extends LivingEntity
{
    public static final EntitySize STANDING_SIZE;
    private static final Map<EntityPose, EntitySize> SIZES;
    private static final TrackedData<Float> ABSORPTION_AMOUNT;
    private static final TrackedData<Integer> SCORE;
    protected static final TrackedData<Byte> PLAYER_MODEL_BIT_MASK;
    protected static final TrackedData<Byte> MAIN_HAND;
    protected static final TrackedData<CompoundTag> LEFT_SHOULDER_ENTITY;
    protected static final TrackedData<CompoundTag> RIGHT_SHOULDER_ENTITY;
    public final PlayerInventory inventory;
    protected EnderChestInventory enderChestInventory;
    public final PlayerContainer playerContainer;
    public Container container;
    protected HungerManager hungerManager;
    protected int bC;
    public float bD;
    public float bE;
    public int experienceOrbPickupDelay;
    public double bG;
    public double bH;
    public double bI;
    public double bJ;
    public double bK;
    public double bL;
    private int sleepTimer;
    protected boolean isInWater;
    private BlockPos spawnPosition;
    private boolean spawnForced;
    public final PlayerAbilities abilities;
    public int experience;
    public int experienceLevel;
    public float experienceLevelProgress;
    protected int enchantmentTableSeed;
    protected final float bS = 0.02f;
    private int bU;
    private final GameProfile gameProfile;
    @Environment(EnvType.CLIENT)
    private boolean reducedDebugInfo;
    private ItemStack bX;
    private final ItemCooldownManager itemCooldownManager;
    @Nullable
    public FishHookEntity fishHook;
    
    public PlayerEntity(final World world, final GameProfile gameProfile) {
        super(EntityType.PLAYER, world);
        this.inventory = new PlayerInventory(this);
        this.enderChestInventory = new EnderChestInventory();
        this.hungerManager = new HungerManager();
        this.abilities = new PlayerAbilities();
        this.bX = ItemStack.EMPTY;
        this.itemCooldownManager = this.createCooldownManager();
        this.setUuid(getUuidFromProfile(gameProfile));
        this.gameProfile = gameProfile;
        this.playerContainer = new PlayerContainer(this.inventory, !world.isClient, this);
        this.container = this.playerContainer;
        final BlockPos blockPos3 = world.getSpawnPos();
        this.setPositionAndAngles(blockPos3.getX() + 0.5, blockPos3.getY() + 1, blockPos3.getZ() + 0.5, 0.0f, 0.0f);
        this.aX = 180.0f;
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeContainer().register(EntityAttributes.ATTACK_DAMAGE).setBaseValue(1.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.10000000149011612);
        this.getAttributeContainer().register(EntityAttributes.ATTACK_SPEED);
        this.getAttributeContainer().register(EntityAttributes.LUCK);
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Float>startTracking(PlayerEntity.ABSORPTION_AMOUNT, 0.0f);
        this.dataTracker.<Integer>startTracking(PlayerEntity.SCORE, 0);
        this.dataTracker.<Byte>startTracking(PlayerEntity.PLAYER_MODEL_BIT_MASK, (Byte)0);
        this.dataTracker.<Byte>startTracking(PlayerEntity.MAIN_HAND, (Byte)1);
        this.dataTracker.<CompoundTag>startTracking(PlayerEntity.LEFT_SHOULDER_ENTITY, new CompoundTag());
        this.dataTracker.<CompoundTag>startTracking(PlayerEntity.RIGHT_SHOULDER_ENTITY, new CompoundTag());
    }
    
    @Override
    public void tick() {
        this.noClip = this.isSpectator();
        if (this.isSpectator()) {
            this.onGround = false;
        }
        if (this.experienceOrbPickupDelay > 0) {
            --this.experienceOrbPickupDelay;
        }
        if (this.isSleeping()) {
            ++this.sleepTimer;
            if (this.sleepTimer > 100) {
                this.sleepTimer = 100;
            }
            if (!this.world.isClient && this.world.isDaylight()) {
                this.wakeUp(false, true, true);
            }
        }
        else if (this.sleepTimer > 0) {
            ++this.sleepTimer;
            if (this.sleepTimer >= 110) {
                this.sleepTimer = 0;
            }
        }
        this.updateInWater();
        super.tick();
        if (!this.world.isClient && this.container != null && !this.container.canUse(this)) {
            this.closeContainer();
            this.container = this.playerContainer;
        }
        if (this.isOnFire() && this.abilities.invulnerable) {
            this.extinguish();
        }
        this.n();
        if (!this.world.isClient) {
            this.hungerManager.update(this);
            this.incrementStat(Stats.k);
            if (this.isAlive()) {
                this.incrementStat(Stats.l);
            }
            if (this.isSneaking()) {
                this.incrementStat(Stats.n);
            }
            if (!this.isSleeping()) {
                this.incrementStat(Stats.m);
            }
        }
        final int integer1 = 29999999;
        final double double2 = MathHelper.clamp(this.x, -2.9999999E7, 2.9999999E7);
        final double double3 = MathHelper.clamp(this.z, -2.9999999E7, 2.9999999E7);
        if (double2 != this.x || double3 != this.z) {
            this.setPosition(double2, this.y, double3);
        }
        ++this.aD;
        final ItemStack itemStack6 = this.getMainHandStack();
        if (!ItemStack.areEqual(this.bX, itemStack6)) {
            if (!ItemStack.areEqualIgnoreDurability(this.bX, itemStack6)) {
                this.dZ();
            }
            this.bX = (itemStack6.isEmpty() ? ItemStack.EMPTY : itemStack6.copy());
        }
        this.updateTurtleHelmet();
        this.itemCooldownManager.update();
        this.updateSize();
    }
    
    protected boolean updateInWater() {
        return this.isInWater = this.isInFluid(FluidTags.a, true);
    }
    
    private void updateTurtleHelmet() {
        final ItemStack itemStack1 = this.getEquippedStack(EquipmentSlot.HEAD);
        if (itemStack1.getItem() == Items.iY && !this.isInFluid(FluidTags.a)) {
            this.addPotionEffect(new StatusEffectInstance(StatusEffects.m, 200, 0, false, false, true));
        }
    }
    
    protected ItemCooldownManager createCooldownManager() {
        return new ItemCooldownManager();
    }
    
    private void n() {
        this.bG = this.bJ;
        this.bH = this.bK;
        this.bI = this.bL;
        final double double1 = this.x - this.bJ;
        final double double2 = this.y - this.bK;
        final double double3 = this.z - this.bL;
        final double double4 = 10.0;
        if (double1 > 10.0) {
            this.bJ = this.x;
            this.bG = this.bJ;
        }
        if (double3 > 10.0) {
            this.bL = this.z;
            this.bI = this.bL;
        }
        if (double2 > 10.0) {
            this.bK = this.y;
            this.bH = this.bK;
        }
        if (double1 < -10.0) {
            this.bJ = this.x;
            this.bG = this.bJ;
        }
        if (double3 < -10.0) {
            this.bL = this.z;
            this.bI = this.bL;
        }
        if (double2 < -10.0) {
            this.bK = this.y;
            this.bH = this.bK;
        }
        this.bJ += double1 * 0.25;
        this.bL += double3 * 0.25;
        this.bK += double2 * 0.25;
    }
    
    protected void updateSize() {
        if (!this.wouldPoseNotCollide(EntityPose.d)) {
            return;
        }
        EntityPose entityPose1;
        if (this.isFallFlying()) {
            entityPose1 = EntityPose.b;
        }
        else if (this.isSleeping()) {
            entityPose1 = EntityPose.c;
        }
        else if (this.isSwimming()) {
            entityPose1 = EntityPose.d;
        }
        else if (this.isUsingRiptide()) {
            entityPose1 = EntityPose.e;
        }
        else if (this.isSneaking() && !this.abilities.flying) {
            entityPose1 = EntityPose.f;
        }
        else {
            entityPose1 = EntityPose.a;
        }
        EntityPose entityPose2;
        if (this.isSpectator() || this.hasVehicle() || this.wouldPoseNotCollide(entityPose1)) {
            entityPose2 = entityPose1;
        }
        else if (this.wouldPoseNotCollide(EntityPose.f)) {
            entityPose2 = EntityPose.f;
        }
        else {
            entityPose2 = EntityPose.d;
        }
        this.setPose(entityPose2);
    }
    
    @Override
    public int getMaxPortalTime() {
        return this.abilities.invulnerable ? 1 : 80;
    }
    
    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.iQ;
    }
    
    @Override
    protected SoundEvent getSplashSound() {
        return SoundEvents.iO;
    }
    
    @Override
    protected SoundEvent getHighSpeedSplashSound() {
        return SoundEvents.iP;
    }
    
    @Override
    public int getDefaultPortalCooldown() {
        return 10;
    }
    
    @Override
    public void playSound(final SoundEvent sound, final float volume, final float float3) {
        this.world.playSound(this, this.x, this.y, this.z, sound, this.getSoundCategory(), volume, float3);
    }
    
    public void playSound(final SoundEvent soundEvent, final SoundCategory soundCategory, final float float3, final float float4) {
    }
    
    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.h;
    }
    
    @Override
    protected int cc() {
        return 20;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void handleStatus(final byte status) {
        if (status == 9) {
            this.q();
        }
        else if (status == 23) {
            this.reducedDebugInfo = false;
        }
        else if (status == 22) {
            this.reducedDebugInfo = true;
        }
        else if (status == 43) {
            this.spawnParticles(ParticleTypes.f);
        }
        else {
            super.handleStatus(status);
        }
    }
    
    @Environment(EnvType.CLIENT)
    private void spawnParticles(final ParticleParameters particleParameters) {
        for (int integer2 = 0; integer2 < 5; ++integer2) {
            final double double3 = this.random.nextGaussian() * 0.02;
            final double double4 = this.random.nextGaussian() * 0.02;
            final double double5 = this.random.nextGaussian() * 0.02;
            this.world.addParticle(particleParameters, this.x + this.random.nextFloat() * this.getWidth() * 2.0f - this.getWidth(), this.y + 1.0 + this.random.nextFloat() * this.getHeight(), this.z + this.random.nextFloat() * this.getWidth() * 2.0f - this.getWidth(), double3, double4, double5);
        }
    }
    
    protected void closeContainer() {
        this.container = this.playerContainer;
    }
    
    @Override
    public void tickRiding() {
        if (!this.world.isClient && this.isSneaking() && this.hasVehicle()) {
            this.stopRiding();
            this.setSneaking(false);
            return;
        }
        final double double1 = this.x;
        final double double2 = this.y;
        final double double3 = this.z;
        final float float7 = this.yaw;
        final float float8 = this.pitch;
        super.tickRiding();
        this.bD = this.bE;
        this.bE = 0.0f;
        this.l(this.x - double1, this.y - double2, this.z - double3);
        if (this.getVehicle() instanceof PigEntity) {
            this.pitch = float8;
            this.yaw = float7;
            this.aK = ((PigEntity)this.getVehicle()).aK;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public void X() {
        this.setPose(EntityPose.a);
        super.X();
        this.setHealth(this.getHealthMaximum());
        this.deathTime = 0;
    }
    
    @Override
    protected void tickNewAi() {
        super.tickNewAi();
        this.tickHandSwing();
        this.headYaw = this.yaw;
    }
    
    @Override
    public void updateState() {
        if (this.bC > 0) {
            --this.bC;
        }
        if (this.world.getDifficulty() == Difficulty.PEACEFUL && this.world.getGameRules().getBoolean("naturalRegeneration")) {
            if (this.getHealth() < this.getHealthMaximum() && this.age % 20 == 0) {
                this.heal(1.0f);
            }
            if (this.hungerManager.isNotFull() && this.age % 10 == 0) {
                this.hungerManager.setFoodLevel(this.hungerManager.getFoodLevel() + 1);
            }
        }
        this.inventory.updateItems();
        this.bD = this.bE;
        super.updateState();
        final EntityAttributeInstance entityAttributeInstance1 = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (!this.world.isClient) {
            entityAttributeInstance1.setBaseValue(this.abilities.getWalkSpeed());
        }
        this.aO = 0.02f;
        if (this.isSprinting()) {
            this.aO += (float)0.005999999865889549;
        }
        this.setMovementSpeed((float)entityAttributeInstance1.getValue());
        float float2;
        if (!this.onGround || this.getHealth() <= 0.0f || this.isSwimming()) {
            float2 = 0.0f;
        }
        else {
            float2 = Math.min(0.1f, MathHelper.sqrt(Entity.squaredHorizontalLength(this.getVelocity())));
        }
        this.bE += (float2 - this.bE) * 0.4f;
        if (this.getHealth() > 0.0f && !this.isSpectator()) {
            BoundingBox boundingBox3;
            if (this.hasVehicle() && !this.getVehicle().removed) {
                boundingBox3 = this.getBoundingBox().union(this.getVehicle().getBoundingBox()).expand(1.0, 0.0, 1.0);
            }
            else {
                boundingBox3 = this.getBoundingBox().expand(1.0, 0.5, 1.0);
            }
            final List<Entity> list4 = this.world.getEntities(this, boundingBox3);
            for (int integer5 = 0; integer5 < list4.size(); ++integer5) {
                final Entity entity6 = list4.get(integer5);
                if (!entity6.removed) {
                    this.collideWithEntity(entity6);
                }
            }
        }
        this.updateShoulderEntity(this.getShoulderEntityLeft());
        this.updateShoulderEntity(this.getShoulderEntityRight());
        if ((!this.world.isClient && (this.fallDistance > 0.5f || this.isInsideWater() || this.hasVehicle())) || this.abilities.flying) {
            this.dropShoulderEntities();
        }
    }
    
    private void updateShoulderEntity(@Nullable final CompoundTag compoundTag) {
        if ((compoundTag != null && !compoundTag.containsKey("Silent")) || !compoundTag.getBoolean("Silent")) {
            final String string2 = compoundTag.getString("id");
            EntityType.get(string2).filter(entityType -> entityType == EntityType.PARROT).ifPresent(entityType -> ParrotEntity.playMobSound(this.world, this));
        }
    }
    
    private void collideWithEntity(final Entity entity) {
        entity.onPlayerCollision(this);
    }
    
    public int getScore() {
        return this.dataTracker.<Integer>get(PlayerEntity.SCORE);
    }
    
    public void setScore(final int integer) {
        this.dataTracker.<Integer>set(PlayerEntity.SCORE, integer);
    }
    
    public void addScore(final int integer) {
        final int integer2 = this.getScore();
        this.dataTracker.<Integer>set(PlayerEntity.SCORE, integer2 + integer);
    }
    
    @Override
    public void onDeath(final DamageSource damageSource) {
        super.onDeath(damageSource);
        this.setPosition(this.x, this.y, this.z);
        if (!this.isSpectator()) {
            this.drop(damageSource);
        }
        if (damageSource != null) {
            this.setVelocity(-MathHelper.cos((this.az + this.yaw) * 0.017453292f) * 0.1f, 0.10000000149011612, -MathHelper.sin((this.az + this.yaw) * 0.017453292f) * 0.1f);
        }
        else {
            this.setVelocity(0.0, 0.1, 0.0);
        }
        this.incrementStat(Stats.L);
        this.resetStat(Stats.i.getOrCreateStat(Stats.l));
        this.resetStat(Stats.i.getOrCreateStat(Stats.m));
        this.extinguish();
        this.setFlag(0, false);
    }
    
    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (!this.world.getGameRules().getBoolean("keepInventory")) {
            this.vanishCursedItems();
            this.inventory.dropAll();
        }
    }
    
    protected void vanishCursedItems() {
        for (int integer1 = 0; integer1 < this.inventory.getInvSize(); ++integer1) {
            final ItemStack itemStack2 = this.inventory.getInvStack(integer1);
            if (!itemStack2.isEmpty() && EnchantmentHelper.hasVanishingCurse(itemStack2)) {
                this.inventory.removeInvStack(integer1);
            }
        }
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        if (source == DamageSource.ON_FIRE) {
            return SoundEvents.iK;
        }
        if (source == DamageSource.DROWN) {
            return SoundEvents.iJ;
        }
        if (source == DamageSource.SWEET_BERRY_BUSH) {
            return SoundEvents.iL;
        }
        return SoundEvents.iI;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.iH;
    }
    
    @Nullable
    public ItemEntity dropSelectedItem(final boolean boolean1) {
        return this.dropItem(this.inventory.takeInvStack(this.inventory.selectedSlot, (boolean1 && !this.inventory.getMainHandStack().isEmpty()) ? this.inventory.getMainHandStack().getAmount() : 1), false, true);
    }
    
    @Nullable
    public ItemEntity dropItem(final ItemStack stack, final boolean boolean2) {
        return this.dropItem(stack, false, boolean2);
    }
    
    @Nullable
    public ItemEntity dropItem(final ItemStack stack, final boolean boolean2, final boolean boolean3) {
        if (stack.isEmpty()) {
            return null;
        }
        final double double4 = this.y - 0.30000001192092896 + this.getStandingEyeHeight();
        final ItemEntity itemEntity6 = new ItemEntity(this.world, this.x, double4, this.z, stack);
        itemEntity6.setPickupDelay(40);
        if (boolean3) {
            itemEntity6.setThrower(this.getUuid());
        }
        if (boolean2) {
            final float float7 = this.random.nextFloat() * 0.5f;
            final float float8 = this.random.nextFloat() * 6.2831855f;
            this.setVelocity(-MathHelper.sin(float8) * float7, 0.20000000298023224, MathHelper.cos(float8) * float7);
        }
        else {
            final float float7 = 0.3f;
            final float float8 = MathHelper.sin(this.pitch * 0.017453292f);
            final float float9 = MathHelper.cos(this.pitch * 0.017453292f);
            final float float10 = MathHelper.sin(this.yaw * 0.017453292f);
            final float float11 = MathHelper.cos(this.yaw * 0.017453292f);
            final float float12 = this.random.nextFloat() * 6.2831855f;
            final float float13 = 0.02f * this.random.nextFloat();
            itemEntity6.setVelocity(-float10 * float9 * 0.3f + Math.cos(float12) * float13, -float8 * 0.3f + 0.1f + (this.random.nextFloat() - this.random.nextFloat()) * 0.1f, float11 * float9 * 0.3f + Math.sin(float12) * float13);
        }
        return itemEntity6;
    }
    
    public float getBlockBreakingSpeed(final BlockState blockState) {
        float float2 = this.inventory.getBlockBreakingSpeed(blockState);
        if (float2 > 1.0f) {
            final int integer3 = EnchantmentHelper.getEfficiency(this);
            final ItemStack itemStack4 = this.getMainHandStack();
            if (integer3 > 0 && !itemStack4.isEmpty()) {
                float2 += integer3 * integer3 + 1;
            }
        }
        if (StatusEffectUtil.hasHaste(this)) {
            float2 *= 1.0f + (StatusEffectUtil.getHasteAmplifier(this) + 1) * 0.2f;
        }
        if (this.hasStatusEffect(StatusEffects.d)) {
            float float3 = 0.0f;
            switch (this.getStatusEffect(StatusEffects.d).getAmplifier()) {
                case 0: {
                    float3 = 0.3f;
                    break;
                }
                case 1: {
                    float3 = 0.09f;
                    break;
                }
                case 2: {
                    float3 = 0.0027f;
                    break;
                }
                default: {
                    float3 = 8.1E-4f;
                    break;
                }
            }
            float2 *= float3;
        }
        if (this.isInFluid(FluidTags.a) && !EnchantmentHelper.hasAquaAffinity(this)) {
            float2 /= 5.0f;
        }
        if (!this.onGround) {
            float2 /= 5.0f;
        }
        return float2;
    }
    
    public boolean isUsingEffectiveTool(final BlockState blockState) {
        return blockState.getMaterial().canBreakByHand() || this.inventory.isUsingEffectiveTool(blockState);
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setUuid(getUuidFromProfile(this.gameProfile));
        final ListTag listTag2 = tag.getList("Inventory", 10);
        this.inventory.deserialize(listTag2);
        this.inventory.selectedSlot = tag.getInt("SelectedItemSlot");
        this.sleepTimer = tag.getShort("SleepTimer");
        this.experienceLevelProgress = tag.getFloat("XpP");
        this.experience = tag.getInt("XpLevel");
        this.experienceLevel = tag.getInt("XpTotal");
        this.enchantmentTableSeed = tag.getInt("XpSeed");
        if (this.enchantmentTableSeed == 0) {
            this.enchantmentTableSeed = this.random.nextInt();
        }
        this.setScore(tag.getInt("Score"));
        if (tag.containsKey("SpawnX", 99) && tag.containsKey("SpawnY", 99) && tag.containsKey("SpawnZ", 99)) {
            this.spawnPosition = new BlockPos(tag.getInt("SpawnX"), tag.getInt("SpawnY"), tag.getInt("SpawnZ"));
            this.spawnForced = tag.getBoolean("SpawnForced");
        }
        this.hungerManager.deserialize(tag);
        this.abilities.deserialize(tag);
        if (tag.containsKey("EnderItems", 9)) {
            this.enderChestInventory.readTags(tag.getList("EnderItems", 10));
        }
        if (tag.containsKey("ShoulderEntityLeft", 10)) {
            this.setShoulderEntityLeft(tag.getCompound("ShoulderEntityLeft"));
        }
        if (tag.containsKey("ShoulderEntityRight", 10)) {
            this.setShoulderEntityRight(tag.getCompound("ShoulderEntityRight"));
        }
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        tag.put("Inventory", this.inventory.serialize(new ListTag()));
        tag.putInt("SelectedItemSlot", this.inventory.selectedSlot);
        tag.putShort("SleepTimer", (short)this.sleepTimer);
        tag.putFloat("XpP", this.experienceLevelProgress);
        tag.putInt("XpLevel", this.experience);
        tag.putInt("XpTotal", this.experienceLevel);
        tag.putInt("XpSeed", this.enchantmentTableSeed);
        tag.putInt("Score", this.getScore());
        if (this.spawnPosition != null) {
            tag.putInt("SpawnX", this.spawnPosition.getX());
            tag.putInt("SpawnY", this.spawnPosition.getY());
            tag.putInt("SpawnZ", this.spawnPosition.getZ());
            tag.putBoolean("SpawnForced", this.spawnForced);
        }
        this.hungerManager.serialize(tag);
        this.abilities.serialize(tag);
        tag.put("EnderItems", this.enderChestInventory.getTags());
        if (!this.getShoulderEntityLeft().isEmpty()) {
            tag.put("ShoulderEntityLeft", this.getShoulderEntityLeft());
        }
        if (!this.getShoulderEntityRight().isEmpty()) {
            tag.put("ShoulderEntityRight", this.getShoulderEntityRight());
        }
    }
    
    @Override
    public boolean damage(final DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (this.abilities.invulnerable && !source.doesDamageToCreative()) {
            return false;
        }
        this.despawnCounter = 0;
        if (this.getHealth() <= 0.0f) {
            return false;
        }
        this.dropShoulderEntities();
        if (source.isScaledWithDifficulty()) {
            if (this.world.getDifficulty() == Difficulty.PEACEFUL) {
                amount = 0.0f;
            }
            if (this.world.getDifficulty() == Difficulty.EASY) {
                amount = Math.min(amount / 2.0f + 1.0f, amount);
            }
            if (this.world.getDifficulty() == Difficulty.HARD) {
                amount = amount * 3.0f / 2.0f;
            }
        }
        return amount != 0.0f && super.damage(source, amount);
    }
    
    @Override
    protected void takeShieldHit(final LivingEntity source) {
        super.takeShieldHit(source);
        if (source.getMainHandStack().getItem() instanceof AxeItem) {
            this.disableShield(true);
        }
    }
    
    public boolean shouldDamagePlayer(final PlayerEntity playerEntity) {
        final AbstractTeam abstractTeam2 = this.getScoreboardTeam();
        final AbstractTeam abstractTeam3 = playerEntity.getScoreboardTeam();
        return abstractTeam2 == null || !abstractTeam2.isEqual(abstractTeam3) || abstractTeam2.isFriendlyFireAllowed();
    }
    
    @Override
    protected void damageArmor(final float amount) {
        this.inventory.damageArmor(amount);
    }
    
    @Override
    protected void damageShield(final float amount) {
        if (amount >= 3.0f && this.activeItemStack.getItem() == Items.oW) {
            final int integer2 = 1 + MathHelper.floor(amount);
            final Hand hand3 = this.getActiveHand();
            this.activeItemStack.<PlayerEntity>applyDamage(integer2, this, playerEntity -> playerEntity.sendToolBreakStatus(hand3));
            if (this.activeItemStack.isEmpty()) {
                if (hand3 == Hand.a) {
                    this.setEquippedStack(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
                }
                else {
                    this.setEquippedStack(EquipmentSlot.HAND_OFF, ItemStack.EMPTY);
                }
                this.activeItemStack = ItemStack.EMPTY;
                this.playSound(SoundEvents.jV, 0.8f, 0.8f + this.world.random.nextFloat() * 0.4f);
            }
        }
    }
    
    @Override
    protected void applyDamage(final DamageSource source, float damage) {
        if (this.isInvulnerableTo(source)) {
            return;
        }
        damage = this.applyArmorToDamage(source, damage);
        final float float3;
        damage = (float3 = this.applyEnchantmentsToDamage(source, damage));
        damage = Math.max(damage - this.getAbsorptionAmount(), 0.0f);
        this.setAbsorptionAmount(this.getAbsorptionAmount() - (float3 - damage));
        final float float4 = float3 - damage;
        if (float4 > 0.0f && float4 < 3.4028235E37f) {
            this.increaseStat(Stats.J, Math.round(float4 * 10.0f));
        }
        if (damage == 0.0f) {
            return;
        }
        this.addExhaustion(source.getExhaustion());
        final float float5 = this.getHealth();
        this.setHealth(this.getHealth() - damage);
        this.getDamageTracker().onDamage(source, float5, damage);
        if (damage < 3.4028235E37f) {
            this.increaseStat(Stats.H, Math.round(damage * 10.0f));
        }
    }
    
    public void openEditSignScreen(final SignBlockEntity signBlockEntity) {
    }
    
    public void openCommandBlockMinecartScreen(final CommandBlockExecutor commandBlockExecutor) {
    }
    
    public void openCommandBlockScreen(final CommandBlockBlockEntity commandBlockBlockEntity) {
    }
    
    public void openStructureBlockScreen(final StructureBlockBlockEntity structureBlockBlockEntity) {
    }
    
    public void openJigsawScreen(final JigsawBlockEntity jigsawBlockEntity) {
    }
    
    public void openHorseInventory(final HorseBaseEntity horseBaseEntity, final Inventory inventory) {
    }
    
    public OptionalInt openContainer(@Nullable final NameableContainerProvider nameableContainerProvider) {
        return OptionalInt.empty();
    }
    
    public void sendTradeOffers(final int syncId, final TraderOfferList traderOfferList, final int integer3, final int integer4, final boolean boolean5) {
    }
    
    public void openEditBookScreen(final ItemStack itemStack, final Hand hand) {
    }
    
    public ActionResult interact(final Entity entity, final Hand hand) {
        if (this.isSpectator()) {
            if (entity instanceof NameableContainerProvider) {
                this.openContainer((NameableContainerProvider)entity);
            }
            return ActionResult.PASS;
        }
        ItemStack itemStack3 = this.getStackInHand(hand);
        final ItemStack itemStack4 = itemStack3.isEmpty() ? ItemStack.EMPTY : itemStack3.copy();
        if (entity.interact(this, hand)) {
            if (this.abilities.creativeMode && itemStack3 == this.getStackInHand(hand) && itemStack3.getAmount() < itemStack4.getAmount()) {
                itemStack3.setAmount(itemStack4.getAmount());
            }
            return ActionResult.a;
        }
        if (!itemStack3.isEmpty() && entity instanceof LivingEntity) {
            if (this.abilities.creativeMode) {
                itemStack3 = itemStack4;
            }
            if (itemStack3.interactWithEntity(this, (LivingEntity)entity, hand)) {
                if (itemStack3.isEmpty() && !this.abilities.creativeMode) {
                    this.setStackInHand(hand, ItemStack.EMPTY);
                }
                return ActionResult.a;
            }
        }
        return ActionResult.PASS;
    }
    
    @Override
    public double getHeightOffset() {
        return -0.35;
    }
    
    @Override
    public void stopRiding() {
        super.stopRiding();
        this.ridingCooldown = 0;
    }
    
    @Override
    protected boolean cannotMove() {
        return super.cannotMove() || this.isSleeping();
    }
    
    public void attack(final Entity entity) {
        if (!entity.canPlayerAttack()) {
            return;
        }
        if (entity.handlePlayerAttack(this)) {
            return;
        }
        float float2 = (float)this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getValue();
        float float3;
        if (entity instanceof LivingEntity) {
            float3 = EnchantmentHelper.getAttackDamage(this.getMainHandStack(), ((LivingEntity)entity).getGroup());
        }
        else {
            float3 = EnchantmentHelper.getAttackDamage(this.getMainHandStack(), EntityGroup.DEFAULT);
        }
        final float float4 = this.s(0.5f);
        float2 *= 0.2f + float4 * float4 * 0.8f;
        float3 *= float4;
        this.dZ();
        if (float2 > 0.0f || float3 > 0.0f) {
            final boolean boolean5 = float4 > 0.9f;
            boolean boolean6 = false;
            int integer7 = 0;
            integer7 += EnchantmentHelper.getKnockback(this);
            if (this.isSprinting() && boolean5) {
                this.world.playSound(null, this.x, this.y, this.z, SoundEvents.iz, this.getSoundCategory(), 1.0f, 1.0f);
                ++integer7;
                boolean6 = true;
            }
            boolean boolean7 = boolean5 && this.fallDistance > 0.0f && !this.onGround && !this.isClimbing() && !this.isInsideWater() && !this.hasStatusEffect(StatusEffects.o) && !this.hasVehicle() && entity instanceof LivingEntity;
            boolean7 = (boolean7 && !this.isSprinting());
            if (boolean7) {
                float2 *= 1.5f;
            }
            float2 += float3;
            boolean boolean8 = false;
            final double double10 = this.E - this.D;
            if (boolean5 && !boolean7 && !boolean6 && this.onGround && double10 < this.getMovementSpeed()) {
                final ItemStack itemStack12 = this.getStackInHand(Hand.a);
                if (itemStack12.getItem() instanceof SwordItem) {
                    boolean8 = true;
                }
            }
            float float5 = 0.0f;
            boolean boolean9 = false;
            final int integer8 = EnchantmentHelper.getFireAspect(this);
            if (entity instanceof LivingEntity) {
                float5 = ((LivingEntity)entity).getHealth();
                if (integer8 > 0 && !entity.isOnFire()) {
                    boolean9 = true;
                    entity.setOnFireFor(1);
                }
            }
            final Vec3d vec3d15 = entity.getVelocity();
            final boolean boolean10 = entity.damage(DamageSource.player(this), float2);
            if (boolean10) {
                if (integer7 > 0) {
                    if (entity instanceof LivingEntity) {
                        ((LivingEntity)entity).takeKnockback(this, integer7 * 0.5f, MathHelper.sin(this.yaw * 0.017453292f), -MathHelper.cos(this.yaw * 0.017453292f));
                    }
                    else {
                        entity.addVelocity(-MathHelper.sin(this.yaw * 0.017453292f) * integer7 * 0.5f, 0.1, MathHelper.cos(this.yaw * 0.017453292f) * integer7 * 0.5f);
                    }
                    this.setVelocity(this.getVelocity().multiply(0.6, 1.0, 0.6));
                    this.setSprinting(false);
                }
                if (boolean8) {
                    final float float6 = 1.0f + EnchantmentHelper.getSweepingMultiplier(this) * float2;
                    final List<LivingEntity> list18 = this.world.<LivingEntity>getEntities(LivingEntity.class, entity.getBoundingBox().expand(1.0, 0.25, 1.0));
                    for (final LivingEntity livingEntity20 : list18) {
                        if (livingEntity20 != this && livingEntity20 != entity) {
                            if (this.isTeammate(livingEntity20)) {
                                continue;
                            }
                            if (livingEntity20 instanceof ArmorStandEntity && ((ArmorStandEntity)livingEntity20).isMarker()) {
                                continue;
                            }
                            if (this.squaredDistanceTo(livingEntity20) >= 9.0) {
                                continue;
                            }
                            livingEntity20.takeKnockback(this, 0.4f, MathHelper.sin(this.yaw * 0.017453292f), -MathHelper.cos(this.yaw * 0.017453292f));
                            livingEntity20.damage(DamageSource.player(this), float6);
                        }
                    }
                    this.world.playSound(null, this.x, this.y, this.z, SoundEvents.iC, this.getSoundCategory(), 1.0f, 1.0f);
                    this.dE();
                }
                if (entity instanceof ServerPlayerEntity && entity.velocityModified) {
                    ((ServerPlayerEntity)entity).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(entity));
                    entity.velocityModified = false;
                    entity.setVelocity(vec3d15);
                }
                if (boolean7) {
                    this.world.playSound(null, this.x, this.y, this.z, SoundEvents.iy, this.getSoundCategory(), 1.0f, 1.0f);
                    this.addCritParticles(entity);
                }
                if (!boolean7 && !boolean8) {
                    if (boolean5) {
                        this.world.playSound(null, this.x, this.y, this.z, SoundEvents.iB, this.getSoundCategory(), 1.0f, 1.0f);
                    }
                    else {
                        this.world.playSound(null, this.x, this.y, this.z, SoundEvents.iD, this.getSoundCategory(), 1.0f, 1.0f);
                    }
                }
                if (float3 > 0.0f) {
                    this.addEnchantedHitParticles(entity);
                }
                this.onAttacking(entity);
                if (entity instanceof LivingEntity) {
                    EnchantmentHelper.onUserDamaged((LivingEntity)entity, this);
                }
                EnchantmentHelper.onTargetDamaged(this, entity);
                final ItemStack itemStack13 = this.getMainHandStack();
                Entity entity2 = entity;
                if (entity instanceof EnderDragonPart) {
                    entity2 = ((EnderDragonPart)entity).owner;
                }
                if (!this.world.isClient && !itemStack13.isEmpty() && entity2 instanceof LivingEntity) {
                    itemStack13.onEntityDamaged((LivingEntity)entity2, this);
                    if (itemStack13.isEmpty()) {
                        this.setStackInHand(Hand.a, ItemStack.EMPTY);
                    }
                }
                if (entity instanceof LivingEntity) {
                    final float float7 = float5 - ((LivingEntity)entity).getHealth();
                    this.increaseStat(Stats.E, Math.round(float7 * 10.0f));
                    if (integer8 > 0) {
                        entity.setOnFireFor(integer8 * 4);
                    }
                    if (this.world instanceof ServerWorld && float7 > 2.0f) {
                        final int integer9 = (int)(float7 * 0.5);
                        ((ServerWorld)this.world).<DefaultParticleType>spawnParticles(ParticleTypes.h, entity.x, entity.y + entity.getHeight() * 0.5f, entity.z, integer9, 0.1, 0.0, 0.1, 0.2);
                    }
                }
                this.addExhaustion(0.1f);
            }
            else {
                this.world.playSound(null, this.x, this.y, this.z, SoundEvents.iA, this.getSoundCategory(), 1.0f, 1.0f);
                if (boolean9) {
                    entity.extinguish();
                }
            }
        }
    }
    
    @Override
    protected void attackLivingEntity(final LivingEntity livingEntity) {
        this.attack(livingEntity);
    }
    
    public void disableShield(final boolean sprinting) {
        float float2 = 0.25f + EnchantmentHelper.getEfficiency(this) * 0.05f;
        if (sprinting) {
            float2 += 0.75f;
        }
        if (this.random.nextFloat() < float2) {
            this.getItemCooldownManager().set(Items.oW, 100);
            this.clearActiveItem();
            this.world.sendEntityStatus(this, (byte)30);
        }
    }
    
    public void addCritParticles(final Entity entity) {
    }
    
    public void addEnchantedHitParticles(final Entity entity) {
    }
    
    public void dE() {
        final double double1 = -MathHelper.sin(this.yaw * 0.017453292f);
        final double double2 = MathHelper.cos(this.yaw * 0.017453292f);
        if (this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).<DefaultParticleType>spawnParticles(ParticleTypes.U, this.x + double1, this.y + this.getHeight() * 0.5, this.z + double2, 0, double1, 0.0, double2, 0.0);
        }
    }
    
    @Environment(EnvType.CLIENT)
    public void requestRespawn() {
    }
    
    @Override
    public void remove() {
        super.remove();
        this.playerContainer.close(this);
        if (this.container != null) {
            this.container.close(this);
        }
    }
    
    public boolean isMainPlayer() {
        return false;
    }
    
    public GameProfile getGameProfile() {
        return this.gameProfile;
    }
    
    public Either<SleepFailureReason, Void> trySleep(final BlockPos blockPos) {
        final Direction direction2 = this.world.getBlockState(blockPos).<Direction>get((Property<Direction>)HorizontalFacingBlock.FACING);
        if (!this.world.isClient) {
            if (this.isSleeping() || !this.isAlive()) {
                return (Either<SleepFailureReason, Void>)Either.left(SleepFailureReason.INVALID_ATTEMPT);
            }
            if (!this.world.dimension.hasVisibleSky()) {
                return (Either<SleepFailureReason, Void>)Either.left(SleepFailureReason.INVALID_WORLD);
            }
            if (this.world.isDaylight()) {
                return (Either<SleepFailureReason, Void>)Either.left(SleepFailureReason.WRONG_TIME);
            }
            if (!this.isWithinSleepingRange(blockPos, direction2)) {
                return (Either<SleepFailureReason, Void>)Either.left(SleepFailureReason.TOO_FAR_AWAY);
            }
            if (this.isBedObstructed(blockPos, direction2)) {
                return (Either<SleepFailureReason, Void>)Either.left(SleepFailureReason.d);
            }
            if (!this.isCreative()) {
                final double double3 = 8.0;
                final double double4 = 5.0;
                final List<HostileEntity> list7 = this.world.<HostileEntity>getEntities(HostileEntity.class, new BoundingBox(blockPos.getX() - 8.0, blockPos.getY() - 5.0, blockPos.getZ() - 8.0, blockPos.getX() + 8.0, blockPos.getY() + 5.0, blockPos.getZ() + 8.0), hostileEntity -> hostileEntity.isAngryAt(this));
                if (!list7.isEmpty()) {
                    return (Either<SleepFailureReason, Void>)Either.left(SleepFailureReason.NOT_SAFE);
                }
            }
        }
        this.sleep(blockPos);
        this.sleepTimer = 0;
        if (this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).updatePlayersSleeping();
        }
        return (Either<SleepFailureReason, Void>)Either.right(Void.INSTANCE);
    }
    
    @Override
    public void sleep(final BlockPos blockPos) {
        this.resetStat(Stats.i.getOrCreateStat(Stats.m));
        this.dropShoulderEntities();
        super.sleep(blockPos);
    }
    
    private boolean isWithinSleepingRange(final BlockPos sleepPos, final Direction direction) {
        if (Math.abs(this.x - sleepPos.getX()) <= 3.0 && Math.abs(this.y - sleepPos.getY()) <= 2.0 && Math.abs(this.z - sleepPos.getZ()) <= 3.0) {
            return true;
        }
        final BlockPos blockPos3 = sleepPos.offset(direction.getOpposite());
        return Math.abs(this.x - blockPos3.getX()) <= 3.0 && Math.abs(this.y - blockPos3.getY()) <= 2.0 && Math.abs(this.z - blockPos3.getZ()) <= 3.0;
    }
    
    private boolean isBedObstructed(final BlockPos pos, final Direction direction) {
        final BlockPos blockPos3 = pos.up();
        return !this.doesNotSuffocate(blockPos3) || !this.doesNotSuffocate(blockPos3.offset(direction.getOpposite()));
    }
    
    public void wakeUp(final boolean boolean1, final boolean boolean2, final boolean boolean3) {
        final Optional<BlockPos> optional4 = this.getSleepingPosition();
        super.wakeUp();
        if (this.world instanceof ServerWorld && boolean2) {
            ((ServerWorld)this.world).updatePlayersSleeping();
        }
        this.sleepTimer = (boolean1 ? 0 : 100);
        if (boolean3) {
            optional4.ifPresent(blockPos -> this.setPlayerSpawn(blockPos, false));
        }
    }
    
    @Override
    public void wakeUp() {
        this.wakeUp(true, true, false);
    }
    
    public static Optional<Vec3d> a(final ViewableWorld viewableWorld, final BlockPos blockPos, final boolean boolean3) {
        final Block block4 = viewableWorld.getBlockState(blockPos).getBlock();
        if (block4 instanceof BedBlock) {
            return BedBlock.findWakeUpPosition(EntityType.PLAYER, viewableWorld, blockPos, 0);
        }
        if (!boolean3) {
            return Optional.<Vec3d>empty();
        }
        final boolean boolean4 = block4.canMobSpawnInside();
        final boolean boolean5 = viewableWorld.getBlockState(blockPos.up()).getBlock().canMobSpawnInside();
        if (boolean4 && boolean5) {
            return Optional.<Vec3d>of(new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 0.1, blockPos.getZ() + 0.5));
        }
        return Optional.<Vec3d>empty();
    }
    
    public boolean isSleepingLongEnough() {
        return this.isSleeping() && this.sleepTimer >= 100;
    }
    
    public int getSleepTimer() {
        return this.sleepTimer;
    }
    
    public void addChatMessage(final TextComponent message, final boolean boolean2) {
    }
    
    public BlockPos getSpawnPosition() {
        return this.spawnPosition;
    }
    
    public boolean isSpawnForced() {
        return this.spawnForced;
    }
    
    public void setPlayerSpawn(final BlockPos pos, final boolean boolean2) {
        if (pos != null) {
            this.spawnPosition = pos;
            this.spawnForced = boolean2;
        }
        else {
            this.spawnPosition = null;
            this.spawnForced = false;
        }
    }
    
    public void incrementStat(final Identifier stat) {
        this.incrementStat(Stats.i.getOrCreateStat(stat));
    }
    
    public void increaseStat(final Identifier stat, final int amount) {
        this.increaseStat(Stats.i.getOrCreateStat(stat), amount);
    }
    
    public void incrementStat(final Stat<?> stat) {
        this.increaseStat(stat, 1);
    }
    
    public void increaseStat(final Stat<?> stat, final int amount) {
    }
    
    public void resetStat(final Stat<?> stat) {
    }
    
    public int unlockRecipes(final Collection<Recipe<?>> recipes) {
        return 0;
    }
    
    public void unlockRecipes(final Identifier[] ids) {
    }
    
    public int lockRecipes(final Collection<Recipe<?>> recipes) {
        return 0;
    }
    
    public void jump() {
        super.jump();
        this.incrementStat(Stats.C);
        if (this.isSprinting()) {
            this.addExhaustion(0.2f);
        }
        else {
            this.addExhaustion(0.05f);
        }
    }
    
    @Override
    public void travel(final Vec3d movementInput) {
        final double double2 = this.x;
        final double double3 = this.y;
        final double double4 = this.z;
        if (this.isSwimming() && !this.hasVehicle()) {
            final double double5 = this.getRotationVector().y;
            final double double6 = (double5 < -0.2) ? 0.085 : 0.06;
            if (double5 <= 0.0 || this.jumping || !this.world.getBlockState(new BlockPos(this.x, this.y + 1.0 - 0.1, this.z)).getFluidState().isEmpty()) {
                final Vec3d vec3d12 = this.getVelocity();
                this.setVelocity(vec3d12.add(0.0, (double5 - vec3d12.y) * double6, 0.0));
            }
        }
        if (this.abilities.flying && !this.hasVehicle()) {
            final double double5 = this.getVelocity().y;
            final float float10 = this.aO;
            this.aO = this.abilities.getFlySpeed() * (this.isSprinting() ? 2 : 1);
            super.travel(movementInput);
            final Vec3d vec3d13 = this.getVelocity();
            this.setVelocity(vec3d13.x, double5 * 0.6, vec3d13.z);
            this.aO = float10;
            this.fallDistance = 0.0f;
            this.setFlag(7, false);
        }
        else {
            super.travel(movementInput);
        }
        this.k(this.x - double2, this.y - double3, this.z - double4);
    }
    
    @Override
    public void updateSwimming() {
        if (this.abilities.flying) {
            this.setSwimming(false);
        }
        else {
            super.updateSwimming();
        }
    }
    
    protected boolean doesNotSuffocate(final BlockPos pos) {
        return !this.world.getBlockState(pos).canSuffocate(this.world, pos);
    }
    
    @Override
    public float getMovementSpeed() {
        return (float)this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue();
    }
    
    public void k(final double double1, final double double3, final double double5) {
        if (this.hasVehicle()) {
            return;
        }
        if (this.isSwimming()) {
            final int integer7 = Math.round(MathHelper.sqrt(double1 * double1 + double3 * double3 + double5 * double5) * 100.0f);
            if (integer7 > 0) {
                this.increaseStat(Stats.B, integer7);
                this.addExhaustion(0.01f * integer7 * 0.01f);
            }
        }
        else if (this.isInFluid(FluidTags.a, true)) {
            final int integer7 = Math.round(MathHelper.sqrt(double1 * double1 + double3 * double3 + double5 * double5) * 100.0f);
            if (integer7 > 0) {
                this.increaseStat(Stats.v, integer7);
                this.addExhaustion(0.01f * integer7 * 0.01f);
            }
        }
        else if (this.isInsideWater()) {
            final int integer7 = Math.round(MathHelper.sqrt(double1 * double1 + double5 * double5) * 100.0f);
            if (integer7 > 0) {
                this.increaseStat(Stats.r, integer7);
                this.addExhaustion(0.01f * integer7 * 0.01f);
            }
        }
        else if (this.isClimbing()) {
            if (double3 > 0.0) {
                this.increaseStat(Stats.t, (int)Math.round(double3 * 100.0));
            }
        }
        else if (this.onGround) {
            final int integer7 = Math.round(MathHelper.sqrt(double1 * double1 + double5 * double5) * 100.0f);
            if (integer7 > 0) {
                if (this.isSprinting()) {
                    this.increaseStat(Stats.q, integer7);
                    this.addExhaustion(0.1f * integer7 * 0.01f);
                }
                else if (this.isSneaking()) {
                    this.increaseStat(Stats.p, integer7);
                    this.addExhaustion(0.0f * integer7 * 0.01f);
                }
                else {
                    this.increaseStat(Stats.o, integer7);
                    this.addExhaustion(0.0f * integer7 * 0.01f);
                }
            }
        }
        else if (this.isFallFlying()) {
            final int integer7 = Math.round(MathHelper.sqrt(double1 * double1 + double3 * double3 + double5 * double5) * 100.0f);
            this.increaseStat(Stats.A, integer7);
        }
        else {
            final int integer7 = Math.round(MathHelper.sqrt(double1 * double1 + double5 * double5) * 100.0f);
            if (integer7 > 25) {
                this.increaseStat(Stats.u, integer7);
            }
        }
    }
    
    private void l(final double double1, final double double3, final double double5) {
        if (this.hasVehicle()) {
            final int integer7 = Math.round(MathHelper.sqrt(double1 * double1 + double3 * double3 + double5 * double5) * 100.0f);
            if (integer7 > 0) {
                if (this.getVehicle() instanceof AbstractMinecartEntity) {
                    this.increaseStat(Stats.w, integer7);
                }
                else if (this.getVehicle() instanceof BoatEntity) {
                    this.increaseStat(Stats.x, integer7);
                }
                else if (this.getVehicle() instanceof PigEntity) {
                    this.increaseStat(Stats.y, integer7);
                }
                else if (this.getVehicle() instanceof HorseBaseEntity) {
                    this.increaseStat(Stats.z, integer7);
                }
            }
        }
    }
    
    @Override
    public void handleFallDamage(final float fallDistance, final float damageMultiplier) {
        if (this.abilities.allowFlying) {
            return;
        }
        if (fallDistance >= 2.0f) {
            this.increaseStat(Stats.s, (int)Math.round(fallDistance * 100.0));
        }
        super.handleFallDamage(fallDistance, damageMultiplier);
    }
    
    @Override
    protected void onSwimmingStart() {
        if (!this.isSpectator()) {
            super.onSwimmingStart();
        }
    }
    
    @Override
    protected SoundEvent getFallSound(final int integer) {
        if (integer > 4) {
            return SoundEvents.iE;
        }
        return SoundEvents.iN;
    }
    
    @Override
    public void b(final LivingEntity livingEntity) {
        this.incrementStat(Stats.g.getOrCreateStat(livingEntity.getType()));
    }
    
    @Override
    public void slowMovement(final BlockState state, final Vec3d multipliers) {
        if (!this.abilities.flying) {
            super.slowMovement(state, multipliers);
        }
    }
    
    public void addExperience(final int integer) {
        this.addScore(integer);
        this.experienceLevelProgress += integer / (float)this.getNextLevelExperience();
        this.experienceLevel = MathHelper.clamp(this.experienceLevel + integer, 0, Integer.MAX_VALUE);
        while (this.experienceLevelProgress < 0.0f) {
            final float float2 = this.experienceLevelProgress * this.getNextLevelExperience();
            if (this.experience > 0) {
                this.c(-1);
                this.experienceLevelProgress = 1.0f + float2 / this.getNextLevelExperience();
            }
            else {
                this.c(-1);
                this.experienceLevelProgress = 0.0f;
            }
        }
        while (this.experienceLevelProgress >= 1.0f) {
            this.experienceLevelProgress = (this.experienceLevelProgress - 1.0f) * this.getNextLevelExperience();
            this.c(1);
            this.experienceLevelProgress /= this.getNextLevelExperience();
        }
    }
    
    public int getEnchantmentTableSeed() {
        return this.enchantmentTableSeed;
    }
    
    public void a(final ItemStack itemStack, final int integer) {
        this.experience -= integer;
        if (this.experience < 0) {
            this.experience = 0;
            this.experienceLevelProgress = 0.0f;
            this.experienceLevel = 0;
        }
        this.enchantmentTableSeed = this.random.nextInt();
    }
    
    public void c(final int integer) {
        this.experience += integer;
        if (this.experience < 0) {
            this.experience = 0;
            this.experienceLevelProgress = 0.0f;
            this.experienceLevel = 0;
        }
        if (integer > 0 && this.experience % 5 == 0 && this.bU < this.age - 100.0f) {
            final float float2 = (this.experience > 30) ? 1.0f : (this.experience / 30.0f);
            this.world.playSound(null, this.x, this.y, this.z, SoundEvents.iM, this.getSoundCategory(), float2 * 0.75f, 1.0f);
            this.bU = this.age;
        }
    }
    
    public int getNextLevelExperience() {
        if (this.experience >= 30) {
            return 112 + (this.experience - 30) * 9;
        }
        if (this.experience >= 15) {
            return 37 + (this.experience - 15) * 5;
        }
        return 7 + this.experience * 2;
    }
    
    public void addExhaustion(final float float1) {
        if (this.abilities.invulnerable) {
            return;
        }
        if (!this.world.isClient) {
            this.hungerManager.addExhaustion(float1);
        }
    }
    
    public HungerManager getHungerManager() {
        return this.hungerManager;
    }
    
    public boolean canConsume(final boolean boolean1) {
        return !this.abilities.invulnerable && (boolean1 || this.hungerManager.isNotFull());
    }
    
    public boolean canFoodHeal() {
        return this.getHealth() > 0.0f && this.getHealth() < this.getHealthMaximum();
    }
    
    public boolean canModifyWorld() {
        return this.abilities.allowModifyWorld;
    }
    
    public boolean canPlaceOn(final BlockPos pos, final Direction facing, final ItemStack itemStack) {
        if (this.abilities.allowModifyWorld) {
            return true;
        }
        final BlockPos blockPos4 = pos.offset(facing.getOpposite());
        final CachedBlockPosition cachedBlockPosition5 = new CachedBlockPosition(this.world, blockPos4, false);
        return itemStack.getCustomCanPlace(this.world.getTagManager(), cachedBlockPosition5);
    }
    
    @Override
    protected int getCurrentExperience(final PlayerEntity playerEntity) {
        if (this.world.getGameRules().getBoolean("keepInventory") || this.isSpectator()) {
            return 0;
        }
        final int integer2 = this.experience * 7;
        if (integer2 > 100) {
            return 100;
        }
        return integer2;
    }
    
    @Override
    protected boolean shouldAlwaysDropXp() {
        return true;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldRenderName() {
        return true;
    }
    
    @Override
    protected boolean canClimb() {
        return !this.abilities.flying;
    }
    
    public void sendAbilitiesUpdate() {
    }
    
    public void setGameMode(final GameMode gameMode) {
    }
    
    @Override
    public TextComponent getName() {
        return new StringTextComponent(this.gameProfile.getName());
    }
    
    public EnderChestInventory getEnderChestInventory() {
        return this.enderChestInventory;
    }
    
    @Override
    public ItemStack getEquippedStack(final EquipmentSlot equipmentSlot) {
        if (equipmentSlot == EquipmentSlot.HAND_MAIN) {
            return this.inventory.getMainHandStack();
        }
        if (equipmentSlot == EquipmentSlot.HAND_OFF) {
            return this.inventory.offHand.get(0);
        }
        if (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR) {
            return this.inventory.armor.get(equipmentSlot.getEntitySlotId());
        }
        return ItemStack.EMPTY;
    }
    
    @Override
    public void setEquippedStack(final EquipmentSlot slot, final ItemStack itemStack) {
        if (slot == EquipmentSlot.HAND_MAIN) {
            this.onEquipStack(itemStack);
            this.inventory.main.set(this.inventory.selectedSlot, itemStack);
        }
        else if (slot == EquipmentSlot.HAND_OFF) {
            this.onEquipStack(itemStack);
            this.inventory.offHand.set(0, itemStack);
        }
        else if (slot.getType() == EquipmentSlot.Type.ARMOR) {
            this.onEquipStack(itemStack);
            this.inventory.armor.set(slot.getEntitySlotId(), itemStack);
        }
    }
    
    public boolean giveItemStack(final ItemStack stack) {
        this.onEquipStack(stack);
        return this.inventory.insertStack(stack);
    }
    
    @Override
    public Iterable<ItemStack> getItemsHand() {
        return Lists.<ItemStack>newArrayList(this.getMainHandStack(), this.getOffHandStack());
    }
    
    @Override
    public Iterable<ItemStack> getArmorItems() {
        return this.inventory.armor;
    }
    
    public boolean addShoulderEntity(final CompoundTag tag) {
        if (this.hasVehicle() || !this.onGround || this.isInsideWater()) {
            return false;
        }
        if (this.getShoulderEntityLeft().isEmpty()) {
            this.setShoulderEntityLeft(tag);
            return true;
        }
        if (this.getShoulderEntityRight().isEmpty()) {
            this.setShoulderEntityRight(tag);
            return true;
        }
        return false;
    }
    
    protected void dropShoulderEntities() {
        this.k(this.getShoulderEntityLeft());
        this.setShoulderEntityLeft(new CompoundTag());
        this.k(this.getShoulderEntityRight());
        this.setShoulderEntityRight(new CompoundTag());
    }
    
    private void k(@Nullable final CompoundTag compoundTag) {
        if (!this.world.isClient && !compoundTag.isEmpty()) {
            EntityType.getEntityFromTag(compoundTag, this.world).ifPresent(entity -> {
                if (entity instanceof TameableEntity) {
                    entity.setOwnerUuid(this.uuid);
                }
                entity.setPosition(this.x, this.y + 0.699999988079071, this.z);
                ((ServerWorld)this.world).d(entity);
            });
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean canSeePlayer(final PlayerEntity player) {
        if (!this.isInvisible()) {
            return false;
        }
        if (player.isSpectator()) {
            return false;
        }
        final AbstractTeam abstractTeam2 = this.getScoreboardTeam();
        return abstractTeam2 == null || player == null || player.getScoreboardTeam() != abstractTeam2 || !abstractTeam2.shouldShowFriendlyInvisibles();
    }
    
    @Override
    public abstract boolean isSpectator();
    
    @Override
    public boolean isSwimming() {
        return !this.abilities.flying && !this.isSpectator() && super.isSwimming();
    }
    
    public abstract boolean isCreative();
    
    @Override
    public boolean canFly() {
        return !this.abilities.flying;
    }
    
    public Scoreboard getScoreboard() {
        return this.world.getScoreboard();
    }
    
    @Override
    public TextComponent getDisplayName() {
        final TextComponent textComponent1 = Team.modifyText(this.getScoreboardTeam(), this.getName());
        return this.addTellClickEvent(textComponent1);
    }
    
    public TextComponent getNameAndUuid() {
        return new StringTextComponent("").append(this.getName()).append(" (").append(this.gameProfile.getId().toString()).append(")");
    }
    
    private TextComponent addTellClickEvent(final TextComponent component) {
        final String string2 = this.getGameProfile().getName();
        final ClickEvent clickEvent;
        final String insertion;
        return component.modifyStyle(style -> {
            new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + insertion + " ");
            style.setClickEvent(clickEvent).setHoverEvent(this.getComponentHoverEvent()).setInsertion(insertion);
        });
    }
    
    @Override
    public String getEntityName() {
        return this.getGameProfile().getName();
    }
    
    public float getActiveEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        switch (entityPose) {
            case d:
            case b:
            case e: {
                return 0.4f;
            }
            case f: {
                return 1.27f;
            }
            default: {
                return 1.62f;
            }
        }
    }
    
    @Override
    public void setAbsorptionAmount(float float1) {
        if (float1 < 0.0f) {
            float1 = 0.0f;
        }
        this.getDataTracker().<Float>set(PlayerEntity.ABSORPTION_AMOUNT, float1);
    }
    
    @Override
    public float getAbsorptionAmount() {
        return this.getDataTracker().<Float>get(PlayerEntity.ABSORPTION_AMOUNT);
    }
    
    public static UUID getUuidFromProfile(final GameProfile profile) {
        UUID uUID2 = profile.getId();
        if (uUID2 == null) {
            uUID2 = getOfflinePlayerUuid(profile.getName());
        }
        return uUID2;
    }
    
    public static UUID getOfflinePlayerUuid(final String nickname) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + nickname).getBytes(StandardCharsets.UTF_8));
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isSkinOverlayVisible(final PlayerModelPart playerModelPart) {
        return (this.getDataTracker().<Byte>get(PlayerEntity.PLAYER_MODEL_BIT_MASK) & playerModelPart.getBitFlag()) == playerModelPart.getBitFlag();
    }
    
    @Override
    public boolean equip(final int slot, final ItemStack item) {
        if (slot >= 0 && slot < this.inventory.main.size()) {
            this.inventory.setInvStack(slot, item);
            return true;
        }
        EquipmentSlot equipmentSlot3;
        if (slot == 100 + EquipmentSlot.HEAD.getEntitySlotId()) {
            equipmentSlot3 = EquipmentSlot.HEAD;
        }
        else if (slot == 100 + EquipmentSlot.CHEST.getEntitySlotId()) {
            equipmentSlot3 = EquipmentSlot.CHEST;
        }
        else if (slot == 100 + EquipmentSlot.LEGS.getEntitySlotId()) {
            equipmentSlot3 = EquipmentSlot.LEGS;
        }
        else if (slot == 100 + EquipmentSlot.FEET.getEntitySlotId()) {
            equipmentSlot3 = EquipmentSlot.FEET;
        }
        else {
            equipmentSlot3 = null;
        }
        if (slot == 98) {
            this.setEquippedStack(EquipmentSlot.HAND_MAIN, item);
            return true;
        }
        if (slot == 99) {
            this.setEquippedStack(EquipmentSlot.HAND_OFF, item);
            return true;
        }
        if (equipmentSlot3 != null) {
            if (!item.isEmpty()) {
                if (item.getItem() instanceof ArmorItem || item.getItem() instanceof ElytraItem) {
                    if (MobEntity.getPreferredEquipmentSlot(item) != equipmentSlot3) {
                        return false;
                    }
                }
                else if (equipmentSlot3 != EquipmentSlot.HEAD) {
                    return false;
                }
            }
            this.inventory.setInvStack(equipmentSlot3.getEntitySlotId() + this.inventory.main.size(), item);
            return true;
        }
        final int integer4 = slot - 200;
        if (integer4 >= 0 && integer4 < this.enderChestInventory.getInvSize()) {
            this.enderChestInventory.setInvStack(integer4, item);
            return true;
        }
        return false;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean getReducedDebugInfo() {
        return this.reducedDebugInfo;
    }
    
    @Environment(EnvType.CLIENT)
    public void setReducedDebugInfo(final boolean boolean1) {
        this.reducedDebugInfo = boolean1;
    }
    
    @Override
    public AbsoluteHand getMainHand() {
        return (this.dataTracker.<Byte>get(PlayerEntity.MAIN_HAND) == 0) ? AbsoluteHand.a : AbsoluteHand.b;
    }
    
    public void setMainHand(final AbsoluteHand absoluteHand) {
        this.dataTracker.<Byte>set(PlayerEntity.MAIN_HAND, (byte)((absoluteHand != AbsoluteHand.a) ? 1 : 0));
    }
    
    public CompoundTag getShoulderEntityLeft() {
        return this.dataTracker.<CompoundTag>get(PlayerEntity.LEFT_SHOULDER_ENTITY);
    }
    
    protected void setShoulderEntityLeft(final CompoundTag compoundTag) {
        this.dataTracker.<CompoundTag>set(PlayerEntity.LEFT_SHOULDER_ENTITY, compoundTag);
    }
    
    public CompoundTag getShoulderEntityRight() {
        return this.dataTracker.<CompoundTag>get(PlayerEntity.RIGHT_SHOULDER_ENTITY);
    }
    
    protected void setShoulderEntityRight(final CompoundTag compoundTag) {
        this.dataTracker.<CompoundTag>set(PlayerEntity.RIGHT_SHOULDER_ENTITY, compoundTag);
    }
    
    public float dY() {
        return (float)(1.0 / this.getAttributeInstance(EntityAttributes.ATTACK_SPEED).getValue() * 20.0);
    }
    
    public float s(final float float1) {
        return MathHelper.clamp((this.aD + float1) / this.dY(), 0.0f, 1.0f);
    }
    
    public void dZ() {
        this.aD = 0;
    }
    
    public ItemCooldownManager getItemCooldownManager() {
        return this.itemCooldownManager;
    }
    
    public float getLuck() {
        return (float)this.getAttributeInstance(EntityAttributes.LUCK).getValue();
    }
    
    public boolean isCreativeLevelTwoOp() {
        return this.abilities.creativeMode && this.getPermissionLevel() >= 2;
    }
    
    @Override
    public boolean canPickUp(final ItemStack itemStack) {
        final EquipmentSlot equipmentSlot2 = MobEntity.getPreferredEquipmentSlot(itemStack);
        return this.getEquippedStack(equipmentSlot2).isEmpty();
    }
    
    @Override
    public EntitySize getSize(final EntityPose entityPose) {
        return PlayerEntity.SIZES.getOrDefault(entityPose, PlayerEntity.STANDING_SIZE);
    }
    
    @Override
    public ItemStack getArrowType(final ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof BaseBowItem)) {
            return ItemStack.EMPTY;
        }
        Predicate<ItemStack> predicate2 = ((BaseBowItem)itemStack.getItem()).getHeldProjectilePredicate();
        final ItemStack itemStack2 = BaseBowItem.getItemHeld(this, predicate2);
        if (!itemStack2.isEmpty()) {
            return itemStack2;
        }
        predicate2 = ((BaseBowItem)itemStack.getItem()).getInventoryProjectilePredicate();
        for (int integer4 = 0; integer4 < this.inventory.getInvSize(); ++integer4) {
            final ItemStack itemStack3 = this.inventory.getInvStack(integer4);
            if (predicate2.test(itemStack3)) {
                return itemStack3;
            }
        }
        return this.abilities.creativeMode ? new ItemStack(Items.jg) : ItemStack.EMPTY;
    }
    
    @Override
    public ItemStack eatFood(final World world, final ItemStack itemStack) {
        this.getHungerManager().eat(itemStack.getItem(), itemStack);
        this.incrementStat(Stats.c.getOrCreateStat(itemStack.getItem()));
        world.playSound(null, this.x, this.y, this.z, SoundEvents.iG, SoundCategory.h, 0.5f, world.random.nextFloat() * 0.1f + 0.9f);
        if (this instanceof ServerPlayerEntity) {
            Criterions.CONSUME_ITEM.handle((ServerPlayerEntity)this, itemStack);
        }
        return super.eatFood(world, itemStack);
    }
    
    static {
        STANDING_SIZE = EntitySize.resizeable(0.6f, 1.8f);
        SIZES = ImmutableMap.<EntityPose, EntitySize>builder().put(EntityPose.a, PlayerEntity.STANDING_SIZE).put(EntityPose.c, PlayerEntity.SLEEPING_SIZE).put(EntityPose.b, EntitySize.resizeable(0.6f, 0.6f)).put(EntityPose.d, EntitySize.resizeable(0.6f, 0.6f)).put(EntityPose.e, EntitySize.resizeable(0.6f, 0.6f)).put(EntityPose.f, EntitySize.resizeable(0.6f, 1.5f)).put(EntityPose.g, EntitySize.constant(0.2f, 0.2f)).build();
        ABSORPTION_AMOUNT = DataTracker.<Float>registerData(PlayerEntity.class, TrackedDataHandlerRegistry.FLOAT);
        SCORE = DataTracker.<Integer>registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
        PLAYER_MODEL_BIT_MASK = DataTracker.<Byte>registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BYTE);
        MAIN_HAND = DataTracker.<Byte>registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BYTE);
        LEFT_SHOULDER_ENTITY = DataTracker.<CompoundTag>registerData(PlayerEntity.class, TrackedDataHandlerRegistry.TAG_COMPOUND);
        RIGHT_SHOULDER_ENTITY = DataTracker.<CompoundTag>registerData(PlayerEntity.class, TrackedDataHandlerRegistry.TAG_COMPOUND);
    }
    
    public enum SleepFailureReason
    {
        INVALID_WORLD, 
        WRONG_TIME((TextComponent)new TranslatableTextComponent("block.minecraft.bed.no_sleep", new Object[0])), 
        TOO_FAR_AWAY((TextComponent)new TranslatableTextComponent("block.minecraft.bed.too_far_away", new Object[0])), 
        d((TextComponent)new TranslatableTextComponent("block.minecraft.bed.obstructed", new Object[0])), 
        INVALID_ATTEMPT, 
        NOT_SAFE((TextComponent)new TranslatableTextComponent("block.minecraft.bed.not_safe", new Object[0]));
        
        @Nullable
        private final TextComponent text;
        
        private SleepFailureReason() {
            this.text = null;
        }
        
        private SleepFailureReason(final TextComponent textComponent) {
            this.text = textComponent;
        }
        
        @Nullable
        public TextComponent getText() {
            return this.text;
        }
    }
}
