package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.world.IWorld;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.math.Direction;
import java.util.Map;
import net.minecraft.state.property.DirectionProperty;

public class AttachedStemBlock extends PlantBlock
{
    public static final DirectionProperty FACING;
    private final GourdBlock gourdBlock;
    private static final Map<Direction, VoxelShape> FACING_TO_SHAPE;
    
    protected AttachedStemBlock(final GourdBlock gourdBlock, final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)AttachedStemBlock.FACING, Direction.NORTH));
        this.gourdBlock = gourdBlock;
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return AttachedStemBlock.FACING_TO_SHAPE.get(state.get((Property<Object>)AttachedStemBlock.FACING));
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (neighborState.getBlock() != this.gourdBlock && facing == state.<Direction>get((Property<Direction>)AttachedStemBlock.FACING)) {
            return ((AbstractPropertyContainer<O, BlockState>)this.gourdBlock.getStem().getDefaultState()).<Comparable, Integer>with((Property<Comparable>)StemBlock.AGE, 7);
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    protected boolean canPlantOnTop(final BlockState floor, final BlockView view, final BlockPos pos) {
        return floor.getBlock() == Blocks.bV;
    }
    
    @Environment(EnvType.CLIENT)
    protected Item getSeeds() {
        if (this.gourdBlock == Blocks.cI) {
            return Items.lZ;
        }
        if (this.gourdBlock == Blocks.dC) {
            return Items.ma;
        }
        return Items.AIR;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public ItemStack getPickStack(final BlockView world, final BlockPos pos, final BlockState state) {
        return new ItemStack(this.getSeeds());
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Direction>with((Property<Comparable>)AttachedStemBlock.FACING, rotation.rotate(state.<Direction>get((Property<Direction>)AttachedStemBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.<Direction>get((Property<Direction>)AttachedStemBlock.FACING)));
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(AttachedStemBlock.FACING);
    }
    
    static {
        FACING = HorizontalFacingBlock.FACING;
        FACING_TO_SHAPE = Maps.<Direction, Object>newEnumMap(ImmutableMap.<Direction, VoxelShape>of(Direction.SOUTH, Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 10.0, 16.0), Direction.WEST, Block.createCuboidShape(0.0, 0.0, 6.0, 10.0, 10.0, 10.0), Direction.NORTH, Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 10.0, 10.0), Direction.EAST, Block.createCuboidShape(6.0, 0.0, 6.0, 16.0, 10.0, 10.0)));
    }
}
