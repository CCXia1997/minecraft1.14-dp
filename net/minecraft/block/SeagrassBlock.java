package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.fluid.Fluid;
import net.minecraft.state.property.Property;
import net.minecraft.block.enums.DoubleBlockHalf;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.ViewableWorld;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.IWorld;
import javax.annotation.Nullable;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.Direction;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.util.shape.VoxelShape;

public class SeagrassBlock extends PlantBlock implements Fertilizable, FluidFillable
{
    protected static final VoxelShape SHAPE;
    
    protected SeagrassBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return SeagrassBlock.SHAPE;
    }
    
    @Override
    protected boolean canPlantOnTop(final BlockState floor, final BlockView view, final BlockPos pos) {
        return Block.isSolidFullSquare(floor, view, pos, Direction.UP) && floor.getBlock() != Blocks.iB;
    }
    
    @Nullable
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final FluidState fluidState2 = ctx.getWorld().getFluidState(ctx.getBlockPos());
        if (fluidState2.matches(FluidTags.a) && fluidState2.getLevel() == 8) {
            return super.getPlacementState(ctx);
        }
        return null;
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        final BlockState blockState7 = super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
        if (!blockState7.isAir()) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return blockState7;
    }
    
    @Override
    public boolean isFertilizable(final BlockView world, final BlockPos pos, final BlockState state, final boolean boolean4) {
        return true;
    }
    
    @Override
    public boolean canGrow(final World world, final Random random, final BlockPos pos, final BlockState state) {
        return true;
    }
    
    @Override
    public FluidState getFluidState(final BlockState state) {
        return Fluids.WATER.getStill(false);
    }
    
    @Override
    public void grow(final World world, final Random random, final BlockPos pos, final BlockState state) {
        final BlockState blockState5 = Blocks.aU.getDefaultState();
        final BlockState blockState6 = ((AbstractPropertyContainer<O, BlockState>)blockState5).<DoubleBlockHalf, DoubleBlockHalf>with(TallSeagrassBlock.HALF, DoubleBlockHalf.a);
        final BlockPos blockPos7 = pos.up();
        if (world.getBlockState(blockPos7).getBlock() == Blocks.A) {
            world.setBlockState(pos, blockState5, 2);
            world.setBlockState(blockPos7, blockState6, 2);
        }
    }
    
    @Override
    public boolean canFillWithFluid(final BlockView view, final BlockPos pos, final BlockState state, final Fluid fluid) {
        return false;
    }
    
    @Override
    public boolean tryFillWithFluid(final IWorld world, final BlockPos pos, final BlockState state, final FluidState fluidState) {
        return false;
    }
    
    static {
        SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);
    }
}
