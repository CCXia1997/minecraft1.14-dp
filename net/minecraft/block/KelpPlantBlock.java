package net.minecraft.block;

import net.minecraft.fluid.Fluid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ViewableWorld;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.FluidState;

public class KelpPlantBlock extends Block implements FluidFillable
{
    private final KelpBlock kelpBlock;
    
    protected KelpPlantBlock(final KelpBlock kelpBlock, final Settings settings) {
        super(settings);
        this.kelpBlock = kelpBlock;
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    @Override
    public FluidState getFluidState(final BlockState state) {
        return Fluids.WATER.getStill(false);
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        if (!state.canPlaceAt(world, pos)) {
            world.breakBlock(pos, true);
        }
        super.onScheduledTick(state, world, pos, random);
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (facing == Direction.DOWN && !state.canPlaceAt(world, pos)) {
            world.getBlockTickScheduler().schedule(pos, this, 1);
        }
        if (facing == Direction.UP) {
            final Block block7 = neighborState.getBlock();
            if (block7 != this && block7 != this.kelpBlock) {
                return this.kelpBlock.getPlacementState(world);
            }
        }
        world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        final BlockPos blockPos4 = pos.down();
        final BlockState blockState5 = world.getBlockState(blockPos4);
        final Block block6 = blockState5.getBlock();
        return block6 != Blocks.iB && (block6 == this || Block.isSolidFullSquare(blockState5, world, blockPos4, Direction.UP));
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public ItemStack getPickStack(final BlockView world, final BlockPos pos, final BlockState state) {
        return new ItemStack(Blocks.jU);
    }
    
    @Override
    public boolean canFillWithFluid(final BlockView view, final BlockPos pos, final BlockState state, final Fluid fluid) {
        return false;
    }
    
    @Override
    public boolean tryFillWithFluid(final IWorld world, final BlockPos pos, final BlockState state, final FluidState fluidState) {
        return false;
    }
}
