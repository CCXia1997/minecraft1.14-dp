package net.minecraft.world.gen.feature;

import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import net.minecraft.block.Blocks;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.Block;

public class IcePatchFeature extends Feature<IcePatchFeatureConfig>
{
    private final Block ICE;
    
    public IcePatchFeature(final Function<Dynamic<?>, ? extends IcePatchFeatureConfig> configDeserializer) {
        super(configDeserializer);
        this.ICE = Blocks.gL;
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, BlockPos pos, final IcePatchFeatureConfig config) {
        while (world.isAir(pos) && pos.getY() > 2) {
            pos = pos.down();
        }
        if (world.getBlockState(pos).getBlock() != Blocks.cC) {
            return false;
        }
        final int integer6 = random.nextInt(config.radius) + 2;
        final int integer7 = 1;
        for (int integer8 = pos.getX() - integer6; integer8 <= pos.getX() + integer6; ++integer8) {
            for (int integer9 = pos.getZ() - integer6; integer9 <= pos.getZ() + integer6; ++integer9) {
                final int integer10 = integer8 - pos.getX();
                final int integer11 = integer9 - pos.getZ();
                if (integer10 * integer10 + integer11 * integer11 <= integer6 * integer6) {
                    for (int integer12 = pos.getY() - 1; integer12 <= pos.getY() + 1; ++integer12) {
                        final BlockPos blockPos13 = new BlockPos(integer8, integer12, integer9);
                        final Block block14 = world.getBlockState(blockPos13).getBlock();
                        if (Block.isNaturalDirt(block14) || block14 == Blocks.cC || block14 == Blocks.cB) {
                            world.setBlockState(blockPos13, this.ICE.getDefaultState(), 2);
                        }
                    }
                }
            }
        }
        return true;
    }
}
