package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.fluid.Fluid;
import net.minecraft.state.StateFactory;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ViewableWorld;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.fluid.Fluids;
import javax.annotation.Nullable;
import net.minecraft.fluid.FluidState;
import net.minecraft.world.IWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.IntegerProperty;

public class KelpBlock extends Block implements FluidFillable
{
    public static final IntegerProperty AGE;
    protected static final VoxelShape SHAPE;
    
    protected KelpBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)KelpBlock.AGE, 0));
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return KelpBlock.SHAPE;
    }
    
    @Nullable
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final FluidState fluidState2 = ctx.getWorld().getFluidState(ctx.getBlockPos());
        if (fluidState2.matches(FluidTags.a) && fluidState2.getLevel() == 8) {
            return this.getPlacementState(ctx.getWorld());
        }
        return null;
    }
    
    public BlockState getPlacementState(final IWorld world) {
        return ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)KelpBlock.AGE, world.getRandom().nextInt(25));
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
            return;
        }
        final BlockPos blockPos5 = pos.up();
        final BlockState blockState6 = world.getBlockState(blockPos5);
        if (blockState6.getBlock() == Blocks.A && state.<Integer>get((Property<Integer>)KelpBlock.AGE) < 25 && random.nextDouble() < 0.14) {
            world.setBlockState(blockPos5, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable>cycle((Property<Comparable>)KelpBlock.AGE));
        }
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        final BlockPos blockPos4 = pos.down();
        final BlockState blockState5 = world.getBlockState(blockPos4);
        final Block block6 = blockState5.getBlock();
        return block6 != Blocks.iB && (block6 == this || block6 == Blocks.jV || Block.isSolidFullSquare(blockState5, world, blockPos4, Direction.UP));
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (!state.canPlaceAt(world, pos)) {
            if (facing == Direction.DOWN) {
                return Blocks.AIR.getDefaultState();
            }
            world.getBlockTickScheduler().schedule(pos, this, 1);
        }
        if (facing == Direction.UP && neighborState.getBlock() == this) {
            return Blocks.jV.getDefaultState();
        }
        world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(KelpBlock.AGE);
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
        AGE = Properties.AGE_25;
        SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 9.0, 16.0);
    }
}
