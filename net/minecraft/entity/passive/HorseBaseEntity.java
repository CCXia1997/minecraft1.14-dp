package net.minecraft.entity.passive;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.IWorld;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.Vec3d;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.item.Items;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.block.Blocks;
import net.minecraft.sound.SoundEvent;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.block.BlockState;
import java.util.Iterator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.EntityGroup;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.HorseBondWithPlayerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.BasicInventory;
import java.util.UUID;
import java.util.Optional;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.LivingEntity;
import java.util.function.Predicate;
import net.minecraft.entity.JumpingMount;
import net.minecraft.inventory.InventoryListener;

public abstract class HorseBaseEntity extends AnimalEntity implements InventoryListener, JumpingMount
{
    private static final Predicate<LivingEntity> IS_BRED_HORSE;
    private static final TargetPredicate PARENT_HORSE_PREDICATE;
    protected static final EntityAttribute JUMP_STRENGTH;
    private static final TrackedData<Byte> HORSE_FLAGS;
    private static final TrackedData<Optional<UUID>> OWNER_UUID;
    private int eatingGrassTicks;
    private int eatingTicks;
    private int angryTicks;
    public int bA;
    public int bB;
    protected boolean inAir;
    protected BasicInventory items;
    protected int temper;
    protected float jumpStrength;
    private boolean jumping;
    private float eatingGrassAnimationProgress;
    private float lastEatingGrassAnimationProgress;
    private float angryAnimationProgress;
    private float lastAngryAnimationProgress;
    private float eatingAnimationProgress;
    private float lastEatingAnimationProgress;
    protected boolean bH;
    protected int soundTicks;
    
    protected HorseBaseEntity(final EntityType<? extends HorseBaseEntity> type, final World world) {
        super(type, world);
        this.bH = true;
        this.stepHeight = 1.0f;
        this.em();
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.2));
        this.goalSelector.add(1, new HorseBondWithPlayerGoal(this, 1.2));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0, HorseBaseEntity.class));
        this.goalSelector.add(4, new FollowParentGoal(this, 1.0));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.7));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.initCustomGoals();
    }
    
    protected void initCustomGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Byte>startTracking(HorseBaseEntity.HORSE_FLAGS, (Byte)0);
        this.dataTracker.<Optional<UUID>>startTracking(HorseBaseEntity.OWNER_UUID, Optional.<UUID>empty());
    }
    
    protected boolean getHorseFlag(final int integer) {
        return (this.dataTracker.<Byte>get(HorseBaseEntity.HORSE_FLAGS) & integer) != 0x0;
    }
    
    protected void setHorseFlag(final int index, final boolean boolean2) {
        final byte byte3 = this.dataTracker.<Byte>get(HorseBaseEntity.HORSE_FLAGS);
        if (boolean2) {
            this.dataTracker.<Byte>set(HorseBaseEntity.HORSE_FLAGS, (byte)(byte3 | index));
        }
        else {
            this.dataTracker.<Byte>set(HorseBaseEntity.HORSE_FLAGS, (byte)(byte3 & ~index));
        }
    }
    
    public boolean isTame() {
        return this.getHorseFlag(2);
    }
    
    @Nullable
    public UUID getOwnerUuid() {
        return this.dataTracker.<Optional<UUID>>get(HorseBaseEntity.OWNER_UUID).orElse(null);
    }
    
    public void setOwnerUuid(@Nullable final UUID uUID) {
        this.dataTracker.<Optional<UUID>>set(HorseBaseEntity.OWNER_UUID, Optional.<UUID>ofNullable(uUID));
    }
    
    public boolean isInAir() {
        return this.inAir;
    }
    
    public void setTame(final boolean boolean1) {
        this.setHorseFlag(2, boolean1);
    }
    
    public void setInAir(final boolean inAir) {
        this.inAir = inAir;
    }
    
    @Override
    public boolean canBeLeashedBy(final PlayerEntity player) {
        return super.canBeLeashedBy(player) && this.getGroup() != EntityGroup.UNDEAD;
    }
    
    @Override
    protected void updateForLeashLength(final float leashLength) {
        if (leashLength > 6.0f && this.isEatingGrass()) {
            this.setEatingGrass(false);
        }
    }
    
    public boolean isEatingGrass() {
        return this.getHorseFlag(16);
    }
    
    public boolean isAngry() {
        return this.getHorseFlag(32);
    }
    
    public boolean isBred() {
        return this.getHorseFlag(8);
    }
    
    public void setBred(final boolean boolean1) {
        this.setHorseFlag(8, boolean1);
    }
    
    public void setSaddled(final boolean boolean1) {
        this.setHorseFlag(4, boolean1);
    }
    
    public int getTemper() {
        return this.temper;
    }
    
    public void setTemper(final int temper) {
        this.temper = temper;
    }
    
    public int addTemper(final int difference) {
        final int integer2 = MathHelper.clamp(this.getTemper() + difference, 0, this.getMaxTemper());
        this.setTemper(integer2);
        return integer2;
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        final Entity entity3 = source.getAttacker();
        return (!this.hasPassengers() || entity3 == null || !this.y(entity3)) && super.damage(source, amount);
    }
    
    @Override
    public boolean isPushable() {
        return !this.hasPassengers();
    }
    
    private void playEatingAnimation() {
        this.setEating();
        if (!this.isSilent()) {
            this.world.playSound(null, this.x, this.y, this.z, SoundEvents.eJ, this.getSoundCategory(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
        }
    }
    
    @Override
    public void handleFallDamage(final float fallDistance, final float damageMultiplier) {
        if (fallDistance > 1.0f) {
            this.playSound(SoundEvents.eN, 0.4f, 1.0f);
        }
        final int integer3 = MathHelper.ceil((fallDistance * 0.5f - 3.0f) * damageMultiplier);
        if (integer3 <= 0) {
            return;
        }
        this.damage(DamageSource.FALL, (float)integer3);
        if (this.hasPassengers()) {
            for (final Entity entity5 : this.getPassengersDeep()) {
                entity5.damage(DamageSource.FALL, (float)integer3);
            }
        }
        final BlockState blockState4 = this.world.getBlockState(new BlockPos(this.x, this.y - 0.2 - this.prevYaw, this.z));
        if (!blockState4.isAir() && !this.isSilent()) {
            final BlockSoundGroup blockSoundGroup5 = blockState4.getSoundGroup();
            this.world.playSound(null, this.x, this.y, this.z, blockSoundGroup5.getStepSound(), this.getSoundCategory(), blockSoundGroup5.getVolume() * 0.5f, blockSoundGroup5.getPitch() * 0.75f);
        }
    }
    
    protected int getInventorySize() {
        return 2;
    }
    
    protected void em() {
        final BasicInventory basicInventory1 = this.items;
        this.items = new BasicInventory(this.getInventorySize());
        if (basicInventory1 != null) {
            basicInventory1.removeListener(this);
            for (int integer2 = Math.min(basicInventory1.getInvSize(), this.items.getInvSize()), integer3 = 0; integer3 < integer2; ++integer3) {
                final ItemStack itemStack4 = basicInventory1.getInvStack(integer3);
                if (!itemStack4.isEmpty()) {
                    this.items.setInvStack(integer3, itemStack4.copy());
                }
            }
        }
        this.items.addListener(this);
        this.updateSaddle();
    }
    
    protected void updateSaddle() {
        if (this.world.isClient) {
            return;
        }
        this.setSaddled(!this.items.getInvStack(0).isEmpty() && this.canBeSaddled());
    }
    
    @Override
    public void onInvChange(final Inventory inventory) {
        final boolean boolean2 = this.isSaddled();
        this.updateSaddle();
        if (this.age > 20 && !boolean2 && this.isSaddled()) {
            this.playSound(SoundEvents.eO, 0.5f, 1.0f);
        }
    }
    
    public double getJumpStrength() {
        return this.getAttributeInstance(HorseBaseEntity.JUMP_STRENGTH).getValue();
    }
    
    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }
    
    @Nullable
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        if (this.random.nextInt(3) == 0) {
            this.updateAnger();
        }
        return null;
    }
    
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        if (this.random.nextInt(10) == 0 && !this.cannotMove()) {
            this.updateAnger();
        }
        return null;
    }
    
    public boolean canBeSaddled() {
        return true;
    }
    
    public boolean isSaddled() {
        return this.getHorseFlag(4);
    }
    
    @Nullable
    protected SoundEvent getAngrySound() {
        this.updateAnger();
        return null;
    }
    
    @Override
    protected void playStepSound(final BlockPos pos, final BlockState state) {
        if (state.getMaterial().isLiquid()) {
            return;
        }
        final BlockState blockState3 = this.world.getBlockState(pos.up());
        BlockSoundGroup blockSoundGroup4 = state.getSoundGroup();
        if (blockState3.getBlock() == Blocks.cA) {
            blockSoundGroup4 = blockState3.getSoundGroup();
        }
        if (this.hasPassengers() && this.bH) {
            ++this.soundTicks;
            if (this.soundTicks > 5 && this.soundTicks % 3 == 0) {
                this.playWalkSound(blockSoundGroup4);
            }
            else if (this.soundTicks <= 5) {
                this.playSound(SoundEvents.eQ, blockSoundGroup4.getVolume() * 0.15f, blockSoundGroup4.getPitch());
            }
        }
        else if (blockSoundGroup4 == BlockSoundGroup.WOOD) {
            this.playSound(SoundEvents.eQ, blockSoundGroup4.getVolume() * 0.15f, blockSoundGroup4.getPitch());
        }
        else {
            this.playSound(SoundEvents.eP, blockSoundGroup4.getVolume() * 0.15f, blockSoundGroup4.getPitch());
        }
    }
    
    protected void playWalkSound(final BlockSoundGroup group) {
        this.playSound(SoundEvents.eK, group.getVolume() * 0.15f, group.getPitch());
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeContainer().register(HorseBaseEntity.JUMP_STRENGTH);
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(53.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.22499999403953552);
    }
    
    @Override
    public int getLimitPerChunk() {
        return 6;
    }
    
    public int getMaxTemper() {
        return 100;
    }
    
    @Override
    protected float getSoundVolume() {
        return 0.8f;
    }
    
    @Override
    public int getMinAmbientSoundDelay() {
        return 400;
    }
    
    public void openInventory(final PlayerEntity player) {
        if (!this.world.isClient && (!this.hasPassengers() || this.hasPassenger(player)) && this.isTame()) {
            player.openHorseInventory(this, this.items);
        }
    }
    
    protected boolean receiveFood(final PlayerEntity player, final ItemStack item) {
        boolean boolean3 = false;
        float float4 = 0.0f;
        int integer5 = 0;
        int integer6 = 0;
        final Item item2 = item.getItem();
        if (item2 == Items.jP) {
            float4 = 2.0f;
            integer5 = 20;
            integer6 = 3;
        }
        else if (item2 == Items.lC) {
            float4 = 1.0f;
            integer5 = 30;
            integer6 = 3;
        }
        else if (item2 == Blocks.gs.getItem()) {
            float4 = 20.0f;
            integer5 = 180;
        }
        else if (item2 == Items.je) {
            float4 = 3.0f;
            integer5 = 60;
            integer6 = 3;
        }
        else if (item2 == Items.nN) {
            float4 = 4.0f;
            integer5 = 60;
            integer6 = 5;
            if (this.isTame() && this.getBreedingAge() == 0 && !this.isInLove()) {
                boolean3 = true;
                this.lovePlayer(player);
            }
        }
        else if (item2 == Items.kp || item2 == Items.kq) {
            float4 = 10.0f;
            integer5 = 240;
            integer6 = 10;
            if (this.isTame() && this.getBreedingAge() == 0 && !this.isInLove()) {
                boolean3 = true;
                this.lovePlayer(player);
            }
        }
        if (this.getHealth() < this.getHealthMaximum() && float4 > 0.0f) {
            this.heal(float4);
            boolean3 = true;
        }
        if (this.isChild() && integer5 > 0) {
            this.world.addParticle(ParticleTypes.C, this.x + this.random.nextFloat() * this.getWidth() * 2.0f - this.getWidth(), this.y + 0.5 + this.random.nextFloat() * this.getHeight(), this.z + this.random.nextFloat() * this.getWidth() * 2.0f - this.getWidth(), 0.0, 0.0, 0.0);
            if (!this.world.isClient) {
                this.growUp(integer5);
            }
            boolean3 = true;
        }
        if (integer6 > 0 && (boolean3 || !this.isTame()) && this.getTemper() < this.getMaxTemper()) {
            boolean3 = true;
            if (!this.world.isClient) {
                this.addTemper(integer6);
            }
        }
        if (boolean3) {
            this.playEatingAnimation();
        }
        return boolean3;
    }
    
    protected void putPlayerOnBack(final PlayerEntity playerEntity) {
        this.setEatingGrass(false);
        this.setAngry(false);
        if (!this.world.isClient) {
            playerEntity.yaw = this.yaw;
            playerEntity.pitch = this.pitch;
            playerEntity.startRiding(this);
        }
    }
    
    @Override
    protected boolean cannotMove() {
        return (super.cannotMove() && this.hasPassengers() && this.isSaddled()) || this.isEatingGrass() || this.isAngry();
    }
    
    @Override
    public boolean isBreedingItem(final ItemStack stack) {
        return false;
    }
    
    private void dY() {
        this.bA = 1;
    }
    
    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.items == null) {
            return;
        }
        for (int integer1 = 0; integer1 < this.items.getInvSize(); ++integer1) {
            final ItemStack itemStack2 = this.items.getInvStack(integer1);
            if (!itemStack2.isEmpty()) {
                this.dropStack(itemStack2);
            }
        }
    }
    
    @Override
    public void updateState() {
        if (this.random.nextInt(200) == 0) {
            this.dY();
        }
        super.updateState();
        if (this.world.isClient || !this.isAlive()) {
            return;
        }
        if (this.random.nextInt(900) == 0 && this.deathTime == 0) {
            this.heal(1.0f);
        }
        if (this.eatsGrass()) {
            if (!this.isEatingGrass() && !this.hasPassengers() && this.random.nextInt(300) == 0 && this.world.getBlockState(new BlockPos(this).down()).getBlock() == Blocks.i) {
                this.setEatingGrass(true);
            }
            if (this.isEatingGrass() && ++this.eatingGrassTicks > 50) {
                this.eatingGrassTicks = 0;
                this.setEatingGrass(false);
            }
        }
        this.walkToParent();
    }
    
    protected void walkToParent() {
        if (this.isBred() && this.isChild() && !this.isEatingGrass()) {
            final LivingEntity livingEntity1 = this.world.<LivingEntity>getClosestEntity(HorseBaseEntity.class, HorseBaseEntity.PARENT_HORSE_PREDICATE, (LivingEntity)this, this.x, this.y, this.z, this.getBoundingBox().stretch(16.0, 16.0, 16.0));
            if (livingEntity1 != null && this.squaredDistanceTo(livingEntity1) > 4.0) {
                this.navigation.findPathTo(livingEntity1);
            }
        }
    }
    
    public boolean eatsGrass() {
        return true;
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.eatingTicks > 0 && ++this.eatingTicks > 30) {
            this.eatingTicks = 0;
            this.setHorseFlag(64, false);
        }
        if ((this.isLogicalSideForUpdatingMovement() || this.canMoveVoluntarily()) && this.angryTicks > 0 && ++this.angryTicks > 20) {
            this.angryTicks = 0;
            this.setAngry(false);
        }
        if (this.bA > 0 && ++this.bA > 8) {
            this.bA = 0;
        }
        if (this.bB > 0) {
            ++this.bB;
            if (this.bB > 300) {
                this.bB = 0;
            }
        }
        this.lastEatingGrassAnimationProgress = this.eatingGrassAnimationProgress;
        if (this.isEatingGrass()) {
            this.eatingGrassAnimationProgress += (1.0f - this.eatingGrassAnimationProgress) * 0.4f + 0.05f;
            if (this.eatingGrassAnimationProgress > 1.0f) {
                this.eatingGrassAnimationProgress = 1.0f;
            }
        }
        else {
            this.eatingGrassAnimationProgress += (0.0f - this.eatingGrassAnimationProgress) * 0.4f - 0.05f;
            if (this.eatingGrassAnimationProgress < 0.0f) {
                this.eatingGrassAnimationProgress = 0.0f;
            }
        }
        this.lastAngryAnimationProgress = this.angryAnimationProgress;
        if (this.isAngry()) {
            this.eatingGrassAnimationProgress = 0.0f;
            this.lastEatingGrassAnimationProgress = this.eatingGrassAnimationProgress;
            this.angryAnimationProgress += (1.0f - this.angryAnimationProgress) * 0.4f + 0.05f;
            if (this.angryAnimationProgress > 1.0f) {
                this.angryAnimationProgress = 1.0f;
            }
        }
        else {
            this.jumping = false;
            this.angryAnimationProgress += (0.8f * this.angryAnimationProgress * this.angryAnimationProgress * this.angryAnimationProgress - this.angryAnimationProgress) * 0.6f - 0.05f;
            if (this.angryAnimationProgress < 0.0f) {
                this.angryAnimationProgress = 0.0f;
            }
        }
        this.lastEatingAnimationProgress = this.eatingAnimationProgress;
        if (this.getHorseFlag(64)) {
            this.eatingAnimationProgress += (1.0f - this.eatingAnimationProgress) * 0.7f + 0.05f;
            if (this.eatingAnimationProgress > 1.0f) {
                this.eatingAnimationProgress = 1.0f;
            }
        }
        else {
            this.eatingAnimationProgress += (0.0f - this.eatingAnimationProgress) * 0.7f - 0.05f;
            if (this.eatingAnimationProgress < 0.0f) {
                this.eatingAnimationProgress = 0.0f;
            }
        }
    }
    
    private void setEating() {
        if (!this.world.isClient) {
            this.eatingTicks = 1;
            this.setHorseFlag(64, true);
        }
    }
    
    public void setEatingGrass(final boolean eatingGrass) {
        this.setHorseFlag(16, eatingGrass);
    }
    
    public void setAngry(final boolean angry) {
        if (angry) {
            this.setEatingGrass(false);
        }
        this.setHorseFlag(32, angry);
    }
    
    private void updateAnger() {
        if (this.isLogicalSideForUpdatingMovement() || this.canMoveVoluntarily()) {
            this.angryTicks = 1;
            this.setAngry(true);
        }
    }
    
    public void playAngrySound() {
        this.updateAnger();
        final SoundEvent soundEvent1 = this.getAngrySound();
        if (soundEvent1 != null) {
            this.playSound(soundEvent1, this.getSoundVolume(), this.getSoundPitch());
        }
    }
    
    public boolean bondWithPlayer(final PlayerEntity player) {
        this.setOwnerUuid(player.getUuid());
        this.setTame(true);
        if (player instanceof ServerPlayerEntity) {
            Criterions.TAME_ANIMAL.handle((ServerPlayerEntity)player, this);
        }
        this.world.sendEntityStatus(this, (byte)7);
        return true;
    }
    
    @Override
    public void travel(final Vec3d movementInput) {
        if (!this.isAlive()) {
            return;
        }
        if (!this.hasPassengers() || !this.canBeControlledByRider() || !this.isSaddled()) {
            this.aO = 0.02f;
            super.travel(movementInput);
            return;
        }
        final LivingEntity livingEntity2 = (LivingEntity)this.getPrimaryPassenger();
        this.yaw = livingEntity2.yaw;
        this.prevYaw = this.yaw;
        this.pitch = livingEntity2.pitch * 0.5f;
        this.setRotation(this.yaw, this.pitch);
        this.aK = this.yaw;
        this.headYaw = this.aK;
        float float3 = livingEntity2.sidewaysSpeed * 0.5f;
        float float4 = livingEntity2.forwardSpeed;
        if (float4 <= 0.0f) {
            float4 *= 0.25f;
            this.soundTicks = 0;
        }
        if (this.onGround && this.jumpStrength == 0.0f && this.isAngry() && !this.jumping) {
            float3 = 0.0f;
            float4 = 0.0f;
        }
        if (this.jumpStrength > 0.0f && !this.isInAir() && this.onGround) {
            final double double5 = this.getJumpStrength() * this.jumpStrength;
            double double6;
            if (this.hasStatusEffect(StatusEffects.h)) {
                double6 = double5 + (this.getStatusEffect(StatusEffects.h).getAmplifier() + 1) * 0.1f;
            }
            else {
                double6 = double5;
            }
            final Vec3d vec3d9 = this.getVelocity();
            this.setVelocity(vec3d9.x, double6, vec3d9.z);
            this.setInAir(true);
            this.velocityDirty = true;
            if (float4 > 0.0f) {
                final float float5 = MathHelper.sin(this.yaw * 0.017453292f);
                final float float6 = MathHelper.cos(this.yaw * 0.017453292f);
                this.setVelocity(this.getVelocity().add(-0.4f * float5 * this.jumpStrength, 0.0, 0.4f * float6 * this.jumpStrength));
                this.playJumpSound();
            }
            this.jumpStrength = 0.0f;
        }
        this.aO = this.getMovementSpeed() * 0.1f;
        if (this.isLogicalSideForUpdatingMovement()) {
            this.setMovementSpeed((float)this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue());
            super.travel(new Vec3d(float3, movementInput.y, float4));
        }
        else if (livingEntity2 instanceof PlayerEntity) {
            this.setVelocity(Vec3d.ZERO);
        }
        if (this.onGround) {
            this.jumpStrength = 0.0f;
            this.setInAir(false);
        }
        this.lastLimbDistance = this.limbDistance;
        final double double5 = this.x - this.prevX;
        double double6 = this.z - this.prevZ;
        float float7 = MathHelper.sqrt(double5 * double5 + double6 * double6) * 4.0f;
        if (float7 > 1.0f) {
            float7 = 1.0f;
        }
        this.limbDistance += (float7 - this.limbDistance) * 0.4f;
        this.limbAngle += this.limbDistance;
    }
    
    protected void playJumpSound() {
        this.playSound(SoundEvents.eM, 0.4f, 1.0f);
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putBoolean("EatingHaystack", this.isEatingGrass());
        tag.putBoolean("Bred", this.isBred());
        tag.putInt("Temper", this.getTemper());
        tag.putBoolean("Tame", this.isTame());
        if (this.getOwnerUuid() != null) {
            tag.putString("OwnerUUID", this.getOwnerUuid().toString());
        }
        if (!this.items.getInvStack(0).isEmpty()) {
            tag.put("SaddleItem", this.items.getInvStack(0).toTag(new CompoundTag()));
        }
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setEatingGrass(tag.getBoolean("EatingHaystack"));
        this.setBred(tag.getBoolean("Bred"));
        this.setTemper(tag.getInt("Temper"));
        this.setTame(tag.getBoolean("Tame"));
        String string2;
        if (tag.containsKey("OwnerUUID", 8)) {
            string2 = tag.getString("OwnerUUID");
        }
        else {
            final String string3 = tag.getString("Owner");
            string2 = ServerConfigHandler.getPlayerUuidByName(this.getServer(), string3);
        }
        if (!string2.isEmpty()) {
            this.setOwnerUuid(UUID.fromString(string2));
        }
        final EntityAttributeInstance entityAttributeInstance3 = this.getAttributeContainer().get("Speed");
        if (entityAttributeInstance3 != null) {
            this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(entityAttributeInstance3.getBaseValue() * 0.25);
        }
        if (tag.containsKey("SaddleItem", 10)) {
            final ItemStack itemStack4 = ItemStack.fromTag(tag.getCompound("SaddleItem"));
            if (itemStack4.getItem() == Items.kB) {
                this.items.setInvStack(0, itemStack4);
            }
        }
        this.updateSaddle();
    }
    
    @Override
    public boolean canBreedWith(final AnimalEntity other) {
        return false;
    }
    
    protected boolean canBreed() {
        return !this.hasPassengers() && !this.hasVehicle() && this.isTame() && !this.isChild() && this.getHealth() >= this.getHealthMaximum() && this.isInLove();
    }
    
    @Nullable
    @Override
    public PassiveEntity createChild(final PassiveEntity mate) {
        return null;
    }
    
    protected void setChildAttributes(final PassiveEntity mate, final HorseBaseEntity child) {
        final double double3 = this.getAttributeInstance(EntityAttributes.MAX_HEALTH).getBaseValue() + mate.getAttributeInstance(EntityAttributes.MAX_HEALTH).getBaseValue() + this.getChildHealthBonus();
        child.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(double3 / 3.0);
        final double double4 = this.getAttributeInstance(HorseBaseEntity.JUMP_STRENGTH).getBaseValue() + mate.getAttributeInstance(HorseBaseEntity.JUMP_STRENGTH).getBaseValue() + this.getChildJumpStrengthBonus();
        child.getAttributeInstance(HorseBaseEntity.JUMP_STRENGTH).setBaseValue(double4 / 3.0);
        final double double5 = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getBaseValue() + mate.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getBaseValue() + this.getChildMovementSpeedBonus();
        child.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(double5 / 3.0);
    }
    
    @Override
    public boolean canBeControlledByRider() {
        return this.getPrimaryPassenger() instanceof LivingEntity;
    }
    
    @Environment(EnvType.CLIENT)
    public float getEatingGrassAnimationProgress(final float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastEatingGrassAnimationProgress, this.eatingGrassAnimationProgress);
    }
    
    @Environment(EnvType.CLIENT)
    public float getAngryAnimationProgress(final float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastAngryAnimationProgress, this.angryAnimationProgress);
    }
    
    @Environment(EnvType.CLIENT)
    public float getEatingAnimationProgress(final float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastEatingAnimationProgress, this.eatingAnimationProgress);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void setJumpStrength(int integer) {
        if (!this.isSaddled()) {
            return;
        }
        if (integer < 0) {
            integer = 0;
        }
        else {
            this.jumping = true;
            this.updateAnger();
        }
        if (integer >= 90) {
            this.jumpStrength = 1.0f;
        }
        else {
            this.jumpStrength = 0.4f + 0.4f * integer / 90.0f;
        }
    }
    
    @Override
    public boolean canJump() {
        return this.isSaddled();
    }
    
    @Override
    public void startJumping(final int integer) {
        this.jumping = true;
        this.updateAnger();
    }
    
    @Override
    public void stopJumping() {
    }
    
    @Environment(EnvType.CLIENT)
    protected void spawnPlayerReactionParticles(final boolean positive) {
        final ParticleParameters particleParameters2 = positive ? ParticleTypes.E : ParticleTypes.Q;
        for (int integer3 = 0; integer3 < 7; ++integer3) {
            final double double4 = this.random.nextGaussian() * 0.02;
            final double double5 = this.random.nextGaussian() * 0.02;
            final double double6 = this.random.nextGaussian() * 0.02;
            this.world.addParticle(particleParameters2, this.x + this.random.nextFloat() * this.getWidth() * 2.0f - this.getWidth(), this.y + 0.5 + this.random.nextFloat() * this.getHeight(), this.z + this.random.nextFloat() * this.getWidth() * 2.0f - this.getWidth(), double4, double5, double6);
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void handleStatus(final byte status) {
        if (status == 7) {
            this.spawnPlayerReactionParticles(true);
        }
        else if (status == 6) {
            this.spawnPlayerReactionParticles(false);
        }
        else {
            super.handleStatus(status);
        }
    }
    
    @Override
    public void updatePassengerPosition(final Entity passenger) {
        super.updatePassengerPosition(passenger);
        if (passenger instanceof MobEntity) {
            final MobEntity mobEntity2 = (MobEntity)passenger;
            this.aK = mobEntity2.aK;
        }
        if (this.lastAngryAnimationProgress > 0.0f) {
            final float float2 = MathHelper.sin(this.aK * 0.017453292f);
            final float float3 = MathHelper.cos(this.aK * 0.017453292f);
            final float float4 = 0.7f * this.lastAngryAnimationProgress;
            final float float5 = 0.15f * this.lastAngryAnimationProgress;
            passenger.setPosition(this.x + float4 * float2, this.y + this.getMountedHeightOffset() + passenger.getHeightOffset() + float5, this.z - float4 * float3);
            if (passenger instanceof LivingEntity) {
                ((LivingEntity)passenger).aK = this.aK;
            }
        }
    }
    
    protected float getChildHealthBonus() {
        return 15.0f + this.random.nextInt(8) + this.random.nextInt(9);
    }
    
    protected double getChildJumpStrengthBonus() {
        return 0.4000000059604645 + this.random.nextDouble() * 0.2 + this.random.nextDouble() * 0.2 + this.random.nextDouble() * 0.2;
    }
    
    protected double getChildMovementSpeedBonus() {
        return (0.44999998807907104 + this.random.nextDouble() * 0.3 + this.random.nextDouble() * 0.3 + this.random.nextDouble() * 0.3) * 0.25;
    }
    
    @Override
    public boolean isClimbing() {
        return false;
    }
    
    @Override
    protected float getActiveEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        return entitySize.height * 0.95f;
    }
    
    public boolean canEquip() {
        return false;
    }
    
    public boolean canEquip(final ItemStack item) {
        return false;
    }
    
    @Override
    public boolean equip(final int slot, final ItemStack item) {
        final int integer3 = slot - 400;
        if (integer3 >= 0 && integer3 < 2 && integer3 < this.items.getInvSize()) {
            if (integer3 == 0 && item.getItem() != Items.kB) {
                return false;
            }
            if (integer3 == 1 && (!this.canEquip() || !this.canEquip(item))) {
                return false;
            }
            this.items.setInvStack(integer3, item);
            this.updateSaddle();
            return true;
        }
        else {
            final int integer4 = slot - 500 + 2;
            if (integer4 >= 2 && integer4 < this.items.getInvSize()) {
                this.items.setInvStack(integer4, item);
                return true;
            }
            return false;
        }
    }
    
    @Nullable
    @Override
    public Entity getPrimaryPassenger() {
        if (this.getPassengerList().isEmpty()) {
            return null;
        }
        return this.getPassengerList().get(0);
    }
    
    @Nullable
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable EntityData entityData, @Nullable final CompoundTag compoundTag) {
        entityData = super.initialize(iWorld, localDifficulty, difficulty, entityData, compoundTag);
        if (this.random.nextInt(5) == 0) {
            this.setBreedingAge(-24000);
        }
        return entityData;
    }
    
    static {
        IS_BRED_HORSE = (livingEntity -> livingEntity instanceof HorseBaseEntity && livingEntity.isBred());
        PARENT_HORSE_PREDICATE = new TargetPredicate().setBaseMaxDistance(16.0).includeInvulnerable().includeTeammates().includeHidden().setPredicate(HorseBaseEntity.IS_BRED_HORSE);
        JUMP_STRENGTH = new ClampedEntityAttribute(null, "horse.jumpStrength", 0.7, 0.0, 2.0).setName("Jump Strength").setTracked(true);
        HORSE_FLAGS = DataTracker.<Byte>registerData(HorseBaseEntity.class, TrackedDataHandlerRegistry.BYTE);
        OWNER_UUID = DataTracker.<Optional<UUID>>registerData(HorseBaseEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    }
}
