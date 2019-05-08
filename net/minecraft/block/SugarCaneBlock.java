package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.fluid.FluidState;
import java.util.Iterator;
import net.minecraft.tag.FluidTags;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ViewableWorld;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.IntegerProperty;

public class SugarCaneBlock extends Block
{
    public static final IntegerProperty AGE;
    protected static final VoxelShape SHAPE;
    
    protected SugarCaneBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)SugarCaneBlock.AGE, 0));
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return SugarCaneBlock.SHAPE;
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        if (!state.canPlaceAt(world, pos)) {
            world.breakBlock(pos, true);
        }
        else if (world.isAir(pos.up())) {
            int integer5;
            for (integer5 = 1; world.getBlockState(pos.down(integer5)).getBlock() == this; ++integer5) {}
            if (integer5 < 3) {
                final int integer6 = state.<Integer>get((Property<Integer>)SugarCaneBlock.AGE);
                if (integer6 == 15) {
                    world.setBlockState(pos.up(), this.getDefaultState());
                    world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Integer>with((Property<Comparable>)SugarCaneBlock.AGE, 0), 4);
                }
                else {
                    world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Integer>with((Property<Comparable>)SugarCaneBlock.AGE, integer6 + 1), 4);
                }
            }
        }
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (!state.canPlaceAt(world, pos)) {
            world.getBlockTickScheduler().schedule(pos, this, 1);
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        final Block block4 = world.getBlockState(pos.down()).getBlock();
        if (block4 == this) {
            return true;
        }
        if (block4 == Blocks.i || block4 == Blocks.j || block4 == Blocks.k || block4 == Blocks.l || block4 == Blocks.C || block4 == Blocks.D) {
            final BlockPos blockPos5 = pos.down();
            for (final Direction direction7 : Direction.Type.HORIZONTAL) {
                final BlockState blockState8 = world.getBlockState(blockPos5.offset(direction7));
                final FluidState fluidState9 = world.getFluidState(blockPos5.offset(direction7));
                if (fluidState9.matches(FluidTags.a) || blockState8.getBlock() == Blocks.iA) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(SugarCaneBlock.AGE);
    }
    
    static {
        AGE = Properties.AGE_15;
        SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
    }
}
