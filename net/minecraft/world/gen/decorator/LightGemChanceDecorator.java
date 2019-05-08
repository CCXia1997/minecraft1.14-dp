package net.minecraft.world.gen.decorator;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class LightGemChanceDecorator extends SimpleDecorator<CountDecoratorConfig>
{
    public LightGemChanceDecorator(final Function<Dynamic<?>, ? extends CountDecoratorConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    public Stream<BlockPos> getPositions(final Random random, final CountDecoratorConfig config, final BlockPos pos) {
        final int integer4;
        final int integer5;
        final int integer6;
        return IntStream.range(0, random.nextInt(random.nextInt(config.count) + 1)).<BlockPos>mapToObj(integer -> {
            integer4 = random.nextInt(16);
            integer5 = random.nextInt(120) + 4;
            integer6 = random.nextInt(16);
            return pos.add(integer4, integer5, integer6);
        });
    }
}
