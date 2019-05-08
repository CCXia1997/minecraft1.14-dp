package net.minecraft.entity.ai.pathing;

import net.minecraft.util.math.Position;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.SystemUtil;
import net.minecraft.client.network.DebugRendererInfoManager;
import net.minecraft.world.BlockView;
import net.minecraft.world.chunk.ChunkCache;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.minecraft.entity.mob.MobEntity;

public abstract class EntityNavigation
{
    protected final MobEntity entity;
    protected final World world;
    @Nullable
    protected Path currentPath;
    protected double speed;
    private final EntityAttributeInstance followRange;
    protected int tickCount;
    protected int f;
    protected Vec3d g;
    protected Vec3d h;
    protected long i;
    protected long j;
    protected double k;
    protected float l;
    protected boolean shouldRecalculate;
    protected long lastRecalculateTime;
    protected PathNodeMaker nodeMaker;
    private BlockPos targetPos;
    private PathNodeNavigator pathNodeNavigator;
    
    public EntityNavigation(final MobEntity mobEntity, final World world) {
        this.g = Vec3d.ZERO;
        this.h = Vec3d.ZERO;
        this.l = 0.5f;
        this.entity = mobEntity;
        this.world = world;
        this.followRange = mobEntity.getAttributeInstance(EntityAttributes.FOLLOW_RANGE);
        this.pathNodeNavigator = this.createPathNodeNavigator(MathHelper.floor(this.followRange.getValue() * 16.0));
    }
    
    public BlockPos getTargetPos() {
        return this.targetPos;
    }
    
    protected abstract PathNodeNavigator createPathNodeNavigator(final int arg1);
    
    public void setSpeed(final double speed) {
        this.speed = speed;
    }
    
    public float getFollowRange() {
        return (float)this.followRange.getValue();
    }
    
    public boolean shouldRecalculatePath() {
        return this.shouldRecalculate;
    }
    
    public void recalculatePath() {
        if (this.world.getTime() - this.lastRecalculateTime > 20L) {
            if (this.targetPos != null) {
                this.currentPath = null;
                this.currentPath = this.findPathTo(this.targetPos);
                this.lastRecalculateTime = this.world.getTime();
                this.shouldRecalculate = false;
            }
        }
        else {
            this.shouldRecalculate = true;
        }
    }
    
    @Nullable
    public final Path findPathTo(final double x, final double y, final double z) {
        return this.findPathTo(new BlockPos(x, y, z));
    }
    
    @Nullable
    public Path findPathTo(final BlockPos pos) {
        final float float2 = pos.getX() + 0.5f;
        final float float3 = pos.getY() + 0.5f;
        final float float4 = pos.getZ() + 0.5f;
        return this.findPathTo(pos, float2, float3, float4, 8, false);
    }
    
    @Nullable
    public Path findPathTo(final Entity entity) {
        final BlockPos blockPos2 = new BlockPos(entity);
        final double double3 = entity.x;
        final double double4 = entity.getBoundingBox().minY;
        final double double5 = entity.z;
        return this.findPathTo(blockPos2, double3, double4, double5, 16, true);
    }
    
    @Nullable
    protected Path findPathTo(final BlockPos pos, final double x, final double y, final double z, final int extraRange, final boolean offsetUp) {
        if (!this.isAtValidPosition()) {
            return null;
        }
        if (this.currentPath != null && !this.currentPath.isFinished() && pos.equals(this.targetPos)) {
            return this.currentPath;
        }
        this.targetPos = pos;
        final float float10 = this.getFollowRange();
        this.world.getProfiler().push("pathfind");
        final BlockPos blockPos11 = offsetUp ? new BlockPos(this.entity).up() : new BlockPos(this.entity);
        final int integer12 = (int)(float10 + extraRange);
        final BlockView blockView13 = new ChunkCache(this.world, blockPos11.add(-integer12, -integer12, -integer12), blockPos11.add(integer12, integer12, integer12));
        final Path path14 = this.pathNodeNavigator.pathfind(blockView13, this.entity, x, y, z, float10);
        this.world.getProfiler().pop();
        return path14;
    }
    
    public boolean startMovingTo(final double double1, final double double3, final double double5, final double speed) {
        return this.startMovingAlong(this.findPathTo(double1, double3, double5), speed);
    }
    
    public boolean startMovingTo(final Entity entity, final double speed) {
        final Path path4 = this.findPathTo(entity);
        return path4 != null && this.startMovingAlong(path4, speed);
    }
    
    public boolean startMovingAlong(@Nullable final Path path, final double speed) {
        if (path == null) {
            this.currentPath = null;
            return false;
        }
        if (!path.equalsPath(this.currentPath)) {
            this.currentPath = path;
        }
        this.D_();
        if (this.currentPath.getLength() <= 0) {
            return false;
        }
        this.speed = speed;
        final Vec3d vec3d4 = this.getPos();
        this.f = this.tickCount;
        this.g = vec3d4;
        return true;
    }
    
    @Nullable
    public Path getCurrentPath() {
        return this.currentPath;
    }
    
    public void tick() {
        ++this.tickCount;
        if (this.shouldRecalculate) {
            this.recalculatePath();
        }
        if (this.isIdle()) {
            return;
        }
        if (this.isAtValidPosition()) {
            this.m();
        }
        else if (this.currentPath != null && this.currentPath.getCurrentNodeIndex() < this.currentPath.getLength()) {
            final Vec3d vec3d1 = this.getPos();
            final Vec3d vec3d2 = this.currentPath.getNodePosition(this.entity, this.currentPath.getCurrentNodeIndex());
            if (vec3d1.y > vec3d2.y && !this.entity.onGround && MathHelper.floor(vec3d1.x) == MathHelper.floor(vec3d2.x) && MathHelper.floor(vec3d1.z) == MathHelper.floor(vec3d2.z)) {
                this.currentPath.setCurrentNodeIndex(this.currentPath.getCurrentNodeIndex() + 1);
            }
        }
        DebugRendererInfoManager.sendPathfindingData(this.world, this.entity, this.currentPath, this.l);
        if (this.isIdle()) {
            return;
        }
        final Vec3d vec3d1 = this.currentPath.getNodePosition(this.entity);
        final BlockPos blockPos2 = new BlockPos(vec3d1);
        this.entity.getMoveControl().moveTo(vec3d1.x, this.world.getBlockState(blockPos2.down()).isAir() ? vec3d1.y : LandPathNodeMaker.a(this.world, blockPos2), vec3d1.z, this.speed);
    }
    
    protected void m() {
        final Vec3d vec3d1 = this.getPos();
        int integer2 = this.currentPath.getLength();
        for (int integer3 = this.currentPath.getCurrentNodeIndex(); integer3 < this.currentPath.getLength(); ++integer3) {
            if (this.currentPath.getNode(integer3).y != Math.floor(vec3d1.y)) {
                integer2 = integer3;
                break;
            }
        }
        this.l = ((this.entity.getWidth() > 0.75f) ? (this.entity.getWidth() / 2.0f) : (0.75f - this.entity.getWidth() / 2.0f));
        final Vec3d vec3d2 = this.currentPath.getCurrentPosition();
        if (Math.abs(this.entity.x - (vec3d2.x + 0.5)) < this.l && Math.abs(this.entity.z - (vec3d2.z + 0.5)) < this.l && Math.abs(this.entity.y - vec3d2.y) < 1.0) {
            this.currentPath.setCurrentNodeIndex(this.currentPath.getCurrentNodeIndex() + 1);
        }
        if (this.entity.world.getTime() % 5L == 0L) {
            final int integer4 = MathHelper.ceil(this.entity.getWidth());
            final int integer5 = MathHelper.ceil(this.entity.getHeight());
            final int integer6 = integer4;
            for (int integer7 = integer2 - 1; integer7 >= this.currentPath.getCurrentNodeIndex(); --integer7) {
                if (this.canPathDirectlyThrough(vec3d1, this.currentPath.getNodePosition(this.entity, integer7), integer4, integer5, integer6)) {
                    this.currentPath.setCurrentNodeIndex(integer7);
                    break;
                }
            }
        }
        this.a(vec3d1);
    }
    
    protected void a(final Vec3d vec3d) {
        if (this.tickCount - this.f > 100) {
            if (vec3d.squaredDistanceTo(this.g) < 2.25) {
                this.stop();
            }
            this.f = this.tickCount;
            this.g = vec3d;
        }
        if (this.currentPath != null && !this.currentPath.isFinished()) {
            final Vec3d vec3d2 = this.currentPath.getCurrentPosition();
            if (vec3d2.equals(this.h)) {
                this.i += SystemUtil.getMeasuringTimeMs() - this.j;
            }
            else {
                this.h = vec3d2;
                final double double3 = vec3d.distanceTo(this.h);
                this.k = ((this.entity.getMovementSpeed() > 0.0f) ? (double3 / this.entity.getMovementSpeed() * 1000.0) : 0.0);
            }
            if (this.k > 0.0 && this.i > this.k * 3.0) {
                this.h = Vec3d.ZERO;
                this.i = 0L;
                this.k = 0.0;
                this.stop();
            }
            this.j = SystemUtil.getMeasuringTimeMs();
        }
    }
    
    public boolean isIdle() {
        return this.currentPath == null || this.currentPath.isFinished();
    }
    
    public void stop() {
        this.currentPath = null;
    }
    
    protected abstract Vec3d getPos();
    
    protected abstract boolean isAtValidPosition();
    
    protected boolean isInLiquid() {
        return this.entity.isInsideWaterOrBubbleColumn() || this.entity.isTouchingLava();
    }
    
    protected void D_() {
        if (this.currentPath == null) {
            return;
        }
        for (int integer1 = 0; integer1 < this.currentPath.getLength(); ++integer1) {
            final PathNode pathNode2 = this.currentPath.getNode(integer1);
            final PathNode pathNode3 = (integer1 + 1 < this.currentPath.getLength()) ? this.currentPath.getNode(integer1 + 1) : null;
            final BlockState blockState4 = this.world.getBlockState(new BlockPos(pathNode2.x, pathNode2.y, pathNode2.z));
            final Block block5 = blockState4.getBlock();
            if (block5 == Blocks.dT) {
                this.currentPath.setNode(integer1, pathNode2.copyWithNewPosition(pathNode2.x, pathNode2.y + 1, pathNode2.z));
                if (pathNode3 != null && pathNode2.y >= pathNode3.y) {
                    this.currentPath.setNode(integer1 + 1, pathNode3.copyWithNewPosition(pathNode3.x, pathNode2.y + 1, pathNode3.z));
                }
            }
        }
    }
    
    protected abstract boolean canPathDirectlyThrough(final Vec3d arg1, final Vec3d arg2, final int arg3, final int arg4, final int arg5);
    
    public boolean isValidPosition(final BlockPos pos) {
        final BlockPos blockPos2 = pos.down();
        return this.world.getBlockState(blockPos2).isFullOpaque(this.world, blockPos2);
    }
    
    public PathNodeMaker getNodeMaker() {
        return this.nodeMaker;
    }
    
    public void setCanSwim(final boolean canSwim) {
        this.nodeMaker.setCanSwim(canSwim);
    }
    
    public boolean canSwim() {
        return this.nodeMaker.canSwim();
    }
    
    public void c(final BlockPos blockPos) {
        if (this.currentPath == null || this.currentPath.isFinished() || this.currentPath.getLength() == 0) {
            return;
        }
        final PathNode pathNode2 = this.currentPath.getEnd();
        final Vec3d vec3d3 = new Vec3d((pathNode2.x + this.entity.x) / 2.0, (pathNode2.y + this.entity.y) / 2.0, (pathNode2.z + this.entity.z) / 2.0);
        if (blockPos.isWithinDistance(vec3d3, this.currentPath.getLength() - this.currentPath.getCurrentNodeIndex())) {
            this.recalculatePath();
        }
    }
}
