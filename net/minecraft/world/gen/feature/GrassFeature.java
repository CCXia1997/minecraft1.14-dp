package net.minecraft.world.gen.feature;

import net.minecraft.block.BlockState;
import net.minecraft.world.ViewableWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class GrassFeature extends Feature<GrassFeatureConfig>
{
    public GrassFeature(final Function<Dynamic<?>, ? extends GrassFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, BlockPos pos, final GrassFeatureConfig config) {
        for (BlockState blockState6 = world.getBlockState(pos); (blockState6.isAir() || blockState6.matches(BlockTags.C)) && pos.getY() > 0; pos = pos.down(), blockState6 = world.getBlockState(pos)) {}
        int integer7 = 0;
        for (int integer8 = 0; integer8 < 128; ++integer8) {
            final BlockPos blockPos9 = pos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
            if (world.isAir(blockPos9) && config.state.canPlaceAt(world, blockPos9)) {
                world.setBlockState(blockPos9, config.state, 2);
                ++integer7;
            }
        }
        return integer7 > 0;
    }
}
