package net.minecraft.entity.mob;

import net.minecraft.entity.effect.StatusEffects;
import java.util.EnumSet;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.IWorld;
import net.minecraft.world.loot.LootTables;
import net.minecraft.util.Identifier;
import net.minecraft.sound.SoundEvent;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;

public class SlimeEntity extends MobEntity implements Monster
{
    private static final TrackedData<Integer> SLIME_SIZE;
    public float targetStretch;
    public float stretch;
    public float lastStretch;
    private boolean onGroundLastTick;
    
    public SlimeEntity(final EntityType<? extends SlimeEntity> type, final World world) {
        super(type, world);
        this.moveControl = new SlimeMoveControl(this);
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new b(this));
        this.goalSelector.add(2, new a(this));
        this.goalSelector.add(3, new e(this));
        this.goalSelector.add(5, new c(this));
        this.targetSelector.add(1, new FollowTargetGoal<>(this, PlayerEntity.class, 10, true, false, livingEntity -> Math.abs(livingEntity.y - this.y) <= 4.0));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, IronGolemEntity.class, true));
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Integer>startTracking(SlimeEntity.SLIME_SIZE, 1);
    }
    
    protected void setSize(final int size, final boolean heal) {
        this.dataTracker.<Integer>set(SlimeEntity.SLIME_SIZE, size);
        this.setPosition(this.x, this.y, this.z);
        this.refreshSize();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(size * size);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.2f + 0.1f * size);
        if (heal) {
            this.setHealth(this.getHealthMaximum());
        }
        this.experiencePoints = size;
    }
    
    public int getSize() {
        return this.dataTracker.<Integer>get(SlimeEntity.SLIME_SIZE);
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("Size", this.getSize() - 1);
        tag.putBoolean("wasOnGround", this.onGroundLastTick);
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        int integer2 = tag.getInt("Size");
        if (integer2 < 0) {
            integer2 = 0;
        }
        this.setSize(integer2 + 1, false);
        this.onGroundLastTick = tag.getBoolean("wasOnGround");
    }
    
    public boolean isSmall() {
        return this.getSize() <= 1;
    }
    
    protected ParticleParameters getParticles() {
        return ParticleTypes.H;
    }
    
    @Override
    public void tick() {
        if (!this.world.isClient && this.world.getDifficulty() == Difficulty.PEACEFUL && this.getSize() > 0) {
            this.removed = true;
        }
        this.stretch += (this.targetStretch - this.stretch) * 0.5f;
        this.lastStretch = this.stretch;
        super.tick();
        if (this.onGround && !this.onGroundLastTick) {
            for (int integer1 = this.getSize(), integer2 = 0; integer2 < integer1 * 8; ++integer2) {
                final float float3 = this.random.nextFloat() * 6.2831855f;
                final float float4 = this.random.nextFloat() * 0.5f + 0.5f;
                final float float5 = MathHelper.sin(float3) * integer1 * 0.5f * float4;
                final float float6 = MathHelper.cos(float3) * integer1 * 0.5f * float4;
                this.world.addParticle(this.getParticles(), this.x + float5, this.getBoundingBox().minY, this.z + float6, 0.0, 0.0, 0.0);
            }
            this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f) / 0.8f);
            this.targetStretch = -0.5f;
        }
        else if (!this.onGround && this.onGroundLastTick) {
            this.targetStretch = 1.0f;
        }
        this.onGroundLastTick = this.onGround;
        this.updateStretch();
    }
    
    protected void updateStretch() {
        this.targetStretch *= 0.6f;
    }
    
    protected int getTicksUntilNextJump() {
        return this.random.nextInt(20) + 10;
    }
    
    @Override
    public void onTrackedDataSet(final TrackedData<?> data) {
        if (SlimeEntity.SLIME_SIZE.equals(data)) {
            this.refreshSize();
            this.yaw = this.headYaw;
            this.aK = this.headYaw;
            if (this.isInsideWater() && this.random.nextInt(20) == 0) {
                this.onSwimmingStart();
            }
        }
        super.onTrackedDataSet(data);
    }
    
    @Override
    public EntityType<? extends SlimeEntity> getType() {
        return super.getType();
    }
    
    @Override
    public void remove() {
        final int integer1 = this.getSize();
        if (!this.world.isClient && integer1 > 1 && this.getHealth() <= 0.0f) {
            for (int integer2 = 2 + this.random.nextInt(3), integer3 = 0; integer3 < integer2; ++integer3) {
                final float float4 = (integer3 % 2 - 0.5f) * integer1 / 4.0f;
                final float float5 = (integer3 / 2 - 0.5f) * integer1 / 4.0f;
                final SlimeEntity slimeEntity6 = (SlimeEntity)this.getType().create(this.world);
                if (this.hasCustomName()) {
                    slimeEntity6.setCustomName(this.getCustomName());
                }
                if (this.isPersistent()) {
                    slimeEntity6.setPersistent();
                }
                slimeEntity6.setSize(integer1 / 2, true);
                slimeEntity6.setPositionAndAngles(this.x + float4, this.y + 0.5, this.z + float5, this.random.nextFloat() * 360.0f, 0.0f);
                this.world.spawnEntity(slimeEntity6);
            }
        }
        super.remove();
    }
    
    @Override
    public void pushAwayFrom(final Entity entity) {
        super.pushAwayFrom(entity);
        if (entity instanceof IronGolemEntity && this.isBig()) {
            this.damage((LivingEntity)entity);
        }
    }
    
    @Override
    public void onPlayerCollision(final PlayerEntity playerEntity) {
        if (this.isBig()) {
            this.damage(playerEntity);
        }
    }
    
    protected void damage(final LivingEntity target) {
        if (this.isAlive()) {
            final int integer2 = this.getSize();
            if (this.squaredDistanceTo(target) < 0.6 * integer2 * (0.6 * integer2) && this.canSee(target) && target.damage(DamageSource.mob(this), (float)this.getDamageAmount())) {
                this.playSound(SoundEvents.kA, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                this.dealDamage(this, target);
            }
        }
    }
    
    @Override
    protected float getActiveEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        return 0.625f * entitySize.height;
    }
    
    protected boolean isBig() {
        return !this.isSmall() && this.canMoveVoluntarily();
    }
    
    protected int getDamageAmount() {
        return this.getSize();
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        if (this.isSmall()) {
            return SoundEvents.kO;
        }
        return SoundEvents.kC;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        if (this.isSmall()) {
            return SoundEvents.kN;
        }
        return SoundEvents.kB;
    }
    
    protected SoundEvent getSquishSound() {
        if (this.isSmall()) {
            return SoundEvents.kQ;
        }
        return SoundEvents.kE;
    }
    
    @Override
    protected Identifier getLootTableId() {
        return (this.getSize() == 1) ? this.getType().getLootTableId() : LootTables.EMPTY;
    }
    
    @Override
    public boolean canSpawn(final IWorld iWorld, final SpawnType spawnType) {
        final BlockPos blockPos3 = new BlockPos(MathHelper.floor(this.x), 0, MathHelper.floor(this.z));
        if (iWorld.getLevelProperties().getGeneratorType() == LevelGeneratorType.FLAT && this.random.nextInt(4) != 1) {
            return false;
        }
        if (iWorld.getDifficulty() != Difficulty.PEACEFUL) {
            final Biome biome4 = iWorld.getBiome(blockPos3);
            if (biome4 == Biomes.h && this.y > 50.0 && this.y < 70.0 && this.random.nextFloat() < 0.5f && this.random.nextFloat() < iWorld.getMoonSize() && iWorld.getLightLevel(new BlockPos(this)) <= this.random.nextInt(8)) {
                return super.canSpawn(iWorld, spawnType);
            }
            final ChunkPos chunkPos5 = new ChunkPos(blockPos3);
            final boolean boolean6 = ChunkRandom.create(chunkPos5.x, chunkPos5.z, iWorld.getSeed(), 987234911L).nextInt(10) == 0;
            if (this.random.nextInt(10) == 0 && boolean6 && this.y < 40.0) {
                return super.canSpawn(iWorld, spawnType);
            }
        }
        return false;
    }
    
    @Override
    protected float getSoundVolume() {
        return 0.4f * this.getSize();
    }
    
    @Override
    public int getLookPitchSpeed() {
        return 0;
    }
    
    protected boolean makesJumpSound() {
        return this.getSize() > 0;
    }
    
    @Override
    protected void jump() {
        final Vec3d vec3d1 = this.getVelocity();
        this.setVelocity(vec3d1.x, 0.41999998688697815, vec3d1.z);
        this.velocityDirty = true;
    }
    
    @Nullable
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable final EntityData entityData, @Nullable final CompoundTag compoundTag) {
        int integer6 = this.random.nextInt(3);
        if (integer6 < 2 && this.random.nextFloat() < 0.5f * localDifficulty.getClampedLocalDifficulty()) {
            ++integer6;
        }
        final int integer7 = 1 << integer6;
        this.setSize(integer7, true);
        return super.initialize(iWorld, localDifficulty, difficulty, entityData, compoundTag);
    }
    
    protected SoundEvent getJumpSound() {
        return this.isSmall() ? SoundEvents.kP : SoundEvents.kD;
    }
    
    @Override
    public EntitySize getSize(final EntityPose entityPose) {
        return super.getSize(entityPose).scaled(0.255f * this.getSize());
    }
    
    static {
        SLIME_SIZE = DataTracker.<Integer>registerData(SlimeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }
    
    static class SlimeMoveControl extends MoveControl
    {
        private float targetYaw;
        private int ticksUntilJump;
        private final SlimeEntity slime;
        private boolean jumpOften;
        
        public SlimeMoveControl(final SlimeEntity slimeEntity) {
            super(slimeEntity);
            this.slime = slimeEntity;
            this.targetYaw = 180.0f * slimeEntity.yaw / 3.1415927f;
        }
        
        public void look(final float targetYaw, final boolean jumpOften) {
            this.targetYaw = targetYaw;
            this.jumpOften = jumpOften;
        }
        
        public void move(final double speed) {
            this.speed = speed;
            this.state = State.b;
        }
        
        @Override
        public void tick() {
            this.entity.yaw = this.changeAngle(this.entity.yaw, this.targetYaw, 90.0f);
            this.entity.headYaw = this.entity.yaw;
            this.entity.aK = this.entity.yaw;
            if (this.state != State.b) {
                this.entity.setForwardSpeed(0.0f);
                return;
            }
            this.state = State.a;
            if (this.entity.onGround) {
                this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue()));
                if (this.ticksUntilJump-- <= 0) {
                    this.ticksUntilJump = this.slime.getTicksUntilNextJump();
                    if (this.jumpOften) {
                        this.ticksUntilJump /= 3;
                    }
                    this.slime.getJumpControl().setActive();
                    if (this.slime.makesJumpSound()) {
                        this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), ((this.slime.getRand().nextFloat() - this.slime.getRand().nextFloat()) * 0.2f + 1.0f) * 0.8f);
                    }
                }
                else {
                    this.slime.sidewaysSpeed = 0.0f;
                    this.slime.forwardSpeed = 0.0f;
                    this.entity.setMovementSpeed(0.0f);
                }
            }
            else {
                this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue()));
            }
        }
    }
    
    static class a extends Goal
    {
        private final SlimeEntity a;
        private int b;
        
        public a(final SlimeEntity slimeEntity) {
            this.a = slimeEntity;
            this.setControls(EnumSet.<Control>of(Control.b));
        }
        
        @Override
        public boolean canStart() {
            final LivingEntity livingEntity1 = this.a.getTarget();
            return livingEntity1 != null && livingEntity1.isAlive() && (!(livingEntity1 instanceof PlayerEntity) || !((PlayerEntity)livingEntity1).abilities.invulnerable) && this.a.getMoveControl() instanceof SlimeMoveControl;
        }
        
        @Override
        public void start() {
            this.b = 300;
            super.start();
        }
        
        @Override
        public boolean shouldContinue() {
            final LivingEntity livingEntity1 = this.a.getTarget();
            return livingEntity1 != null && livingEntity1.isAlive() && (!(livingEntity1 instanceof PlayerEntity) || !((PlayerEntity)livingEntity1).abilities.invulnerable) && --this.b > 0;
        }
        
        @Override
        public void tick() {
            this.a.lookAtEntity(this.a.getTarget(), 10.0f, 10.0f);
            ((SlimeMoveControl)this.a.getMoveControl()).look(this.a.yaw, this.a.isBig());
        }
    }
    
    static class e extends Goal
    {
        private final SlimeEntity a;
        private float b;
        private int c;
        
        public e(final SlimeEntity slimeEntity) {
            this.a = slimeEntity;
            this.setControls(EnumSet.<Control>of(Control.b));
        }
        
        @Override
        public boolean canStart() {
            return this.a.getTarget() == null && (this.a.onGround || this.a.isInsideWater() || this.a.isTouchingLava() || this.a.hasStatusEffect(StatusEffects.y)) && this.a.getMoveControl() instanceof SlimeMoveControl;
        }
        
        @Override
        public void tick() {
            final int c = this.c - 1;
            this.c = c;
            if (c <= 0) {
                this.c = 40 + this.a.getRand().nextInt(60);
                this.b = (float)this.a.getRand().nextInt(360);
            }
            ((SlimeMoveControl)this.a.getMoveControl()).look(this.b, false);
        }
    }
    
    static class b extends Goal
    {
        private final SlimeEntity a;
        
        public b(final SlimeEntity slimeEntity) {
            this.a = slimeEntity;
            this.setControls(EnumSet.<Control>of(Control.c, Control.a));
            slimeEntity.getNavigation().setCanSwim(true);
        }
        
        @Override
        public boolean canStart() {
            return (this.a.isInsideWater() || this.a.isTouchingLava()) && this.a.getMoveControl() instanceof SlimeMoveControl;
        }
        
        @Override
        public void tick() {
            if (this.a.getRand().nextFloat() < 0.8f) {
                this.a.getJumpControl().setActive();
            }
            ((SlimeMoveControl)this.a.getMoveControl()).move(1.2);
        }
    }
    
    static class c extends Goal
    {
        private final SlimeEntity a;
        
        public c(final SlimeEntity slimeEntity) {
            this.a = slimeEntity;
            this.setControls(EnumSet.<Control>of(Control.c, Control.a));
        }
        
        @Override
        public boolean canStart() {
            return !this.a.hasVehicle();
        }
        
        @Override
        public void tick() {
            ((SlimeMoveControl)this.a.getMoveControl()).move(1.0);
        }
    }
}
