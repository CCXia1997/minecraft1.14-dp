package net.minecraft.world.gen.feature;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.ViewableWorld;
import net.minecraft.block.Material;
import net.minecraft.block.Blocks;
import net.minecraft.world.LightType;
import net.minecraft.block.Block;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.BlockState;

public class LakeFeature extends Feature<LakeFeatureConfig>
{
    private static final BlockState CAVE_AIR;
    
    public LakeFeature(final Function<Dynamic<?>, ? extends LakeFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, BlockPos pos, final LakeFeatureConfig config) {
        while (pos.getY() > 5 && world.isAir(pos)) {
            pos = pos.down();
        }
        if (pos.getY() <= 4) {
            return false;
        }
        pos = pos.down(4);
        final ChunkPos chunkPos6 = new ChunkPos(pos);
        if (!world.getChunk(chunkPos6.x, chunkPos6.z, ChunkStatus.STRUCTURE_REFERENCES).getStructureReferences(Feature.VILLAGE.getName()).isEmpty()) {
            return false;
        }
        final boolean[] arr7 = new boolean[2048];
        for (int integer8 = random.nextInt(4) + 4, integer9 = 0; integer9 < integer8; ++integer9) {
            final double double10 = random.nextDouble() * 6.0 + 3.0;
            final double double11 = random.nextDouble() * 4.0 + 2.0;
            final double double12 = random.nextDouble() * 6.0 + 3.0;
            final double double13 = random.nextDouble() * (16.0 - double10 - 2.0) + 1.0 + double10 / 2.0;
            final double double14 = random.nextDouble() * (8.0 - double11 - 4.0) + 2.0 + double11 / 2.0;
            final double double15 = random.nextDouble() * (16.0 - double12 - 2.0) + 1.0 + double12 / 2.0;
            for (int integer10 = 1; integer10 < 15; ++integer10) {
                for (int integer11 = 1; integer11 < 15; ++integer11) {
                    for (int integer12 = 1; integer12 < 7; ++integer12) {
                        final double double16 = (integer10 - double13) / (double10 / 2.0);
                        final double double17 = (integer12 - double14) / (double11 / 2.0);
                        final double double18 = (integer11 - double15) / (double12 / 2.0);
                        final double double19 = double16 * double16 + double17 * double17 + double18 * double18;
                        if (double19 < 1.0) {
                            arr7[(integer10 * 16 + integer11) * 8 + integer12] = true;
                        }
                    }
                }
            }
        }
        for (int integer9 = 0; integer9 < 16; ++integer9) {
            for (int integer13 = 0; integer13 < 16; ++integer13) {
                for (int integer14 = 0; integer14 < 8; ++integer14) {
                    final boolean boolean12 = !arr7[(integer9 * 16 + integer13) * 8 + integer14] && ((integer9 < 15 && arr7[((integer9 + 1) * 16 + integer13) * 8 + integer14]) || (integer9 > 0 && arr7[((integer9 - 1) * 16 + integer13) * 8 + integer14]) || (integer13 < 15 && arr7[(integer9 * 16 + integer13 + 1) * 8 + integer14]) || (integer13 > 0 && arr7[(integer9 * 16 + (integer13 - 1)) * 8 + integer14]) || (integer14 < 7 && arr7[(integer9 * 16 + integer13) * 8 + integer14 + 1]) || (integer14 > 0 && arr7[(integer9 * 16 + integer13) * 8 + (integer14 - 1)]));
                    if (boolean12) {
                        final Material material13 = world.getBlockState(pos.add(integer9, integer14, integer13)).getMaterial();
                        if (integer14 >= 4 && material13.isLiquid()) {
                            return false;
                        }
                        if (integer14 < 4 && !material13.isSolid() && world.getBlockState(pos.add(integer9, integer14, integer13)) != config.state) {
                            return false;
                        }
                    }
                }
            }
        }
        for (int integer9 = 0; integer9 < 16; ++integer9) {
            for (int integer13 = 0; integer13 < 16; ++integer13) {
                for (int integer14 = 0; integer14 < 8; ++integer14) {
                    if (arr7[(integer9 * 16 + integer13) * 8 + integer14]) {
                        world.setBlockState(pos.add(integer9, integer14, integer13), (integer14 >= 4) ? LakeFeature.CAVE_AIR : config.state, 2);
                    }
                }
            }
        }
        for (int integer9 = 0; integer9 < 16; ++integer9) {
            for (int integer13 = 0; integer13 < 16; ++integer13) {
                for (int integer14 = 4; integer14 < 8; ++integer14) {
                    if (arr7[(integer9 * 16 + integer13) * 8 + integer14]) {
                        final BlockPos blockPos12 = pos.add(integer9, integer14 - 1, integer13);
                        if (Block.isNaturalDirt(world.getBlockState(blockPos12).getBlock()) && world.getLightLevel(LightType.SKY, pos.add(integer9, integer14, integer13)) > 0) {
                            final Biome biome13 = world.getBiome(blockPos12);
                            if (biome13.getSurfaceConfig().getTopMaterial().getBlock() == Blocks.dL) {
                                world.setBlockState(blockPos12, Blocks.dL.getDefaultState(), 2);
                            }
                            else {
                                world.setBlockState(blockPos12, Blocks.i.getDefaultState(), 2);
                            }
                        }
                    }
                }
            }
        }
        if (config.state.getMaterial() == Material.LAVA) {
            for (int integer9 = 0; integer9 < 16; ++integer9) {
                for (int integer13 = 0; integer13 < 16; ++integer13) {
                    for (int integer14 = 0; integer14 < 8; ++integer14) {
                        final boolean boolean12 = !arr7[(integer9 * 16 + integer13) * 8 + integer14] && ((integer9 < 15 && arr7[((integer9 + 1) * 16 + integer13) * 8 + integer14]) || (integer9 > 0 && arr7[((integer9 - 1) * 16 + integer13) * 8 + integer14]) || (integer13 < 15 && arr7[(integer9 * 16 + integer13 + 1) * 8 + integer14]) || (integer13 > 0 && arr7[(integer9 * 16 + (integer13 - 1)) * 8 + integer14]) || (integer14 < 7 && arr7[(integer9 * 16 + integer13) * 8 + integer14 + 1]) || (integer14 > 0 && arr7[(integer9 * 16 + integer13) * 8 + (integer14 - 1)]));
                        if (boolean12 && (integer14 < 4 || random.nextInt(2) != 0) && world.getBlockState(pos.add(integer9, integer14, integer13)).getMaterial().isSolid()) {
                            world.setBlockState(pos.add(integer9, integer14, integer13), Blocks.b.getDefaultState(), 2);
                        }
                    }
                }
            }
        }
        if (config.state.getMaterial() == Material.WATER) {
            for (int integer9 = 0; integer9 < 16; ++integer9) {
                for (int integer13 = 0; integer13 < 16; ++integer13) {
                    final int integer14 = 4;
                    final BlockPos blockPos12 = pos.add(integer9, 4, integer13);
                    if (world.getBiome(blockPos12).canSetSnow(world, blockPos12, false)) {
                        world.setBlockState(blockPos12, Blocks.cB.getDefaultState(), 2);
                    }
                }
            }
        }
        return true;
    }
    
    static {
        CAVE_AIR = Blocks.kT.getDefaultState();
    }
}
