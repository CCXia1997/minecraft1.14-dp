package net.minecraft.world.gen.feature;

import java.util.BitSet;
import net.minecraft.world.Heightmap;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class OreFeature extends Feature<OreFeatureConfig>
{
    public OreFeature(final Function<Dynamic<?>, ? extends OreFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final OreFeatureConfig config) {
        final float float6 = random.nextFloat() * 3.1415927f;
        final float float7 = config.size / 8.0f;
        final int integer8 = MathHelper.ceil((config.size / 16.0f * 2.0f + 1.0f) / 2.0f);
        final double double9 = pos.getX() + MathHelper.sin(float6) * float7;
        final double double10 = pos.getX() - MathHelper.sin(float6) * float7;
        final double double11 = pos.getZ() + MathHelper.cos(float6) * float7;
        final double double12 = pos.getZ() - MathHelper.cos(float6) * float7;
        final int integer9 = 2;
        final double double13 = pos.getY() + random.nextInt(3) - 2;
        final double double14 = pos.getY() + random.nextInt(3) - 2;
        final int integer10 = pos.getX() - MathHelper.ceil(float7) - integer8;
        final int integer11 = pos.getY() - 2 - integer8;
        final int integer12 = pos.getZ() - MathHelper.ceil(float7) - integer8;
        final int integer13 = 2 * (MathHelper.ceil(float7) + integer8);
        final int integer14 = 2 * (2 + integer8);
        for (int integer15 = integer10; integer15 <= integer10 + integer13; ++integer15) {
            for (int integer16 = integer12; integer16 <= integer12 + integer13; ++integer16) {
                if (integer11 <= world.getTop(Heightmap.Type.c, integer15, integer16)) {
                    return this.generateVeinPart(world, random, config, double9, double10, double11, double12, double13, double14, integer10, integer11, integer12, integer13, integer14);
                }
            }
        }
        return false;
    }
    
    protected boolean generateVeinPart(final IWorld world, final Random random, final OreFeatureConfig config, final double startX, final double endX, final double startZ, final double endZ, final double startY, final double endY, final int x, final int y, final int z, final int size, final int integer20) {
        int integer21 = 0;
        final BitSet bitSet22 = new BitSet(size * integer20 * size);
        final BlockPos.Mutable mutable23 = new BlockPos.Mutable();
        final double[] arr24 = new double[config.size * 4];
        for (int integer22 = 0; integer22 < config.size; ++integer22) {
            final float float26 = integer22 / (float)config.size;
            final double double27 = MathHelper.lerp(float26, startX, endX);
            final double double28 = MathHelper.lerp(float26, startY, endY);
            final double double29 = MathHelper.lerp(float26, startZ, endZ);
            final double double30 = random.nextDouble() * config.size / 16.0;
            final double double31 = ((MathHelper.sin(3.1415927f * float26) + 1.0f) * double30 + 1.0) / 2.0;
            arr24[integer22 * 4 + 0] = double27;
            arr24[integer22 * 4 + 1] = double28;
            arr24[integer22 * 4 + 2] = double29;
            arr24[integer22 * 4 + 3] = double31;
        }
        for (int integer22 = 0; integer22 < config.size - 1; ++integer22) {
            if (arr24[integer22 * 4 + 3] > 0.0) {
                for (int integer23 = integer22 + 1; integer23 < config.size; ++integer23) {
                    if (arr24[integer23 * 4 + 3] > 0.0) {
                        final double double27 = arr24[integer22 * 4 + 0] - arr24[integer23 * 4 + 0];
                        final double double28 = arr24[integer22 * 4 + 1] - arr24[integer23 * 4 + 1];
                        final double double29 = arr24[integer22 * 4 + 2] - arr24[integer23 * 4 + 2];
                        final double double30 = arr24[integer22 * 4 + 3] - arr24[integer23 * 4 + 3];
                        if (double30 * double30 > double27 * double27 + double28 * double28 + double29 * double29) {
                            if (double30 > 0.0) {
                                arr24[integer23 * 4 + 3] = -1.0;
                            }
                            else {
                                arr24[integer22 * 4 + 3] = -1.0;
                            }
                        }
                    }
                }
            }
        }
        for (int integer22 = 0; integer22 < config.size; ++integer22) {
            final double double32 = arr24[integer22 * 4 + 3];
            if (double32 >= 0.0) {
                final double double33 = arr24[integer22 * 4 + 0];
                final double double34 = arr24[integer22 * 4 + 1];
                final double double35 = arr24[integer22 * 4 + 2];
                final int integer24 = Math.max(MathHelper.floor(double33 - double32), x);
                final int integer25 = Math.max(MathHelper.floor(double34 - double32), y);
                final int integer26 = Math.max(MathHelper.floor(double35 - double32), z);
                final int integer27 = Math.max(MathHelper.floor(double33 + double32), integer24);
                final int integer28 = Math.max(MathHelper.floor(double34 + double32), integer25);
                final int integer29 = Math.max(MathHelper.floor(double35 + double32), integer26);
                for (int integer30 = integer24; integer30 <= integer27; ++integer30) {
                    final double double36 = (integer30 + 0.5 - double33) / double32;
                    if (double36 * double36 < 1.0) {
                        for (int integer31 = integer25; integer31 <= integer28; ++integer31) {
                            final double double37 = (integer31 + 0.5 - double34) / double32;
                            if (double36 * double36 + double37 * double37 < 1.0) {
                                for (int integer32 = integer26; integer32 <= integer29; ++integer32) {
                                    final double double38 = (integer32 + 0.5 - double35) / double32;
                                    if (double36 * double36 + double37 * double37 + double38 * double38 < 1.0) {
                                        final int integer33 = integer30 - x + (integer31 - y) * size + (integer32 - z) * size * integer20;
                                        if (!bitSet22.get(integer33)) {
                                            bitSet22.set(integer33);
                                            mutable23.set(integer30, integer31, integer32);
                                            if (config.target.getCondition().test(world.getBlockState(mutable23))) {
                                                world.setBlockState(mutable23, config.state, 2);
                                                ++integer21;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return integer21 > 0;
    }
}
