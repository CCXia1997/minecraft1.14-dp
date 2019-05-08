package net.minecraft.world.gen.decorator;

import java.util.function.Predicate;
import java.util.Objects;
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

public class ChorusPlantDecorator extends Decorator<NopeDecoratorConfig>
{
    public ChorusPlantDecorator(final Function<Dynamic<?>, ? extends NopeDecoratorConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public Stream<BlockPos> getPositions(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final NopeDecoratorConfig config, final BlockPos pos) {
        final int integer6 = random.nextInt(5);
        final int integer7;
        final int integer8;
        final int integer9;
        int integer10;
        return IntStream.range(0, integer6).<BlockPos>mapToObj(integer -> {
            integer7 = random.nextInt(16);
            integer8 = random.nextInt(16);
            integer9 = world.getTopPosition(Heightmap.Type.e, pos.add(integer7, 0, integer8)).getY();
            if (integer9 > 0) {
                integer10 = integer9 - 1;
                return new BlockPos(pos.getX() + integer7, integer10, pos.getZ() + integer8);
            }
            else {
                return null;
            }
        }).filter(Objects::nonNull);
    }
}
