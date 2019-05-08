package net.minecraft.world.gen.decorator;

import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class EndIslandDecorator extends SimpleDecorator<NopeDecoratorConfig>
{
    public EndIslandDecorator(final Function<Dynamic<?>, ? extends NopeDecoratorConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    public Stream<BlockPos> getPositions(final Random random, final NopeDecoratorConfig config, final BlockPos pos) {
        Stream<BlockPos> stream4 = Stream.<BlockPos>empty();
        if (random.nextInt(14) == 0) {
            stream4 = Stream.<BlockPos>concat(stream4, Stream.of((T)pos.add(random.nextInt(16), 55 + random.nextInt(16), random.nextInt(16))));
            if (random.nextInt(4) == 0) {
                stream4 = Stream.<BlockPos>concat(stream4, Stream.of((T)pos.add(random.nextInt(16), 55 + random.nextInt(16), random.nextInt(16))));
            }
            return stream4;
        }
        return Stream.<BlockPos>empty();
    }
}
