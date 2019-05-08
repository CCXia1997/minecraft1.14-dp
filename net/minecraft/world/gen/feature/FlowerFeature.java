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

public abstract class FlowerFeature extends Feature<DefaultFeatureConfig>
{
    public FlowerFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer, false);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final DefaultFeatureConfig config) {
        final BlockState blockState6 = this.getFlowerToPlace(random, pos);
        int integer7 = 0;
        for (int integer8 = 0; integer8 < 64; ++integer8) {
            final BlockPos blockPos9 = pos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
            if (world.isAir(blockPos9) && blockPos9.getY() < 255 && blockState6.canPlaceAt(world, blockPos9)) {
                world.setBlockState(blockPos9, blockState6, 2);
                ++integer7;
            }
        }
        return integer7 > 0;
    }
    
    public abstract BlockState getFlowerToPlace(final Random arg1, final BlockPos arg2);
}
