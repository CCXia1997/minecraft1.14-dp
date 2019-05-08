package net.minecraft.entity.passive;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.ViewableWorld;
import net.minecraft.sound.SoundEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.BlockStateParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.TrackIronGolemTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.IronGolemLookGoal;
import net.minecraft.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.entity.ai.goal.WanderAroundPointOfInterestGoal;
import net.minecraft.entity.ai.goal.GoToEntityTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;

public class IronGolemEntity extends GolemEntity
{
    protected static final TrackedData<Byte> IRON_GOLEM_FLAGS;
    private int c;
    private int d;
    
    public IronGolemEntity(final EntityType<? extends IronGolemEntity> type, final World world) {
        super(type, world);
        this.stepHeight = 1.0f;
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(2, new GoToEntityTargetGoal(this, 0.9, 32.0f));
        this.goalSelector.add(2, new WanderAroundPointOfInterestGoal(this, 0.6));
        this.goalSelector.add(3, new MoveThroughVillageGoal(this, 0.6, false, 4, () -> false));
        this.goalSelector.add(5, new IronGolemLookGoal(this));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.6));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackIronGolemTargetGoal(this));
        this.targetSelector.add(2, new RevengeGoal(this, new Class[0]));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, MobEntity.class, 5, false, false, livingEntity -> livingEntity instanceof Monster && !(livingEntity instanceof CreeperEntity)));
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Byte>startTracking(IronGolemEntity.IRON_GOLEM_FLAGS, (Byte)0);
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(100.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
        this.getAttributeInstance(EntityAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
    }
    
    @Override
    protected int getNextBreathInWater(final int breath) {
        return breath;
    }
    
    @Override
    protected void pushAway(final Entity entity) {
        if (entity instanceof Monster && !(entity instanceof CreeperEntity) && this.getRand().nextInt(20) == 0) {
            this.setTarget((LivingEntity)entity);
        }
        super.pushAway(entity);
    }
    
    @Override
    public void updateState() {
        super.updateState();
        if (this.c > 0) {
            --this.c;
        }
        if (this.d > 0) {
            --this.d;
        }
        if (Entity.squaredHorizontalLength(this.getVelocity()) > 2.500000277905201E-7 && this.random.nextInt(5) == 0) {
            final int integer1 = MathHelper.floor(this.x);
            final int integer2 = MathHelper.floor(this.y - 0.20000000298023224);
            final int integer3 = MathHelper.floor(this.z);
            final BlockState blockState4 = this.world.getBlockState(new BlockPos(integer1, integer2, integer3));
            if (!blockState4.isAir()) {
                this.world.addParticle(new BlockStateParticleParameters(ParticleTypes.d, blockState4), this.x + (this.random.nextFloat() - 0.5) * this.getWidth(), this.getBoundingBox().minY + 0.1, this.z + (this.random.nextFloat() - 0.5) * this.getWidth(), 4.0 * (this.random.nextFloat() - 0.5), 0.5, (this.random.nextFloat() - 0.5) * 4.0);
            }
        }
    }
    
    @Override
    public boolean canTarget(final EntityType<?> entityType) {
        return (!this.isPlayerCreated() || entityType != EntityType.PLAYER) && entityType != EntityType.CREEPER && super.canTarget(entityType);
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putBoolean("PlayerCreated", this.isPlayerCreated());
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setPlayerCreated(tag.getBoolean("PlayerCreated"));
    }
    
    @Override
    public boolean tryAttack(final Entity entity) {
        this.c = 10;
        this.world.sendEntityStatus(this, (byte)4);
        final boolean boolean2 = entity.damage(DamageSource.mob(this), (float)(7 + this.random.nextInt(15)));
        if (boolean2) {
            entity.setVelocity(entity.getVelocity().add(0.0, 0.4000000059604645, 0.0));
            this.dealDamage(this, entity);
        }
        this.playSound(SoundEvents.ft, 1.0f, 1.0f);
        return boolean2;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void handleStatus(final byte status) {
        if (status == 4) {
            this.c = 10;
            this.playSound(SoundEvents.ft, 1.0f, 1.0f);
        }
        else if (status == 11) {
            this.d = 400;
        }
        else if (status == 34) {
            this.d = 0;
        }
        else {
            super.handleStatus(status);
        }
    }
    
    @Environment(EnvType.CLIENT)
    public int l() {
        return this.c;
    }
    
    public void r(final boolean boolean1) {
        if (boolean1) {
            this.d = 400;
            this.world.sendEntityStatus(this, (byte)11);
        }
        else {
            this.d = 0;
            this.world.sendEntityStatus(this, (byte)34);
        }
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.fv;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.fu;
    }
    
    @Override
    protected void playStepSound(final BlockPos pos, final BlockState state) {
        this.playSound(SoundEvents.fw, 1.0f, 1.0f);
    }
    
    @Environment(EnvType.CLIENT)
    public int dV() {
        return this.d;
    }
    
    public boolean isPlayerCreated() {
        return (this.dataTracker.<Byte>get(IronGolemEntity.IRON_GOLEM_FLAGS) & 0x1) != 0x0;
    }
    
    public void setPlayerCreated(final boolean boolean1) {
        final byte byte2 = this.dataTracker.<Byte>get(IronGolemEntity.IRON_GOLEM_FLAGS);
        if (boolean1) {
            this.dataTracker.<Byte>set(IronGolemEntity.IRON_GOLEM_FLAGS, (byte)(byte2 | 0x1));
        }
        else {
            this.dataTracker.<Byte>set(IronGolemEntity.IRON_GOLEM_FLAGS, (byte)(byte2 & 0xFFFFFFFE));
        }
    }
    
    @Override
    public void onDeath(final DamageSource damageSource) {
        super.onDeath(damageSource);
    }
    
    @Override
    public boolean canSpawn(final ViewableWorld world) {
        final BlockPos blockPos2 = new BlockPos(this);
        final BlockPos blockPos3 = blockPos2.down();
        final BlockState blockState4 = world.getBlockState(blockPos3);
        if (blockState4.hasSolidTopSurface(world, blockPos3, this)) {
            final BlockPos blockPos4 = blockPos2.up();
            final BlockState blockState5 = world.getBlockState(blockPos4);
            return SpawnHelper.isClearForSpawn(world, blockPos4, blockState5, blockState5.getFluidState()) && SpawnHelper.isClearForSpawn(world, blockPos2, world.getBlockState(blockPos2), Fluids.EMPTY.getDefaultState()) && world.intersectsEntities(this);
        }
        return false;
    }
    
    static {
        IRON_GOLEM_FLAGS = DataTracker.<Byte>registerData(IronGolemEntity.class, TrackedDataHandlerRegistry.BYTE);
    }
}
