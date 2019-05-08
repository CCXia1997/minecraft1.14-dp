package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.world.ViewableWorld;
import net.minecraft.fluid.Fluid;
import net.minecraft.world.IWorld;
import net.minecraft.item.ItemStack;
import javax.annotation.Nullable;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.Direction;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.block.enums.SlabType;
import net.minecraft.state.property.EnumProperty;

public class SlabBlock extends Block implements Waterloggable
{
    public static final EnumProperty<SlabType> TYPE;
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape BOTTOM_SHAPE;
    protected static final VoxelShape TOP_SHAPE;
    
    public SlabBlock(final Settings settings) {
        super(settings);
        this.setDefaultState((((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).with(SlabBlock.TYPE, SlabType.b)).<Comparable, Boolean>with((Property<Comparable>)SlabBlock.WATERLOGGED, false));
    }
    
    @Override
    public boolean n(final BlockState state) {
        return state.<SlabType>get(SlabBlock.TYPE) != SlabType.c;
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(SlabBlock.TYPE, SlabBlock.WATERLOGGED);
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        final SlabType slabType5 = state.<SlabType>get(SlabBlock.TYPE);
        switch (slabType5) {
            case c: {
                return VoxelShapes.fullCube();
            }
            case a: {
                return SlabBlock.TOP_SHAPE;
            }
            default: {
                return SlabBlock.BOTTOM_SHAPE;
            }
        }
    }
    
    @Nullable
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final BlockPos blockPos2 = ctx.getBlockPos();
        final BlockState blockState3 = ctx.getWorld().getBlockState(blockPos2);
        if (blockState3.getBlock() == this) {
            return (((AbstractPropertyContainer<O, BlockState>)blockState3).with(SlabBlock.TYPE, SlabType.c)).<Comparable, Boolean>with((Property<Comparable>)SlabBlock.WATERLOGGED, false);
        }
        final FluidState fluidState4 = ctx.getWorld().getFluidState(blockPos2);
        final BlockState blockState4 = (((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).with(SlabBlock.TYPE, SlabType.b)).<Comparable, Boolean>with((Property<Comparable>)SlabBlock.WATERLOGGED, fluidState4.getFluid() == Fluids.WATER);
        final Direction direction6 = ctx.getFacing();
        if (direction6 == Direction.DOWN || (direction6 != Direction.UP && ctx.getPos().y - blockPos2.getY() > 0.5)) {
            return ((AbstractPropertyContainer<O, BlockState>)blockState4).<SlabType, SlabType>with(SlabBlock.TYPE, SlabType.a);
        }
        return blockState4;
    }
    
    @Override
    public boolean canReplace(final BlockState state, final ItemPlacementContext ctx) {
        final ItemStack itemStack3 = ctx.getItemStack();
        final SlabType slabType4 = state.<SlabType>get(SlabBlock.TYPE);
        if (slabType4 == SlabType.c || itemStack3.getItem() != this.getItem()) {
            return false;
        }
        if (!ctx.c()) {
            return true;
        }
        final boolean boolean5 = ctx.getPos().y - ctx.getBlockPos().getY() > 0.5;
        final Direction direction6 = ctx.getFacing();
        if (slabType4 == SlabType.b) {
            return direction6 == Direction.UP || (boolean5 && direction6.getAxis().isHorizontal());
        }
        return direction6 == Direction.DOWN || (!boolean5 && direction6.getAxis().isHorizontal());
    }
    
    @Override
    public FluidState getFluidState(final BlockState state) {
        if (state.<Boolean>get((Property<Boolean>)SlabBlock.WATERLOGGED)) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }
    
    @Override
    public boolean tryFillWithFluid(final IWorld world, final BlockPos pos, final BlockState state, final FluidState fluidState) {
        return state.<SlabType>get(SlabBlock.TYPE) != SlabType.c && super.tryFillWithFluid(world, pos, state, fluidState);
    }
    
    @Override
    public boolean canFillWithFluid(final BlockView view, final BlockPos pos, final BlockState state, final Fluid fluid) {
        return state.<SlabType>get(SlabBlock.TYPE) != SlabType.c && super.canFillWithFluid(view, pos, state, fluid);
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (state.<Boolean>get((Property<Boolean>)SlabBlock.WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        switch (env) {
            case a: {
                return world.<SlabType>get(SlabBlock.TYPE) == SlabType.b;
            }
            case b: {
                return view.getFluidState(pos).matches(FluidTags.a);
            }
            case c: {
                return false;
            }
            default: {
                return false;
            }
        }
    }
    
    static {
        TYPE = Properties.SLAB_TYPE;
        WATERLOGGED = Properties.WATERLOGGED;
        BOTTOM_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
        TOP_SHAPE = Block.createCuboidShape(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);
    }
}
