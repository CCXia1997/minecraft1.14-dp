package net.minecraft.world.gen.surfacebuilder;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.BlockState;

public class WoodedBadlandsSurfaceBuilder extends BadlandsSurfaceBuilder
{
    private static final BlockState WHITE_TERRACOTTA;
    private static final BlockState ORANGE_TERRACOTTA;
    private static final BlockState TERRACOTTA;
    
    public WoodedBadlandsSurfaceBuilder(final Function<Dynamic<?>, ? extends TernarySurfaceConfig> function) {
        super(function);
    }
    
    @Override
    public void generate(final Random random, final Chunk chunk, final Biome biome, final int x, final int z, final int worldHeight, final double noise, final BlockState defaultBlock, final BlockState defaultFluid, final int seaLevel, final long seed, final TernarySurfaceConfig surfaceBlocks) {
        final int integer15 = x & 0xF;
        final int integer16 = z & 0xF;
        BlockState blockState17 = WoodedBadlandsSurfaceBuilder.WHITE_TERRACOTTA;
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
                            blockState17 = WoodedBadlandsSurfaceBuilder.WHITE_TERRACOTTA;
                            blockState18 = biome.getSurfaceConfig().getUnderMaterial();
                        }
                        if (integer20 < seaLevel && (blockState17 == null || blockState17.isAir())) {
                            blockState17 = defaultFluid;
                        }
                        integer18 = integer17 + Math.max(0, integer20 - seaLevel);
                        if (integer20 >= seaLevel - 1) {
                            if (integer20 > 86 + integer17 * 2) {
                                if (boolean20) {
                                    chunk.setBlockState(mutable24, Blocks.k.getDefaultState(), false);
                                }
                                else {
                                    chunk.setBlockState(mutable24, Blocks.i.getDefaultState(), false);
                                }
                            }
                            else if (integer20 > seaLevel + 3 + integer17) {
                                BlockState blockState20;
                                if (integer20 < 64 || integer20 > 127) {
                                    blockState20 = WoodedBadlandsSurfaceBuilder.ORANGE_TERRACOTTA;
                                }
                                else if (boolean20) {
                                    blockState20 = WoodedBadlandsSurfaceBuilder.TERRACOTTA;
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
                            if (blockState18 == WoodedBadlandsSurfaceBuilder.WHITE_TERRACOTTA) {
                                chunk.setBlockState(mutable24, WoodedBadlandsSurfaceBuilder.ORANGE_TERRACOTTA, false);
                            }
                        }
                    }
                    else if (integer18 > 0) {
                        --integer18;
                        if (boolean21) {
                            chunk.setBlockState(mutable24, WoodedBadlandsSurfaceBuilder.ORANGE_TERRACOTTA, false);
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
    
    static {
        WHITE_TERRACOTTA = Blocks.fx.getDefaultState();
        ORANGE_TERRACOTTA = Blocks.fy.getDefaultState();
        TERRACOTTA = Blocks.gJ.getDefaultState();
    }
}
