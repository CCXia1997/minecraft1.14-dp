package net.minecraft.world.gen.feature;

import java.util.Iterator;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class RandomFeature extends Feature<RandomFeatureConfig>
{
    public RandomFeature(final Function<Dynamic<?>, ? extends RandomFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final RandomFeatureConfig config) {
        for (final RandomFeatureEntry<?> randomFeatureEntry7 : config.features) {
            if (random.nextFloat() < randomFeatureEntry7.chance) {
                return randomFeatureEntry7.generate(world, generator, random, pos);
            }
        }
        return config.defaultFeature.generate(world, generator, random, pos);
    }
}
