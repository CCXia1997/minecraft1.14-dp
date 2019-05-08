package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.state.property.Property;
import net.minecraft.block.enums.RailShape;
import net.minecraft.state.property.EnumProperty;

public class RailBlock extends AbstractRailBlock
{
    public static final EnumProperty<RailShape> SHAPE;
    
    protected RailBlock(final Settings settings) {
        super(false, settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.a));
    }
    
    @Override
    protected void updateBlockState(final BlockState state, final World world, final BlockPos pos, final Block neighbor) {
        if (neighbor.getDefaultState().emitsRedstonePower() && new RailPlacementHelper(world, pos, state).b() == 3) {
            this.updateBlockState(world, pos, state, false);
        }
    }
    
    @Override
    public Property<RailShape> getShapeProperty() {
        return RailBlock.SHAPE;
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        Label_0424: {
            switch (rotation) {
                case ROT_180: {
                    switch (state.<RailShape>get(RailBlock.SHAPE)) {
                        case c: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.d);
                        }
                        case d: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.c);
                        }
                        case e: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.f);
                        }
                        case f: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.e);
                        }
                        case g: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.i);
                        }
                        case h: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.j);
                        }
                        case i: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.g);
                        }
                        case j: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.h);
                        }
                        default: {
                            break Label_0424;
                        }
                    }
                    break;
                }
                case ROT_270: {
                    switch (state.<RailShape>get(RailBlock.SHAPE)) {
                        case a: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.b);
                        }
                        case b: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.a);
                        }
                        case c: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.e);
                        }
                        case d: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.f);
                        }
                        case e: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.d);
                        }
                        case f: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.c);
                        }
                        case g: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.j);
                        }
                        case h: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.g);
                        }
                        case i: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.h);
                        }
                        case j: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.i);
                        }
                        default: {
                            break Label_0424;
                        }
                    }
                    break;
                }
                case ROT_90: {
                    switch (state.<RailShape>get(RailBlock.SHAPE)) {
                        case a: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.b);
                        }
                        case b: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.a);
                        }
                        case c: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.f);
                        }
                        case d: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.e);
                        }
                        case e: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.c);
                        }
                        case f: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.d);
                        }
                        case g: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.h);
                        }
                        case h: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.i);
                        }
                        case i: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.j);
                        }
                        case j: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.g);
                        }
                        default: {
                            break Label_0424;
                        }
                    }
                    break;
                }
            }
        }
        return state;
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        final RailShape railShape3 = state.<RailShape>get(RailBlock.SHAPE);
        Label_0319: {
            switch (mirror) {
                case LEFT_RIGHT: {
                    switch (railShape3) {
                        case e: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.f);
                        }
                        case f: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.e);
                        }
                        case g: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.j);
                        }
                        case h: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.i);
                        }
                        case i: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.h);
                        }
                        case j: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.g);
                        }
                        default: {
                            break Label_0319;
                        }
                    }
                    break;
                }
                case FRONT_BACK: {
                    switch (railShape3) {
                        case c: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.d);
                        }
                        case d: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.c);
                        }
                        case g: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.h);
                        }
                        case h: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.g);
                        }
                        case i: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.j);
                        }
                        case j: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(RailBlock.SHAPE, RailShape.i);
                        }
                        default: {
                            break Label_0319;
                        }
                    }
                    break;
                }
            }
        }
        return super.mirror(state, mirror);
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(RailBlock.SHAPE);
    }
    
    static {
        SHAPE = Properties.RAIL_SHAPE;
    }
}
