package net.minecraft.entity.ai.pathing;

import java.util.Iterator;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.world.BlockView;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.entity.mob.MobEntity;

public class MobNavigation extends EntityNavigation
{
    private boolean avoidSunlight;
    
    public MobNavigation(final MobEntity mobEntity, final World world) {
        super(mobEntity, world);
    }
    
    @Override
    protected PathNodeNavigator createPathNodeNavigator(final int integer) {
        (this.nodeMaker = new LandPathNodeMaker()).setCanEnterOpenDoors(true);
        return new PathNodeNavigator(this.nodeMaker, integer);
    }
    
    @Override
    protected boolean isAtValidPosition() {
        return this.entity.onGround || this.isInLiquid() || this.entity.hasVehicle();
    }
    
    @Override
    protected Vec3d getPos() {
        return new Vec3d(this.entity.x, this.s(), this.entity.z);
    }
    
    @Override
    public Path findPathTo(BlockPos pos) {
        if (this.world.getBlockState(pos).isAir()) {
            BlockPos blockPos2;
            for (blockPos2 = pos.down(); blockPos2.getY() > 0 && this.world.getBlockState(blockPos2).isAir(); blockPos2 = blockPos2.down()) {}
            if (blockPos2.getY() > 0) {
                return super.findPathTo(blockPos2.up());
            }
            while (blockPos2.getY() < this.world.getHeight() && this.world.getBlockState(blockPos2).isAir()) {
                blockPos2 = blockPos2.up();
            }
            pos = blockPos2;
        }
        if (this.world.getBlockState(pos).getMaterial().isSolid()) {
            BlockPos blockPos2;
            for (blockPos2 = pos.up(); blockPos2.getY() < this.world.getHeight() && this.world.getBlockState(blockPos2).getMaterial().isSolid(); blockPos2 = blockPos2.up()) {}
            return super.findPathTo(blockPos2);
        }
        return super.findPathTo(pos);
    }
    
    @Override
    public Path findPathTo(final Entity entity) {
        return this.findPathTo(new BlockPos(entity));
    }
    
    private int s() {
        if (!this.entity.isInsideWater() || !this.canSwim()) {
            return (int)(this.entity.getBoundingBox().minY + 0.5);
        }
        int integer1 = (int)this.entity.getBoundingBox().minY;
        Block block2 = this.world.getBlockState(new BlockPos(MathHelper.floor(this.entity.x), integer1, MathHelper.floor(this.entity.z))).getBlock();
        int integer2 = 0;
        while (block2 == Blocks.A) {
            ++integer1;
            block2 = this.world.getBlockState(new BlockPos(MathHelper.floor(this.entity.x), integer1, MathHelper.floor(this.entity.z))).getBlock();
            if (++integer2 > 16) {
                return (int)this.entity.getBoundingBox().minY;
            }
        }
        return integer1;
    }
    
    @Override
    protected void D_() {
        super.D_();
        if (this.avoidSunlight) {
            if (this.world.isSkyVisible(new BlockPos(MathHelper.floor(this.entity.x), (int)(this.entity.getBoundingBox().minY + 0.5), MathHelper.floor(this.entity.z)))) {
                return;
            }
            for (int integer1 = 0; integer1 < this.currentPath.getLength(); ++integer1) {
                final PathNode pathNode2 = this.currentPath.getNode(integer1);
                if (this.world.isSkyVisible(new BlockPos(pathNode2.x, pathNode2.y, pathNode2.z))) {
                    this.currentPath.setLength(integer1);
                    return;
                }
            }
        }
    }
    
    @Override
    protected boolean canPathDirectlyThrough(final Vec3d origin, final Vec3d target, int sizeX, final int sizeY, int sizeZ) {
        int integer6 = MathHelper.floor(origin.x);
        int integer7 = MathHelper.floor(origin.z);
        double double8 = target.x - origin.x;
        double double9 = target.z - origin.z;
        final double double10 = double8 * double8 + double9 * double9;
        if (double10 < 1.0E-8) {
            return false;
        }
        final double double11 = 1.0 / Math.sqrt(double10);
        double8 *= double11;
        double9 *= double11;
        sizeX += 2;
        sizeZ += 2;
        if (!this.a(integer6, (int)origin.y, integer7, sizeX, sizeY, sizeZ, origin, double8, double9)) {
            return false;
        }
        sizeX -= 2;
        sizeZ -= 2;
        final double double12 = 1.0 / Math.abs(double8);
        final double double13 = 1.0 / Math.abs(double9);
        double double14 = integer6 - origin.x;
        double double15 = integer7 - origin.z;
        if (double8 >= 0.0) {
            ++double14;
        }
        if (double9 >= 0.0) {
            ++double15;
        }
        double14 /= double8;
        double15 /= double9;
        final int integer8 = (double8 < 0.0) ? -1 : 1;
        final int integer9 = (double9 < 0.0) ? -1 : 1;
        final int integer10 = MathHelper.floor(target.x);
        final int integer11 = MathHelper.floor(target.z);
        int integer12 = integer10 - integer6;
        int integer13 = integer11 - integer7;
        while (integer12 * integer8 > 0 || integer13 * integer9 > 0) {
            if (double14 < double15) {
                double14 += double12;
                integer6 += integer8;
                integer12 = integer10 - integer6;
            }
            else {
                double15 += double13;
                integer7 += integer9;
                integer13 = integer11 - integer7;
            }
            if (!this.a(integer6, (int)origin.y, integer7, sizeX, sizeY, sizeZ, origin, double8, double9)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean a(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final Vec3d vec3d, final double double8, final double double10) {
        final int integer7 = integer1 - integer4 / 2;
        final int integer8 = integer3 - integer6 / 2;
        if (!this.b(integer7, integer2, integer8, integer4, integer5, integer6, vec3d, double8, double10)) {
            return false;
        }
        for (int integer9 = integer7; integer9 < integer7 + integer4; ++integer9) {
            for (int integer10 = integer8; integer10 < integer8 + integer6; ++integer10) {
                final double double11 = integer9 + 0.5 - vec3d.x;
                final double double12 = integer10 + 0.5 - vec3d.z;
                if (double11 * double8 + double12 * double10 >= 0.0) {
                    PathNodeType pathNodeType20 = this.nodeMaker.getPathNodeType(this.world, integer9, integer2 - 1, integer10, this.entity, integer4, integer5, integer6, true, true);
                    if (pathNodeType20 == PathNodeType.g) {
                        return false;
                    }
                    if (pathNodeType20 == PathNodeType.f) {
                        return false;
                    }
                    if (pathNodeType20 == PathNodeType.b) {
                        return false;
                    }
                    pathNodeType20 = this.nodeMaker.getPathNodeType(this.world, integer9, integer2, integer10, this.entity, integer4, integer5, integer6, true, true);
                    final float float21 = this.entity.getPathNodeTypeWeight(pathNodeType20);
                    if (float21 < 0.0f || float21 >= 8.0f) {
                        return false;
                    }
                    if (pathNodeType20 == PathNodeType.k || pathNodeType20 == PathNodeType.j || pathNodeType20 == PathNodeType.o) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    private boolean b(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final Vec3d vec3d, final double double8, final double double10) {
        for (final BlockPos blockPos13 : BlockPos.iterate(new BlockPos(integer1, integer2, integer3), new BlockPos(integer1 + integer4 - 1, integer2 + integer5 - 1, integer3 + integer6 - 1))) {
            final double double11 = blockPos13.getX() + 0.5 - vec3d.x;
            final double double12 = blockPos13.getZ() + 0.5 - vec3d.z;
            if (double11 * double8 + double12 * double10 < 0.0) {
                continue;
            }
            if (!this.world.getBlockState(blockPos13).canPlaceAtSide(this.world, blockPos13, BlockPlacementEnvironment.a)) {
                return false;
            }
        }
        return true;
    }
    
    public void setCanPathThroughDoors(final boolean boolean1) {
        this.nodeMaker.setCanPathThroughDoors(boolean1);
    }
    
    public boolean canEnterOpenDoors() {
        return this.nodeMaker.canEnterOpenDoors();
    }
    
    public void setAvoidSunlight(final boolean avoidSunlight) {
        this.avoidSunlight = avoidSunlight;
    }
}
