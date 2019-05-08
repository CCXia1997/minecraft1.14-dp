package net.minecraft.entity.passive;

import net.minecraft.state.AbstractPropertyContainer;
import java.util.AbstractList;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.goal.DiveJumpingGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.GoToVillageGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.state.property.Property;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.block.Blocks;
import net.minecraft.world.ViewableWorld;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.ai.goal.EscapeSunlightGoal;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.stat.Stats;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.control.MoveControl;
import java.util.EnumSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Comparator;
import net.minecraft.world.biome.Biomes;
import java.util.Arrays;
import java.util.Map;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import java.util.Random;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import java.util.Iterator;
import net.minecraft.nbt.Tag;
import net.minecraft.util.TagHelper;
import net.minecraft.nbt.ListTag;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.minecraft.world.biome.Biome;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.IWorld;
import net.minecraft.entity.attribute.EntityAttributes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ItemStackParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import java.util.function.Predicate;
import java.util.UUID;
import java.util.Optional;
import net.minecraft.entity.data.TrackedData;

public class FoxEntity extends AnimalEntity
{
    private static final TrackedData<Integer> TYPE;
    private static final TrackedData<Byte> FOX_FLAGS;
    private static final TrackedData<Optional<UUID>> OWNER;
    private static final TrackedData<Optional<UUID>> OTHER_TRUSTED;
    private static final Predicate<ItemEntity> PICKABLE_DROP_FILTER;
    private static final Predicate<Entity> JUST_ATTACKED_SOMETHING_FILTER;
    private static final Predicate<Entity> CHICKEN_AND_RABBIT_FILTER;
    private static final Predicate<Entity> NOTICEABLE_PLAYER_FILTER;
    private Goal followChickenAndRabbitGoal;
    private Goal followBabyTurtleGoal;
    private Goal followFishGoal;
    private float headRollProgress;
    private float lastHeadRollProgress;
    private float extraRollingHeight;
    private float lastExtraRollingHeight;
    private int eatingTime;
    
    public FoxEntity(final EntityType<? extends FoxEntity> type, final World world) {
        super(type, world);
        this.lookControl = new FoxLookControl();
        this.moveControl = new FoxMoveControl();
        this.setPathNodeTypeWeight(PathNodeType.n, 0.0f);
        this.setPathNodeTypeWeight(PathNodeType.o, 0.0f);
        this.setCanPickUpLoot(true);
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Optional<UUID>>startTracking(FoxEntity.OWNER, Optional.<UUID>empty());
        this.dataTracker.<Optional<UUID>>startTracking(FoxEntity.OTHER_TRUSTED, Optional.<UUID>empty());
        this.dataTracker.<Integer>startTracking(FoxEntity.TYPE, 0);
        this.dataTracker.<Byte>startTracking(FoxEntity.FOX_FLAGS, (Byte)0);
    }
    
    @Override
    protected void initGoals() {
        this.followChickenAndRabbitGoal = new FollowTargetGoal<>(this, AnimalEntity.class, 10, false, false, livingEntity -> livingEntity instanceof ChickenEntity || livingEntity instanceof RabbitEntity);
        this.followBabyTurtleGoal = new FollowTargetGoal<>(this, TurtleEntity.class, 10, false, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER);
        this.followFishGoal = new FollowTargetGoal<>(this, FishEntity.class, 20, false, false, livingEntity -> livingEntity instanceof SchoolingFishEntity);
        this.goalSelector.add(0, new FoxSwimGoal());
        this.goalSelector.add(1, new StopWanderingGoal());
        this.goalSelector.add(2, new EscapeWhenNotAggresiveGoal(2.2));
        this.goalSelector.add(3, new FleeEntityGoal<>(this, PlayerEntity.class, 16.0f, 1.6, 1.4, livingEntity -> FoxEntity.NOTICEABLE_PLAYER_FILTER.test(livingEntity) && !this.canTrust(livingEntity.getUuid())));
        this.goalSelector.add(3, new FleeEntityGoal<>(this, WolfEntity.class, 8.0f, 1.6, 1.4, livingEntity -> !livingEntity.isTamed()));
        this.goalSelector.add(4, new MoveToHuntGoal());
        this.goalSelector.add(5, new JumpChasingGoal());
        this.goalSelector.add(5, new MateGoal(1.0));
        this.goalSelector.add(5, new AvoidDaylightGoal(1.25));
        this.goalSelector.add(6, new AttackGoal(1.2000000476837158, true));
        this.goalSelector.add(6, new DelayedCalmDownGoal());
        this.goalSelector.add(7, new h(this, 1.25));
        this.goalSelector.add(8, new GoToVillageGoal(32, 200));
        this.goalSelector.add(9, new EatSweetBerriesGoal(1.2000000476837158, 12, 2));
        this.goalSelector.add(9, new PounceAtTargetGoal(this, 0.4f));
        this.goalSelector.add(10, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(10, new PickupItemGoal());
        this.goalSelector.add(11, new j(this, PlayerEntity.class, 24.0f));
        this.goalSelector.add(12, new SitDownAndLookAroundGoal());
        this.targetSelector.add(3, new DefendFriendGoal(LivingEntity.class, false, false, livingEntity -> FoxEntity.JUST_ATTACKED_SOMETHING_FILTER.test(livingEntity) && !this.canTrust(livingEntity.getUuid())));
    }
    
    @Override
    public SoundEvent getEatSound(final ItemStack itemStack) {
        return SoundEvents.dx;
    }
    
    @Override
    public void updateState() {
        if (!this.world.isClient && this.isAlive() && this.canMoveVoluntarily()) {
            ++this.eatingTime;
            final ItemStack itemStack1 = this.getEquippedStack(EquipmentSlot.HAND_MAIN);
            if (this.j(itemStack1)) {
                if (this.eatingTime > 600) {
                    final ItemStack itemStack2 = itemStack1.onItemFinishedUsing(this.world, this);
                    if (!itemStack2.isEmpty()) {
                        this.setEquippedStack(EquipmentSlot.HAND_MAIN, itemStack2);
                    }
                    this.eatingTime = 0;
                }
                else if (this.eatingTime > 560 && this.random.nextFloat() < 0.1f) {
                    this.playSound(this.getEatSound(itemStack1), 1.0f, 1.0f);
                    this.world.sendEntityStatus(this, (byte)45);
                }
            }
            final LivingEntity livingEntity2 = this.getTarget();
            if (livingEntity2 == null || !livingEntity2.isAlive()) {
                this.setCrouching(false);
                this.setRollingHead(false);
            }
        }
        if (this.isSleeping() || this.cannotMove()) {
            this.jumping = false;
            this.sidewaysSpeed = 0.0f;
            this.forwardSpeed = 0.0f;
            this.be = 0.0f;
        }
        super.updateState();
        if (this.isAggressive() && this.random.nextFloat() < 0.05f) {
            this.playSound(SoundEvents.dt, 1.0f, 1.0f);
        }
    }
    
    @Override
    protected boolean cannotMove() {
        return this.getHealth() <= 0.0f;
    }
    
    private boolean j(final ItemStack itemStack) {
        return itemStack.getItem().isFood() && this.getTarget() == null && this.onGround && !this.isSleeping();
    }
    
    @Override
    protected void initEquipment(final LocalDifficulty localDifficulty) {
        if (this.random.nextFloat() < 0.2f) {
            final float float2 = this.random.nextFloat();
            ItemStack itemStack3;
            if (float2 < 0.05f) {
                itemStack3 = new ItemStack(Items.nF);
            }
            else if (float2 < 0.2f) {
                itemStack3 = new ItemStack(Items.kW);
            }
            else if (float2 < 0.4f) {
                itemStack3 = (this.random.nextBoolean() ? new ItemStack(Items.oj) : new ItemStack(Items.ok));
            }
            else if (float2 < 0.6f) {
                itemStack3 = new ItemStack(Items.jP);
            }
            else if (float2 < 0.8f) {
                itemStack3 = new ItemStack(Items.kF);
            }
            else {
                itemStack3 = new ItemStack(Items.jH);
            }
            this.setEquippedStack(EquipmentSlot.HAND_MAIN, itemStack3);
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void handleStatus(final byte status) {
        if (status == 45) {
            final ItemStack itemStack2 = this.getEquippedStack(EquipmentSlot.HAND_MAIN);
            if (!itemStack2.isEmpty()) {
                for (int integer3 = 0; integer3 < 8; ++integer3) {
                    final Vec3d vec3d4 = new Vec3d((this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0).rotateX(-this.pitch * 0.017453292f).rotateY(-this.yaw * 0.017453292f);
                    this.world.addParticle(new ItemStackParticleParameters(ParticleTypes.G, itemStack2), this.x + this.getRotationVector().x / 2.0, this.y, this.z + this.getRotationVector().z / 2.0, vec3d4.x, vec3d4.y + 0.05, vec3d4.z);
                }
            }
        }
        else {
            super.handleStatus(status);
        }
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896);
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
        this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(32.0);
        this.getAttributeContainer().register(EntityAttributes.ATTACK_DAMAGE).setBaseValue(2.0);
    }
    
    @Override
    public FoxEntity createChild(final PassiveEntity mate) {
        final FoxEntity foxEntity2 = EntityType.B.create(this.world);
        foxEntity2.setType(this.random.nextBoolean() ? this.getFoxType() : ((FoxEntity)mate).getFoxType());
        return foxEntity2;
    }
    
    @Nullable
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable EntityData entityData, @Nullable final CompoundTag compoundTag) {
        final Biome biome6 = iWorld.getBiome(new BlockPos(this));
        Type type7 = Type.fromBiome(biome6);
        boolean boolean8 = false;
        if (entityData instanceof FoxData) {
            type7 = ((FoxData)entityData).type;
            if (((FoxData)entityData).uses >= 2) {
                boolean8 = true;
            }
            else {
                final FoxData foxData = (FoxData)entityData;
                ++foxData.uses;
            }
        }
        else {
            entityData = new FoxData(type7);
            final FoxData foxData2 = (FoxData)entityData;
            ++foxData2.uses;
        }
        this.setType(type7);
        if (boolean8) {
            this.setBreedingAge(-24000);
        }
        this.addTypeSpecificGoals();
        this.initEquipment(localDifficulty);
        return super.initialize(iWorld, localDifficulty, difficulty, entityData, compoundTag);
    }
    
    private void addTypeSpecificGoals() {
        if (this.getFoxType() == Type.a) {
            this.targetSelector.add(4, this.followChickenAndRabbitGoal);
            this.targetSelector.add(4, this.followBabyTurtleGoal);
            this.targetSelector.add(6, this.followFishGoal);
        }
        else {
            this.targetSelector.add(4, this.followFishGoal);
            this.targetSelector.add(6, this.followChickenAndRabbitGoal);
            this.targetSelector.add(6, this.followBabyTurtleGoal);
        }
    }
    
    @Override
    protected void eat(final PlayerEntity player, final ItemStack stack) {
        if (this.isBreedingItem(stack)) {
            this.playSound(this.getEatSound(stack), 1.0f, 1.0f);
        }
        super.eat(player, stack);
    }
    
    @Override
    protected float getActiveEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        if (this.isChild()) {
            return entitySize.height * 0.95f;
        }
        return 0.4f;
    }
    
    public Type getFoxType() {
        return Type.fromId(this.dataTracker.<Integer>get(FoxEntity.TYPE));
    }
    
    private void setType(final Type type) {
        this.dataTracker.<Integer>set(FoxEntity.TYPE, type.getId());
    }
    
    private List<UUID> getTrustedUuids() {
        final List<UUID> list1 = Lists.newArrayList();
        list1.add(this.dataTracker.<Optional<UUID>>get(FoxEntity.OWNER).orElse(null));
        list1.add(this.dataTracker.<Optional<UUID>>get(FoxEntity.OTHER_TRUSTED).orElse(null));
        return list1;
    }
    
    private void addTrustedUuid(@Nullable final UUID uUID) {
        if (this.dataTracker.<Optional<UUID>>get(FoxEntity.OWNER).isPresent()) {
            this.dataTracker.<Optional<UUID>>set(FoxEntity.OTHER_TRUSTED, Optional.<UUID>ofNullable(uUID));
        }
        else {
            this.dataTracker.<Optional<UUID>>set(FoxEntity.OWNER, Optional.<UUID>ofNullable(uUID));
        }
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        final List<UUID> list2 = this.getTrustedUuids();
        final ListTag listTag3 = new ListTag();
        for (final UUID uUID5 : list2) {
            if (uUID5 != null) {
                ((AbstractList<CompoundTag>)listTag3).add(TagHelper.serializeUuid(uUID5));
            }
        }
        tag.put("TrustedUUIDs", listTag3);
        tag.putBoolean("Sleeping", this.isSleeping());
        tag.putString("Type", this.getFoxType().getKey());
        tag.putBoolean("Sitting", this.isSitting());
        tag.putBoolean("Crouching", this.isCrouching());
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        final ListTag listTag2 = tag.getList("TrustedUUIDs", 10);
        for (int integer3 = 0; integer3 < listTag2.size(); ++integer3) {
            this.addTrustedUuid(TagHelper.deserializeUuid(listTag2.getCompoundTag(integer3)));
        }
        this.setSleeping(tag.getBoolean("Sleeping"));
        this.setType(Type.byName(tag.getString("Type")));
        this.setSitting(tag.getBoolean("Sitting"));
        this.setCrouching(tag.getBoolean("Crouching"));
        this.addTypeSpecificGoals();
    }
    
    public boolean isSitting() {
        return this.getFoxFlag(1);
    }
    
    public void setSitting(final boolean boolean1) {
        this.setFoxFlag(1, boolean1);
    }
    
    public boolean isWalking() {
        return this.getFoxFlag(64);
    }
    
    private void setWalking(final boolean boolean1) {
        this.setFoxFlag(64, boolean1);
    }
    
    private boolean isAggressive() {
        return this.getFoxFlag(128);
    }
    
    private void setAggressive(final boolean boolean1) {
        this.setFoxFlag(128, boolean1);
    }
    
    @Override
    public boolean isSleeping() {
        return this.getFoxFlag(32);
    }
    
    private void setSleeping(final boolean boolean1) {
        this.setFoxFlag(32, boolean1);
    }
    
    private void setFoxFlag(final int mask, final boolean value) {
        if (value) {
            this.dataTracker.<Byte>set(FoxEntity.FOX_FLAGS, (byte)(this.dataTracker.<Byte>get(FoxEntity.FOX_FLAGS) | mask));
        }
        else {
            this.dataTracker.<Byte>set(FoxEntity.FOX_FLAGS, (byte)(this.dataTracker.<Byte>get(FoxEntity.FOX_FLAGS) & ~mask));
        }
    }
    
    private boolean getFoxFlag(final int integer) {
        return (this.dataTracker.<Byte>get(FoxEntity.FOX_FLAGS) & integer) != 0x0;
    }
    
    @Override
    public boolean canPickUp(final ItemStack itemStack) {
        final EquipmentSlot equipmentSlot2 = MobEntity.getPreferredEquipmentSlot(itemStack);
        return this.getEquippedStack(equipmentSlot2).isEmpty() && equipmentSlot2 == EquipmentSlot.HAND_MAIN && super.canPickUp(itemStack);
    }
    
    @Override
    protected boolean canPickupItem(final ItemStack itemStack) {
        final Item item2 = itemStack.getItem();
        final ItemStack itemStack2 = this.getEquippedStack(EquipmentSlot.HAND_MAIN);
        return itemStack2.isEmpty() || (this.eatingTime > 0 && item2.isFood() && !itemStack2.getItem().isFood());
    }
    
    private void spit(final ItemStack itemStack) {
        if (itemStack.isEmpty() || this.world.isClient) {
            return;
        }
        final ItemEntity itemEntity2 = new ItemEntity(this.world, this.x + this.getRotationVector().x, this.y + 1.0, this.z + this.getRotationVector().z, itemStack);
        itemEntity2.setPickupDelay(40);
        itemEntity2.setThrower(this.getUuid());
        this.playSound(SoundEvents.dC, 1.0f, 1.0f);
        this.world.spawnEntity(itemEntity2);
    }
    
    private void dropItem(final ItemStack itemStack) {
        final ItemEntity itemEntity2 = new ItemEntity(this.world, this.x, this.y, this.z, itemStack);
        this.world.spawnEntity(itemEntity2);
    }
    
    @Override
    protected void loot(final ItemEntity item) {
        final ItemStack itemStack2 = item.getStack();
        if (this.canPickupItem(itemStack2)) {
            final int integer3 = itemStack2.getAmount();
            if (integer3 > 1) {
                this.dropItem(itemStack2.split(integer3 - 1));
            }
            this.spit(this.getEquippedStack(EquipmentSlot.HAND_MAIN));
            this.setEquippedStack(EquipmentSlot.HAND_MAIN, itemStack2.split(1));
            this.handDropChances[EquipmentSlot.HAND_MAIN.getEntitySlotId()] = 2.0f;
            this.sendPickup(item, itemStack2.getAmount());
            item.remove();
            this.eatingTime = 0;
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.canMoveVoluntarily()) {
            final boolean boolean1 = this.isInsideWater();
            if (boolean1 || this.getTarget() != null || this.world.isThundering()) {
                this.wakeUp();
            }
            if (boolean1 || this.isSleeping()) {
                this.setSitting(false);
            }
            if (this.isWalking() && this.world.random.nextFloat() < 0.2f) {
                final BlockPos blockPos2 = new BlockPos(this.x, this.y, this.z);
                final BlockState blockState3 = this.world.getBlockState(blockPos2);
                this.world.playLevelEvent(2001, blockPos2, Block.getRawIdFromState(blockState3));
            }
        }
        this.lastHeadRollProgress = this.headRollProgress;
        if (this.isRollingHead()) {
            this.headRollProgress += (1.0f - this.headRollProgress) * 0.4f;
        }
        else {
            this.headRollProgress += (0.0f - this.headRollProgress) * 0.4f;
        }
        this.lastExtraRollingHeight = this.extraRollingHeight;
        if (this.isCrouching()) {
            this.extraRollingHeight += 0.2f;
            if (this.extraRollingHeight > 3.0f) {
                this.extraRollingHeight = 3.0f;
            }
        }
        else {
            this.extraRollingHeight = 0.0f;
        }
    }
    
    @Override
    public boolean isBreedingItem(final ItemStack stack) {
        return stack.getItem() == Items.pR;
    }
    
    @Override
    protected void onPlayerSpawnedChild(final PlayerEntity player, final PassiveEntity child) {
        ((FoxEntity)child).addTrustedUuid(player.getUuid());
    }
    
    public boolean isChasing() {
        return this.getFoxFlag(16);
    }
    
    public void setChasing(final boolean boolean1) {
        this.setFoxFlag(16, boolean1);
    }
    
    public boolean isFullyCrouched() {
        return this.extraRollingHeight == 3.0f;
    }
    
    public void setCrouching(final boolean boolean1) {
        this.setFoxFlag(4, boolean1);
    }
    
    public boolean isCrouching() {
        return this.getFoxFlag(4);
    }
    
    public void setRollingHead(final boolean boolean1) {
        this.setFoxFlag(8, boolean1);
    }
    
    public boolean isRollingHead() {
        return this.getFoxFlag(8);
    }
    
    @Environment(EnvType.CLIENT)
    public float getHeadRoll(final float float1) {
        return MathHelper.lerp(float1, this.lastHeadRollProgress, this.headRollProgress) * 0.11f * 3.1415927f;
    }
    
    @Environment(EnvType.CLIENT)
    public float getBodyRotationHeightOffset(final float float1) {
        return MathHelper.lerp(float1, this.lastExtraRollingHeight, this.extraRollingHeight);
    }
    
    @Override
    public void setTarget(@Nullable final LivingEntity target) {
        if (this.isAggressive() && target == null) {
            this.setAggressive(false);
        }
        super.setTarget(target);
    }
    
    @Override
    public void handleFallDamage(final float fallDistance, final float damageMultiplier) {
        final int integer3 = MathHelper.ceil((fallDistance - 5.0f) * damageMultiplier);
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
    
    private void wakeUp() {
        this.setSleeping(false);
    }
    
    private void stopActions() {
        this.setRollingHead(false);
        this.setCrouching(false);
        this.setSitting(false);
        this.setSleeping(false);
        this.setAggressive(false);
        this.setWalking(false);
    }
    
    private boolean wantsToPickupItem() {
        return !this.isSleeping() && !this.isSitting() && !this.isWalking();
    }
    
    @Override
    public void playAmbientSound() {
        final SoundEvent soundEvent1 = this.getAmbientSound();
        if (soundEvent1 == SoundEvents.dz) {
            this.playSound(soundEvent1, 2.0f, this.getSoundPitch());
        }
        else {
            super.playAmbientSound();
        }
    }
    
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isSleeping()) {
            return SoundEvents.dA;
        }
        if (!this.world.isDaylight() && this.random.nextFloat() < 0.1f) {
            final List<PlayerEntity> list1 = this.world.<PlayerEntity>getEntities(PlayerEntity.class, this.getBoundingBox().expand(16.0, 16.0, 16.0), EntityPredicates.EXCEPT_SPECTATOR);
            if (list1.isEmpty()) {
                return SoundEvents.dz;
            }
        }
        return SoundEvents.du;
    }
    
    @Nullable
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.dy;
    }
    
    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.dw;
    }
    
    private boolean canTrust(final UUID uUID) {
        return this.getTrustedUuids().contains(uUID);
    }
    
    @Override
    protected void drop(final DamageSource damageSource) {
        final ItemStack itemStack2 = this.getEquippedStack(EquipmentSlot.HAND_MAIN);
        if (!itemStack2.isEmpty()) {
            this.dropStack(itemStack2);
            this.setEquippedStack(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
        }
        super.drop(damageSource);
    }
    
    public static boolean canJumpChase(final FoxEntity foxEntity, final LivingEntity livingEntity) {
        final double double3 = livingEntity.z - foxEntity.z;
        final double double4 = livingEntity.x - foxEntity.x;
        final double double5 = double3 / double4;
        final int integer9 = 6;
        for (int integer10 = 0; integer10 < 6; ++integer10) {
            final double double6 = (double5 == 0.0) ? 0.0 : (double3 * (integer10 / 6.0f));
            final double double7 = (double5 == 0.0) ? (double4 * (integer10 / 6.0f)) : (double6 / double5);
            for (int integer11 = 1; integer11 < 4; ++integer11) {
                if (!foxEntity.world.getBlockState(new BlockPos(foxEntity.x + double7, foxEntity.y + integer11, foxEntity.z + double6)).getMaterial().isReplaceable()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    static {
        TYPE = DataTracker.<Integer>registerData(FoxEntity.class, TrackedDataHandlerRegistry.INTEGER);
        FOX_FLAGS = DataTracker.<Byte>registerData(FoxEntity.class, TrackedDataHandlerRegistry.BYTE);
        OWNER = DataTracker.<Optional<UUID>>registerData(FoxEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
        OTHER_TRUSTED = DataTracker.<Optional<UUID>>registerData(FoxEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
        PICKABLE_DROP_FILTER = (itemEntity -> !itemEntity.cannotPickup() && itemEntity.isAlive());
        LivingEntity livingEntity2;
        JUST_ATTACKED_SOMETHING_FILTER = (entity -> {
            if (entity instanceof LivingEntity) {
                livingEntity2 = entity;
                return livingEntity2.getAttacking() != null && livingEntity2.getLastAttackTime() < livingEntity2.age + 600;
            }
            else {
                return false;
            }
        });
        CHICKEN_AND_RABBIT_FILTER = (entity -> entity instanceof ChickenEntity || entity instanceof RabbitEntity);
        NOTICEABLE_PLAYER_FILTER = (entity -> !entity.isSneaking() && EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(entity));
    }
    
    public enum Type
    {
        a(0, "red", new Biome[] { Biomes.g, Biomes.u, Biomes.af, Biomes.H, Biomes.ao, Biomes.I, Biomes.ap }), 
        b(1, "snow", new Biome[] { Biomes.F, Biomes.G, Biomes.an });
        
        private static final Type[] c;
        private static final Map<String, Type> byName;
        private final int id;
        private final String key;
        private final List<Biome> biomes;
        
        private Type(final int integer1, final String string2, final Biome[] arr) {
            this.id = integer1;
            this.key = string2;
            this.biomes = Arrays.<Biome>asList(arr);
        }
        
        public String getKey() {
            return this.key;
        }
        
        public List<Biome> getBiomes() {
            return this.biomes;
        }
        
        public int getId() {
            return this.id;
        }
        
        public static Type byName(final String string) {
            return Type.byName.getOrDefault(string, Type.a);
        }
        
        public static Type fromId(int integer) {
            if (integer < 0 || integer > Type.c.length) {
                integer = 0;
            }
            return Type.c[integer];
        }
        
        public static Type fromBiome(final Biome biome) {
            return Type.b.getBiomes().contains(biome) ? Type.b : Type.a;
        }
        
        static {
            c = Arrays.<Type>stream(values()).sorted(Comparator.comparingInt(Type::getId)).<Type>toArray(Type[]::new);
            byName = Arrays.<Type>stream(values()).collect(Collectors.toMap(Type::getKey, type -> type));
        }
    }
    
    class PickupItemGoal extends Goal
    {
        public PickupItemGoal() {
            this.setControls(EnumSet.<Control>of(Control.a));
        }
        
        @Override
        public boolean canStart() {
            if (!FoxEntity.this.getEquippedStack(EquipmentSlot.HAND_MAIN).isEmpty()) {
                return false;
            }
            if (FoxEntity.this.getTarget() != null || FoxEntity.this.getAttacker() != null) {
                return false;
            }
            if (!FoxEntity.this.wantsToPickupItem()) {
                return false;
            }
            if (FoxEntity.this.getRand().nextInt(10) != 0) {
                return false;
            }
            final List<ItemEntity> list1 = FoxEntity.this.world.<ItemEntity>getEntities(ItemEntity.class, FoxEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), FoxEntity.PICKABLE_DROP_FILTER);
            return !list1.isEmpty() && FoxEntity.this.getEquippedStack(EquipmentSlot.HAND_MAIN).isEmpty();
        }
        
        @Override
        public void tick() {
            final List<ItemEntity> list1 = FoxEntity.this.world.<ItemEntity>getEntities(ItemEntity.class, FoxEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), FoxEntity.PICKABLE_DROP_FILTER);
            final ItemStack itemStack2 = FoxEntity.this.getEquippedStack(EquipmentSlot.HAND_MAIN);
            if (itemStack2.isEmpty() && !list1.isEmpty()) {
                FoxEntity.this.getNavigation().startMovingTo(list1.get(0), 1.2000000476837158);
            }
        }
        
        @Override
        public void start() {
            final List<ItemEntity> list1 = FoxEntity.this.world.<ItemEntity>getEntities(ItemEntity.class, FoxEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), FoxEntity.PICKABLE_DROP_FILTER);
            if (!list1.isEmpty()) {
                FoxEntity.this.getNavigation().startMovingTo(list1.get(0), 1.2000000476837158);
            }
        }
    }
    
    class FoxMoveControl extends MoveControl
    {
        public FoxMoveControl() {
            super(FoxEntity.this);
        }
        
        @Override
        public void tick() {
            if (FoxEntity.this.wantsToPickupItem()) {
                super.tick();
            }
        }
    }
    
    class MoveToHuntGoal extends Goal
    {
        public MoveToHuntGoal() {
            this.setControls(EnumSet.<Control>of(Control.a, Control.b));
        }
        
        @Override
        public boolean canStart() {
            if (FoxEntity.this.isSleeping()) {
                return false;
            }
            final LivingEntity livingEntity1 = FoxEntity.this.getTarget();
            return livingEntity1 != null && livingEntity1.isAlive() && FoxEntity.CHICKEN_AND_RABBIT_FILTER.test(livingEntity1) && FoxEntity.this.squaredDistanceTo(livingEntity1) > 36.0 && !FoxEntity.this.isCrouching() && !FoxEntity.this.isRollingHead() && !FoxEntity.this.jumping;
        }
        
        @Override
        public void start() {
            FoxEntity.this.setSitting(false);
            FoxEntity.this.setWalking(false);
        }
        
        @Override
        public void stop() {
            final LivingEntity livingEntity1 = FoxEntity.this.getTarget();
            if (livingEntity1 != null && FoxEntity.canJumpChase(FoxEntity.this, livingEntity1)) {
                FoxEntity.this.setRollingHead(true);
                FoxEntity.this.setCrouching(true);
                FoxEntity.this.getNavigation().stop();
                FoxEntity.this.getLookControl().lookAt(livingEntity1, (float)FoxEntity.this.dA(), (float)FoxEntity.this.getLookPitchSpeed());
            }
            else {
                FoxEntity.this.setRollingHead(false);
                FoxEntity.this.setCrouching(false);
            }
        }
        
        @Override
        public void tick() {
            final LivingEntity livingEntity1 = FoxEntity.this.getTarget();
            FoxEntity.this.getLookControl().lookAt(livingEntity1, (float)FoxEntity.this.dA(), (float)FoxEntity.this.getLookPitchSpeed());
            if (FoxEntity.this.squaredDistanceTo(livingEntity1) <= 36.0) {
                FoxEntity.this.setRollingHead(true);
                FoxEntity.this.setCrouching(true);
                FoxEntity.this.getNavigation().stop();
            }
            else {
                FoxEntity.this.getNavigation().startMovingTo(livingEntity1, 1.5);
            }
        }
    }
    
    class AttackGoal extends MeleeAttackGoal
    {
        public AttackGoal(final double double2, final boolean boolean4) {
            super(FoxEntity.this, double2, boolean4);
        }
        
        @Override
        protected void attack(final LivingEntity target, final double squaredDistance) {
            final double double4 = this.getSquaredMaxAttackDistance(target);
            if (squaredDistance <= double4 && this.ticksUntilAttack <= 0) {
                this.ticksUntilAttack = 20;
                this.entity.tryAttack(target);
                FoxEntity.this.playSound(SoundEvents.dv, 1.0f, 1.0f);
            }
        }
        
        @Override
        public void start() {
            FoxEntity.this.setRollingHead(false);
            super.start();
        }
        
        @Override
        public boolean canStart() {
            return !FoxEntity.this.isSitting() && !FoxEntity.this.isSleeping() && !FoxEntity.this.isCrouching() && !FoxEntity.this.isWalking() && super.canStart();
        }
    }
    
    class MateGoal extends AnimalMateGoal
    {
        public MateGoal(final double double2) {
            super(FoxEntity.this, double2);
        }
        
        @Override
        public void start() {
            ((FoxEntity)this.owner).stopActions();
            ((FoxEntity)this.mate).stopActions();
            super.start();
        }
        
        @Override
        protected void breed() {
            final FoxEntity foxEntity1 = (FoxEntity)this.owner.createChild(this.mate);
            if (foxEntity1 == null) {
                return;
            }
            final ServerPlayerEntity serverPlayerEntity2 = this.owner.getLovingPlayer();
            final ServerPlayerEntity serverPlayerEntity3 = this.mate.getLovingPlayer();
            ServerPlayerEntity serverPlayerEntity4;
            if ((serverPlayerEntity4 = serverPlayerEntity2) != null) {
                foxEntity1.addTrustedUuid(serverPlayerEntity2.getUuid());
            }
            else {
                serverPlayerEntity4 = serverPlayerEntity3;
            }
            if (serverPlayerEntity3 != null && serverPlayerEntity2 != serverPlayerEntity3) {
                foxEntity1.addTrustedUuid(serverPlayerEntity3.getUuid());
            }
            if (serverPlayerEntity4 != null) {
                serverPlayerEntity4.incrementStat(Stats.N);
                Criterions.BRED_ANIMALS.handle(serverPlayerEntity4, this.owner, this.mate, foxEntity1);
            }
            final int integer5 = 6000;
            this.owner.setBreedingAge(6000);
            this.mate.setBreedingAge(6000);
            this.owner.resetLoveTicks();
            this.mate.resetLoveTicks();
            foxEntity1.setBreedingAge(-24000);
            foxEntity1.setPositionAndAngles(this.owner.x, this.owner.y, this.owner.z, 0.0f, 0.0f);
            this.world.spawnEntity(foxEntity1);
            this.world.sendEntityStatus(this.owner, (byte)18);
            if (this.world.getGameRules().getBoolean("doMobLoot")) {
                this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.owner.x, this.owner.y, this.owner.z, this.owner.getRand().nextInt(7) + 1));
            }
        }
    }
    
    class DefendFriendGoal extends FollowTargetGoal<LivingEntity>
    {
        @Nullable
        private LivingEntity offender;
        private LivingEntity friend;
        private int lastAttackedTime;
        
        public DefendFriendGoal(final Class<LivingEntity> class2, final boolean boolean3, final boolean boolean4, @Nullable final Predicate<LivingEntity> predicate) {
            super(FoxEntity.this, class2, 10, boolean3, boolean4, predicate);
        }
        
        @Override
        public boolean canStart() {
            if (this.reciprocalChance > 0 && this.entity.getRand().nextInt(this.reciprocalChance) != 0) {
                return false;
            }
            for (final UUID uUID2 : FoxEntity.this.getTrustedUuids()) {
                if (uUID2 != null) {
                    if (!(FoxEntity.this.world instanceof ServerWorld)) {
                        continue;
                    }
                    final Entity entity3 = ((ServerWorld)FoxEntity.this.world).getEntity(uUID2);
                    if (!(entity3 instanceof LivingEntity)) {
                        continue;
                    }
                    final LivingEntity livingEntity4 = (LivingEntity)entity3;
                    this.friend = livingEntity4;
                    this.offender = livingEntity4.getAttacker();
                    final int integer5 = livingEntity4.getLastAttackedTime();
                    return integer5 != this.lastAttackedTime && this.canTrack(this.offender, TargetPredicate.DEFAULT);
                }
            }
            return false;
        }
        
        @Override
        public void start() {
            FoxEntity.this.setTarget(this.offender);
            this.targetEntity = this.offender;
            if (this.friend != null) {
                this.lastAttackedTime = this.friend.getLastAttackedTime();
            }
            FoxEntity.this.playSound(SoundEvents.dt, 1.0f, 1.0f);
            FoxEntity.this.setAggressive(true);
            FoxEntity.this.wakeUp();
            super.start();
        }
    }
    
    class AvoidDaylightGoal extends EscapeSunlightGoal
    {
        private int timer;
        
        public AvoidDaylightGoal(final double double2) {
            super(FoxEntity.this, double2);
            this.timer = 100;
        }
        
        @Override
        public boolean canStart() {
            if (FoxEntity.this.isSleeping() || this.owner.getTarget() != null) {
                return false;
            }
            if (FoxEntity.this.world.isThundering()) {
                return true;
            }
            if (this.timer > 0) {
                --this.timer;
                return false;
            }
            this.timer = 100;
            final BlockPos blockPos1 = new BlockPos(this.owner);
            return FoxEntity.this.world.isDaylight() && FoxEntity.this.world.isSkyVisible(blockPos1) && !((ServerWorld)FoxEntity.this.world).isNearOccupiedPointOfInterest(blockPos1) && this.g();
        }
        
        @Override
        public void start() {
            FoxEntity.this.stopActions();
            super.start();
        }
    }
    
    public class WorriableEntityFilter implements Predicate<LivingEntity>
    {
        public boolean a(final LivingEntity livingEntity) {
            if (livingEntity instanceof FoxEntity) {
                return false;
            }
            if (livingEntity instanceof ChickenEntity || livingEntity instanceof RabbitEntity || livingEntity instanceof HostileEntity) {
                return true;
            }
            if (livingEntity instanceof TameableEntity) {
                return !((TameableEntity)livingEntity).isTamed();
            }
            return (!(livingEntity instanceof PlayerEntity) || (!livingEntity.isSpectator() && !((PlayerEntity)livingEntity).isCreative())) && !FoxEntity.this.canTrust(livingEntity.getUuid()) && !livingEntity.isSleeping() && !livingEntity.isSneaking();
        }
    }
    
    abstract class CalmDownGoal extends Goal
    {
        private final TargetPredicate WORRIABLE_ENTITY_PREDICATE;
        
        private CalmDownGoal() {
            this.WORRIABLE_ENTITY_PREDICATE = new TargetPredicate().setBaseMaxDistance(12.0).includeHidden().setPredicate(new WorriableEntityFilter());
        }
        
        protected boolean isAtFavoredLocation() {
            final BlockPos blockPos1 = new BlockPos(FoxEntity.this);
            return !FoxEntity.this.world.isSkyVisible(blockPos1) && FoxEntity.this.getPathfindingFavor(blockPos1) >= 0.0f;
        }
        
        protected boolean canCalmDown() {
            return !FoxEntity.this.world.<LivingEntity>getTargets(LivingEntity.class, this.WORRIABLE_ENTITY_PREDICATE, (LivingEntity)FoxEntity.this, FoxEntity.this.getBoundingBox().expand(12.0, 6.0, 12.0)).isEmpty();
        }
    }
    
    class DelayedCalmDownGoal extends CalmDownGoal
    {
        private int timer;
        
        public DelayedCalmDownGoal() {
            this.timer = FoxEntity.this.random.nextInt(140);
            this.setControls(EnumSet.<Control>of(Control.a, Control.b, Control.c));
        }
        
        @Override
        public boolean canStart() {
            return FoxEntity.this.sidewaysSpeed == 0.0f && FoxEntity.this.upwardSpeed == 0.0f && FoxEntity.this.forwardSpeed == 0.0f && (this.j() || FoxEntity.this.isSleeping());
        }
        
        @Override
        public boolean shouldContinue() {
            return this.j();
        }
        
        private boolean j() {
            if (this.timer > 0) {
                --this.timer;
                return false;
            }
            return FoxEntity.this.world.isDaylight() && this.isAtFavoredLocation() && !this.canCalmDown();
        }
        
        @Override
        public void stop() {
            this.timer = FoxEntity.this.random.nextInt(140);
            FoxEntity.this.stopActions();
        }
        
        @Override
        public void start() {
            FoxEntity.this.setSitting(false);
            FoxEntity.this.setCrouching(false);
            FoxEntity.this.setRollingHead(false);
            FoxEntity.this.setJumping(false);
            FoxEntity.this.setSleeping(true);
            FoxEntity.this.getNavigation().stop();
            FoxEntity.this.getMoveControl().moveTo(FoxEntity.this.x, FoxEntity.this.y, FoxEntity.this.z, 0.0);
        }
    }
    
    class SitDownAndLookAroundGoal extends CalmDownGoal
    {
        private double lookX;
        private double lookZ;
        private int timer;
        private int counter;
        
        public SitDownAndLookAroundGoal() {
            this.setControls(EnumSet.<Control>of(Control.a, Control.b));
        }
        
        @Override
        public boolean canStart() {
            return FoxEntity.this.getAttacker() == null && FoxEntity.this.getRand().nextFloat() < 0.02f && !FoxEntity.this.isSleeping() && FoxEntity.this.getTarget() == null && FoxEntity.this.getNavigation().isIdle() && !this.canCalmDown() && !FoxEntity.this.isChasing() && !FoxEntity.this.isCrouching();
        }
        
        @Override
        public boolean shouldContinue() {
            return this.counter > 0;
        }
        
        @Override
        public void start() {
            this.chooseNewAngle();
            this.counter = 2 + FoxEntity.this.getRand().nextInt(3);
            FoxEntity.this.setSitting(true);
            FoxEntity.this.getNavigation().stop();
        }
        
        @Override
        public void stop() {
            FoxEntity.this.setSitting(false);
        }
        
        @Override
        public void tick() {
            --this.timer;
            if (this.timer <= 0) {
                --this.counter;
                this.chooseNewAngle();
            }
            FoxEntity.this.getLookControl().lookAt(FoxEntity.this.x + this.lookX, FoxEntity.this.y + FoxEntity.this.getStandingEyeHeight(), FoxEntity.this.z + this.lookZ, (float)FoxEntity.this.dA(), (float)FoxEntity.this.getLookPitchSpeed());
        }
        
        private void chooseNewAngle() {
            final double double1 = 6.283185307179586 * FoxEntity.this.getRand().nextDouble();
            this.lookX = Math.cos(double1);
            this.lookZ = Math.sin(double1);
            this.timer = 80 + FoxEntity.this.getRand().nextInt(20);
        }
    }
    
    public class EatSweetBerriesGoal extends MoveToTargetPosGoal
    {
        protected int timer;
        
        public EatSweetBerriesGoal(final double double2, final int integer4, final int integer5) {
            super(FoxEntity.this, double2, integer4, integer5);
        }
        
        @Override
        public double getDesiredSquaredDistanceToTarget() {
            return 2.0;
        }
        
        @Override
        public boolean shouldResetPath() {
            return this.tryingTime % 100 == 0;
        }
        
        @Override
        protected boolean isTargetPos(final ViewableWorld world, final BlockPos pos) {
            final BlockState blockState3 = world.getBlockState(pos);
            return blockState3.getBlock() == Blocks.lW && blockState3.<Integer>get((Property<Integer>)SweetBerryBushBlock.AGE) >= 2;
        }
        
        @Override
        public void tick() {
            if (this.hasReached()) {
                if (this.timer >= 40) {
                    this.eatSweetBerry();
                }
                else {
                    ++this.timer;
                }
            }
            else if (!this.hasReached() && FoxEntity.this.random.nextFloat() < 0.05f) {
                FoxEntity.this.playSound(SoundEvents.dB, 1.0f, 1.0f);
            }
            super.tick();
        }
        
        protected void eatSweetBerry() {
            if (!FoxEntity.this.world.getGameRules().getBoolean("mobGriefing")) {
                return;
            }
            final BlockState blockState1 = FoxEntity.this.world.getBlockState(this.targetPos);
            if (blockState1.getBlock() != Blocks.lW) {
                return;
            }
            final int integer2 = blockState1.<Integer>get((Property<Integer>)SweetBerryBushBlock.AGE);
            ((AbstractPropertyContainer<Object, Object>)blockState1).<Comparable, Integer>with((Property<Comparable>)SweetBerryBushBlock.AGE, 1);
            int integer3 = 1 + FoxEntity.this.world.random.nextInt(2) + ((integer2 == 3) ? 1 : 0);
            final ItemStack itemStack4 = FoxEntity.this.getEquippedStack(EquipmentSlot.HAND_MAIN);
            if (itemStack4.isEmpty()) {
                FoxEntity.this.setEquippedStack(EquipmentSlot.HAND_MAIN, new ItemStack(Items.pR));
                --integer3;
            }
            if (integer3 > 0) {
                Block.dropStack(FoxEntity.this.world, this.targetPos, new ItemStack(Items.pR, integer3));
            }
            FoxEntity.this.playSound(SoundEvents.lB, 1.0f, 1.0f);
            FoxEntity.this.world.setBlockState(this.targetPos, ((AbstractPropertyContainer<O, BlockState>)blockState1).<Comparable, Integer>with((Property<Comparable>)SweetBerryBushBlock.AGE, 1), 2);
        }
        
        @Override
        public boolean canStart() {
            return !FoxEntity.this.isSleeping() && super.canStart();
        }
        
        @Override
        public void start() {
            this.timer = 0;
            FoxEntity.this.setSitting(false);
            super.start();
        }
    }
    
    public static class FoxData implements EntityData
    {
        public final Type type;
        public int uses;
        
        public FoxData(final Type type) {
            this.type = type;
        }
    }
    
    class StopWanderingGoal extends Goal
    {
        int timer;
        
        public StopWanderingGoal() {
            this.setControls(EnumSet.<Control>of(Control.b, Control.c, Control.a));
        }
        
        @Override
        public boolean canStart() {
            return FoxEntity.this.isWalking();
        }
        
        @Override
        public boolean shouldContinue() {
            return this.canStart() && this.timer > 0;
        }
        
        @Override
        public void start() {
            this.timer = 40;
        }
        
        @Override
        public void stop() {
            FoxEntity.this.setWalking(false);
        }
        
        @Override
        public void tick() {
            --this.timer;
        }
    }
    
    class EscapeWhenNotAggresiveGoal extends EscapeDangerGoal
    {
        public EscapeWhenNotAggresiveGoal(final double double2) {
            super(FoxEntity.this, double2);
        }
        
        @Override
        public boolean canStart() {
            return !FoxEntity.this.isAggressive() && super.canStart();
        }
    }
    
    class GoToVillageGoal extends net.minecraft.entity.ai.goal.GoToVillageGoal
    {
        public GoToVillageGoal(final int integer2, final int integer3) {
            super(FoxEntity.this, integer3);
        }
        
        @Override
        public void start() {
            FoxEntity.this.stopActions();
            super.start();
        }
        
        @Override
        public boolean canStart() {
            return super.canStart() && this.canGoToVillage();
        }
        
        @Override
        public boolean shouldContinue() {
            return super.shouldContinue() && this.canGoToVillage();
        }
        
        private boolean canGoToVillage() {
            return !FoxEntity.this.isSleeping() && !FoxEntity.this.isSitting() && !FoxEntity.this.isAggressive() && FoxEntity.this.getTarget() == null;
        }
    }
    
    class FoxSwimGoal extends SwimGoal
    {
        public FoxSwimGoal() {
            super(FoxEntity.this);
        }
        
        @Override
        public void start() {
            super.start();
            FoxEntity.this.stopActions();
        }
        
        @Override
        public boolean canStart() {
            return (FoxEntity.this.isInsideWater() && FoxEntity.this.getWaterHeight() > 0.25) || FoxEntity.this.isTouchingLava();
        }
    }
    
    public class JumpChasingGoal extends DiveJumpingGoal
    {
        @Override
        public boolean canStart() {
            if (!FoxEntity.this.isFullyCrouched()) {
                return false;
            }
            final LivingEntity livingEntity1 = FoxEntity.this.getTarget();
            if (livingEntity1 == null || !livingEntity1.isAlive()) {
                return false;
            }
            if (livingEntity1.getMovementDirection() != livingEntity1.getHorizontalFacing()) {
                return false;
            }
            final boolean boolean2 = FoxEntity.canJumpChase(FoxEntity.this, livingEntity1);
            if (!boolean2) {
                FoxEntity.this.getNavigation().findPathTo(livingEntity1);
                FoxEntity.this.setCrouching(false);
                FoxEntity.this.setRollingHead(false);
            }
            return boolean2;
        }
        
        @Override
        public boolean shouldContinue() {
            final LivingEntity livingEntity1 = FoxEntity.this.getTarget();
            if (livingEntity1 == null || !livingEntity1.isAlive()) {
                return false;
            }
            final double double2 = FoxEntity.this.getVelocity().y;
            return (double2 * double2 >= 0.05000000074505806 || Math.abs(FoxEntity.this.pitch) >= 15.0f || !FoxEntity.this.onGround) && !FoxEntity.this.isWalking();
        }
        
        @Override
        public boolean canStop() {
            return false;
        }
        
        @Override
        public void start() {
            FoxEntity.this.setJumping(true);
            FoxEntity.this.setChasing(true);
            FoxEntity.this.setRollingHead(false);
            final LivingEntity livingEntity1 = FoxEntity.this.getTarget();
            FoxEntity.this.getLookControl().lookAt(livingEntity1, 60.0f, 30.0f);
            final Vec3d vec3d2 = new Vec3d(livingEntity1.x - FoxEntity.this.x, livingEntity1.y - FoxEntity.this.y, livingEntity1.z - FoxEntity.this.z).normalize();
            FoxEntity.this.setVelocity(FoxEntity.this.getVelocity().add(vec3d2.x * 0.8, 0.9, vec3d2.z * 0.8));
            FoxEntity.this.getNavigation().stop();
        }
        
        @Override
        public void stop() {
            FoxEntity.this.setCrouching(false);
            FoxEntity.this.extraRollingHeight = 0.0f;
            FoxEntity.this.lastExtraRollingHeight = 0.0f;
            FoxEntity.this.setRollingHead(false);
            FoxEntity.this.setChasing(false);
        }
        
        @Override
        public void tick() {
            final LivingEntity livingEntity1 = FoxEntity.this.getTarget();
            if (livingEntity1 != null) {
                FoxEntity.this.getLookControl().lookAt(livingEntity1, 60.0f, 30.0f);
            }
            if (!FoxEntity.this.isWalking()) {
                final Vec3d vec3d2 = FoxEntity.this.getVelocity();
                if (vec3d2.y * vec3d2.y < 0.029999999329447746 && FoxEntity.this.pitch != 0.0f) {
                    FoxEntity.this.pitch = this.updatePitch(FoxEntity.this.pitch, 0.0f, 0.2f);
                }
                else {
                    final double double3 = Math.sqrt(Entity.squaredHorizontalLength(vec3d2));
                    final double double4 = Math.signum(-vec3d2.y) * Math.acos(double3 / vec3d2.length()) * 57.2957763671875;
                    FoxEntity.this.pitch = (float)double4;
                }
            }
            if (livingEntity1 != null && FoxEntity.this.distanceTo(livingEntity1) <= 2.0f) {
                FoxEntity.this.tryAttack(livingEntity1);
            }
            else if (FoxEntity.this.pitch > 0.0f && FoxEntity.this.onGround && (float)FoxEntity.this.getVelocity().y != 0.0f && FoxEntity.this.world.getBlockState(new BlockPos(FoxEntity.this)).getBlock() == Blocks.cA) {
                FoxEntity.this.pitch = 60.0f;
                FoxEntity.this.setTarget(null);
                FoxEntity.this.setWalking(true);
            }
        }
    }
    
    public class FoxLookControl extends LookControl
    {
        public FoxLookControl() {
            super(FoxEntity.this);
        }
        
        @Override
        public void tick() {
            if (!FoxEntity.this.isSleeping()) {
                super.tick();
            }
        }
        
        @Override
        protected boolean b() {
            return !FoxEntity.this.isChasing() && !FoxEntity.this.isCrouching() && (!FoxEntity.this.isRollingHead() & !FoxEntity.this.isWalking());
        }
    }
    
    class h extends FollowParentGoal
    {
        private final FoxEntity b;
        
        public h(final FoxEntity foxEntity2, final double double3) {
            super(foxEntity2, double3);
            this.b = foxEntity2;
        }
        
        @Override
        public boolean canStart() {
            return !this.b.isAggressive() && super.canStart();
        }
        
        @Override
        public boolean shouldContinue() {
            return !this.b.isAggressive() && super.shouldContinue();
        }
        
        @Override
        public void start() {
            this.b.stopActions();
            super.start();
        }
    }
    
    class j extends LookAtEntityGoal
    {
        public j(final MobEntity mobEntity, final Class<? extends LivingEntity> class3, final float float4) {
            super(mobEntity, class3, float4);
        }
        
        @Override
        public boolean canStart() {
            return super.canStart() && !FoxEntity.this.isWalking() && !FoxEntity.this.isRollingHead();
        }
        
        @Override
        public boolean shouldContinue() {
            return super.shouldContinue() && !FoxEntity.this.isWalking() && !FoxEntity.this.isRollingHead();
        }
    }
}
