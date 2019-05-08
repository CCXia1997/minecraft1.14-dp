package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.util.BlockRotation;
import net.minecraft.state.property.DirectionProperty;

public abstract class HorizontalFacingBlock extends Block
{
    public static final DirectionProperty FACING;
    
    protected HorizontalFacingBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Direction>with((Property<Comparable>)HorizontalFacingBlock.FACING, rotation.rotate(state.<Direction>get((Property<Direction>)HorizontalFacingBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.<Direction>get((Property<Direction>)HorizontalFacingBlock.FACING)));
    }
    
    static {
        FACING = Properties.FACING_HORIZONTAL;
    }
}
