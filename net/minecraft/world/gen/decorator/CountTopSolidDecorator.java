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

public class CountTopSolidDecorator extends Decorator<CountDecoratorConfig>
{
    public CountTopSolidDecorator(final Function<Dynamic<?>, ? extends CountDecoratorConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public Stream<BlockPos> getPositions(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final CountDecoratorConfig config, final BlockPos pos) {
        final int integer5;
        final int integer6;
        return IntStream.range(0, config.count).<BlockPos>mapToObj(integer -> {
            integer5 = random.nextInt(16) + pos.getX();
            integer6 = random.nextInt(16) + pos.getZ();
            return new BlockPos(integer5, world.getTop(Heightmap.Type.c, integer5, integer6), integer6);
        });
    }
}
