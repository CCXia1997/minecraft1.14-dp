package net.minecraft.entity.mob;

import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import java.util.EnumSet;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.ai.control.MoveControl;
import java.util.Random;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.IWorld;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.MovementType;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.data.TrackedData;

public class VexEntity extends HostileEntity
{
    protected static final TrackedData<Byte> VEX_FLAGS;
    private MobEntity owner;
    @Nullable
    private BlockPos bounds;
    private boolean alive;
    private int lifeTicks;
    
    public VexEntity(final EntityType<? extends VexEntity> type, final World world) {
        super(type, world);
        this.moveControl = new VexMoveControl(this);
        this.experiencePoints = 3;
    }
    
    @Override
    public void move(final MovementType type, final Vec3d offset) {
        super.move(type, offset);
        this.checkBlockCollision();
    }
    
    @Override
    public void tick() {
        this.noClip = true;
        super.tick();
        this.noClip = false;
        this.setUnaffectedByGravity(true);
        if (this.alive && --this.lifeTicks <= 0) {
            this.lifeTicks = 20;
            this.damage(DamageSource.STARVE, 1.0f);
        }
    }
    
    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(4, new ChargeTargetGoal());
        this.goalSelector.add(8, new LookAtTargetGoal());
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0f, 1.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0f));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[] { RaiderEntity.class }).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new TrackOwnerTargetGoal(this));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, PlayerEntity.class, true));
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(14.0);
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Byte>startTracking(VexEntity.VEX_FLAGS, (Byte)0);
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.containsKey("BoundX")) {
            this.bounds = new BlockPos(tag.getInt("BoundX"), tag.getInt("BoundY"), tag.getInt("BoundZ"));
        }
        if (tag.containsKey("LifeTicks")) {
            this.setLifeTicks(tag.getInt("LifeTicks"));
        }
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        if (this.bounds != null) {
            tag.putInt("BoundX", this.bounds.getX());
            tag.putInt("BoundY", this.bounds.getY());
            tag.putInt("BoundZ", this.bounds.getZ());
        }
        if (this.alive) {
            tag.putInt("LifeTicks", this.lifeTicks);
        }
    }
    
    public MobEntity getOwner() {
        return this.owner;
    }
    
    @Nullable
    public BlockPos getBounds() {
        return this.bounds;
    }
    
    public void setBounds(@Nullable final BlockPos blockPos) {
        this.bounds = blockPos;
    }
    
    private boolean areFlagsSet(final int mask) {
        final int integer2 = this.dataTracker.<Byte>get(VexEntity.VEX_FLAGS);
        return (integer2 & mask) != 0x0;
    }
    
    private void setVexFlag(final int mask, final boolean value) {
        int integer3 = this.dataTracker.<Byte>get(VexEntity.VEX_FLAGS);
        if (value) {
            integer3 |= mask;
        }
        else {
            integer3 &= ~mask;
        }
        this.dataTracker.<Byte>set(VexEntity.VEX_FLAGS, (byte)(integer3 & 0xFF));
    }
    
    public boolean isCharging() {
        return this.areFlagsSet(1);
    }
    
    public void setCharging(final boolean charging) {
        this.setVexFlag(1, charging);
    }
    
    public void setOwner(final MobEntity mobEntity) {
        this.owner = mobEntity;
    }
    
    public void setLifeTicks(final int lifeTicks) {
        this.alive = true;
        this.lifeTicks = lifeTicks;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.mq;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ms;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.mt;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public int getLightmapCoordinates() {
        return 15728880;
    }
    
    @Override
    public float getBrightnessAtEyes() {
        return 1.0f;
    }
    
    @Nullable
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable final EntityData entityData, @Nullable final CompoundTag compoundTag) {
        this.initEquipment(localDifficulty);
        this.updateEnchantments(localDifficulty);
        return super.initialize(iWorld, localDifficulty, difficulty, entityData, compoundTag);
    }
    
    @Override
    protected void initEquipment(final LocalDifficulty localDifficulty) {
        this.setEquippedStack(EquipmentSlot.HAND_MAIN, new ItemStack(Items.jm));
        this.setEquipmentDropChance(EquipmentSlot.HAND_MAIN, 0.0f);
    }
    
    static {
        VEX_FLAGS = DataTracker.<Byte>registerData(VexEntity.class, TrackedDataHandlerRegistry.BYTE);
    }
    
    class VexMoveControl extends MoveControl
    {
        public VexMoveControl(final VexEntity vexEntity2) {
            super(vexEntity2);
        }
        
        @Override
        public void tick() {
            if (this.state != State.b) {
                return;
            }
            final Vec3d vec3d1 = new Vec3d(this.targetX - VexEntity.this.x, this.targetY - VexEntity.this.y, this.targetZ - VexEntity.this.z);
            final double double2 = vec3d1.length();
            if (double2 < VexEntity.this.getBoundingBox().averageDimension()) {
                this.state = State.a;
                VexEntity.this.setVelocity(VexEntity.this.getVelocity().multiply(0.5));
            }
            else {
                VexEntity.this.setVelocity(VexEntity.this.getVelocity().add(vec3d1.multiply(this.speed * 0.05 / double2)));
                if (VexEntity.this.getTarget() == null) {
                    final Vec3d vec3d2 = VexEntity.this.getVelocity();
                    VexEntity.this.yaw = -(float)MathHelper.atan2(vec3d2.x, vec3d2.z) * 57.295776f;
                    VexEntity.this.aK = VexEntity.this.yaw;
                }
                else {
                    final double double3 = VexEntity.this.getTarget().x - VexEntity.this.x;
                    final double double4 = VexEntity.this.getTarget().z - VexEntity.this.z;
                    VexEntity.this.yaw = -(float)MathHelper.atan2(double3, double4) * 57.295776f;
                    VexEntity.this.aK = VexEntity.this.yaw;
                }
            }
        }
    }
    
    class ChargeTargetGoal extends Goal
    {
        public ChargeTargetGoal() {
            this.setControls(EnumSet.<Control>of(Control.a));
        }
        
        @Override
        public boolean canStart() {
            return VexEntity.this.getTarget() != null && !VexEntity.this.getMoveControl().isMoving() && VexEntity.this.random.nextInt(7) == 0 && VexEntity.this.squaredDistanceTo(VexEntity.this.getTarget()) > 4.0;
        }
        
        @Override
        public boolean shouldContinue() {
            return VexEntity.this.getMoveControl().isMoving() && VexEntity.this.isCharging() && VexEntity.this.getTarget() != null && VexEntity.this.getTarget().isAlive();
        }
        
        @Override
        public void start() {
            final LivingEntity livingEntity1 = VexEntity.this.getTarget();
            final Vec3d vec3d2 = livingEntity1.getCameraPosVec(1.0f);
            VexEntity.this.moveControl.moveTo(vec3d2.x, vec3d2.y, vec3d2.z, 1.0);
            VexEntity.this.setCharging(true);
            VexEntity.this.playSound(SoundEvents.mr, 1.0f, 1.0f);
        }
        
        @Override
        public void stop() {
            VexEntity.this.setCharging(false);
        }
        
        @Override
        public void tick() {
            final LivingEntity livingEntity1 = VexEntity.this.getTarget();
            if (VexEntity.this.getBoundingBox().intersects(livingEntity1.getBoundingBox())) {
                VexEntity.this.tryAttack(livingEntity1);
                VexEntity.this.setCharging(false);
            }
            else {
                final double double2 = VexEntity.this.squaredDistanceTo(livingEntity1);
                if (double2 < 9.0) {
                    final Vec3d vec3d4 = livingEntity1.getCameraPosVec(1.0f);
                    VexEntity.this.moveControl.moveTo(vec3d4.x, vec3d4.y, vec3d4.z, 1.0);
                }
            }
        }
    }
    
    class LookAtTargetGoal extends Goal
    {
        public LookAtTargetGoal() {
            this.setControls(EnumSet.<Control>of(Control.a));
        }
        
        @Override
        public boolean canStart() {
            return !VexEntity.this.getMoveControl().isMoving() && VexEntity.this.random.nextInt(7) == 0;
        }
        
        @Override
        public boolean shouldContinue() {
            return false;
        }
        
        @Override
        public void tick() {
            BlockPos blockPos1 = VexEntity.this.getBounds();
            if (blockPos1 == null) {
                blockPos1 = new BlockPos(VexEntity.this);
            }
            int integer2 = 0;
            while (integer2 < 3) {
                final BlockPos blockPos2 = blockPos1.add(VexEntity.this.random.nextInt(15) - 7, VexEntity.this.random.nextInt(11) - 5, VexEntity.this.random.nextInt(15) - 7);
                if (VexEntity.this.world.isAir(blockPos2)) {
                    VexEntity.this.moveControl.moveTo(blockPos2.getX() + 0.5, blockPos2.getY() + 0.5, blockPos2.getZ() + 0.5, 0.25);
                    if (VexEntity.this.getTarget() == null) {
                        VexEntity.this.getLookControl().lookAt(blockPos2.getX() + 0.5, blockPos2.getY() + 0.5, blockPos2.getZ() + 0.5, 180.0f, 20.0f);
                        break;
                    }
                    break;
                }
                else {
                    ++integer2;
                }
            }
        }
    }
    
    class TrackOwnerTargetGoal extends TrackTargetGoal
    {
        private final TargetPredicate TRACK_OWNER_PREDICATE;
        
        public TrackOwnerTargetGoal(final MobEntityWithAi mobEntityWithAi) {
            super(mobEntityWithAi, false);
            this.TRACK_OWNER_PREDICATE = new TargetPredicate().includeHidden().ignoreDistanceScalingFactor();
        }
        
        @Override
        public boolean canStart() {
            return VexEntity.this.owner != null && VexEntity.this.owner.getTarget() != null && this.canTrack(VexEntity.this.owner.getTarget(), this.TRACK_OWNER_PREDICATE);
        }
        
        @Override
        public void start() {
            VexEntity.this.setTarget(VexEntity.this.owner.getTarget());
            super.start();
        }
    }
}
