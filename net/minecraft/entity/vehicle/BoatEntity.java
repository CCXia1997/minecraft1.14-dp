package net.minecraft.entity.vehicle;

import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.block.LilyPadBlock;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.fluid.FluidState;
import net.minecraft.world.BlockView;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import java.util.List;
import net.minecraft.sound.SoundEvent;
import net.minecraft.entity.WaterCreatureEntity;
import net.minecraft.entity.LivingEntity;
import java.util.function.Predicate;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.entity.MovementType;
import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.BoatPaddleStateC2SPacket;
import net.minecraft.util.math.Direction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.item.ItemProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.damage.DamageSource;
import javax.annotation.Nullable;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.Entity;

public class BoatEntity extends Entity
{
    private static final TrackedData<Integer> b;
    private static final TrackedData<Integer> c;
    private static final TrackedData<Float> d;
    private static final TrackedData<Integer> BOAT_TYPE;
    private static final TrackedData<Boolean> LEFT_PADDLE_MOVING;
    private static final TrackedData<Boolean> RIGHT_PADDLE_MOVING;
    private static final TrackedData<Integer> ar;
    private final float[] as;
    private float at;
    private float au;
    private float av;
    private int aw;
    private double ax;
    private double ay;
    private double az;
    private double aA;
    private double aB;
    private boolean aC;
    private boolean aD;
    private boolean aE;
    private boolean aF;
    private double aG;
    private float aH;
    private Location location;
    private Location lastLocation;
    private double aK;
    private boolean aL;
    private boolean aM;
    private float aN;
    private float aO;
    private float aP;
    
    public BoatEntity(final EntityType<? extends BoatEntity> type, final World world) {
        super(type, world);
        this.as = new float[2];
        this.i = true;
    }
    
    public BoatEntity(final World world, final double x, final double double4, final double double6) {
        this(EntityType.BOAT, world);
        this.setPosition(x, double4, double6);
        this.setVelocity(Vec3d.ZERO);
        this.prevX = x;
        this.prevY = double4;
        this.prevZ = double6;
    }
    
    @Override
    protected boolean canClimb() {
        return false;
    }
    
    @Override
    protected void initDataTracker() {
        this.dataTracker.<Integer>startTracking(BoatEntity.b, 0);
        this.dataTracker.<Integer>startTracking(BoatEntity.c, 1);
        this.dataTracker.<Float>startTracking(BoatEntity.d, 0.0f);
        this.dataTracker.<Integer>startTracking(BoatEntity.BOAT_TYPE, Type.OAK.ordinal());
        this.dataTracker.<Boolean>startTracking(BoatEntity.LEFT_PADDLE_MOVING, false);
        this.dataTracker.<Boolean>startTracking(BoatEntity.RIGHT_PADDLE_MOVING, false);
        this.dataTracker.<Integer>startTracking(BoatEntity.ar, 0);
    }
    
    @Nullable
    @Override
    public BoundingBox j(final Entity entity) {
        if (entity.isPushable()) {
            return entity.getBoundingBox();
        }
        return null;
    }
    
    @Nullable
    @Override
    public BoundingBox ap() {
        return this.getBoundingBox();
    }
    
    @Override
    public boolean isPushable() {
        return true;
    }
    
    @Override
    public double getMountedHeightOffset() {
        return -0.1;
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (this.world.isClient || this.removed) {
            return true;
        }
        if (source instanceof ProjectileDamageSource && source.getAttacker() != null && this.hasPassenger(source.getAttacker())) {
            return false;
        }
        this.c(-this.o());
        this.b(10);
        this.a(this.m() + amount * 10.0f);
        this.scheduleVelocityUpdate();
        final boolean boolean3 = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity)source.getAttacker()).abilities.creativeMode;
        if (boolean3 || this.m() > 40.0f) {
            if (!boolean3 && this.world.getGameRules().getBoolean("doEntityDrops")) {
                this.dropItem(this.asItem());
            }
            this.remove();
        }
        return true;
    }
    
    @Override
    public void j(final boolean boolean1) {
        if (!this.world.isClient) {
            this.aL = true;
            this.aM = boolean1;
            if (this.A() == 0) {
                this.d(60);
            }
        }
        this.world.addParticle(ParticleTypes.X, this.x + this.random.nextFloat(), this.y + 0.7, this.z + this.random.nextFloat(), 0.0, 0.0, 0.0);
        if (this.random.nextInt(20) == 0) {
            this.world.playSound(this.x, this.y, this.z, this.getSplashSound(), this.getSoundCategory(), 1.0f, 0.8f + 0.4f * this.random.nextFloat(), false);
        }
    }
    
    @Override
    public void pushAwayFrom(final Entity entity) {
        if (entity instanceof BoatEntity) {
            if (entity.getBoundingBox().minY < this.getBoundingBox().maxY) {
                super.pushAwayFrom(entity);
            }
        }
        else if (entity.getBoundingBox().minY <= this.getBoundingBox().minY) {
            super.pushAwayFrom(entity);
        }
    }
    
    public Item asItem() {
        switch (this.getBoatType()) {
            default: {
                return Items.kE;
            }
            case SPRUCE: {
                return Items.oY;
            }
            case BIRCH: {
                return Items.oZ;
            }
            case JUNGLE: {
                return Items.pa;
            }
            case ACACIA: {
                return Items.pb;
            }
            case DARK_OAK: {
                return Items.pc;
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void aX() {
        this.c(-this.o());
        this.b(10);
        this.a(this.m() * 11.0f);
    }
    
    @Override
    public boolean collides() {
        return !this.removed;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void setPositionAndRotations(final double x, final double y, final double z, final float float7, final float float8, final int integer9, final boolean boolean10) {
        this.ax = x;
        this.ay = y;
        this.az = z;
        this.aA = float7;
        this.aB = float8;
        this.aw = 10;
    }
    
    @Override
    public Direction getMovementDirection() {
        return this.getHorizontalFacing().rotateYClockwise();
    }
    
    @Override
    public void tick() {
        this.lastLocation = this.location;
        this.location = this.checkLocation();
        if (this.location == Location.b || this.location == Location.c) {
            ++this.au;
        }
        else {
            this.au = 0.0f;
        }
        if (!this.world.isClient && this.au >= 60.0f) {
            this.removeAllPassengers();
        }
        if (this.n() > 0) {
            this.b(this.n() - 1);
        }
        if (this.m() > 0.0f) {
            this.a(this.m() - 1.0f);
        }
        this.prevX = this.x;
        this.prevY = this.y;
        this.prevZ = this.z;
        super.tick();
        this.r();
        if (this.isLogicalSideForUpdatingMovement()) {
            if (this.getPassengerList().isEmpty() || !(this.getPassengerList().get(0) instanceof PlayerEntity)) {
                this.setPaddleState(false, false);
            }
            this.w();
            if (this.world.isClient) {
                this.z();
                this.world.sendPacket(new BoatPaddleStateC2SPacket(this.getPaddleState(0), this.getPaddleState(1)));
            }
            this.move(MovementType.a, this.getVelocity());
        }
        else {
            this.setVelocity(Vec3d.ZERO);
        }
        this.q();
        for (int integer1 = 0; integer1 <= 1; ++integer1) {
            if (this.getPaddleState(integer1)) {
                if (!this.isSilent() && this.as[integer1] % 6.2831855f <= 0.7853981852531433 && (this.as[integer1] + 0.39269909262657166) % 6.2831854820251465 >= 0.7853981852531433) {
                    final SoundEvent soundEvent2 = this.getPaddleSoundEvent();
                    if (soundEvent2 != null) {
                        final Vec3d vec3d3 = this.getRotationVec(1.0f);
                        final double double4 = (integer1 == 1) ? (-vec3d3.z) : vec3d3.z;
                        final double double5 = (integer1 == 1) ? vec3d3.x : (-vec3d3.x);
                        this.world.playSound(null, this.x + double4, this.y, this.z + double5, soundEvent2, this.getSoundCategory(), 1.0f, 0.8f + 0.4f * this.random.nextFloat());
                    }
                }
                final float[] as = this.as;
                final int n = integer1;
                as[n] += 0.39269909262657166;
            }
            else {
                this.as[integer1] = 0.0f;
            }
        }
        this.checkBlockCollision();
        final List<Entity> list1 = this.world.getEntities(this, this.getBoundingBox().expand(0.20000000298023224, -0.009999999776482582, 0.20000000298023224), EntityPredicates.canBePushedBy(this));
        if (!list1.isEmpty()) {
            final boolean boolean2 = !this.world.isClient && !(this.getPrimaryPassenger() instanceof PlayerEntity);
            for (int integer2 = 0; integer2 < list1.size(); ++integer2) {
                final Entity entity4 = list1.get(integer2);
                if (!entity4.hasPassenger(this)) {
                    if (boolean2 && this.getPassengerList().size() < 2 && !entity4.hasVehicle() && entity4.getWidth() < this.getWidth() && entity4 instanceof LivingEntity && !(entity4 instanceof WaterCreatureEntity) && !(entity4 instanceof PlayerEntity)) {
                        entity4.startRiding(this);
                    }
                    else {
                        this.pushAwayFrom(entity4);
                    }
                }
            }
        }
    }
    
    private void q() {
        if (this.world.isClient) {
            final int integer1 = this.A();
            if (integer1 > 0) {
                this.aN += 0.05f;
            }
            else {
                this.aN -= 0.1f;
            }
            this.aN = MathHelper.clamp(this.aN, 0.0f, 1.0f);
            this.aP = this.aO;
            this.aO = 10.0f * (float)Math.sin(0.5f * this.world.getTime()) * this.aN;
        }
        else {
            if (!this.aL) {
                this.d(0);
            }
            int integer1 = this.A();
            if (integer1 > 0) {
                --integer1;
                this.d(integer1);
                final int integer2 = 60 - integer1 - 1;
                if (integer2 > 0 && integer1 == 0) {
                    this.d(0);
                    final Vec3d vec3d3 = this.getVelocity();
                    if (this.aM) {
                        this.setVelocity(vec3d3.add(0.0, -0.7, 0.0));
                        this.removeAllPassengers();
                    }
                    else {
                        this.setVelocity(vec3d3.x, this.a(PlayerEntity.class) ? 2.7 : 0.6, vec3d3.z);
                    }
                }
                this.aL = false;
            }
        }
    }
    
    @Nullable
    protected SoundEvent getPaddleSoundEvent() {
        switch (this.checkLocation()) {
            case a:
            case b:
            case c: {
                return SoundEvents.ag;
            }
            case d: {
                return SoundEvents.af;
            }
            default: {
                return null;
            }
        }
    }
    
    private void r() {
        if (this.aw <= 0 || this.isLogicalSideForUpdatingMovement()) {
            return;
        }
        final double double1 = this.x + (this.ax - this.x) / this.aw;
        final double double2 = this.y + (this.ay - this.y) / this.aw;
        final double double3 = this.z + (this.az - this.z) / this.aw;
        final double double4 = MathHelper.wrapDegrees(this.aA - this.yaw);
        this.yaw += (float)(double4 / this.aw);
        this.pitch += (float)((this.aB - this.pitch) / this.aw);
        --this.aw;
        this.setPosition(double1, double2, double3);
        this.setRotation(this.yaw, this.pitch);
    }
    
    public void setPaddleState(final boolean boolean1, final boolean boolean2) {
        this.dataTracker.<Boolean>set(BoatEntity.LEFT_PADDLE_MOVING, boolean1);
        this.dataTracker.<Boolean>set(BoatEntity.RIGHT_PADDLE_MOVING, boolean2);
    }
    
    @Environment(EnvType.CLIENT)
    public float a(final int integer, final float float2) {
        if (this.getPaddleState(integer)) {
            return (float)MathHelper.clampedLerp(this.as[integer] - 0.39269909262657166, this.as[integer], float2);
        }
        return 0.0f;
    }
    
    private Location checkLocation() {
        final Location location1 = this.getUnderWaterLocation();
        if (location1 != null) {
            this.aG = this.getBoundingBox().maxY;
            return location1;
        }
        if (this.checKBoatInWater()) {
            return Location.a;
        }
        final float float2 = this.l();
        if (float2 > 0.0f) {
            this.aH = float2;
            return Location.d;
        }
        return Location.e;
    }
    
    public float k() {
        final BoundingBox boundingBox1 = this.getBoundingBox();
        final int integer2 = MathHelper.floor(boundingBox1.minX);
        final int integer3 = MathHelper.ceil(boundingBox1.maxX);
        final int integer4 = MathHelper.floor(boundingBox1.maxY);
        final int integer5 = MathHelper.ceil(boundingBox1.maxY - this.aK);
        final int integer6 = MathHelper.floor(boundingBox1.minZ);
        final int integer7 = MathHelper.ceil(boundingBox1.maxZ);
        try (final BlockPos.PooledMutable pooledMutable8 = BlockPos.PooledMutable.get()) {
            int integer8 = integer4;
        Label_0238_Outer:
            while (integer8 < integer5) {
                float float11 = 0.0f;
                int integer9 = integer2;
            Label_0238:
                while (true) {
                    while (integer9 < integer3) {
                        for (int integer10 = integer6; integer10 < integer7; ++integer10) {
                            pooledMutable8.set(integer9, integer8, integer10);
                            final FluidState fluidState14 = this.world.getFluidState(pooledMutable8);
                            if (fluidState14.matches(FluidTags.a)) {
                                float11 = Math.max(float11, fluidState14.getHeight(this.world, pooledMutable8));
                            }
                            if (float11 >= 1.0f) {
                                break Label_0238;
                            }
                        }
                        ++integer9;
                        continue Label_0238_Outer;
                        ++integer8;
                        continue Label_0238_Outer;
                    }
                    if (float11 < 1.0f) {
                        return pooledMutable8.getY() + float11;
                    }
                    continue Label_0238;
                }
            }
            return (float)(integer5 + 1);
        }
    }
    
    public float l() {
        final BoundingBox boundingBox1 = this.getBoundingBox();
        final BoundingBox boundingBox2 = new BoundingBox(boundingBox1.minX, boundingBox1.minY - 0.001, boundingBox1.minZ, boundingBox1.maxX, boundingBox1.minY, boundingBox1.maxZ);
        final int integer3 = MathHelper.floor(boundingBox2.minX) - 1;
        final int integer4 = MathHelper.ceil(boundingBox2.maxX) + 1;
        final int integer5 = MathHelper.floor(boundingBox2.minY) - 1;
        final int integer6 = MathHelper.ceil(boundingBox2.maxY) + 1;
        final int integer7 = MathHelper.floor(boundingBox2.minZ) - 1;
        final int integer8 = MathHelper.ceil(boundingBox2.maxZ) + 1;
        final VoxelShape voxelShape9 = VoxelShapes.cuboid(boundingBox2);
        float float10 = 0.0f;
        int integer9 = 0;
        try (final BlockPos.PooledMutable pooledMutable12 = BlockPos.PooledMutable.get()) {
            for (int integer10 = integer3; integer10 < integer4; ++integer10) {
                for (int integer11 = integer7; integer11 < integer8; ++integer11) {
                    final int integer12 = ((integer10 == integer3 || integer10 == integer4 - 1) + (integer11 == integer7 || integer11 == integer8 - 1)) ? 1 : 0;
                    if (integer12 != 2) {
                        for (int integer13 = integer5; integer13 < integer6; ++integer13) {
                            if (integer12 > 0) {
                                if (integer13 == integer5) {
                                    continue;
                                }
                                if (integer13 == integer6 - 1) {
                                    continue;
                                }
                            }
                            pooledMutable12.set(integer10, integer13, integer11);
                            final BlockState blockState18 = this.world.getBlockState(pooledMutable12);
                            if (!(blockState18.getBlock() instanceof LilyPadBlock)) {
                                if (VoxelShapes.matchesAnywhere(blockState18.getCollisionShape(this.world, pooledMutable12).offset(integer10, integer13, integer11), voxelShape9, BooleanBiFunction.AND)) {
                                    float10 += blockState18.getBlock().getFrictionCoefficient();
                                    ++integer9;
                                }
                            }
                        }
                    }
                }
            }
        }
        return float10 / integer9;
    }
    
    private boolean checKBoatInWater() {
        final BoundingBox boundingBox1 = this.getBoundingBox();
        final int integer2 = MathHelper.floor(boundingBox1.minX);
        final int integer3 = MathHelper.ceil(boundingBox1.maxX);
        final int integer4 = MathHelper.floor(boundingBox1.minY);
        final int integer5 = MathHelper.ceil(boundingBox1.minY + 0.001);
        final int integer6 = MathHelper.floor(boundingBox1.minZ);
        final int integer7 = MathHelper.ceil(boundingBox1.maxZ);
        boolean boolean8 = false;
        this.aG = Double.MIN_VALUE;
        try (final BlockPos.PooledMutable pooledMutable9 = BlockPos.PooledMutable.get()) {
            for (int integer8 = integer2; integer8 < integer3; ++integer8) {
                for (int integer9 = integer4; integer9 < integer5; ++integer9) {
                    for (int integer10 = integer6; integer10 < integer7; ++integer10) {
                        pooledMutable9.set(integer8, integer9, integer10);
                        final FluidState fluidState14 = this.world.getFluidState(pooledMutable9);
                        if (fluidState14.matches(FluidTags.a)) {
                            final float float15 = integer9 + fluidState14.getHeight(this.world, pooledMutable9);
                            this.aG = Math.max(float15, this.aG);
                            boolean8 |= (boundingBox1.minY < float15);
                        }
                    }
                }
            }
        }
        return boolean8;
    }
    
    @Nullable
    private Location getUnderWaterLocation() {
        final BoundingBox boundingBox1 = this.getBoundingBox();
        final double double2 = boundingBox1.maxY + 0.001;
        final int integer4 = MathHelper.floor(boundingBox1.minX);
        final int integer5 = MathHelper.ceil(boundingBox1.maxX);
        final int integer6 = MathHelper.floor(boundingBox1.maxY);
        final int integer7 = MathHelper.ceil(double2);
        final int integer8 = MathHelper.floor(boundingBox1.minZ);
        final int integer9 = MathHelper.ceil(boundingBox1.maxZ);
        boolean boolean10 = false;
        try (final BlockPos.PooledMutable pooledMutable11 = BlockPos.PooledMutable.get()) {
            for (int integer10 = integer4; integer10 < integer5; ++integer10) {
                for (int integer11 = integer6; integer11 < integer7; ++integer11) {
                    for (int integer12 = integer8; integer12 < integer9; ++integer12) {
                        pooledMutable11.set(integer10, integer11, integer12);
                        final FluidState fluidState16 = this.world.getFluidState(pooledMutable11);
                        if (fluidState16.matches(FluidTags.a) && double2 < pooledMutable11.getY() + fluidState16.getHeight(this.world, pooledMutable11)) {
                            if (!fluidState16.isStill()) {
                                return Location.c;
                            }
                            boolean10 = true;
                        }
                    }
                }
            }
        }
        return boolean10 ? Location.b : null;
    }
    
    private void w() {
        final double double1 = -0.03999999910593033;
        double double2 = this.isUnaffectedByGravity() ? 0.0 : -0.03999999910593033;
        double double3 = 0.0;
        this.at = 0.05f;
        if (this.lastLocation == Location.e && this.location != Location.e && this.location != Location.d) {
            this.aG = this.getBoundingBox().minY + this.getHeight();
            this.setPosition(this.x, this.k() - this.getHeight() + 0.101, this.z);
            this.setVelocity(this.getVelocity().multiply(1.0, 0.0, 1.0));
            this.aK = 0.0;
            this.location = Location.a;
        }
        else {
            if (this.location == Location.a) {
                double3 = (this.aG - this.getBoundingBox().minY) / this.getHeight();
                this.at = 0.9f;
            }
            else if (this.location == Location.c) {
                double2 = -7.0E-4;
                this.at = 0.9f;
            }
            else if (this.location == Location.b) {
                double3 = 0.009999999776482582;
                this.at = 0.45f;
            }
            else if (this.location == Location.e) {
                this.at = 0.9f;
            }
            else if (this.location == Location.d) {
                this.at = this.aH;
                if (this.getPrimaryPassenger() instanceof PlayerEntity) {
                    this.aH /= 2.0f;
                }
            }
            final Vec3d vec3d7 = this.getVelocity();
            this.setVelocity(vec3d7.x * this.at, vec3d7.y + double2, vec3d7.z * this.at);
            this.av *= this.at;
            if (double3 > 0.0) {
                final Vec3d vec3d8 = this.getVelocity();
                this.setVelocity(vec3d8.x, (vec3d8.y + double3 * 0.06153846016296973) * 0.75, vec3d8.z);
            }
        }
    }
    
    private void z() {
        if (!this.hasPassengers()) {
            return;
        }
        float float1 = 0.0f;
        if (this.aC) {
            --this.av;
        }
        if (this.aD) {
            ++this.av;
        }
        if (this.aD != this.aC && !this.aE && !this.aF) {
            float1 += 0.005f;
        }
        this.yaw += this.av;
        if (this.aE) {
            float1 += 0.04f;
        }
        if (this.aF) {
            float1 -= 0.005f;
        }
        this.setVelocity(this.getVelocity().add(MathHelper.sin(-this.yaw * 0.017453292f) * float1, 0.0, MathHelper.cos(this.yaw * 0.017453292f) * float1));
        this.setPaddleState((this.aD && !this.aC) || this.aE, (this.aC && !this.aD) || this.aE);
    }
    
    @Override
    public void updatePassengerPosition(final Entity passenger) {
        if (!this.hasPassenger(passenger)) {
            return;
        }
        float float2 = 0.0f;
        final float float3 = (float)((this.removed ? 0.009999999776482582 : this.getMountedHeightOffset()) + passenger.getHeightOffset());
        if (this.getPassengerList().size() > 1) {
            final int integer4 = this.getPassengerList().indexOf(passenger);
            if (integer4 == 0) {
                float2 = 0.2f;
            }
            else {
                float2 = -0.6f;
            }
            if (passenger instanceof AnimalEntity) {
                float2 += (float)0.2;
            }
        }
        final Vec3d vec3d4 = new Vec3d(float2, 0.0, 0.0).rotateY(-this.yaw * 0.017453292f - 1.5707964f);
        passenger.setPosition(this.x + vec3d4.x, this.y + float3, this.z + vec3d4.z);
        passenger.yaw += this.av;
        passenger.setHeadYaw(passenger.getHeadYaw() + this.av);
        this.copyEntityData(passenger);
        if (passenger instanceof AnimalEntity && this.getPassengerList().size() > 1) {
            final int integer5 = (passenger.getEntityId() % 2 == 0) ? 90 : 270;
            passenger.setYaw(((AnimalEntity)passenger).aK + integer5);
            passenger.setHeadYaw(passenger.getHeadYaw() + integer5);
        }
    }
    
    protected void copyEntityData(final Entity entity) {
        entity.setYaw(this.yaw);
        final float float2 = MathHelper.wrapDegrees(entity.yaw - this.yaw);
        final float float3 = MathHelper.clamp(float2, -105.0f, 105.0f);
        entity.prevYaw += float3 - float2;
        entity.setHeadYaw(entity.yaw += float3 - float2);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void onPassengerLookAround(final Entity passenger) {
        this.copyEntityData(passenger);
    }
    
    @Override
    protected void writeCustomDataToTag(final CompoundTag tag) {
        tag.putString("Type", this.getBoatType().getName());
    }
    
    @Override
    protected void readCustomDataFromTag(final CompoundTag tag) {
        if (tag.containsKey("Type", 8)) {
            this.setBoatType(Type.getType(tag.getString("Type")));
        }
    }
    
    @Override
    public boolean interact(final PlayerEntity player, final Hand hand) {
        if (player.isSneaking()) {
            return false;
        }
        if (!this.world.isClient && this.au < 60.0f) {
            player.startRiding(this);
        }
        return true;
    }
    
    @Override
    protected void fall(final double heightDifference, final boolean onGround, final BlockState blockState, final BlockPos blockPos) {
        this.aK = this.getVelocity().y;
        if (this.hasVehicle()) {
            return;
        }
        if (onGround) {
            if (this.fallDistance > 3.0f) {
                if (this.location != Location.d) {
                    this.fallDistance = 0.0f;
                    return;
                }
                this.handleFallDamage(this.fallDistance, 1.0f);
                if (!this.world.isClient && !this.removed) {
                    this.remove();
                    if (this.world.getGameRules().getBoolean("doEntityDrops")) {
                        for (int integer6 = 0; integer6 < 3; ++integer6) {
                            this.dropItem(this.getBoatType().getBaseBlock());
                        }
                        for (int integer6 = 0; integer6 < 2; ++integer6) {
                            this.dropItem(Items.jz);
                        }
                    }
                }
            }
            this.fallDistance = 0.0f;
        }
        else if (!this.world.getFluidState(new BlockPos(this).down()).matches(FluidTags.a) && heightDifference < 0.0) {
            this.fallDistance -= (float)heightDifference;
        }
    }
    
    public boolean getPaddleState(final int paddle) {
        return this.dataTracker.<Boolean>get((paddle == 0) ? BoatEntity.LEFT_PADDLE_MOVING : BoatEntity.RIGHT_PADDLE_MOVING) && this.getPrimaryPassenger() != null;
    }
    
    public void a(final float float1) {
        this.dataTracker.<Float>set(BoatEntity.d, float1);
    }
    
    public float m() {
        return this.dataTracker.<Float>get(BoatEntity.d);
    }
    
    public void b(final int integer) {
        this.dataTracker.<Integer>set(BoatEntity.b, integer);
    }
    
    public int n() {
        return this.dataTracker.<Integer>get(BoatEntity.b);
    }
    
    private void d(final int integer) {
        this.dataTracker.<Integer>set(BoatEntity.ar, integer);
    }
    
    private int A() {
        return this.dataTracker.<Integer>get(BoatEntity.ar);
    }
    
    @Environment(EnvType.CLIENT)
    public float b(final float float1) {
        return MathHelper.lerp(float1, this.aP, this.aO);
    }
    
    public void c(final int integer) {
        this.dataTracker.<Integer>set(BoatEntity.c, integer);
    }
    
    public int o() {
        return this.dataTracker.<Integer>get(BoatEntity.c);
    }
    
    public void setBoatType(final Type type) {
        this.dataTracker.<Integer>set(BoatEntity.BOAT_TYPE, type.ordinal());
    }
    
    public Type getBoatType() {
        return Type.getType(this.dataTracker.<Integer>get(BoatEntity.BOAT_TYPE));
    }
    
    @Override
    protected boolean canAddPassenger(final Entity entity) {
        return this.getPassengerList().size() < 2 && !this.isInFluid(FluidTags.a);
    }
    
    @Nullable
    @Override
    public Entity getPrimaryPassenger() {
        final List<Entity> list1 = this.getPassengerList();
        return list1.isEmpty() ? null : list1.get(0);
    }
    
    @Environment(EnvType.CLIENT)
    public void a(final boolean boolean1, final boolean boolean2, final boolean boolean3, final boolean boolean4) {
        this.aC = boolean1;
        this.aD = boolean2;
        this.aE = boolean3;
        this.aF = boolean4;
    }
    
    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
    
    static {
        b = DataTracker.<Integer>registerData(BoatEntity.class, TrackedDataHandlerRegistry.INTEGER);
        c = DataTracker.<Integer>registerData(BoatEntity.class, TrackedDataHandlerRegistry.INTEGER);
        d = DataTracker.<Float>registerData(BoatEntity.class, TrackedDataHandlerRegistry.FLOAT);
        BOAT_TYPE = DataTracker.<Integer>registerData(BoatEntity.class, TrackedDataHandlerRegistry.INTEGER);
        LEFT_PADDLE_MOVING = DataTracker.<Boolean>registerData(BoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        RIGHT_PADDLE_MOVING = DataTracker.<Boolean>registerData(BoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        ar = DataTracker.<Integer>registerData(BoatEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }
    
    public enum Location
    {
        a, 
        b, 
        c, 
        d, 
        e;
    }
    
    public enum Type
    {
        OAK(Blocks.n, "oak"), 
        SPRUCE(Blocks.o, "spruce"), 
        BIRCH(Blocks.p, "birch"), 
        JUNGLE(Blocks.q, "jungle"), 
        ACACIA(Blocks.r, "acacia"), 
        DARK_OAK(Blocks.s, "dark_oak");
        
        private final String name;
        private final Block baseBlock;
        
        private Type(final Block block, final String string2) {
            this.name = string2;
            this.baseBlock = block;
        }
        
        public String getName() {
            return this.name;
        }
        
        public Block getBaseBlock() {
            return this.baseBlock;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
        
        public static Type getType(int integer) {
            final Type[] arr2 = values();
            if (integer < 0 || integer >= arr2.length) {
                integer = 0;
            }
            return arr2[integer];
        }
        
        public static Type getType(final String string) {
            final Type[] arr2 = values();
            for (int integer3 = 0; integer3 < arr2.length; ++integer3) {
                if (arr2[integer3].getName().equals(string)) {
                    return arr2[integer3];
                }
            }
            return arr2[0];
        }
    }
}
