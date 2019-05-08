package net.minecraft.world.gen.feature;

import java.util.Iterator;
import net.minecraft.util.math.Vec3i;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class ForestRockFeature extends Feature<BoulderFeatureConfig>
{
    public ForestRockFeature(final Function<Dynamic<?>, ? extends BoulderFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, BlockPos pos, final BoulderFeatureConfig config) {
        while (pos.getY() > 3) {
            if (!world.isAir(pos.down())) {
                final Block block6 = world.getBlockState(pos.down()).getBlock();
                if (block6 == Blocks.i || Block.isNaturalDirt(block6)) {
                    break;
                }
                if (Block.isNaturalStone(block6)) {
                    break;
                }
            }
            pos = pos.down();
        }
        if (pos.getY() <= 3) {
            return false;
        }
        for (int integer6 = config.startRadius, integer7 = 0; integer6 >= 0 && integer7 < 3; ++integer7) {
            final int integer8 = integer6 + random.nextInt(2);
            final int integer9 = integer6 + random.nextInt(2);
            final int integer10 = integer6 + random.nextInt(2);
            final float float11 = (integer8 + integer9 + integer10) * 0.333f + 0.5f;
            for (final BlockPos blockPos13 : BlockPos.iterate(pos.add(-integer8, -integer9, -integer10), pos.add(integer8, integer9, integer10))) {
                if (blockPos13.getSquaredDistance(pos) <= float11 * float11) {
                    world.setBlockState(blockPos13, config.state, 4);
                }
            }
            pos = pos.add(-(integer6 + 1) + random.nextInt(2 + integer6 * 2), 0 - random.nextInt(2), -(integer6 + 1) + random.nextInt(2 + integer6 * 2));
        }
        return true;
    }
}
