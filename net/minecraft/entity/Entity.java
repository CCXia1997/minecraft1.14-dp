package net.minecraft.entity;

import java.util.AbstractList;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import org.apache.logging.log4j.LogManager;
import net.minecraft.text.Style;
import net.minecraft.network.Packet;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.SoundCategory;
import net.minecraft.block.piston.PistonBehavior;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.ActionResult;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.event.HoverEvent;
import net.minecraft.scoreboard.Team;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Heightmap;
import java.util.Locale;
import java.util.function.Consumer;
import net.minecraft.text.event.ClickEvent;
import net.minecraft.scoreboard.AbstractTeam;
import com.google.common.collect.Iterables;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.world.IWorld;
import net.minecraft.block.PortalBlock;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.Hand;
import net.minecraft.item.ItemProvider;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.util.Identifier;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.RayTraceContext;
import net.minecraft.util.hit.HitResult;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;
import net.minecraft.particle.BlockStateParticleParameters;
import net.minecraft.block.BlockRenderType;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.tag.FluidTags;
import net.minecraft.entity.vehicle.BoatEntity;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.LoopingStream;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.Objects;
import net.minecraft.util.shape.VoxelShape;
import java.util.stream.Stream;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.shape.VoxelShapes;
import java.util.Arrays;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.BlockView;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.client.network.packet.EntityS2CPacket;
import com.google.common.collect.Sets;
import net.minecraft.util.math.MathHelper;
import com.google.common.collect.Lists;
import java.util.Set;
import java.util.UUID;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextComponent;
import java.util.Optional;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.DataTracker;
import java.util.Random;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.item.ItemStack;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.Logger;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.util.Nameable;

public abstract class Entity implements Nameable, CommandOutput
{
    protected static final Logger LOGGER;
    private static final AtomicInteger MAX_ENTITY_ID;
    private static final List<ItemStack> EMPTY_STACK_LIST;
    private static final BoundingBox NULL_BOX;
    private static double renderDistanceMultiplier;
    private final EntityType<?> type;
    private int entityId;
    public boolean i;
    private final List<Entity> passengerList;
    protected int ridingCooldown;
    private Entity vehicle;
    public boolean teleporting;
    public World world;
    public double prevX;
    public double prevY;
    public double prevZ;
    public double x;
    public double y;
    public double z;
    private Vec3d velocity;
    public float yaw;
    public float pitch;
    public float prevYaw;
    public float prevPitch;
    private BoundingBox boundingBox;
    public boolean onGround;
    public boolean horizontalCollision;
    public boolean verticalCollision;
    public boolean collided;
    public boolean velocityModified;
    protected Vec3d movementMultiplier;
    public boolean removed;
    public float D;
    public float E;
    public float F;
    public float fallDistance;
    private float av;
    private float aw;
    public double prevRenderX;
    public double prevRenderY;
    public double prevRenderZ;
    public float stepHeight;
    public boolean noClip;
    public float pushSpeedReduction;
    protected final Random random;
    public int age;
    private int fireTime;
    protected boolean insideWater;
    protected double waterHeight;
    protected boolean R;
    protected boolean S;
    public int T;
    protected boolean U;
    protected final DataTracker dataTracker;
    protected static final TrackedData<Byte> FLAGS;
    private static final TrackedData<Integer> BREATH;
    private static final TrackedData<Optional<TextComponent>> CUSTOM_NAME;
    private static final TrackedData<Boolean> NAME_VISIBLE;
    private static final TrackedData<Boolean> SILENT;
    private static final TrackedData<Boolean> NO_GRAVITY;
    protected static final TrackedData<EntityPose> POSE;
    public boolean Y;
    public int chunkX;
    public int chunkY;
    public int chunkZ;
    @Environment(EnvType.CLIENT)
    public long ac;
    @Environment(EnvType.CLIENT)
    public long ad;
    @Environment(EnvType.CLIENT)
    public long ae;
    public boolean ignoreCameraFrustum;
    public boolean velocityDirty;
    public int portalCooldown;
    protected boolean inPortal;
    protected int portalTime;
    public DimensionType dimension;
    protected BlockPos lastPortalPosition;
    protected Vec3d am;
    protected Direction an;
    private boolean invulnerable;
    protected UUID uuid;
    protected String uuidString;
    protected boolean glowing;
    private final Set<String> scoreboardTags;
    private boolean aF;
    private final double[] pistonMovementDelta;
    private long pistonMovementTick;
    private EntitySize size;
    private float standingEyeHeight;
    
    public Entity(final EntityType<?> type, final World world) {
        this.entityId = Entity.MAX_ENTITY_ID.incrementAndGet();
        this.passengerList = Lists.newArrayList();
        this.velocity = Vec3d.ZERO;
        this.boundingBox = Entity.NULL_BOX;
        this.movementMultiplier = Vec3d.ZERO;
        this.av = 1.0f;
        this.aw = 1.0f;
        this.random = new Random();
        this.fireTime = -this.cc();
        this.U = true;
        this.uuid = MathHelper.randomUuid(this.random);
        this.uuidString = this.uuid.toString();
        this.scoreboardTags = Sets.newHashSet();
        this.pistonMovementDelta = new double[] { 0.0, 0.0, 0.0 };
        this.type = type;
        this.world = world;
        this.size = type.getDefaultSize();
        this.setPosition(0.0, 0.0, 0.0);
        if (world != null) {
            this.dimension = world.dimension.getType();
        }
        (this.dataTracker = new DataTracker(this)).<Byte>startTracking(Entity.FLAGS, (Byte)0);
        this.dataTracker.<Integer>startTracking(Entity.BREATH, this.getMaxBreath());
        this.dataTracker.<Boolean>startTracking(Entity.NAME_VISIBLE, false);
        this.dataTracker.<Optional<TextComponent>>startTracking(Entity.CUSTOM_NAME, Optional.<TextComponent>empty());
        this.dataTracker.<Boolean>startTracking(Entity.SILENT, false);
        this.dataTracker.<Boolean>startTracking(Entity.NO_GRAVITY, false);
        this.dataTracker.<EntityPose>startTracking(Entity.POSE, EntityPose.a);
        this.initDataTracker();
        this.standingEyeHeight = this.getEyeHeight(EntityPose.a, this.size);
    }
    
    public boolean isSpectator() {
        return false;
    }
    
    public final void detach() {
        if (this.hasPassengers()) {
            this.removeAllPassengers();
        }
        if (this.hasVehicle()) {
            this.stopRiding();
        }
    }
    
    @Environment(EnvType.CLIENT)
    public void b(final double double1, final double double3, final double double5) {
        this.ac = EntityS2CPacket.a(double1);
        this.ad = EntityS2CPacket.a(double3);
        this.ae = EntityS2CPacket.a(double5);
    }
    
    public EntityType<?> getType() {
        return this.type;
    }
    
    public int getEntityId() {
        return this.entityId;
    }
    
    public void setEntityId(final int integer) {
        this.entityId = integer;
    }
    
    public Set<String> getScoreboardTags() {
        return this.scoreboardTags;
    }
    
    public boolean addScoreboardTag(final String string) {
        return this.scoreboardTags.size() < 1024 && this.scoreboardTags.add(string);
    }
    
    public boolean removeScoreboardTag(final String string) {
        return this.scoreboardTags.remove(string);
    }
    
    public void kill() {
        this.remove();
    }
    
    protected abstract void initDataTracker();
    
    public DataTracker getDataTracker() {
        return this.dataTracker;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof Entity && ((Entity)o).entityId == this.entityId;
    }
    
    @Override
    public int hashCode() {
        return this.entityId;
    }
    
    @Environment(EnvType.CLIENT)
    protected void X() {
        if (this.world == null) {
            return;
        }
        while (this.y > 0.0 && this.y < 256.0) {
            this.setPosition(this.x, this.y, this.z);
            if (this.world.doesNotCollide(this)) {
                break;
            }
            ++this.y;
        }
        this.setVelocity(Vec3d.ZERO);
        this.pitch = 0.0f;
    }
    
    public void remove() {
        this.removed = true;
    }
    
    protected void setPose(final EntityPose entityPose) {
        this.dataTracker.<EntityPose>set(Entity.POSE, entityPose);
    }
    
    public EntityPose getPose() {
        return this.dataTracker.<EntityPose>get(Entity.POSE);
    }
    
    protected void setRotation(final float yaw, final float float2) {
        this.yaw = yaw % 360.0f;
        this.pitch = float2 % 360.0f;
    }
    
    public void setPosition(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        final float float7 = this.size.width / 2.0f;
        final float float8 = this.size.height;
        this.setBoundingBox(new BoundingBox(x - float7, y, z - float7, x + float7, y + float8, z + float7));
    }
    
    @Environment(EnvType.CLIENT)
    public void changeLookDirection(final double cursorDeltaX, final double cursorDeltaY) {
        final double double5 = cursorDeltaY * 0.15;
        final double double6 = cursorDeltaX * 0.15;
        this.pitch += (float)double5;
        this.yaw += (float)double6;
        this.pitch = MathHelper.clamp(this.pitch, -90.0f, 90.0f);
        this.prevPitch += (float)double5;
        this.prevYaw += (float)double6;
        this.prevPitch = MathHelper.clamp(this.prevPitch, -90.0f, 90.0f);
        if (this.vehicle != null) {
            this.vehicle.onPassengerLookAround(this);
        }
    }
    
    public void tick() {
        if (!this.world.isClient) {
            this.setFlag(6, this.isGlowing());
        }
        this.baseTick();
    }
    
    public void baseTick() {
        this.world.getProfiler().push("entityBaseTick");
        if (this.hasVehicle() && this.getVehicle().removed) {
            this.stopRiding();
        }
        if (this.ridingCooldown > 0) {
            --this.ridingCooldown;
        }
        this.D = this.E;
        this.prevX = this.x;
        this.prevY = this.y;
        this.prevZ = this.z;
        this.prevPitch = this.pitch;
        this.prevYaw = this.yaw;
        this.tickPortal();
        this.attemptSprintingParticles();
        this.m();
        if (this.world.isClient) {
            this.extinguish();
        }
        else if (this.fireTime > 0) {
            if (this.isFireImmune()) {
                this.fireTime -= 4;
                if (this.fireTime < 0) {
                    this.extinguish();
                }
            }
            else {
                if (this.fireTime % 20 == 0) {
                    this.damage(DamageSource.ON_FIRE, 1.0f);
                }
                --this.fireTime;
            }
        }
        if (this.isTouchingLava()) {
            this.setOnFireFromLava();
            this.fallDistance *= 0.5f;
        }
        if (this.y < -64.0) {
            this.destroy();
        }
        if (!this.world.isClient) {
            this.setFlag(0, this.fireTime > 0);
        }
        this.U = false;
        this.world.getProfiler().pop();
    }
    
    protected void tickPortalCooldown() {
        if (this.portalCooldown > 0) {
            --this.portalCooldown;
        }
    }
    
    public int getMaxPortalTime() {
        return 1;
    }
    
    protected void setOnFireFromLava() {
        if (this.isFireImmune()) {
            return;
        }
        this.setOnFireFor(15);
        this.damage(DamageSource.LAVA, 4.0f);
    }
    
    public void setOnFireFor(final int seconds) {
        int integer2 = seconds * 20;
        if (this instanceof LivingEntity) {
            integer2 = ProtectionEnchantment.transformFireDuration((LivingEntity)this, integer2);
        }
        if (this.fireTime < integer2) {
            this.fireTime = integer2;
        }
    }
    
    public void extinguish() {
        this.fireTime = 0;
    }
    
    protected void destroy() {
        this.remove();
    }
    
    public boolean doesNotCollide(final double offsetX, final double offsetY, final double offsetZ) {
        return this.doesNotCollide(this.getBoundingBox().offset(offsetX, offsetY, offsetZ));
    }
    
    private boolean doesNotCollide(final BoundingBox box) {
        return this.world.doesNotCollide(this, box) && !this.world.intersectsFluid(box);
    }
    
    public void move(final MovementType type, Vec3d offset) {
        if (this.noClip) {
            this.setBoundingBox(this.getBoundingBox().offset(offset));
            this.moveToBoundingBoxCenter();
            return;
        }
        if (type == MovementType.c) {
            offset = this.a(offset);
            if (offset.equals(Vec3d.ZERO)) {
                return;
            }
        }
        this.world.getProfiler().push("move");
        if (this.movementMultiplier.lengthSquared() > 1.0E-7) {
            offset = offset.multiply(this.movementMultiplier);
            this.movementMultiplier = Vec3d.ZERO;
            this.setVelocity(Vec3d.ZERO);
        }
        offset = this.clipSneakingMovement(offset, type);
        final Vec3d vec3d3 = this.e(offset);
        if (vec3d3.lengthSquared() > 1.0E-7) {
            this.setBoundingBox(this.getBoundingBox().offset(vec3d3));
            this.moveToBoundingBoxCenter();
        }
        this.world.getProfiler().pop();
        this.world.getProfiler().push("rest");
        this.horizontalCollision = (!MathHelper.b(offset.x, vec3d3.x) || !MathHelper.b(offset.z, vec3d3.z));
        this.verticalCollision = (offset.y != vec3d3.y);
        this.onGround = (this.verticalCollision && offset.y < 0.0);
        this.collided = (this.horizontalCollision || this.verticalCollision);
        final int integer4 = MathHelper.floor(this.x);
        final int integer5 = MathHelper.floor(this.y - 0.20000000298023224);
        final int integer6 = MathHelper.floor(this.z);
        BlockPos blockPos7 = new BlockPos(integer4, integer5, integer6);
        BlockState blockState8 = this.world.getBlockState(blockPos7);
        if (blockState8.isAir()) {
            final BlockPos blockPos8 = blockPos7.down();
            final BlockState blockState9 = this.world.getBlockState(blockPos8);
            final Block block11 = blockState9.getBlock();
            if (block11.matches(BlockTags.G) || block11.matches(BlockTags.z) || block11 instanceof FenceGateBlock) {
                blockState8 = blockState9;
                blockPos7 = blockPos8;
            }
        }
        this.fall(vec3d3.y, this.onGround, blockState8, blockPos7);
        final Vec3d vec3d4 = this.getVelocity();
        if (offset.x != vec3d3.x) {
            this.setVelocity(0.0, vec3d4.y, vec3d4.z);
        }
        if (offset.z != vec3d3.z) {
            this.setVelocity(vec3d4.x, vec3d4.y, 0.0);
        }
        final Block block12 = blockState8.getBlock();
        if (offset.y != vec3d3.y) {
            block12.onEntityLand(this.world, this);
        }
        if (this.canClimb() && (!this.onGround || !this.isSneaking() || !(this instanceof PlayerEntity)) && !this.hasVehicle()) {
            final double double11 = vec3d3.x;
            double double12 = vec3d3.y;
            final double double13 = vec3d3.z;
            if (block12 != Blocks.ce && block12 != Blocks.lI) {
                double12 = 0.0;
            }
            if (this.onGround) {
                block12.onSteppedOn(this.world, blockPos7, this);
            }
            this.E += (float)(MathHelper.sqrt(squaredHorizontalLength(vec3d3)) * 0.6);
            this.F += (float)(MathHelper.sqrt(double11 * double11 + double12 * double12 + double13 * double13) * 0.6);
            if (this.F > this.av && !blockState8.isAir()) {
                this.av = this.af();
                if (this.isInsideWater()) {
                    final Entity entity17 = (this.hasPassengers() && this.getPrimaryPassenger() != null) ? this.getPrimaryPassenger() : this;
                    final float float18 = (entity17 == this) ? 0.35f : 0.4f;
                    final Vec3d vec3d5 = entity17.getVelocity();
                    float float19 = MathHelper.sqrt(vec3d5.x * vec3d5.x * 0.20000000298023224 + vec3d5.y * vec3d5.y + vec3d5.z * vec3d5.z * 0.20000000298023224) * float18;
                    if (float19 > 1.0f) {
                        float19 = 1.0f;
                    }
                    this.playSwimSound(float19);
                }
                else {
                    this.playStepSound(blockPos7, blockState8);
                }
            }
            else if (this.F > this.aw && this.al() && blockState8.isAir()) {
                this.aw = this.e(this.F);
            }
        }
        try {
            this.S = false;
            this.checkBlockCollision();
        }
        catch (Throwable throwable11) {
            final CrashReport crashReport12 = CrashReport.create(throwable11, "Checking entity block collision");
            final CrashReportSection crashReportSection13 = crashReport12.addElement("Entity being checked for collision");
            this.populateCrashReport(crashReportSection13);
            throw new CrashException(crashReport12);
        }
        final boolean boolean11 = this.isTouchingWater();
        if (this.world.doesAreaContainFireSource(this.getBoundingBox().contract(0.001))) {
            if (!boolean11) {
                ++this.fireTime;
                if (this.fireTime == 0) {
                    this.setOnFireFor(8);
                }
            }
            this.burn(1);
        }
        else if (this.fireTime <= 0) {
            this.fireTime = -this.cc();
        }
        if (boolean11 && this.isOnFire()) {
            this.playSound(SoundEvents.dK, 0.7f, 1.6f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
            this.fireTime = -this.cc();
        }
        this.world.getProfiler().pop();
    }
    
    protected Vec3d clipSneakingMovement(Vec3d offset, final MovementType type) {
        if (this instanceof PlayerEntity && (type == MovementType.a || type == MovementType.b) && this.onGround && this.isSneaking()) {
            double double3 = offset.x;
            double double4 = offset.z;
            final double double5 = 0.05;
            while (double3 != 0.0 && this.world.doesNotCollide(this, this.getBoundingBox().offset(double3, -this.stepHeight, 0.0))) {
                if (double3 < 0.05 && double3 >= -0.05) {
                    double3 = 0.0;
                }
                else if (double3 > 0.0) {
                    double3 -= 0.05;
                }
                else {
                    double3 += 0.05;
                }
            }
            while (double4 != 0.0 && this.world.doesNotCollide(this, this.getBoundingBox().offset(0.0, -this.stepHeight, double4))) {
                if (double4 < 0.05 && double4 >= -0.05) {
                    double4 = 0.0;
                }
                else if (double4 > 0.0) {
                    double4 -= 0.05;
                }
                else {
                    double4 += 0.05;
                }
            }
            while (double3 != 0.0 && double4 != 0.0 && this.world.doesNotCollide(this, this.getBoundingBox().offset(double3, -this.stepHeight, double4))) {
                if (double3 < 0.05 && double3 >= -0.05) {
                    double3 = 0.0;
                }
                else if (double3 > 0.0) {
                    double3 -= 0.05;
                }
                else {
                    double3 += 0.05;
                }
                if (double4 < 0.05 && double4 >= -0.05) {
                    double4 = 0.0;
                }
                else if (double4 > 0.0) {
                    double4 -= 0.05;
                }
                else {
                    double4 += 0.05;
                }
            }
            offset = new Vec3d(double3, offset.y, double4);
        }
        return offset;
    }
    
    protected Vec3d a(final Vec3d vec3d) {
        if (vec3d.lengthSquared() <= 1.0E-7) {
            return vec3d;
        }
        final long long2 = this.world.getTime();
        if (long2 != this.pistonMovementTick) {
            Arrays.fill(this.pistonMovementDelta, 0.0);
            this.pistonMovementTick = long2;
        }
        if (vec3d.x != 0.0) {
            final double double4 = this.a(Direction.Axis.X, vec3d.x);
            return (Math.abs(double4) <= 9.999999747378752E-6) ? Vec3d.ZERO : new Vec3d(double4, 0.0, 0.0);
        }
        if (vec3d.y != 0.0) {
            final double double4 = this.a(Direction.Axis.Y, vec3d.y);
            return (Math.abs(double4) <= 9.999999747378752E-6) ? Vec3d.ZERO : new Vec3d(0.0, double4, 0.0);
        }
        if (vec3d.z != 0.0) {
            final double double4 = this.a(Direction.Axis.Z, vec3d.z);
            return (Math.abs(double4) <= 9.999999747378752E-6) ? Vec3d.ZERO : new Vec3d(0.0, 0.0, double4);
        }
        return Vec3d.ZERO;
    }
    
    private double a(final Direction.Axis axis, double double2) {
        final int integer4 = axis.ordinal();
        final double double3 = MathHelper.clamp(double2 + this.pistonMovementDelta[integer4], -0.51, 0.51);
        double2 = double3 - this.pistonMovementDelta[integer4];
        this.pistonMovementDelta[integer4] = double3;
        return double2;
    }
    
    private Vec3d e(final Vec3d vec3d) {
        final BoundingBox boundingBox2 = this.getBoundingBox();
        final VerticalEntityPosition verticalEntityPosition3 = VerticalEntityPosition.fromEntity(this);
        final VoxelShape voxelShape4 = this.world.getWorldBorder().asVoxelShape();
        final Stream<VoxelShape> stream5 = VoxelShapes.matchesAnywhere(voxelShape4, VoxelShapes.cuboid(boundingBox2.contract(1.0E-7)), BooleanBiFunction.AND) ? Stream.<VoxelShape>empty() : Stream.<VoxelShape>of(voxelShape4);
        final BoundingBox boundingBox3 = boundingBox2.stretch(vec3d).expand(1.0E-7);
        final Stream<VoxelShape> stream6 = this.world.getEntities(this, boundingBox3).stream().filter(entity -> !this.isConnectedThroughVehicle(entity)).flatMap(entity -> Stream.<BoundingBox>of(new BoundingBox[] { entity.ap(), this.j(entity) })).filter(Objects::nonNull).filter(boundingBox3::intersects).<VoxelShape>map(VoxelShapes::cuboid);
        final LoopingStream<VoxelShape> loopingStream8 = new LoopingStream<VoxelShape>(Stream.<VoxelShape>concat(stream6, stream5));
        final Vec3d vec3d2 = (vec3d.lengthSquared() == 0.0) ? vec3d : a(vec3d, boundingBox2, this.world, verticalEntityPosition3, loopingStream8);
        final boolean boolean10 = vec3d.x != vec3d2.x;
        final boolean boolean11 = vec3d.y != vec3d2.y;
        final boolean boolean12 = vec3d.z != vec3d2.z;
        final boolean boolean13 = this.onGround || (boolean11 && vec3d.y < 0.0);
        if (this.stepHeight > 0.0f && boolean13 && (boolean10 || boolean12)) {
            Vec3d vec3d3 = a(new Vec3d(vec3d.x, this.stepHeight, vec3d.z), boundingBox2, this.world, verticalEntityPosition3, loopingStream8);
            final Vec3d vec3d4 = a(new Vec3d(0.0, this.stepHeight, 0.0), boundingBox2.stretch(vec3d.x, 0.0, vec3d.z), this.world, verticalEntityPosition3, loopingStream8);
            if (vec3d4.y < this.stepHeight) {
                final Vec3d vec3d5 = a(new Vec3d(vec3d.x, 0.0, vec3d.z), boundingBox2.offset(vec3d4), this.world, verticalEntityPosition3, loopingStream8).add(vec3d4);
                if (squaredHorizontalLength(vec3d5) > squaredHorizontalLength(vec3d3)) {
                    vec3d3 = vec3d5;
                }
            }
            if (squaredHorizontalLength(vec3d3) > squaredHorizontalLength(vec3d2)) {
                return vec3d3.add(a(new Vec3d(0.0, -vec3d3.y + vec3d.y, 0.0), boundingBox2.offset(vec3d3), this.world, verticalEntityPosition3, loopingStream8));
            }
        }
        return vec3d2;
    }
    
    public static double squaredHorizontalLength(final Vec3d vec3d) {
        return vec3d.x * vec3d.x + vec3d.z * vec3d.z;
    }
    
    public static Vec3d a(final Vec3d vec3d, BoundingBox boundingBox, final ViewableWorld viewableWorld, final VerticalEntityPosition verticalEntityPosition, final LoopingStream<VoxelShape> loopingStream) {
        double double6 = vec3d.x;
        double double7 = vec3d.y;
        double double8 = vec3d.z;
        if (double7 != 0.0) {
            double7 = VoxelShapes.a(Direction.Axis.Y, boundingBox, viewableWorld, double7, verticalEntityPosition, loopingStream.getStream());
            if (double7 != 0.0) {
                boundingBox = boundingBox.offset(0.0, double7, 0.0);
            }
        }
        final boolean boolean12 = Math.abs(double6) < Math.abs(double8);
        if (boolean12 && double8 != 0.0) {
            double8 = VoxelShapes.a(Direction.Axis.Z, boundingBox, viewableWorld, double8, verticalEntityPosition, loopingStream.getStream());
            if (double8 != 0.0) {
                boundingBox = boundingBox.offset(0.0, 0.0, double8);
            }
        }
        if (double6 != 0.0) {
            double6 = VoxelShapes.a(Direction.Axis.X, boundingBox, viewableWorld, double6, verticalEntityPosition, loopingStream.getStream());
            if (!boolean12 && double6 != 0.0) {
                boundingBox = boundingBox.offset(double6, 0.0, 0.0);
            }
        }
        if (!boolean12 && double8 != 0.0) {
            double8 = VoxelShapes.a(Direction.Axis.Z, boundingBox, viewableWorld, double8, verticalEntityPosition, loopingStream.getStream());
        }
        return new Vec3d(double6, double7, double8);
    }
    
    protected float af() {
        return (float)((int)this.F + 1);
    }
    
    public void moveToBoundingBoxCenter() {
        final BoundingBox boundingBox1 = this.getBoundingBox();
        this.x = (boundingBox1.minX + boundingBox1.maxX) / 2.0;
        this.y = boundingBox1.minY;
        this.z = (boundingBox1.minZ + boundingBox1.maxZ) / 2.0;
    }
    
    protected SoundEvent getSwimSound() {
        return SoundEvents.dO;
    }
    
    protected SoundEvent getSplashSound() {
        return SoundEvents.dN;
    }
    
    protected SoundEvent getHighSpeedSplashSound() {
        return SoundEvents.dN;
    }
    
    protected void checkBlockCollision() {
        final BoundingBox boundingBox1 = this.getBoundingBox();
        try (final BlockPos.PooledMutable pooledMutable2 = BlockPos.PooledMutable.get(boundingBox1.minX + 0.001, boundingBox1.minY + 0.001, boundingBox1.minZ + 0.001);
             final BlockPos.PooledMutable pooledMutable3 = BlockPos.PooledMutable.get(boundingBox1.maxX - 0.001, boundingBox1.maxY - 0.001, boundingBox1.maxZ - 0.001);
             final BlockPos.PooledMutable pooledMutable4 = BlockPos.PooledMutable.get()) {
            if (this.world.isAreaLoaded(pooledMutable2, pooledMutable3)) {
                for (int integer8 = pooledMutable2.getX(); integer8 <= pooledMutable3.getX(); ++integer8) {
                    for (int integer9 = pooledMutable2.getY(); integer9 <= pooledMutable3.getY(); ++integer9) {
                        for (int integer10 = pooledMutable2.getZ(); integer10 <= pooledMutable3.getZ(); ++integer10) {
                            pooledMutable4.set(integer8, integer9, integer10);
                            final BlockState blockState11 = this.world.getBlockState(pooledMutable4);
                            try {
                                blockState11.onEntityCollision(this.world, pooledMutable4, this);
                                this.onBlockCollision(blockState11);
                            }
                            catch (Throwable throwable12) {
                                final CrashReport crashReport13 = CrashReport.create(throwable12, "Colliding entity with block");
                                final CrashReportSection crashReportSection14 = crashReport13.addElement("Block being collided with");
                                CrashReportSection.addBlockInfo(crashReportSection14, pooledMutable4, blockState11);
                                throw new CrashException(crashReport13);
                            }
                        }
                    }
                }
            }
        }
    }
    
    protected void onBlockCollision(final BlockState blockState) {
    }
    
    protected void playStepSound(final BlockPos pos, final BlockState state) {
        if (state.getMaterial().isLiquid()) {
            return;
        }
        final BlockState blockState3 = this.world.getBlockState(pos.up());
        final BlockSoundGroup blockSoundGroup4 = (blockState3.getBlock() == Blocks.cA) ? blockState3.getSoundGroup() : state.getSoundGroup();
        this.playSound(blockSoundGroup4.getStepSound(), blockSoundGroup4.getVolume() * 0.15f, blockSoundGroup4.getPitch());
    }
    
    protected void playSwimSound(final float volume) {
        this.playSound(this.getSwimSound(), volume, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
    }
    
    protected float e(final float float1) {
        return 0.0f;
    }
    
    protected boolean al() {
        return false;
    }
    
    public void playSound(final SoundEvent sound, final float volume, final float float3) {
        if (!this.isSilent()) {
            this.world.playSound(null, this.x, this.y, this.z, sound, this.getSoundCategory(), volume, float3);
        }
    }
    
    public boolean isSilent() {
        return this.dataTracker.<Boolean>get(Entity.SILENT);
    }
    
    public void setSilent(final boolean boolean1) {
        this.dataTracker.<Boolean>set(Entity.SILENT, boolean1);
    }
    
    public boolean isUnaffectedByGravity() {
        return this.dataTracker.<Boolean>get(Entity.NO_GRAVITY);
    }
    
    public void setUnaffectedByGravity(final boolean boolean1) {
        this.dataTracker.<Boolean>set(Entity.NO_GRAVITY, boolean1);
    }
    
    protected boolean canClimb() {
        return true;
    }
    
    protected void fall(final double heightDifference, final boolean onGround, final BlockState blockState, final BlockPos blockPos) {
        if (onGround) {
            if (this.fallDistance > 0.0f) {
                blockState.getBlock().onLandedUpon(this.world, blockPos, this, this.fallDistance);
            }
            this.fallDistance = 0.0f;
        }
        else if (heightDifference < 0.0) {
            this.fallDistance -= (float)heightDifference;
        }
    }
    
    @Nullable
    public BoundingBox ap() {
        return null;
    }
    
    protected void burn(final int integer) {
        if (!this.isFireImmune()) {
            this.damage(DamageSource.IN_FIRE, (float)integer);
        }
    }
    
    public final boolean isFireImmune() {
        return this.getType().isFireImmune();
    }
    
    public void handleFallDamage(final float fallDistance, final float damageMultiplier) {
        if (this.hasPassengers()) {
            for (final Entity entity4 : this.getPassengerList()) {
                entity4.handleFallDamage(fallDistance, damageMultiplier);
            }
        }
    }
    
    public boolean isInsideWater() {
        return this.insideWater;
    }
    
    private boolean isBeingRainedOn() {
        try (final BlockPos.PooledMutable pooledMutable1 = BlockPos.PooledMutable.getEntityPos(this)) {
            return this.world.hasRain(pooledMutable1) || this.world.hasRain(pooledMutable1.set(this.x, this.y + this.size.height, this.z));
        }
    }
    
    private boolean isInsideBubbleColumn() {
        return this.world.getBlockState(new BlockPos(this)).getBlock() == Blocks.kU;
    }
    
    public boolean isInsideWaterOrRain() {
        return this.isInsideWater() || this.isBeingRainedOn();
    }
    
    public boolean isTouchingWater() {
        return this.isInsideWater() || this.isBeingRainedOn() || this.isInsideBubbleColumn();
    }
    
    public boolean isInsideWaterOrBubbleColumn() {
        return this.isInsideWater() || this.isInsideBubbleColumn();
    }
    
    public boolean isInWater() {
        return this.R && this.isInsideWater();
    }
    
    private void m() {
        this.ax();
        this.n();
        this.updateSwimming();
    }
    
    public void updateSwimming() {
        if (this.isSwimming()) {
            this.setSwimming(this.isSprinting() && this.isInsideWater() && !this.hasVehicle());
        }
        else {
            this.setSwimming(this.isSprinting() && this.isInWater() && !this.hasVehicle());
        }
    }
    
    public boolean ax() {
        if (this.getVehicle() instanceof BoatEntity) {
            this.insideWater = false;
        }
        else if (this.updateMovementInFluid(FluidTags.a)) {
            if (!this.insideWater && !this.U) {
                this.onSwimmingStart();
            }
            this.fallDistance = 0.0f;
            this.insideWater = true;
            this.extinguish();
        }
        else {
            this.insideWater = false;
        }
        return this.insideWater;
    }
    
    private void n() {
        this.R = this.isInFluid(FluidTags.a, true);
    }
    
    protected void onSwimmingStart() {
        final Entity entity1 = (this.hasPassengers() && this.getPrimaryPassenger() != null) ? this.getPrimaryPassenger() : this;
        final float float2 = (entity1 == this) ? 0.2f : 0.9f;
        final Vec3d vec3d3 = entity1.getVelocity();
        float float3 = MathHelper.sqrt(vec3d3.x * vec3d3.x * 0.20000000298023224 + vec3d3.y * vec3d3.y + vec3d3.z * vec3d3.z * 0.20000000298023224) * float2;
        if (float3 > 1.0f) {
            float3 = 1.0f;
        }
        if (float3 < 0.25) {
            this.playSound(this.getSplashSound(), float3, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
        }
        else {
            this.playSound(this.getHighSpeedSplashSound(), float3, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
        }
        final float float4 = (float)MathHelper.floor(this.getBoundingBox().minY);
        for (int integer6 = 0; integer6 < 1.0f + this.size.width * 20.0f; ++integer6) {
            final float float5 = (this.random.nextFloat() * 2.0f - 1.0f) * this.size.width;
            final float float6 = (this.random.nextFloat() * 2.0f - 1.0f) * this.size.width;
            this.world.addParticle(ParticleTypes.e, this.x + float5, float4 + 1.0f, this.z + float6, vec3d3.x, vec3d3.y - this.random.nextFloat() * 0.2f, vec3d3.z);
        }
        for (int integer6 = 0; integer6 < 1.0f + this.size.width * 20.0f; ++integer6) {
            final float float5 = (this.random.nextFloat() * 2.0f - 1.0f) * this.size.width;
            final float float6 = (this.random.nextFloat() * 2.0f - 1.0f) * this.size.width;
            this.world.addParticle(ParticleTypes.X, this.x + float5, float4 + 1.0f, this.z + float6, vec3d3.x, vec3d3.y, vec3d3.z);
        }
    }
    
    public void attemptSprintingParticles() {
        if (this.isSprinting() && !this.isInsideWater()) {
            this.spawnSprintingParticles();
        }
    }
    
    protected void spawnSprintingParticles() {
        final int integer1 = MathHelper.floor(this.x);
        final int integer2 = MathHelper.floor(this.y - 0.20000000298023224);
        final int integer3 = MathHelper.floor(this.z);
        final BlockPos blockPos4 = new BlockPos(integer1, integer2, integer3);
        final BlockState blockState5 = this.world.getBlockState(blockPos4);
        if (blockState5.getRenderType() != BlockRenderType.a) {
            final Vec3d vec3d6 = this.getVelocity();
            this.world.addParticle(new BlockStateParticleParameters(ParticleTypes.d, blockState5), this.x + (this.random.nextFloat() - 0.5) * this.size.width, this.y + 0.1, this.z + (this.random.nextFloat() - 0.5) * this.size.width, vec3d6.x * -4.0, 1.5, vec3d6.z * -4.0);
        }
    }
    
    public boolean isInFluid(final Tag<Fluid> tag) {
        return this.isInFluid(tag, false);
    }
    
    public boolean isInFluid(final Tag<Fluid> tag, final boolean requireLoadedChunk) {
        if (this.getVehicle() instanceof BoatEntity) {
            return false;
        }
        final double double3 = this.y + this.getStandingEyeHeight();
        final BlockPos blockPos5 = new BlockPos(this.x, double3, this.z);
        if (requireLoadedChunk && !this.world.isChunkLoaded(blockPos5.getX() >> 4, blockPos5.getZ() >> 4)) {
            return false;
        }
        final FluidState fluidState6 = this.world.getFluidState(blockPos5);
        return fluidState6.matches(tag) && double3 < blockPos5.getY() + (fluidState6.getHeight(this.world, blockPos5) + 0.11111111f);
    }
    
    public void aB() {
        this.S = true;
    }
    
    public boolean isTouchingLava() {
        return this.S;
    }
    
    public void updateVelocity(final float speed, final Vec3d movementInput) {
        final Vec3d vec3d3 = movementInputToVelocity(movementInput, speed, this.yaw);
        this.setVelocity(this.getVelocity().add(vec3d3));
    }
    
    protected static Vec3d movementInputToVelocity(final Vec3d movementInput, final float speed, final float yaw) {
        final double double4 = movementInput.lengthSquared();
        if (double4 < 1.0E-7) {
            return Vec3d.ZERO;
        }
        final Vec3d vec3d6 = ((double4 > 1.0) ? movementInput.normalize() : movementInput).multiply(speed);
        final float float7 = MathHelper.sin(yaw * 0.017453292f);
        final float float8 = MathHelper.cos(yaw * 0.017453292f);
        return new Vec3d(vec3d6.x * float8 - vec3d6.z * float7, vec3d6.y, vec3d6.z * float8 + vec3d6.x * float7);
    }
    
    @Environment(EnvType.CLIENT)
    public int getLightmapCoordinates() {
        final BlockPos blockPos1 = new BlockPos(this.x, this.y + this.getStandingEyeHeight(), this.z);
        if (this.world.isBlockLoaded(blockPos1)) {
            return this.world.getLightmapIndex(blockPos1, 0);
        }
        return 0;
    }
    
    public float getBrightnessAtEyes() {
        final BlockPos.Mutable mutable1 = new BlockPos.Mutable(MathHelper.floor(this.x), 0, MathHelper.floor(this.z));
        if (this.world.isBlockLoaded(mutable1)) {
            mutable1.setY(MathHelper.floor(this.y + this.getStandingEyeHeight()));
            return this.world.getBrightness(mutable1);
        }
        return 0.0f;
    }
    
    public void setWorld(final World world) {
        this.world = world;
    }
    
    public void setPositionAnglesAndUpdate(final double x, final double y, final double z, final float yaw, float pitch) {
        this.x = MathHelper.clamp(x, -3.0E7, 3.0E7);
        this.y = y;
        this.z = MathHelper.clamp(z, -3.0E7, 3.0E7);
        this.prevX = this.x;
        this.prevY = this.y;
        this.prevZ = this.z;
        pitch = MathHelper.clamp(pitch, -90.0f, 90.0f);
        this.yaw = yaw;
        this.pitch = pitch;
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
        final double double9 = this.prevYaw - yaw;
        if (double9 < -180.0) {
            this.prevYaw += 360.0f;
        }
        if (double9 >= 180.0) {
            this.prevYaw -= 360.0f;
        }
        this.setPosition(this.x, this.y, this.z);
        this.setRotation(yaw, pitch);
    }
    
    public void setPositionAndAngles(final BlockPos pos, final float yaw, final float pitch) {
        this.setPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, yaw, pitch);
    }
    
    public void setPositionAndAngles(final double x, final double y, final double z, final float yaw, final float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.prevX = this.x;
        this.prevY = this.y;
        this.prevZ = this.z;
        this.prevRenderX = this.x;
        this.prevRenderY = this.y;
        this.prevRenderZ = this.z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.setPosition(this.x, this.y, this.z);
    }
    
    public float distanceTo(final Entity entity) {
        final float float2 = (float)(this.x - entity.x);
        final float float3 = (float)(this.y - entity.y);
        final float float4 = (float)(this.z - entity.z);
        return MathHelper.sqrt(float2 * float2 + float3 * float3 + float4 * float4);
    }
    
    public double squaredDistanceTo(final double x, final double double3, final double double5) {
        final double double6 = this.x - x;
        final double double7 = this.y - double3;
        final double double8 = this.z - double5;
        return double6 * double6 + double7 * double7 + double8 * double8;
    }
    
    public double squaredDistanceTo(final Entity entity) {
        return this.squaredDistanceTo(entity.getPos());
    }
    
    public double squaredDistanceTo(final Vec3d vec3d) {
        final double double2 = this.x - vec3d.x;
        final double double3 = this.y - vec3d.y;
        final double double4 = this.z - vec3d.z;
        return double2 * double2 + double3 * double3 + double4 * double4;
    }
    
    public void onPlayerCollision(final PlayerEntity playerEntity) {
    }
    
    public void pushAwayFrom(final Entity entity) {
        if (this.isConnectedThroughVehicle(entity)) {
            return;
        }
        if (entity.noClip || this.noClip) {
            return;
        }
        double double2 = entity.x - this.x;
        double double3 = entity.z - this.z;
        double double4 = MathHelper.absMax(double2, double3);
        if (double4 >= 0.009999999776482582) {
            double4 = MathHelper.sqrt(double4);
            double2 /= double4;
            double3 /= double4;
            double double5 = 1.0 / double4;
            if (double5 > 1.0) {
                double5 = 1.0;
            }
            double2 *= double5;
            double3 *= double5;
            double2 *= 0.05000000074505806;
            double3 *= 0.05000000074505806;
            double2 *= 1.0f - this.pushSpeedReduction;
            double3 *= 1.0f - this.pushSpeedReduction;
            if (!this.hasPassengers()) {
                this.addVelocity(-double2, 0.0, -double3);
            }
            if (!entity.hasPassengers()) {
                entity.addVelocity(double2, 0.0, double3);
            }
        }
    }
    
    public void addVelocity(final double deltaX, final double deltaY, final double deltaZ) {
        this.setVelocity(this.getVelocity().add(deltaX, deltaY, deltaZ));
        this.velocityDirty = true;
    }
    
    protected void scheduleVelocityUpdate() {
        this.velocityModified = true;
    }
    
    public boolean damage(final DamageSource source, final float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        this.scheduleVelocityUpdate();
        return false;
    }
    
    public final Vec3d getRotationVec(final float tickDelta) {
        return this.getRotationVector(this.getPitch(tickDelta), this.getYaw(tickDelta));
    }
    
    public float getPitch(final float tickDelta) {
        if (tickDelta == 1.0f) {
            return this.pitch;
        }
        return MathHelper.lerp(tickDelta, this.prevPitch, this.pitch);
    }
    
    public float getYaw(final float tickDelta) {
        if (tickDelta == 1.0f) {
            return this.yaw;
        }
        return MathHelper.lerp(tickDelta, this.prevYaw, this.yaw);
    }
    
    protected final Vec3d getRotationVector(final float pitch, final float yaw) {
        final float float3 = pitch * 0.017453292f;
        final float float4 = -yaw * 0.017453292f;
        final float float5 = MathHelper.cos(float4);
        final float float6 = MathHelper.sin(float4);
        final float float7 = MathHelper.cos(float3);
        final float float8 = MathHelper.sin(float3);
        return new Vec3d(float6 * float7, -float8, float5 * float7);
    }
    
    public final Vec3d getOppositeRotationVector(final float tickDelta) {
        return this.getOppositeRotationVector(this.getPitch(tickDelta), this.getYaw(tickDelta));
    }
    
    protected final Vec3d getOppositeRotationVector(final float pitch, final float yaw) {
        return this.getRotationVector(pitch - 90.0f, yaw);
    }
    
    public Vec3d getCameraPosVec(final float tickDelta) {
        if (tickDelta == 1.0f) {
            return new Vec3d(this.x, this.y + this.getStandingEyeHeight(), this.z);
        }
        final double double2 = MathHelper.lerp(tickDelta, this.prevX, this.x);
        final double double3 = MathHelper.lerp(tickDelta, this.prevY, this.y) + this.getStandingEyeHeight();
        final double double4 = MathHelper.lerp(tickDelta, this.prevZ, this.z);
        return new Vec3d(double2, double3, double4);
    }
    
    @Environment(EnvType.CLIENT)
    public HitResult rayTrace(final double maxDistance, final float tickDelta, final boolean boolean4) {
        final Vec3d vec3d5 = this.getCameraPosVec(tickDelta);
        final Vec3d vec3d6 = this.getRotationVec(tickDelta);
        final Vec3d vec3d7 = vec3d5.add(vec3d6.x * maxDistance, vec3d6.y * maxDistance, vec3d6.z * maxDistance);
        return this.world.rayTrace(new RayTraceContext(vec3d5, vec3d7, RayTraceContext.ShapeType.b, boolean4 ? RayTraceContext.FluidHandling.c : RayTraceContext.FluidHandling.NONE, this));
    }
    
    public boolean collides() {
        return false;
    }
    
    public boolean isPushable() {
        return false;
    }
    
    public void a(final Entity entity, final int integer, final DamageSource damageSource) {
        if (entity instanceof ServerPlayerEntity) {
            Criterions.ENTITY_KILLED_PLAYER.handle((ServerPlayerEntity)entity, this, damageSource);
        }
    }
    
    @Environment(EnvType.CLIENT)
    public boolean shouldRenderFrom(final double x, final double y, final double z) {
        final double double7 = this.x - x;
        final double double8 = this.y - y;
        final double double9 = this.z - z;
        final double double10 = double7 * double7 + double8 * double8 + double9 * double9;
        return this.shouldRenderAtDistance(double10);
    }
    
    @Environment(EnvType.CLIENT)
    public boolean shouldRenderAtDistance(final double distance) {
        double double3 = this.getBoundingBox().averageDimension();
        if (Double.isNaN(double3)) {
            double3 = 1.0;
        }
        double3 *= 64.0 * Entity.renderDistanceMultiplier;
        return distance < double3 * double3;
    }
    
    public boolean saveSelfToTag(final CompoundTag compoundTag) {
        final String string2 = this.getSavedEntityId();
        if (this.removed || string2 == null) {
            return false;
        }
        compoundTag.putString("id", string2);
        this.toTag(compoundTag);
        return true;
    }
    
    public boolean saveToTag(final CompoundTag compoundTag) {
        return !this.hasVehicle() && this.saveSelfToTag(compoundTag);
    }
    
    public CompoundTag toTag(final CompoundTag compoundTag) {
        try {
            compoundTag.put("Pos", this.toListTag(this.x, this.y, this.z));
            final Vec3d vec3d2 = this.getVelocity();
            compoundTag.put("Motion", this.toListTag(vec3d2.x, vec3d2.y, vec3d2.z));
            compoundTag.put("Rotation", this.toListTag(this.yaw, this.pitch));
            compoundTag.putFloat("FallDistance", this.fallDistance);
            compoundTag.putShort("Fire", (short)this.fireTime);
            compoundTag.putShort("Air", (short)this.getBreath());
            compoundTag.putBoolean("OnGround", this.onGround);
            compoundTag.putInt("Dimension", this.dimension.getRawId());
            compoundTag.putBoolean("Invulnerable", this.invulnerable);
            compoundTag.putInt("PortalCooldown", this.portalCooldown);
            compoundTag.putUuid("UUID", this.getUuid());
            final TextComponent textComponent3 = this.getCustomName();
            if (textComponent3 != null) {
                compoundTag.putString("CustomName", TextComponent.Serializer.toJsonString(textComponent3));
            }
            if (this.isCustomNameVisible()) {
                compoundTag.putBoolean("CustomNameVisible", this.isCustomNameVisible());
            }
            if (this.isSilent()) {
                compoundTag.putBoolean("Silent", this.isSilent());
            }
            if (this.isUnaffectedByGravity()) {
                compoundTag.putBoolean("NoGravity", this.isUnaffectedByGravity());
            }
            if (this.glowing) {
                compoundTag.putBoolean("Glowing", this.glowing);
            }
            if (!this.scoreboardTags.isEmpty()) {
                final ListTag listTag4 = new ListTag();
                for (final String string6 : this.scoreboardTags) {
                    ((AbstractList<StringTag>)listTag4).add(new StringTag(string6));
                }
                compoundTag.put("Tags", listTag4);
            }
            this.writeCustomDataToTag(compoundTag);
            if (this.hasPassengers()) {
                final ListTag listTag4 = new ListTag();
                for (final Entity entity6 : this.getPassengerList()) {
                    final CompoundTag compoundTag2 = new CompoundTag();
                    if (entity6.saveSelfToTag(compoundTag2)) {
                        ((AbstractList<CompoundTag>)listTag4).add(compoundTag2);
                    }
                }
                if (!listTag4.isEmpty()) {
                    compoundTag.put("Passengers", listTag4);
                }
            }
        }
        catch (Throwable throwable2) {
            final CrashReport crashReport3 = CrashReport.create(throwable2, "Saving entity NBT");
            final CrashReportSection crashReportSection4 = crashReport3.addElement("Entity being saved");
            this.populateCrashReport(crashReportSection4);
            throw new CrashException(crashReport3);
        }
        return compoundTag;
    }
    
    public void fromTag(final CompoundTag compoundTag) {
        try {
            final ListTag listTag2 = compoundTag.getList("Pos", 6);
            final ListTag listTag3 = compoundTag.getList("Motion", 6);
            final ListTag listTag4 = compoundTag.getList("Rotation", 5);
            final double double5 = listTag3.getDouble(0);
            final double double6 = listTag3.getDouble(1);
            final double double7 = listTag3.getDouble(2);
            this.setVelocity((Math.abs(double5) > 10.0) ? 0.0 : double5, (Math.abs(double6) > 10.0) ? 0.0 : double6, (Math.abs(double7) > 10.0) ? 0.0 : double7);
            this.x = listTag2.getDouble(0);
            this.y = listTag2.getDouble(1);
            this.z = listTag2.getDouble(2);
            this.prevRenderX = this.x;
            this.prevRenderY = this.y;
            this.prevRenderZ = this.z;
            this.prevX = this.x;
            this.prevY = this.y;
            this.prevZ = this.z;
            this.yaw = listTag4.getFloat(0);
            this.pitch = listTag4.getFloat(1);
            this.prevYaw = this.yaw;
            this.prevPitch = this.pitch;
            this.setHeadYaw(this.yaw);
            this.setYaw(this.yaw);
            this.fallDistance = compoundTag.getFloat("FallDistance");
            this.fireTime = compoundTag.getShort("Fire");
            this.setBreath(compoundTag.getShort("Air"));
            this.onGround = compoundTag.getBoolean("OnGround");
            if (compoundTag.containsKey("Dimension")) {
                this.dimension = DimensionType.byRawId(compoundTag.getInt("Dimension"));
            }
            this.invulnerable = compoundTag.getBoolean("Invulnerable");
            this.portalCooldown = compoundTag.getInt("PortalCooldown");
            if (compoundTag.hasUuid("UUID")) {
                this.uuid = compoundTag.getUuid("UUID");
                this.uuidString = this.uuid.toString();
            }
            if (!Double.isFinite(this.x) || !Double.isFinite(this.y) || !Double.isFinite(this.z)) {
                throw new IllegalStateException("Entity has invalid position");
            }
            if (!Double.isFinite(this.yaw) || !Double.isFinite(this.pitch)) {
                throw new IllegalStateException("Entity has invalid rotation");
            }
            this.setPosition(this.x, this.y, this.z);
            this.setRotation(this.yaw, this.pitch);
            if (compoundTag.containsKey("CustomName", 8)) {
                this.setCustomName(TextComponent.Serializer.fromJsonString(compoundTag.getString("CustomName")));
            }
            this.setCustomNameVisible(compoundTag.getBoolean("CustomNameVisible"));
            this.setSilent(compoundTag.getBoolean("Silent"));
            this.setUnaffectedByGravity(compoundTag.getBoolean("NoGravity"));
            this.setGlowing(compoundTag.getBoolean("Glowing"));
            if (compoundTag.containsKey("Tags", 9)) {
                this.scoreboardTags.clear();
                final ListTag listTag5 = compoundTag.getList("Tags", 8);
                for (int integer12 = Math.min(listTag5.size(), 1024), integer13 = 0; integer13 < integer12; ++integer13) {
                    this.scoreboardTags.add(listTag5.getString(integer13));
                }
            }
            this.readCustomDataFromTag(compoundTag);
            if (this.shouldSetPositionOnLoad()) {
                this.setPosition(this.x, this.y, this.z);
            }
        }
        catch (Throwable throwable2) {
            final CrashReport crashReport3 = CrashReport.create(throwable2, "Loading entity NBT");
            final CrashReportSection crashReportSection4 = crashReport3.addElement("Entity being loaded");
            this.populateCrashReport(crashReportSection4);
            throw new CrashException(crashReport3);
        }
    }
    
    protected boolean shouldSetPositionOnLoad() {
        return true;
    }
    
    @Nullable
    protected final String getSavedEntityId() {
        final EntityType<?> entityType1 = this.getType();
        final Identifier identifier2 = EntityType.getId(entityType1);
        return (!entityType1.isSaveable() || identifier2 == null) ? null : identifier2.toString();
    }
    
    protected abstract void readCustomDataFromTag(final CompoundTag arg1);
    
    protected abstract void writeCustomDataToTag(final CompoundTag arg1);
    
    protected ListTag toListTag(final double... arr) {
        final ListTag listTag2 = new ListTag();
        for (final double double6 : arr) {
            ((AbstractList<DoubleTag>)listTag2).add(new DoubleTag(double6));
        }
        return listTag2;
    }
    
    protected ListTag toListTag(final float... arr) {
        final ListTag listTag2 = new ListTag();
        for (final float float6 : arr) {
            ((AbstractList<FloatTag>)listTag2).add(new FloatTag(float6));
        }
        return listTag2;
    }
    
    @Nullable
    public ItemEntity dropItem(final ItemProvider itemProvider) {
        return this.dropItem(itemProvider, 0);
    }
    
    @Nullable
    public ItemEntity dropItem(final ItemProvider itemProvider, final int integer) {
        return this.dropStack(new ItemStack(itemProvider), (float)integer);
    }
    
    @Nullable
    public ItemEntity dropStack(final ItemStack itemStack) {
        return this.dropStack(itemStack, 0.0f);
    }
    
    @Nullable
    public ItemEntity dropStack(final ItemStack stack, final float float2) {
        if (stack.isEmpty()) {
            return null;
        }
        if (this.world.isClient) {
            return null;
        }
        final ItemEntity itemEntity3 = new ItemEntity(this.world, this.x, this.y + float2, this.z, stack);
        itemEntity3.setToDefaultPickupDelay();
        this.world.spawnEntity(itemEntity3);
        return itemEntity3;
    }
    
    public boolean isAlive() {
        return !this.removed;
    }
    
    public boolean isInsideWall() {
        if (this.noClip) {
            return false;
        }
        try (final BlockPos.PooledMutable pooledMutable1 = BlockPos.PooledMutable.get()) {
            for (int integer3 = 0; integer3 < 8; ++integer3) {
                final int integer4 = MathHelper.floor(this.y + ((integer3 >> 0) % 2 - 0.5f) * 0.1f + this.standingEyeHeight);
                final int integer5 = MathHelper.floor(this.x + ((integer3 >> 1) % 2 - 0.5f) * this.size.width * 0.8f);
                final int integer6 = MathHelper.floor(this.z + ((integer3 >> 2) % 2 - 0.5f) * this.size.width * 0.8f);
                if (pooledMutable1.getX() != integer5 || pooledMutable1.getY() != integer4 || pooledMutable1.getZ() != integer6) {
                    pooledMutable1.set(integer5, integer4, integer6);
                    if (this.world.getBlockState(pooledMutable1).canSuffocate(this.world, pooledMutable1)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean interact(final PlayerEntity player, final Hand hand) {
        return false;
    }
    
    @Nullable
    public BoundingBox j(final Entity entity) {
        return null;
    }
    
    public void tickRiding() {
        this.setVelocity(Vec3d.ZERO);
        this.tick();
        if (!this.hasVehicle()) {
            return;
        }
        this.getVehicle().updatePassengerPosition(this);
    }
    
    public void updatePassengerPosition(final Entity passenger) {
        if (!this.hasPassenger(passenger)) {
            return;
        }
        passenger.setPosition(this.x, this.y + this.getMountedHeightOffset() + passenger.getHeightOffset(), this.z);
    }
    
    @Environment(EnvType.CLIENT)
    public void onPassengerLookAround(final Entity passenger) {
    }
    
    public double getHeightOffset() {
        return 0.0;
    }
    
    public double getMountedHeightOffset() {
        return this.size.height * 0.75;
    }
    
    public boolean startRiding(final Entity entity) {
        return this.startRiding(entity, false);
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isLiving() {
        return this instanceof LivingEntity;
    }
    
    public boolean startRiding(final Entity entity, final boolean boolean2) {
        for (Entity entity2 = entity; entity2.vehicle != null; entity2 = entity2.vehicle) {
            if (entity2.vehicle == this) {
                return false;
            }
        }
        if (!boolean2 && (!this.canStartRiding(entity) || !entity.canAddPassenger(this))) {
            return false;
        }
        if (this.hasVehicle()) {
            this.stopRiding();
        }
        (this.vehicle = entity).addPassenger(this);
        return true;
    }
    
    protected boolean canStartRiding(final Entity entity) {
        return this.ridingCooldown <= 0;
    }
    
    protected boolean wouldPoseNotCollide(final EntityPose entityPose) {
        return this.world.doesNotCollide(this, this.d(entityPose));
    }
    
    public void removeAllPassengers() {
        for (int integer1 = this.passengerList.size() - 1; integer1 >= 0; --integer1) {
            this.passengerList.get(integer1).stopRiding();
        }
    }
    
    public void stopRiding() {
        if (this.vehicle != null) {
            final Entity entity1 = this.vehicle;
            this.vehicle = null;
            entity1.removePassenger(this);
        }
    }
    
    protected void addPassenger(final Entity entity) {
        if (entity.getVehicle() != this) {
            throw new IllegalStateException("Use x.startRiding(y), not y.addPassenger(x)");
        }
        if (!this.world.isClient && entity instanceof PlayerEntity && !(this.getPrimaryPassenger() instanceof PlayerEntity)) {
            this.passengerList.add(0, entity);
        }
        else {
            this.passengerList.add(entity);
        }
    }
    
    protected void removePassenger(final Entity entity) {
        if (entity.getVehicle() == this) {
            throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
        }
        this.passengerList.remove(entity);
        entity.ridingCooldown = 60;
    }
    
    protected boolean canAddPassenger(final Entity entity) {
        return this.getPassengerList().size() < 1;
    }
    
    @Environment(EnvType.CLIENT)
    public void setPositionAndRotations(final double x, final double y, final double z, final float float7, final float float8, final int integer9, final boolean boolean10) {
        this.setPosition(x, y, z);
        this.setRotation(float7, float8);
    }
    
    @Environment(EnvType.CLIENT)
    public void a(final float float1, final int integer) {
        this.setHeadYaw(float1);
    }
    
    public float getBoundingBoxMarginForTargeting() {
        return 0.0f;
    }
    
    public Vec3d getRotationVector() {
        return this.getRotationVector(this.pitch, this.yaw);
    }
    
    public Vec2f getRotationClient() {
        return new Vec2f(this.pitch, this.yaw);
    }
    
    @Environment(EnvType.CLIENT)
    public Vec3d getRotationVecClient() {
        return Vec3d.fromPolar(this.getRotationClient());
    }
    
    public void setInPortal(final BlockPos pos) {
        if (this.portalCooldown > 0) {
            this.portalCooldown = this.getDefaultPortalCooldown();
            return;
        }
        if (!this.world.isClient && !pos.equals(this.lastPortalPosition)) {
            this.lastPortalPosition = new BlockPos(pos);
            final BlockPattern.Result result2 = ((PortalBlock)Blocks.cM).findPortal(this.world, this.lastPortalPosition);
            final double double3 = (result2.getForwards().getAxis() == Direction.Axis.X) ? result2.getFrontTopLeft().getZ() : ((double)result2.getFrontTopLeft().getX());
            final double double4 = Math.abs(MathHelper.minusDiv(((result2.getForwards().getAxis() == Direction.Axis.X) ? this.z : this.x) - (double)((result2.getForwards().rotateYClockwise().getDirection() == Direction.AxisDirection.NEGATIVE) ? 1 : 0), double3, double3 - result2.getWidth()));
            final double double5 = MathHelper.minusDiv(this.y - 1.0, result2.getFrontTopLeft().getY(), result2.getFrontTopLeft().getY() - result2.getHeight());
            this.am = new Vec3d(double4, double5, 0.0);
            this.an = result2.getForwards();
        }
        this.inPortal = true;
    }
    
    protected void tickPortal() {
        if (!(this.world instanceof ServerWorld)) {
            return;
        }
        final int integer1 = this.getMaxPortalTime();
        if (this.inPortal) {
            if (this.world.getServer().isNetherAllowed() && !this.hasVehicle() && this.portalTime++ >= integer1) {
                this.world.getProfiler().push("portal");
                this.portalTime = integer1;
                this.portalCooldown = this.getDefaultPortalCooldown();
                this.changeDimension((this.world.dimension.getType() == DimensionType.b) ? DimensionType.a : DimensionType.b);
                this.world.getProfiler().pop();
            }
            this.inPortal = false;
        }
        else {
            if (this.portalTime > 0) {
                this.portalTime -= 4;
            }
            if (this.portalTime < 0) {
                this.portalTime = 0;
            }
        }
        this.tickPortalCooldown();
    }
    
    public int getDefaultPortalCooldown() {
        return 300;
    }
    
    @Environment(EnvType.CLIENT)
    public void setVelocityClient(final double x, final double y, final double z) {
        this.setVelocity(x, y, z);
    }
    
    @Environment(EnvType.CLIENT)
    public void handleStatus(final byte status) {
    }
    
    @Environment(EnvType.CLIENT)
    public void aX() {
    }
    
    public Iterable<ItemStack> getItemsHand() {
        return Entity.EMPTY_STACK_LIST;
    }
    
    public Iterable<ItemStack> getArmorItems() {
        return Entity.EMPTY_STACK_LIST;
    }
    
    public Iterable<ItemStack> getItemsEquipped() {
        return Iterables.<ItemStack>concat(this.getItemsHand(), this.getArmorItems());
    }
    
    public void setEquippedStack(final EquipmentSlot slot, final ItemStack itemStack) {
    }
    
    public boolean isOnFire() {
        final boolean boolean1 = this.world != null && this.world.isClient;
        return !this.isFireImmune() && (this.fireTime > 0 || (boolean1 && this.getFlag(0)));
    }
    
    public boolean hasVehicle() {
        return this.getVehicle() != null;
    }
    
    public boolean hasPassengers() {
        return !this.getPassengerList().isEmpty();
    }
    
    public boolean canBeRiddenInWater() {
        return true;
    }
    
    public boolean isSneaking() {
        return this.getFlag(1);
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isInSneakingPose() {
        return this.getPose() == EntityPose.f;
    }
    
    public void setSneaking(final boolean sneaking) {
        this.setFlag(1, sneaking);
    }
    
    public boolean isSprinting() {
        return this.getFlag(3);
    }
    
    public void setSprinting(final boolean sprinting) {
        this.setFlag(3, sprinting);
    }
    
    public boolean isSwimming() {
        return this.getFlag(4);
    }
    
    public boolean isInSwimmingPose() {
        return this.getPose() == EntityPose.d;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean bk() {
        return this.isInSwimmingPose() && !this.isInsideWater();
    }
    
    public void setSwimming(final boolean swimming) {
        this.setFlag(4, swimming);
    }
    
    public boolean isGlowing() {
        return this.glowing || (this.world.isClient && this.getFlag(6));
    }
    
    public void setGlowing(final boolean glowing) {
        this.glowing = glowing;
        if (!this.world.isClient) {
            this.setFlag(6, this.glowing);
        }
    }
    
    public boolean isInvisible() {
        return this.getFlag(5);
    }
    
    @Environment(EnvType.CLIENT)
    public boolean canSeePlayer(final PlayerEntity player) {
        if (player.isSpectator()) {
            return false;
        }
        final AbstractTeam abstractTeam2 = this.getScoreboardTeam();
        return (abstractTeam2 == null || player == null || player.getScoreboardTeam() != abstractTeam2 || !abstractTeam2.shouldShowFriendlyInvisibles()) && this.isInvisible();
    }
    
    @Nullable
    public AbstractTeam getScoreboardTeam() {
        return this.world.getScoreboard().getPlayerTeam(this.getEntityName());
    }
    
    public boolean isTeammate(final Entity entity) {
        return this.isTeamPlayer(entity.getScoreboardTeam());
    }
    
    public boolean isTeamPlayer(final AbstractTeam abstractTeam) {
        return this.getScoreboardTeam() != null && this.getScoreboardTeam().isEqual(abstractTeam);
    }
    
    public void setInvisible(final boolean invisible) {
        this.setFlag(5, invisible);
    }
    
    protected boolean getFlag(final int index) {
        return (this.dataTracker.<Byte>get(Entity.FLAGS) & 1 << index) != 0x0;
    }
    
    protected void setFlag(final int index, final boolean value) {
        final byte byte3 = this.dataTracker.<Byte>get(Entity.FLAGS);
        if (value) {
            this.dataTracker.<Byte>set(Entity.FLAGS, (byte)(byte3 | 1 << index));
        }
        else {
            this.dataTracker.<Byte>set(Entity.FLAGS, (byte)(byte3 & ~(1 << index)));
        }
    }
    
    public int getMaxBreath() {
        return 300;
    }
    
    public int getBreath() {
        return this.dataTracker.<Integer>get(Entity.BREATH);
    }
    
    public void setBreath(final int breath) {
        this.dataTracker.<Integer>set(Entity.BREATH, breath);
    }
    
    public void onStruckByLightning(final LightningEntity lightning) {
        ++this.fireTime;
        if (this.fireTime == 0) {
            this.setOnFireFor(8);
        }
        this.damage(DamageSource.LIGHTNING_BOLT, 5.0f);
    }
    
    public void j(final boolean boolean1) {
        final Vec3d vec3d2 = this.getVelocity();
        double double3;
        if (boolean1) {
            double3 = Math.max(-0.9, vec3d2.y - 0.03);
        }
        else {
            double3 = Math.min(1.8, vec3d2.y + 0.1);
        }
        this.setVelocity(vec3d2.x, double3, vec3d2.z);
    }
    
    public void k(final boolean boolean1) {
        final Vec3d vec3d2 = this.getVelocity();
        double double3;
        if (boolean1) {
            double3 = Math.max(-0.3, vec3d2.y - 0.03);
        }
        else {
            double3 = Math.min(0.7, vec3d2.y + 0.06);
        }
        this.setVelocity(vec3d2.x, double3, vec3d2.z);
        this.fallDistance = 0.0f;
    }
    
    public void b(final LivingEntity livingEntity) {
    }
    
    protected void pushOutOfBlocks(final double x, final double y, final double z) {
        final BlockPos blockPos7 = new BlockPos(x, y, z);
        final Vec3d vec3d8 = new Vec3d(x - blockPos7.getX(), y - blockPos7.getY(), z - blockPos7.getZ());
        final BlockPos.Mutable mutable9 = new BlockPos.Mutable();
        Direction direction10 = Direction.UP;
        double double11 = Double.MAX_VALUE;
        for (final Direction direction11 : new Direction[] { Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP }) {
            mutable9.set(blockPos7).setOffset(direction11);
            if (!Block.isShapeFullCube(this.world.getBlockState(mutable9).getCollisionShape(this.world, mutable9))) {
                final double double12 = vec3d8.getComponentAlongAxis(direction11.getAxis());
                final double double13 = (direction11.getDirection() == Direction.AxisDirection.POSITIVE) ? (1.0 - double12) : double12;
                if (double13 < double11) {
                    double11 = double13;
                    direction10 = direction11;
                }
            }
        }
        final float float13 = this.random.nextFloat() * 0.2f + 0.1f;
        final float float14 = (float)direction10.getDirection().offset();
        final Vec3d vec3d9 = this.getVelocity().multiply(0.75);
        if (direction10.getAxis() == Direction.Axis.X) {
            this.setVelocity(float14 * float13, vec3d9.y, vec3d9.z);
        }
        else if (direction10.getAxis() == Direction.Axis.Y) {
            this.setVelocity(vec3d9.x, float14 * float13, vec3d9.z);
        }
        else if (direction10.getAxis() == Direction.Axis.Z) {
            this.setVelocity(vec3d9.x, vec3d9.y, float14 * float13);
        }
    }
    
    public void slowMovement(final BlockState state, final Vec3d multipliers) {
        this.fallDistance = 0.0f;
        this.movementMultiplier = multipliers;
    }
    
    private static void c(final TextComponent textComponent) {
        textComponent.modifyStyle(style -> style.setClickEvent(null)).getSiblings().forEach(Entity::c);
    }
    
    @Override
    public TextComponent getName() {
        final TextComponent textComponent1 = this.getCustomName();
        if (textComponent1 != null) {
            final TextComponent textComponent2 = textComponent1.copy();
            c(textComponent2);
            return textComponent2;
        }
        return this.type.getTextComponent();
    }
    
    public boolean isPartOf(final Entity entity) {
        return this == entity;
    }
    
    public float getHeadYaw() {
        return 0.0f;
    }
    
    public void setHeadYaw(final float headYaw) {
    }
    
    public void setYaw(final float float1) {
    }
    
    public boolean canPlayerAttack() {
        return true;
    }
    
    public boolean handlePlayerAttack(final Entity entity) {
        return false;
    }
    
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]", this.getClass().getSimpleName(), this.getName().getText(), this.entityId, (this.world == null) ? "~NULL~" : this.world.getLevelProperties().getLevelName(), this.x, this.y, this.z);
    }
    
    public boolean isInvulnerableTo(final DamageSource damageSource) {
        return this.invulnerable && damageSource != DamageSource.OUT_OF_WORLD && !damageSource.isSourceCreativePlayer();
    }
    
    public boolean isInvulnerable() {
        return this.invulnerable;
    }
    
    public void setInvulnerable(final boolean boolean1) {
        this.invulnerable = boolean1;
    }
    
    public void setPositionAndAngles(final Entity entity) {
        this.setPositionAndAngles(entity.x, entity.y, entity.z, entity.yaw, entity.pitch);
    }
    
    public void v(final Entity entity) {
        final CompoundTag compoundTag2 = entity.toTag(new CompoundTag());
        compoundTag2.remove("Dimension");
        this.fromTag(compoundTag2);
        this.portalCooldown = entity.portalCooldown;
        this.lastPortalPosition = entity.lastPortalPosition;
        this.am = entity.am;
        this.an = entity.an;
    }
    
    @Nullable
    public Entity changeDimension(final DimensionType newDimension) {
        if (this.world.isClient || this.removed) {
            return null;
        }
        this.world.getProfiler().push("changeDimension");
        final MinecraftServer minecraftServer2 = this.getServer();
        final DimensionType dimensionType3 = this.dimension;
        final ServerWorld serverWorld4 = minecraftServer2.getWorld(dimensionType3);
        final ServerWorld serverWorld5 = minecraftServer2.getWorld(newDimension);
        this.dimension = newDimension;
        this.detach();
        this.world.getProfiler().push("reposition");
        Vec3d vec3d7 = this.getVelocity();
        float float8 = 0.0f;
        BlockPos blockPos6;
        if (dimensionType3 == DimensionType.c && newDimension == DimensionType.a) {
            blockPos6 = serverWorld5.getTopPosition(Heightmap.Type.f, serverWorld5.getSpawnPos());
        }
        else if (newDimension == DimensionType.c) {
            blockPos6 = serverWorld5.getForcedSpawnPoint();
        }
        else {
            double double9 = this.x;
            double double10 = this.z;
            final double double11 = 8.0;
            if (dimensionType3 == DimensionType.a && newDimension == DimensionType.b) {
                double9 /= 8.0;
                double10 /= 8.0;
            }
            else if (dimensionType3 == DimensionType.b && newDimension == DimensionType.a) {
                double9 *= 8.0;
                double10 *= 8.0;
            }
            final double double12 = Math.min(-2.9999872E7, serverWorld5.getWorldBorder().getBoundWest() + 16.0);
            final double double13 = Math.min(-2.9999872E7, serverWorld5.getWorldBorder().getBoundNorth() + 16.0);
            final double double14 = Math.min(2.9999872E7, serverWorld5.getWorldBorder().getBoundEast() - 16.0);
            final double double15 = Math.min(2.9999872E7, serverWorld5.getWorldBorder().getBoundSouth() - 16.0);
            double9 = MathHelper.clamp(double9, double12, double14);
            double10 = MathHelper.clamp(double10, double13, double15);
            final Vec3d vec3d8 = this.bw();
            blockPos6 = new BlockPos(double9, this.y, double10);
            final BlockPattern.TeleportTarget teleportTarget24 = serverWorld5.getPortalForcer().getPortal(blockPos6, vec3d7, this.bx(), vec3d8.x, vec3d8.y, this instanceof PlayerEntity);
            if (teleportTarget24 == null) {
                return null;
            }
            blockPos6 = new BlockPos(teleportTarget24.pos);
            vec3d7 = teleportTarget24.velocity;
            float8 = (float)teleportTarget24.yaw;
        }
        this.world.getProfiler().swap("reloading");
        final Entity entity9 = (Entity)this.getType().create(serverWorld5);
        if (entity9 != null) {
            entity9.v(this);
            entity9.setPositionAndAngles(blockPos6, entity9.yaw + float8, entity9.pitch);
            entity9.setVelocity(vec3d7);
            serverWorld5.e(entity9);
        }
        this.removed = true;
        this.world.getProfiler().pop();
        serverWorld4.resetIdleTimeout();
        serverWorld5.resetIdleTimeout();
        this.world.getProfiler().pop();
        return entity9;
    }
    
    public boolean canUsePortals() {
        return true;
    }
    
    public float getEffectiveExplosionResistance(final Explosion explosion, final BlockView world, final BlockPos pos, final BlockState blockState, final FluidState state, final float float6) {
        return float6;
    }
    
    public boolean canExplosionDestroyBlock(final Explosion explosion, final BlockView world, final BlockPos pos, final BlockState state, final float float5) {
        return true;
    }
    
    public int getSafeFallDistance() {
        return 3;
    }
    
    public Vec3d bw() {
        return this.am;
    }
    
    public Direction bx() {
        return this.an;
    }
    
    public boolean canAvoidTraps() {
        return false;
    }
    
    public void populateCrashReport(final CrashReportSection crashReportSection) {
        crashReportSection.add("Entity Type", () -> EntityType.getId(this.getType()) + " (" + this.getClass().getCanonicalName() + ")");
        crashReportSection.add("Entity ID", this.entityId);
        crashReportSection.add("Entity Name", () -> this.getName().getString());
        crashReportSection.add("Entity's Exact location", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.x, this.y, this.z));
        crashReportSection.add("Entity's Block location", CrashReportSection.createPositionString(MathHelper.floor(this.x), MathHelper.floor(this.y), MathHelper.floor(this.z)));
        final Vec3d vec3d2 = this.getVelocity();
        crashReportSection.add("Entity's Momentum", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", vec3d2.x, vec3d2.y, vec3d2.z));
        crashReportSection.add("Entity's Passengers", () -> this.getPassengerList().toString());
        crashReportSection.add("Entity's Vehicle", () -> this.getVehicle().toString());
    }
    
    @Environment(EnvType.CLIENT)
    public boolean doesRenderOnFire() {
        return this.isOnFire();
    }
    
    public void setUuid(final UUID uUID) {
        this.uuid = uUID;
        this.uuidString = this.uuid.toString();
    }
    
    public UUID getUuid() {
        return this.uuid;
    }
    
    public String getUuidAsString() {
        return this.uuidString;
    }
    
    public String getEntityName() {
        return this.uuidString;
    }
    
    public boolean canFly() {
        return true;
    }
    
    @Environment(EnvType.CLIENT)
    public static double getRenderDistanceMultiplier() {
        return Entity.renderDistanceMultiplier;
    }
    
    @Environment(EnvType.CLIENT)
    public static void setRenderDistanceMultiplier(final double value) {
        Entity.renderDistanceMultiplier = value;
    }
    
    @Override
    public TextComponent getDisplayName() {
        return Team.modifyText(this.getScoreboardTeam(), this.getName()).modifyStyle(style -> style.setHoverEvent(this.getComponentHoverEvent()).setInsertion(this.getUuidAsString()));
    }
    
    public void setCustomName(@Nullable final TextComponent textComponent) {
        this.dataTracker.<Optional<TextComponent>>set(Entity.CUSTOM_NAME, Optional.<TextComponent>ofNullable(textComponent));
    }
    
    @Nullable
    @Override
    public TextComponent getCustomName() {
        return this.dataTracker.<Optional<TextComponent>>get(Entity.CUSTOM_NAME).orElse(null);
    }
    
    @Override
    public boolean hasCustomName() {
        return this.dataTracker.<Optional<TextComponent>>get(Entity.CUSTOM_NAME).isPresent();
    }
    
    public void setCustomNameVisible(final boolean boolean1) {
        this.dataTracker.<Boolean>set(Entity.NAME_VISIBLE, boolean1);
    }
    
    public boolean isCustomNameVisible() {
        return this.dataTracker.<Boolean>get(Entity.NAME_VISIBLE);
    }
    
    public void requestTeleport(final double double1, final double double3, final double double5) {
        if (!(this.world instanceof ServerWorld)) {
            return;
        }
        this.aF = true;
        this.setPositionAndAngles(double1, double3, double5, this.yaw, this.pitch);
        ((ServerWorld)this.world).checkChunk(this);
    }
    
    @Environment(EnvType.CLIENT)
    public boolean shouldRenderName() {
        return this.isCustomNameVisible();
    }
    
    public void onTrackedDataSet(final TrackedData<?> data) {
        if (Entity.POSE.equals(data)) {
            this.refreshSize();
        }
    }
    
    public void refreshSize() {
        final EntitySize entitySize1 = this.size;
        final EntityPose entityPose2 = this.getPose();
        final EntitySize entitySize2 = this.getSize(entityPose2);
        this.size = entitySize2;
        this.standingEyeHeight = this.getEyeHeight(entityPose2, entitySize2);
        if (entitySize2.width < entitySize1.width) {
            final double double4 = entitySize2.width / 2.0;
            this.setBoundingBox(new BoundingBox(this.x - double4, this.y, this.z - double4, this.x + double4, this.y + entitySize2.height, this.z + double4));
            return;
        }
        final BoundingBox boundingBox4 = this.getBoundingBox();
        this.setBoundingBox(new BoundingBox(boundingBox4.minX, boundingBox4.minY, boundingBox4.minZ, boundingBox4.minX + entitySize2.width, boundingBox4.minY + entitySize2.height, boundingBox4.minZ + entitySize2.width));
        if (entitySize2.width > entitySize1.width && !this.U && !this.world.isClient) {
            final float float5 = entitySize1.width - entitySize2.width;
            this.move(MovementType.a, new Vec3d(float5, 0.0, float5));
        }
    }
    
    public Direction getHorizontalFacing() {
        return Direction.fromRotation(this.yaw);
    }
    
    public Direction getMovementDirection() {
        return this.getHorizontalFacing();
    }
    
    protected HoverEvent getComponentHoverEvent() {
        final CompoundTag compoundTag1 = new CompoundTag();
        final Identifier identifier2 = EntityType.getId(this.getType());
        compoundTag1.putString("id", this.getUuidAsString());
        if (identifier2 != null) {
            compoundTag1.putString("type", identifier2.toString());
        }
        compoundTag1.putString("name", TextComponent.Serializer.toJsonString(this.getName()));
        return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new StringTextComponent(compoundTag1.toString()));
    }
    
    public boolean a(final ServerPlayerEntity serverPlayerEntity) {
        return true;
    }
    
    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }
    
    @Environment(EnvType.CLIENT)
    public BoundingBox getVisibilityBoundingBox() {
        return this.getBoundingBox();
    }
    
    protected BoundingBox d(final EntityPose entityPose) {
        final EntitySize entitySize2 = this.getSize(entityPose);
        final float float3 = entitySize2.width / 2.0f;
        final Vec3d vec3d4 = new Vec3d(this.x - float3, this.y, this.z - float3);
        final Vec3d vec3d5 = new Vec3d(this.x + float3, this.y + entitySize2.height, this.z + float3);
        return new BoundingBox(vec3d4, vec3d5);
    }
    
    public void setBoundingBox(final BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }
    
    protected float getEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        return entitySize.height * 0.85f;
    }
    
    @Environment(EnvType.CLIENT)
    public float getEyeHeight(final EntityPose pose) {
        return this.getEyeHeight(pose, this.getSize(pose));
    }
    
    public final float getStandingEyeHeight() {
        return this.standingEyeHeight;
    }
    
    public boolean equip(final int slot, final ItemStack item) {
        return false;
    }
    
    @Override
    public void sendMessage(final TextComponent message) {
    }
    
    public BlockPos getBlockPos() {
        return new BlockPos(this);
    }
    
    public Vec3d getPosVector() {
        return new Vec3d(this.x, this.y, this.z);
    }
    
    public World getEntityWorld() {
        return this.world;
    }
    
    @Nullable
    public MinecraftServer getServer() {
        return this.world.getServer();
    }
    
    public ActionResult interactAt(final PlayerEntity player, final Vec3d hitPos, final Hand hand) {
        return ActionResult.PASS;
    }
    
    public boolean isImmuneToExplosion() {
        return false;
    }
    
    protected void dealDamage(final LivingEntity attacker, final Entity target) {
        if (target instanceof LivingEntity) {
            EnchantmentHelper.onUserDamaged((LivingEntity)target, attacker);
        }
        EnchantmentHelper.onTargetDamaged(attacker, target);
    }
    
    public void onStartedTrackingBy(final ServerPlayerEntity serverPlayerEntity) {
    }
    
    public void onStoppedTrackingBy(final ServerPlayerEntity serverPlayerEntity) {
    }
    
    public float applyRotation(final BlockRotation blockRotation) {
        final float float2 = MathHelper.wrapDegrees(this.yaw);
        switch (blockRotation) {
            case ROT_180: {
                return float2 + 180.0f;
            }
            case ROT_270: {
                return float2 + 270.0f;
            }
            case ROT_90: {
                return float2 + 90.0f;
            }
            default: {
                return float2;
            }
        }
    }
    
    public float applyMirror(final BlockMirror blockMirror) {
        final float float2 = MathHelper.wrapDegrees(this.yaw);
        switch (blockMirror) {
            case LEFT_RIGHT: {
                return -float2;
            }
            case FRONT_BACK: {
                return 180.0f - float2;
            }
            default: {
                return float2;
            }
        }
    }
    
    public boolean bS() {
        return false;
    }
    
    public boolean bT() {
        final boolean boolean1 = this.aF;
        this.aF = false;
        return boolean1;
    }
    
    @Nullable
    public Entity getPrimaryPassenger() {
        return null;
    }
    
    public List<Entity> getPassengerList() {
        if (this.passengerList.isEmpty()) {
            return Collections.<Entity>emptyList();
        }
        return Lists.newArrayList(this.passengerList);
    }
    
    public boolean hasPassenger(final Entity entity) {
        for (final Entity entity2 : this.getPassengerList()) {
            if (entity2.equals(entity)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean a(final Class<? extends Entity> class1) {
        for (final Entity entity3 : this.getPassengerList()) {
            if (class1.isAssignableFrom(entity3.getClass())) {
                return true;
            }
        }
        return false;
    }
    
    public Collection<Entity> getPassengersDeep() {
        final Set<Entity> set1 = Sets.newHashSet();
        for (final Entity entity3 : this.getPassengerList()) {
            set1.add(entity3);
            entity3.a(false, set1);
        }
        return set1;
    }
    
    public boolean bX() {
        final Set<Entity> set1 = Sets.newHashSet();
        this.a(true, set1);
        return set1.size() == 1;
    }
    
    private void a(final boolean boolean1, final Set<Entity> set) {
        for (final Entity entity4 : this.getPassengerList()) {
            if (!boolean1 || ServerPlayerEntity.class.isAssignableFrom(entity4.getClass())) {
                set.add(entity4);
            }
            entity4.a(boolean1, set);
        }
    }
    
    public Entity getTopmostVehicle() {
        Entity entity1;
        for (entity1 = this; entity1.hasVehicle(); entity1 = entity1.getVehicle()) {}
        return entity1;
    }
    
    public boolean isConnectedThroughVehicle(final Entity entity) {
        return this.getTopmostVehicle() == entity.getTopmostVehicle();
    }
    
    public boolean y(final Entity entity) {
        for (final Entity entity2 : this.getPassengerList()) {
            if (entity2.equals(entity)) {
                return true;
            }
            if (entity2.y(entity)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isLogicalSideForUpdatingMovement() {
        final Entity entity1 = this.getPrimaryPassenger();
        if (entity1 instanceof PlayerEntity) {
            return ((PlayerEntity)entity1).isMainPlayer();
        }
        return !this.world.isClient;
    }
    
    @Nullable
    public Entity getVehicle() {
        return this.vehicle;
    }
    
    public PistonBehavior getPistonBehavior() {
        return PistonBehavior.a;
    }
    
    public SoundCategory getSoundCategory() {
        return SoundCategory.g;
    }
    
    protected int cc() {
        return 1;
    }
    
    public ServerCommandSource getCommandSource() {
        return new ServerCommandSource(this, new Vec3d(this.x, this.y, this.z), this.getRotationClient(), (this.world instanceof ServerWorld) ? ((ServerWorld)this.world) : null, this.getPermissionLevel(), this.getName().getString(), this.getDisplayName(), this.world.getServer(), this);
    }
    
    protected int getPermissionLevel() {
        return 0;
    }
    
    public boolean allowsPermissionLevel(final int integer) {
        return this.getPermissionLevel() >= integer;
    }
    
    @Override
    public boolean sendCommandFeedback() {
        return this.world.getGameRules().getBoolean("sendCommandFeedback");
    }
    
    @Override
    public boolean shouldTrackOutput() {
        return true;
    }
    
    @Override
    public boolean shouldBroadcastConsoleToOps() {
        return true;
    }
    
    public void lookAt(final EntityAnchorArgumentType.EntityAnchor anchor, final Vec3d target) {
        final Vec3d vec3d3 = anchor.positionAt(this);
        final double double4 = target.x - vec3d3.x;
        final double double5 = target.y - vec3d3.y;
        final double double6 = target.z - vec3d3.z;
        final double double7 = MathHelper.sqrt(double4 * double4 + double6 * double6);
        this.pitch = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(double5, double7) * 57.2957763671875)));
        this.setHeadYaw(this.yaw = MathHelper.wrapDegrees((float)(MathHelper.atan2(double6, double4) * 57.2957763671875) - 90.0f));
        this.prevPitch = this.pitch;
        this.prevYaw = this.yaw;
    }
    
    public boolean updateMovementInFluid(final Tag<Fluid> tag) {
        final BoundingBox boundingBox2 = this.getBoundingBox().contract(0.001);
        final int integer3 = MathHelper.floor(boundingBox2.minX);
        final int integer4 = MathHelper.ceil(boundingBox2.maxX);
        final int integer5 = MathHelper.floor(boundingBox2.minY);
        final int integer6 = MathHelper.ceil(boundingBox2.maxY);
        final int integer7 = MathHelper.floor(boundingBox2.minZ);
        final int integer8 = MathHelper.ceil(boundingBox2.maxZ);
        if (!this.world.isAreaLoaded(integer3, integer5, integer7, integer4, integer6, integer8)) {
            return false;
        }
        double double9 = 0.0;
        final boolean boolean11 = this.canFly();
        boolean boolean12 = false;
        Vec3d vec3d13 = Vec3d.ZERO;
        int integer9 = 0;
        try (final BlockPos.PooledMutable pooledMutable15 = BlockPos.PooledMutable.get()) {
            for (int integer10 = integer3; integer10 < integer4; ++integer10) {
                for (int integer11 = integer5; integer11 < integer6; ++integer11) {
                    for (int integer12 = integer7; integer12 < integer8; ++integer12) {
                        pooledMutable15.set(integer10, integer11, integer12);
                        final FluidState fluidState20 = this.world.getFluidState(pooledMutable15);
                        if (fluidState20.matches(tag)) {
                            final double double10 = integer11 + fluidState20.getHeight(this.world, pooledMutable15);
                            if (double10 >= boundingBox2.minY) {
                                boolean12 = true;
                                double9 = Math.max(double10 - boundingBox2.minY, double9);
                                if (boolean11) {
                                    Vec3d vec3d14 = fluidState20.getVelocity(this.world, pooledMutable15);
                                    if (double9 < 0.4) {
                                        vec3d14 = vec3d14.multiply(double9);
                                    }
                                    vec3d13 = vec3d13.add(vec3d14);
                                    ++integer9;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (vec3d13.length() > 0.0) {
            if (integer9 > 0) {
                vec3d13 = vec3d13.multiply(1.0 / integer9);
            }
            if (!(this instanceof PlayerEntity)) {
                vec3d13 = vec3d13.normalize();
            }
            this.setVelocity(this.getVelocity().add(vec3d13.multiply(0.014)));
        }
        this.waterHeight = double9;
        return boolean12;
    }
    
    public double getWaterHeight() {
        return this.waterHeight;
    }
    
    public final float getWidth() {
        return this.size.width;
    }
    
    public final float getHeight() {
        return this.size.height;
    }
    
    public abstract Packet<?> createSpawnPacket();
    
    public EntitySize getSize(final EntityPose entityPose) {
        return this.type.getDefaultSize();
    }
    
    public Vec3d getPos() {
        return new Vec3d(this.x, this.y, this.z);
    }
    
    public Vec3d getVelocity() {
        return this.velocity;
    }
    
    public void setVelocity(final Vec3d vec3d) {
        this.velocity = vec3d;
    }
    
    public void setVelocity(final double x, final double y, final double z) {
        this.setVelocity(new Vec3d(x, y, z));
    }
    
    static {
        LOGGER = LogManager.getLogger();
        MAX_ENTITY_ID = new AtomicInteger();
        EMPTY_STACK_LIST = Collections.<ItemStack>emptyList();
        NULL_BOX = new BoundingBox(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        Entity.renderDistanceMultiplier = 1.0;
        FLAGS = DataTracker.<Byte>registerData(Entity.class, TrackedDataHandlerRegistry.BYTE);
        BREATH = DataTracker.<Integer>registerData(Entity.class, TrackedDataHandlerRegistry.INTEGER);
        CUSTOM_NAME = DataTracker.<Optional<TextComponent>>registerData(Entity.class, TrackedDataHandlerRegistry.OPTIONAL_TEXT_COMPONENT);
        NAME_VISIBLE = DataTracker.<Boolean>registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
        SILENT = DataTracker.<Boolean>registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
        NO_GRAVITY = DataTracker.<Boolean>registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
        POSE = DataTracker.<EntityPose>registerData(Entity.class, TrackedDataHandlerRegistry.ENTITY_POSE);
    }
}
