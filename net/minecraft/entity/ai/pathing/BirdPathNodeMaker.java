package net.minecraft.entity.ai.pathing;

import java.util.EnumSet;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.block.Block;
import com.google.common.collect.Sets;
import net.minecraft.entity.Entity;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.BlockView;

public class BirdPathNodeMaker extends LandPathNodeMaker
{
    @Override
    public void init(final BlockView blockView, final MobEntity mobEntity) {
        super.init(blockView, mobEntity);
        this.waterPathNodeTypeWeight = mobEntity.getPathNodeTypeWeight(PathNodeType.g);
    }
    
    @Override
    public void clear() {
        this.entity.setPathNodeTypeWeight(PathNodeType.g, this.waterPathNodeTypeWeight);
        super.clear();
    }
    
    @Override
    public PathNode getStart() {
        int integer1;
        if (this.canSwim() && this.entity.isInsideWater()) {
            integer1 = (int)this.entity.getBoundingBox().minY;
            final BlockPos.Mutable mutable2 = new BlockPos.Mutable(MathHelper.floor(this.entity.x), integer1, MathHelper.floor(this.entity.z));
            for (Block block3 = this.blockView.getBlockState(mutable2).getBlock(); block3 == Blocks.A; block3 = this.blockView.getBlockState(mutable2).getBlock()) {
                ++integer1;
                mutable2.set(MathHelper.floor(this.entity.x), integer1, MathHelper.floor(this.entity.z));
            }
        }
        else {
            integer1 = MathHelper.floor(this.entity.getBoundingBox().minY + 0.5);
        }
        final BlockPos blockPos2 = new BlockPos(this.entity);
        final PathNodeType pathNodeType3 = this.a(this.entity, blockPos2.getX(), integer1, blockPos2.getZ());
        if (this.entity.getPathNodeTypeWeight(pathNodeType3) < 0.0f) {
            final Set<BlockPos> set4 = Sets.newHashSet();
            set4.add(new BlockPos(this.entity.getBoundingBox().minX, integer1, this.entity.getBoundingBox().minZ));
            set4.add(new BlockPos(this.entity.getBoundingBox().minX, integer1, this.entity.getBoundingBox().maxZ));
            set4.add(new BlockPos(this.entity.getBoundingBox().maxX, integer1, this.entity.getBoundingBox().minZ));
            set4.add(new BlockPos(this.entity.getBoundingBox().maxX, integer1, this.entity.getBoundingBox().maxZ));
            for (final BlockPos blockPos3 : set4) {
                final PathNodeType pathNodeType4 = this.a(this.entity, blockPos3);
                if (this.entity.getPathNodeTypeWeight(pathNodeType4) >= 0.0f) {
                    return super.getPathNode(blockPos3.getX(), blockPos3.getY(), blockPos3.getZ());
                }
            }
        }
        return super.getPathNode(blockPos2.getX(), integer1, blockPos2.getZ());
    }
    
    @Override
    public PathNode getPathNode(final double x, final double y, final double z) {
        return super.getPathNode(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
    }
    
    @Override
    public int getPathNodes(final PathNode[] nodes, final PathNode startNode, final PathNode endNode, final float float4) {
        int integer5 = 0;
        final PathNode pathNode6 = this.getPathNode(startNode.x, startNode.y, startNode.z + 1);
        final PathNode pathNode7 = this.getPathNode(startNode.x - 1, startNode.y, startNode.z);
        final PathNode pathNode8 = this.getPathNode(startNode.x + 1, startNode.y, startNode.z);
        final PathNode pathNode9 = this.getPathNode(startNode.x, startNode.y, startNode.z - 1);
        final PathNode pathNode10 = this.getPathNode(startNode.x, startNode.y + 1, startNode.z);
        final PathNode pathNode11 = this.getPathNode(startNode.x, startNode.y - 1, startNode.z);
        if (pathNode6 != null && !pathNode6.i && pathNode6.distance(endNode) < float4) {
            nodes[integer5++] = pathNode6;
        }
        if (pathNode7 != null && !pathNode7.i && pathNode7.distance(endNode) < float4) {
            nodes[integer5++] = pathNode7;
        }
        if (pathNode8 != null && !pathNode8.i && pathNode8.distance(endNode) < float4) {
            nodes[integer5++] = pathNode8;
        }
        if (pathNode9 != null && !pathNode9.i && pathNode9.distance(endNode) < float4) {
            nodes[integer5++] = pathNode9;
        }
        if (pathNode10 != null && !pathNode10.i && pathNode10.distance(endNode) < float4) {
            nodes[integer5++] = pathNode10;
        }
        if (pathNode11 != null && !pathNode11.i && pathNode11.distance(endNode) < float4) {
            nodes[integer5++] = pathNode11;
        }
        final boolean boolean12 = pathNode9 == null || pathNode9.l != 0.0f;
        final boolean boolean13 = pathNode6 == null || pathNode6.l != 0.0f;
        final boolean boolean14 = pathNode8 == null || pathNode8.l != 0.0f;
        final boolean boolean15 = pathNode7 == null || pathNode7.l != 0.0f;
        final boolean boolean16 = pathNode10 == null || pathNode10.l != 0.0f;
        final boolean boolean17 = pathNode11 == null || pathNode11.l != 0.0f;
        if (boolean12 && boolean15) {
            final PathNode pathNode12 = this.getPathNode(startNode.x - 1, startNode.y, startNode.z - 1);
            if (pathNode12 != null && !pathNode12.i && pathNode12.distance(endNode) < float4) {
                nodes[integer5++] = pathNode12;
            }
        }
        if (boolean12 && boolean14) {
            final PathNode pathNode12 = this.getPathNode(startNode.x + 1, startNode.y, startNode.z - 1);
            if (pathNode12 != null && !pathNode12.i && pathNode12.distance(endNode) < float4) {
                nodes[integer5++] = pathNode12;
            }
        }
        if (boolean13 && boolean15) {
            final PathNode pathNode12 = this.getPathNode(startNode.x - 1, startNode.y, startNode.z + 1);
            if (pathNode12 != null && !pathNode12.i && pathNode12.distance(endNode) < float4) {
                nodes[integer5++] = pathNode12;
            }
        }
        if (boolean13 && boolean14) {
            final PathNode pathNode12 = this.getPathNode(startNode.x + 1, startNode.y, startNode.z + 1);
            if (pathNode12 != null && !pathNode12.i && pathNode12.distance(endNode) < float4) {
                nodes[integer5++] = pathNode12;
            }
        }
        if (boolean12 && boolean16) {
            final PathNode pathNode12 = this.getPathNode(startNode.x, startNode.y + 1, startNode.z - 1);
            if (pathNode12 != null && !pathNode12.i && pathNode12.distance(endNode) < float4) {
                nodes[integer5++] = pathNode12;
            }
        }
        if (boolean13 && boolean16) {
            final PathNode pathNode12 = this.getPathNode(startNode.x, startNode.y + 1, startNode.z + 1);
            if (pathNode12 != null && !pathNode12.i && pathNode12.distance(endNode) < float4) {
                nodes[integer5++] = pathNode12;
            }
        }
        if (boolean14 && boolean16) {
            final PathNode pathNode12 = this.getPathNode(startNode.x + 1, startNode.y + 1, startNode.z);
            if (pathNode12 != null && !pathNode12.i && pathNode12.distance(endNode) < float4) {
                nodes[integer5++] = pathNode12;
            }
        }
        if (boolean15 && boolean16) {
            final PathNode pathNode12 = this.getPathNode(startNode.x - 1, startNode.y + 1, startNode.z);
            if (pathNode12 != null && !pathNode12.i && pathNode12.distance(endNode) < float4) {
                nodes[integer5++] = pathNode12;
            }
        }
        if (boolean12 && boolean17) {
            final PathNode pathNode12 = this.getPathNode(startNode.x, startNode.y - 1, startNode.z - 1);
            if (pathNode12 != null && !pathNode12.i && pathNode12.distance(endNode) < float4) {
                nodes[integer5++] = pathNode12;
            }
        }
        if (boolean13 && boolean17) {
            final PathNode pathNode12 = this.getPathNode(startNode.x, startNode.y - 1, startNode.z + 1);
            if (pathNode12 != null && !pathNode12.i && pathNode12.distance(endNode) < float4) {
                nodes[integer5++] = pathNode12;
            }
        }
        if (boolean14 && boolean17) {
            final PathNode pathNode12 = this.getPathNode(startNode.x + 1, startNode.y - 1, startNode.z);
            if (pathNode12 != null && !pathNode12.i && pathNode12.distance(endNode) < float4) {
                nodes[integer5++] = pathNode12;
            }
        }
        if (boolean15 && boolean17) {
            final PathNode pathNode12 = this.getPathNode(startNode.x - 1, startNode.y - 1, startNode.z);
            if (pathNode12 != null && !pathNode12.i && pathNode12.distance(endNode) < float4) {
                nodes[integer5++] = pathNode12;
            }
        }
        return integer5;
    }
    
    @Nullable
    @Override
    protected PathNode getPathNode(final int x, final int y, final int integer3) {
        PathNode pathNode4 = null;
        final PathNodeType pathNodeType5 = this.a(this.entity, x, y, integer3);
        final float float6 = this.entity.getPathNodeTypeWeight(pathNodeType5);
        if (float6 >= 0.0f) {
            pathNode4 = super.getPathNode(x, y, integer3);
            pathNode4.type = pathNodeType5;
            pathNode4.l = Math.max(pathNode4.l, float6);
            if (pathNodeType5 == PathNodeType.c) {
                final PathNode pathNode5 = pathNode4;
                ++pathNode5.l;
            }
        }
        if (pathNodeType5 == PathNodeType.b || pathNodeType5 == PathNodeType.c) {
            return pathNode4;
        }
        return pathNode4;
    }
    
    @Override
    public PathNodeType getPathNodeType(final BlockView blockView, final int x, final int y, final int z, final MobEntity entity, final int xSize, final int ySize, final int zSize, final boolean canPathThroughDoors, final boolean boolean10) {
        final EnumSet<PathNodeType> enumSet11 = EnumSet.<PathNodeType>noneOf(PathNodeType.class);
        PathNodeType pathNodeType12 = PathNodeType.a;
        final BlockPos blockPos13 = new BlockPos(entity);
        pathNodeType12 = this.a(blockView, x, y, z, xSize, ySize, zSize, canPathThroughDoors, boolean10, enumSet11, pathNodeType12, blockPos13);
        if (enumSet11.contains(PathNodeType.e)) {
            return PathNodeType.e;
        }
        PathNodeType pathNodeType13 = PathNodeType.a;
        for (final PathNodeType pathNodeType14 : enumSet11) {
            if (entity.getPathNodeTypeWeight(pathNodeType14) < 0.0f) {
                return pathNodeType14;
            }
            if (entity.getPathNodeTypeWeight(pathNodeType14) < entity.getPathNodeTypeWeight(pathNodeType13)) {
                continue;
            }
            pathNodeType13 = pathNodeType14;
        }
        if (pathNodeType12 == PathNodeType.b && entity.getPathNodeTypeWeight(pathNodeType13) == 0.0f) {
            return PathNodeType.b;
        }
        return pathNodeType13;
    }
    
    @Override
    public PathNodeType getPathNodeType(final BlockView blockView, final int x, final int y, final int integer4) {
        PathNodeType pathNodeType5 = this.getBasicPathNodeType(blockView, x, y, integer4);
        if (pathNodeType5 == PathNodeType.b && y >= 1) {
            final Block block6 = blockView.getBlockState(new BlockPos(x, y - 1, integer4)).getBlock();
            final PathNodeType pathNodeType6 = this.getBasicPathNodeType(blockView, x, y - 1, integer4);
            if (pathNodeType6 == PathNodeType.k || block6 == Blocks.iB || pathNodeType6 == PathNodeType.f || block6 == Blocks.lV) {
                pathNodeType5 = PathNodeType.k;
            }
            else if (pathNodeType6 == PathNodeType.m) {
                pathNodeType5 = PathNodeType.m;
            }
            else if (pathNodeType6 == PathNodeType.o) {
                pathNodeType5 = PathNodeType.o;
            }
            else {
                pathNodeType5 = ((pathNodeType6 == PathNodeType.c || pathNodeType6 == PathNodeType.b || pathNodeType6 == PathNodeType.g) ? PathNodeType.b : PathNodeType.c);
            }
        }
        pathNodeType5 = this.a(blockView, x, y, integer4, pathNodeType5);
        return pathNodeType5;
    }
    
    private PathNodeType a(final MobEntity mobEntity, final BlockPos blockPos) {
        return this.a(mobEntity, blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }
    
    private PathNodeType a(final MobEntity mobEntity, final int integer2, final int integer3, final int integer4) {
        return this.getPathNodeType(this.blockView, integer2, integer3, integer4, mobEntity, this.d, this.e, this.f, this.canPathThroughDoors(), this.canEnterOpenDoors());
    }
}
