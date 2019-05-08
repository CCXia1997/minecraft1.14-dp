package net.minecraft.world.gen.decorator;

import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class WaterLakeDecorator extends Decorator<LakeDecoratorConfig>
{
    public WaterLakeDecorator(final Function<Dynamic<?>, ? extends LakeDecoratorConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public Stream<BlockPos> getPositions(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final LakeDecoratorConfig config, final BlockPos pos) {
        if (random.nextInt(config.chance) == 0) {
            final int integer6 = random.nextInt(16);
            final int integer7 = random.nextInt(generator.getMaxY());
            final int integer8 = random.nextInt(16);
            return Stream.<BlockPos>of(pos.add(integer6, integer7, integer8));
        }
        return Stream.<BlockPos>empty();
    }
}
