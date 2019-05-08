package net.minecraft.block;

import net.minecraft.world.IWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ConcretePowderBlock extends FallingBlock
{
    private final BlockState hardenedState;
    
    public ConcretePowderBlock(final Block hardened, final Settings settings) {
        super(settings);
        this.hardenedState = hardened.getDefaultState();
    }
    
    @Override
    public void onLanding(final World world, final BlockPos pos, final BlockState fallingBlockState, final BlockState currentStateInPos) {
        if (hardensIn(currentStateInPos)) {
            world.setBlockState(pos, this.hardenedState, 3);
        }
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final BlockView blockView2 = ctx.getWorld();
        final BlockPos blockPos3 = ctx.getBlockPos();
        if (hardensIn(blockView2.getBlockState(blockPos3)) || hardensOnAnySide(blockView2, blockPos3)) {
            return this.hardenedState;
        }
        return super.getPlacementState(ctx);
    }
    
    private static boolean hardensOnAnySide(final BlockView view, final BlockPos pos) {
        boolean boolean3 = false;
        final BlockPos.Mutable mutable4 = new BlockPos.Mutable(pos);
        for (final Direction direction8 : Direction.values()) {
            BlockState blockState9 = view.getBlockState(mutable4);
            if (direction8 != Direction.DOWN || hardensIn(blockState9)) {
                mutable4.set(pos).setOffset(direction8);
                blockState9 = view.getBlockState(mutable4);
                if (hardensIn(blockState9) && !Block.isSolidFullSquare(blockState9, view, pos, direction8.getOpposite())) {
                    boolean3 = true;
                    break;
                }
            }
        }
        return boolean3;
    }
    
    private static boolean hardensIn(final BlockState state) {
        return state.getFluidState().matches(FluidTags.a);
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (hardensOnAnySide(world, pos)) {
            return this.hardenedState;
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
}
