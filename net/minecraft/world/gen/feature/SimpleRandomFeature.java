package net.minecraft.world.gen.feature;

import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class SimpleRandomFeature extends Feature<SimpleRandomFeatureConfig>
{
    public SimpleRandomFeature(final Function<Dynamic<?>, ? extends SimpleRandomFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final SimpleRandomFeatureConfig config) {
        final int integer6 = random.nextInt(config.features.size());
        final ConfiguredFeature<?> configuredFeature7 = config.features.get(integer6);
        return configuredFeature7.generate(world, generator, random, pos);
    }
}
