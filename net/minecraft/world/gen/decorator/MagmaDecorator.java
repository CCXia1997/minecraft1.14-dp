package net.minecraft.world.gen.decorator;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class MagmaDecorator extends Decorator<CountDecoratorConfig>
{
    public MagmaDecorator(final Function<Dynamic<?>, ? extends CountDecoratorConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public Stream<BlockPos> getPositions(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final CountDecoratorConfig config, final BlockPos pos) {
        final int integer6 = world.getSeaLevel() / 2 + 1;
        final int integer7;
        final int n;
        final int integer8;
        final int integer9;
        return IntStream.range(0, config.count).<BlockPos>mapToObj(integer4 -> {
            integer7 = random.nextInt(16);
            integer8 = n - 5 + random.nextInt(10);
            integer9 = random.nextInt(16);
            return pos.add(integer7, integer8, integer9);
        });
    }
}
