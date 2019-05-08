package net.minecraft.world.gen.decorator;

import net.minecraft.world.Heightmap;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class HeightmapRangeDecorator extends Decorator<HeightmapRangeDecoratorConfig>
{
    public HeightmapRangeDecorator(final Function<Dynamic<?>, ? extends HeightmapRangeDecoratorConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public Stream<BlockPos> getPositions(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final HeightmapRangeDecoratorConfig config, final BlockPos pos) {
        final int integer6 = random.nextInt(config.max - config.min) + config.min;
        final int integer7;
        final int integer8;
        final int integer9;
        return IntStream.range(0, integer6).<BlockPos>mapToObj(integer -> {
            integer7 = random.nextInt(16);
            integer8 = random.nextInt(16);
            integer9 = world.getTop(Heightmap.Type.c, pos.getX() + integer7, pos.getZ() + integer8);
            return new BlockPos(pos.getX() + integer7, integer9, pos.getZ() + integer8);
        });
    }
}
