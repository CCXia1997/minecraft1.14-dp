package net.minecraft.world.gen.decorator;

import java.util.BitSet;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class CarvingMaskDecorator extends Decorator<CarvingMaskDecoratorConfig>
{
    public CarvingMaskDecorator(final Function<Dynamic<?>, ? extends CarvingMaskDecoratorConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public Stream<BlockPos> getPositions(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final CarvingMaskDecoratorConfig config, final BlockPos pos) {
        final Chunk chunk6 = world.getChunk(pos);
        final ChunkPos chunkPos7 = chunk6.getPos();
        final BitSet bitSet8 = chunk6.getCarvingMask(config.step);
        final int integer2;
        final int integer3;
        final int integer4;
        final ChunkPos chunkPos8;
        return IntStream.range(0, bitSet8.length()).filter(integer -> bitSet8.get(integer) && random.nextFloat() < config.probability).<BlockPos>mapToObj(integer -> {
            integer2 = (integer & 0xF);
            integer3 = (integer >> 4 & 0xF);
            integer4 = integer >> 8;
            return new BlockPos(chunkPos8.getStartX() + integer2, integer4, chunkPos8.getStartZ() + integer3);
        });
    }
}
