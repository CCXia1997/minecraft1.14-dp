package net.minecraft.world.gen.feature;

import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class EmeraldOreFeature extends Feature<EmeraldOreFeatureConfig>
{
    public EmeraldOreFeature(final Function<Dynamic<?>, ? extends EmeraldOreFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final EmeraldOreFeatureConfig config) {
        if (world.getBlockState(pos).getBlock() == config.target.getBlock()) {
            world.setBlockState(pos, config.state, 2);
        }
        return true;
    }
}
