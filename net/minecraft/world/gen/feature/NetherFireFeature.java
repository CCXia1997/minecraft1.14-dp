package net.minecraft.world.gen.feature;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class NetherFireFeature extends Feature<DefaultFeatureConfig>
{
    public NetherFireFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final DefaultFeatureConfig config) {
        for (int integer6 = 0; integer6 < 64; ++integer6) {
            final BlockPos blockPos7 = pos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
            if (world.isAir(blockPos7)) {
                if (world.getBlockState(blockPos7.down()).getBlock() == Blocks.cJ) {
                    world.setBlockState(blockPos7, Blocks.bM.getDefaultState(), 2);
                }
            }
        }
        return true;
    }
}
