package net.minecraft.world.gen.feature;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class MelonFeature extends Feature<DefaultFeatureConfig>
{
    public MelonFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final DefaultFeatureConfig config) {
        for (int integer6 = 0; integer6 < 64; ++integer6) {
            final BlockPos blockPos7 = pos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
            final BlockState blockState8 = Blocks.dC.getDefaultState();
            if (world.getBlockState(blockPos7).getMaterial().isReplaceable() && world.getBlockState(blockPos7.down()).getBlock() == Blocks.i) {
                world.setBlockState(blockPos7, blockState8, 2);
            }
        }
        return true;
    }
}
