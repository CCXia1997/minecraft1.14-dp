package net.minecraft.world.gen.surfacebuilder;

import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class SwampSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig>
{
    public SwampSurfaceBuilder(final Function<Dynamic<?>, ? extends TernarySurfaceConfig> function) {
        super(function);
    }
    
    @Override
    public void generate(final Random random, final Chunk chunk, final Biome biome, final int x, final int z, final int worldHeight, final double noise, final BlockState defaultBlock, final BlockState defaultFluid, final int seaLevel, final long seed, final TernarySurfaceConfig surfaceBlocks) {
        final double double15 = Biome.FOLIAGE_NOISE.sample(x * 0.25, z * 0.25);
        if (double15 > 0.0) {
            final int integer17 = x & 0xF;
            final int integer18 = z & 0xF;
            final BlockPos.Mutable mutable19 = new BlockPos.Mutable();
            int integer19 = worldHeight;
            while (integer19 >= 0) {
                mutable19.set(integer17, integer19, integer18);
                if (!chunk.getBlockState(mutable19).isAir()) {
                    if (integer19 == 62 && chunk.getBlockState(mutable19).getBlock() != defaultFluid.getBlock()) {
                        chunk.setBlockState(mutable19, defaultFluid, false);
                        break;
                    }
                    break;
                }
                else {
                    --integer19;
                }
            }
        }
        SurfaceBuilder.DEFAULT.generate(random, chunk, biome, x, z, worldHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, surfaceBlocks);
    }
}
