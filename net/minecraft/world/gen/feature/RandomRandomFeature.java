package net.minecraft.world.gen.feature;

import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class RandomRandomFeature extends Feature<RandomRandomFeatureConfig>
{
    public RandomRandomFeature(final Function<Dynamic<?>, ? extends RandomRandomFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final RandomRandomFeatureConfig config) {
        for (int integer6 = random.nextInt(5) - 3 + config.count, integer7 = 0; integer7 < integer6; ++integer7) {
            final int integer8 = random.nextInt(config.features.size());
            final ConfiguredFeature<?> configuredFeature9 = config.features.get(integer8);
            configuredFeature9.generate(world, generator, random, pos);
        }
        return true;
    }
}
