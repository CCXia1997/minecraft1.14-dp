package net.minecraft.entity.passive;

import net.minecraft.util.math.MathHelper;
import net.minecraft.tag.FluidTags;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.SwimAroundGoal;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.sound.SoundEvents;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.IWorld;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.WaterCreatureEntity;

public abstract class FishEntity extends WaterCreatureEntity
{
    private static final TrackedData<Boolean> FROM_BUCKET;
    
    public FishEntity(final EntityType<? extends FishEntity> type, final World world) {
        super(type, world);
        this.moveControl = new FishMoveControl(this);
    }
    
    @Override
    protected float getActiveEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        return entitySize.height * 0.65f;
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(3.0);
    }
    
    public boolean cannotDespawn() {
        return this.isFromBucket();
    }
    
    @Override
    public boolean canSpawn(final IWorld iWorld, final SpawnType spawnType) {
        final BlockPos blockPos3 = new BlockPos(this);
        return iWorld.getBlockState(blockPos3).getBlock() == Blocks.A && iWorld.getBlockState(blockPos3.up()).getBlock() == Blocks.A && super.canSpawn(iWorld, spawnType);
    }
    
    @Override
    public boolean canImmediatelyDespawn(final double distanceSquared) {
        return !this.isFromBucket() && !this.hasCustomName();
    }
    
    @Override
    public int getLimitPerChunk() {
        return 8;
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Boolean>startTracking(FishEntity.FROM_BUCKET, false);
    }
    
    private boolean isFromBucket() {
        return this.dataTracker.<Boolean>get(FishEntity.FROM_BUCKET);
    }
    
    public void setFromBucket(final boolean boolean1) {
        this.dataTracker.<Boolean>set(FishEntity.FROM_BUCKET, boolean1);
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putBoolean("FromBucket", this.isFromBucket());
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setFromBucket(tag.getBoolean("FromBucket"));
    }
    
    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new EscapeDangerGoal(this, 1.25));
        this.goalSelector.add(2, new FleeEntityGoal<>(this, PlayerEntity.class, 8.0f, 1.6, 1.4, EntityPredicates.EXCEPT_SPECTATOR::test));
        this.goalSelector.add(4, new SwimToRandomPlaceGoal(this));
    }
    
    @Override
    protected EntityNavigation createNavigation(final World world) {
        return new SwimNavigation(this, world);
    }
    
    @Override
    public void travel(final Vec3d movementInput) {
        if (this.canMoveVoluntarily() && this.isInsideWater()) {
            this.updateVelocity(0.01f, movementInput);
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
    public void updateState() {
        if (!this.isInsideWater() && this.onGround && this.verticalCollision) {
            this.setVelocity(this.getVelocity().add((this.random.nextFloat() * 2.0f - 1.0f) * 0.05f, 0.4000000059604645, (this.random.nextFloat() * 2.0f - 1.0f) * 0.05f));
            this.onGround = false;
            this.velocityDirty = true;
            this.playSound(this.getFlopSound(), this.getSoundVolume(), this.getSoundPitch());
        }
        super.updateState();
    }
    
    @Override
    protected boolean interactMob(final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack3 = player.getStackInHand(hand);
        if (itemStack3.getItem() == Items.ky && this.isAlive()) {
            this.playSound(SoundEvents.aA, 1.0f, 1.0f);
            itemStack3.subtractAmount(1);
            final ItemStack itemStack4 = this.getFishBucketItem();
            this.copyDataToStack(itemStack4);
            if (!this.world.isClient) {
                Criterions.FILLED_BUCKET.handle((ServerPlayerEntity)player, itemStack4);
            }
            if (itemStack3.isEmpty()) {
                player.setStackInHand(hand, itemStack4);
            }
            else if (!player.inventory.insertStack(itemStack4)) {
                player.dropItem(itemStack4, false);
            }
            this.remove();
            return true;
        }
        return super.interactMob(player, hand);
    }
    
    protected void copyDataToStack(final ItemStack itemStack) {
        if (this.hasCustomName()) {
            itemStack.setDisplayName(this.getCustomName());
        }
    }
    
    protected abstract ItemStack getFishBucketItem();
    
    protected boolean hasSelfControl() {
        return true;
    }
    
    protected abstract SoundEvent getFlopSound();
    
    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.dr;
    }
    
    static {
        FROM_BUCKET = DataTracker.<Boolean>registerData(FishEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }
    
    static class SwimToRandomPlaceGoal extends SwimAroundGoal
    {
        private final FishEntity h;
        
        public SwimToRandomPlaceGoal(final FishEntity fishEntity) {
            super(fishEntity, 1.0, 40);
            this.h = fishEntity;
        }
        
        @Override
        public boolean canStart() {
            return this.h.hasSelfControl() && super.canStart();
        }
    }
    
    static class FishMoveControl extends MoveControl
    {
        private final FishEntity fish;
        
        FishMoveControl(final FishEntity fishEntity) {
            super(fishEntity);
            this.fish = fishEntity;
        }
        
        @Override
        public void tick() {
            if (this.fish.isInFluid(FluidTags.a)) {
                this.fish.setVelocity(this.fish.getVelocity().add(0.0, 0.005, 0.0));
            }
            if (this.state != State.b || this.fish.getNavigation().isIdle()) {
                this.fish.setMovementSpeed(0.0f);
                return;
            }
            final double double1 = this.targetX - this.fish.x;
            double double2 = this.targetY - this.fish.y;
            final double double3 = this.targetZ - this.fish.z;
            final double double4 = MathHelper.sqrt(double1 * double1 + double2 * double2 + double3 * double3);
            double2 /= double4;
            final float float9 = (float)(MathHelper.atan2(double3, double1) * 57.2957763671875) - 90.0f;
            this.fish.yaw = this.changeAngle(this.fish.yaw, float9, 90.0f);
            this.fish.aK = this.fish.yaw;
            final float float10 = (float)(this.speed * this.fish.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue());
            this.fish.setMovementSpeed(MathHelper.lerp(0.125f, this.fish.getMovementSpeed(), float10));
            this.fish.setVelocity(this.fish.getVelocity().add(0.0, this.fish.getMovementSpeed() * double2 * 0.1, 0.0));
        }
    }
}
