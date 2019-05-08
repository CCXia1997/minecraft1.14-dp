package net.minecraft.world.gen.feature;

import net.minecraft.block.TallPlantBlock;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class DoublePlantFeature extends Feature<DoublePlantFeatureConfig>
{
    public DoublePlantFeature(final Function<Dynamic<?>, ? extends DoublePlantFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final DoublePlantFeatureConfig config) {
        boolean boolean6 = false;
        for (int integer7 = 0; integer7 < 64; ++integer7) {
            final BlockPos blockPos8 = pos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
            if (world.isAir(blockPos8) && blockPos8.getY() < 254 && config.state.canPlaceAt(world, blockPos8)) {
                ((TallPlantBlock)config.state.getBlock()).placeAt(world, blockPos8, 2);
                boolean6 = true;
            }
        }
        return boolean6;
    }
}
