package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.IWorld;
import javax.annotation.Nullable;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.world.ViewableWorld;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.math.Direction;
import java.util.Map;
import net.minecraft.state.property.DirectionProperty;

public class WallTorchBlock extends TorchBlock
{
    public static final DirectionProperty FACING;
    private static final Map<Direction, VoxelShape> BOUNDING_SHAPES;
    
    protected WallTorchBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)WallTorchBlock.FACING, Direction.NORTH));
    }
    
    @Override
    public String getTranslationKey() {
        return this.getItem().getTranslationKey();
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return getBoundingShape(state);
    }
    
    public static VoxelShape getBoundingShape(final BlockState state) {
        return WallTorchBlock.BOUNDING_SHAPES.get(state.get((Property<Object>)WallTorchBlock.FACING));
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        final Direction direction4 = state.<Direction>get((Property<Direction>)WallTorchBlock.FACING);
        final BlockPos blockPos5 = pos.offset(direction4.getOpposite());
        final BlockState blockState6 = world.getBlockState(blockPos5);
        return Block.isSolidFullSquare(blockState6, world, blockPos5, direction4);
    }
    
    @Nullable
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        BlockState blockState2 = this.getDefaultState();
        final ViewableWorld viewableWorld3 = ctx.getWorld();
        final BlockPos blockPos4 = ctx.getBlockPos();
        final Direction[] placementFacings;
        final Direction[] arr5 = placementFacings = ctx.getPlacementFacings();
        for (final Direction direction9 : placementFacings) {
            if (direction9.getAxis().isHorizontal()) {
                final Direction direction10 = direction9.getOpposite();
                blockState2 = ((AbstractPropertyContainer<O, BlockState>)blockState2).<Comparable, Direction>with((Property<Comparable>)WallTorchBlock.FACING, direction10);
                if (blockState2.canPlaceAt(viewableWorld3, blockPos4)) {
                    return blockState2;
                }
            }
        }
        return null;
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (facing.getOpposite() == state.<Direction>get((Property<Direction>)WallTorchBlock.FACING) && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return state;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(final BlockState state, final World world, final BlockPos pos, final Random rnd) {
        final Direction direction5 = state.<Direction>get((Property<Direction>)WallTorchBlock.FACING);
        final double double6 = pos.getX() + 0.5;
        final double double7 = pos.getY() + 0.7;
        final double double8 = pos.getZ() + 0.5;
        final double double9 = 0.22;
        final double double10 = 0.27;
        final Direction direction6 = direction5.getOpposite();
        world.addParticle(ParticleTypes.Q, double6 + 0.27 * direction6.getOffsetX(), double7 + 0.22, double8 + 0.27 * direction6.getOffsetZ(), 0.0, 0.0, 0.0);
        world.addParticle(ParticleTypes.A, double6 + 0.27 * direction6.getOffsetX(), double7 + 0.22, double8 + 0.27 * direction6.getOffsetZ(), 0.0, 0.0, 0.0);
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Direction>with((Property<Comparable>)WallTorchBlock.FACING, rotation.rotate(state.<Direction>get((Property<Direction>)WallTorchBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.<Direction>get((Property<Direction>)WallTorchBlock.FACING)));
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(WallTorchBlock.FACING);
    }
    
    static {
        FACING = HorizontalFacingBlock.FACING;
        BOUNDING_SHAPES = Maps.<Direction, Object>newEnumMap(ImmutableMap.<Direction, VoxelShape>of(Direction.NORTH, Block.createCuboidShape(5.5, 3.0, 11.0, 10.5, 13.0, 16.0), Direction.SOUTH, Block.createCuboidShape(5.5, 3.0, 0.0, 10.5, 13.0, 5.0), Direction.WEST, Block.createCuboidShape(11.0, 3.0, 5.5, 16.0, 13.0, 10.5), Direction.EAST, Block.createCuboidShape(0.0, 3.0, 5.5, 5.0, 13.0, 10.5)));
    }
}
