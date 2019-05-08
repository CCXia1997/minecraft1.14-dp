package net.minecraft.entity.mob;

import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.IWorld;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.TargetPredicate;

public class EndermiteEntity extends HostileEntity
{
    private static final TargetPredicate CLOSE_PLAYER_PREDICATE;
    private int lifeTime;
    private boolean playerSpawned;
    
    public EndermiteEntity(final EntityType<? extends EndermiteEntity> type, final World world) {
        super(type, world);
        this.experiencePoints = 3;
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[0]).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new FollowTargetGoal<>(this, PlayerEntity.class, true));
    }
    
    @Override
    protected float getActiveEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        return 0.1f;
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(8.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(2.0);
    }
    
    @Override
    protected boolean canClimb() {
        return false;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.cL;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.cN;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.cM;
    }
    
    @Override
    protected void playStepSound(final BlockPos pos, final BlockState state) {
        this.playSound(SoundEvents.cO, 0.15f, 1.0f);
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.lifeTime = tag.getInt("Lifetime");
        this.playerSpawned = tag.getBoolean("PlayerSpawned");
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("Lifetime", this.lifeTime);
        tag.putBoolean("PlayerSpawned", this.playerSpawned);
    }
    
    @Override
    public void tick() {
        this.aK = this.yaw;
        super.tick();
    }
    
    @Override
    public void setYaw(final float float1) {
        super.setYaw(this.yaw = float1);
    }
    
    @Override
    public double getHeightOffset() {
        return 0.1;
    }
    
    public boolean isPlayerSpawned() {
        return this.playerSpawned;
    }
    
    public void setPlayerSpawned(final boolean playerSpawned) {
        this.playerSpawned = playerSpawned;
    }
    
    @Override
    public void updateState() {
        super.updateState();
        if (this.world.isClient) {
            for (int integer1 = 0; integer1 < 2; ++integer1) {
                this.world.addParticle(ParticleTypes.O, this.x + (this.random.nextDouble() - 0.5) * this.getWidth(), this.y + this.random.nextDouble() * this.getHeight(), this.z + (this.random.nextDouble() - 0.5) * this.getWidth(), (this.random.nextDouble() - 0.5) * 2.0, -this.random.nextDouble(), (this.random.nextDouble() - 0.5) * 2.0);
            }
        }
        else {
            if (!this.isPersistent()) {
                ++this.lifeTime;
            }
            if (this.lifeTime >= 2400) {
                this.remove();
            }
        }
    }
    
    @Override
    protected boolean checkLightLevelForSpawn() {
        return true;
    }
    
    @Override
    public boolean canSpawn(final IWorld iWorld, final SpawnType spawnType) {
        if (super.canSpawn(iWorld, spawnType)) {
            final PlayerEntity playerEntity3 = this.world.getClosestPlayer(EndermiteEntity.CLOSE_PLAYER_PREDICATE, this);
            return playerEntity3 == null;
        }
        return false;
    }
    
    @Override
    public EntityGroup getGroup() {
        return EntityGroup.ARTHROPOD;
    }
    
    static {
        CLOSE_PLAYER_PREDICATE = new TargetPredicate().setBaseMaxDistance(5.0).ignoreDistanceScalingFactor();
    }
}
