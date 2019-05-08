package net.minecraft.block;

import net.minecraft.util.SystemUtil;
import com.google.common.collect.Maps;
import net.minecraft.state.property.Properties;
import java.util.EnumMap;
import net.minecraft.state.property.Property;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.shape.VoxelShape;
import java.util.Map;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.Direction;

public class ConnectedPlantBlock extends Block
{
    private static final Direction[] FACINGS;
    public static final BooleanProperty NORTH;
    public static final BooleanProperty EAST;
    public static final BooleanProperty SOUTH;
    public static final BooleanProperty WEST;
    public static final BooleanProperty UP;
    public static final BooleanProperty DOWN;
    public static final Map<Direction, BooleanProperty> FACING_PROPERTIES;
    protected final VoxelShape[] CONNECTIONS_TO_SHAPE;
    
    protected ConnectedPlantBlock(final float float1, final Settings settings) {
        super(settings);
        this.CONNECTIONS_TO_SHAPE = this.generateFacingsToShapeMap(float1);
    }
    
    private VoxelShape[] generateFacingsToShapeMap(final float float1) {
        final float float2 = 0.5f - float1;
        final float float3 = 0.5f + float1;
        final VoxelShape voxelShape4 = Block.createCuboidShape(float2 * 16.0f, float2 * 16.0f, float2 * 16.0f, float3 * 16.0f, float3 * 16.0f, float3 * 16.0f);
        final VoxelShape[] arr5 = new VoxelShape[ConnectedPlantBlock.FACINGS.length];
        for (int integer6 = 0; integer6 < ConnectedPlantBlock.FACINGS.length; ++integer6) {
            final Direction direction7 = ConnectedPlantBlock.FACINGS[integer6];
            arr5[integer6] = VoxelShapes.cuboid(0.5 + Math.min(-float1, direction7.getOffsetX() * 0.5), 0.5 + Math.min(-float1, direction7.getOffsetY() * 0.5), 0.5 + Math.min(-float1, direction7.getOffsetZ() * 0.5), 0.5 + Math.max(float1, direction7.getOffsetX() * 0.5), 0.5 + Math.max(float1, direction7.getOffsetY() * 0.5), 0.5 + Math.max(float1, direction7.getOffsetZ() * 0.5));
        }
        final VoxelShape[] arr6 = new VoxelShape[64];
        for (int integer7 = 0; integer7 < 64; ++integer7) {
            VoxelShape voxelShape5 = voxelShape4;
            for (int integer8 = 0; integer8 < ConnectedPlantBlock.FACINGS.length; ++integer8) {
                if ((integer7 & 1 << integer8) != 0x0) {
                    voxelShape5 = VoxelShapes.union(voxelShape5, arr5[integer8]);
                }
            }
            arr6[integer7] = voxelShape5;
        }
        return arr6;
    }
    
    @Override
    public boolean isTranslucent(final BlockState state, final BlockView view, final BlockPos pos) {
        return false;
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return this.CONNECTIONS_TO_SHAPE[this.getConnectionMask(state)];
    }
    
    protected int getConnectionMask(final BlockState state) {
        int integer2 = 0;
        for (int integer3 = 0; integer3 < ConnectedPlantBlock.FACINGS.length; ++integer3) {
            if (state.<Boolean>get((Property<Boolean>)ConnectedPlantBlock.FACING_PROPERTIES.get(ConnectedPlantBlock.FACINGS[integer3]))) {
                integer2 |= 1 << integer3;
            }
        }
        return integer2;
    }
    
    static {
        FACINGS = Direction.values();
        NORTH = Properties.NORTH_BOOL;
        EAST = Properties.EAST_BOOL;
        SOUTH = Properties.SOUTH_BOOL;
        WEST = Properties.WEST_BOOL;
        UP = Properties.UP_BOOL;
        DOWN = Properties.DOWN_BOOL;
        FACING_PROPERTIES = SystemUtil.<Map<Direction, BooleanProperty>>consume(Maps.newEnumMap(Direction.class), enumMap -> {
            enumMap.put(Direction.NORTH, ConnectedPlantBlock.NORTH);
            enumMap.put(Direction.EAST, ConnectedPlantBlock.EAST);
            enumMap.put(Direction.SOUTH, ConnectedPlantBlock.SOUTH);
            enumMap.put(Direction.WEST, ConnectedPlantBlock.WEST);
            enumMap.put(Direction.UP, ConnectedPlantBlock.UP);
            enumMap.put(Direction.DOWN, ConnectedPlantBlock.DOWN);
        });
    }
}
