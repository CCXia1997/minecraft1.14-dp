package net.minecraft.entity.passive;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.ZombiePigmanEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.item.Items;
import net.minecraft.item.ItemProvider;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.recipe.Ingredient;
import net.minecraft.entity.data.TrackedData;

public class PigEntity extends AnimalEntity
{
    private static final TrackedData<Boolean> SADDLED;
    private static final TrackedData<Integer> bA;
    private static final Ingredient BREEDING_INGREDIENT;
    private boolean bD;
    private int bE;
    private int bF;
    
    public PigEntity(final EntityType<? extends PigEntity> type, final World world) {
        super(type, world);
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.25));
        this.goalSelector.add(3, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(4, new TemptGoal(this, 1.2, Ingredient.ofItems(Items.nU), false));
        this.goalSelector.add(4, new TemptGoal(this, 1.2, false, PigEntity.BREEDING_INGREDIENT));
        this.goalSelector.add(5, new FollowParentGoal(this, 1.1));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
    }
    
    @Nullable
    @Override
    public Entity getPrimaryPassenger() {
        if (this.getPassengerList().isEmpty()) {
            return null;
        }
        return this.getPassengerList().get(0);
    }
    
    @Override
    public boolean canBeControlledByRider() {
        final Entity entity1 = this.getPrimaryPassenger();
        if (!(entity1 instanceof PlayerEntity)) {
            return false;
        }
        final PlayerEntity playerEntity2 = (PlayerEntity)entity1;
        return playerEntity2.getMainHandStack().getItem() == Items.nU || playerEntity2.getOffHandStack().getItem() == Items.nU;
    }
    
    @Override
    public void onTrackedDataSet(final TrackedData<?> data) {
        if (PigEntity.bA.equals(data) && this.world.isClient) {
            this.bD = true;
            this.bE = 0;
            this.bF = this.dataTracker.<Integer>get(PigEntity.bA);
        }
        super.onTrackedDataSet(data);
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Boolean>startTracking(PigEntity.SADDLED, false);
        this.dataTracker.<Integer>startTracking(PigEntity.bA, 0);
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putBoolean("Saddle", this.isSaddled());
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setSaddled(tag.getBoolean("Saddle"));
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.in;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.ip;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.io;
    }
    
    @Override
    protected void playStepSound(final BlockPos pos, final BlockState state) {
        this.playSound(SoundEvents.ir, 0.15f, 1.0f);
    }
    
    @Override
    public boolean interactMob(final PlayerEntity player, final Hand hand) {
        if (super.interactMob(player, hand)) {
            return true;
        }
        final ItemStack itemStack3 = player.getStackInHand(hand);
        if (itemStack3.getItem() == Items.or) {
            itemStack3.interactWithEntity(player, this, hand);
            return true;
        }
        if (this.isSaddled() && !this.hasPassengers()) {
            if (!this.world.isClient) {
                player.startRiding(this);
            }
            return true;
        }
        if (itemStack3.getItem() == Items.kB) {
            itemStack3.interactWithEntity(player, this, hand);
            return true;
        }
        return false;
    }
    
    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.isSaddled()) {
            this.dropItem(Items.kB);
        }
    }
    
    public boolean isSaddled() {
        return this.dataTracker.<Boolean>get(PigEntity.SADDLED);
    }
    
    public void setSaddled(final boolean saddled) {
        if (saddled) {
            this.dataTracker.<Boolean>set(PigEntity.SADDLED, true);
        }
        else {
            this.dataTracker.<Boolean>set(PigEntity.SADDLED, false);
        }
    }
    
    @Override
    public void onStruckByLightning(final LightningEntity lightning) {
        final ZombiePigmanEntity zombiePigmanEntity2 = EntityType.ZOMBIE_PIGMAN.create(this.world);
        zombiePigmanEntity2.setEquippedStack(EquipmentSlot.HAND_MAIN, new ItemStack(Items.jC));
        zombiePigmanEntity2.setPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
        zombiePigmanEntity2.setAiDisabled(this.isAiDisabled());
        if (this.hasCustomName()) {
            zombiePigmanEntity2.setCustomName(this.getCustomName());
            zombiePigmanEntity2.setCustomNameVisible(this.isCustomNameVisible());
        }
        this.world.spawnEntity(zombiePigmanEntity2);
        this.remove();
    }
    
    @Override
    public void travel(final Vec3d movementInput) {
        if (!this.isAlive()) {
            return;
        }
        final Entity entity2 = this.getPassengerList().isEmpty() ? null : this.getPassengerList().get(0);
        if (!this.hasPassengers() || !this.canBeControlledByRider()) {
            this.stepHeight = 0.5f;
            this.aO = 0.02f;
            super.travel(movementInput);
            return;
        }
        this.yaw = entity2.yaw;
        this.prevYaw = this.yaw;
        this.pitch = entity2.pitch * 0.5f;
        this.setRotation(this.yaw, this.pitch);
        this.aK = this.yaw;
        this.headYaw = this.yaw;
        this.stepHeight = 1.0f;
        this.aO = this.getMovementSpeed() * 0.1f;
        if (this.bD && this.bE++ > this.bF) {
            this.bD = false;
        }
        if (this.isLogicalSideForUpdatingMovement()) {
            float float3 = (float)this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue() * 0.225f;
            if (this.bD) {
                float3 += float3 * 1.15f * MathHelper.sin(this.bE / (float)this.bF * 3.1415927f);
            }
            this.setMovementSpeed(float3);
            super.travel(new Vec3d(0.0, 0.0, 1.0));
        }
        else {
            this.setVelocity(Vec3d.ZERO);
        }
        this.lastLimbDistance = this.limbDistance;
        final double double3 = this.x - this.prevX;
        final double double4 = this.z - this.prevZ;
        float float4 = MathHelper.sqrt(double3 * double3 + double4 * double4) * 4.0f;
        if (float4 > 1.0f) {
            float4 = 1.0f;
        }
        this.limbDistance += (float4 - this.limbDistance) * 0.4f;
        this.limbAngle += this.limbDistance;
    }
    
    public boolean dW() {
        if (this.bD) {
            return false;
        }
        this.bD = true;
        this.bE = 0;
        this.bF = this.getRand().nextInt(841) + 140;
        this.getDataTracker().<Integer>set(PigEntity.bA, this.bF);
        return true;
    }
    
    @Override
    public PigEntity createChild(final PassiveEntity mate) {
        return EntityType.PIG.create(this.world);
    }
    
    @Override
    public boolean isBreedingItem(final ItemStack stack) {
        return PigEntity.BREEDING_INGREDIENT.a(stack);
    }
    
    static {
        SADDLED = DataTracker.<Boolean>registerData(PigEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        bA = DataTracker.<Integer>registerData(PigEntity.class, TrackedDataHandlerRegistry.INTEGER);
        BREEDING_INGREDIENT = Ingredient.ofItems(Items.nI, Items.nJ, Items.oO);
    }
}
