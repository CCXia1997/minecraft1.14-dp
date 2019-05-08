package net.minecraft.world.gen.feature;

import java.util.Iterator;
import net.minecraft.block.BlockState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class DiskFeature extends Feature<DiskFeatureConfig>
{
    public DiskFeature(final Function<Dynamic<?>, ? extends DiskFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final DiskFeatureConfig config) {
        if (!world.getFluidState(pos).matches(FluidTags.a)) {
            return false;
        }
        int integer6 = 0;
        for (int integer7 = random.nextInt(config.radius - 2) + 2, integer8 = pos.getX() - integer7; integer8 <= pos.getX() + integer7; ++integer8) {
            for (int integer9 = pos.getZ() - integer7; integer9 <= pos.getZ() + integer7; ++integer9) {
                final int integer10 = integer8 - pos.getX();
                final int integer11 = integer9 - pos.getZ();
                if (integer10 * integer10 + integer11 * integer11 <= integer7 * integer7) {
                    for (int integer12 = pos.getY() - config.ySize; integer12 <= pos.getY() + config.ySize; ++integer12) {
                        final BlockPos blockPos13 = new BlockPos(integer8, integer12, integer9);
                        final BlockState blockState14 = world.getBlockState(blockPos13);
                        for (final BlockState blockState15 : config.targets) {
                            if (blockState15.getBlock() == blockState14.getBlock()) {
                                world.setBlockState(blockPos13, config.state, 2);
                                ++integer6;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return integer6 > 0;
    }
}
