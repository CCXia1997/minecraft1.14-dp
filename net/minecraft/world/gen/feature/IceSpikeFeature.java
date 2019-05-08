package net.minecraft.world.gen.feature;

import net.minecraft.block.BlockState;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.block.Block;
import net.minecraft.util.math.MathHelper;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class IceSpikeFeature extends Feature<DefaultFeatureConfig>
{
    public IceSpikeFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, BlockPos pos, final DefaultFeatureConfig config) {
        while (world.isAir(pos) && pos.getY() > 2) {
            pos = pos.down();
        }
        if (world.getBlockState(pos).getBlock() != Blocks.cC) {
            return false;
        }
        pos = pos.up(random.nextInt(4));
        final int integer6 = random.nextInt(4) + 7;
        final int integer7 = integer6 / 4 + random.nextInt(2);
        if (integer7 > 1 && random.nextInt(60) == 0) {
            pos = pos.up(10 + random.nextInt(30));
        }
        for (int integer8 = 0; integer8 < integer6; ++integer8) {
            final float float9 = (1.0f - integer8 / (float)integer6) * integer7;
            for (int integer9 = MathHelper.ceil(float9), integer10 = -integer9; integer10 <= integer9; ++integer10) {
                final float float10 = MathHelper.abs(integer10) - 0.25f;
                for (int integer11 = -integer9; integer11 <= integer9; ++integer11) {
                    final float float11 = MathHelper.abs(integer11) - 0.25f;
                    if ((integer10 == 0 && integer11 == 0) || float10 * float10 + float11 * float11 <= float9 * float9) {
                        if ((integer10 != -integer9 && integer10 != integer9 && integer11 != -integer9 && integer11 != integer9) || random.nextFloat() <= 0.75f) {
                            BlockState blockState15 = world.getBlockState(pos.add(integer10, integer8, integer11));
                            Block block16 = blockState15.getBlock();
                            if (blockState15.isAir() || Block.isNaturalDirt(block16) || block16 == Blocks.cC || block16 == Blocks.cB) {
                                this.setBlockState(world, pos.add(integer10, integer8, integer11), Blocks.gL.getDefaultState());
                            }
                            if (integer8 != 0 && integer9 > 1) {
                                blockState15 = world.getBlockState(pos.add(integer10, -integer8, integer11));
                                block16 = blockState15.getBlock();
                                if (blockState15.isAir() || Block.isNaturalDirt(block16) || block16 == Blocks.cC || block16 == Blocks.cB) {
                                    this.setBlockState(world, pos.add(integer10, -integer8, integer11), Blocks.gL.getDefaultState());
                                }
                            }
                        }
                    }
                }
            }
        }
        int integer8 = integer7 - 1;
        if (integer8 < 0) {
            integer8 = 0;
        }
        else if (integer8 > 1) {
            integer8 = 1;
        }
        for (int integer12 = -integer8; integer12 <= integer8; ++integer12) {
            for (int integer9 = -integer8; integer9 <= integer8; ++integer9) {
                BlockPos blockPos11 = pos.add(integer12, -1, integer9);
                int integer13 = 50;
                if (Math.abs(integer12) == 1 && Math.abs(integer9) == 1) {
                    integer13 = random.nextInt(5);
                }
                while (blockPos11.getY() > 50) {
                    final BlockState blockState16 = world.getBlockState(blockPos11);
                    final Block block17 = blockState16.getBlock();
                    if (!blockState16.isAir() && !Block.isNaturalDirt(block17) && block17 != Blocks.cC && block17 != Blocks.cB && block17 != Blocks.gL) {
                        break;
                    }
                    this.setBlockState(world, blockPos11, Blocks.gL.getDefaultState());
                    blockPos11 = blockPos11.down();
                    if (--integer13 > 0) {
                        continue;
                    }
                    blockPos11 = blockPos11.down(random.nextInt(5) + 1);
                    integer13 = random.nextInt(5);
                }
            }
        }
        return true;
    }
}
