package net.minecraft.world.gen.decorator;

import net.minecraft.world.Heightmap;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class DarkOakTreeDecorator extends Decorator<NopeDecoratorConfig>
{
    public DarkOakTreeDecorator(final Function<Dynamic<?>, ? extends NopeDecoratorConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public Stream<BlockPos> getPositions(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final NopeDecoratorConfig config, final BlockPos pos) {
        final int integer2;
        final int integer3;
        final int integer4;
        final int integer5;
        return IntStream.range(0, 16).<BlockPos>mapToObj(integer -> {
            integer2 = integer / 4;
            integer3 = integer % 4;
            integer4 = integer2 * 4 + 1 + random.nextInt(3);
            integer5 = integer3 * 4 + 1 + random.nextInt(3);
            return world.getTopPosition(Heightmap.Type.e, pos.add(integer4, 0, integer5));
        });
    }
}
