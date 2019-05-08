package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.state.property.Property;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.block.enums.RailShape;
import net.minecraft.state.property.EnumProperty;

public class PoweredRailBlock extends AbstractRailBlock
{
    public static final EnumProperty<RailShape> SHAPE;
    public static final BooleanProperty POWERED;
    
    protected PoweredRailBlock(final Settings settings) {
        super(true, settings);
        this.setDefaultState((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with(PoweredRailBlock.SHAPE, RailShape.a)).<Comparable, Boolean>with((Property<Comparable>)PoweredRailBlock.POWERED, false));
    }
    
    protected boolean isPoweredByOtherRails(final World world, final BlockPos pos, final BlockState state, final boolean boolean4, final int distance) {
        if (distance >= 8) {
            return false;
        }
        int integer6 = pos.getX();
        int integer7 = pos.getY();
        int integer8 = pos.getZ();
        boolean boolean5 = true;
        RailShape railShape10 = state.<RailShape>get(PoweredRailBlock.SHAPE);
        switch (railShape10) {
            case a: {
                if (boolean4) {
                    ++integer8;
                    break;
                }
                --integer8;
                break;
            }
            case b: {
                if (boolean4) {
                    --integer6;
                    break;
                }
                ++integer6;
                break;
            }
            case c: {
                if (boolean4) {
                    --integer6;
                }
                else {
                    ++integer6;
                    ++integer7;
                    boolean5 = false;
                }
                railShape10 = RailShape.b;
                break;
            }
            case d: {
                if (boolean4) {
                    --integer6;
                    ++integer7;
                    boolean5 = false;
                }
                else {
                    ++integer6;
                }
                railShape10 = RailShape.b;
                break;
            }
            case e: {
                if (boolean4) {
                    ++integer8;
                }
                else {
                    --integer8;
                    ++integer7;
                    boolean5 = false;
                }
                railShape10 = RailShape.a;
                break;
            }
            case f: {
                if (boolean4) {
                    ++integer8;
                    ++integer7;
                    boolean5 = false;
                }
                else {
                    --integer8;
                }
                railShape10 = RailShape.a;
                break;
            }
        }
        return this.isPoweredByOtherRails(world, new BlockPos(integer6, integer7, integer8), boolean4, distance, railShape10) || (boolean5 && this.isPoweredByOtherRails(world, new BlockPos(integer6, integer7 - 1, integer8), boolean4, distance, railShape10));
    }
    
    protected boolean isPoweredByOtherRails(final World world, final BlockPos pos, final boolean boolean3, final int distance, final RailShape shape) {
        final BlockState blockState6 = world.getBlockState(pos);
        if (blockState6.getBlock() != this) {
            return false;
        }
        final RailShape railShape7 = blockState6.<RailShape>get(PoweredRailBlock.SHAPE);
        return (shape != RailShape.b || (railShape7 != RailShape.a && railShape7 != RailShape.e && railShape7 != RailShape.f)) && (shape != RailShape.a || (railShape7 != RailShape.b && railShape7 != RailShape.c && railShape7 != RailShape.d)) && blockState6.<Boolean>get((Property<Boolean>)PoweredRailBlock.POWERED) && (world.isReceivingRedstonePower(pos) || this.isPoweredByOtherRails(world, pos, blockState6, boolean3, distance + 1));
    }
    
    @Override
    protected void updateBlockState(final BlockState state, final World world, final BlockPos pos, final Block neighbor) {
        final boolean boolean5 = state.<Boolean>get((Property<Boolean>)PoweredRailBlock.POWERED);
        final boolean boolean6 = world.isReceivingRedstonePower(pos) || this.isPoweredByOtherRails(world, pos, state, true, 0) || this.isPoweredByOtherRails(world, pos, state, false, 0);
        if (boolean6 != boolean5) {
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)PoweredRailBlock.POWERED, boolean6), 3);
            world.updateNeighborsAlways(pos.down(), this);
            if (state.<RailShape>get(PoweredRailBlock.SHAPE).isAscending()) {
                world.updateNeighborsAlways(pos.up(), this);
            }
        }
    }
    
    @Override
    public Property<RailShape> getShapeProperty() {
        return PoweredRailBlock.SHAPE;
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        Label_0424: {
            switch (rotation) {
                case ROT_180: {
                    switch (state.<RailShape>get(PoweredRailBlock.SHAPE)) {
                        case c: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.d);
                        }
                        case d: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.c);
                        }
                        case e: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.f);
                        }
                        case f: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.e);
                        }
                        case g: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.i);
                        }
                        case h: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.j);
                        }
                        case i: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.g);
                        }
                        case j: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.h);
                        }
                        default: {
                            break Label_0424;
                        }
                    }
                    break;
                }
                case ROT_270: {
                    switch (state.<RailShape>get(PoweredRailBlock.SHAPE)) {
                        case a: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.b);
                        }
                        case b: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.a);
                        }
                        case c: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.e);
                        }
                        case d: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.f);
                        }
                        case e: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.d);
                        }
                        case f: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.c);
                        }
                        case g: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.j);
                        }
                        case h: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.g);
                        }
                        case i: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.h);
                        }
                        case j: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.i);
                        }
                        default: {
                            break Label_0424;
                        }
                    }
                    break;
                }
                case ROT_90: {
                    switch (state.<RailShape>get(PoweredRailBlock.SHAPE)) {
                        case a: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.b);
                        }
                        case b: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.a);
                        }
                        case c: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.f);
                        }
                        case d: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.e);
                        }
                        case e: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.c);
                        }
                        case f: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.d);
                        }
                        case g: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.h);
                        }
                        case h: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.i);
                        }
                        case i: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.j);
                        }
                        case j: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.g);
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
        final RailShape railShape3 = state.<RailShape>get(PoweredRailBlock.SHAPE);
        Label_0319: {
            switch (mirror) {
                case LEFT_RIGHT: {
                    switch (railShape3) {
                        case e: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.f);
                        }
                        case f: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.e);
                        }
                        case g: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.j);
                        }
                        case h: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.i);
                        }
                        case i: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.h);
                        }
                        case j: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.g);
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
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.d);
                        }
                        case d: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.c);
                        }
                        case g: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.h);
                        }
                        case h: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.g);
                        }
                        case i: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.j);
                        }
                        case j: {
                            return ((AbstractPropertyContainer<O, BlockState>)state).<RailShape, RailShape>with(PoweredRailBlock.SHAPE, RailShape.i);
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
        builder.add(PoweredRailBlock.SHAPE, PoweredRailBlock.POWERED);
    }
    
    static {
        SHAPE = Properties.STRAIGHT_RAIL_SHAPE;
        POWERED = Properties.POWERED;
    }
}
