package net.minecraft.world.gen.decorator;

import java.util.List;
import com.google.common.collect.Lists;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class HellFireDecorator extends SimpleDecorator<CountDecoratorConfig>
{
    public HellFireDecorator(final Function<Dynamic<?>, ? extends CountDecoratorConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    public Stream<BlockPos> getPositions(final Random random, final CountDecoratorConfig config, final BlockPos pos) {
        final List<BlockPos> list4 = Lists.newArrayList();
        for (int integer5 = 0; integer5 < random.nextInt(random.nextInt(config.count) + 1) + 1; ++integer5) {
            final int integer6 = random.nextInt(16);
            final int integer7 = random.nextInt(120) + 4;
            final int integer8 = random.nextInt(16);
            list4.add(pos.add(integer6, integer7, integer8));
        }
        return list4.stream();
    }
}
