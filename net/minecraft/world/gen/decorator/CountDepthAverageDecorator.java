package net.minecraft.world.gen.decorator;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class CountDepthAverageDecorator extends SimpleDecorator<CountDepthDecoratorConfig>
{
    public CountDepthAverageDecorator(final Function<Dynamic<?>, ? extends CountDepthDecoratorConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    public Stream<BlockPos> getPositions(final Random random, final CountDepthDecoratorConfig config, final BlockPos pos) {
        final int integer4 = config.count;
        final int integer5 = config.baseline;
        final int integer6 = config.spread;
        final int integer7;
        final int n;
        final int n2;
        final int integer8;
        final int integer9;
        return IntStream.range(0, integer4).<BlockPos>mapToObj(integer5 -> {
            integer7 = random.nextInt(16);
            integer8 = random.nextInt(n) + random.nextInt(n) - n + n2;
            integer9 = random.nextInt(16);
            return pos.add(integer7, integer8, integer9);
        });
    }
}
