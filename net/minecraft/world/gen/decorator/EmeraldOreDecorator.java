package net.minecraft.world.gen.decorator;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class EmeraldOreDecorator extends SimpleDecorator<NopeDecoratorConfig>
{
    public EmeraldOreDecorator(final Function<Dynamic<?>, ? extends NopeDecoratorConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    public Stream<BlockPos> getPositions(final Random random, final NopeDecoratorConfig config, final BlockPos pos) {
        final int integer4 = 3 + random.nextInt(6);
        final int integer5;
        final int integer6;
        final int integer7;
        return IntStream.range(0, integer4).<BlockPos>mapToObj(integer -> {
            integer5 = random.nextInt(16);
            integer6 = random.nextInt(28) + 4;
            integer7 = random.nextInt(16);
            return pos.add(integer5, integer6, integer7);
        });
    }
}
