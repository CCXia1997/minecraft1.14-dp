package net.minecraft.entity.passive;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.item.ItemStack;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.recipe.Ingredient;

public class ChickenEntity extends AnimalEntity
{
    private static final Ingredient BREEDING_INGREDIENT;
    public float bz;
    public float bA;
    public float bB;
    public float bD;
    public float bE;
    public int eggLayTime;
    public boolean jockey;
    
    public ChickenEntity(final EntityType<? extends ChickenEntity> type, final World world) {
        super(type, world);
        this.bE = 1.0f;
        this.eggLayTime = this.random.nextInt(6000) + 6000;
        this.setPathNodeTypeWeight(PathNodeType.g, 0.0f);
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.4));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(3, new TemptGoal(this, 1.0, false, ChickenEntity.BREEDING_INGREDIENT));
        this.goalSelector.add(4, new FollowParentGoal(this, 1.1));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(7, new LookAroundGoal(this));
    }
    
    @Override
    protected float getActiveEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        return entitySize.height * 0.95f;
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(4.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
    }
    
    @Override
    public void updateState() {
        super.updateState();
        this.bD = this.bz;
        this.bB = this.bA;
        this.bA += (float)((this.onGround ? -1 : 4) * 0.3);
        this.bA = MathHelper.clamp(this.bA, 0.0f, 1.0f);
        if (!this.onGround && this.bE < 1.0f) {
            this.bE = 1.0f;
        }
        this.bE *= (float)0.9;
        final Vec3d vec3d1 = this.getVelocity();
        if (!this.onGround && vec3d1.y < 0.0) {
            this.setVelocity(vec3d1.multiply(1.0, 0.6, 1.0));
        }
        this.bz += this.bE * 2.0f;
        if (!this.world.isClient && this.isAlive() && !this.isChild() && !this.hasJockey() && --this.eggLayTime <= 0) {
            this.playSound(SoundEvents.aR, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
            this.dropItem(Items.kW);
            this.eggLayTime = this.random.nextInt(6000) + 6000;
        }
    }
    
    @Override
    public void handleFallDamage(final float fallDistance, final float damageMultiplier) {
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.aP;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.aS;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.aQ;
    }
    
    @Override
    protected void playStepSound(final BlockPos pos, final BlockState state) {
        this.playSound(SoundEvents.aT, 0.15f, 1.0f);
    }
    
    @Override
    public ChickenEntity createChild(final PassiveEntity mate) {
        return EntityType.CHICKEN.create(this.world);
    }
    
    @Override
    public boolean isBreedingItem(final ItemStack stack) {
        return ChickenEntity.BREEDING_INGREDIENT.a(stack);
    }
    
    @Override
    protected int getCurrentExperience(final PlayerEntity playerEntity) {
        if (this.hasJockey()) {
            return 10;
        }
        return super.getCurrentExperience(playerEntity);
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.jockey = tag.getBoolean("IsChickenJockey");
        if (tag.containsKey("EggLayTime")) {
            this.eggLayTime = tag.getInt("EggLayTime");
        }
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putBoolean("IsChickenJockey", this.jockey);
        tag.putInt("EggLayTime", this.eggLayTime);
    }
    
    @Override
    public boolean canImmediatelyDespawn(final double distanceSquared) {
        return this.hasJockey() && !this.hasPassengers();
    }
    
    @Override
    public void updatePassengerPosition(final Entity passenger) {
        super.updatePassengerPosition(passenger);
        final float float2 = MathHelper.sin(this.aK * 0.017453292f);
        final float float3 = MathHelper.cos(this.aK * 0.017453292f);
        final float float4 = 0.1f;
        final float float5 = 0.0f;
        passenger.setPosition(this.x + 0.1f * float2, this.y + this.getHeight() * 0.5f + passenger.getHeightOffset() + 0.0, this.z - 0.1f * float3);
        if (passenger instanceof LivingEntity) {
            ((LivingEntity)passenger).aK = this.aK;
        }
    }
    
    public boolean hasJockey() {
        return this.jockey;
    }
    
    public void setHasJockey(final boolean hasJockey) {
        this.jockey = hasJockey;
    }
    
    static {
        BREEDING_INGREDIENT = Ingredient.ofItems(Items.jO, Items.ma, Items.lZ, Items.oP);
    }
}
