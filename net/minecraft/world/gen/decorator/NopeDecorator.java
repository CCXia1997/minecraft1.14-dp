package net.minecraft.world.gen.decorator;

import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class NopeDecorator extends SimpleDecorator<NopeDecoratorConfig>
{
    public NopeDecorator(final Function<Dynamic<?>, ? extends NopeDecoratorConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    public Stream<BlockPos> getPositions(final Random random, final NopeDecoratorConfig config, final BlockPos pos) {
        return Stream.<BlockPos>of(pos);
    }
}
