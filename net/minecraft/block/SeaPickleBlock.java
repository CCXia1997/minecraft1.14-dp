package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.state.StateFactory;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import javax.annotation.Nullable;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.Property;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntegerProperty;

public class SeaPickleBlock extends PlantBlock implements Fertilizable, Waterloggable
{
    public static final IntegerProperty PICKLES;
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape ONE_PICKLE_SHAPE;
    protected static final VoxelShape TWO_PICKLES_SHAPE;
    protected static final VoxelShape THREE_PICKLES_SHAPE;
    protected static final VoxelShape FOUR_PICKLES_SHAPE;
    
    protected SeaPickleBlock(final Settings settings) {
        super(settings);
        this.setDefaultState((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)SeaPickleBlock.PICKLES, 1)).<Comparable, Boolean>with((Property<Comparable>)SeaPickleBlock.WATERLOGGED, true));
    }
    
    @Override
    public int getLuminance(final BlockState state) {
        return this.isDry(state) ? 0 : (super.getLuminance(state) + 3 * state.<Integer>get((Property<Integer>)SeaPickleBlock.PICKLES));
    }
    
    @Nullable
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final BlockState blockState2 = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (blockState2.getBlock() == this) {
            return ((AbstractPropertyContainer<O, BlockState>)blockState2).<Comparable, Integer>with((Property<Comparable>)SeaPickleBlock.PICKLES, Math.min(4, blockState2.<Integer>get((Property<Integer>)SeaPickleBlock.PICKLES) + 1));
        }
        final FluidState fluidState3 = ctx.getWorld().getFluidState(ctx.getBlockPos());
        final boolean boolean4 = fluidState3.matches(FluidTags.a) && fluidState3.getLevel() == 8;
        return ((AbstractPropertyContainer<O, BlockState>)super.getPlacementState(ctx)).<Comparable, Boolean>with((Property<Comparable>)SeaPickleBlock.WATERLOGGED, boolean4);
    }
    
    private boolean isDry(final BlockState state) {
        return !state.<Boolean>get((Property<Boolean>)SeaPickleBlock.WATERLOGGED);
    }
    
    @Override
    protected boolean canPlantOnTop(final BlockState floor, final BlockView view, final BlockPos pos) {
        return !floor.getCollisionShape(view, pos).getFace(Direction.UP).isEmpty();
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        final BlockPos blockPos4 = pos.down();
        return this.canPlantOnTop(world.getBlockState(blockPos4), world, blockPos4);
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (!state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        if (state.<Boolean>get((Property<Boolean>)SeaPickleBlock.WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public boolean canReplace(final BlockState state, final ItemPlacementContext ctx) {
        return (ctx.getItemStack().getItem() == this.getItem() && state.<Integer>get((Property<Integer>)SeaPickleBlock.PICKLES) < 4) || super.canReplace(state, ctx);
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        switch (state.<Integer>get((Property<Integer>)SeaPickleBlock.PICKLES)) {
            default: {
                return SeaPickleBlock.ONE_PICKLE_SHAPE;
            }
            case 2: {
                return SeaPickleBlock.TWO_PICKLES_SHAPE;
            }
            case 3: {
                return SeaPickleBlock.THREE_PICKLES_SHAPE;
            }
            case 4: {
                return SeaPickleBlock.FOUR_PICKLES_SHAPE;
            }
        }
    }
    
    @Override
    public FluidState getFluidState(final BlockState state) {
        if (state.<Boolean>get((Property<Boolean>)SeaPickleBlock.WATERLOGGED)) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(SeaPickleBlock.PICKLES, SeaPickleBlock.WATERLOGGED);
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
    public void grow(final World world, final Random random, final BlockPos pos, final BlockState state) {
        if (!this.isDry(state) && world.getBlockState(pos.down()).matches(BlockTags.N)) {
            final int integer5 = 5;
            int integer6 = 1;
            final int integer7 = 2;
            int integer8 = 0;
            final int integer9 = pos.getX() - 2;
            int integer10 = 0;
            for (int integer11 = 0; integer11 < 5; ++integer11) {
                for (int integer12 = 0; integer12 < integer6; ++integer12) {
                    for (int integer13 = 2 + pos.getY() - 1, integer14 = integer13 - 2; integer14 < integer13; ++integer14) {
                        final BlockPos blockPos15 = new BlockPos(integer9 + integer11, integer14, pos.getZ() - integer10 + integer12);
                        if (blockPos15 != pos) {
                            if (random.nextInt(6) == 0 && world.getBlockState(blockPos15).getBlock() == Blocks.A) {
                                final BlockState blockState16 = world.getBlockState(blockPos15.down());
                                if (blockState16.matches(BlockTags.N)) {
                                    world.setBlockState(blockPos15, ((AbstractPropertyContainer<O, BlockState>)Blocks.kM.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)SeaPickleBlock.PICKLES, random.nextInt(4) + 1), 3);
                                }
                            }
                        }
                    }
                }
                if (integer8 < 2) {
                    integer6 += 2;
                    ++integer10;
                }
                else {
                    integer6 -= 2;
                    --integer10;
                }
                ++integer8;
            }
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Integer>with((Property<Comparable>)SeaPickleBlock.PICKLES, 4), 2);
        }
    }
    
    static {
        PICKLES = Properties.PICKLES;
        WATERLOGGED = Properties.WATERLOGGED;
        ONE_PICKLE_SHAPE = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 6.0, 10.0);
        TWO_PICKLES_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 6.0, 13.0);
        THREE_PICKLES_SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 6.0, 14.0);
        FOUR_PICKLES_SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 7.0, 14.0);
    }
}
