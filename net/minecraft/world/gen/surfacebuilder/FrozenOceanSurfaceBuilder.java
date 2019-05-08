package net.minecraft.world.gen.surfacebuilder;

import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.block.BlockState;

public class FrozenOceanSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig>
{
    protected static final BlockState PACKED_ICE;
    protected static final BlockState SNOW_BLOCK;
    private static final BlockState AIR;
    private static final BlockState GRAVEL;
    private static final BlockState ICE;
    private OctaveSimplexNoiseSampler S;
    private OctaveSimplexNoiseSampler T;
    private long seed;
    
    public FrozenOceanSurfaceBuilder(final Function<Dynamic<?>, ? extends TernarySurfaceConfig> function) {
        super(function);
    }
    
    @Override
    public void generate(final Random random, final Chunk chunk, final Biome biome, final int x, final int z, final int worldHeight, final double noise, final BlockState defaultBlock, final BlockState defaultFluid, final int seaLevel, final long seed, final TernarySurfaceConfig surfaceBlocks) {
        double double15 = 0.0;
        double double16 = 0.0;
        final BlockPos.Mutable mutable19 = new BlockPos.Mutable();
        final float float20 = biome.getTemperature(mutable19.set(x, 63, z));
        final double double17 = Math.min(Math.abs(noise), this.S.sample(x * 0.1, z * 0.1));
        if (double17 > 1.8) {
            final double double18 = 0.09765625;
            final double double19 = Math.abs(this.T.sample(x * 0.09765625, z * 0.09765625));
            double15 = double17 * double17 * 1.2;
            final double double20 = Math.ceil(double19 * 40.0) + 14.0;
            if (double15 > double20) {
                double15 = double20;
            }
            if (float20 > 0.1f) {
                double15 -= 2.0;
            }
            if (double15 > 2.0) {
                double16 = seaLevel - double15 - 7.0;
                double15 += seaLevel;
            }
            else {
                double15 = 0.0;
            }
        }
        final int integer23 = x & 0xF;
        final int integer24 = z & 0xF;
        BlockState blockState25 = biome.getSurfaceConfig().getUnderMaterial();
        BlockState blockState26 = biome.getSurfaceConfig().getTopMaterial();
        final int integer25 = (int)(noise / 3.0 + 3.0 + random.nextDouble() * 0.25);
        int integer26 = -1;
        int integer27 = 0;
        final int integer28 = 2 + random.nextInt(4);
        final int integer29 = seaLevel + 18 + random.nextInt(10);
        for (int integer30 = Math.max(worldHeight, (int)double15 + 1); integer30 >= 0; --integer30) {
            mutable19.set(integer23, integer30, integer24);
            if (chunk.getBlockState(mutable19).isAir() && integer30 < (int)double15 && random.nextDouble() > 0.01) {
                chunk.setBlockState(mutable19, FrozenOceanSurfaceBuilder.PACKED_ICE, false);
            }
            else if (chunk.getBlockState(mutable19).getMaterial() == Material.WATER && integer30 > (int)double16 && integer30 < seaLevel && double16 != 0.0 && random.nextDouble() > 0.15) {
                chunk.setBlockState(mutable19, FrozenOceanSurfaceBuilder.PACKED_ICE, false);
            }
            final BlockState blockState27 = chunk.getBlockState(mutable19);
            if (blockState27.isAir()) {
                integer26 = -1;
            }
            else if (blockState27.getBlock() == defaultBlock.getBlock()) {
                if (integer26 == -1) {
                    if (integer25 <= 0) {
                        blockState26 = FrozenOceanSurfaceBuilder.AIR;
                        blockState25 = defaultBlock;
                    }
                    else if (integer30 >= seaLevel - 4 && integer30 <= seaLevel + 1) {
                        blockState26 = biome.getSurfaceConfig().getTopMaterial();
                        blockState25 = biome.getSurfaceConfig().getUnderMaterial();
                    }
                    if (integer30 < seaLevel && (blockState26 == null || blockState26.isAir())) {
                        if (biome.getTemperature(mutable19.set(x, integer30, z)) < 0.15f) {
                            blockState26 = FrozenOceanSurfaceBuilder.ICE;
                        }
                        else {
                            blockState26 = defaultFluid;
                        }
                    }
                    integer26 = integer25;
                    if (integer30 >= seaLevel - 1) {
                        chunk.setBlockState(mutable19, blockState26, false);
                    }
                    else if (integer30 < seaLevel - 7 - integer25) {
                        blockState26 = FrozenOceanSurfaceBuilder.AIR;
                        blockState25 = defaultBlock;
                        chunk.setBlockState(mutable19, FrozenOceanSurfaceBuilder.GRAVEL, false);
                    }
                    else {
                        chunk.setBlockState(mutable19, blockState25, false);
                    }
                }
                else if (integer26 > 0) {
                    --integer26;
                    chunk.setBlockState(mutable19, blockState25, false);
                    if (integer26 == 0 && blockState25.getBlock() == Blocks.C && integer25 > 1) {
                        integer26 = random.nextInt(4) + Math.max(0, integer30 - 63);
                        blockState25 = ((blockState25.getBlock() == Blocks.D) ? Blocks.hy.getDefaultState() : Blocks.as.getDefaultState());
                    }
                }
            }
            else if (blockState27.getBlock() == Blocks.gL && integer27 <= integer28 && integer30 > integer29) {
                chunk.setBlockState(mutable19, FrozenOceanSurfaceBuilder.SNOW_BLOCK, false);
                ++integer27;
            }
        }
    }
    
    @Override
    public void initSeed(final long seed) {
        if (this.seed != seed || this.S == null || this.T == null) {
            final Random random3 = new ChunkRandom(seed);
            this.S = new OctaveSimplexNoiseSampler(random3, 4);
            this.T = new OctaveSimplexNoiseSampler(random3, 1);
        }
        this.seed = seed;
    }
    
    static {
        PACKED_ICE = Blocks.gL.getDefaultState();
        SNOW_BLOCK = Blocks.cC.getDefaultState();
        AIR = Blocks.AIR.getDefaultState();
        GRAVEL = Blocks.E.getDefaultState();
        ICE = Blocks.cB.getDefaultState();
    }
}
