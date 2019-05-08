package net.minecraft.world.gen.decorator;

import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class ChanceRangeDecorator extends SimpleDecorator<ChanceRangeDecoratorConfig>
{
    public ChanceRangeDecorator(final Function<Dynamic<?>, ? extends ChanceRangeDecoratorConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    public Stream<BlockPos> getPositions(final Random random, final ChanceRangeDecoratorConfig config, final BlockPos pos) {
        if (random.nextFloat() < config.chance) {
            final int integer4 = random.nextInt(16);
            final int integer5 = random.nextInt(config.top - config.topOffset) + config.bottomOffset;
            final int integer6 = random.nextInt(16);
            return Stream.<BlockPos>of(pos.add(integer4, integer5, integer6));
        }
        return Stream.<BlockPos>empty();
    }
}
