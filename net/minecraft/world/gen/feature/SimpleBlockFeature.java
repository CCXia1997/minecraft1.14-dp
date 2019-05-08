package net.minecraft.world.gen.feature;

import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class SimpleBlockFeature extends Feature<SimpleBlockFeatureConfig>
{
    public SimpleBlockFeature(final Function<Dynamic<?>, ? extends SimpleBlockFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final SimpleBlockFeatureConfig config) {
        if (config.placeOn.contains(world.getBlockState(pos.down())) && config.placeIn.contains(world.getBlockState(pos)) && config.placeUnder.contains(world.getBlockState(pos.up()))) {
            world.setBlockState(pos, config.toPlace, 2);
            return true;
        }
        return false;
    }
}
