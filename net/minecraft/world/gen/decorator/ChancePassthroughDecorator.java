package net.minecraft.world.gen.decorator;

import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class ChancePassthroughDecorator extends SimpleDecorator<ChanceDecoratorConfig>
{
    public ChancePassthroughDecorator(final Function<Dynamic<?>, ? extends ChanceDecoratorConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    public Stream<BlockPos> getPositions(final Random random, final ChanceDecoratorConfig config, final BlockPos pos) {
        if (random.nextFloat() < 1.0f / config.chance) {
            return Stream.<BlockPos>of(pos);
        }
        return Stream.<BlockPos>empty();
    }
}
