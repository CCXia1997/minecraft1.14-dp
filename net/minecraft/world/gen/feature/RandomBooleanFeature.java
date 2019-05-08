package net.minecraft.world.gen.feature;

import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class RandomBooleanFeature extends Feature<RandomBooleanFeatureConfig>
{
    public RandomBooleanFeature(final Function<Dynamic<?>, ? extends RandomBooleanFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final RandomBooleanFeatureConfig config) {
        final boolean boolean6 = random.nextBoolean();
        if (boolean6) {
            return config.featureTrue.generate(world, generator, random, pos);
        }
        return config.featureFalse.generate(world, generator, random, pos);
    }
}
