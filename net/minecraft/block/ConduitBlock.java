package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.item.ItemStack;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.FluidState;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.BooleanProperty;

public class ConduitBlock extends BlockWithEntity implements Waterloggable
{
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape SHAPE;
    
    public ConduitBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)ConduitBlock.WATERLOGGED, true));
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(ConduitBlock.WATERLOGGED);
    }
    
    @Override
    public BlockEntity createBlockEntity(final BlockView view) {
        return new ConduitBlockEntity();
    }
    
    @Override
    public BlockRenderType getRenderType(final BlockState state) {
        return BlockRenderType.b;
    }
    
    @Override
    public FluidState getFluidState(final BlockState state) {
        if (state.<Boolean>get((Property<Boolean>)ConduitBlock.WATERLOGGED)) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (state.<Boolean>get((Property<Boolean>)ConduitBlock.WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return ConduitBlock.SHAPE;
    }
    
    @Override
    public void onPlaced(final World world, final BlockPos pos, final BlockState state, @Nullable final LivingEntity placer, final ItemStack itemStack) {
        if (itemStack.hasDisplayName()) {
            final BlockEntity blockEntity6 = world.getBlockEntity(pos);
            if (blockEntity6 instanceof BeaconBlockEntity) {
                ((BeaconBlockEntity)blockEntity6).setCustomName(itemStack.getDisplayName());
            }
        }
    }
    
    @Nullable
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final FluidState fluidState2 = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)ConduitBlock.WATERLOGGED, fluidState2.matches(FluidTags.a) && fluidState2.getLevel() == 8);
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        return false;
    }
    
    static {
        WATERLOGGED = Properties.WATERLOGGED;
        SHAPE = Block.createCuboidShape(5.0, 5.0, 5.0, 11.0, 11.0, 11.0);
    }
}
