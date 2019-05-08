package net.minecraft.entity.passive;

import net.minecraft.world.BlockView;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.tag.FluidTags;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import java.util.Random;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Position;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.biome.Biomes;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Hand;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.ai.goal.ChaseBoatGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.DolphinJumpGoal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.SwimAroundGoal;
import net.minecraft.entity.ai.goal.MoveIntoWaterGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.ai.goal.BreatheAirGoal;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.IWorld;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.ai.control.DolphinLookControl;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import java.util.function.Predicate;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.WaterCreatureEntity;

public class DolphinEntity extends WaterCreatureEntity
{
    private static final TrackedData<BlockPos> TREASURE_POS;
    private static final TrackedData<Boolean> HAS_FISH;
    private static final TrackedData<Integer> MOISTNESS;
    private static final TargetPredicate CLOSE_PLAYER_PREDICATE;
    public static final Predicate<ItemEntity> CAN_TAKE;
    
    public DolphinEntity(final EntityType<? extends DolphinEntity> type, final World world) {
        super(type, world);
        this.moveControl = new DolphinMoveControl(this);
        this.lookControl = new DolphinLookControl(this, 10);
        this.setCanPickUpLoot(true);
    }
    
    @Nullable
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable final EntityData entityData, @Nullable final CompoundTag compoundTag) {
        this.setBreath(this.getMaxBreath());
        this.pitch = 0.0f;
        return super.initialize(iWorld, localDifficulty, difficulty, entityData, compoundTag);
    }
    
    @Override
    public boolean canBreatheInWater() {
        return false;
    }
    
    @Override
    protected void tickBreath(final int breath) {
    }
    
    public void setTreasurePos(final BlockPos treasurePos) {
        this.dataTracker.<BlockPos>set(DolphinEntity.TREASURE_POS, treasurePos);
    }
    
    public BlockPos getTreasurePos() {
        return this.dataTracker.<BlockPos>get(DolphinEntity.TREASURE_POS);
    }
    
    public boolean hasFish() {
        return this.dataTracker.<Boolean>get(DolphinEntity.HAS_FISH);
    }
    
    public void setHasFish(final boolean hasFish) {
        this.dataTracker.<Boolean>set(DolphinEntity.HAS_FISH, hasFish);
    }
    
    public int getMoistness() {
        return this.dataTracker.<Integer>get(DolphinEntity.MOISTNESS);
    }
    
    public void setMoistness(final int moistness) {
        this.dataTracker.<Integer>set(DolphinEntity.MOISTNESS, moistness);
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<BlockPos>startTracking(DolphinEntity.TREASURE_POS, BlockPos.ORIGIN);
        this.dataTracker.<Boolean>startTracking(DolphinEntity.HAS_FISH, false);
        this.dataTracker.<Integer>startTracking(DolphinEntity.MOISTNESS, 2400);
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("TreasurePosX", this.getTreasurePos().getX());
        tag.putInt("TreasurePosY", this.getTreasurePos().getY());
        tag.putInt("TreasurePosZ", this.getTreasurePos().getZ());
        tag.putBoolean("GotFish", this.hasFish());
        tag.putInt("Moistness", this.getMoistness());
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        final int integer2 = tag.getInt("TreasurePosX");
        final int integer3 = tag.getInt("TreasurePosY");
        final int integer4 = tag.getInt("TreasurePosZ");
        this.setTreasurePos(new BlockPos(integer2, integer3, integer4));
        super.readCustomDataFromTag(tag);
        this.setHasFish(tag.getBoolean("GotFish"));
        this.setMoistness(tag.getInt("Moistness"));
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new BreatheAirGoal(this));
        this.goalSelector.add(0, new MoveIntoWaterGoal(this));
        this.goalSelector.add(1, new b(this));
        this.goalSelector.add(2, new SwimWithPlayerGoal(this, 4.0));
        this.goalSelector.add(4, new SwimAroundGoal(this, 1.0, 10));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(5, new DolphinJumpGoal(this, 10));
        this.goalSelector.add(6, new MeleeAttackGoal(this, 1.2000000476837158, true));
        this.goalSelector.add(8, new d());
        this.goalSelector.add(8, new ChaseBoatGoal(this));
        this.goalSelector.add(9, new FleeEntityGoal<>(this, GuardianEntity.class, 8.0f, 1.0, 1.0));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[] { GuardianEntity.class }).setGroupRevenge(new Class[0]));
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(1.2000000476837158);
        this.getAttributeContainer().register(EntityAttributes.ATTACK_DAMAGE);
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(3.0);
    }
    
    @Override
    protected EntityNavigation createNavigation(final World world) {
        return new SwimNavigation(this, world);
    }
    
    @Override
    public boolean tryAttack(final Entity entity) {
        final boolean boolean2 = entity.damage(DamageSource.mob(this), (float)(int)this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getValue());
        if (boolean2) {
            this.dealDamage(this, entity);
            this.playSound(SoundEvents.bN, 1.0f, 1.0f);
        }
        return boolean2;
    }
    
    @Override
    public int getMaxBreath() {
        return 4800;
    }
    
    @Override
    protected int getNextBreathInAir(final int breath) {
        return this.getMaxBreath();
    }
    
    @Override
    protected float getActiveEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        return 0.3f;
    }
    
    @Override
    public int getLookPitchSpeed() {
        return 1;
    }
    
    @Override
    public int dA() {
        return 1;
    }
    
    @Override
    protected boolean canStartRiding(final Entity entity) {
        return true;
    }
    
    @Override
    public boolean canPickUp(final ItemStack itemStack) {
        final EquipmentSlot equipmentSlot2 = MobEntity.getPreferredEquipmentSlot(itemStack);
        return this.getEquippedStack(equipmentSlot2).isEmpty() && equipmentSlot2 == EquipmentSlot.HAND_MAIN && super.canPickUp(itemStack);
    }
    
    @Override
    protected void loot(final ItemEntity item) {
        if (this.getEquippedStack(EquipmentSlot.HAND_MAIN).isEmpty()) {
            final ItemStack itemStack2 = item.getStack();
            if (this.canPickupItem(itemStack2)) {
                this.setEquippedStack(EquipmentSlot.HAND_MAIN, itemStack2);
                this.handDropChances[EquipmentSlot.HAND_MAIN.getEntitySlotId()] = 2.0f;
                this.sendPickup(item, itemStack2.getAmount());
                item.remove();
            }
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.isAiDisabled()) {
            return;
        }
        if (this.isTouchingWater()) {
            this.setMoistness(2400);
        }
        else {
            this.setMoistness(this.getMoistness() - 1);
            if (this.getMoistness() <= 0) {
                this.damage(DamageSource.DRYOUT, 1.0f);
            }
            if (this.onGround) {
                this.setVelocity(this.getVelocity().add((this.random.nextFloat() * 2.0f - 1.0f) * 0.2f, 0.5, (this.random.nextFloat() * 2.0f - 1.0f) * 0.2f));
                this.yaw = this.random.nextFloat() * 360.0f;
                this.onGround = false;
                this.velocityDirty = true;
            }
        }
        if (this.world.isClient && this.isInsideWater() && this.getVelocity().lengthSquared() > 0.03) {
            final Vec3d vec3d1 = this.getRotationVec(0.0f);
            final float float2 = MathHelper.cos(this.yaw * 0.017453292f) * 0.3f;
            final float float3 = MathHelper.sin(this.yaw * 0.017453292f) * 0.3f;
            final float float4 = 1.2f - this.random.nextFloat() * 0.7f;
            for (int integer5 = 0; integer5 < 2; ++integer5) {
                this.world.addParticle(ParticleTypes.ad, this.x - vec3d1.x * float4 + float2, this.y - vec3d1.y, this.z - vec3d1.z * float4 + float3, 0.0, 0.0, 0.0);
                this.world.addParticle(ParticleTypes.ad, this.x - vec3d1.x * float4 - float2, this.y - vec3d1.y, this.z - vec3d1.z * float4 - float3, 0.0, 0.0, 0.0);
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void handleStatus(final byte status) {
        if (status == 38) {
            this.spawnParticlesAround(ParticleTypes.C);
        }
        else {
            super.handleStatus(status);
        }
    }
    
    @Environment(EnvType.CLIENT)
    private void spawnParticlesAround(final ParticleParameters parameters) {
        for (int integer2 = 0; integer2 < 7; ++integer2) {
            final double double3 = this.random.nextGaussian() * 0.01;
            final double double4 = this.random.nextGaussian() * 0.01;
            final double double5 = this.random.nextGaussian() * 0.01;
            this.world.addParticle(parameters, this.x + this.random.nextFloat() * this.getWidth() * 2.0f - this.getWidth(), this.y + 0.20000000298023224 + this.random.nextFloat() * this.getHeight(), this.z + this.random.nextFloat() * this.getWidth() * 2.0f - this.getWidth(), double3, double4, double5);
        }
    }
    
    @Override
    protected boolean interactMob(final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack3 = player.getStackInHand(hand);
        if (!itemStack3.isEmpty() && itemStack3.getItem().matches(ItemTags.I)) {
            if (!this.world.isClient) {
                this.playSound(SoundEvents.bP, 1.0f, 1.0f);
            }
            this.setHasFish(true);
            if (!player.abilities.creativeMode) {
                itemStack3.subtractAmount(1);
            }
            return true;
        }
        return super.interactMob(player, hand);
    }
    
    @Override
    public boolean canSpawn(final IWorld iWorld, final SpawnType spawnType) {
        return (this.y > 45.0 && this.y < iWorld.getSeaLevel() && iWorld.getBiome(new BlockPos(this)) != Biomes.a) || (iWorld.getBiome(new BlockPos(this)) != Biomes.z && super.canSpawn(iWorld, spawnType));
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.bQ;
    }
    
    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.bO;
    }
    
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return this.isInsideWater() ? SoundEvents.bM : SoundEvents.bL;
    }
    
    @Override
    protected SoundEvent getSplashSound() {
        return SoundEvents.bT;
    }
    
    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.bU;
    }
    
    protected boolean isCloseToTarget() {
        final BlockPos blockPos1 = this.getNavigation().getTargetPos();
        return blockPos1 != null && blockPos1.isWithinDistance(this.getPos(), 12.0);
    }
    
    @Override
    public void travel(final Vec3d movementInput) {
        if (this.canMoveVoluntarily() && this.isInsideWater()) {
            this.updateVelocity(this.getMovementSpeed(), movementInput);
            this.move(MovementType.a, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.9));
            if (this.getTarget() == null) {
                this.setVelocity(this.getVelocity().add(0.0, -0.005, 0.0));
            }
        }
        else {
            super.travel(movementInput);
        }
    }
    
    @Override
    public boolean canBeLeashedBy(final PlayerEntity player) {
        return true;
    }
    
    static {
        TREASURE_POS = DataTracker.<BlockPos>registerData(DolphinEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
        HAS_FISH = DataTracker.<Boolean>registerData(DolphinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        MOISTNESS = DataTracker.<Integer>registerData(DolphinEntity.class, TrackedDataHandlerRegistry.INTEGER);
        CLOSE_PLAYER_PREDICATE = new TargetPredicate().setBaseMaxDistance(10.0).includeTeammates().includeInvulnerable();
        CAN_TAKE = (itemEntity -> !itemEntity.cannotPickup() && itemEntity.isAlive() && itemEntity.isInsideWater());
    }
    
    static class DolphinMoveControl extends MoveControl
    {
        private final DolphinEntity dolphin;
        
        public DolphinMoveControl(final DolphinEntity dolphinEntity) {
            super(dolphinEntity);
            this.dolphin = dolphinEntity;
        }
        
        @Override
        public void tick() {
            if (this.dolphin.isInsideWater()) {
                this.dolphin.setVelocity(this.dolphin.getVelocity().add(0.0, 0.005, 0.0));
            }
            if (this.state != State.b || this.dolphin.getNavigation().isIdle()) {
                this.dolphin.setMovementSpeed(0.0f);
                this.dolphin.setSidewaysSpeed(0.0f);
                this.dolphin.setUpwardSpeed(0.0f);
                this.dolphin.setForwardSpeed(0.0f);
                return;
            }
            final double double1 = this.targetX - this.dolphin.x;
            final double double2 = this.targetY - this.dolphin.y;
            final double double3 = this.targetZ - this.dolphin.z;
            final double double4 = double1 * double1 + double2 * double2 + double3 * double3;
            if (double4 < 2.500000277905201E-7) {
                this.entity.setForwardSpeed(0.0f);
                return;
            }
            final float float9 = (float)(MathHelper.atan2(double3, double1) * 57.2957763671875) - 90.0f;
            this.dolphin.yaw = this.changeAngle(this.dolphin.yaw, float9, 10.0f);
            this.dolphin.aK = this.dolphin.yaw;
            this.dolphin.headYaw = this.dolphin.yaw;
            final float float10 = (float)(this.speed * this.dolphin.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue());
            if (this.dolphin.isInsideWater()) {
                this.dolphin.setMovementSpeed(float10 * 0.02f);
                float float11 = -(float)(MathHelper.atan2(double2, MathHelper.sqrt(double1 * double1 + double3 * double3)) * 57.2957763671875);
                float11 = MathHelper.clamp(MathHelper.wrapDegrees(float11), -85.0f, 85.0f);
                this.dolphin.pitch = this.changeAngle(this.dolphin.pitch, float11, 5.0f);
                final float float12 = MathHelper.cos(this.dolphin.pitch * 0.017453292f);
                final float float13 = MathHelper.sin(this.dolphin.pitch * 0.017453292f);
                this.dolphin.forwardSpeed = float12 * float10;
                this.dolphin.upwardSpeed = -float13 * float10;
            }
            else {
                this.dolphin.setMovementSpeed(float10 * 0.1f);
            }
        }
    }
    
    class d extends Goal
    {
        private int b;
        
        private d() {
        }
        
        @Override
        public boolean canStart() {
            if (this.b > DolphinEntity.this.age) {
                return false;
            }
            final List<ItemEntity> list1 = DolphinEntity.this.world.<ItemEntity>getEntities(ItemEntity.class, DolphinEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), DolphinEntity.CAN_TAKE);
            return !list1.isEmpty() || !DolphinEntity.this.getEquippedStack(EquipmentSlot.HAND_MAIN).isEmpty();
        }
        
        @Override
        public void start() {
            final List<ItemEntity> list1 = DolphinEntity.this.world.<ItemEntity>getEntities(ItemEntity.class, DolphinEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), DolphinEntity.CAN_TAKE);
            if (!list1.isEmpty()) {
                DolphinEntity.this.getNavigation().startMovingTo(list1.get(0), 1.2000000476837158);
                DolphinEntity.this.playSound(SoundEvents.bS, 1.0f, 1.0f);
            }
            this.b = 0;
        }
        
        @Override
        public void stop() {
            final ItemStack itemStack1 = DolphinEntity.this.getEquippedStack(EquipmentSlot.HAND_MAIN);
            if (!itemStack1.isEmpty()) {
                this.a(itemStack1);
                DolphinEntity.this.setEquippedStack(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
                this.b = DolphinEntity.this.age + DolphinEntity.this.random.nextInt(100);
            }
        }
        
        @Override
        public void tick() {
            final List<ItemEntity> list1 = DolphinEntity.this.world.<ItemEntity>getEntities(ItemEntity.class, DolphinEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), DolphinEntity.CAN_TAKE);
            final ItemStack itemStack2 = DolphinEntity.this.getEquippedStack(EquipmentSlot.HAND_MAIN);
            if (!itemStack2.isEmpty()) {
                this.a(itemStack2);
                DolphinEntity.this.setEquippedStack(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
            }
            else if (!list1.isEmpty()) {
                DolphinEntity.this.getNavigation().startMovingTo(list1.get(0), 1.2000000476837158);
            }
        }
        
        private void a(final ItemStack itemStack) {
            if (itemStack.isEmpty()) {
                return;
            }
            final double double2 = DolphinEntity.this.y - 0.30000001192092896 + DolphinEntity.this.getStandingEyeHeight();
            final ItemEntity itemEntity4 = new ItemEntity(DolphinEntity.this.world, DolphinEntity.this.x, double2, DolphinEntity.this.z, itemStack);
            itemEntity4.setPickupDelay(40);
            itemEntity4.setThrower(DolphinEntity.this.getUuid());
            final float float5 = 0.3f;
            final float float6 = DolphinEntity.this.random.nextFloat() * 6.2831855f;
            final float float7 = 0.02f * DolphinEntity.this.random.nextFloat();
            itemEntity4.setVelocity(0.3f * -MathHelper.sin(DolphinEntity.this.yaw * 0.017453292f) * MathHelper.cos(DolphinEntity.this.pitch * 0.017453292f) + MathHelper.cos(float6) * float7, 0.3f * MathHelper.sin(DolphinEntity.this.pitch * 0.017453292f) * 1.5f, 0.3f * MathHelper.cos(DolphinEntity.this.yaw * 0.017453292f) * MathHelper.cos(DolphinEntity.this.pitch * 0.017453292f) + MathHelper.sin(float6) * float7);
            DolphinEntity.this.world.spawnEntity(itemEntity4);
        }
    }
    
    static class SwimWithPlayerGoal extends Goal
    {
        private final DolphinEntity dolphin;
        private final double speed;
        private PlayerEntity closestPlayer;
        
        SwimWithPlayerGoal(final DolphinEntity dolphin, final double speed) {
            this.dolphin = dolphin;
            this.speed = speed;
            this.setControls(EnumSet.<Control>of(Control.a, Control.b));
        }
        
        @Override
        public boolean canStart() {
            this.closestPlayer = this.dolphin.world.getClosestPlayer(DolphinEntity.CLOSE_PLAYER_PREDICATE, this.dolphin);
            return this.closestPlayer != null && this.closestPlayer.isSwimming();
        }
        
        @Override
        public boolean shouldContinue() {
            return this.closestPlayer != null && this.closestPlayer.isSwimming() && this.dolphin.squaredDistanceTo(this.closestPlayer) < 256.0;
        }
        
        @Override
        public void start() {
            this.closestPlayer.addPotionEffect(new StatusEffectInstance(StatusEffects.D, 100));
        }
        
        @Override
        public void stop() {
            this.closestPlayer = null;
            this.dolphin.getNavigation().stop();
        }
        
        @Override
        public void tick() {
            this.dolphin.getLookControl().lookAt(this.closestPlayer, (float)(this.dolphin.dA() + 20), (float)this.dolphin.getLookPitchSpeed());
            if (this.dolphin.squaredDistanceTo(this.closestPlayer) < 6.25) {
                this.dolphin.getNavigation().stop();
            }
            else {
                this.dolphin.getNavigation().startMovingTo(this.closestPlayer, this.speed);
            }
            if (this.closestPlayer.isSwimming() && this.closestPlayer.world.random.nextInt(6) == 0) {
                this.closestPlayer.addPotionEffect(new StatusEffectInstance(StatusEffects.D, 100));
            }
        }
    }
    
    static class b extends Goal
    {
        private final DolphinEntity a;
        private boolean b;
        
        b(final DolphinEntity dolphinEntity) {
            this.a = dolphinEntity;
            this.setControls(EnumSet.<Control>of(Control.a, Control.b));
        }
        
        @Override
        public boolean canStop() {
            return false;
        }
        
        @Override
        public boolean canStart() {
            return this.a.hasFish() && this.a.getBreath() >= 100;
        }
        
        @Override
        public boolean shouldContinue() {
            final BlockPos blockPos1 = this.a.getTreasurePos();
            return !new BlockPos(blockPos1.getX(), this.a.y, blockPos1.getZ()).isWithinDistance(this.a.getPos(), 4.0) && !this.b && this.a.getBreath() >= 100;
        }
        
        @Override
        public void start() {
            this.b = false;
            this.a.getNavigation().stop();
            final World world1 = this.a.world;
            final BlockPos blockPos2 = new BlockPos(this.a);
            final String string3 = (world1.random.nextFloat() >= 0.5) ? "Ocean_Ruin" : "Shipwreck";
            final BlockPos blockPos3 = world1.locateStructure(string3, blockPos2, 50, false);
            if (blockPos3 == null) {
                final BlockPos blockPos4 = world1.locateStructure(string3.equals("Ocean_Ruin") ? "Shipwreck" : "Ocean_Ruin", blockPos2, 50, false);
                if (blockPos4 == null) {
                    this.b = true;
                    return;
                }
                this.a.setTreasurePos(blockPos4);
            }
            else {
                this.a.setTreasurePos(blockPos3);
            }
            world1.sendEntityStatus(this.a, (byte)38);
        }
        
        @Override
        public void stop() {
            final BlockPos blockPos1 = this.a.getTreasurePos();
            if (new BlockPos(blockPos1.getX(), this.a.y, blockPos1.getZ()).isWithinDistance(this.a.getPos(), 4.0) || this.b) {
                this.a.setHasFish(false);
            }
        }
        
        @Override
        public void tick() {
            final BlockPos blockPos1 = this.a.getTreasurePos();
            final World world2 = this.a.world;
            if (this.a.isCloseToTarget() || this.a.getNavigation().isIdle()) {
                Vec3d vec3d3 = PathfindingUtil.a(this.a, 16, 1, new Vec3d(blockPos1.getX(), blockPos1.getY(), blockPos1.getZ()), 0.39269909262657166);
                if (vec3d3 == null) {
                    vec3d3 = PathfindingUtil.a(this.a, 8, 4, new Vec3d(blockPos1.getX(), blockPos1.getY(), blockPos1.getZ()));
                }
                if (vec3d3 != null) {
                    final BlockPos blockPos2 = new BlockPos(vec3d3);
                    if (!world2.getFluidState(blockPos2).matches(FluidTags.a) || !world2.getBlockState(blockPos2).canPlaceAtSide(world2, blockPos2, BlockPlacementEnvironment.b)) {
                        vec3d3 = PathfindingUtil.a(this.a, 8, 5, new Vec3d(blockPos1.getX(), blockPos1.getY(), blockPos1.getZ()));
                    }
                }
                if (vec3d3 == null) {
                    this.b = true;
                    return;
                }
                this.a.getLookControl().lookAt(vec3d3.x, vec3d3.y, vec3d3.z, (float)(this.a.dA() + 20), (float)this.a.getLookPitchSpeed());
                this.a.getNavigation().startMovingTo(vec3d3.x, vec3d3.y, vec3d3.z, 1.3);
                if (world2.random.nextInt(80) == 0) {
                    world2.sendEntityStatus(this.a, (byte)38);
                }
            }
        }
    }
}
