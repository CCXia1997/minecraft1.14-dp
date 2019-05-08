package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.tag.FluidTags;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.BlockView;
import net.minecraft.util.math.Direction;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;

public abstract class SpreadableBlock extends SnowyBlock
{
    protected SpreadableBlock(final Settings settings) {
        super(settings);
    }
    
    private static boolean b(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        final BlockPos blockPos4 = pos.up();
        final BlockState blockState5 = world.getBlockState(blockPos4);
        if (blockState5.getBlock() == Blocks.cA && blockState5.<Integer>get((Property<Integer>)SnowBlock.LAYERS) == 1) {
            return true;
        }
        final int integer6 = ChunkLightProvider.a(world, state, pos, blockState5, blockPos4, Direction.UP, blockState5.getLightSubtracted(world, blockPos4));
        return integer6 < world.getMaxLightLevel();
    }
    
    private static boolean c(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        final BlockPos blockPos4 = pos.up();
        return b(state, world, pos) && !world.getFluidState(blockPos4).matches(FluidTags.a);
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        if (world.isClient) {
            return;
        }
        if (!b(state, world, pos)) {
            world.setBlockState(pos, Blocks.j.getDefaultState());
            return;
        }
        if (world.getLightLevel(pos.up()) < 4) {
            return;
        }
        if (world.getLightLevel(pos.up()) >= 9) {
            final BlockState blockState5 = this.getDefaultState();
            for (int integer6 = 0; integer6 < 4; ++integer6) {
                final BlockPos blockPos7 = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                if (world.getBlockState(blockPos7).getBlock() == Blocks.j && c(blockState5, world, blockPos7)) {
                    world.setBlockState(blockPos7, ((AbstractPropertyContainer<O, BlockState>)blockState5).<Comparable, Boolean>with((Property<Comparable>)SpreadableBlock.SNOWY, world.getBlockState(blockPos7.up()).getBlock() == Blocks.cA));
                }
            }
        }
    }
}
