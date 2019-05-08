package net.minecraft.entity.passive;

import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.world.LocalDifficulty;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.minecraft.block.BlockState;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.IWorld;
import net.minecraft.entity.attribute.EntityAttributes;
import java.util.function.Predicate;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;

public class PolarBearEntity extends AnimalEntity
{
    private static final TrackedData<Boolean> WARNING;
    private float lastWarningAnimationProgress;
    private float warningAnimationProgress;
    private int warningSoundCooldown;
    
    public PolarBearEntity(final EntityType<? extends PolarBearEntity> type, final World world) {
        super(type, world);
    }
    
    @Override
    public PassiveEntity createChild(final PassiveEntity mate) {
        return EntityType.POLAR_BEAR.create(this.world);
    }
    
    @Override
    public boolean isBreedingItem(final ItemStack stack) {
        return false;
    }
    
    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new AttackGoal());
        this.goalSelector.add(1, new PolarBearEscapeDangerGoal());
        this.goalSelector.add(4, new FollowParentGoal(this, 1.25));
        this.goalSelector.add(5, new WanderAroundGoal(this, 1.0));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(7, new LookAroundGoal(this));
        this.targetSelector.add(1, new PolarBearRevengeGoal());
        this.targetSelector.add(2, new FollowPlayersGoal());
        this.targetSelector.add(3, new FollowTargetGoal<>(this, FoxEntity.class, 10, true, true, null));
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(30.0);
        this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(20.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
        this.getAttributeContainer().register(EntityAttributes.ATTACK_DAMAGE);
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(6.0);
    }
    
    @Override
    public boolean canSpawn(final IWorld iWorld, final SpawnType spawnType) {
        final int integer3 = MathHelper.floor(this.x);
        final int integer4 = MathHelper.floor(this.getBoundingBox().minY);
        final int integer5 = MathHelper.floor(this.z);
        final BlockPos blockPos6 = new BlockPos(integer3, integer4, integer5);
        final Biome biome7 = iWorld.getBiome(blockPos6);
        if (biome7 == Biomes.l || biome7 == Biomes.Z) {
            return iWorld.getLightLevel(blockPos6, 0) > 8 && iWorld.getBlockState(blockPos6.down()).getBlock() == Blocks.cB;
        }
        return super.canSpawn(iWorld, spawnType);
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isChild()) {
            return SoundEvents.iS;
        }
        return SoundEvents.iR;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.iU;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.iT;
    }
    
    @Override
    protected void playStepSound(final BlockPos pos, final BlockState state) {
        this.playSound(SoundEvents.iV, 0.15f, 1.0f);
    }
    
    protected void playWarningSound() {
        if (this.warningSoundCooldown <= 0) {
            this.playSound(SoundEvents.iW, 1.0f, this.getSoundPitch());
            this.warningSoundCooldown = 40;
        }
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Boolean>startTracking(PolarBearEntity.WARNING, false);
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.world.isClient) {
            if (this.warningAnimationProgress != this.lastWarningAnimationProgress) {
                this.refreshSize();
            }
            this.lastWarningAnimationProgress = this.warningAnimationProgress;
            if (this.isWarning()) {
                this.warningAnimationProgress = MathHelper.clamp(this.warningAnimationProgress + 1.0f, 0.0f, 6.0f);
            }
            else {
                this.warningAnimationProgress = MathHelper.clamp(this.warningAnimationProgress - 1.0f, 0.0f, 6.0f);
            }
        }
        if (this.warningSoundCooldown > 0) {
            --this.warningSoundCooldown;
        }
    }
    
    @Override
    public EntitySize getSize(final EntityPose entityPose) {
        if (this.warningAnimationProgress > 0.0f) {
            final float float2 = this.warningAnimationProgress / 6.0f;
            final float float3 = 1.0f + float2;
            return super.getSize(entityPose).scaled(1.0f, float3);
        }
        return super.getSize(entityPose);
    }
    
    @Override
    public boolean tryAttack(final Entity entity) {
        final boolean boolean2 = entity.damage(DamageSource.mob(this), (float)(int)this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getValue());
        if (boolean2) {
            this.dealDamage(this, entity);
        }
        return boolean2;
    }
    
    public boolean isWarning() {
        return this.dataTracker.<Boolean>get(PolarBearEntity.WARNING);
    }
    
    public void setWarning(final boolean warning) {
        this.dataTracker.<Boolean>set(PolarBearEntity.WARNING, warning);
    }
    
    @Environment(EnvType.CLIENT)
    public float getWarningAnimationProgress(final float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastWarningAnimationProgress, this.warningAnimationProgress) / 6.0f;
    }
    
    @Override
    protected float getBaseMovementSpeedMultiplier() {
        return 0.98f;
    }
    
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable EntityData entityData, @Nullable final CompoundTag compoundTag) {
        if (entityData instanceof PolarBearEntityData) {
            this.setBreedingAge(-24000);
        }
        else {
            entityData = new PolarBearEntityData();
        }
        return entityData;
    }
    
    static {
        WARNING = DataTracker.<Boolean>registerData(PolarBearEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }
    
    static class PolarBearEntityData implements EntityData
    {
        private PolarBearEntityData() {
        }
    }
    
    class PolarBearRevengeGoal extends RevengeGoal
    {
        public PolarBearRevengeGoal() {
            super(PolarBearEntity.this, new Class[0]);
        }
        
        @Override
        public void start() {
            super.start();
            if (PolarBearEntity.this.isChild()) {
                this.callSameTypeForRevenge();
                this.stop();
            }
        }
        
        @Override
        protected void setMobEntityTarget(final MobEntity mobEntity, final LivingEntity livingEntity) {
            if (mobEntity instanceof PolarBearEntity && !mobEntity.isChild()) {
                super.setMobEntityTarget(mobEntity, livingEntity);
            }
        }
    }
    
    class FollowPlayersGoal extends FollowTargetGoal<PlayerEntity>
    {
        public FollowPlayersGoal() {
            super(PolarBearEntity.this, PlayerEntity.class, 20, true, true, null);
        }
        
        @Override
        public boolean canStart() {
            if (PolarBearEntity.this.isChild()) {
                return false;
            }
            if (super.canStart()) {
                final List<PolarBearEntity> list1 = PolarBearEntity.this.world.<PolarBearEntity>getEntities(PolarBearEntity.class, PolarBearEntity.this.getBoundingBox().expand(8.0, 4.0, 8.0));
                for (final PolarBearEntity polarBearEntity3 : list1) {
                    if (polarBearEntity3.isChild()) {
                        return true;
                    }
                }
            }
            return false;
        }
        
        @Override
        protected double getFollowRange() {
            return super.getFollowRange() * 0.5;
        }
    }
    
    class AttackGoal extends MeleeAttackGoal
    {
        public AttackGoal() {
            super(PolarBearEntity.this, 1.25, true);
        }
        
        @Override
        protected void attack(final LivingEntity target, final double squaredDistance) {
            final double double4 = this.getSquaredMaxAttackDistance(target);
            if (squaredDistance <= double4 && this.ticksUntilAttack <= 0) {
                this.ticksUntilAttack = 20;
                this.entity.tryAttack(target);
                PolarBearEntity.this.setWarning(false);
            }
            else if (squaredDistance <= double4 * 2.0) {
                if (this.ticksUntilAttack <= 0) {
                    PolarBearEntity.this.setWarning(false);
                    this.ticksUntilAttack = 20;
                }
                if (this.ticksUntilAttack <= 10) {
                    PolarBearEntity.this.setWarning(true);
                    PolarBearEntity.this.playWarningSound();
                }
            }
            else {
                this.ticksUntilAttack = 20;
                PolarBearEntity.this.setWarning(false);
            }
        }
        
        @Override
        public void stop() {
            PolarBearEntity.this.setWarning(false);
            super.stop();
        }
        
        @Override
        protected double getSquaredMaxAttackDistance(final LivingEntity entity) {
            return 4.0f + entity.getWidth();
        }
    }
    
    class PolarBearEscapeDangerGoal extends EscapeDangerGoal
    {
        public PolarBearEscapeDangerGoal() {
            super(PolarBearEntity.this, 2.0);
        }
        
        @Override
        public boolean canStart() {
            return (PolarBearEntity.this.isChild() || PolarBearEntity.this.isOnFire()) && super.canStart();
        }
    }
}
