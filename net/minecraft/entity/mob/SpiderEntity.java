package net.minecraft.entity.mob;

import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import java.util.Random;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.world.Difficulty;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.IWorld;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.EntityGroup;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.ai.pathing.SpiderNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;

public class SpiderEntity extends HostileEntity
{
    private static final TrackedData<Byte> SPIDER_FLAGS;
    
    public SpiderEntity(final EntityType<? extends SpiderEntity> type, final World world) {
        super(type, world);
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(3, new PounceAtTargetGoal(this, 0.4f));
        this.goalSelector.add(4, new a(this));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[0]));
        this.targetSelector.add(2, new c<>(this, PlayerEntity.class));
        this.targetSelector.add(3, new c<>(this, IronGolemEntity.class));
    }
    
    @Override
    public double getMountedHeightOffset() {
        return this.getHeight() * 0.5f;
    }
    
    @Override
    protected EntityNavigation createNavigation(final World world) {
        return new SpiderNavigation(this, world);
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Byte>startTracking(SpiderEntity.SPIDER_FLAGS, (Byte)0);
    }
    
    @Override
    public void tick() {
        super.tick();
        if (!this.world.isClient) {
            this.setCanClimb(this.horizontalCollision);
        }
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(16.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896);
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.lc;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.le;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ld;
    }
    
    @Override
    protected void playStepSound(final BlockPos pos, final BlockState state) {
        this.playSound(SoundEvents.lf, 0.15f, 1.0f);
    }
    
    @Override
    public boolean isClimbing() {
        return this.getCanClimb();
    }
    
    @Override
    public void slowMovement(final BlockState state, final Vec3d multipliers) {
        if (state.getBlock() != Blocks.aP) {
            super.slowMovement(state, multipliers);
        }
    }
    
    @Override
    public EntityGroup getGroup() {
        return EntityGroup.ARTHROPOD;
    }
    
    @Override
    public boolean isPotionEffective(final StatusEffectInstance statusEffectInstance) {
        return statusEffectInstance.getEffectType() != StatusEffects.s && super.isPotionEffective(statusEffectInstance);
    }
    
    public boolean getCanClimb() {
        return (this.dataTracker.<Byte>get(SpiderEntity.SPIDER_FLAGS) & 0x1) != 0x0;
    }
    
    public void setCanClimb(final boolean boolean1) {
        byte byte2 = this.dataTracker.<Byte>get(SpiderEntity.SPIDER_FLAGS);
        if (boolean1) {
            byte2 |= 0x1;
        }
        else {
            byte2 &= 0xFFFFFFFE;
        }
        this.dataTracker.<Byte>set(SpiderEntity.SPIDER_FLAGS, byte2);
    }
    
    @Nullable
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable EntityData entityData, @Nullable final CompoundTag compoundTag) {
        entityData = super.initialize(iWorld, localDifficulty, difficulty, entityData, compoundTag);
        if (iWorld.getRandom().nextInt(100) == 0) {
            final SkeletonEntity skeletonEntity6 = EntityType.SKELETON.create(this.world);
            skeletonEntity6.setPositionAndAngles(this.x, this.y, this.z, this.yaw, 0.0f);
            skeletonEntity6.initialize(iWorld, localDifficulty, difficulty, null, null);
            iWorld.spawnEntity(skeletonEntity6);
            skeletonEntity6.startRiding(this);
        }
        if (entityData == null) {
            entityData = new b();
            if (iWorld.getDifficulty() == Difficulty.HARD && iWorld.getRandom().nextFloat() < 0.1f * localDifficulty.getClampedLocalDifficulty()) {
                ((b)entityData).a(iWorld.getRandom());
            }
        }
        if (entityData instanceof b) {
            final StatusEffect statusEffect6 = ((b)entityData).a;
            if (statusEffect6 != null) {
                this.addPotionEffect(new StatusEffectInstance(statusEffect6, Integer.MAX_VALUE));
            }
        }
        return entityData;
    }
    
    @Override
    protected float getActiveEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        return 0.65f;
    }
    
    static {
        SPIDER_FLAGS = DataTracker.<Byte>registerData(SpiderEntity.class, TrackedDataHandlerRegistry.BYTE);
    }
    
    public static class b implements EntityData
    {
        public StatusEffect a;
        
        public void a(final Random random) {
            final int integer2 = random.nextInt(5);
            if (integer2 <= 1) {
                this.a = StatusEffects.a;
            }
            else if (integer2 <= 2) {
                this.a = StatusEffects.e;
            }
            else if (integer2 <= 3) {
                this.a = StatusEffects.j;
            }
            else if (integer2 <= 4) {
                this.a = StatusEffects.n;
            }
        }
    }
    
    static class a extends MeleeAttackGoal
    {
        public a(final SpiderEntity spiderEntity) {
            super(spiderEntity, 1.0, true);
        }
        
        @Override
        public boolean canStart() {
            return super.canStart() && !this.entity.hasPassengers();
        }
        
        @Override
        public boolean shouldContinue() {
            final float float1 = this.entity.getBrightnessAtEyes();
            if (float1 >= 0.5f && this.entity.getRand().nextInt(100) == 0) {
                this.entity.setTarget(null);
                return false;
            }
            return super.shouldContinue();
        }
        
        @Override
        protected double getSquaredMaxAttackDistance(final LivingEntity entity) {
            return 4.0f + entity.getWidth();
        }
    }
    
    static class c<T extends LivingEntity> extends FollowTargetGoal<T>
    {
        public c(final SpiderEntity spiderEntity, final Class<T> class2) {
            super(spiderEntity, class2, true);
        }
        
        @Override
        public boolean canStart() {
            final float float1 = this.entity.getBrightnessAtEyes();
            return float1 < 0.5f && super.canStart();
        }
    }
}
