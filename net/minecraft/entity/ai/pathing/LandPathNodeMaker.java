package net.minecraft.entity.ai.pathing;

import net.minecraft.fluid.FluidState;
import net.minecraft.block.BlockState;
import net.minecraft.tag.FluidTags;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.state.property.Property;
import net.minecraft.block.Material;
import net.minecraft.block.DoorBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.block.AbstractRailBlock;
import java.util.EnumSet;
import net.minecraft.util.shape.VoxelShape;
import javax.annotation.Nullable;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.block.Block;
import com.google.common.collect.Sets;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.entity.Entity;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.BlockView;

public class LandPathNodeMaker extends PathNodeMaker
{
    protected float waterPathNodeTypeWeight;
    
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
            --integer1;
        }
        else if (this.entity.onGround) {
            integer1 = MathHelper.floor(this.entity.getBoundingBox().minY + 0.5);
        }
        else {
            BlockPos blockPos2;
            for (blockPos2 = new BlockPos(this.entity); (this.blockView.getBlockState(blockPos2).isAir() || this.blockView.getBlockState(blockPos2).canPlaceAtSide(this.blockView, blockPos2, BlockPlacementEnvironment.a)) && blockPos2.getY() > 0; blockPos2 = blockPos2.down()) {}
            integer1 = blockPos2.up().getY();
        }
        BlockPos blockPos2 = new BlockPos(this.entity);
        final PathNodeType pathNodeType3 = this.getPathNodeType(this.entity, blockPos2.getX(), integer1, blockPos2.getZ());
        if (this.entity.getPathNodeTypeWeight(pathNodeType3) < 0.0f) {
            final Set<BlockPos> set4 = Sets.newHashSet();
            set4.add(new BlockPos(this.entity.getBoundingBox().minX, integer1, this.entity.getBoundingBox().minZ));
            set4.add(new BlockPos(this.entity.getBoundingBox().minX, integer1, this.entity.getBoundingBox().maxZ));
            set4.add(new BlockPos(this.entity.getBoundingBox().maxX, integer1, this.entity.getBoundingBox().minZ));
            set4.add(new BlockPos(this.entity.getBoundingBox().maxX, integer1, this.entity.getBoundingBox().maxZ));
            for (final BlockPos blockPos3 : set4) {
                final PathNodeType pathNodeType4 = this.getPathNodeType(this.entity, blockPos3);
                if (this.entity.getPathNodeTypeWeight(pathNodeType4) >= 0.0f) {
                    return this.getPathNode(blockPos3.getX(), blockPos3.getY(), blockPos3.getZ());
                }
            }
        }
        return this.getPathNode(blockPos2.getX(), integer1, blockPos2.getZ());
    }
    
    @Override
    public PathNode getPathNode(final double x, final double y, final double z) {
        return this.getPathNode(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
    }
    
    @Override
    public int getPathNodes(final PathNode[] nodes, final PathNode startNode, final PathNode endNode, final float float4) {
        int integer5 = 0;
        int integer6 = 0;
        final PathNodeType pathNodeType7 = this.getPathNodeType(this.entity, startNode.x, startNode.y + 1, startNode.z);
        if (this.entity.getPathNodeTypeWeight(pathNodeType7) >= 0.0f) {
            integer6 = MathHelper.floor(Math.max(1.0f, this.entity.stepHeight));
        }
        final double double8 = a(this.blockView, new BlockPos(startNode.x, startNode.y, startNode.z));
        final PathNode pathNode10 = this.getPathNode(startNode.x, startNode.y, startNode.z + 1, integer6, double8, Direction.SOUTH);
        final PathNode pathNode11 = this.getPathNode(startNode.x - 1, startNode.y, startNode.z, integer6, double8, Direction.WEST);
        final PathNode pathNode12 = this.getPathNode(startNode.x + 1, startNode.y, startNode.z, integer6, double8, Direction.EAST);
        final PathNode pathNode13 = this.getPathNode(startNode.x, startNode.y, startNode.z - 1, integer6, double8, Direction.NORTH);
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
        final boolean boolean14 = pathNode13 == null || pathNode13.type == PathNodeType.b || pathNode13.l != 0.0f;
        final boolean boolean15 = pathNode10 == null || pathNode10.type == PathNodeType.b || pathNode10.l != 0.0f;
        final boolean boolean16 = pathNode12 == null || pathNode12.type == PathNodeType.b || pathNode12.l != 0.0f;
        final boolean boolean17 = pathNode11 == null || pathNode11.type == PathNodeType.b || pathNode11.l != 0.0f;
        if (boolean14 && boolean17) {
            final PathNode pathNode14 = this.getPathNode(startNode.x - 1, startNode.y, startNode.z - 1, integer6, double8, Direction.NORTH);
            if (pathNode14 != null && !pathNode14.i && pathNode14.distance(endNode) < float4) {
                nodes[integer5++] = pathNode14;
            }
        }
        if (boolean14 && boolean16) {
            final PathNode pathNode14 = this.getPathNode(startNode.x + 1, startNode.y, startNode.z - 1, integer6, double8, Direction.NORTH);
            if (pathNode14 != null && !pathNode14.i && pathNode14.distance(endNode) < float4) {
                nodes[integer5++] = pathNode14;
            }
        }
        if (boolean15 && boolean17) {
            final PathNode pathNode14 = this.getPathNode(startNode.x - 1, startNode.y, startNode.z + 1, integer6, double8, Direction.SOUTH);
            if (pathNode14 != null && !pathNode14.i && pathNode14.distance(endNode) < float4) {
                nodes[integer5++] = pathNode14;
            }
        }
        if (boolean15 && boolean16) {
            final PathNode pathNode14 = this.getPathNode(startNode.x + 1, startNode.y, startNode.z + 1, integer6, double8, Direction.SOUTH);
            if (pathNode14 != null && !pathNode14.i && pathNode14.distance(endNode) < float4) {
                nodes[integer5++] = pathNode14;
            }
        }
        return integer5;
    }
    
    @Nullable
    private PathNode getPathNode(final int x, int y, final int z, final int maxYStep, final double double5, final Direction direction7) {
        PathNode pathNode8 = null;
        final BlockPos blockPos9 = new BlockPos(x, y, z);
        final double double6 = a(this.blockView, blockPos9);
        if (double6 - double5 > 1.125) {
            return null;
        }
        PathNodeType pathNodeType12 = this.getPathNodeType(this.entity, x, y, z);
        float float13 = this.entity.getPathNodeTypeWeight(pathNodeType12);
        final double double7 = this.entity.getWidth() / 2.0;
        if (float13 >= 0.0f) {
            pathNode8 = this.getPathNode(x, y, z);
            pathNode8.type = pathNodeType12;
            pathNode8.l = Math.max(pathNode8.l, float13);
        }
        if (pathNodeType12 == PathNodeType.c) {
            return pathNode8;
        }
        if (pathNode8 == null && maxYStep > 0 && pathNodeType12 != PathNodeType.e && pathNodeType12 != PathNodeType.d) {
            pathNode8 = this.getPathNode(x, y + 1, z, maxYStep - 1, double5, direction7);
            if (pathNode8 != null && (pathNode8.type == PathNodeType.b || pathNode8.type == PathNodeType.c) && this.entity.getWidth() < 1.0f) {
                final double double8 = x - direction7.getOffsetX() + 0.5;
                final double double9 = z - direction7.getOffsetZ() + 0.5;
                final BoundingBox boundingBox20 = new BoundingBox(double8 - double7, a(this.blockView, new BlockPos(double8, y + 1, double9)) + 0.001, double9 - double7, double8 + double7, this.entity.getHeight() + a(this.blockView, blockPos9.up()) - 0.002, double9 + double7);
                if (!this.entity.world.doesNotCollide(this.entity, boundingBox20)) {
                    pathNode8 = null;
                }
            }
        }
        if (pathNodeType12 == PathNodeType.g && !this.canSwim()) {
            if (this.getPathNodeType(this.entity, x, y - 1, z) != PathNodeType.g) {
                return pathNode8;
            }
            while (y > 0) {
                --y;
                pathNodeType12 = this.getPathNodeType(this.entity, x, y, z);
                if (pathNodeType12 != PathNodeType.g) {
                    return pathNode8;
                }
                pathNode8 = this.getPathNode(x, y, z);
                pathNode8.type = pathNodeType12;
                pathNode8.l = Math.max(pathNode8.l, this.entity.getPathNodeTypeWeight(pathNodeType12));
            }
        }
        if (pathNodeType12 == PathNodeType.b) {
            final BoundingBox boundingBox21 = new BoundingBox(x - double7 + 0.5, y + 0.001, z - double7 + 0.5, x + double7 + 0.5, y + this.entity.getHeight(), z + double7 + 0.5);
            if (!this.entity.world.doesNotCollide(this.entity, boundingBox21)) {
                return null;
            }
            if (this.entity.getWidth() >= 1.0f) {
                final PathNodeType pathNodeType13 = this.getPathNodeType(this.entity, x, y - 1, z);
                if (pathNodeType13 == PathNodeType.a) {
                    pathNode8 = this.getPathNode(x, y, z);
                    pathNode8.type = PathNodeType.c;
                    pathNode8.l = Math.max(pathNode8.l, float13);
                    return pathNode8;
                }
            }
            int integer17 = 0;
            while (y > 0 && pathNodeType12 == PathNodeType.b) {
                --y;
                if (integer17++ >= this.entity.getSafeFallDistance()) {
                    return null;
                }
                pathNodeType12 = this.getPathNodeType(this.entity, x, y, z);
                float13 = this.entity.getPathNodeTypeWeight(pathNodeType12);
                if (pathNodeType12 != PathNodeType.b && float13 >= 0.0f) {
                    pathNode8 = this.getPathNode(x, y, z);
                    pathNode8.type = pathNodeType12;
                    pathNode8.l = Math.max(pathNode8.l, float13);
                    break;
                }
                if (float13 < 0.0f) {
                    return null;
                }
            }
        }
        return pathNode8;
    }
    
    public static double a(final BlockView blockView, final BlockPos blockPos) {
        final BlockPos blockPos2 = blockPos.down();
        final VoxelShape voxelShape4 = blockView.getBlockState(blockPos2).getCollisionShape(blockView, blockPos2);
        return blockPos2.getY() + (voxelShape4.isEmpty() ? 0.0 : voxelShape4.getMaximum(Direction.Axis.Y));
    }
    
    @Override
    public PathNodeType getPathNodeType(final BlockView blockView, final int x, final int y, final int z, final MobEntity entity, final int xSize, final int ySize, final int zSize, final boolean canPathThroughDoors, final boolean boolean10) {
        final EnumSet<PathNodeType> enumSet11 = EnumSet.<PathNodeType>noneOf(PathNodeType.class);
        PathNodeType pathNodeType12 = PathNodeType.a;
        final double double13 = entity.getWidth() / 2.0;
        final BlockPos blockPos15 = new BlockPos(entity);
        pathNodeType12 = this.a(blockView, x, y, z, xSize, ySize, zSize, canPathThroughDoors, boolean10, enumSet11, pathNodeType12, blockPos15);
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
    
    public PathNodeType a(final BlockView blockView, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final boolean boolean8, final boolean boolean9, final EnumSet<PathNodeType> enumSet, PathNodeType pathNodeType, final BlockPos blockPos) {
        for (int integer8 = 0; integer8 < integer5; ++integer8) {
            for (int integer9 = 0; integer9 < integer6; ++integer9) {
                for (int integer10 = 0; integer10 < integer7; ++integer10) {
                    final int integer11 = integer8 + integer2;
                    final int integer12 = integer9 + integer3;
                    final int integer13 = integer10 + integer4;
                    PathNodeType pathNodeType2 = this.getPathNodeType(blockView, integer11, integer12, integer13);
                    pathNodeType2 = this.a(blockView, boolean8, boolean9, blockPos, pathNodeType2);
                    if (integer8 == 0 && integer9 == 0 && integer10 == 0) {
                        pathNodeType = pathNodeType2;
                    }
                    enumSet.add(pathNodeType2);
                }
            }
        }
        return pathNodeType;
    }
    
    protected PathNodeType a(final BlockView blockView, final boolean boolean2, final boolean boolean3, final BlockPos blockPos, PathNodeType pathNodeType) {
        if (pathNodeType == PathNodeType.q && boolean2 && boolean3) {
            pathNodeType = PathNodeType.c;
        }
        if (pathNodeType == PathNodeType.p && !boolean3) {
            pathNodeType = PathNodeType.a;
        }
        if (pathNodeType == PathNodeType.i && !(blockView.getBlockState(blockPos).getBlock() instanceof AbstractRailBlock) && !(blockView.getBlockState(blockPos.down()).getBlock() instanceof AbstractRailBlock)) {
            pathNodeType = PathNodeType.e;
        }
        if (pathNodeType == PathNodeType.t) {
            pathNodeType = PathNodeType.a;
        }
        return pathNodeType;
    }
    
    private PathNodeType getPathNodeType(final MobEntity entity, final BlockPos blockPos) {
        return this.getPathNodeType(entity, blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }
    
    private PathNodeType getPathNodeType(final MobEntity entity, final int x, final int y, final int integer4) {
        return this.getPathNodeType(this.blockView, x, y, integer4, entity, this.d, this.e, this.f, this.canPathThroughDoors(), this.canEnterOpenDoors());
    }
    
    @Override
    public PathNodeType getPathNodeType(final BlockView blockView, final int x, final int y, final int integer4) {
        PathNodeType pathNodeType5 = this.getBasicPathNodeType(blockView, x, y, integer4);
        if (pathNodeType5 == PathNodeType.b && y >= 1) {
            final Block block6 = blockView.getBlockState(new BlockPos(x, y - 1, integer4)).getBlock();
            final PathNodeType pathNodeType6 = this.getBasicPathNodeType(blockView, x, y - 1, integer4);
            pathNodeType5 = ((pathNodeType6 == PathNodeType.c || pathNodeType6 == PathNodeType.b || pathNodeType6 == PathNodeType.g || pathNodeType6 == PathNodeType.f) ? PathNodeType.b : PathNodeType.c);
            if (pathNodeType6 == PathNodeType.k || block6 == Blocks.iB || block6 == Blocks.lV) {
                pathNodeType5 = PathNodeType.k;
            }
            if (pathNodeType6 == PathNodeType.m) {
                pathNodeType5 = PathNodeType.m;
            }
            if (pathNodeType6 == PathNodeType.o) {
                pathNodeType5 = PathNodeType.o;
            }
        }
        pathNodeType5 = this.a(blockView, x, y, integer4, pathNodeType5);
        return pathNodeType5;
    }
    
    public PathNodeType a(final BlockView blockView, final int integer2, final int integer3, final int integer4, PathNodeType pathNodeType) {
        if (pathNodeType == PathNodeType.c) {
            try (final BlockPos.PooledMutable pooledMutable6 = BlockPos.PooledMutable.get()) {
                for (int integer5 = -1; integer5 <= 1; ++integer5) {
                    for (int integer6 = -1; integer6 <= 1; ++integer6) {
                        if (integer5 != 0 || integer6 != 0) {
                            final Block block10 = blockView.getBlockState(pooledMutable6.set(integer5 + integer2, integer3, integer6 + integer4)).getBlock();
                            if (block10 == Blocks.cD) {
                                pathNodeType = PathNodeType.l;
                            }
                            else if (block10 == Blocks.bM) {
                                pathNodeType = PathNodeType.j;
                            }
                            else if (block10 == Blocks.lW) {
                                pathNodeType = PathNodeType.n;
                            }
                        }
                    }
                }
            }
        }
        return pathNodeType;
    }
    
    protected PathNodeType getBasicPathNodeType(final BlockView blockView, final int x, final int y, final int integer4) {
        final BlockPos blockPos5 = new BlockPos(x, y, integer4);
        final BlockState blockState6 = blockView.getBlockState(blockPos5);
        final Block block7 = blockState6.getBlock();
        final Material material8 = blockState6.getMaterial();
        if (blockState6.isAir()) {
            return PathNodeType.b;
        }
        if (block7.matches(BlockTags.D) || block7 == Blocks.dM) {
            return PathNodeType.d;
        }
        if (block7 == Blocks.bM) {
            return PathNodeType.k;
        }
        if (block7 == Blocks.cD) {
            return PathNodeType.m;
        }
        if (block7 == Blocks.lW) {
            return PathNodeType.o;
        }
        if (block7 instanceof DoorBlock && material8 == Material.WOOD && !blockState6.<Boolean>get((Property<Boolean>)DoorBlock.OPEN)) {
            return PathNodeType.q;
        }
        if (block7 instanceof DoorBlock && material8 == Material.METAL && !blockState6.<Boolean>get((Property<Boolean>)DoorBlock.OPEN)) {
            return PathNodeType.r;
        }
        if (block7 instanceof DoorBlock && blockState6.<Boolean>get((Property<Boolean>)DoorBlock.OPEN)) {
            return PathNodeType.p;
        }
        if (block7 instanceof AbstractRailBlock) {
            return PathNodeType.i;
        }
        if (block7 instanceof LeavesBlock) {
            return PathNodeType.t;
        }
        if (block7.matches(BlockTags.G) || block7.matches(BlockTags.z) || (block7 instanceof FenceGateBlock && !blockState6.<Boolean>get((Property<Boolean>)FenceGateBlock.OPEN))) {
            return PathNodeType.e;
        }
        final FluidState fluidState9 = blockView.getFluidState(blockPos5);
        if (fluidState9.matches(FluidTags.a)) {
            return PathNodeType.g;
        }
        if (fluidState9.matches(FluidTags.b)) {
            return PathNodeType.f;
        }
        if (blockState6.canPlaceAtSide(blockView, blockPos5, BlockPlacementEnvironment.a)) {
            return PathNodeType.b;
        }
        return PathNodeType.a;
    }
}
