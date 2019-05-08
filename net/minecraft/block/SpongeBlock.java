package net.minecraft.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import java.util.Queue;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.IWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.Direction;
import net.minecraft.util.Pair;
import com.google.common.collect.Lists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpongeBlock extends Block
{
    protected SpongeBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public void onBlockAdded(final BlockState state, final World world, final BlockPos pos, final BlockState oldState, final boolean boolean5) {
        if (oldState.getBlock() == state.getBlock()) {
            return;
        }
        this.update(world, pos);
    }
    
    @Override
    public void neighborUpdate(final BlockState state, final World world, final BlockPos pos, final Block block, final BlockPos neighborPos, final boolean boolean6) {
        this.update(world, pos);
        super.neighborUpdate(state, world, pos, block, neighborPos, boolean6);
    }
    
    protected void update(final World world, final BlockPos blockPos) {
        if (this.absorbWater(world, blockPos)) {
            world.setBlockState(blockPos, Blocks.an.getDefaultState(), 2);
            world.playLevelEvent(2001, blockPos, Block.getRawIdFromState(Blocks.A.getDefaultState()));
        }
    }
    
    private boolean absorbWater(final World world, final BlockPos pos) {
        final Queue<Pair<BlockPos, Integer>> queue3 = Lists.newLinkedList();
        queue3.add(new Pair<BlockPos, Integer>(pos, 0));
        int integer4 = 0;
        while (!queue3.isEmpty()) {
            final Pair<BlockPos, Integer> pair5 = queue3.poll();
            final BlockPos blockPos6 = pair5.getLeft();
            final int integer5 = pair5.getRight();
            for (final Direction direction11 : Direction.values()) {
                final BlockPos blockPos7 = blockPos6.offset(direction11);
                final BlockState blockState13 = world.getBlockState(blockPos7);
                final FluidState fluidState14 = world.getFluidState(blockPos7);
                final Material material15 = blockState13.getMaterial();
                if (fluidState14.matches(FluidTags.a)) {
                    if (blockState13.getBlock() instanceof FluidDrainable && ((FluidDrainable)blockState13.getBlock()).tryDrainFluid(world, blockPos7, blockState13) != Fluids.EMPTY) {
                        ++integer4;
                        if (integer5 < 6) {
                            queue3.add(new Pair<BlockPos, Integer>(blockPos7, integer5 + 1));
                        }
                    }
                    else if (blockState13.getBlock() instanceof FluidBlock) {
                        world.setBlockState(blockPos7, Blocks.AIR.getDefaultState(), 3);
                        ++integer4;
                        if (integer5 < 6) {
                            queue3.add(new Pair<BlockPos, Integer>(blockPos7, integer5 + 1));
                        }
                    }
                    else if (material15 == Material.UNDERWATER_PLANT || material15 == Material.SEAGRASS) {
                        final BlockEntity blockEntity16 = blockState13.getBlock().hasBlockEntity() ? world.getBlockEntity(blockPos7) : null;
                        Block.dropStacks(blockState13, world, blockPos7, blockEntity16);
                        world.setBlockState(blockPos7, Blocks.AIR.getDefaultState(), 3);
                        ++integer4;
                        if (integer5 < 6) {
                            queue3.add(new Pair<BlockPos, Integer>(blockPos7, integer5 + 1));
                        }
                    }
                }
            }
            if (integer4 > 64) {
                break;
            }
        }
        return integer4 > 0;
    }
}
