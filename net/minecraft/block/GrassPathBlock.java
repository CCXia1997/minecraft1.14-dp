package net.minecraft.block;

import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.world.BlockView;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ViewableWorld;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.shape.VoxelShape;

public class GrassPathBlock extends Block
{
    protected static final VoxelShape SHAPE;
    
    protected GrassPathBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public boolean n(final BlockState state) {
        return true;
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        if (!this.getDefaultState().canPlaceAt(ctx.getWorld(), ctx.getBlockPos())) {
            return Block.pushEntitiesUpBeforeBlockChange(this.getDefaultState(), Blocks.j.getDefaultState(), ctx.getWorld(), ctx.getBlockPos());
        }
        return super.getPlacementState(ctx);
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (facing == Direction.UP && !state.canPlaceAt(world, pos)) {
            world.getBlockTickScheduler().schedule(pos, this, 1);
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        FarmlandBlock.setToDirt(state, world, pos);
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        final BlockState blockState4 = world.getBlockState(pos.up());
        return !blockState4.getMaterial().isSolid() || blockState4.getBlock() instanceof FenceGateBlock;
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return GrassPathBlock.SHAPE;
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        return false;
    }
    
    static {
        SHAPE = FarmlandBlock.SHAPE;
    }
}
