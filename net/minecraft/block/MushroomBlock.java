package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import java.util.Map;
import net.minecraft.state.property.BooleanProperty;

public class MushroomBlock extends Block
{
    public static final BooleanProperty NORTH;
    public static final BooleanProperty EAST;
    public static final BooleanProperty SOUTH;
    public static final BooleanProperty WEST;
    public static final BooleanProperty UP;
    public static final BooleanProperty DOWN;
    private static final Map<Direction, BooleanProperty> FACING_PROPERTIES;
    
    public MushroomBlock(final Settings settings) {
        super(settings);
        this.setDefaultState((((((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)MushroomBlock.NORTH, true)).with((Property<Comparable>)MushroomBlock.EAST, true)).with((Property<Comparable>)MushroomBlock.SOUTH, true)).with((Property<Comparable>)MushroomBlock.WEST, true)).with((Property<Comparable>)MushroomBlock.UP, true)).<Comparable, Boolean>with((Property<Comparable>)MushroomBlock.DOWN, true));
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final BlockView blockView2 = ctx.getWorld();
        final BlockPos blockPos3 = ctx.getBlockPos();
        return (((((((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).with((Property<Comparable>)MushroomBlock.DOWN, this != blockView2.getBlockState(blockPos3.down()).getBlock())).with((Property<Comparable>)MushroomBlock.UP, this != blockView2.getBlockState(blockPos3.up()).getBlock())).with((Property<Comparable>)MushroomBlock.NORTH, this != blockView2.getBlockState(blockPos3.north()).getBlock())).with((Property<Comparable>)MushroomBlock.EAST, this != blockView2.getBlockState(blockPos3.east()).getBlock())).with((Property<Comparable>)MushroomBlock.SOUTH, this != blockView2.getBlockState(blockPos3.south()).getBlock())).<Comparable, Boolean>with((Property<Comparable>)MushroomBlock.WEST, this != blockView2.getBlockState(blockPos3.west()).getBlock());
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (neighborState.getBlock() == this) {
            return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)MushroomBlock.FACING_PROPERTIES.get(facing), false);
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return (((((((AbstractPropertyContainer<O, BlockState>)state).with((Property<Comparable>)MushroomBlock.FACING_PROPERTIES.get(rotation.rotate(Direction.NORTH)), (Comparable)state.<V>get((Property<V>)MushroomBlock.NORTH))).with((Property<Comparable>)MushroomBlock.FACING_PROPERTIES.get(rotation.rotate(Direction.SOUTH)), (Comparable)state.<V>get((Property<V>)MushroomBlock.SOUTH))).with((Property<Comparable>)MushroomBlock.FACING_PROPERTIES.get(rotation.rotate(Direction.EAST)), (Comparable)state.<V>get((Property<V>)MushroomBlock.EAST))).with((Property<Comparable>)MushroomBlock.FACING_PROPERTIES.get(rotation.rotate(Direction.WEST)), (Comparable)state.<V>get((Property<V>)MushroomBlock.WEST))).with((Property<Comparable>)MushroomBlock.FACING_PROPERTIES.get(rotation.rotate(Direction.UP)), (Comparable)state.<V>get((Property<V>)MushroomBlock.UP))).<Comparable, Comparable>with((Property<Comparable>)MushroomBlock.FACING_PROPERTIES.get(rotation.rotate(Direction.DOWN)), (Comparable)state.<V>get((Property<V>)MushroomBlock.DOWN));
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return (((((((AbstractPropertyContainer<O, BlockState>)state).with((Property<Comparable>)MushroomBlock.FACING_PROPERTIES.get(mirror.apply(Direction.NORTH)), (Comparable)state.<V>get((Property<V>)MushroomBlock.NORTH))).with((Property<Comparable>)MushroomBlock.FACING_PROPERTIES.get(mirror.apply(Direction.SOUTH)), (Comparable)state.<V>get((Property<V>)MushroomBlock.SOUTH))).with((Property<Comparable>)MushroomBlock.FACING_PROPERTIES.get(mirror.apply(Direction.EAST)), (Comparable)state.<V>get((Property<V>)MushroomBlock.EAST))).with((Property<Comparable>)MushroomBlock.FACING_PROPERTIES.get(mirror.apply(Direction.WEST)), (Comparable)state.<V>get((Property<V>)MushroomBlock.WEST))).with((Property<Comparable>)MushroomBlock.FACING_PROPERTIES.get(mirror.apply(Direction.UP)), (Comparable)state.<V>get((Property<V>)MushroomBlock.UP))).<Comparable, Comparable>with((Property<Comparable>)MushroomBlock.FACING_PROPERTIES.get(mirror.apply(Direction.DOWN)), (Comparable)state.<V>get((Property<V>)MushroomBlock.DOWN));
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(MushroomBlock.UP, MushroomBlock.DOWN, MushroomBlock.NORTH, MushroomBlock.EAST, MushroomBlock.SOUTH, MushroomBlock.WEST);
    }
    
    static {
        NORTH = ConnectedPlantBlock.NORTH;
        EAST = ConnectedPlantBlock.EAST;
        SOUTH = ConnectedPlantBlock.SOUTH;
        WEST = ConnectedPlantBlock.WEST;
        UP = ConnectedPlantBlock.UP;
        DOWN = ConnectedPlantBlock.DOWN;
        FACING_PROPERTIES = ConnectedPlantBlock.FACING_PROPERTIES;
    }
}
