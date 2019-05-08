package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockRotation;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.state.property.EnumProperty;

public class PillarBlock extends Block
{
    public static final EnumProperty<Direction.Axis> AXIS;
    
    public PillarBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Direction.Axis, Direction.Axis>with(PillarBlock.AXIS, Direction.Axis.Y));
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        switch (rotation) {
            case ROT_270:
            case ROT_90: {
                switch (state.<Direction.Axis>get(PillarBlock.AXIS)) {
                    case X: {
                        return ((AbstractPropertyContainer<O, BlockState>)state).<Direction.Axis, Direction.Axis>with(PillarBlock.AXIS, Direction.Axis.Z);
                    }
                    case Z: {
                        return ((AbstractPropertyContainer<O, BlockState>)state).<Direction.Axis, Direction.Axis>with(PillarBlock.AXIS, Direction.Axis.X);
                    }
                    default: {
                        return state;
                    }
                }
                break;
            }
            default: {
                return state;
            }
        }
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(PillarBlock.AXIS);
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        return ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Direction.Axis, Direction.Axis>with(PillarBlock.AXIS, ctx.getFacing().getAxis());
    }
    
    static {
        AXIS = Properties.AXIS_XYZ;
    }
}
