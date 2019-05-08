package net.minecraft.world.gen.decorator;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class RandomCountRangeDecorator extends SimpleDecorator<RangeDecoratorConfig>
{
    public RandomCountRangeDecorator(final Function<Dynamic<?>, ? extends RangeDecoratorConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    public Stream<BlockPos> getPositions(final Random random, final RangeDecoratorConfig config, final BlockPos pos) {
        final int integer4 = random.nextInt(Math.max(config.count, 1));
        final int integer5;
        final int integer6;
        final int integer7;
        return IntStream.range(0, integer4).<BlockPos>mapToObj(integer -> {
            integer5 = random.nextInt(16);
            integer6 = random.nextInt(config.maximum - config.topOffset) + config.bottomOffset;
            integer7 = random.nextInt(16);
            return pos.add(integer5, integer6, integer7);
        });
    }
}
