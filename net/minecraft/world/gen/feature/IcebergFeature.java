package net.minecraft.world.gen.feature;

import net.minecraft.world.BlockView;
import net.minecraft.block.Material;
import net.minecraft.block.Block;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class IcebergFeature extends Feature<IcebergFeatureConfig>
{
    public IcebergFeature(final Function<Dynamic<?>, ? extends IcebergFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, BlockPos pos, final IcebergFeatureConfig config) {
        pos = new BlockPos(pos.getX(), world.getSeaLevel(), pos.getZ());
        final boolean boolean6 = random.nextDouble() > 0.7;
        final BlockState blockState7 = config.state;
        final double double8 = random.nextDouble() * 2.0 * 3.141592653589793;
        final int integer10 = 11 - random.nextInt(5);
        final int integer11 = 3 + random.nextInt(3);
        final boolean boolean7 = random.nextDouble() > 0.7;
        final int integer12 = 11;
        int integer13 = boolean7 ? (random.nextInt(6) + 6) : (random.nextInt(15) + 3);
        if (!boolean7 && random.nextDouble() > 0.9) {
            integer13 += random.nextInt(19) + 7;
        }
        final int integer14 = Math.min(integer13 + random.nextInt(11), 18);
        final int integer15 = Math.min(integer13 + random.nextInt(7) - random.nextInt(5), 11);
        final int integer16 = boolean7 ? integer10 : 11;
        for (int integer17 = -integer16; integer17 < integer16; ++integer17) {
            for (int integer18 = -integer16; integer18 < integer16; ++integer18) {
                for (int integer19 = 0; integer19 < integer13; ++integer19) {
                    final int integer20 = boolean7 ? this.b(integer19, integer13, integer15) : this.a(random, integer19, integer13, integer15);
                    if (boolean7 || integer17 < integer20) {
                        this.a(world, random, pos, integer13, integer17, integer19, integer18, integer20, integer16, boolean7, integer11, double8, boolean6, blockState7);
                    }
                }
            }
        }
        this.a(world, pos, integer15, integer13, boolean7, integer10);
        for (int integer17 = -integer16; integer17 < integer16; ++integer17) {
            for (int integer18 = -integer16; integer18 < integer16; ++integer18) {
                for (int integer19 = -1; integer19 > -integer14; --integer19) {
                    final int integer20 = boolean7 ? MathHelper.ceil(integer16 * (1.0f - (float)Math.pow(integer19, 2.0) / (integer14 * 8.0f))) : integer16;
                    final int integer21 = this.b(random, -integer19, integer14, integer15);
                    if (integer17 < integer21) {
                        this.a(world, random, pos, integer14, integer17, integer19, integer18, integer21, integer20, boolean7, integer11, double8, boolean6, blockState7);
                    }
                }
            }
        }
        final boolean boolean8 = boolean7 ? (random.nextDouble() > 0.1) : (random.nextDouble() > 0.7);
        if (boolean8) {
            this.a(random, world, integer15, integer13, pos, boolean7, integer10, double8, integer11);
        }
        return true;
    }
    
    private void a(final Random random, final IWorld iWorld, final int integer3, final int integer4, final BlockPos blockPos, final boolean boolean6, final int integer7, final double double8, final int integer10) {
        final int integer11 = random.nextBoolean() ? -1 : 1;
        final int integer12 = random.nextBoolean() ? -1 : 1;
        int integer13 = random.nextInt(Math.max(integer3 / 2 - 2, 1));
        if (random.nextBoolean()) {
            integer13 = integer3 / 2 + 1 - random.nextInt(Math.max(integer3 - integer3 / 2 - 1, 1));
        }
        int integer14 = random.nextInt(Math.max(integer3 / 2 - 2, 1));
        if (random.nextBoolean()) {
            integer14 = integer3 / 2 + 1 - random.nextInt(Math.max(integer3 - integer3 / 2 - 1, 1));
        }
        if (boolean6) {
            integer14 = (integer13 = random.nextInt(Math.max(integer7 - 5, 1)));
        }
        final BlockPos blockPos2 = new BlockPos(integer11 * integer13, 0, integer12 * integer14);
        final double double9 = boolean6 ? (double8 + 1.5707963267948966) : (random.nextDouble() * 2.0 * 3.141592653589793);
        for (int integer15 = 0; integer15 < integer4 - 3; ++integer15) {
            final int integer16 = this.a(random, integer15, integer4, integer3);
            this.a(integer16, integer15, blockPos, iWorld, false, double9, blockPos2, integer7, integer10);
        }
        for (int integer15 = -1; integer15 > -integer4 + random.nextInt(5); --integer15) {
            final int integer16 = this.b(random, -integer15, integer4, integer3);
            this.a(integer16, integer15, blockPos, iWorld, true, double9, blockPos2, integer7, integer10);
        }
    }
    
    private void a(final int integer1, final int integer2, final BlockPos blockPos3, final IWorld iWorld, final boolean boolean5, final double double6, final BlockPos blockPos8, final int integer9, final int integer10) {
        final int integer11 = integer1 + 1 + integer9 / 3;
        final int integer12 = Math.min(integer1 - 3, 3) + integer10 / 2 - 1;
        for (int integer13 = -integer11; integer13 < integer11; ++integer13) {
            for (int integer14 = -integer11; integer14 < integer11; ++integer14) {
                final double double7 = this.a(integer13, integer14, blockPos8, integer11, integer12, double6);
                if (double7 < 0.0) {
                    final BlockPos blockPos9 = blockPos3.add(integer13, integer2, integer14);
                    final Block block18 = iWorld.getBlockState(blockPos9).getBlock();
                    if (this.a(block18) || block18 == Blocks.cC) {
                        if (boolean5) {
                            this.setBlockState(iWorld, blockPos9, Blocks.A.getDefaultState());
                        }
                        else {
                            this.setBlockState(iWorld, blockPos9, Blocks.AIR.getDefaultState());
                            this.a(iWorld, blockPos9);
                        }
                    }
                }
            }
        }
    }
    
    private void a(final IWorld iWorld, final BlockPos blockPos) {
        if (iWorld.getBlockState(blockPos.up()).getBlock() == Blocks.cA) {
            this.setBlockState(iWorld, blockPos.up(), Blocks.AIR.getDefaultState());
        }
    }
    
    private void a(final IWorld iWorld, final Random random, final BlockPos blockPos, final int integer4, final int integer5, final int integer6, final int integer7, final int integer8, final int integer9, final boolean boolean10, final int integer11, final double double12, final boolean boolean14, final BlockState blockState15) {
        final double double13 = boolean10 ? this.a(integer5, integer7, BlockPos.ORIGIN, integer9, this.a(integer6, integer4, integer11), double12) : this.a(integer5, integer7, BlockPos.ORIGIN, integer8, random);
        if (double13 < 0.0) {
            final BlockPos blockPos2 = blockPos.add(integer5, integer6, integer7);
            final double double14 = boolean10 ? -0.5 : (-6 - random.nextInt(3));
            if (double13 > double14 && random.nextDouble() > 0.9) {
                return;
            }
            this.a(blockPos2, iWorld, random, integer4 - integer6, integer4, boolean10, boolean14, blockState15);
        }
    }
    
    private void a(final BlockPos blockPos, final IWorld iWorld, final Random random, final int integer4, final int integer5, final boolean boolean6, final boolean boolean7, final BlockState blockState) {
        final BlockState blockState2 = iWorld.getBlockState(blockPos);
        final Block block10 = blockState2.getBlock();
        if (blockState2.getMaterial() == Material.AIR || block10 == Blocks.cC || block10 == Blocks.cB || block10 == Blocks.A) {
            final boolean boolean8 = !boolean6 || random.nextDouble() > 0.05;
            final int integer6 = boolean6 ? 3 : 2;
            if (boolean7 && block10 != Blocks.A && integer4 <= random.nextInt(Math.max(1, integer5 / integer6)) + integer5 * 0.6 && boolean8) {
                this.setBlockState(iWorld, blockPos, Blocks.cC.getDefaultState());
            }
            else {
                this.setBlockState(iWorld, blockPos, blockState);
            }
        }
    }
    
    private int a(final int integer1, final int integer2, final int integer3) {
        int integer4 = integer3;
        if (integer1 > 0 && integer2 - integer1 <= 3) {
            integer4 -= 4 - (integer2 - integer1);
        }
        return integer4;
    }
    
    private double a(final int integer1, final int integer2, final BlockPos blockPos, final int integer4, final Random random) {
        final float float6 = 10.0f * MathHelper.clamp(random.nextFloat(), 0.2f, 0.8f) / integer4;
        return float6 + Math.pow(integer1 - blockPos.getX(), 2.0) + Math.pow(integer2 - blockPos.getZ(), 2.0) - Math.pow(integer4, 2.0);
    }
    
    private double a(final int integer1, final int integer2, final BlockPos blockPos, final int integer4, final int integer5, final double double6) {
        return Math.pow(((integer1 - blockPos.getX()) * Math.cos(double6) - (integer2 - blockPos.getZ()) * Math.sin(double6)) / integer4, 2.0) + Math.pow(((integer1 - blockPos.getX()) * Math.sin(double6) + (integer2 - blockPos.getZ()) * Math.cos(double6)) / integer5, 2.0) - 1.0;
    }
    
    private int a(final Random random, final int integer2, final int integer3, final int integer4) {
        final float float5 = 3.5f - random.nextFloat();
        float float6 = (1.0f - (float)Math.pow(integer2, 2.0) / (integer3 * float5)) * integer4;
        if (integer3 > 15 + random.nextInt(5)) {
            final int integer5 = (integer2 < 3 + random.nextInt(6)) ? (integer2 / 2) : integer2;
            float6 = (1.0f - integer5 / (integer3 * float5 * 0.4f)) * integer4;
        }
        return MathHelper.ceil(float6 / 2.0f);
    }
    
    private int b(final int integer1, final int integer2, final int integer3) {
        final float float4 = 1.0f;
        final float float5 = (1.0f - (float)Math.pow(integer1, 2.0) / (integer2 * 1.0f)) * integer3;
        return MathHelper.ceil(float5 / 2.0f);
    }
    
    private int b(final Random random, final int integer2, final int integer3, final int integer4) {
        final float float5 = 1.0f + random.nextFloat() / 2.0f;
        final float float6 = (1.0f - integer2 / (integer3 * float5)) * integer4;
        return MathHelper.ceil(float6 / 2.0f);
    }
    
    private boolean a(final Block block) {
        return block == Blocks.gL || block == Blocks.cC || block == Blocks.kN;
    }
    
    private boolean a(final BlockView blockView, final BlockPos blockPos) {
        return blockView.getBlockState(blockPos.down()).getMaterial() == Material.AIR;
    }
    
    private void a(final IWorld iWorld, final BlockPos blockPos, final int integer3, final int integer4, final boolean boolean5, final int integer6) {
        for (int integer7 = boolean5 ? integer6 : (integer3 / 2), integer8 = -integer7; integer8 <= integer7; ++integer8) {
            for (int integer9 = -integer7; integer9 <= integer7; ++integer9) {
                for (int integer10 = 0; integer10 <= integer4; ++integer10) {
                    final BlockPos blockPos2 = blockPos.add(integer8, integer10, integer9);
                    final Block block12 = iWorld.getBlockState(blockPos2).getBlock();
                    if (this.a(block12) || block12 == Blocks.cA) {
                        if (this.a((BlockView)iWorld, blockPos2)) {
                            this.setBlockState(iWorld, blockPos2, Blocks.AIR.getDefaultState());
                            this.setBlockState(iWorld, blockPos2.up(), Blocks.AIR.getDefaultState());
                        }
                        else if (this.a(block12)) {
                            final Block[] arr13 = { iWorld.getBlockState(blockPos2.west()).getBlock(), iWorld.getBlockState(blockPos2.east()).getBlock(), iWorld.getBlockState(blockPos2.north()).getBlock(), iWorld.getBlockState(blockPos2.south()).getBlock() };
                            int integer11 = 0;
                            for (final Block block13 : arr13) {
                                if (!this.a(block13)) {
                                    ++integer11;
                                }
                            }
                            if (integer11 >= 3) {
                                this.setBlockState(iWorld, blockPos2, Blocks.AIR.getDefaultState());
                            }
                        }
                    }
                }
            }
        }
    }
}
