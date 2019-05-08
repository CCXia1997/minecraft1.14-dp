package net.minecraft.entity.mob;

import net.minecraft.sound.SoundCategory;
import net.minecraft.entity.ai.goal.StepAndDestroyBlockGoal;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import java.util.Random;
import java.util.List;
import net.minecraft.block.Blocks;
import java.time.temporal.TemporalField;
import java.time.temporal.ChronoField;
import java.time.LocalDate;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.entity.passive.ChickenEntity;
import javax.annotation.Nullable;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.entity.EntityGroup;
import net.minecraft.block.BlockState;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.EntityData;
import net.minecraft.world.IWorld;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.BlockView;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.tag.FluidTags;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.entity.ai.goal.ZombieRaiseArmsGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.BreakDoorGoal;
import net.minecraft.world.Difficulty;
import java.util.function.Predicate;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import java.util.UUID;
import net.minecraft.entity.attribute.EntityAttribute;

public class ZombieEntity extends HostileEntity
{
    protected static final EntityAttribute SPAWN_REINFORCEMENTS;
    private static final UUID BABY_SPEED_ID;
    private static final EntityAttributeModifier BABY_SPEED_BONUS;
    private static final TrackedData<Boolean> BABY;
    private static final TrackedData<Integer> bA;
    private static final TrackedData<Boolean> CONVERTING_IN_WATER;
    private static final Predicate<Difficulty> bC;
    private final BreakDoorGoal breakDoorsGoal;
    private boolean canBreakDoors;
    private int inWaterTime;
    private int ticksUntilWaterConversion;
    
    public ZombieEntity(final EntityType<? extends ZombieEntity> type, final World world) {
        super(type, world);
        this.breakDoorsGoal = new BreakDoorGoal(this, ZombieEntity.bC);
    }
    
    public ZombieEntity(final World world) {
        this(EntityType.ZOMBIE, world);
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(4, new DestroyEggGoal(this, 1.0, 3));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.initCustomGoals();
    }
    
    protected void initCustomGoals() {
        this.goalSelector.add(2, new ZombieRaiseArmsGoal(this, 1.0, false));
        this.goalSelector.add(6, new MoveThroughVillageGoal(this, 1.0, true, 4, this::canBreakDoors));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[0]).setGroupRevenge(ZombiePigmanEntity.class));
        this.targetSelector.add(2, new FollowTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, AbstractTraderEntity.class, false));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(5, new FollowTargetGoal<>(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(35.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513);
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(3.0);
        this.getAttributeInstance(EntityAttributes.ARMOR).setBaseValue(2.0);
        this.getAttributeContainer().register(ZombieEntity.SPAWN_REINFORCEMENTS).setBaseValue(this.random.nextDouble() * 0.10000000149011612);
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().<Boolean>startTracking(ZombieEntity.BABY, false);
        this.getDataTracker().<Integer>startTracking(ZombieEntity.bA, 0);
        this.getDataTracker().<Boolean>startTracking(ZombieEntity.CONVERTING_IN_WATER, false);
    }
    
    public boolean isConvertingInWater() {
        return this.getDataTracker().<Boolean>get(ZombieEntity.CONVERTING_IN_WATER);
    }
    
    public boolean canBreakDoors() {
        return this.canBreakDoors;
    }
    
    public void setCanBreakDoors(final boolean canBreakDoors) {
        if (this.shouldBreakDoors()) {
            if (this.canBreakDoors != canBreakDoors) {
                this.canBreakDoors = canBreakDoors;
                ((MobNavigation)this.getNavigation()).setCanPathThroughDoors(canBreakDoors);
                if (canBreakDoors) {
                    this.goalSelector.add(1, this.breakDoorsGoal);
                }
                else {
                    this.goalSelector.remove(this.breakDoorsGoal);
                }
            }
        }
        else if (this.canBreakDoors) {
            this.goalSelector.remove(this.breakDoorsGoal);
            this.canBreakDoors = false;
        }
    }
    
    protected boolean shouldBreakDoors() {
        return true;
    }
    
    @Override
    public boolean isChild() {
        return this.getDataTracker().<Boolean>get(ZombieEntity.BABY);
    }
    
    @Override
    protected int getCurrentExperience(final PlayerEntity playerEntity) {
        if (this.isChild()) {
            this.experiencePoints *= (int)2.5f;
        }
        return super.getCurrentExperience(playerEntity);
    }
    
    public void setChild(final boolean boolean1) {
        this.getDataTracker().<Boolean>set(ZombieEntity.BABY, boolean1);
        if (this.world != null && !this.world.isClient) {
            final EntityAttributeInstance entityAttributeInstance2 = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
            entityAttributeInstance2.removeModifier(ZombieEntity.BABY_SPEED_BONUS);
            if (boolean1) {
                entityAttributeInstance2.addModifier(ZombieEntity.BABY_SPEED_BONUS);
            }
        }
    }
    
    @Override
    public void onTrackedDataSet(final TrackedData<?> data) {
        if (ZombieEntity.BABY.equals(data)) {
            this.refreshSize();
        }
        super.onTrackedDataSet(data);
    }
    
    protected boolean canConvertInWater() {
        return true;
    }
    
    @Override
    public void tick() {
        if (!this.world.isClient && this.isAlive()) {
            if (this.isConvertingInWater()) {
                --this.ticksUntilWaterConversion;
                if (this.ticksUntilWaterConversion < 0) {
                    this.convertInWater();
                }
            }
            else if (this.canConvertInWater()) {
                if (this.isInFluid(FluidTags.a)) {
                    ++this.inWaterTime;
                    if (this.inWaterTime >= 600) {
                        this.setTicksUntilWaterConversion(300);
                    }
                }
                else {
                    this.inWaterTime = -1;
                }
            }
        }
        super.tick();
    }
    
    @Override
    public void updateState() {
        if (this.isAlive()) {
            boolean boolean1 = this.burnsInDaylight() && this.isInDaylight();
            if (boolean1) {
                final ItemStack itemStack2 = this.getEquippedStack(EquipmentSlot.HEAD);
                if (!itemStack2.isEmpty()) {
                    if (itemStack2.hasDurability()) {
                        itemStack2.setDamage(itemStack2.getDamage() + this.random.nextInt(2));
                        if (itemStack2.getDamage() >= itemStack2.getDurability()) {
                            this.sendEquipmentBreakStatus(EquipmentSlot.HEAD);
                            this.setEquippedStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    }
                    boolean1 = false;
                }
                if (boolean1) {
                    this.setOnFireFor(8);
                }
            }
        }
        super.updateState();
    }
    
    private void setTicksUntilWaterConversion(final int integer) {
        this.ticksUntilWaterConversion = integer;
        this.getDataTracker().<Boolean>set(ZombieEntity.CONVERTING_IN_WATER, true);
    }
    
    protected void convertInWater() {
        this.convertTo(EntityType.DROWNED);
        this.world.playLevelEvent(null, 1040, new BlockPos((int)this.x, (int)this.y, (int)this.z), 0);
    }
    
    protected void convertTo(final EntityType<? extends ZombieEntity> entityType) {
        if (this.removed) {
            return;
        }
        final ZombieEntity zombieEntity2 = (ZombieEntity)entityType.create(this.world);
        zombieEntity2.setPositionAndAngles(this);
        zombieEntity2.setCanPickUpLoot(this.canPickUpLoot());
        zombieEntity2.setCanBreakDoors(zombieEntity2.shouldBreakDoors() && this.canBreakDoors());
        zombieEntity2.v(zombieEntity2.world.getLocalDifficulty(new BlockPos(zombieEntity2)).getClampedLocalDifficulty());
        zombieEntity2.setChild(this.isChild());
        zombieEntity2.setAiDisabled(this.isAiDisabled());
        for (final EquipmentSlot equipmentSlot6 : EquipmentSlot.values()) {
            final ItemStack itemStack7 = this.getEquippedStack(equipmentSlot6);
            if (!itemStack7.isEmpty()) {
                zombieEntity2.setEquippedStack(equipmentSlot6, itemStack7);
                zombieEntity2.setEquipmentDropChance(equipmentSlot6, this.getDropChance(equipmentSlot6));
            }
        }
        if (this.hasCustomName()) {
            zombieEntity2.setCustomName(this.getCustomName());
            zombieEntity2.setCustomNameVisible(this.isCustomNameVisible());
        }
        this.world.spawnEntity(zombieEntity2);
        this.remove();
    }
    
    protected boolean burnsInDaylight() {
        return true;
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        if (super.damage(source, amount)) {
            LivingEntity livingEntity3 = this.getTarget();
            if (livingEntity3 == null && source.getAttacker() instanceof LivingEntity) {
                livingEntity3 = (LivingEntity)source.getAttacker();
            }
            if (livingEntity3 != null && this.world.getDifficulty() == Difficulty.HARD && this.random.nextFloat() < this.getAttributeInstance(ZombieEntity.SPAWN_REINFORCEMENTS).getValue() && this.world.getGameRules().getBoolean("doMobSpawning")) {
                final int integer4 = MathHelper.floor(this.x);
                final int integer5 = MathHelper.floor(this.y);
                final int integer6 = MathHelper.floor(this.z);
                final ZombieEntity zombieEntity7 = new ZombieEntity(this.world);
                for (int integer7 = 0; integer7 < 50; ++integer7) {
                    final int integer8 = integer4 + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                    final int integer9 = integer5 + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                    final int integer10 = integer6 + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                    final BlockPos blockPos12 = new BlockPos(integer8, integer9 - 1, integer10);
                    if (this.world.getBlockState(blockPos12).hasSolidTopSurface(this.world, blockPos12, zombieEntity7) && this.world.getLightLevel(new BlockPos(integer8, integer9, integer10)) < 10) {
                        zombieEntity7.setPosition(integer8, integer9, integer10);
                        if (!this.world.isPlayerInRange(integer8, integer9, integer10, 7.0) && this.world.intersectsEntities(zombieEntity7) && this.world.doesNotCollide(zombieEntity7) && !this.world.intersectsFluid(zombieEntity7.getBoundingBox())) {
                            this.world.spawnEntity(zombieEntity7);
                            zombieEntity7.setTarget(livingEntity3);
                            zombieEntity7.initialize(this.world, this.world.getLocalDifficulty(new BlockPos(zombieEntity7)), SpawnType.j, null, null);
                            this.getAttributeInstance(ZombieEntity.SPAWN_REINFORCEMENTS).addModifier(new EntityAttributeModifier("Zombie reinforcement caller charge", -0.05000000074505806, EntityAttributeModifier.Operation.a));
                            zombieEntity7.getAttributeInstance(ZombieEntity.SPAWN_REINFORCEMENTS).addModifier(new EntityAttributeModifier("Zombie reinforcement callee charge", -0.05000000074505806, EntityAttributeModifier.Operation.a));
                            break;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean tryAttack(final Entity entity) {
        final boolean boolean2 = super.tryAttack(entity);
        if (boolean2) {
            final float float3 = this.world.getLocalDifficulty(new BlockPos(this)).getLocalDifficulty();
            if (this.getMainHandStack().isEmpty() && this.isOnFire() && this.random.nextFloat() < float3 * 0.3f) {
                entity.setOnFireFor(2 * (int)float3);
            }
        }
        return boolean2;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.nS;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.oc;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.nX;
    }
    
    protected SoundEvent getStepSound() {
        return SoundEvents.oi;
    }
    
    @Override
    protected void playStepSound(final BlockPos pos, final BlockState state) {
        this.playSound(this.getStepSound(), 0.15f, 1.0f);
    }
    
    @Override
    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }
    
    @Override
    protected void initEquipment(final LocalDifficulty localDifficulty) {
        super.initEquipment(localDifficulty);
        if (this.random.nextFloat() < ((this.world.getDifficulty() == Difficulty.HARD) ? 0.05f : 0.01f)) {
            final int integer2 = this.random.nextInt(3);
            if (integer2 == 0) {
                this.setEquippedStack(EquipmentSlot.HAND_MAIN, new ItemStack(Items.jm));
            }
            else {
                this.setEquippedStack(EquipmentSlot.HAND_MAIN, new ItemStack(Items.ja));
            }
        }
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        if (this.isChild()) {
            tag.putBoolean("IsBaby", true);
        }
        tag.putBoolean("CanBreakDoors", this.canBreakDoors());
        tag.putInt("InWaterTime", this.isInsideWater() ? this.inWaterTime : -1);
        tag.putInt("DrownedConversionTime", this.isConvertingInWater() ? this.ticksUntilWaterConversion : -1);
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.getBoolean("IsBaby")) {
            this.setChild(true);
        }
        this.setCanBreakDoors(tag.getBoolean("CanBreakDoors"));
        this.inWaterTime = tag.getInt("InWaterTime");
        if (tag.containsKey("DrownedConversionTime", 99) && tag.getInt("DrownedConversionTime") > -1) {
            this.setTicksUntilWaterConversion(tag.getInt("DrownedConversionTime"));
        }
    }
    
    @Override
    public void b(final LivingEntity livingEntity) {
        super.b(livingEntity);
        if ((this.world.getDifficulty() == Difficulty.NORMAL || this.world.getDifficulty() == Difficulty.HARD) && livingEntity instanceof VillagerEntity) {
            if (this.world.getDifficulty() != Difficulty.HARD && this.random.nextBoolean()) {
                return;
            }
            final VillagerEntity villagerEntity2 = (VillagerEntity)livingEntity;
            final ZombieVillagerEntity zombieVillagerEntity3 = EntityType.ZOMBIE_VILLAGER.create(this.world);
            zombieVillagerEntity3.setPositionAndAngles(villagerEntity2);
            villagerEntity2.remove();
            zombieVillagerEntity3.initialize(this.world, this.world.getLocalDifficulty(new BlockPos(zombieVillagerEntity3)), SpawnType.i, new b(false), null);
            zombieVillagerEntity3.setVillagerData(villagerEntity2.getVillagerData());
            zombieVillagerEntity3.setOfferData(villagerEntity2.getOffers().toTag());
            zombieVillagerEntity3.setXp(villagerEntity2.getExperience());
            zombieVillagerEntity3.setChild(villagerEntity2.isChild());
            zombieVillagerEntity3.setAiDisabled(villagerEntity2.isAiDisabled());
            if (villagerEntity2.hasCustomName()) {
                zombieVillagerEntity3.setCustomName(villagerEntity2.getCustomName());
                zombieVillagerEntity3.setCustomNameVisible(villagerEntity2.isCustomNameVisible());
            }
            this.world.spawnEntity(zombieVillagerEntity3);
            this.world.playLevelEvent(null, 1026, new BlockPos(this), 0);
        }
    }
    
    @Override
    protected float getActiveEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        return this.isChild() ? 0.93f : 1.74f;
    }
    
    @Override
    protected boolean canPickupItem(final ItemStack itemStack) {
        return (itemStack.getItem() != Items.kW || !this.isChild() || !this.hasVehicle()) && super.canPickupItem(itemStack);
    }
    
    @Nullable
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable EntityData entityData, @Nullable final CompoundTag compoundTag) {
        entityData = super.initialize(iWorld, localDifficulty, difficulty, entityData, compoundTag);
        final float float6 = localDifficulty.getClampedLocalDifficulty();
        this.setCanPickUpLoot(this.random.nextFloat() < 0.55f * float6);
        if (entityData == null) {
            entityData = new b(iWorld.getRandom().nextFloat() < 0.05f);
        }
        if (entityData instanceof b) {
            final b b7 = (b)entityData;
            if (b7.a) {
                this.setChild(true);
                if (iWorld.getRandom().nextFloat() < 0.05) {
                    final List<ChickenEntity> list8 = iWorld.<ChickenEntity>getEntities(ChickenEntity.class, this.getBoundingBox().expand(5.0, 3.0, 5.0), EntityPredicates.NOT_MOUNTED);
                    if (!list8.isEmpty()) {
                        final ChickenEntity chickenEntity9 = list8.get(0);
                        chickenEntity9.setHasJockey(true);
                        this.startRiding(chickenEntity9);
                    }
                }
                else if (iWorld.getRandom().nextFloat() < 0.05) {
                    final ChickenEntity chickenEntity10 = EntityType.CHICKEN.create(this.world);
                    chickenEntity10.setPositionAndAngles(this.x, this.y, this.z, this.yaw, 0.0f);
                    chickenEntity10.initialize(iWorld, localDifficulty, SpawnType.g, null, null);
                    chickenEntity10.setHasJockey(true);
                    iWorld.spawnEntity(chickenEntity10);
                    this.startRiding(chickenEntity10);
                }
            }
            this.setCanBreakDoors(this.shouldBreakDoors() && this.random.nextFloat() < float6 * 0.1f);
            this.initEquipment(localDifficulty);
            this.updateEnchantments(localDifficulty);
        }
        if (this.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
            final LocalDate localDate7 = LocalDate.now();
            final int integer8 = localDate7.get(ChronoField.DAY_OF_MONTH);
            final int integer9 = localDate7.get(ChronoField.MONTH_OF_YEAR);
            if (integer9 == 10 && integer8 == 31 && this.random.nextFloat() < 0.25f) {
                this.setEquippedStack(EquipmentSlot.HEAD, new ItemStack((this.random.nextFloat() < 0.1f) ? Blocks.cO : Blocks.cN));
                this.armorDropChances[EquipmentSlot.HEAD.getEntitySlotId()] = 0.0f;
            }
        }
        this.v(float6);
        return entityData;
    }
    
    protected void v(final float float1) {
        this.getAttributeInstance(EntityAttributes.KNOCKBACK_RESISTANCE).addModifier(new EntityAttributeModifier("Random spawn bonus", this.random.nextDouble() * 0.05000000074505806, EntityAttributeModifier.Operation.a));
        final double double2 = this.random.nextDouble() * 1.5 * float1;
        if (double2 > 1.0) {
            this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).addModifier(new EntityAttributeModifier("Random zombie-spawn bonus", double2, EntityAttributeModifier.Operation.c));
        }
        if (this.random.nextFloat() < float1 * 0.05f) {
            this.getAttributeInstance(ZombieEntity.SPAWN_REINFORCEMENTS).addModifier(new EntityAttributeModifier("Leader zombie bonus", this.random.nextDouble() * 0.25 + 0.5, EntityAttributeModifier.Operation.a));
            this.getAttributeInstance(EntityAttributes.MAX_HEALTH).addModifier(new EntityAttributeModifier("Leader zombie bonus", this.random.nextDouble() * 3.0 + 1.0, EntityAttributeModifier.Operation.c));
            this.setCanBreakDoors(this.shouldBreakDoors());
        }
    }
    
    @Override
    public double getHeightOffset() {
        return this.isChild() ? 0.0 : -0.45;
    }
    
    @Override
    protected void dropEquipment(final DamageSource damageSource, final int addedDropChance, final boolean dropAllowed) {
        super.dropEquipment(damageSource, addedDropChance, dropAllowed);
        final Entity entity4 = damageSource.getAttacker();
        if (entity4 instanceof CreeperEntity) {
            final CreeperEntity creeperEntity5 = (CreeperEntity)entity4;
            if (creeperEntity5.shouldDropHead()) {
                creeperEntity5.onHeadDropped();
                final ItemStack itemStack6 = this.getSkull();
                if (!itemStack6.isEmpty()) {
                    this.dropStack(itemStack6);
                }
            }
        }
    }
    
    protected ItemStack getSkull() {
        return new ItemStack(Items.ZOMBIE_HEAD);
    }
    
    static {
        SPAWN_REINFORCEMENTS = new ClampedEntityAttribute(null, "zombie.spawnReinforcements", 0.0, 0.0, 1.0).setName("Spawn Reinforcements Chance");
        BABY_SPEED_ID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
        BABY_SPEED_BONUS = new EntityAttributeModifier(ZombieEntity.BABY_SPEED_ID, "Baby speed boost", 0.5, EntityAttributeModifier.Operation.b);
        BABY = DataTracker.<Boolean>registerData(ZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        bA = DataTracker.<Integer>registerData(ZombieEntity.class, TrackedDataHandlerRegistry.INTEGER);
        CONVERTING_IN_WATER = DataTracker.<Boolean>registerData(ZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        bC = (difficulty -> difficulty == Difficulty.HARD);
    }
    
    public class b implements EntityData
    {
        public final boolean a;
        
        private b(final boolean boolean2) {
            this.a = boolean2;
        }
    }
    
    class DestroyEggGoal extends StepAndDestroyBlockGoal
    {
        DestroyEggGoal(final MobEntityWithAi mobEntityWithAi, final double double3, final int integer5) {
            super(Blocks.jX, mobEntityWithAi, double3, integer5);
        }
        
        @Override
        public void tickStepping(final IWorld world, final BlockPos pos) {
            world.playSound(null, pos, SoundEvents.nY, SoundCategory.f, 0.5f, 0.9f + ZombieEntity.this.random.nextFloat() * 0.2f);
        }
        
        @Override
        public void onDestroyBlock(final World world, final BlockPos pos) {
            world.playSound(null, pos, SoundEvents.lY, SoundCategory.e, 0.7f, 0.9f + world.random.nextFloat() * 0.2f);
        }
        
        @Override
        public double getDesiredSquaredDistanceToTarget() {
            return 1.14;
        }
    }
}
