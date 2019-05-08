package net.minecraft.entity.mob;

import net.minecraft.util.math.BlockPos;
import java.util.Random;
import java.util.EnumSet;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.IWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundCategory;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.Difficulty;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;

public class GhastEntity extends FlyingEntity implements Monster
{
    private static final TrackedData<Boolean> SHOOTING;
    private int fireballStrength;
    
    public GhastEntity(final EntityType<? extends GhastEntity> type, final World world) {
        super(type, world);
        this.fireballStrength = 1;
        this.experiencePoints = 5;
        this.moveControl = new GhastMoveControl(this);
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(5, new FlyRandomlyGoal(this));
        this.goalSelector.add(7, new a(this));
        this.goalSelector.add(7, new ShootFireballGoal(this));
        this.targetSelector.add(1, new FollowTargetGoal<>(this, PlayerEntity.class, 10, true, false, livingEntity -> Math.abs(livingEntity.y - this.y) <= 4.0));
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isShooting() {
        return this.dataTracker.<Boolean>get(GhastEntity.SHOOTING);
    }
    
    public void setShooting(final boolean boolean1) {
        this.dataTracker.<Boolean>set(GhastEntity.SHOOTING, boolean1);
    }
    
    public int getFireballStrength() {
        return this.fireballStrength;
    }
    
    @Override
    public void tick() {
        super.tick();
        if (!this.world.isClient && this.world.getDifficulty() == Difficulty.PEACEFUL) {
            this.remove();
        }
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (source.getSource() instanceof FireballEntity && source.getAttacker() instanceof PlayerEntity) {
            super.damage(source, 1000.0f);
            return true;
        }
        return super.damage(source, amount);
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Boolean>startTracking(GhastEntity.SHOOTING, false);
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
        this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(100.0);
    }
    
    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.f;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.dP;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.dR;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.dQ;
    }
    
    @Override
    protected float getSoundVolume() {
        return 10.0f;
    }
    
    @Override
    public boolean canSpawn(final IWorld iWorld, final SpawnType spawnType) {
        return this.random.nextInt(20) == 0 && super.canSpawn(iWorld, spawnType) && iWorld.getDifficulty() != Difficulty.PEACEFUL;
    }
    
    @Override
    public int getLimitPerChunk() {
        return 1;
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("ExplosionPower", this.fireballStrength);
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.containsKey("ExplosionPower", 99)) {
            this.fireballStrength = tag.getInt("ExplosionPower");
        }
    }
    
    @Override
    protected float getActiveEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        return 2.6f;
    }
    
    static {
        SHOOTING = DataTracker.<Boolean>registerData(GhastEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }
    
    static class GhastMoveControl extends MoveControl
    {
        private final GhastEntity ghast;
        private int j;
        
        public GhastMoveControl(final GhastEntity ghastEntity) {
            super(ghastEntity);
            this.ghast = ghastEntity;
        }
        
        @Override
        public void tick() {
            if (this.state != State.b) {
                return;
            }
            if (this.j-- <= 0) {
                this.j += this.ghast.getRand().nextInt(5) + 2;
                Vec3d vec3d1 = new Vec3d(this.targetX - this.ghast.x, this.targetY - this.ghast.y, this.targetZ - this.ghast.z);
                final double double2 = vec3d1.length();
                vec3d1 = vec3d1.normalize();
                if (this.a(vec3d1, MathHelper.ceil(double2))) {
                    this.ghast.setVelocity(this.ghast.getVelocity().add(vec3d1.multiply(0.1)));
                }
                else {
                    this.state = State.a;
                }
            }
        }
        
        private boolean a(final Vec3d vec3d, final int integer) {
            BoundingBox boundingBox3 = this.ghast.getBoundingBox();
            for (int integer2 = 1; integer2 < integer; ++integer2) {
                boundingBox3 = boundingBox3.offset(vec3d);
                if (!this.ghast.world.doesNotCollide(this.ghast, boundingBox3)) {
                    return false;
                }
            }
            return true;
        }
    }
    
    static class FlyRandomlyGoal extends Goal
    {
        private final GhastEntity a;
        
        public FlyRandomlyGoal(final GhastEntity ghastEntity) {
            this.a = ghastEntity;
            this.setControls(EnumSet.<Control>of(Control.a));
        }
        
        @Override
        public boolean canStart() {
            final MoveControl moveControl1 = this.a.getMoveControl();
            if (!moveControl1.isMoving()) {
                return true;
            }
            final double double2 = moveControl1.getTargetX() - this.a.x;
            final double double3 = moveControl1.getTargetY() - this.a.y;
            final double double4 = moveControl1.getTargetZ() - this.a.z;
            final double double5 = double2 * double2 + double3 * double3 + double4 * double4;
            return double5 < 1.0 || double5 > 3600.0;
        }
        
        @Override
        public boolean shouldContinue() {
            return false;
        }
        
        @Override
        public void start() {
            final Random random1 = this.a.getRand();
            final double double2 = this.a.x + (random1.nextFloat() * 2.0f - 1.0f) * 16.0f;
            final double double3 = this.a.y + (random1.nextFloat() * 2.0f - 1.0f) * 16.0f;
            final double double4 = this.a.z + (random1.nextFloat() * 2.0f - 1.0f) * 16.0f;
            this.a.getMoveControl().moveTo(double2, double3, double4, 1.0);
        }
    }
    
    static class a extends Goal
    {
        private final GhastEntity a;
        
        public a(final GhastEntity ghastEntity) {
            this.a = ghastEntity;
            this.setControls(EnumSet.<Control>of(Control.b));
        }
        
        @Override
        public boolean canStart() {
            return true;
        }
        
        @Override
        public void tick() {
            if (this.a.getTarget() == null) {
                final Vec3d vec3d1 = this.a.getVelocity();
                this.a.yaw = -(float)MathHelper.atan2(vec3d1.x, vec3d1.z) * 57.295776f;
                this.a.aK = this.a.yaw;
            }
            else {
                final LivingEntity livingEntity1 = this.a.getTarget();
                final double double2 = 64.0;
                if (livingEntity1.squaredDistanceTo(this.a) < 4096.0) {
                    final double double3 = livingEntity1.x - this.a.x;
                    final double double4 = livingEntity1.z - this.a.z;
                    this.a.yaw = -(float)MathHelper.atan2(double3, double4) * 57.295776f;
                    this.a.aK = this.a.yaw;
                }
            }
        }
    }
    
    static class ShootFireballGoal extends Goal
    {
        private final GhastEntity owner;
        public int cooldown;
        
        public ShootFireballGoal(final GhastEntity ghastEntity) {
            this.owner = ghastEntity;
        }
        
        @Override
        public boolean canStart() {
            return this.owner.getTarget() != null;
        }
        
        @Override
        public void start() {
            this.cooldown = 0;
        }
        
        @Override
        public void stop() {
            this.owner.setShooting(false);
        }
        
        @Override
        public void tick() {
            final LivingEntity livingEntity1 = this.owner.getTarget();
            final double double2 = 64.0;
            if (livingEntity1.squaredDistanceTo(this.owner) < 4096.0 && this.owner.canSee(livingEntity1)) {
                final World world4 = this.owner.world;
                ++this.cooldown;
                if (this.cooldown == 10) {
                    world4.playLevelEvent(null, 1015, new BlockPos(this.owner), 0);
                }
                if (this.cooldown == 20) {
                    final double double3 = 4.0;
                    final Vec3d vec3d7 = this.owner.getRotationVec(1.0f);
                    final double double4 = livingEntity1.x - (this.owner.x + vec3d7.x * 4.0);
                    final double double5 = livingEntity1.getBoundingBox().minY + livingEntity1.getHeight() / 2.0f - (0.5 + this.owner.y + this.owner.getHeight() / 2.0f);
                    final double double6 = livingEntity1.z - (this.owner.z + vec3d7.z * 4.0);
                    world4.playLevelEvent(null, 1016, new BlockPos(this.owner), 0);
                    final FireballEntity fireballEntity14 = new FireballEntity(world4, this.owner, double4, double5, double6);
                    fireballEntity14.explosionPower = this.owner.getFireballStrength();
                    fireballEntity14.x = this.owner.x + vec3d7.x * 4.0;
                    fireballEntity14.y = this.owner.y + this.owner.getHeight() / 2.0f + 0.5;
                    fireballEntity14.z = this.owner.z + vec3d7.z * 4.0;
                    world4.spawnEntity(fireballEntity14);
                    this.cooldown = -40;
                }
            }
            else if (this.cooldown > 0) {
                --this.cooldown;
            }
            this.owner.setShooting(this.cooldown > 10);
        }
    }
}
