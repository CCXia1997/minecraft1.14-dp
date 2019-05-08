package net.minecraft.world.gen.feature;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.world.ViewableWorld;
import net.minecraft.state.property.Property;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.world.Heightmap;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class SeaPickleFeature extends Feature<SeaPickleFeatureConfig>
{
    public SeaPickleFeature(final Function<Dynamic<?>, ? extends SeaPickleFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<?> generator, final Random random, final BlockPos pos, final SeaPickleFeatureConfig config) {
        int integer6 = 0;
        for (int integer7 = 0; integer7 < config.count; ++integer7) {
            final int integer8 = random.nextInt(8) - random.nextInt(8);
            final int integer9 = random.nextInt(8) - random.nextInt(8);
            final int integer10 = world.getTop(Heightmap.Type.d, pos.getX() + integer8, pos.getZ() + integer9);
            final BlockPos blockPos11 = new BlockPos(pos.getX() + integer8, integer10, pos.getZ() + integer9);
            final BlockState blockState12 = ((AbstractPropertyContainer<O, BlockState>)Blocks.kM.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)SeaPickleBlock.PICKLES, random.nextInt(4) + 1);
            if (world.getBlockState(blockPos11).getBlock() == Blocks.A && blockState12.canPlaceAt(world, blockPos11)) {
                world.setBlockState(blockPos11, blockState12, 2);
                ++integer6;
            }
        }
        return integer6 > 0;
    }
}
