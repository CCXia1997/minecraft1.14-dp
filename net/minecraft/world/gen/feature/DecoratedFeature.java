package net.minecraft.world.gen.feature;

import net.minecraft.util.registry.Registry;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class DecoratedFeature extends Feature<DecoratedFeatureConfig>
{
    public DecoratedFeature(final Function<Dynamic<?>, ? extends DecoratedFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final DecoratedFeatureConfig config) {
        return config.decorator.generate(world, generator, random, pos, config.feature);
    }
    
    @Override
    public String toString() {
        return String.format("< %s [%s] >", this.getClass().getSimpleName(), Registry.FEATURE.getId(this));
    }
}
