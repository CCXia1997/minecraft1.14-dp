package net.minecraft.world.gen.feature;

import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class FillLayerFeature extends Feature<FillLayerFeatureConfig>
{
    public FillLayerFeature(final Function<Dynamic<?>, ? extends FillLayerFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final FillLayerFeatureConfig config) {
        final BlockPos.Mutable mutable6 = new BlockPos.Mutable();
        for (int integer7 = 0; integer7 < 16; ++integer7) {
            for (int integer8 = 0; integer8 < 16; ++integer8) {
                final int integer9 = pos.getX() + integer7;
                final int integer10 = pos.getZ() + integer8;
                final int integer11 = config.height;
                mutable6.set(integer9, integer11, integer10);
                if (world.getBlockState(mutable6).isAir()) {
                    world.setBlockState(mutable6, config.state, 2);
                }
            }
        }
        return true;
    }
}
