package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CoralBlockBlock extends Block
{
    private final Block deadCoralBlock;
    
    public CoralBlockBlock(final Block block, final Settings settings) {
        super(settings);
        this.deadCoralBlock = block;
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        if (!this.isInWater(world, pos)) {
            world.setBlockState(pos, this.deadCoralBlock.getDefaultState(), 2);
        }
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (!this.isInWater(world, pos)) {
            world.getBlockTickScheduler().schedule(pos, this, 60 + world.getRandom().nextInt(40));
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    protected boolean isInWater(final BlockView world, final BlockPos pos) {
        for (final Direction direction6 : Direction.values()) {
            final FluidState fluidState7 = world.getFluidState(pos.offset(direction6));
            if (fluidState7.matches(FluidTags.a)) {
                return true;
            }
        }
        return false;
    }
    
    @Nullable
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        if (!this.isInWater(ctx.getWorld(), ctx.getBlockPos())) {
            ctx.getWorld().getBlockTickScheduler().schedule(ctx.getBlockPos(), this, 60 + ctx.getWorld().getRandom().nextInt(40));
        }
        return this.getDefaultState();
    }
}
