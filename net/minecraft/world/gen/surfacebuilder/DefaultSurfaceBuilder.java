package net.minecraft.world.gen.surfacebuilder;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class DefaultSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig>
{
    public DefaultSurfaceBuilder(final Function<Dynamic<?>, ? extends TernarySurfaceConfig> function) {
        super(function);
    }
    
    @Override
    public void generate(final Random random, final Chunk chunk, final Biome biome, final int x, final int z, final int worldHeight, final double noise, final BlockState defaultBlock, final BlockState defaultFluid, final int seaLevel, final long seed, final TernarySurfaceConfig surfaceBlocks) {
        this.generate(random, chunk, biome, x, z, worldHeight, noise, defaultBlock, defaultFluid, surfaceBlocks.getTopMaterial(), surfaceBlocks.getUnderMaterial(), surfaceBlocks.getUnderwaterMaterial(), seaLevel);
    }
    
    protected void generate(final Random random, final Chunk chunk, final Biome biome, final int x, final int z, final int worldHeight, final double noise, final BlockState defaultBlock, final BlockState fluidBlock, final BlockState topBlock, final BlockState underBlock, final BlockState underwaterBlock, final int seaLevel) {
        BlockState blockState15 = topBlock;
        BlockState blockState16 = underBlock;
        final BlockPos.Mutable mutable17 = new BlockPos.Mutable();
        int integer18 = -1;
        final int integer19 = (int)(noise / 3.0 + 3.0 + random.nextDouble() * 0.25);
        final int integer20 = x & 0xF;
        final int integer21 = z & 0xF;
        for (int integer22 = worldHeight; integer22 >= 0; --integer22) {
            mutable17.set(integer20, integer22, integer21);
            final BlockState blockState17 = chunk.getBlockState(mutable17);
            if (blockState17.isAir()) {
                integer18 = -1;
            }
            else if (blockState17.getBlock() == defaultBlock.getBlock()) {
                if (integer18 == -1) {
                    if (integer19 <= 0) {
                        blockState15 = Blocks.AIR.getDefaultState();
                        blockState16 = defaultBlock;
                    }
                    else if (integer22 >= seaLevel - 4 && integer22 <= seaLevel + 1) {
                        blockState15 = topBlock;
                        blockState16 = underBlock;
                    }
                    if (integer22 < seaLevel && (blockState15 == null || blockState15.isAir())) {
                        if (biome.getTemperature(mutable17.set(x, integer22, z)) < 0.15f) {
                            blockState15 = Blocks.cB.getDefaultState();
                        }
                        else {
                            blockState15 = fluidBlock;
                        }
                        mutable17.set(integer20, integer22, integer21);
                    }
                    integer18 = integer19;
                    if (integer22 >= seaLevel - 1) {
                        chunk.setBlockState(mutable17, blockState15, false);
                    }
                    else if (integer22 < seaLevel - 7 - integer19) {
                        blockState15 = Blocks.AIR.getDefaultState();
                        blockState16 = defaultBlock;
                        chunk.setBlockState(mutable17, underwaterBlock, false);
                    }
                    else {
                        chunk.setBlockState(mutable17, blockState16, false);
                    }
                }
                else if (integer18 > 0) {
                    --integer18;
                    chunk.setBlockState(mutable17, blockState16, false);
                    if (integer18 == 0 && blockState16.getBlock() == Blocks.C && integer19 > 1) {
                        integer18 = random.nextInt(4) + Math.max(0, integer22 - 63);
                        blockState16 = ((blockState16.getBlock() == Blocks.D) ? Blocks.hy.getDefaultState() : Blocks.as.getDefaultState());
                    }
                }
            }
        }
    }
}
