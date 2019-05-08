package net.minecraft.world.gen.feature;

import net.minecraft.world.ViewableWorld;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class CactusFeature extends Feature<DefaultFeatureConfig>
{
    public CactusFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final DefaultFeatureConfig config) {
        for (int integer6 = 0; integer6 < 10; ++integer6) {
            final BlockPos blockPos7 = pos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
            if (world.isAir(blockPos7)) {
                for (int integer7 = 1 + random.nextInt(random.nextInt(3) + 1), integer8 = 0; integer8 < integer7; ++integer8) {
                    if (Blocks.cD.getDefaultState().canPlaceAt(world, blockPos7)) {
                        world.setBlockState(blockPos7.up(integer8), Blocks.cD.getDefaultState(), 2);
                    }
                }
            }
        }
        return true;
    }
}
