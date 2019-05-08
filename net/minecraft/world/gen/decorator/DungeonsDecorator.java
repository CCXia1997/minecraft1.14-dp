package net.minecraft.world.gen.decorator;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class DungeonsDecorator extends Decorator<DungeonDecoratorConfig>
{
    public DungeonsDecorator(final Function<Dynamic<?>, ? extends DungeonDecoratorConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public Stream<BlockPos> getPositions(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final DungeonDecoratorConfig config, final BlockPos pos) {
        final int integer6 = config.chance;
        final int integer7;
        final int integer8;
        final int integer9;
        return IntStream.range(0, integer6).<BlockPos>mapToObj(integer -> {
            integer7 = random.nextInt(16);
            integer8 = random.nextInt(generator.getMaxY());
            integer9 = random.nextInt(16);
            return pos.add(integer7, integer8, integer9);
        });
    }
}
