package net.minecraft.entity.passive;

import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import java.util.Random;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.CreeperEntity;
import java.util.UUID;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.DyeItem;
import net.minecraft.util.Hand;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvents;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.DyeColor;
import javax.annotation.Nullable;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.ai.goal.FollowTargetIfTamedGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.AttackWithOwnerGoal;
import net.minecraft.entity.ai.goal.TrackAttackerGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.WolfBegGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import java.util.function.Predicate;
import net.minecraft.entity.data.TrackedData;

public class WolfEntity extends TameableEntity
{
    private static final TrackedData<Float> WOLF_HEALTH;
    private static final TrackedData<Boolean> BEGGING;
    private static final TrackedData<Integer> COLLAR_COLOR;
    public static final Predicate<LivingEntity> FOLLOW_TAMED_PREDICATE;
    private float begAnimationProgress;
    private float lastBegAnimationProgress;
    private boolean wet;
    private boolean canShakeWaterOff;
    private float shakeProgress;
    private float lastShakeProgress;
    
    public WolfEntity(final EntityType<? extends WolfEntity> type, final World world) {
        super(type, world);
        this.setTamed(false);
    }
    
    @Override
    protected void initGoals() {
        this.sitGoal = new SitGoal(this);
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, this.sitGoal);
        this.goalSelector.add(3, new AvoidLlamaGoal<>(this, LlamaEntity.class, 24.0f, 1.5, 1.5));
        this.goalSelector.add(4, new PounceAtTargetGoal(this, 0.4f));
        this.goalSelector.add(5, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(6, new FollowOwnerGoal(this, 1.0, 10.0f, 2.0f));
        this.goalSelector.add(7, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(8, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(9, new WolfBegGoal(this, 8.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(10, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, new RevengeGoal(this, new Class[0]).setGroupRevenge(new Class[0]));
        this.targetSelector.add(4, new FollowTargetIfTamedGoal<>(this, AnimalEntity.class, false, WolfEntity.FOLLOW_TAMED_PREDICATE));
        this.targetSelector.add(4, new FollowTargetIfTamedGoal<>(this, TurtleEntity.class, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
        this.targetSelector.add(5, new FollowTargetGoal<>(this, AbstractSkeletonEntity.class, false));
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896);
        if (this.isTamed()) {
            this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(20.0);
        }
        else {
            this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(8.0);
        }
        this.getAttributeContainer().register(EntityAttributes.ATTACK_DAMAGE).setBaseValue(2.0);
    }
    
    @Override
    public void setTarget(@Nullable final LivingEntity target) {
        super.setTarget(target);
        if (target == null) {
            this.setAngry(false);
        }
        else if (!this.isTamed()) {
            this.setAngry(true);
        }
    }
    
    @Override
    protected void mobTick() {
        this.dataTracker.<Float>set(WolfEntity.WOLF_HEALTH, this.getHealth());
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Float>startTracking(WolfEntity.WOLF_HEALTH, this.getHealth());
        this.dataTracker.<Boolean>startTracking(WolfEntity.BEGGING, false);
        this.dataTracker.<Integer>startTracking(WolfEntity.COLLAR_COLOR, DyeColor.o.getId());
    }
    
    @Override
    protected void playStepSound(final BlockPos pos, final BlockState state) {
        this.playSound(SoundEvents.nD, 0.15f, 1.0f);
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putBoolean("Angry", this.isAngry());
        tag.putByte("CollarColor", (byte)this.getCollarColor().getId());
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setAngry(tag.getBoolean("Angry"));
        if (tag.containsKey("CollarColor", 99)) {
            this.setCollarColor(DyeColor.byId(tag.getInt("CollarColor")));
        }
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isAngry()) {
            return SoundEvents.ny;
        }
        if (this.random.nextInt(3) != 0) {
            return SoundEvents.nw;
        }
        if (this.isTamed() && this.dataTracker.<Float>get(WolfEntity.WOLF_HEALTH) < 10.0f) {
            return SoundEvents.nE;
        }
        return SoundEvents.nB;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.nA;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.nx;
    }
    
    @Override
    protected float getSoundVolume() {
        return 0.4f;
    }
    
    @Override
    public void updateState() {
        super.updateState();
        if (!this.world.isClient && this.wet && !this.canShakeWaterOff && !this.isNavigating() && this.onGround) {
            this.canShakeWaterOff = true;
            this.shakeProgress = 0.0f;
            this.lastShakeProgress = 0.0f;
            this.world.sendEntityStatus(this, (byte)8);
        }
        if (!this.world.isClient && this.getTarget() == null && this.isAngry()) {
            this.setAngry(false);
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        if (!this.isAlive()) {
            return;
        }
        this.lastBegAnimationProgress = this.begAnimationProgress;
        if (this.isBegging()) {
            this.begAnimationProgress += (1.0f - this.begAnimationProgress) * 0.4f;
        }
        else {
            this.begAnimationProgress += (0.0f - this.begAnimationProgress) * 0.4f;
        }
        if (this.isTouchingWater()) {
            this.wet = true;
            this.canShakeWaterOff = false;
            this.shakeProgress = 0.0f;
            this.lastShakeProgress = 0.0f;
        }
        else if ((this.wet || this.canShakeWaterOff) && this.canShakeWaterOff) {
            if (this.shakeProgress == 0.0f) {
                this.playSound(SoundEvents.nC, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
            }
            this.lastShakeProgress = this.shakeProgress;
            this.shakeProgress += 0.05f;
            if (this.lastShakeProgress >= 2.0f) {
                this.wet = false;
                this.canShakeWaterOff = false;
                this.lastShakeProgress = 0.0f;
                this.shakeProgress = 0.0f;
            }
            if (this.shakeProgress > 0.4f) {
                final float float1 = (float)this.getBoundingBox().minY;
                final int integer2 = (int)(MathHelper.sin((this.shakeProgress - 0.4f) * 3.1415927f) * 7.0f);
                final Vec3d vec3d3 = this.getVelocity();
                for (int integer3 = 0; integer3 < integer2; ++integer3) {
                    final float float2 = (this.random.nextFloat() * 2.0f - 1.0f) * this.getWidth() * 0.5f;
                    final float float3 = (this.random.nextFloat() * 2.0f - 1.0f) * this.getWidth() * 0.5f;
                    this.world.addParticle(ParticleTypes.X, this.x + float2, float1 + 0.8f, this.z + float3, vec3d3.x, vec3d3.y, vec3d3.z);
                }
            }
        }
    }
    
    @Override
    public void onDeath(final DamageSource damageSource) {
        this.wet = false;
        this.canShakeWaterOff = false;
        this.lastShakeProgress = 0.0f;
        this.shakeProgress = 0.0f;
        super.onDeath(damageSource);
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isWet() {
        return this.wet;
    }
    
    @Environment(EnvType.CLIENT)
    public float getWetBrightnessMultiplier(final float tickDelta) {
        return 0.75f + MathHelper.lerp(tickDelta, this.lastShakeProgress, this.shakeProgress) / 2.0f * 0.25f;
    }
    
    @Environment(EnvType.CLIENT)
    public float getShakeAnimationProgress(final float tickDelta, final float float2) {
        float float3 = (MathHelper.lerp(tickDelta, this.lastShakeProgress, this.shakeProgress) + float2) / 1.8f;
        if (float3 < 0.0f) {
            float3 = 0.0f;
        }
        else if (float3 > 1.0f) {
            float3 = 1.0f;
        }
        return MathHelper.sin(float3 * 3.1415927f) * MathHelper.sin(float3 * 3.1415927f * 11.0f) * 0.15f * 3.1415927f;
    }
    
    @Environment(EnvType.CLIENT)
    public float getBegAnimationProgress(final float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastBegAnimationProgress, this.begAnimationProgress) * 0.15f * 3.1415927f;
    }
    
    @Override
    protected float getActiveEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        return entitySize.height * 0.8f;
    }
    
    @Override
    public int getLookPitchSpeed() {
        if (this.isSitting()) {
            return 20;
        }
        return super.getLookPitchSpeed();
    }
    
    @Override
    public boolean damage(final DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        final Entity entity3 = source.getAttacker();
        if (this.sitGoal != null) {
            this.sitGoal.setEnabledWithOwner(false);
        }
        if (entity3 != null && !(entity3 instanceof PlayerEntity) && !(entity3 instanceof ProjectileEntity)) {
            amount = (amount + 1.0f) / 2.0f;
        }
        return super.damage(source, amount);
    }
    
    @Override
    public boolean tryAttack(final Entity entity) {
        final boolean boolean2 = entity.damage(DamageSource.mob(this), (float)(int)this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getValue());
        if (boolean2) {
            this.dealDamage(this, entity);
        }
        return boolean2;
    }
    
    @Override
    public void setTamed(final boolean boolean1) {
        super.setTamed(boolean1);
        if (boolean1) {
            this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(20.0);
        }
        else {
            this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(8.0);
        }
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
    }
    
    @Override
    public boolean interactMob(final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack3 = player.getStackInHand(hand);
        final Item item4 = itemStack3.getItem();
        if (this.isTamed()) {
            if (!itemStack3.isEmpty()) {
                if (item4.isFood()) {
                    if (item4.getFoodSetting().isWolfFood() && this.dataTracker.<Float>get(WolfEntity.WOLF_HEALTH) < 20.0f) {
                        if (!player.abilities.creativeMode) {
                            itemStack3.subtractAmount(1);
                        }
                        this.heal((float)item4.getFoodSetting().getHunger());
                        return true;
                    }
                }
                else if (item4 instanceof DyeItem) {
                    final DyeColor dyeColor5 = ((DyeItem)item4).getColor();
                    if (dyeColor5 != this.getCollarColor()) {
                        this.setCollarColor(dyeColor5);
                        if (!player.abilities.creativeMode) {
                            itemStack3.subtractAmount(1);
                        }
                        return true;
                    }
                }
            }
            if (this.isOwner(player) && !this.world.isClient && !this.isBreedingItem(itemStack3)) {
                this.sitGoal.setEnabledWithOwner(!this.isSitting());
                this.jumping = false;
                this.navigation.stop();
                this.setTarget(null);
            }
        }
        else if (item4 == Items.lB && !this.isAngry()) {
            if (!player.abilities.creativeMode) {
                itemStack3.subtractAmount(1);
            }
            if (!this.world.isClient) {
                if (this.random.nextInt(3) == 0) {
                    this.setOwner(player);
                    this.navigation.stop();
                    this.setTarget(null);
                    this.sitGoal.setEnabledWithOwner(true);
                    this.setHealth(20.0f);
                    this.showEmoteParticle(true);
                    this.world.sendEntityStatus(this, (byte)7);
                }
                else {
                    this.showEmoteParticle(false);
                    this.world.sendEntityStatus(this, (byte)6);
                }
            }
            return true;
        }
        return super.interactMob(player, hand);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void handleStatus(final byte status) {
        if (status == 8) {
            this.canShakeWaterOff = true;
            this.shakeProgress = 0.0f;
            this.lastShakeProgress = 0.0f;
        }
        else {
            super.handleStatus(status);
        }
    }
    
    @Environment(EnvType.CLIENT)
    public float ef() {
        if (this.isAngry()) {
            return 1.5393804f;
        }
        if (this.isTamed()) {
            return (0.55f - (this.getHealthMaximum() - this.dataTracker.<Float>get(WolfEntity.WOLF_HEALTH)) * 0.02f) * 3.1415927f;
        }
        return 0.62831855f;
    }
    
    @Override
    public boolean isBreedingItem(final ItemStack stack) {
        final Item item2 = stack.getItem();
        return item2.isFood() && item2.getFoodSetting().isWolfFood();
    }
    
    @Override
    public int getLimitPerChunk() {
        return 8;
    }
    
    public boolean isAngry() {
        return (this.dataTracker.<Byte>get(WolfEntity.TAMEABLE_FLAGS) & 0x2) != 0x0;
    }
    
    public void setAngry(final boolean boolean1) {
        final byte byte2 = this.dataTracker.<Byte>get(WolfEntity.TAMEABLE_FLAGS);
        if (boolean1) {
            this.dataTracker.<Byte>set(WolfEntity.TAMEABLE_FLAGS, (byte)(byte2 | 0x2));
        }
        else {
            this.dataTracker.<Byte>set(WolfEntity.TAMEABLE_FLAGS, (byte)(byte2 & 0xFFFFFFFD));
        }
    }
    
    public DyeColor getCollarColor() {
        return DyeColor.byId(this.dataTracker.<Integer>get(WolfEntity.COLLAR_COLOR));
    }
    
    public void setCollarColor(final DyeColor color) {
        this.dataTracker.<Integer>set(WolfEntity.COLLAR_COLOR, color.getId());
    }
    
    @Override
    public WolfEntity createChild(final PassiveEntity mate) {
        final WolfEntity wolfEntity2 = EntityType.WOLF.create(this.world);
        final UUID uUID3 = this.getOwnerUuid();
        if (uUID3 != null) {
            wolfEntity2.setOwnerUuid(uUID3);
            wolfEntity2.setTamed(true);
        }
        return wolfEntity2;
    }
    
    public void setBegging(final boolean begging) {
        this.dataTracker.<Boolean>set(WolfEntity.BEGGING, begging);
    }
    
    @Override
    public boolean canBreedWith(final AnimalEntity other) {
        if (other == this) {
            return false;
        }
        if (!this.isTamed()) {
            return false;
        }
        if (!(other instanceof WolfEntity)) {
            return false;
        }
        final WolfEntity wolfEntity2 = (WolfEntity)other;
        return wolfEntity2.isTamed() && !wolfEntity2.isSitting() && this.isInLove() && wolfEntity2.isInLove();
    }
    
    public boolean isBegging() {
        return this.dataTracker.<Boolean>get(WolfEntity.BEGGING);
    }
    
    @Override
    public boolean canAttackWithOwner(final LivingEntity target, final LivingEntity owner) {
        if (target instanceof CreeperEntity || target instanceof GhastEntity) {
            return false;
        }
        if (target instanceof WolfEntity) {
            final WolfEntity wolfEntity3 = (WolfEntity)target;
            if (wolfEntity3.isTamed() && wolfEntity3.getOwner() == owner) {
                return false;
            }
        }
        return (!(target instanceof PlayerEntity) || !(owner instanceof PlayerEntity) || ((PlayerEntity)owner).shouldDamagePlayer((PlayerEntity)target)) && (!(target instanceof HorseBaseEntity) || !((HorseBaseEntity)target).isTame()) && (!(target instanceof CatEntity) || !((CatEntity)target).isTamed());
    }
    
    @Override
    public boolean canBeLeashedBy(final PlayerEntity player) {
        return !this.isAngry() && super.canBeLeashedBy(player);
    }
    
    static {
        WOLF_HEALTH = DataTracker.<Float>registerData(WolfEntity.class, TrackedDataHandlerRegistry.FLOAT);
        BEGGING = DataTracker.<Boolean>registerData(WolfEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        COLLAR_COLOR = DataTracker.<Integer>registerData(WolfEntity.class, TrackedDataHandlerRegistry.INTEGER);
        final EntityType<?> entityType2;
        FOLLOW_TAMED_PREDICATE = (livingEntity -> {
            entityType2 = livingEntity.getType();
            return entityType2 == EntityType.SHEEP || entityType2 == EntityType.RABBIT || entityType2 == EntityType.B;
        });
    }
    
    class AvoidLlamaGoal<T extends LivingEntity> extends FleeEntityGoal<T>
    {
        private final WolfEntity wolf;
        
        public AvoidLlamaGoal(final WolfEntity wolfEntity2, final Class<T> class3, final float float4, final double double5, final double double7) {
            super(wolfEntity2, class3, float4, double5, double7);
            this.wolf = wolfEntity2;
        }
        
        @Override
        public boolean canStart() {
            return super.canStart() && this.targetEntity instanceof LlamaEntity && !this.wolf.isTamed() && this.isScaredOf((LlamaEntity)this.targetEntity);
        }
        
        private boolean isScaredOf(final LlamaEntity llama) {
            return llama.getStrength() >= WolfEntity.this.random.nextInt(5);
        }
        
        @Override
        public void start() {
            WolfEntity.this.setTarget(null);
            super.start();
        }
        
        @Override
        public void tick() {
            WolfEntity.this.setTarget(null);
            super.tick();
        }
    }
}
