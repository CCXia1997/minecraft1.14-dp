package net.minecraft.entity.vehicle;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.network.Packet;
import net.minecraft.nbt.Tag;
import net.minecraft.util.TagHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.BlockView;
import net.minecraft.block.enums.RailShape;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.entity.MovementType;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.entity.passive.IronGolemEntity;
import java.util.function.Predicate;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.state.property.Property;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import javax.annotation.Nullable;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.Entity;

public abstract class AbstractMinecartEntity extends Entity
{
    private static final TrackedData<Integer> b;
    private static final TrackedData<Integer> c;
    private static final TrackedData<Float> d;
    private static final TrackedData<Integer> CUSTOM_BLOCK_ID;
    private static final TrackedData<Integer> CUSTOM_BLOCK_OFFSET;
    private static final TrackedData<Boolean> CUSTOM_BLOCK_PRESENT;
    private boolean ar;
    private static final int[][][] as;
    private int at;
    private double au;
    private double av;
    private double aw;
    private double ax;
    private double ay;
    @Environment(EnvType.CLIENT)
    private double az;
    @Environment(EnvType.CLIENT)
    private double aA;
    @Environment(EnvType.CLIENT)
    private double aB;
    
    protected AbstractMinecartEntity(final EntityType<?> type, final World world) {
        super(type, world);
        this.i = true;
    }
    
    protected AbstractMinecartEntity(final EntityType<?> world, final World world, final double x, final double double5, final double double7) {
        this(world, world);
        this.setPosition(x, double5, double7);
        this.setVelocity(Vec3d.ZERO);
        this.prevX = x;
        this.prevY = double5;
        this.prevZ = double7;
    }
    
    public static AbstractMinecartEntity create(final World world, final double x, final double y, final double z, final Type type) {
        if (type == Type.b) {
            return new ChestMinecartEntity(world, x, y, z);
        }
        if (type == Type.c) {
            return new FurnaceMinecartEntity(world, x, y, z);
        }
        if (type == Type.d) {
            return new TNTMinecartEntity(world, x, y, z);
        }
        if (type == Type.e) {
            return new MobSpawnerMinecartEntity(world, x, y, z);
        }
        if (type == Type.f) {
            return new HopperMinecartEntity(world, x, y, z);
        }
        if (type == Type.g) {
            return new CommandBlockMinecartEntity(world, x, y, z);
        }
        return new MinecartEntity(world, x, y, z);
    }
    
    @Override
    protected boolean canClimb() {
        return false;
    }
    
    @Override
    protected void initDataTracker() {
        this.dataTracker.<Integer>startTracking(AbstractMinecartEntity.b, 0);
        this.dataTracker.<Integer>startTracking(AbstractMinecartEntity.c, 1);
        this.dataTracker.<Float>startTracking(AbstractMinecartEntity.d, 0.0f);
        this.dataTracker.<Integer>startTracking(AbstractMinecartEntity.CUSTOM_BLOCK_ID, Block.getRawIdFromState(Blocks.AIR.getDefaultState()));
        this.dataTracker.<Integer>startTracking(AbstractMinecartEntity.CUSTOM_BLOCK_OFFSET, 6);
        this.dataTracker.<Boolean>startTracking(AbstractMinecartEntity.CUSTOM_BLOCK_PRESENT, false);
    }
    
    @Nullable
    @Override
    public BoundingBox j(final Entity entity) {
        if (entity.isPushable()) {
            return entity.getBoundingBox();
        }
        return null;
    }
    
    @Override
    public boolean isPushable() {
        return true;
    }
    
    @Override
    public double getMountedHeightOffset() {
        return 0.0;
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        if (this.world.isClient || this.removed) {
            return true;
        }
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        this.d(-this.n());
        this.c(10);
        this.scheduleVelocityUpdate();
        this.a(this.l() + amount * 10.0f);
        final boolean boolean3 = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity)source.getAttacker()).abilities.creativeMode;
        if (boolean3 || this.l() > 40.0f) {
            this.removeAllPassengers();
            if (!boolean3 || this.hasCustomName()) {
                this.dropItems(source);
            }
            else {
                this.remove();
            }
        }
        return true;
    }
    
    public void dropItems(final DamageSource damageSource) {
        this.remove();
        if (this.world.getGameRules().getBoolean("doEntityDrops")) {
            final ItemStack itemStack2 = new ItemStack(Items.kA);
            if (this.hasCustomName()) {
                itemStack2.setDisplayName(this.getCustomName());
            }
            this.dropStack(itemStack2);
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void aX() {
        this.d(-this.n());
        this.c(10);
        this.a(this.l() + this.l() * 10.0f);
    }
    
    @Override
    public boolean collides() {
        return !this.removed;
    }
    
    @Override
    public Direction getMovementDirection() {
        return this.ar ? this.getHorizontalFacing().getOpposite().rotateYClockwise() : this.getHorizontalFacing().rotateYClockwise();
    }
    
    @Override
    public void tick() {
        if (this.m() > 0) {
            this.c(this.m() - 1);
        }
        if (this.l() > 0.0f) {
            this.a(this.l() - 1.0f);
        }
        if (this.y < -64.0) {
            this.destroy();
        }
        this.tickPortal();
        if (this.world.isClient) {
            if (this.at > 0) {
                final double double1 = this.x + (this.au - this.x) / this.at;
                final double double2 = this.y + (this.av - this.y) / this.at;
                final double double3 = this.z + (this.aw - this.z) / this.at;
                final double double4 = MathHelper.wrapDegrees(this.ax - this.yaw);
                this.yaw += (float)(double4 / this.at);
                this.pitch += (float)((this.ay - this.pitch) / this.at);
                --this.at;
                this.setPosition(double1, double2, double3);
                this.setRotation(this.yaw, this.pitch);
            }
            else {
                this.setPosition(this.x, this.y, this.z);
                this.setRotation(this.yaw, this.pitch);
            }
            return;
        }
        this.prevX = this.x;
        this.prevY = this.y;
        this.prevZ = this.z;
        if (!this.isUnaffectedByGravity()) {
            this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
        }
        final int integer1 = MathHelper.floor(this.x);
        int integer2 = MathHelper.floor(this.y);
        final int integer3 = MathHelper.floor(this.z);
        if (this.world.getBlockState(new BlockPos(integer1, integer2 - 1, integer3)).matches(BlockTags.B)) {
            --integer2;
        }
        final BlockPos blockPos4 = new BlockPos(integer1, integer2, integer3);
        final BlockState blockState5 = this.world.getBlockState(blockPos4);
        if (blockState5.matches(BlockTags.B)) {
            this.b(blockPos4, blockState5);
            if (blockState5.getBlock() == Blocks.fv) {
                this.onActivatorRail(integer1, integer2, integer3, blockState5.<Boolean>get((Property<Boolean>)PoweredRailBlock.POWERED));
            }
        }
        else {
            this.i();
        }
        this.checkBlockCollision();
        this.pitch = 0.0f;
        final double double5 = this.prevX - this.x;
        final double double6 = this.prevZ - this.z;
        if (double5 * double5 + double6 * double6 > 0.001) {
            this.yaw = (float)(MathHelper.atan2(double6, double5) * 180.0 / 3.141592653589793);
            if (this.ar) {
                this.yaw += 180.0f;
            }
        }
        final double double7 = MathHelper.wrapDegrees(this.yaw - this.prevYaw);
        if (double7 < -170.0 || double7 >= 170.0) {
            this.yaw += 180.0f;
            this.ar = !this.ar;
        }
        this.setRotation(this.yaw, this.pitch);
        if (this.getMinecartType() == Type.a && Entity.squaredHorizontalLength(this.getVelocity()) > 0.01) {
            final List<Entity> list12 = this.world.getEntities(this, this.getBoundingBox().expand(0.20000000298023224, 0.0, 0.20000000298023224), EntityPredicates.canBePushedBy(this));
            if (!list12.isEmpty()) {
                for (int integer4 = 0; integer4 < list12.size(); ++integer4) {
                    final Entity entity14 = list12.get(integer4);
                    if (entity14 instanceof PlayerEntity || entity14 instanceof IronGolemEntity || entity14 instanceof AbstractMinecartEntity || this.hasPassengers() || entity14.hasVehicle()) {
                        entity14.pushAwayFrom(this);
                    }
                    else {
                        entity14.startRiding(this);
                    }
                }
            }
        }
        else {
            for (final Entity entity15 : this.world.getEntities(this, this.getBoundingBox().expand(0.20000000298023224, 0.0, 0.20000000298023224))) {
                if (!this.hasPassenger(entity15) && entity15.isPushable() && entity15 instanceof AbstractMinecartEntity) {
                    entity15.pushAwayFrom(this);
                }
            }
        }
        this.ax();
    }
    
    protected double f() {
        return 0.4;
    }
    
    public void onActivatorRail(final int x, final int y, final int z, final boolean boolean4) {
    }
    
    protected void i() {
        final double double1 = this.f();
        final Vec3d vec3d3 = this.getVelocity();
        this.setVelocity(MathHelper.clamp(vec3d3.x, -double1, double1), vec3d3.y, MathHelper.clamp(vec3d3.z, -double1, double1));
        if (this.onGround) {
            this.setVelocity(this.getVelocity().multiply(0.5));
        }
        this.move(MovementType.a, this.getVelocity());
        if (!this.onGround) {
            this.setVelocity(this.getVelocity().multiply(0.95));
        }
    }
    
    protected void b(final BlockPos blockPos, final BlockState blockState) {
        this.fallDistance = 0.0f;
        final Vec3d vec3d3 = this.k(this.x, this.y, this.z);
        this.y = blockPos.getY();
        boolean boolean4 = false;
        boolean boolean5 = false;
        final AbstractRailBlock abstractRailBlock6 = (AbstractRailBlock)blockState.getBlock();
        if (abstractRailBlock6 == Blocks.aM) {
            boolean4 = blockState.<Boolean>get((Property<Boolean>)PoweredRailBlock.POWERED);
            boolean5 = !boolean4;
        }
        final double double7 = 0.0078125;
        Vec3d vec3d4 = this.getVelocity();
        final RailShape railShape10 = blockState.<RailShape>get(abstractRailBlock6.getShapeProperty());
        switch (railShape10) {
            case c: {
                this.setVelocity(vec3d4.add(-0.0078125, 0.0, 0.0));
                ++this.y;
                break;
            }
            case d: {
                this.setVelocity(vec3d4.add(0.0078125, 0.0, 0.0));
                ++this.y;
                break;
            }
            case e: {
                this.setVelocity(vec3d4.add(0.0, 0.0, 0.0078125));
                ++this.y;
                break;
            }
            case f: {
                this.setVelocity(vec3d4.add(0.0, 0.0, -0.0078125));
                ++this.y;
                break;
            }
        }
        vec3d4 = this.getVelocity();
        final int[][] arr11 = AbstractMinecartEntity.as[railShape10.getId()];
        double double8 = arr11[1][0] - arr11[0][0];
        double double9 = arr11[1][2] - arr11[0][2];
        final double double10 = Math.sqrt(double8 * double8 + double9 * double9);
        final double double11 = vec3d4.x * double8 + vec3d4.z * double9;
        if (double11 < 0.0) {
            double8 = -double8;
            double9 = -double9;
        }
        final double double12 = Math.min(2.0, Math.sqrt(Entity.squaredHorizontalLength(vec3d4)));
        vec3d4 = new Vec3d(double12 * double8 / double10, vec3d4.y, double12 * double9 / double10);
        this.setVelocity(vec3d4);
        final Entity entity22 = this.getPassengerList().isEmpty() ? null : this.getPassengerList().get(0);
        if (entity22 instanceof PlayerEntity) {
            final Vec3d vec3d5 = entity22.getVelocity();
            final double double13 = Entity.squaredHorizontalLength(vec3d5);
            final double double14 = Entity.squaredHorizontalLength(this.getVelocity());
            if (double13 > 1.0E-4 && double14 < 0.01) {
                this.setVelocity(this.getVelocity().add(vec3d5.x * 0.1, 0.0, vec3d5.z * 0.1));
                boolean5 = false;
            }
        }
        if (boolean5) {
            final double double15 = Math.sqrt(Entity.squaredHorizontalLength(this.getVelocity()));
            if (double15 < 0.03) {
                this.setVelocity(Vec3d.ZERO);
            }
            else {
                this.setVelocity(this.getVelocity().multiply(0.5, 0.0, 0.5));
            }
        }
        final double double15 = blockPos.getX() + 0.5 + arr11[0][0] * 0.5;
        final double double16 = blockPos.getZ() + 0.5 + arr11[0][2] * 0.5;
        final double double17 = blockPos.getX() + 0.5 + arr11[1][0] * 0.5;
        final double double18 = blockPos.getZ() + 0.5 + arr11[1][2] * 0.5;
        double8 = double17 - double15;
        double9 = double18 - double16;
        double double19;
        if (double8 == 0.0) {
            this.x = blockPos.getX() + 0.5;
            double19 = this.z - blockPos.getZ();
        }
        else if (double9 == 0.0) {
            this.z = blockPos.getZ() + 0.5;
            double19 = this.x - blockPos.getX();
        }
        else {
            final double double20 = this.x - double15;
            final double double21 = this.z - double16;
            double19 = (double20 * double8 + double21 * double9) * 2.0;
        }
        this.x = double15 + double8 * double19;
        this.z = double16 + double9 * double19;
        this.setPosition(this.x, this.y, this.z);
        final double double20 = this.hasPassengers() ? 0.75 : 1.0;
        final double double21 = this.f();
        vec3d4 = this.getVelocity();
        this.move(MovementType.a, new Vec3d(MathHelper.clamp(double20 * vec3d4.x, -double21, double21), 0.0, MathHelper.clamp(double20 * vec3d4.z, -double21, double21)));
        if (arr11[0][1] != 0 && MathHelper.floor(this.x) - blockPos.getX() == arr11[0][0] && MathHelper.floor(this.z) - blockPos.getZ() == arr11[0][2]) {
            this.setPosition(this.x, this.y + arr11[0][1], this.z);
        }
        else if (arr11[1][1] != 0 && MathHelper.floor(this.x) - blockPos.getX() == arr11[1][0] && MathHelper.floor(this.z) - blockPos.getZ() == arr11[1][2]) {
            this.setPosition(this.x, this.y + arr11[1][1], this.z);
        }
        this.k();
        final Vec3d vec3d6 = this.k(this.x, this.y, this.z);
        if (vec3d6 != null && vec3d3 != null) {
            final double double22 = (vec3d3.y - vec3d6.y) * 0.05;
            final Vec3d vec3d7 = this.getVelocity();
            final double double23 = Math.sqrt(Entity.squaredHorizontalLength(vec3d7));
            if (double23 > 0.0) {
                this.setVelocity(vec3d7.multiply((double23 + double22) / double23, 1.0, (double23 + double22) / double23));
            }
            this.setPosition(this.x, vec3d6.y, this.z);
        }
        final int integer38 = MathHelper.floor(this.x);
        final int integer39 = MathHelper.floor(this.z);
        if (integer38 != blockPos.getX() || integer39 != blockPos.getZ()) {
            final Vec3d vec3d7 = this.getVelocity();
            final double double23 = Math.sqrt(Entity.squaredHorizontalLength(vec3d7));
            this.setVelocity(double23 * (integer38 - blockPos.getX()), vec3d7.y, double23 * (integer39 - blockPos.getZ()));
        }
        if (boolean4) {
            final Vec3d vec3d7 = this.getVelocity();
            final double double23 = Math.sqrt(Entity.squaredHorizontalLength(vec3d7));
            if (double23 > 0.01) {
                final double double24 = 0.06;
                this.setVelocity(vec3d7.add(vec3d7.x / double23 * 0.06, 0.0, vec3d7.z / double23 * 0.06));
            }
            else {
                final Vec3d vec3d8 = this.getVelocity();
                double double25 = vec3d8.x;
                double double26 = vec3d8.z;
                if (railShape10 == RailShape.b) {
                    if (this.a(blockPos.west())) {
                        double25 = 0.02;
                    }
                    else if (this.a(blockPos.east())) {
                        double25 = -0.02;
                    }
                }
                else {
                    if (railShape10 != RailShape.a) {
                        return;
                    }
                    if (this.a(blockPos.north())) {
                        double26 = 0.02;
                    }
                    else if (this.a(blockPos.south())) {
                        double26 = -0.02;
                    }
                }
                this.setVelocity(double25, vec3d8.y, double26);
            }
        }
    }
    
    private boolean a(final BlockPos blockPos) {
        return this.world.getBlockState(blockPos).isSimpleFullBlock(this.world, blockPos);
    }
    
    protected void k() {
        final double double1 = this.hasPassengers() ? 0.997 : 0.96;
        this.setVelocity(this.getVelocity().multiply(double1, 0.0, double1));
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public Vec3d a(double double1, double double3, double double5, final double double7) {
        final int integer9 = MathHelper.floor(double1);
        int integer10 = MathHelper.floor(double3);
        final int integer11 = MathHelper.floor(double5);
        if (this.world.getBlockState(new BlockPos(integer9, integer10 - 1, integer11)).matches(BlockTags.B)) {
            --integer10;
        }
        final BlockState blockState12 = this.world.getBlockState(new BlockPos(integer9, integer10, integer11));
        if (blockState12.matches(BlockTags.B)) {
            final RailShape railShape13 = blockState12.<RailShape>get(((AbstractRailBlock)blockState12.getBlock()).getShapeProperty());
            double3 = integer10;
            if (railShape13.isAscending()) {
                double3 = integer10 + 1;
            }
            final int[][] arr14 = AbstractMinecartEntity.as[railShape13.getId()];
            double double8 = arr14[1][0] - arr14[0][0];
            double double9 = arr14[1][2] - arr14[0][2];
            final double double10 = Math.sqrt(double8 * double8 + double9 * double9);
            double8 /= double10;
            double9 /= double10;
            double1 += double8 * double7;
            double5 += double9 * double7;
            if (arr14[0][1] != 0 && MathHelper.floor(double1) - integer9 == arr14[0][0] && MathHelper.floor(double5) - integer11 == arr14[0][2]) {
                double3 += arr14[0][1];
            }
            else if (arr14[1][1] != 0 && MathHelper.floor(double1) - integer9 == arr14[1][0] && MathHelper.floor(double5) - integer11 == arr14[1][2]) {
                double3 += arr14[1][1];
            }
            return this.k(double1, double3, double5);
        }
        return null;
    }
    
    @Nullable
    public Vec3d k(double double1, double double3, double double5) {
        final int integer7 = MathHelper.floor(double1);
        int integer8 = MathHelper.floor(double3);
        final int integer9 = MathHelper.floor(double5);
        if (this.world.getBlockState(new BlockPos(integer7, integer8 - 1, integer9)).matches(BlockTags.B)) {
            --integer8;
        }
        final BlockState blockState10 = this.world.getBlockState(new BlockPos(integer7, integer8, integer9));
        if (blockState10.matches(BlockTags.B)) {
            final RailShape railShape11 = blockState10.<RailShape>get(((AbstractRailBlock)blockState10.getBlock()).getShapeProperty());
            final int[][] arr12 = AbstractMinecartEntity.as[railShape11.getId()];
            final double double6 = integer7 + 0.5 + arr12[0][0] * 0.5;
            final double double7 = integer8 + 0.0625 + arr12[0][1] * 0.5;
            final double double8 = integer9 + 0.5 + arr12[0][2] * 0.5;
            final double double9 = integer7 + 0.5 + arr12[1][0] * 0.5;
            final double double10 = integer8 + 0.0625 + arr12[1][1] * 0.5;
            final double double11 = integer9 + 0.5 + arr12[1][2] * 0.5;
            final double double12 = double9 - double6;
            final double double13 = (double10 - double7) * 2.0;
            final double double14 = double11 - double8;
            double double15;
            if (double12 == 0.0) {
                double15 = double5 - integer9;
            }
            else if (double14 == 0.0) {
                double15 = double1 - integer7;
            }
            else {
                final double double16 = double1 - double6;
                final double double17 = double5 - double8;
                double15 = (double16 * double12 + double17 * double14) * 2.0;
            }
            double1 = double6 + double12 * double15;
            double3 = double7 + double13 * double15;
            double5 = double8 + double14 * double15;
            if (double13 < 0.0) {
                ++double3;
            }
            if (double13 > 0.0) {
                double3 += 0.5;
            }
            return new Vec3d(double1, double3, double5);
        }
        return null;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public BoundingBox getVisibilityBoundingBox() {
        final BoundingBox boundingBox1 = this.getBoundingBox();
        if (this.hasCustomBlock()) {
            return boundingBox1.expand(Math.abs(this.getBlockOffset()) / 16.0);
        }
        return boundingBox1;
    }
    
    @Override
    protected void readCustomDataFromTag(final CompoundTag tag) {
        if (tag.getBoolean("CustomDisplayTile")) {
            this.setCustomBlock(TagHelper.deserializeBlockState(tag.getCompound("DisplayState")));
            this.setCustomBlockOffset(tag.getInt("DisplayOffset"));
        }
    }
    
    @Override
    protected void writeCustomDataToTag(final CompoundTag tag) {
        if (this.hasCustomBlock()) {
            tag.putBoolean("CustomDisplayTile", true);
            tag.put("DisplayState", TagHelper.serializeBlockState(this.getContainedBlock()));
            tag.putInt("DisplayOffset", this.getBlockOffset());
        }
    }
    
    @Override
    public void pushAwayFrom(final Entity entity) {
        if (this.world.isClient) {
            return;
        }
        if (entity.noClip || this.noClip) {
            return;
        }
        if (this.hasPassenger(entity)) {
            return;
        }
        double double2 = entity.x - this.x;
        double double3 = entity.z - this.z;
        double double4 = double2 * double2 + double3 * double3;
        if (double4 >= 9.999999747378752E-5) {
            double4 = MathHelper.sqrt(double4);
            double2 /= double4;
            double3 /= double4;
            double double5 = 1.0 / double4;
            if (double5 > 1.0) {
                double5 = 1.0;
            }
            double2 *= double5;
            double3 *= double5;
            double2 *= 0.10000000149011612;
            double3 *= 0.10000000149011612;
            double2 *= 1.0f - this.pushSpeedReduction;
            double3 *= 1.0f - this.pushSpeedReduction;
            double2 *= 0.5;
            double3 *= 0.5;
            if (entity instanceof AbstractMinecartEntity) {
                final double double6 = entity.x - this.x;
                final double double7 = entity.z - this.z;
                final Vec3d vec3d14 = new Vec3d(double6, 0.0, double7).normalize();
                final Vec3d vec3d15 = new Vec3d(MathHelper.cos(this.yaw * 0.017453292f), 0.0, MathHelper.sin(this.yaw * 0.017453292f)).normalize();
                final double double8 = Math.abs(vec3d14.dotProduct(vec3d15));
                if (double8 < 0.800000011920929) {
                    return;
                }
                final Vec3d vec3d16 = this.getVelocity();
                final Vec3d vec3d17 = entity.getVelocity();
                if (((AbstractMinecartEntity)entity).getMinecartType() == Type.c && this.getMinecartType() != Type.c) {
                    this.setVelocity(vec3d16.multiply(0.2, 1.0, 0.2));
                    this.addVelocity(vec3d17.x - double2, 0.0, vec3d17.z - double3);
                    entity.setVelocity(vec3d17.multiply(0.95, 1.0, 0.95));
                }
                else if (((AbstractMinecartEntity)entity).getMinecartType() != Type.c && this.getMinecartType() == Type.c) {
                    entity.setVelocity(vec3d17.multiply(0.2, 1.0, 0.2));
                    entity.addVelocity(vec3d16.x + double2, 0.0, vec3d16.z + double3);
                    this.setVelocity(vec3d16.multiply(0.95, 1.0, 0.95));
                }
                else {
                    final double double9 = (vec3d17.x + vec3d16.x) / 2.0;
                    final double double10 = (vec3d17.z + vec3d16.z) / 2.0;
                    this.setVelocity(vec3d16.multiply(0.2, 1.0, 0.2));
                    this.addVelocity(double9 - double2, 0.0, double10 - double3);
                    entity.setVelocity(vec3d17.multiply(0.2, 1.0, 0.2));
                    entity.addVelocity(double9 + double2, 0.0, double10 + double3);
                }
            }
            else {
                this.addVelocity(-double2, 0.0, -double3);
                entity.addVelocity(double2 / 4.0, 0.0, double3 / 4.0);
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void setPositionAndRotations(final double x, final double y, final double z, final float float7, final float float8, final int integer9, final boolean boolean10) {
        this.au = x;
        this.av = y;
        this.aw = z;
        this.ax = float7;
        this.ay = float8;
        this.at = integer9 + 2;
        this.setVelocity(this.az, this.aA, this.aB);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void setVelocityClient(final double x, final double y, final double z) {
        this.az = x;
        this.aA = y;
        this.aB = z;
        this.setVelocity(this.az, this.aA, this.aB);
    }
    
    public void a(final float float1) {
        this.dataTracker.<Float>set(AbstractMinecartEntity.d, float1);
    }
    
    public float l() {
        return this.dataTracker.<Float>get(AbstractMinecartEntity.d);
    }
    
    public void c(final int integer) {
        this.dataTracker.<Integer>set(AbstractMinecartEntity.b, integer);
    }
    
    public int m() {
        return this.dataTracker.<Integer>get(AbstractMinecartEntity.b);
    }
    
    public void d(final int integer) {
        this.dataTracker.<Integer>set(AbstractMinecartEntity.c, integer);
    }
    
    public int n() {
        return this.dataTracker.<Integer>get(AbstractMinecartEntity.c);
    }
    
    public abstract Type getMinecartType();
    
    public BlockState getContainedBlock() {
        if (!this.hasCustomBlock()) {
            return this.getDefaultContainedBlock();
        }
        return Block.getStateFromRawId(this.getDataTracker().<Integer>get(AbstractMinecartEntity.CUSTOM_BLOCK_ID));
    }
    
    public BlockState getDefaultContainedBlock() {
        return Blocks.AIR.getDefaultState();
    }
    
    public int getBlockOffset() {
        if (!this.hasCustomBlock()) {
            return this.getDefaultBlockOffset();
        }
        return this.getDataTracker().<Integer>get(AbstractMinecartEntity.CUSTOM_BLOCK_OFFSET);
    }
    
    public int getDefaultBlockOffset() {
        return 6;
    }
    
    public void setCustomBlock(final BlockState blockState) {
        this.getDataTracker().<Integer>set(AbstractMinecartEntity.CUSTOM_BLOCK_ID, Block.getRawIdFromState(blockState));
        this.setCustomBlockPresent(true);
    }
    
    public void setCustomBlockOffset(final int integer) {
        this.getDataTracker().<Integer>set(AbstractMinecartEntity.CUSTOM_BLOCK_OFFSET, integer);
        this.setCustomBlockPresent(true);
    }
    
    public boolean hasCustomBlock() {
        return this.getDataTracker().<Boolean>get(AbstractMinecartEntity.CUSTOM_BLOCK_PRESENT);
    }
    
    public void setCustomBlockPresent(final boolean boolean1) {
        this.getDataTracker().<Boolean>set(AbstractMinecartEntity.CUSTOM_BLOCK_PRESENT, boolean1);
    }
    
    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
    
    static {
        b = DataTracker.<Integer>registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);
        c = DataTracker.<Integer>registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);
        d = DataTracker.<Float>registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.FLOAT);
        CUSTOM_BLOCK_ID = DataTracker.<Integer>registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);
        CUSTOM_BLOCK_OFFSET = DataTracker.<Integer>registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);
        CUSTOM_BLOCK_PRESENT = DataTracker.<Boolean>registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        as = new int[][][] { { { 0, 0, -1 }, { 0, 0, 1 } }, { { -1, 0, 0 }, { 1, 0, 0 } }, { { -1, -1, 0 }, { 1, 0, 0 } }, { { -1, 0, 0 }, { 1, -1, 0 } }, { { 0, 0, -1 }, { 0, -1, 1 } }, { { 0, -1, -1 }, { 0, 0, 1 } }, { { 0, 0, 1 }, { 1, 0, 0 } }, { { 0, 0, 1 }, { -1, 0, 0 } }, { { 0, 0, -1 }, { -1, 0, 0 } }, { { 0, 0, -1 }, { 1, 0, 0 } } };
    }
    
    public enum Type
    {
        a, 
        b, 
        c, 
        d, 
        e, 
        f, 
        g;
    }
}
