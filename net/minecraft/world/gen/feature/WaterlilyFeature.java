package net.minecraft.world.gen.feature;

import net.minecraft.block.BlockState;
import net.minecraft.world.ViewableWorld;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class WaterlilyFeature extends Feature<DefaultFeatureConfig>
{
    public WaterlilyFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final DefaultFeatureConfig config) {
        BlockPos blockPos7;
        for (BlockPos blockPos6 = pos; blockPos6.getY() > 0; blockPos6 = blockPos7) {
            blockPos7 = blockPos6.down();
            if (!world.isAir(blockPos7)) {
                break;
            }
        }
        for (int integer7 = 0; integer7 < 10; ++integer7) {
            final BlockPos blockPos8 = pos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
            final BlockState blockState9 = Blocks.dM.getDefaultState();
            if (world.isAir(blockPos8) && blockState9.canPlaceAt(world, blockPos8)) {
                world.setBlockState(blockPos8, blockState9, 2);
            }
        }
        return true;
    }
}
