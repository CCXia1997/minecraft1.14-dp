package net.minecraft.entity.passive;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import java.time.temporal.TemporalField;
import java.time.temporal.ChronoField;
import java.time.LocalDate;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.IWorld;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Position;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.BlockView;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import javax.annotation.Nullable;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.AmbientEntity;

public class BatEntity extends AmbientEntity
{
    private static final TrackedData<Byte> BAT_FLAGS;
    private static final TargetPredicate CLOSE_PLAYER_PREDICATE;
    private BlockPos d;
    
    public BatEntity(final EntityType<? extends BatEntity> type, final World world) {
        super(type, world);
        this.setRoosting(true);
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Byte>startTracking(BatEntity.BAT_FLAGS, (Byte)0);
    }
    
    @Override
    protected float getSoundVolume() {
        return 0.1f;
    }
    
    @Override
    protected float getSoundPitch() {
        return super.getSoundPitch() * 0.95f;
    }
    
    @Nullable
    public SoundEvent getAmbientSound() {
        if (this.isRoosting() && this.random.nextInt(4) != 0) {
            return null;
        }
        return SoundEvents.P;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.R;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.Q;
    }
    
    @Override
    public boolean isPushable() {
        return false;
    }
    
    @Override
    protected void pushAway(final Entity entity) {
    }
    
    @Override
    protected void doPushLogic() {
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(6.0);
    }
    
    public boolean isRoosting() {
        return (this.dataTracker.<Byte>get(BatEntity.BAT_FLAGS) & 0x1) != 0x0;
    }
    
    public void setRoosting(final boolean boolean1) {
        final byte byte2 = this.dataTracker.<Byte>get(BatEntity.BAT_FLAGS);
        if (boolean1) {
            this.dataTracker.<Byte>set(BatEntity.BAT_FLAGS, (byte)(byte2 | 0x1));
        }
        else {
            this.dataTracker.<Byte>set(BatEntity.BAT_FLAGS, (byte)(byte2 & 0xFFFFFFFE));
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.isRoosting()) {
            this.setVelocity(Vec3d.ZERO);
            this.y = MathHelper.floor(this.y) + 1.0 - this.getHeight();
        }
        else {
            this.setVelocity(this.getVelocity().multiply(1.0, 0.6, 1.0));
        }
    }
    
    @Override
    protected void mobTick() {
        super.mobTick();
        final BlockPos blockPos1 = new BlockPos(this);
        final BlockPos blockPos2 = blockPos1.up();
        if (this.isRoosting()) {
            if (this.world.getBlockState(blockPos2).isSimpleFullBlock(this.world, blockPos1)) {
                if (this.random.nextInt(200) == 0) {
                    this.headYaw = (float)this.random.nextInt(360);
                }
                if (this.world.getClosestPlayer(BatEntity.CLOSE_PLAYER_PREDICATE, this) != null) {
                    this.setRoosting(false);
                    this.world.playLevelEvent(null, 1025, blockPos1, 0);
                }
            }
            else {
                this.setRoosting(false);
                this.world.playLevelEvent(null, 1025, blockPos1, 0);
            }
        }
        else {
            if (this.d != null && (!this.world.isAir(this.d) || this.d.getY() < 1)) {
                this.d = null;
            }
            if (this.d == null || this.random.nextInt(30) == 0 || this.d.isWithinDistance(this.getPos(), 2.0)) {
                this.d = new BlockPos((int)this.x + this.random.nextInt(7) - this.random.nextInt(7), (int)this.y + this.random.nextInt(6) - 2, (int)this.z + this.random.nextInt(7) - this.random.nextInt(7));
            }
            final double double3 = this.d.getX() + 0.5 - this.x;
            final double double4 = this.d.getY() + 0.1 - this.y;
            final double double5 = this.d.getZ() + 0.5 - this.z;
            final Vec3d vec3d9 = this.getVelocity();
            final Vec3d vec3d10 = vec3d9.add((Math.signum(double3) * 0.5 - vec3d9.x) * 0.10000000149011612, (Math.signum(double4) * 0.699999988079071 - vec3d9.y) * 0.10000000149011612, (Math.signum(double5) * 0.5 - vec3d9.z) * 0.10000000149011612);
            this.setVelocity(vec3d10);
            final float float11 = (float)(MathHelper.atan2(vec3d10.z, vec3d10.x) * 57.2957763671875) - 90.0f;
            final float float12 = MathHelper.wrapDegrees(float11 - this.yaw);
            this.forwardSpeed = 0.5f;
            this.yaw += float12;
            if (this.random.nextInt(100) == 0 && this.world.getBlockState(blockPos2).isSimpleFullBlock(this.world, blockPos2)) {
                this.setRoosting(true);
            }
        }
    }
    
    @Override
    protected boolean canClimb() {
        return false;
    }
    
    @Override
    public void handleFallDamage(final float fallDistance, final float damageMultiplier) {
    }
    
    @Override
    protected void fall(final double heightDifference, final boolean onGround, final BlockState blockState, final BlockPos blockPos) {
    }
    
    @Override
    public boolean canAvoidTraps() {
        return true;
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (!this.world.isClient && this.isRoosting()) {
            this.setRoosting(false);
        }
        return super.damage(source, amount);
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.dataTracker.<Byte>set(BatEntity.BAT_FLAGS, tag.getByte("BatFlags"));
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putByte("BatFlags", this.dataTracker.<Byte>get(BatEntity.BAT_FLAGS));
    }
    
    @Override
    public boolean canSpawn(final IWorld iWorld, final SpawnType spawnType) {
        final BlockPos blockPos3 = new BlockPos(this.x, this.getBoundingBox().minY, this.z);
        if (blockPos3.getY() >= iWorld.getSeaLevel()) {
            return false;
        }
        final int integer4 = iWorld.getLightLevel(blockPos3);
        int integer5 = 4;
        if (this.dT()) {
            integer5 = 7;
        }
        else if (this.random.nextBoolean()) {
            return false;
        }
        return integer4 <= this.random.nextInt(integer5) && super.canSpawn(iWorld, spawnType);
    }
    
    private boolean dT() {
        final LocalDate localDate1 = LocalDate.now();
        final int integer2 = localDate1.get(ChronoField.DAY_OF_MONTH);
        final int integer3 = localDate1.get(ChronoField.MONTH_OF_YEAR);
        return (integer3 == 10 && integer2 >= 20) || (integer3 == 11 && integer2 <= 3);
    }
    
    @Override
    protected float getActiveEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        return entitySize.height / 2.0f;
    }
    
    static {
        BAT_FLAGS = DataTracker.<Byte>registerData(BatEntity.class, TrackedDataHandlerRegistry.BYTE);
        CLOSE_PLAYER_PREDICATE = new TargetPredicate().setBaseMaxDistance(4.0).includeTeammates();
    }
}
