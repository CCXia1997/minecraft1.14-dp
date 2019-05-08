package net.minecraft.world.gen.decorator;

import java.util.stream.IntStream;
import net.minecraft.world.biome.Biome;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class HeightmapNoiseBiasedDecorator extends Decorator<TopSolidHeightmapNoiseBiasedDecoratorConfig>
{
    public HeightmapNoiseBiasedDecorator(final Function<Dynamic<?>, ? extends TopSolidHeightmapNoiseBiasedDecoratorConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public Stream<BlockPos> getPositions(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final TopSolidHeightmapNoiseBiasedDecoratorConfig config, final BlockPos pos) {
        final double double6 = Biome.FOLIAGE_NOISE.sample(pos.getX() / config.noiseFactor, pos.getZ() / config.noiseFactor);
        final int integer8 = (int)Math.ceil((double6 + config.noiseOffset) * config.noiseToCountRatio);
        final int integer9;
        final int integer10;
        final int integer11;
        return IntStream.range(0, integer8).<BlockPos>mapToObj(integer -> {
            integer9 = random.nextInt(16);
            integer10 = random.nextInt(16);
            integer11 = world.getTop(config.heightmap, pos.getX() + integer9, pos.getZ() + integer10);
            return new BlockPos(pos.getX() + integer9, integer11, pos.getZ() + integer10);
        });
    }
}
