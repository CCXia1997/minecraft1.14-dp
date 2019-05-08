package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.state.property.Property;
import net.minecraft.state.property.BooleanProperty;

public class SnowyBlock extends Block
{
    public static final BooleanProperty SNOWY;
    
    protected SnowyBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)SnowyBlock.SNOWY, false));
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (facing == Direction.UP) {
            final Block block7 = neighborState.getBlock();
            return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)SnowyBlock.SNOWY, block7 == Blocks.cC || block7 == Blocks.cA);
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final Block block2 = ctx.getWorld().getBlockState(ctx.getBlockPos().up()).getBlock();
        return ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)SnowyBlock.SNOWY, block2 == Blocks.cC || block2 == Blocks.cA);
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(SnowyBlock.SNOWY);
    }
    
    static {
        SNOWY = Properties.SNOWY;
    }
}
