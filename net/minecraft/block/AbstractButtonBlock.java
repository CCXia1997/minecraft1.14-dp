package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import java.util.List;
import net.minecraft.util.math.Vec3i;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.Entity;
import java.util.Random;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundCategory;
import javax.annotation.Nullable;
import net.minecraft.world.IWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.ViewableWorld;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.BooleanProperty;

public abstract class AbstractButtonBlock extends WallMountedBlock
{
    public static final BooleanProperty POWERED;
    protected static final VoxelShape SHAPE_CEILING_X;
    protected static final VoxelShape SHAPE_CEILING_Z;
    protected static final VoxelShape SHAPE_FLOOR_X;
    protected static final VoxelShape SHAPE_FLOOR_Z;
    protected static final VoxelShape SHAPE_NORTH;
    protected static final VoxelShape SHAPE_SOUTH;
    protected static final VoxelShape SHAPE_WEST;
    protected static final VoxelShape SHAPE_EAST;
    protected static final VoxelShape SHAPE_CEILING_X_POWERED;
    protected static final VoxelShape SHAPE_CEILING_Z_POWERED;
    protected static final VoxelShape SHAPE_FLOOR_X_POWERED;
    protected static final VoxelShape SHAPE_FLOOR_Z_POWERED;
    protected static final VoxelShape SHAPE_NORTH_POWERED;
    protected static final VoxelShape SHAPE_SOUTH_POWERED;
    protected static final VoxelShape SHAPE_WEST_POWERED;
    protected static final VoxelShape SHAPE_EAST_POWERED;
    private final boolean wooden;
    
    protected AbstractButtonBlock(final boolean wooden, final Settings settings) {
        super(settings);
        this.setDefaultState(((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)AbstractButtonBlock.FACING, Direction.NORTH)).with((Property<Comparable>)AbstractButtonBlock.POWERED, false)).<WallMountLocation, WallMountLocation>with(AbstractButtonBlock.FACE, WallMountLocation.b));
        this.wooden = wooden;
    }
    
    @Override
    public int getTickRate(final ViewableWorld world) {
        return this.wooden ? 30 : 20;
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        final Direction direction5 = state.<Direction>get((Property<Direction>)AbstractButtonBlock.FACING);
        final boolean boolean6 = state.<Boolean>get((Property<Boolean>)AbstractButtonBlock.POWERED);
        switch (state.<WallMountLocation>get(AbstractButtonBlock.FACE)) {
            case a: {
                if (direction5.getAxis() == Direction.Axis.X) {
                    return boolean6 ? AbstractButtonBlock.SHAPE_FLOOR_X_POWERED : AbstractButtonBlock.SHAPE_FLOOR_X;
                }
                return boolean6 ? AbstractButtonBlock.SHAPE_FLOOR_Z_POWERED : AbstractButtonBlock.SHAPE_FLOOR_Z;
            }
            case b: {
                switch (direction5) {
                    case EAST: {
                        return boolean6 ? AbstractButtonBlock.SHAPE_EAST_POWERED : AbstractButtonBlock.SHAPE_EAST;
                    }
                    case WEST: {
                        return boolean6 ? AbstractButtonBlock.SHAPE_WEST_POWERED : AbstractButtonBlock.SHAPE_WEST;
                    }
                    case SOUTH: {
                        return boolean6 ? AbstractButtonBlock.SHAPE_SOUTH_POWERED : AbstractButtonBlock.SHAPE_SOUTH;
                    }
                    default: {
                        return boolean6 ? AbstractButtonBlock.SHAPE_NORTH_POWERED : AbstractButtonBlock.SHAPE_NORTH;
                    }
                }
                break;
            }
            default: {
                if (direction5.getAxis() == Direction.Axis.X) {
                    return boolean6 ? AbstractButtonBlock.SHAPE_CEILING_X_POWERED : AbstractButtonBlock.SHAPE_CEILING_X;
                }
                return boolean6 ? AbstractButtonBlock.SHAPE_CEILING_Z_POWERED : AbstractButtonBlock.SHAPE_CEILING_Z;
            }
        }
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        if (state.<Boolean>get((Property<Boolean>)AbstractButtonBlock.POWERED)) {
            return true;
        }
        world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)AbstractButtonBlock.POWERED, true), 3);
        this.playClickSound(player, world, pos, true);
        this.updateNeighbors(state, world, pos);
        world.getBlockTickScheduler().schedule(pos, this, this.getTickRate(world));
        return true;
    }
    
    protected void playClickSound(@Nullable final PlayerEntity player, final IWorld world, final BlockPos pos, final boolean powered) {
        world.playSound(powered ? player : null, pos, this.getClickSound(powered), SoundCategory.e, 0.3f, powered ? 0.6f : 0.5f);
    }
    
    protected abstract SoundEvent getClickSound(final boolean arg1);
    
    @Override
    public void onBlockRemoved(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean boolean5) {
        if (boolean5 || state.getBlock() == newState.getBlock()) {
            return;
        }
        if (state.<Boolean>get((Property<Boolean>)AbstractButtonBlock.POWERED)) {
            this.updateNeighbors(state, world, pos);
        }
        super.onBlockRemoved(state, world, pos, newState, boolean5);
    }
    
    @Override
    public int getWeakRedstonePower(final BlockState state, final BlockView view, final BlockPos pos, final Direction facing) {
        return state.<Boolean>get((Property<Boolean>)AbstractButtonBlock.POWERED) ? 15 : 0;
    }
    
    @Override
    public int getStrongRedstonePower(final BlockState state, final BlockView view, final BlockPos pos, final Direction facing) {
        if (state.<Boolean>get((Property<Boolean>)AbstractButtonBlock.POWERED) && WallMountedBlock.getDirection(state) == facing) {
            return 15;
        }
        return 0;
    }
    
    @Override
    public boolean emitsRedstonePower(final BlockState state) {
        return true;
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        if (world.isClient || !state.<Boolean>get((Property<Boolean>)AbstractButtonBlock.POWERED)) {
            return;
        }
        if (this.wooden) {
            this.tryPowerWithProjectiles(state, world, pos);
        }
        else {
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)AbstractButtonBlock.POWERED, false), 3);
            this.updateNeighbors(state, world, pos);
            this.playClickSound(null, world, pos, false);
        }
    }
    
    @Override
    public void onEntityCollision(final BlockState state, final World world, final BlockPos pos, final Entity entity) {
        if (world.isClient || !this.wooden || state.<Boolean>get((Property<Boolean>)AbstractButtonBlock.POWERED)) {
            return;
        }
        this.tryPowerWithProjectiles(state, world, pos);
    }
    
    private void tryPowerWithProjectiles(final BlockState state, final World world, final BlockPos pos) {
        final List<? extends Entity> list4 = world.getEntities(ProjectileEntity.class, state.getOutlineShape(world, pos).getBoundingBox().offset(pos));
        final boolean boolean5 = !list4.isEmpty();
        final boolean boolean6 = state.<Boolean>get((Property<Boolean>)AbstractButtonBlock.POWERED);
        if (boolean5 != boolean6) {
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)AbstractButtonBlock.POWERED, boolean5), 3);
            this.updateNeighbors(state, world, pos);
            this.playClickSound(null, world, pos, boolean5);
        }
        if (boolean5) {
            world.getBlockTickScheduler().schedule(new BlockPos(pos), this, this.getTickRate(world));
        }
    }
    
    private void updateNeighbors(final BlockState state, final World world, final BlockPos pos) {
        world.updateNeighborsAlways(pos, this);
        world.updateNeighborsAlways(pos.offset(WallMountedBlock.getDirection(state).getOpposite()), this);
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(AbstractButtonBlock.FACING, AbstractButtonBlock.POWERED, AbstractButtonBlock.FACE);
    }
    
    static {
        POWERED = Properties.POWERED;
        SHAPE_CEILING_X = Block.createCuboidShape(6.0, 14.0, 5.0, 10.0, 16.0, 11.0);
        SHAPE_CEILING_Z = Block.createCuboidShape(5.0, 14.0, 6.0, 11.0, 16.0, 10.0);
        SHAPE_FLOOR_X = Block.createCuboidShape(6.0, 0.0, 5.0, 10.0, 2.0, 11.0);
        SHAPE_FLOOR_Z = Block.createCuboidShape(5.0, 0.0, 6.0, 11.0, 2.0, 10.0);
        SHAPE_NORTH = Block.createCuboidShape(5.0, 6.0, 14.0, 11.0, 10.0, 16.0);
        SHAPE_SOUTH = Block.createCuboidShape(5.0, 6.0, 0.0, 11.0, 10.0, 2.0);
        SHAPE_WEST = Block.createCuboidShape(14.0, 6.0, 5.0, 16.0, 10.0, 11.0);
        SHAPE_EAST = Block.createCuboidShape(0.0, 6.0, 5.0, 2.0, 10.0, 11.0);
        SHAPE_CEILING_X_POWERED = Block.createCuboidShape(6.0, 15.0, 5.0, 10.0, 16.0, 11.0);
        SHAPE_CEILING_Z_POWERED = Block.createCuboidShape(5.0, 15.0, 6.0, 11.0, 16.0, 10.0);
        SHAPE_FLOOR_X_POWERED = Block.createCuboidShape(6.0, 0.0, 5.0, 10.0, 1.0, 11.0);
        SHAPE_FLOOR_Z_POWERED = Block.createCuboidShape(5.0, 0.0, 6.0, 11.0, 1.0, 10.0);
        SHAPE_NORTH_POWERED = Block.createCuboidShape(5.0, 6.0, 15.0, 11.0, 10.0, 16.0);
        SHAPE_SOUTH_POWERED = Block.createCuboidShape(5.0, 6.0, 0.0, 11.0, 10.0, 1.0);
        SHAPE_WEST_POWERED = Block.createCuboidShape(15.0, 6.0, 5.0, 16.0, 10.0, 11.0);
        SHAPE_EAST_POWERED = Block.createCuboidShape(0.0, 6.0, 5.0, 1.0, 10.0, 11.0);
    }
}
