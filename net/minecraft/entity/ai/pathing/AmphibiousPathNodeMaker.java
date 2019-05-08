package net.minecraft.entity.ai.pathing;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.AbstractRailBlock;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.BlockView;

public class AmphibiousPathNodeMaker extends LandPathNodeMaker
{
    private float k;
    private float l;
    
    @Override
    public void init(final BlockView blockView, final MobEntity mobEntity) {
        super.init(blockView, mobEntity);
        mobEntity.setPathNodeTypeWeight(PathNodeType.g, 0.0f);
        this.k = mobEntity.getPathNodeTypeWeight(PathNodeType.c);
        mobEntity.setPathNodeTypeWeight(PathNodeType.c, 6.0f);
        this.l = mobEntity.getPathNodeTypeWeight(PathNodeType.h);
        mobEntity.setPathNodeTypeWeight(PathNodeType.h, 4.0f);
    }
    
    @Override
    public void clear() {
        this.entity.setPathNodeTypeWeight(PathNodeType.c, this.k);
        this.entity.setPathNodeTypeWeight(PathNodeType.h, this.l);
        super.clear();
    }
    
    @Override
    public PathNode getStart() {
        return this.getPathNode(MathHelper.floor(this.entity.getBoundingBox().minX), MathHelper.floor(this.entity.getBoundingBox().minY + 0.5), MathHelper.floor(this.entity.getBoundingBox().minZ));
    }
    
    @Override
    public PathNode getPathNode(final double x, final double y, final double z) {
        return this.getPathNode(MathHelper.floor(x), MathHelper.floor(y + 0.5), MathHelper.floor(z));
    }
    
    @Override
    public int getPathNodes(final PathNode[] nodes, final PathNode startNode, final PathNode endNode, final float float4) {
        int integer5 = 0;
        final int integer6 = 1;
        final BlockPos blockPos7 = new BlockPos(startNode.x, startNode.y, startNode.z);
        final double double8 = this.a(blockPos7);
        final PathNode pathNode10 = this.a(startNode.x, startNode.y, startNode.z + 1, 1, double8);
        final PathNode pathNode11 = this.a(startNode.x - 1, startNode.y, startNode.z, 1, double8);
        final PathNode pathNode12 = this.a(startNode.x + 1, startNode.y, startNode.z, 1, double8);
        final PathNode pathNode13 = this.a(startNode.x, startNode.y, startNode.z - 1, 1, double8);
        final PathNode pathNode14 = this.a(startNode.x, startNode.y + 1, startNode.z, 0, double8);
        final PathNode pathNode15 = this.a(startNode.x, startNode.y - 1, startNode.z, 1, double8);
        if (pathNode10 != null && !pathNode10.i && pathNode10.distance(endNode) < float4) {
            nodes[integer5++] = pathNode10;
        }
        if (pathNode11 != null && !pathNode11.i && pathNode11.distance(endNode) < float4) {
            nodes[integer5++] = pathNode11;
        }
        if (pathNode12 != null && !pathNode12.i && pathNode12.distance(endNode) < float4) {
            nodes[integer5++] = pathNode12;
        }
        if (pathNode13 != null && !pathNode13.i && pathNode13.distance(endNode) < float4) {
            nodes[integer5++] = pathNode13;
        }
        if (pathNode14 != null && !pathNode14.i && pathNode14.distance(endNode) < float4) {
            nodes[integer5++] = pathNode14;
        }
        if (pathNode15 != null && !pathNode15.i && pathNode15.distance(endNode) < float4) {
            nodes[integer5++] = pathNode15;
        }
        final boolean boolean16 = pathNode13 == null || pathNode13.type == PathNodeType.b || pathNode13.l != 0.0f;
        final boolean boolean17 = pathNode10 == null || pathNode10.type == PathNodeType.b || pathNode10.l != 0.0f;
        final boolean boolean18 = pathNode12 == null || pathNode12.type == PathNodeType.b || pathNode12.l != 0.0f;
        final boolean boolean19 = pathNode11 == null || pathNode11.type == PathNodeType.b || pathNode11.l != 0.0f;
        if (boolean16 && boolean19) {
            final PathNode pathNode16 = this.a(startNode.x - 1, startNode.y, startNode.z - 1, 1, double8);
            if (pathNode16 != null && !pathNode16.i && pathNode16.distance(endNode) < float4) {
                nodes[integer5++] = pathNode16;
            }
        }
        if (boolean16 && boolean18) {
            final PathNode pathNode16 = this.a(startNode.x + 1, startNode.y, startNode.z - 1, 1, double8);
            if (pathNode16 != null && !pathNode16.i && pathNode16.distance(endNode) < float4) {
                nodes[integer5++] = pathNode16;
            }
        }
        if (boolean17 && boolean19) {
            final PathNode pathNode16 = this.a(startNode.x - 1, startNode.y, startNode.z + 1, 1, double8);
            if (pathNode16 != null && !pathNode16.i && pathNode16.distance(endNode) < float4) {
                nodes[integer5++] = pathNode16;
            }
        }
        if (boolean17 && boolean18) {
            final PathNode pathNode16 = this.a(startNode.x + 1, startNode.y, startNode.z + 1, 1, double8);
            if (pathNode16 != null && !pathNode16.i && pathNode16.distance(endNode) < float4) {
                nodes[integer5++] = pathNode16;
            }
        }
        return integer5;
    }
    
    private double a(final BlockPos blockPos) {
        if (!this.entity.isInsideWater()) {
            final BlockPos blockPos2 = blockPos.down();
            final VoxelShape voxelShape3 = this.blockView.getBlockState(blockPos2).getCollisionShape(this.blockView, blockPos2);
            return blockPos2.getY() + (voxelShape3.isEmpty() ? 0.0 : voxelShape3.getMaximum(Direction.Axis.Y));
        }
        return blockPos.getY() + 0.5;
    }
    
    @Nullable
    private PathNode a(final int integer1, int integer2, final int integer3, final int integer4, final double double5) {
        PathNode pathNode7 = null;
        final BlockPos blockPos8 = new BlockPos(integer1, integer2, integer3);
        final double double6 = this.a(blockPos8);
        if (double6 - double5 > 1.125) {
            return null;
        }
        PathNodeType pathNodeType11 = this.getPathNodeType(this.blockView, integer1, integer2, integer3, this.entity, this.d, this.e, this.f, false, false);
        float float12 = this.entity.getPathNodeTypeWeight(pathNodeType11);
        final double double7 = this.entity.getWidth() / 2.0;
        if (float12 >= 0.0f) {
            pathNode7 = this.getPathNode(integer1, integer2, integer3);
            pathNode7.type = pathNodeType11;
            pathNode7.l = Math.max(pathNode7.l, float12);
        }
        if (pathNodeType11 == PathNodeType.g || pathNodeType11 == PathNodeType.c) {
            if (integer2 < this.entity.world.getSeaLevel() - 10 && pathNode7 != null) {
                final PathNode pathNode8 = pathNode7;
                ++pathNode8.l;
            }
            return pathNode7;
        }
        if (pathNode7 == null && integer4 > 0 && pathNodeType11 != PathNodeType.e && pathNodeType11 != PathNodeType.d) {
            pathNode7 = this.a(integer1, integer2 + 1, integer3, integer4 - 1, double5);
        }
        if (pathNodeType11 == PathNodeType.b) {
            final BoundingBox boundingBox15 = new BoundingBox(integer1 - double7 + 0.5, integer2 + 0.001, integer3 - double7 + 0.5, integer1 + double7 + 0.5, integer2 + this.entity.getHeight(), integer3 + double7 + 0.5);
            if (!this.entity.world.doesNotCollide(this.entity, boundingBox15)) {
                return null;
            }
            final PathNodeType pathNodeType12 = this.getPathNodeType(this.blockView, integer1, integer2 - 1, integer3, this.entity, this.d, this.e, this.f, false, false);
            if (pathNodeType12 == PathNodeType.a) {
                pathNode7 = this.getPathNode(integer1, integer2, integer3);
                pathNode7.type = PathNodeType.c;
                pathNode7.l = Math.max(pathNode7.l, float12);
                return pathNode7;
            }
            if (pathNodeType12 == PathNodeType.g) {
                pathNode7 = this.getPathNode(integer1, integer2, integer3);
                pathNode7.type = PathNodeType.g;
                pathNode7.l = Math.max(pathNode7.l, float12);
                return pathNode7;
            }
            int integer5 = 0;
            while (integer2 > 0 && pathNodeType11 == PathNodeType.b) {
                --integer2;
                if (integer5++ >= this.entity.getSafeFallDistance()) {
                    return null;
                }
                pathNodeType11 = this.getPathNodeType(this.blockView, integer1, integer2, integer3, this.entity, this.d, this.e, this.f, false, false);
                float12 = this.entity.getPathNodeTypeWeight(pathNodeType11);
                if (pathNodeType11 != PathNodeType.b && float12 >= 0.0f) {
                    pathNode7 = this.getPathNode(integer1, integer2, integer3);
                    pathNode7.type = pathNodeType11;
                    pathNode7.l = Math.max(pathNode7.l, float12);
                    break;
                }
                if (float12 < 0.0f) {
                    return null;
                }
            }
        }
        return pathNode7;
    }
    
    @Override
    protected PathNodeType a(final BlockView blockView, final boolean boolean2, final boolean boolean3, final BlockPos blockPos, PathNodeType pathNodeType) {
        if (pathNodeType == PathNodeType.i && !(blockView.getBlockState(blockPos).getBlock() instanceof AbstractRailBlock) && !(blockView.getBlockState(blockPos.down()).getBlock() instanceof AbstractRailBlock)) {
            pathNodeType = PathNodeType.e;
        }
        if (pathNodeType == PathNodeType.p || pathNodeType == PathNodeType.q || pathNodeType == PathNodeType.r) {
            pathNodeType = PathNodeType.a;
        }
        if (pathNodeType == PathNodeType.t) {
            pathNodeType = PathNodeType.a;
        }
        return pathNodeType;
    }
    
    @Override
    public PathNodeType getPathNodeType(final BlockView blockView, final int x, final int y, final int integer4) {
        PathNodeType pathNodeType5 = this.getBasicPathNodeType(blockView, x, y, integer4);
        if (pathNodeType5 == PathNodeType.g) {
            for (final Direction direction9 : Direction.values()) {
                final PathNodeType pathNodeType6 = this.getBasicPathNodeType(blockView, x + direction9.getOffsetX(), y + direction9.getOffsetY(), integer4 + direction9.getOffsetZ());
                if (pathNodeType6 == PathNodeType.a) {
                    return PathNodeType.h;
                }
            }
            return PathNodeType.g;
        }
        if (pathNodeType5 == PathNodeType.b && y >= 1) {
            final Block block6 = blockView.getBlockState(new BlockPos(x, y - 1, integer4)).getBlock();
            final PathNodeType pathNodeType7 = this.getBasicPathNodeType(blockView, x, y - 1, integer4);
            if (pathNodeType7 == PathNodeType.c || pathNodeType7 == PathNodeType.b || pathNodeType7 == PathNodeType.f) {
                pathNodeType5 = PathNodeType.b;
            }
            else {
                pathNodeType5 = PathNodeType.c;
            }
            if (pathNodeType7 == PathNodeType.k || block6 == Blocks.iB || block6 == Blocks.lV) {
                pathNodeType5 = PathNodeType.k;
            }
            if (pathNodeType7 == PathNodeType.m) {
                pathNodeType5 = PathNodeType.m;
            }
            if (pathNodeType7 == PathNodeType.o) {
                pathNodeType5 = PathNodeType.o;
            }
        }
        pathNodeType5 = this.a(blockView, x, y, integer4, pathNodeType5);
        return pathNodeType5;
    }
}
