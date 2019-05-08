package net.minecraft.entity.mob;

import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.world.Difficulty;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.IWorld;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.tag.FluidTags;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import java.util.function.Predicate;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import java.util.EnumSet;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoToWalkTargetGoal;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.TrackedData;

public class GuardianEntity extends HostileEntity
{
    private static final TrackedData<Boolean> SPIKES_RETRACTED;
    private static final TrackedData<Integer> BEAM_TARGET_ID;
    protected float spikesExtension;
    protected float prevSpikesExtension;
    protected float spikesExtensionRate;
    protected float tailAngle;
    protected float prevTailAngle;
    private LivingEntity cachedBeamTarget;
    private int beamTicks;
    private boolean flopping;
    protected WanderAroundGoal wanderGoal;
    
    public GuardianEntity(final EntityType<? extends GuardianEntity> type, final World world) {
        super(type, world);
        this.experiencePoints = 10;
        this.moveControl = new GuardianMoveControl(this);
        this.spikesExtension = this.random.nextFloat();
        this.prevSpikesExtension = this.spikesExtension;
    }
    
    @Override
    protected void initGoals() {
        final GoToWalkTargetGoal goToWalkTargetGoal1 = new GoToWalkTargetGoal(this, 1.0);
        this.wanderGoal = new WanderAroundGoal(this, 1.0, 80);
        this.goalSelector.add(4, new FireBeamGoal(this));
        this.goalSelector.add(5, goToWalkTargetGoal1);
        this.goalSelector.add(7, this.wanderGoal);
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(8, new LookAtEntityGoal(this, GuardianEntity.class, 12.0f, 0.01f));
        this.goalSelector.add(9, new LookAroundGoal(this));
        this.wanderGoal.setControls(EnumSet.<Goal.Control>of(Goal.Control.a, Goal.Control.b));
        goToWalkTargetGoal1.setControls(EnumSet.<Goal.Control>of(Goal.Control.a, Goal.Control.b));
        this.targetSelector.add(1, new FollowTargetGoal<>(this, LivingEntity.class, 10, true, false, new GuardianTargetPredicate(this)));
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(6.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
        this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(16.0);
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(30.0);
    }
    
    @Override
    protected EntityNavigation createNavigation(final World world) {
        return new SwimNavigation(this, world);
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Boolean>startTracking(GuardianEntity.SPIKES_RETRACTED, false);
        this.dataTracker.<Integer>startTracking(GuardianEntity.BEAM_TARGET_ID, 0);
    }
    
    @Override
    public boolean canBreatheInWater() {
        return true;
    }
    
    @Override
    public EntityGroup getGroup() {
        return EntityGroup.AQUATIC;
    }
    
    public boolean areSpikesRetracted() {
        return this.dataTracker.<Boolean>get(GuardianEntity.SPIKES_RETRACTED);
    }
    
    private void setSpikesRetracted(final boolean retracted) {
        this.dataTracker.<Boolean>set(GuardianEntity.SPIKES_RETRACTED, retracted);
    }
    
    public int getWarmupTime() {
        return 80;
    }
    
    private void setBeamTarget(final int progress) {
        this.dataTracker.<Integer>set(GuardianEntity.BEAM_TARGET_ID, progress);
    }
    
    public boolean hasBeamTarget() {
        return this.dataTracker.<Integer>get(GuardianEntity.BEAM_TARGET_ID) != 0;
    }
    
    @Nullable
    public LivingEntity getBeamTarget() {
        if (!this.hasBeamTarget()) {
            return null;
        }
        if (!this.world.isClient) {
            return this.getTarget();
        }
        if (this.cachedBeamTarget != null) {
            return this.cachedBeamTarget;
        }
        final Entity entity1 = this.world.getEntityById(this.dataTracker.<Integer>get(GuardianEntity.BEAM_TARGET_ID));
        if (entity1 instanceof LivingEntity) {
            return this.cachedBeamTarget = (LivingEntity)entity1;
        }
        return null;
    }
    
    @Override
    public void onTrackedDataSet(final TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (GuardianEntity.BEAM_TARGET_ID.equals(data)) {
            this.beamTicks = 0;
            this.cachedBeamTarget = null;
        }
    }
    
    @Override
    public int getMinAmbientSoundDelay() {
        return 160;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return this.isInsideWaterOrBubbleColumn() ? SoundEvents.ev : SoundEvents.ew;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return this.isInsideWaterOrBubbleColumn() ? SoundEvents.eB : SoundEvents.eC;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return this.isInsideWaterOrBubbleColumn() ? SoundEvents.ey : SoundEvents.ez;
    }
    
    @Override
    protected boolean canClimb() {
        return false;
    }
    
    @Override
    protected float getActiveEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        return entitySize.height * 0.5f;
    }
    
    @Override
    public float getPathfindingFavor(final BlockPos pos, final ViewableWorld world) {
        if (world.getFluidState(pos).matches(FluidTags.a)) {
            return 10.0f + world.getBrightness(pos) - 0.5f;
        }
        return super.getPathfindingFavor(pos, world);
    }
    
    @Override
    public void updateState() {
        if (this.isAlive()) {
            if (this.world.isClient) {
                this.prevSpikesExtension = this.spikesExtension;
                if (!this.isInsideWater()) {
                    this.spikesExtensionRate = 2.0f;
                    final Vec3d vec3d1 = this.getVelocity();
                    if (vec3d1.y > 0.0 && this.flopping && !this.isSilent()) {
                        this.world.playSound(this.x, this.y, this.z, this.getFlopSound(), this.getSoundCategory(), 1.0f, 1.0f, false);
                    }
                    this.flopping = (vec3d1.y < 0.0 && this.world.doesBlockHaveSolidTopSurface(new BlockPos(this).down(), this));
                }
                else if (this.areSpikesRetracted()) {
                    if (this.spikesExtensionRate < 0.5f) {
                        this.spikesExtensionRate = 4.0f;
                    }
                    else {
                        this.spikesExtensionRate += (0.5f - this.spikesExtensionRate) * 0.1f;
                    }
                }
                else {
                    this.spikesExtensionRate += (0.125f - this.spikesExtensionRate) * 0.2f;
                }
                this.spikesExtension += this.spikesExtensionRate;
                this.prevTailAngle = this.tailAngle;
                if (!this.isInsideWaterOrBubbleColumn()) {
                    this.tailAngle = this.random.nextFloat();
                }
                else if (this.areSpikesRetracted()) {
                    this.tailAngle += (0.0f - this.tailAngle) * 0.25f;
                }
                else {
                    this.tailAngle += (1.0f - this.tailAngle) * 0.06f;
                }
                if (this.areSpikesRetracted() && this.isInsideWater()) {
                    final Vec3d vec3d1 = this.getRotationVec(0.0f);
                    for (int integer2 = 0; integer2 < 2; ++integer2) {
                        this.world.addParticle(ParticleTypes.e, this.x + (this.random.nextDouble() - 0.5) * this.getWidth() - vec3d1.x * 1.5, this.y + this.random.nextDouble() * this.getHeight() - vec3d1.y * 1.5, this.z + (this.random.nextDouble() - 0.5) * this.getWidth() - vec3d1.z * 1.5, 0.0, 0.0, 0.0);
                    }
                }
                if (this.hasBeamTarget()) {
                    if (this.beamTicks < this.getWarmupTime()) {
                        ++this.beamTicks;
                    }
                    final LivingEntity livingEntity1 = this.getBeamTarget();
                    if (livingEntity1 != null) {
                        this.getLookControl().lookAt(livingEntity1, 90.0f, 90.0f);
                        this.getLookControl().tick();
                        final double double2 = this.getBeamProgress(0.0f);
                        double double3 = livingEntity1.x - this.x;
                        double double4 = livingEntity1.y + livingEntity1.getHeight() * 0.5f - (this.y + this.getStandingEyeHeight());
                        double double5 = livingEntity1.z - this.z;
                        final double double6 = Math.sqrt(double3 * double3 + double4 * double4 + double5 * double5);
                        double3 /= double6;
                        double4 /= double6;
                        double5 /= double6;
                        double double7 = this.random.nextDouble();
                        while (double7 < double6) {
                            double7 += 1.8 - double2 + this.random.nextDouble() * (1.7 - double2);
                            this.world.addParticle(ParticleTypes.e, this.x + double3 * double7, this.y + double4 * double7 + this.getStandingEyeHeight(), this.z + double5 * double7, 0.0, 0.0, 0.0);
                        }
                    }
                }
            }
            if (this.isInsideWaterOrBubbleColumn()) {
                this.setBreath(300);
            }
            else if (this.onGround) {
                this.setVelocity(this.getVelocity().add((this.random.nextFloat() * 2.0f - 1.0f) * 0.4f, 0.5, (this.random.nextFloat() * 2.0f - 1.0f) * 0.4f));
                this.yaw = this.random.nextFloat() * 360.0f;
                this.onGround = false;
                this.velocityDirty = true;
            }
            if (this.hasBeamTarget()) {
                this.yaw = this.headYaw;
            }
        }
        super.updateState();
    }
    
    protected SoundEvent getFlopSound() {
        return SoundEvents.eA;
    }
    
    @Environment(EnvType.CLIENT)
    public float getSpikesExtension(final float tickDelta) {
        return MathHelper.lerp(tickDelta, this.prevSpikesExtension, this.spikesExtension);
    }
    
    @Environment(EnvType.CLIENT)
    public float getTailAngle(final float tickDelta) {
        return MathHelper.lerp(tickDelta, this.prevTailAngle, this.tailAngle);
    }
    
    public float getBeamProgress(final float tickDelta) {
        return (this.beamTicks + tickDelta) / this.getWarmupTime();
    }
    
    @Override
    protected boolean checkLightLevelForSpawn() {
        return true;
    }
    
    @Override
    public boolean canSpawn(final ViewableWorld world) {
        return world.intersectsEntities(this);
    }
    
    @Override
    public boolean canSpawn(final IWorld iWorld, final SpawnType spawnType) {
        return (this.random.nextInt(20) == 0 || !iWorld.v(new BlockPos(this))) && super.canSpawn(iWorld, spawnType);
    }
    
    @Override
    protected boolean a(final IWorld iWorld, final SpawnType spawnType, final BlockPos blockPos) {
        return iWorld.getFluidState(blockPos).matches(FluidTags.a);
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        if (!this.areSpikesRetracted() && !source.getMagic() && source.getSource() instanceof LivingEntity) {
            final LivingEntity livingEntity3 = (LivingEntity)source.getSource();
            if (!source.isExplosive()) {
                livingEntity3.damage(DamageSource.thorns(this), 2.0f);
            }
        }
        if (this.wanderGoal != null) {
            this.wanderGoal.ignoreChanceOnce();
        }
        return super.damage(source, amount);
    }
    
    @Override
    public int getLookPitchSpeed() {
        return 180;
    }
    
    @Override
    public void travel(final Vec3d movementInput) {
        if (this.canMoveVoluntarily() && this.isInsideWater()) {
            this.updateVelocity(0.1f, movementInput);
            this.move(MovementType.a, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.9));
            if (!this.areSpikesRetracted() && this.getTarget() == null) {
                this.setVelocity(this.getVelocity().add(0.0, -0.005, 0.0));
            }
        }
        else {
            super.travel(movementInput);
        }
    }
    
    static {
        SPIKES_RETRACTED = DataTracker.<Boolean>registerData(GuardianEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        BEAM_TARGET_ID = DataTracker.<Integer>registerData(GuardianEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }
    
    static class GuardianTargetPredicate implements Predicate<LivingEntity>
    {
        private final GuardianEntity owner;
        
        public GuardianTargetPredicate(final GuardianEntity owner) {
            this.owner = owner;
        }
        
        public boolean a(@Nullable final LivingEntity context) {
            return (context instanceof PlayerEntity || context instanceof SquidEntity) && context.squaredDistanceTo(this.owner) > 9.0;
        }
    }
    
    static class FireBeamGoal extends Goal
    {
        private final GuardianEntity owner;
        private int beamTicks;
        private final boolean elderOwner;
        
        public FireBeamGoal(final GuardianEntity owner) {
            this.owner = owner;
            this.elderOwner = (owner instanceof ElderGuardianEntity);
            this.setControls(EnumSet.<Control>of(Control.a, Control.b));
        }
        
        @Override
        public boolean canStart() {
            final LivingEntity livingEntity1 = this.owner.getTarget();
            return livingEntity1 != null && livingEntity1.isAlive();
        }
        
        @Override
        public boolean shouldContinue() {
            return super.shouldContinue() && (this.elderOwner || this.owner.squaredDistanceTo(this.owner.getTarget()) > 9.0);
        }
        
        @Override
        public void start() {
            this.beamTicks = -10;
            this.owner.getNavigation().stop();
            this.owner.getLookControl().lookAt(this.owner.getTarget(), 90.0f, 90.0f);
            this.owner.velocityDirty = true;
        }
        
        @Override
        public void stop() {
            this.owner.setBeamTarget(0);
            this.owner.setTarget(null);
            this.owner.wanderGoal.ignoreChanceOnce();
        }
        
        @Override
        public void tick() {
            final LivingEntity livingEntity1 = this.owner.getTarget();
            this.owner.getNavigation().stop();
            this.owner.getLookControl().lookAt(livingEntity1, 90.0f, 90.0f);
            if (!this.owner.canSee(livingEntity1)) {
                this.owner.setTarget(null);
                return;
            }
            ++this.beamTicks;
            if (this.beamTicks == 0) {
                this.owner.setBeamTarget(this.owner.getTarget().getEntityId());
                this.owner.world.sendEntityStatus(this.owner, (byte)21);
            }
            else if (this.beamTicks >= this.owner.getWarmupTime()) {
                float float2 = 1.0f;
                if (this.owner.world.getDifficulty() == Difficulty.HARD) {
                    float2 += 2.0f;
                }
                if (this.elderOwner) {
                    float2 += 2.0f;
                }
                livingEntity1.damage(DamageSource.magic(this.owner, this.owner), float2);
                livingEntity1.damage(DamageSource.mob(this.owner), (float)this.owner.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getValue());
                this.owner.setTarget(null);
            }
            super.tick();
        }
    }
    
    static class GuardianMoveControl extends MoveControl
    {
        private final GuardianEntity guardian;
        
        public GuardianMoveControl(final GuardianEntity guardianEntity) {
            super(guardianEntity);
            this.guardian = guardianEntity;
        }
        
        @Override
        public void tick() {
            if (this.state != State.b || this.guardian.getNavigation().isIdle()) {
                this.guardian.setMovementSpeed(0.0f);
                this.guardian.setSpikesRetracted(false);
                return;
            }
            final Vec3d vec3d1 = new Vec3d(this.targetX - this.guardian.x, this.targetY - this.guardian.y, this.targetZ - this.guardian.z);
            final double double2 = vec3d1.length();
            final double double3 = vec3d1.x / double2;
            final double double4 = vec3d1.y / double2;
            final double double5 = vec3d1.z / double2;
            final float float10 = (float)(MathHelper.atan2(vec3d1.z, vec3d1.x) * 57.2957763671875) - 90.0f;
            this.guardian.yaw = this.changeAngle(this.guardian.yaw, float10, 90.0f);
            this.guardian.aK = this.guardian.yaw;
            final float float11 = (float)(this.speed * this.guardian.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue());
            final float float12 = MathHelper.lerp(0.125f, this.guardian.getMovementSpeed(), float11);
            this.guardian.setMovementSpeed(float12);
            final double double6 = Math.sin((this.guardian.age + this.guardian.getEntityId()) * 0.5) * 0.05;
            final double double7 = Math.cos(this.guardian.yaw * 0.017453292f);
            final double double8 = Math.sin(this.guardian.yaw * 0.017453292f);
            final double double9 = Math.sin((this.guardian.age + this.guardian.getEntityId()) * 0.75) * 0.05;
            this.guardian.setVelocity(this.guardian.getVelocity().add(double6 * double7, double9 * (double8 + double7) * 0.25 + float12 * double4 * 0.1, double6 * double8));
            final LookControl lookControl21 = this.guardian.getLookControl();
            final double double10 = this.guardian.x + double3 * 2.0;
            final double double11 = this.guardian.getStandingEyeHeight() + this.guardian.y + double4 / double2;
            final double double12 = this.guardian.z + double5 * 2.0;
            double double13 = lookControl21.getLookX();
            double double14 = lookControl21.getLookY();
            double double15 = lookControl21.getLookZ();
            if (!lookControl21.isActive()) {
                double13 = double10;
                double14 = double11;
                double15 = double12;
            }
            this.guardian.getLookControl().lookAt(MathHelper.lerp(0.125, double13, double10), MathHelper.lerp(0.125, double14, double11), MathHelper.lerp(0.125, double15, double12), 10.0f, 40.0f);
            this.guardian.setSpikesRetracted(true);
        }
    }
}
