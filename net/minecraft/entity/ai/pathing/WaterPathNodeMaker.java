package net.minecraft.entity.ai.pathing;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.BlockView;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class WaterPathNodeMaker extends PathNodeMaker
{
    private final boolean j;
    
    public WaterPathNodeMaker(final boolean boolean1) {
        this.j = boolean1;
    }
    
    @Override
    public PathNode getStart() {
        return super.getPathNode(MathHelper.floor(this.entity.getBoundingBox().minX), MathHelper.floor(this.entity.getBoundingBox().minY + 0.5), MathHelper.floor(this.entity.getBoundingBox().minZ));
    }
    
    @Override
    public PathNode getPathNode(final double x, final double y, final double z) {
        return super.getPathNode(MathHelper.floor(x - this.entity.getWidth() / 2.0f), MathHelper.floor(y + 0.5), MathHelper.floor(z - this.entity.getWidth() / 2.0f));
    }
    
    @Override
    public int getPathNodes(final PathNode[] nodes, final PathNode startNode, final PathNode endNode, final float float4) {
        int integer5 = 0;
        for (final Direction direction9 : Direction.values()) {
            final PathNode pathNode10 = this.getPathNodeInWater(startNode.x + direction9.getOffsetX(), startNode.y + direction9.getOffsetY(), startNode.z + direction9.getOffsetZ());
            if (pathNode10 != null && !pathNode10.i && pathNode10.distance(endNode) < float4) {
                nodes[integer5++] = pathNode10;
            }
        }
        return integer5;
    }
    
    @Override
    public PathNodeType getPathNodeType(final BlockView blockView, final int x, final int y, final int z, final MobEntity entity, final int xSize, final int ySize, final int zSize, final boolean canPathThroughDoors, final boolean boolean10) {
        return this.getPathNodeType(blockView, x, y, z);
    }
    
    @Override
    public PathNodeType getPathNodeType(final BlockView blockView, final int x, final int y, final int integer4) {
        final BlockPos blockPos5 = new BlockPos(x, y, integer4);
        final FluidState fluidState6 = blockView.getFluidState(blockPos5);
        final BlockState blockState7 = blockView.getBlockState(blockPos5);
        if (fluidState6.isEmpty() && blockState7.canPlaceAtSide(blockView, blockPos5.down(), BlockPlacementEnvironment.b) && blockState7.isAir()) {
            return PathNodeType.s;
        }
        if (!fluidState6.matches(FluidTags.a) || !blockState7.canPlaceAtSide(blockView, blockPos5, BlockPlacementEnvironment.b)) {
            return PathNodeType.a;
        }
        return PathNodeType.g;
    }
    
    @Nullable
    private PathNode getPathNodeInWater(final int x, final int y, final int integer3) {
        final PathNodeType pathNodeType4 = this.getPathNodeType(x, y, integer3);
        if ((this.j && pathNodeType4 == PathNodeType.s) || pathNodeType4 == PathNodeType.g) {
            return this.getPathNode(x, y, integer3);
        }
        return null;
    }
    
    @Nullable
    @Override
    protected PathNode getPathNode(final int x, final int y, final int integer3) {
        PathNode pathNode4 = null;
        final PathNodeType pathNodeType5 = this.getPathNodeType(this.entity.world, x, y, integer3);
        final float float6 = this.entity.getPathNodeTypeWeight(pathNodeType5);
        if (float6 >= 0.0f) {
            pathNode4 = super.getPathNode(x, y, integer3);
            pathNode4.type = pathNodeType5;
            pathNode4.l = Math.max(pathNode4.l, float6);
            if (this.blockView.getFluidState(new BlockPos(x, y, integer3)).isEmpty()) {
                final PathNode pathNode5 = pathNode4;
                pathNode5.l += 8.0f;
            }
        }
        if (pathNodeType5 == PathNodeType.b) {
            return pathNode4;
        }
        return pathNode4;
    }
    
    private PathNodeType getPathNodeType(final int x, final int y, final int integer3) {
        final BlockPos.Mutable mutable4 = new BlockPos.Mutable();
        for (int integer4 = x; integer4 < x + this.d; ++integer4) {
            for (int integer5 = y; integer5 < y + this.e; ++integer5) {
                for (int integer6 = integer3; integer6 < integer3 + this.f; ++integer6) {
                    final FluidState fluidState8 = this.blockView.getFluidState(mutable4.set(integer4, integer5, integer6));
                    final BlockState blockState9 = this.blockView.getBlockState(mutable4.set(integer4, integer5, integer6));
                    if (fluidState8.isEmpty() && blockState9.canPlaceAtSide(this.blockView, mutable4.down(), BlockPlacementEnvironment.b) && blockState9.isAir()) {
                        return PathNodeType.s;
                    }
                    if (!fluidState8.matches(FluidTags.a)) {
                        return PathNodeType.a;
                    }
                }
            }
        }
        final BlockState blockState10 = this.blockView.getBlockState(mutable4);
        if (blockState10.canPlaceAtSide(this.blockView, mutable4, BlockPlacementEnvironment.b)) {
            return PathNodeType.g;
        }
        return PathNodeType.a;
    }
}
