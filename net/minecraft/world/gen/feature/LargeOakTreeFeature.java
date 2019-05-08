package net.minecraft.world.gen.feature;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.block.Blocks;
import com.google.common.collect.Lists;
import java.util.Random;
import java.util.Iterator;
import java.util.List;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.block.LogBlock;
import java.util.Objects;
import java.util.Set;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.TestableWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.BlockState;

public class LargeOakTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig>
{
    private static final BlockState LOG;
    private static final BlockState LEAVES;
    
    public LargeOakTreeFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer, final boolean emitNeighborBlockUpdates) {
        super(configDeserializer, emitNeighborBlockUpdates);
    }
    
    private void makeLeafLayer(final ModifiableTestableWorld world, final BlockPos pos, final float radius) {
        for (int integer4 = (int)(radius + 0.618), integer5 = -integer4; integer5 <= integer4; ++integer5) {
            for (int integer6 = -integer4; integer6 <= integer4; ++integer6) {
                if (Math.pow(Math.abs(integer5) + 0.5, 2.0) + Math.pow(Math.abs(integer6) + 0.5, 2.0) <= radius * radius) {
                    final BlockPos blockPos7 = pos.add(integer5, 0, integer6);
                    if (AbstractTreeFeature.isAirOrLeaves(world, blockPos7)) {
                        this.setBlockState(world, blockPos7, LargeOakTreeFeature.LEAVES);
                    }
                }
            }
        }
    }
    
    private float getBaseBranchSize(final int treeHeight, final int branchCount) {
        if (branchCount < treeHeight * 0.3f) {
            return -1.0f;
        }
        final float float3 = treeHeight / 2.0f;
        final float float4 = float3 - branchCount;
        float float5 = MathHelper.sqrt(float3 * float3 - float4 * float4);
        if (float4 == 0.0f) {
            float5 = float3;
        }
        else if (Math.abs(float4) >= float3) {
            return 0.0f;
        }
        return float5 * 0.5f;
    }
    
    private float getLeafRadiusForLayer(final int integer) {
        if (integer < 0 || integer >= 5) {
            return -1.0f;
        }
        if (integer == 0 || integer == 4) {
            return 2.0f;
        }
        return 3.0f;
    }
    
    private void makeLeaves(final ModifiableTestableWorld world, final BlockPos pos) {
        for (int integer3 = 0; integer3 < 5; ++integer3) {
            this.makeLeafLayer(world, pos.up(integer3), this.getLeafRadiusForLayer(integer3));
        }
    }
    
    private int makeOrCheckBranch(final Set<BlockPos> logPositions, final ModifiableTestableWorld world, final BlockPos start, final BlockPos end, final boolean make) {
        if (!make && Objects.equals(start, end)) {
            return -1;
        }
        final BlockPos blockPos6 = end.add(-start.getX(), -start.getY(), -start.getZ());
        final int integer7 = this.getLongestSide(blockPos6);
        final float float8 = blockPos6.getX() / (float)integer7;
        final float float9 = blockPos6.getY() / (float)integer7;
        final float float10 = blockPos6.getZ() / (float)integer7;
        for (int integer8 = 0; integer8 <= integer7; ++integer8) {
            final BlockPos blockPos7 = start.add(0.5f + integer8 * float8, 0.5f + integer8 * float9, 0.5f + integer8 * float10);
            if (make) {
                this.setBlockState(logPositions, world, blockPos7, ((AbstractPropertyContainer<O, BlockState>)LargeOakTreeFeature.LOG).<Direction.Axis, Direction.Axis>with(LogBlock.AXIS, this.getLogAxis(start, blockPos7)));
            }
            else if (!AbstractTreeFeature.canTreeReplace(world, blockPos7)) {
                return integer8;
            }
        }
        return -1;
    }
    
    private int getLongestSide(final BlockPos box) {
        final int integer2 = MathHelper.abs(box.getX());
        final int integer3 = MathHelper.abs(box.getY());
        final int integer4 = MathHelper.abs(box.getZ());
        if (integer4 > integer2 && integer4 > integer3) {
            return integer4;
        }
        if (integer3 > integer2) {
            return integer3;
        }
        return integer2;
    }
    
    private Direction.Axis getLogAxis(final BlockPos branchStart, final BlockPos branchEnd) {
        Direction.Axis axis3 = Direction.Axis.Y;
        final int integer4 = Math.abs(branchEnd.getX() - branchStart.getX());
        final int integer5 = Math.abs(branchEnd.getZ() - branchStart.getZ());
        final int integer6 = Math.max(integer4, integer5);
        if (integer6 > 0) {
            if (integer4 == integer6) {
                axis3 = Direction.Axis.X;
            }
            else if (integer5 == integer6) {
                axis3 = Direction.Axis.Z;
            }
        }
        return axis3;
    }
    
    private void makeLeaves(final ModifiableTestableWorld world, final int treeHeight, final BlockPos treePos, final List<BranchPosition> branchPositions) {
        for (final BranchPosition branchPosition6 : branchPositions) {
            if (this.isHighEnough(treeHeight, branchPosition6.getEndY() - treePos.getY())) {
                this.makeLeaves(world, branchPosition6);
            }
        }
    }
    
    private boolean isHighEnough(final int treeHeight, final int height) {
        return height >= treeHeight * 0.2;
    }
    
    private void makeTrunk(final Set<BlockPos> logPositions, final ModifiableTestableWorld world, final BlockPos pos, final int height) {
        this.makeOrCheckBranch(logPositions, world, pos, pos.up(height), true);
    }
    
    private void makeBranches(final Set<BlockPos> logPositions, final ModifiableTestableWorld world, final int treeHeight, final BlockPos treePosition, final List<BranchPosition> branchPositions) {
        for (final BranchPosition branchPosition7 : branchPositions) {
            final int integer8 = branchPosition7.getEndY();
            final BlockPos blockPos9 = new BlockPos(treePosition.getX(), integer8, treePosition.getZ());
            if (!blockPos9.equals(branchPosition7) && this.isHighEnough(treeHeight, integer8 - treePosition.getY())) {
                this.makeOrCheckBranch(logPositions, world, blockPos9, branchPosition7, true);
            }
        }
    }
    
    public boolean generate(final Set<BlockPos> logPositions, final ModifiableTestableWorld world, final Random random, final BlockPos pos) {
        final Random random2 = new Random(random.nextLong());
        final int integer6 = this.getTreeHeight(logPositions, world, pos, 5 + random2.nextInt(12));
        if (integer6 == -1) {
            return false;
        }
        this.setToDirt(world, pos.down());
        int integer7 = (int)(integer6 * 0.618);
        if (integer7 >= integer6) {
            integer7 = integer6 - 1;
        }
        final double double8 = 1.0;
        int integer8 = (int)(1.382 + Math.pow(1.0 * integer6 / 13.0, 2.0));
        if (integer8 < 1) {
            integer8 = 1;
        }
        final int integer9 = pos.getY() + integer7;
        int integer10 = integer6 - 5;
        final List<BranchPosition> list13 = Lists.newArrayList();
        list13.add(new BranchPosition(pos.up(integer10), integer9));
        while (integer10 >= 0) {
            final float float14 = this.getBaseBranchSize(integer6, integer10);
            if (float14 >= 0.0f) {
                for (int integer11 = 0; integer11 < integer8; ++integer11) {
                    final double double9 = 1.0;
                    final double double10 = 1.0 * float14 * (random2.nextFloat() + 0.328);
                    final double double11 = random2.nextFloat() * 2.0f * 3.141592653589793;
                    final double double12 = double10 * Math.sin(double11) + 0.5;
                    final double double13 = double10 * Math.cos(double11) + 0.5;
                    final BlockPos blockPos26 = pos.add(double12, integer10 - 1, double13);
                    final BlockPos blockPos27 = blockPos26.up(5);
                    if (this.makeOrCheckBranch(logPositions, world, blockPos26, blockPos27, false) == -1) {
                        final int integer12 = pos.getX() - blockPos26.getX();
                        final int integer13 = pos.getZ() - blockPos26.getZ();
                        final double double14 = blockPos26.getY() - Math.sqrt(integer12 * integer12 + integer13 * integer13) * 0.381;
                        final int integer14 = (double14 > integer9) ? integer9 : ((int)double14);
                        final BlockPos blockPos28 = new BlockPos(pos.getX(), integer14, pos.getZ());
                        if (this.makeOrCheckBranch(logPositions, world, blockPos28, blockPos26, false) == -1) {
                            list13.add(new BranchPosition(blockPos26, blockPos28.getY()));
                        }
                    }
                }
            }
            --integer10;
        }
        this.makeLeaves(world, integer6, pos, list13);
        this.makeTrunk(logPositions, world, pos, integer7);
        this.makeBranches(logPositions, world, integer6, pos, list13);
        return true;
    }
    
    private int getTreeHeight(final Set<BlockPos> logPositions, final ModifiableTestableWorld world, final BlockPos pos, final int height) {
        if (!AbstractTreeFeature.isDirtOrGrass(world, pos.down())) {
            return -1;
        }
        final int integer5 = this.makeOrCheckBranch(logPositions, world, pos, pos.up(height - 1), false);
        if (integer5 == -1) {
            return height;
        }
        if (integer5 < 6) {
            return -1;
        }
        return integer5;
    }
    
    static {
        LOG = Blocks.I.getDefaultState();
        LEAVES = Blocks.ag.getDefaultState();
    }
    
    static class BranchPosition extends BlockPos
    {
        private final int endY;
        
        public BranchPosition(final BlockPos pos, final int endY) {
            super(pos.getX(), pos.getY(), pos.getZ());
            this.endY = endY;
        }
        
        public int getEndY() {
            return this.endY;
        }
    }
}
