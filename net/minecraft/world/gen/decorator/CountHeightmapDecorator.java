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

public class CountHeightmapDecorator extends Decorator<CountDecoratorConfig>
{
    public CountHeightmapDecorator(final Function<Dynamic<?>, ? extends CountDecoratorConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public Stream<BlockPos> getPositions(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final CountDecoratorConfig config, final BlockPos pos) {
        final int integer5;
        final int integer6;
        return IntStream.range(0, config.count).<BlockPos>mapToObj(integer -> {
            integer5 = random.nextInt(16);
            integer6 = random.nextInt(16);
            return world.getTopPosition(Heightmap.Type.e, pos.add(integer5, 0, integer6));
        });
    }
}
