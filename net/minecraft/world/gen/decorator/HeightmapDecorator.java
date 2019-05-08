package net.minecraft.world.gen.decorator;

import net.minecraft.world.Heightmap;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class HeightmapDecorator extends Decorator<NopeDecoratorConfig>
{
    public HeightmapDecorator(final Function<Dynamic<?>, ? extends NopeDecoratorConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public Stream<BlockPos> getPositions(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final NopeDecoratorConfig config, final BlockPos pos) {
        final int integer6 = random.nextInt(16);
        final int integer7 = random.nextInt(16);
        final int integer8 = world.getTop(Heightmap.Type.c, pos.getX() + integer6, pos.getZ() + integer7);
        return Stream.<BlockPos>of(new BlockPos(pos.getX() + integer6, integer8, pos.getZ() + integer7));
    }
}
