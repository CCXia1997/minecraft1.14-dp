package net.minecraft.world.gen.feature;

import net.minecraft.block.BlockState;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class BushFeature extends Feature<BushFeatureConfig>
{
    public BushFeature(final Function<Dynamic<?>, ? extends BushFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final BushFeatureConfig config) {
        int integer6 = 0;
        final BlockState blockState7 = config.state;
        for (int integer7 = 0; integer7 < 64; ++integer7) {
            final BlockPos blockPos9 = pos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
            if (world.isAir(blockPos9) && (!world.getDimension().isNether() || blockPos9.getY() < 255) && blockState7.canPlaceAt(world, blockPos9)) {
                world.setBlockState(blockPos9, blockState7, 2);
                ++integer6;
            }
        }
        return integer6 > 0;
    }
}
