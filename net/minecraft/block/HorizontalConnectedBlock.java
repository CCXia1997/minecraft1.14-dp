package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import java.util.stream.Collector;
import net.minecraft.util.SystemUtil;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.FluidState;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.math.Direction;
import java.util.Map;
import net.minecraft.state.property.BooleanProperty;

public class HorizontalConnectedBlock extends Block implements Waterloggable
{
    public static final BooleanProperty NORTH;
    public static final BooleanProperty EAST;
    public static final BooleanProperty SOUTH;
    public static final BooleanProperty WEST;
    public static final BooleanProperty WATERLOGGED;
    protected static final Map<Direction, BooleanProperty> FACING_PROPERTIES;
    protected final VoxelShape[] collisionShapes;
    protected final VoxelShape[] boundingShapes;
    
    protected HorizontalConnectedBlock(final float float1, final float float2, final float float3, final float float4, final float float5, final Settings settings) {
        super(settings);
        this.collisionShapes = this.createShapes(float1, float2, float5, 0.0f, float5);
        this.boundingShapes = this.createShapes(float1, float2, float3, 0.0f, float4);
    }
    
    protected VoxelShape[] createShapes(final float float1, final float float2, final float float3, final float float4, final float float5) {
        final float float6 = 8.0f - float1;
        final float float7 = 8.0f + float1;
        final float float8 = 8.0f - float2;
        final float float9 = 8.0f + float2;
        final VoxelShape voxelShape10 = Block.createCuboidShape(float6, 0.0, float6, float7, float3, float7);
        final VoxelShape voxelShape11 = Block.createCuboidShape(float8, float4, 0.0, float9, float5, float9);
        final VoxelShape voxelShape12 = Block.createCuboidShape(float8, float4, float8, float9, float5, 16.0);
        final VoxelShape voxelShape13 = Block.createCuboidShape(0.0, float4, float8, float9, float5, float9);
        final VoxelShape voxelShape14 = Block.createCuboidShape(float8, float4, float8, 16.0, float5, float9);
        final VoxelShape voxelShape15 = VoxelShapes.union(voxelShape11, voxelShape14);
        final VoxelShape voxelShape16 = VoxelShapes.union(voxelShape12, voxelShape13);
        final VoxelShape[] arr17 = { VoxelShapes.empty(), voxelShape12, voxelShape13, voxelShape16, voxelShape11, VoxelShapes.union(voxelShape12, voxelShape11), VoxelShapes.union(voxelShape13, voxelShape11), VoxelShapes.union(voxelShape16, voxelShape11), voxelShape14, VoxelShapes.union(voxelShape12, voxelShape14), VoxelShapes.union(voxelShape13, voxelShape14), VoxelShapes.union(voxelShape16, voxelShape14), voxelShape15, VoxelShapes.union(voxelShape12, voxelShape15), VoxelShapes.union(voxelShape13, voxelShape15), VoxelShapes.union(voxelShape16, voxelShape15) };
        for (int integer18 = 0; integer18 < 16; ++integer18) {
            arr17[integer18] = VoxelShapes.union(voxelShape10, arr17[integer18]);
        }
        return arr17;
    }
    
    @Override
    public boolean isTranslucent(final BlockState state, final BlockView view, final BlockPos pos) {
        return !state.<Boolean>get((Property<Boolean>)HorizontalConnectedBlock.WATERLOGGED);
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return this.boundingShapes[this.getShapeIndex(state)];
    }
    
    @Override
    public VoxelShape getCollisionShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition ePos) {
        return this.collisionShapes[this.getShapeIndex(state)];
    }
    
    private static int getDirectionMask(final Direction direction) {
        return 1 << direction.getHorizontal();
    }
    
    protected int getShapeIndex(final BlockState blockState) {
        int integer2 = 0;
        if (blockState.<Boolean>get((Property<Boolean>)HorizontalConnectedBlock.NORTH)) {
            integer2 |= getDirectionMask(Direction.NORTH);
        }
        if (blockState.<Boolean>get((Property<Boolean>)HorizontalConnectedBlock.EAST)) {
            integer2 |= getDirectionMask(Direction.EAST);
        }
        if (blockState.<Boolean>get((Property<Boolean>)HorizontalConnectedBlock.SOUTH)) {
            integer2 |= getDirectionMask(Direction.SOUTH);
        }
        if (blockState.<Boolean>get((Property<Boolean>)HorizontalConnectedBlock.WEST)) {
            integer2 |= getDirectionMask(Direction.WEST);
        }
        return integer2;
    }
    
    @Override
    public FluidState getFluidState(final BlockState state) {
        if (state.<Boolean>get((Property<Boolean>)HorizontalConnectedBlock.WATERLOGGED)) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        return false;
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        switch (rotation) {
            case ROT_180: {
                return (((((AbstractPropertyContainer<O, BlockState>)state).with((Property<Comparable>)HorizontalConnectedBlock.NORTH, (Comparable)state.<V>get((Property<V>)HorizontalConnectedBlock.SOUTH))).with((Property<Comparable>)HorizontalConnectedBlock.EAST, (Comparable)state.<V>get((Property<V>)HorizontalConnectedBlock.WEST))).with((Property<Comparable>)HorizontalConnectedBlock.SOUTH, (Comparable)state.<V>get((Property<V>)HorizontalConnectedBlock.NORTH))).<Comparable, Comparable>with((Property<Comparable>)HorizontalConnectedBlock.WEST, (Comparable)state.<V>get((Property<V>)HorizontalConnectedBlock.EAST));
            }
            case ROT_270: {
                return (((((AbstractPropertyContainer<O, BlockState>)state).with((Property<Comparable>)HorizontalConnectedBlock.NORTH, (Comparable)state.<V>get((Property<V>)HorizontalConnectedBlock.EAST))).with((Property<Comparable>)HorizontalConnectedBlock.EAST, (Comparable)state.<V>get((Property<V>)HorizontalConnectedBlock.SOUTH))).with((Property<Comparable>)HorizontalConnectedBlock.SOUTH, (Comparable)state.<V>get((Property<V>)HorizontalConnectedBlock.WEST))).<Comparable, Comparable>with((Property<Comparable>)HorizontalConnectedBlock.WEST, (Comparable)state.<V>get((Property<V>)HorizontalConnectedBlock.NORTH));
            }
            case ROT_90: {
                return (((((AbstractPropertyContainer<O, BlockState>)state).with((Property<Comparable>)HorizontalConnectedBlock.NORTH, (Comparable)state.<V>get((Property<V>)HorizontalConnectedBlock.WEST))).with((Property<Comparable>)HorizontalConnectedBlock.EAST, (Comparable)state.<V>get((Property<V>)HorizontalConnectedBlock.NORTH))).with((Property<Comparable>)HorizontalConnectedBlock.SOUTH, (Comparable)state.<V>get((Property<V>)HorizontalConnectedBlock.EAST))).<Comparable, Comparable>with((Property<Comparable>)HorizontalConnectedBlock.WEST, (Comparable)state.<V>get((Property<V>)HorizontalConnectedBlock.SOUTH));
            }
            default: {
                return state;
            }
        }
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        switch (mirror) {
            case LEFT_RIGHT: {
                return (((AbstractPropertyContainer<O, BlockState>)state).with((Property<Comparable>)HorizontalConnectedBlock.NORTH, (Comparable)state.<V>get((Property<V>)HorizontalConnectedBlock.SOUTH))).<Comparable, Comparable>with((Property<Comparable>)HorizontalConnectedBlock.SOUTH, (Comparable)state.<V>get((Property<V>)HorizontalConnectedBlock.NORTH));
            }
            case FRONT_BACK: {
                return (((AbstractPropertyContainer<O, BlockState>)state).with((Property<Comparable>)HorizontalConnectedBlock.EAST, (Comparable)state.<V>get((Property<V>)HorizontalConnectedBlock.WEST))).<Comparable, Comparable>with((Property<Comparable>)HorizontalConnectedBlock.WEST, (Comparable)state.<V>get((Property<V>)HorizontalConnectedBlock.EAST));
            }
            default: {
                return super.mirror(state, mirror);
            }
        }
    }
    
    static {
        NORTH = ConnectedPlantBlock.NORTH;
        EAST = ConnectedPlantBlock.EAST;
        SOUTH = ConnectedPlantBlock.SOUTH;
        WEST = ConnectedPlantBlock.WEST;
        WATERLOGGED = Properties.WATERLOGGED;
        FACING_PROPERTIES = ConnectedPlantBlock.FACING_PROPERTIES.entrySet().stream().filter(entry -> entry.getKey().getAxis().isHorizontal()).collect(SystemUtil.<Direction, BooleanProperty>toMap());
    }
}
