package net.minecraft.entity.ai.pathing;

import net.minecraft.world.BlockView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RayTraceContext;
import net.minecraft.util.SystemUtil;
import net.minecraft.client.network.DebugRendererInfoManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.world.World;
import net.minecraft.entity.mob.MobEntity;

public class SwimNavigation extends EntityNavigation
{
    private boolean p;
    
    public SwimNavigation(final MobEntity mobEntity, final World world) {
        super(mobEntity, world);
    }
    
    @Override
    protected PathNodeNavigator createPathNodeNavigator(final int integer) {
        this.p = (this.entity instanceof DolphinEntity);
        this.nodeMaker = new WaterPathNodeMaker(this.p);
        return new PathNodeNavigator(this.nodeMaker, integer);
    }
    
    @Override
    protected boolean isAtValidPosition() {
        return this.p || this.isInLiquid();
    }
    
    @Override
    protected Vec3d getPos() {
        return new Vec3d(this.entity.x, this.entity.y + this.entity.getHeight() * 0.5, this.entity.z);
    }
    
    @Override
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
            final Vec3d vec3d1 = this.currentPath.getNodePosition(this.entity, this.currentPath.getCurrentNodeIndex());
            if (MathHelper.floor(this.entity.x) == MathHelper.floor(vec3d1.x) && MathHelper.floor(this.entity.y) == MathHelper.floor(vec3d1.y) && MathHelper.floor(this.entity.z) == MathHelper.floor(vec3d1.z)) {
                this.currentPath.setCurrentNodeIndex(this.currentPath.getCurrentNodeIndex() + 1);
            }
        }
        DebugRendererInfoManager.sendPathfindingData(this.world, this.entity, this.currentPath, this.l);
        if (this.isIdle()) {
            return;
        }
        final Vec3d vec3d1 = this.currentPath.getNodePosition(this.entity);
        this.entity.getMoveControl().moveTo(vec3d1.x, vec3d1.y, vec3d1.z, this.speed);
    }
    
    @Override
    protected void m() {
        if (this.currentPath == null) {
            return;
        }
        final Vec3d vec3d1 = this.getPos();
        final float float2 = this.entity.getWidth();
        float float3 = (float2 > 0.75f) ? (float2 / 2.0f) : (0.75f - float2 / 2.0f);
        final Vec3d vec3d2 = this.entity.getVelocity();
        if (Math.abs(vec3d2.x) > 0.2 || Math.abs(vec3d2.z) > 0.2) {
            float3 *= (float)(vec3d2.length() * 6.0);
        }
        final int integer5 = 6;
        Vec3d vec3d3 = this.currentPath.getCurrentPosition();
        if (Math.abs(this.entity.x - (vec3d3.x + 0.5)) < float3 && Math.abs(this.entity.z - (vec3d3.z + 0.5)) < float3 && Math.abs(this.entity.y - vec3d3.y) < float3 * 2.0f) {
            this.currentPath.next();
        }
        for (int integer6 = Math.min(this.currentPath.getCurrentNodeIndex() + 6, this.currentPath.getLength() - 1); integer6 > this.currentPath.getCurrentNodeIndex(); --integer6) {
            vec3d3 = this.currentPath.getNodePosition(this.entity, integer6);
            if (vec3d3.squaredDistanceTo(vec3d1) <= 36.0) {
                if (this.canPathDirectlyThrough(vec3d1, vec3d3, 0, 0, 0)) {
                    this.currentPath.setCurrentNodeIndex(integer6);
                    break;
                }
            }
        }
        this.a(vec3d1);
    }
    
    @Override
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
                this.k = ((this.entity.getMovementSpeed() > 0.0f) ? (double3 / this.entity.getMovementSpeed() * 100.0) : 0.0);
            }
            if (this.k > 0.0 && this.i > this.k * 2.0) {
                this.h = Vec3d.ZERO;
                this.i = 0L;
                this.k = 0.0;
                this.stop();
            }
            this.j = SystemUtil.getMeasuringTimeMs();
        }
    }
    
    @Override
    protected boolean canPathDirectlyThrough(final Vec3d origin, final Vec3d target, final int sizeX, final int sizeY, final int sizeZ) {
        final Vec3d vec3d6 = new Vec3d(target.x, target.y + this.entity.getHeight() * 0.5, target.z);
        return this.world.rayTrace(new RayTraceContext(origin, vec3d6, RayTraceContext.ShapeType.a, RayTraceContext.FluidHandling.NONE, this.entity)).getType() == HitResult.Type.NONE;
    }
    
    @Override
    public boolean isValidPosition(final BlockPos pos) {
        return !this.world.getBlockState(pos).isFullOpaque(this.world, pos);
    }
    
    @Override
    public void setCanSwim(final boolean canSwim) {
    }
}
