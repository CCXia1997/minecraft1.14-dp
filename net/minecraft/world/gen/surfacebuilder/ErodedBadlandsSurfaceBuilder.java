package net.minecraft.world.gen.surfacebuilder;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.BlockState;

public class ErodedBadlandsSurfaceBuilder extends BadlandsSurfaceBuilder
{
    private static final BlockState WHITE_TERRACOTTA;
    private static final BlockState ORANGE_TERRACOTTA;
    private static final BlockState TERACOTTA;
    
    public ErodedBadlandsSurfaceBuilder(final Function<Dynamic<?>, ? extends TernarySurfaceConfig> function) {
        super(function);
    }
    
    @Override
    public void generate(final Random random, final Chunk chunk, final Biome biome, final int x, final int z, final int worldHeight, final double noise, final BlockState defaultBlock, final BlockState defaultFluid, final int seaLevel, final long seed, final TernarySurfaceConfig surfaceBlocks) {
        double double15 = 0.0;
        final double double16 = Math.min(Math.abs(noise), this.c.sample(x * 0.25, z * 0.25));
        if (double16 > 0.0) {
            final double double17 = 0.001953125;
            final double double18 = Math.abs(this.d.sample(x * 0.001953125, z * 0.001953125));
            double15 = double16 * double16 * 2.5;
            final double double19 = Math.ceil(double18 * 50.0) + 14.0;
            if (double15 > double19) {
                double15 = double19;
            }
            double15 += 64.0;
        }
        final int integer19 = x & 0xF;
        final int integer20 = z & 0xF;
        BlockState blockState21 = ErodedBadlandsSurfaceBuilder.WHITE_TERRACOTTA;
        BlockState blockState22 = biome.getSurfaceConfig().getUnderMaterial();
        final int integer21 = (int)(noise / 3.0 + 3.0 + random.nextDouble() * 0.25);
        final boolean boolean24 = Math.cos(noise / 3.0 * 3.141592653589793) > 0.0;
        int integer22 = -1;
        boolean boolean25 = false;
        final BlockPos.Mutable mutable27 = new BlockPos.Mutable();
        for (int integer23 = Math.max(worldHeight, (int)double15 + 1); integer23 >= 0; --integer23) {
            mutable27.set(integer19, integer23, integer20);
            if (chunk.getBlockState(mutable27).isAir() && integer23 < (int)double15) {
                chunk.setBlockState(mutable27, defaultBlock, false);
            }
            final BlockState blockState23 = chunk.getBlockState(mutable27);
            if (blockState23.isAir()) {
                integer22 = -1;
            }
            else if (blockState23.getBlock() == defaultBlock.getBlock()) {
                if (integer22 == -1) {
                    boolean25 = false;
                    if (integer21 <= 0) {
                        blockState21 = Blocks.AIR.getDefaultState();
                        blockState22 = defaultBlock;
                    }
                    else if (integer23 >= seaLevel - 4 && integer23 <= seaLevel + 1) {
                        blockState21 = ErodedBadlandsSurfaceBuilder.WHITE_TERRACOTTA;
                        blockState22 = biome.getSurfaceConfig().getUnderMaterial();
                    }
                    if (integer23 < seaLevel && (blockState21 == null || blockState21.isAir())) {
                        blockState21 = defaultFluid;
                    }
                    integer22 = integer21 + Math.max(0, integer23 - seaLevel);
                    if (integer23 >= seaLevel - 1) {
                        if (integer23 > seaLevel + 3 + integer21) {
                            BlockState blockState24;
                            if (integer23 < 64 || integer23 > 127) {
                                blockState24 = ErodedBadlandsSurfaceBuilder.ORANGE_TERRACOTTA;
                            }
                            else if (boolean24) {
                                blockState24 = ErodedBadlandsSurfaceBuilder.TERACOTTA;
                            }
                            else {
                                blockState24 = this.a(x, integer23, z);
                            }
                            chunk.setBlockState(mutable27, blockState24, false);
                        }
                        else {
                            chunk.setBlockState(mutable27, biome.getSurfaceConfig().getTopMaterial(), false);
                            boolean25 = true;
                        }
                    }
                    else {
                        chunk.setBlockState(mutable27, blockState22, false);
                        final Block block30 = blockState22.getBlock();
                        if (block30 == Blocks.fx || block30 == Blocks.fy || block30 == Blocks.fz || block30 == Blocks.fA || block30 == Blocks.fB || block30 == Blocks.fC || block30 == Blocks.fD || block30 == Blocks.fE || block30 == Blocks.fF || block30 == Blocks.fG || block30 == Blocks.fH || block30 == Blocks.fI || block30 == Blocks.fJ || block30 == Blocks.fK || block30 == Blocks.fL || block30 == Blocks.fM) {
                            chunk.setBlockState(mutable27, ErodedBadlandsSurfaceBuilder.ORANGE_TERRACOTTA, false);
                        }
                    }
                }
                else if (integer22 > 0) {
                    --integer22;
                    if (boolean25) {
                        chunk.setBlockState(mutable27, ErodedBadlandsSurfaceBuilder.ORANGE_TERRACOTTA, false);
                    }
                    else {
                        chunk.setBlockState(mutable27, this.a(x, integer23, z), false);
                    }
                }
            }
        }
    }
    
    static {
        WHITE_TERRACOTTA = Blocks.fx.getDefaultState();
        ORANGE_TERRACOTTA = Blocks.fy.getDefaultState();
        TERACOTTA = Blocks.gJ.getDefaultState();
    }
}
