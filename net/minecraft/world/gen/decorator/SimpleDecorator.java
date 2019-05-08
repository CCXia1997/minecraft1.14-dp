package net.minecraft.world.gen.decorator;

import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public abstract class SimpleDecorator<DC extends DecoratorConfig> extends Decorator<DC>
{
    public SimpleDecorator(final Function<Dynamic<?>, ? extends DC> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public final Stream<BlockPos> getPositions(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final DC config, final BlockPos pos) {
        return this.getPositions(random, config, pos);
    }
    
    protected abstract Stream<BlockPos> getPositions(final Random arg1, final DC arg2, final BlockPos arg3);
}
