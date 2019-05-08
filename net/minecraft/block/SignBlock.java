package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.MathHelper;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;
import net.minecraft.state.property.Property;
import net.minecraft.state.property.IntegerProperty;

public class SignBlock extends AbstractSignBlock
{
    public static final IntegerProperty ROTATION;
    
    public SignBlock(final Settings settings) {
        super(settings);
        this.setDefaultState((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)SignBlock.ROTATION, 0)).<Comparable, Boolean>with((Property<Comparable>)SignBlock.WATERLOGGED, false));
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        return world.getBlockState(pos.down()).getMaterial().isSolid();
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final FluidState fluidState2 = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return (((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).with((Property<Comparable>)SignBlock.ROTATION, MathHelper.floor((180.0f + ctx.getPlayerYaw()) * 16.0f / 360.0f + 0.5) & 0xF)).<Comparable, Boolean>with((Property<Comparable>)SignBlock.WATERLOGGED, fluidState2.getFluid() == Fluids.WATER);
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (facing == Direction.DOWN && !this.canPlaceAt(state, world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Integer>with((Property<Comparable>)SignBlock.ROTATION, rotation.rotate(state.<Integer>get((Property<Integer>)SignBlock.ROTATION), 16));
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Integer>with((Property<Comparable>)SignBlock.ROTATION, mirror.mirror(state.<Integer>get((Property<Integer>)SignBlock.ROTATION), 16));
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(SignBlock.ROTATION, SignBlock.WATERLOGGED);
    }
    
    static {
        ROTATION = Properties.ROTATION_16;
    }
}
