package net.minecraft.world.gen.surfacebuilder;

import net.minecraft.block.Blocks;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.block.BlockState;

public class NetherSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig>
{
    private static final BlockState CAVE_AIR;
    private static final BlockState NETHERRACK;
    private static final BlockState GRAVEL;
    private static final BlockState GLOWSTONE;
    protected long seed;
    protected OctavePerlinNoiseSampler noise;
    
    public NetherSurfaceBuilder(final Function<Dynamic<?>, ? extends TernarySurfaceConfig> function) {
        super(function);
    }
    
    @Override
    public void generate(final Random random, final Chunk chunk, final Biome biome, final int x, final int z, final int worldHeight, final double noise, final BlockState defaultBlock, final BlockState defaultFluid, final int seaLevel, final long seed, final TernarySurfaceConfig surfaceBlocks) {
        final int integer15 = seaLevel + 1;
        final int integer16 = x & 0xF;
        final int integer17 = z & 0xF;
        final double double18 = 0.03125;
        final boolean boolean20 = this.noise.sample(x * 0.03125, z * 0.03125, 0.0) + random.nextDouble() * 0.2 > 0.0;
        final boolean boolean21 = this.noise.sample(x * 0.03125, 109.0, z * 0.03125) + random.nextDouble() * 0.2 > 0.0;
        final int integer18 = (int)(noise / 3.0 + 3.0 + random.nextDouble() * 0.25);
        final BlockPos.Mutable mutable23 = new BlockPos.Mutable();
        int integer19 = -1;
        BlockState blockState25 = NetherSurfaceBuilder.NETHERRACK;
        BlockState blockState26 = NetherSurfaceBuilder.NETHERRACK;
        for (int integer20 = 127; integer20 >= 0; --integer20) {
            mutable23.set(integer16, integer20, integer17);
            final BlockState blockState27 = chunk.getBlockState(mutable23);
            if (blockState27.getBlock() == null || blockState27.isAir()) {
                integer19 = -1;
            }
            else if (blockState27.getBlock() == defaultBlock.getBlock()) {
                if (integer19 == -1) {
                    if (integer18 <= 0) {
                        blockState25 = NetherSurfaceBuilder.CAVE_AIR;
                        blockState26 = NetherSurfaceBuilder.NETHERRACK;
                    }
                    else if (integer20 >= integer15 - 4 && integer20 <= integer15 + 1) {
                        blockState25 = NetherSurfaceBuilder.NETHERRACK;
                        blockState26 = NetherSurfaceBuilder.NETHERRACK;
                        if (boolean21) {
                            blockState25 = NetherSurfaceBuilder.GRAVEL;
                            blockState26 = NetherSurfaceBuilder.NETHERRACK;
                        }
                        if (boolean20) {
                            blockState25 = NetherSurfaceBuilder.GLOWSTONE;
                            blockState26 = NetherSurfaceBuilder.GLOWSTONE;
                        }
                    }
                    if (integer20 < integer15 && (blockState25 == null || blockState25.isAir())) {
                        blockState25 = defaultFluid;
                    }
                    integer19 = integer18;
                    if (integer20 >= integer15 - 1) {
                        chunk.setBlockState(mutable23, blockState25, false);
                    }
                    else {
                        chunk.setBlockState(mutable23, blockState26, false);
                    }
                }
                else if (integer19 > 0) {
                    --integer19;
                    chunk.setBlockState(mutable23, blockState26, false);
                }
            }
        }
    }
    
    @Override
    public void initSeed(final long seed) {
        if (this.seed != seed || this.noise == null) {
            this.noise = new OctavePerlinNoiseSampler(new ChunkRandom(seed), 4);
        }
        this.seed = seed;
    }
    
    static {
        CAVE_AIR = Blocks.kT.getDefaultState();
        NETHERRACK = Blocks.cJ.getDefaultState();
        GRAVEL = Blocks.E.getDefaultState();
        GLOWSTONE = Blocks.cK.getDefaultState();
    }
}
