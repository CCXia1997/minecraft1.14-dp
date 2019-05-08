package net.minecraft.block;

import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.util.math.Vec3i;
import net.minecraft.entity.Entity;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ViewableWorld;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.shape.VoxelShape;

public abstract class AbstractPressurePlateBlock extends Block
{
    protected static final VoxelShape DEPRESSED_SHAPE;
    protected static final VoxelShape DEFAULT_SHAPE;
    protected static final BoundingBox BOX;
    
    protected AbstractPressurePlateBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return (this.getRedstoneOutput(state) > 0) ? AbstractPressurePlateBlock.DEPRESSED_SHAPE : AbstractPressurePlateBlock.DEFAULT_SHAPE;
    }
    
    @Override
    public int getTickRate(final ViewableWorld world) {
        return 20;
    }
    
    @Override
    public boolean canMobSpawnInside() {
        return true;
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (facing == Direction.DOWN && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        final BlockPos blockPos4 = pos.down();
        return Block.isSolidMediumSquare(world, blockPos4) || Block.isSolidSmallSquare(world, blockPos4, Direction.UP);
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        if (world.isClient) {
            return;
        }
        final int integer5 = this.getRedstoneOutput(state);
        if (integer5 > 0) {
            this.updatePlateState(world, pos, state, integer5);
        }
    }
    
    @Override
    public void onEntityCollision(final BlockState state, final World world, final BlockPos pos, final Entity entity) {
        if (world.isClient) {
            return;
        }
        final int integer5 = this.getRedstoneOutput(state);
        if (integer5 == 0) {
            this.updatePlateState(world, pos, state, integer5);
        }
    }
    
    protected void updatePlateState(final World world, final BlockPos pos, BlockState state, final int rsOut) {
        final int integer5 = this.getRedstoneOutput(world, pos);
        final boolean boolean6 = rsOut > 0;
        final boolean boolean7 = integer5 > 0;
        if (rsOut != integer5) {
            state = this.setRedstoneOutput(state, integer5);
            world.setBlockState(pos, state, 2);
            this.updateNeighbors(world, pos);
            world.scheduleBlockRender(pos);
        }
        if (!boolean7 && boolean6) {
            this.playDepressSound(world, pos);
        }
        else if (boolean7 && !boolean6) {
            this.playPressSound(world, pos);
        }
        if (boolean7) {
            world.getBlockTickScheduler().schedule(new BlockPos(pos), this, this.getTickRate(world));
        }
    }
    
    protected abstract void playPressSound(final IWorld arg1, final BlockPos arg2);
    
    protected abstract void playDepressSound(final IWorld arg1, final BlockPos arg2);
    
    @Override
    public void onBlockRemoved(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean boolean5) {
        if (boolean5 || state.getBlock() == newState.getBlock()) {
            return;
        }
        if (this.getRedstoneOutput(state) > 0) {
            this.updateNeighbors(world, pos);
        }
        super.onBlockRemoved(state, world, pos, newState, boolean5);
    }
    
    protected void updateNeighbors(final World world, final BlockPos pos) {
        world.updateNeighborsAlways(pos, this);
        world.updateNeighborsAlways(pos.down(), this);
    }
    
    @Override
    public int getWeakRedstonePower(final BlockState state, final BlockView view, final BlockPos pos, final Direction facing) {
        return this.getRedstoneOutput(state);
    }
    
    @Override
    public int getStrongRedstonePower(final BlockState state, final BlockView view, final BlockPos pos, final Direction facing) {
        if (facing == Direction.UP) {
            return this.getRedstoneOutput(state);
        }
        return 0;
    }
    
    @Override
    public boolean emitsRedstonePower(final BlockState state) {
        return true;
    }
    
    @Override
    public PistonBehavior getPistonBehavior(final BlockState state) {
        return PistonBehavior.b;
    }
    
    protected abstract int getRedstoneOutput(final World arg1, final BlockPos arg2);
    
    protected abstract int getRedstoneOutput(final BlockState arg1);
    
    protected abstract BlockState setRedstoneOutput(final BlockState arg1, final int arg2);
    
    static {
        DEPRESSED_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 0.5, 15.0);
        DEFAULT_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 1.0, 15.0);
        BOX = new BoundingBox(0.125, 0.0, 0.125, 0.875, 0.25, 0.875);
    }
}
