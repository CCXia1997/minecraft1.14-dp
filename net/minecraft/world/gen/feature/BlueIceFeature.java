package net.minecraft.world.gen.feature;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.util.math.Direction;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class BlueIceFeature extends Feature<DefaultFeatureConfig>
{
    public BlueIceFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final DefaultFeatureConfig config) {
        if (pos.getY() > world.getSeaLevel() - 1) {
            return false;
        }
        if (world.getBlockState(pos).getBlock() != Blocks.A && world.getBlockState(pos.down()).getBlock() != Blocks.A) {
            return false;
        }
        boolean boolean6 = false;
        for (final Direction direction10 : Direction.values()) {
            if (direction10 != Direction.DOWN) {
                if (world.getBlockState(pos.offset(direction10)).getBlock() == Blocks.gL) {
                    boolean6 = true;
                    break;
                }
            }
        }
        if (!boolean6) {
            return false;
        }
        world.setBlockState(pos, Blocks.kN.getDefaultState(), 2);
        for (int integer7 = 0; integer7 < 200; ++integer7) {
            final int integer8 = random.nextInt(5) - random.nextInt(6);
            int integer9 = 3;
            if (integer8 < 2) {
                integer9 += integer8 / 2;
            }
            if (integer9 >= 1) {
                final BlockPos blockPos10 = pos.add(random.nextInt(integer9) - random.nextInt(integer9), integer8, random.nextInt(integer9) - random.nextInt(integer9));
                final BlockState blockState11 = world.getBlockState(blockPos10);
                final Block block12 = blockState11.getBlock();
                if (blockState11.getMaterial() == Material.AIR || block12 == Blocks.A || block12 == Blocks.gL || block12 == Blocks.cB) {
                    for (final Direction direction11 : Direction.values()) {
                        final Block block13 = world.getBlockState(blockPos10.offset(direction11)).getBlock();
                        if (block13 == Blocks.kN) {
                            world.setBlockState(blockPos10, Blocks.kN.getDefaultState(), 2);
                            break;
                        }
                    }
                }
            }
        }
        return true;
    }
}
