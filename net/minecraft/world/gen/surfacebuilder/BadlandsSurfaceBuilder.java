package net.minecraft.world.gen.surfacebuilder;

import java.util.Arrays;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.block.BlockState;

public class BadlandsSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig>
{
    private static final BlockState WHITE_TERACOTTA;
    private static final BlockState ORANGE_TERRACOTTA;
    private static final BlockState TERACOTTA;
    private static final BlockState YELLOW_TERACOTTA;
    private static final BlockState BROWN_TERACOTTA;
    private static final BlockState RED_TERACOTTA;
    private static final BlockState LIGHT_GRAY_TERACOTTA;
    protected BlockState[] layerBlocks;
    protected long seed;
    protected OctaveSimplexNoiseSampler c;
    protected OctaveSimplexNoiseSampler d;
    protected OctaveSimplexNoiseSampler e;
    
    public BadlandsSurfaceBuilder(final Function<Dynamic<?>, ? extends TernarySurfaceConfig> function) {
        super(function);
    }
    
    @Override
    public void generate(final Random random, final Chunk chunk, final Biome biome, final int x, final int z, final int worldHeight, final double noise, final BlockState defaultBlock, final BlockState defaultFluid, final int seaLevel, final long seed, final TernarySurfaceConfig surfaceBlocks) {
        final int integer15 = x & 0xF;
        final int integer16 = z & 0xF;
        BlockState blockState17 = BadlandsSurfaceBuilder.WHITE_TERACOTTA;
        BlockState blockState18 = biome.getSurfaceConfig().getUnderMaterial();
        final int integer17 = (int)(noise / 3.0 + 3.0 + random.nextDouble() * 0.25);
        final boolean boolean20 = Math.cos(noise / 3.0 * 3.141592653589793) > 0.0;
        int integer18 = -1;
        boolean boolean21 = false;
        int integer19 = 0;
        final BlockPos.Mutable mutable24 = new BlockPos.Mutable();
        for (int integer20 = worldHeight; integer20 >= 0; --integer20) {
            if (integer19 < 15) {
                mutable24.set(integer15, integer20, integer16);
                final BlockState blockState19 = chunk.getBlockState(mutable24);
                if (blockState19.isAir()) {
                    integer18 = -1;
                }
                else if (blockState19.getBlock() == defaultBlock.getBlock()) {
                    if (integer18 == -1) {
                        boolean21 = false;
                        if (integer17 <= 0) {
                            blockState17 = Blocks.AIR.getDefaultState();
                            blockState18 = defaultBlock;
                        }
                        else if (integer20 >= seaLevel - 4 && integer20 <= seaLevel + 1) {
                            blockState17 = BadlandsSurfaceBuilder.WHITE_TERACOTTA;
                            blockState18 = biome.getSurfaceConfig().getUnderMaterial();
                        }
                        if (integer20 < seaLevel && (blockState17 == null || blockState17.isAir())) {
                            blockState17 = defaultFluid;
                        }
                        integer18 = integer17 + Math.max(0, integer20 - seaLevel);
                        if (integer20 >= seaLevel - 1) {
                            if (integer20 > seaLevel + 3 + integer17) {
                                BlockState blockState20;
                                if (integer20 < 64 || integer20 > 127) {
                                    blockState20 = BadlandsSurfaceBuilder.ORANGE_TERRACOTTA;
                                }
                                else if (boolean20) {
                                    blockState20 = BadlandsSurfaceBuilder.TERACOTTA;
                                }
                                else {
                                    blockState20 = this.a(x, integer20, z);
                                }
                                chunk.setBlockState(mutable24, blockState20, false);
                            }
                            else {
                                chunk.setBlockState(mutable24, biome.getSurfaceConfig().getTopMaterial(), false);
                                boolean21 = true;
                            }
                        }
                        else {
                            chunk.setBlockState(mutable24, blockState18, false);
                            final Block block27 = blockState18.getBlock();
                            if (block27 == Blocks.fx || block27 == Blocks.fy || block27 == Blocks.fz || block27 == Blocks.fA || block27 == Blocks.fB || block27 == Blocks.fC || block27 == Blocks.fD || block27 == Blocks.fE || block27 == Blocks.fF || block27 == Blocks.fG || block27 == Blocks.fH || block27 == Blocks.fI || block27 == Blocks.fJ || block27 == Blocks.fK || block27 == Blocks.fL || block27 == Blocks.fM) {
                                chunk.setBlockState(mutable24, BadlandsSurfaceBuilder.ORANGE_TERRACOTTA, false);
                            }
                        }
                    }
                    else if (integer18 > 0) {
                        --integer18;
                        if (boolean21) {
                            chunk.setBlockState(mutable24, BadlandsSurfaceBuilder.ORANGE_TERRACOTTA, false);
                        }
                        else {
                            chunk.setBlockState(mutable24, this.a(x, integer20, z), false);
                        }
                    }
                    ++integer19;
                }
            }
        }
    }
    
    @Override
    public void initSeed(final long seed) {
        if (this.seed != seed || this.layerBlocks == null) {
            this.initLayerBlocks(seed);
        }
        if (this.seed != seed || this.c == null || this.d == null) {
            final Random random3 = new ChunkRandom(seed);
            this.c = new OctaveSimplexNoiseSampler(random3, 4);
            this.d = new OctaveSimplexNoiseSampler(random3, 1);
        }
        this.seed = seed;
    }
    
    protected void initLayerBlocks(final long seed) {
        Arrays.fill(this.layerBlocks = new BlockState[64], BadlandsSurfaceBuilder.TERACOTTA);
        final Random random3 = new ChunkRandom(seed);
        this.e = new OctaveSimplexNoiseSampler(random3, 1);
        for (int integer4 = 0; integer4 < 64; ++integer4) {
            integer4 += random3.nextInt(5) + 1;
            if (integer4 < 64) {
                this.layerBlocks[integer4] = BadlandsSurfaceBuilder.ORANGE_TERRACOTTA;
            }
        }
        for (int integer4 = random3.nextInt(4) + 2, integer5 = 0; integer5 < integer4; ++integer5) {
            for (int integer6 = random3.nextInt(3) + 1, integer7 = random3.nextInt(64), integer8 = 0; integer7 + integer8 < 64 && integer8 < integer6; ++integer8) {
                this.layerBlocks[integer7 + integer8] = BadlandsSurfaceBuilder.YELLOW_TERACOTTA;
            }
        }
        for (int integer5 = random3.nextInt(4) + 2, integer6 = 0; integer6 < integer5; ++integer6) {
            for (int integer7 = random3.nextInt(3) + 2, integer8 = random3.nextInt(64), integer9 = 0; integer8 + integer9 < 64 && integer9 < integer7; ++integer9) {
                this.layerBlocks[integer8 + integer9] = BadlandsSurfaceBuilder.BROWN_TERACOTTA;
            }
        }
        for (int integer6 = random3.nextInt(4) + 2, integer7 = 0; integer7 < integer6; ++integer7) {
            for (int integer8 = random3.nextInt(3) + 1, integer9 = random3.nextInt(64), integer10 = 0; integer9 + integer10 < 64 && integer10 < integer8; ++integer10) {
                this.layerBlocks[integer9 + integer10] = BadlandsSurfaceBuilder.RED_TERACOTTA;
            }
        }
        int integer7 = random3.nextInt(3) + 3;
        int integer8 = 0;
        for (int integer9 = 0; integer9 < integer7; ++integer9) {
            final int integer10 = 1;
            integer8 += random3.nextInt(16) + 4;
            for (int integer11 = 0; integer8 + integer11 < 64 && integer11 < 1; ++integer11) {
                this.layerBlocks[integer8 + integer11] = BadlandsSurfaceBuilder.WHITE_TERACOTTA;
                if (integer8 + integer11 > 1 && random3.nextBoolean()) {
                    this.layerBlocks[integer8 + integer11 - 1] = BadlandsSurfaceBuilder.LIGHT_GRAY_TERACOTTA;
                }
                if (integer8 + integer11 < 63 && random3.nextBoolean()) {
                    this.layerBlocks[integer8 + integer11 + 1] = BadlandsSurfaceBuilder.LIGHT_GRAY_TERACOTTA;
                }
            }
        }
    }
    
    protected BlockState a(final int integer1, final int integer2, final int integer3) {
        final int integer4 = (int)Math.round(this.e.sample(integer1 / 512.0, integer3 / 512.0) * 2.0);
        return this.layerBlocks[(integer2 + integer4 + 64) % 64];
    }
    
    static {
        WHITE_TERACOTTA = Blocks.fx.getDefaultState();
        ORANGE_TERRACOTTA = Blocks.fy.getDefaultState();
        TERACOTTA = Blocks.gJ.getDefaultState();
        YELLOW_TERACOTTA = Blocks.fB.getDefaultState();
        BROWN_TERACOTTA = Blocks.fJ.getDefaultState();
        RED_TERACOTTA = Blocks.fL.getDefaultState();
        LIGHT_GRAY_TERACOTTA = Blocks.fF.getDefaultState();
    }
}
