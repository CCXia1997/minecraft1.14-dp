package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.util.math.Direction;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.Property;
import net.minecraft.state.StateFactory;

public class GlazedTerracottaBlock extends HorizontalFacingBlock
{
    public GlazedTerracottaBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(GlazedTerracottaBlock.FACING);
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        return ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)GlazedTerracottaBlock.FACING, ctx.getPlayerHorizontalFacing().getOpposite());
    }
    
    @Override
    public PistonBehavior getPistonBehavior(final BlockState state) {
        return PistonBehavior.e;
    }
}
