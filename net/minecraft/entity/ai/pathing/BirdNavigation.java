package net.minecraft.entity.ai.pathing;

import net.minecraft.world.BlockView;
import net.minecraft.client.network.DebugRendererInfoManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.entity.mob.MobEntity;

public class BirdNavigation extends EntityNavigation
{
    public BirdNavigation(final MobEntity mobEntity, final World world) {
        super(mobEntity, world);
    }
    
    @Override
    protected PathNodeNavigator createPathNodeNavigator(final int integer) {
        (this.nodeMaker = new BirdPathNodeMaker()).setCanEnterOpenDoors(true);
        return new PathNodeNavigator(this.nodeMaker, integer);
    }
    
    @Override
    protected boolean isAtValidPosition() {
        return (this.canSwim() && this.isInLiquid()) || !this.entity.hasVehicle();
    }
    
    @Override
    protected Vec3d getPos() {
        return new Vec3d(this.entity.x, this.entity.y, this.entity.z);
    }
    
    @Override
    public Path findPathTo(final Entity entity) {
        return this.findPathTo(new BlockPos(entity));
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
    protected boolean canPathDirectlyThrough(final Vec3d origin, final Vec3d target, final int sizeX, final int sizeY, final int sizeZ) {
        int integer6 = MathHelper.floor(origin.x);
        int integer7 = MathHelper.floor(origin.y);
        int integer8 = MathHelper.floor(origin.z);
        double double9 = target.x - origin.x;
        double double10 = target.y - origin.y;
        double double11 = target.z - origin.z;
        final double double12 = double9 * double9 + double10 * double10 + double11 * double11;
        if (double12 < 1.0E-8) {
            return false;
        }
        final double double13 = 1.0 / Math.sqrt(double12);
        double9 *= double13;
        double10 *= double13;
        double11 *= double13;
        final double double14 = 1.0 / Math.abs(double9);
        final double double15 = 1.0 / Math.abs(double10);
        final double double16 = 1.0 / Math.abs(double11);
        double double17 = integer6 - origin.x;
        double double18 = integer7 - origin.y;
        double double19 = integer8 - origin.z;
        if (double9 >= 0.0) {
            ++double17;
        }
        if (double10 >= 0.0) {
            ++double18;
        }
        if (double11 >= 0.0) {
            ++double19;
        }
        double17 /= double9;
        double18 /= double10;
        double19 /= double11;
        final int integer9 = (double9 < 0.0) ? -1 : 1;
        final int integer10 = (double10 < 0.0) ? -1 : 1;
        final int integer11 = (double11 < 0.0) ? -1 : 1;
        final int integer12 = MathHelper.floor(target.x);
        final int integer13 = MathHelper.floor(target.y);
        final int integer14 = MathHelper.floor(target.z);
        int integer15 = integer12 - integer6;
        int integer16 = integer13 - integer7;
        int integer17 = integer14 - integer8;
        while (integer15 * integer9 > 0 || integer16 * integer10 > 0 || integer17 * integer11 > 0) {
            if (double17 < double19 && double17 <= double18) {
                double17 += double14;
                integer6 += integer9;
                integer15 = integer12 - integer6;
            }
            else if (double18 < double17 && double18 <= double19) {
                double18 += double15;
                integer7 += integer10;
                integer16 = integer13 - integer7;
            }
            else {
                double19 += double16;
                integer8 += integer11;
                integer17 = integer14 - integer8;
            }
        }
        return true;
    }
    
    public void setCanPathThroughDoors(final boolean canPathThroughDoors) {
        this.nodeMaker.setCanPathThroughDoors(canPathThroughDoors);
    }
    
    public void setCanEnterOpenDoors(final boolean canEnterOpenDoors) {
        this.nodeMaker.setCanEnterOpenDoors(canEnterOpenDoors);
    }
    
    @Override
    public boolean isValidPosition(final BlockPos pos) {
        return this.world.getBlockState(pos).hasSolidTopSurface(this.world, pos, this.entity);
    }
}
