package net.minecraft.world.gen.feature;

import net.minecraft.util.math.Direction;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class GlowstoneBlobFeature extends Feature<DefaultFeatureConfig>
{
    public GlowstoneBlobFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final DefaultFeatureConfig config) {
        if (!world.isAir(pos)) {
            return false;
        }
        if (world.getBlockState(pos.up()).getBlock() != Blocks.cJ) {
            return false;
        }
        world.setBlockState(pos, Blocks.cL.getDefaultState(), 2);
        for (int integer6 = 0; integer6 < 1500; ++integer6) {
            final BlockPos blockPos7 = pos.add(random.nextInt(8) - random.nextInt(8), -random.nextInt(12), random.nextInt(8) - random.nextInt(8));
            if (world.getBlockState(blockPos7).isAir()) {
                int integer7 = 0;
                for (final Direction direction12 : Direction.values()) {
                    if (world.getBlockState(blockPos7.offset(direction12)).getBlock() == Blocks.cL) {
                        ++integer7;
                    }
                    if (integer7 > 1) {
                        break;
                    }
                }
                if (integer7 == 1) {
                    world.setBlockState(blockPos7, Blocks.cL.getDefaultState(), 2);
                }
            }
        }
        return true;
    }
}
