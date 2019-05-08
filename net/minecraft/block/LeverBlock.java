package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Random;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.DustParticleParameters;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.IWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.BooleanProperty;

public class LeverBlock extends WallMountedBlock
{
    public static final BooleanProperty POWERED;
    protected static final VoxelShape NORTH_WALL_SHAPE;
    protected static final VoxelShape SOUTH_WALL_SHAPE;
    protected static final VoxelShape WEST_WALL_SHAPE;
    protected static final VoxelShape EAST_WALL_SHAPE;
    protected static final VoxelShape FLOOR_Z_AXIS_SHAPE;
    protected static final VoxelShape FLOOR_X_AXIS_SHAPE;
    protected static final VoxelShape CEILING_Z_AXIS_SHAPE;
    protected static final VoxelShape CEILING_X_AXIS_SHAPE;
    
    protected LeverBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)LeverBlock.FACING, Direction.NORTH)).with((Property<Comparable>)LeverBlock.POWERED, false)).<WallMountLocation, WallMountLocation>with(LeverBlock.FACE, WallMountLocation.b));
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        switch (state.<WallMountLocation>get(LeverBlock.FACE)) {
            case a: {
                switch (state.<Direction>get((Property<Direction>)LeverBlock.FACING).getAxis()) {
                    case X: {
                        return LeverBlock.FLOOR_X_AXIS_SHAPE;
                    }
                    default: {
                        return LeverBlock.FLOOR_Z_AXIS_SHAPE;
                    }
                }
                break;
            }
            case b: {
                switch (state.<Direction>get((Property<Direction>)LeverBlock.FACING)) {
                    case EAST: {
                        return LeverBlock.EAST_WALL_SHAPE;
                    }
                    case WEST: {
                        return LeverBlock.WEST_WALL_SHAPE;
                    }
                    case SOUTH: {
                        return LeverBlock.SOUTH_WALL_SHAPE;
                    }
                    default: {
                        return LeverBlock.NORTH_WALL_SHAPE;
                    }
                }
                break;
            }
            default: {
                switch (state.<Direction>get((Property<Direction>)LeverBlock.FACING).getAxis()) {
                    case X: {
                        return LeverBlock.CEILING_X_AXIS_SHAPE;
                    }
                    default: {
                        return LeverBlock.CEILING_Z_AXIS_SHAPE;
                    }
                }
                break;
            }
        }
    }
    
    @Override
    public boolean activate(BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        state = ((AbstractPropertyContainer<O, BlockState>)state).<Comparable>cycle((Property<Comparable>)LeverBlock.POWERED);
        final boolean boolean7 = state.<Boolean>get((Property<Boolean>)LeverBlock.POWERED);
        if (world.isClient) {
            if (boolean7) {
                spawnParticles(state, world, pos, 1.0f);
            }
            return true;
        }
        world.setBlockState(pos, state, 3);
        final float float8 = boolean7 ? 0.6f : 0.5f;
        world.playSound(null, pos, SoundEvents.fV, SoundCategory.e, 0.3f, float8);
        this.updateNeighbors(state, world, pos);
        return true;
    }
    
    private static void spawnParticles(final BlockState state, final IWorld world, final BlockPos pos, final float alpha) {
        final Direction direction5 = state.<Direction>get((Property<Direction>)LeverBlock.FACING).getOpposite();
        final Direction direction6 = WallMountedBlock.getDirection(state).getOpposite();
        final double double7 = pos.getX() + 0.5 + 0.1 * direction5.getOffsetX() + 0.2 * direction6.getOffsetX();
        final double double8 = pos.getY() + 0.5 + 0.1 * direction5.getOffsetY() + 0.2 * direction6.getOffsetY();
        final double double9 = pos.getZ() + 0.5 + 0.1 * direction5.getOffsetZ() + 0.2 * direction6.getOffsetZ();
        world.addParticle(new DustParticleParameters(1.0f, 0.0f, 0.0f, alpha), double7, double8, double9, 0.0, 0.0, 0.0);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(final BlockState state, final World world, final BlockPos pos, final Random rnd) {
        if (state.<Boolean>get((Property<Boolean>)LeverBlock.POWERED) && rnd.nextFloat() < 0.25f) {
            spawnParticles(state, world, pos, 0.5f);
        }
    }
    
    @Override
    public void onBlockRemoved(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean boolean5) {
        if (boolean5 || state.getBlock() == newState.getBlock()) {
            return;
        }
        if (state.<Boolean>get((Property<Boolean>)LeverBlock.POWERED)) {
            this.updateNeighbors(state, world, pos);
        }
        super.onBlockRemoved(state, world, pos, newState, boolean5);
    }
    
    @Override
    public int getWeakRedstonePower(final BlockState state, final BlockView view, final BlockPos pos, final Direction facing) {
        return state.<Boolean>get((Property<Boolean>)LeverBlock.POWERED) ? 15 : 0;
    }
    
    @Override
    public int getStrongRedstonePower(final BlockState state, final BlockView view, final BlockPos pos, final Direction facing) {
        if (state.<Boolean>get((Property<Boolean>)LeverBlock.POWERED) && WallMountedBlock.getDirection(state) == facing) {
            return 15;
        }
        return 0;
    }
    
    @Override
    public boolean emitsRedstonePower(final BlockState state) {
        return true;
    }
    
    private void updateNeighbors(final BlockState state, final World world, final BlockPos pos) {
        world.updateNeighborsAlways(pos, this);
        world.updateNeighborsAlways(pos.offset(WallMountedBlock.getDirection(state).getOpposite()), this);
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(LeverBlock.FACE, LeverBlock.FACING, LeverBlock.POWERED);
    }
    
    static {
        POWERED = Properties.POWERED;
        NORTH_WALL_SHAPE = Block.createCuboidShape(5.0, 4.0, 10.0, 11.0, 12.0, 16.0);
        SOUTH_WALL_SHAPE = Block.createCuboidShape(5.0, 4.0, 0.0, 11.0, 12.0, 6.0);
        WEST_WALL_SHAPE = Block.createCuboidShape(10.0, 4.0, 5.0, 16.0, 12.0, 11.0);
        EAST_WALL_SHAPE = Block.createCuboidShape(0.0, 4.0, 5.0, 6.0, 12.0, 11.0);
        FLOOR_Z_AXIS_SHAPE = Block.createCuboidShape(5.0, 0.0, 4.0, 11.0, 6.0, 12.0);
        FLOOR_X_AXIS_SHAPE = Block.createCuboidShape(4.0, 0.0, 5.0, 12.0, 6.0, 11.0);
        CEILING_Z_AXIS_SHAPE = Block.createCuboidShape(5.0, 10.0, 4.0, 11.0, 16.0, 12.0);
        CEILING_X_AXIS_SHAPE = Block.createCuboidShape(4.0, 10.0, 5.0, 12.0, 16.0, 11.0);
    }
}
