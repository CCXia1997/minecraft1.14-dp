package net.minecraft.world.gen.feature;

import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class PlainFlowerFeature extends FlowerFeature
{
    public PlainFlowerFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public BlockState getFlowerToPlace(final Random random, final BlockPos pos) {
        final double double3 = Biome.FOLIAGE_NOISE.sample(pos.getX() / 200.0, pos.getZ() / 200.0);
        if (double3 < -0.8) {
            final int integer5 = random.nextInt(4);
            switch (integer5) {
                case 0: {
                    return Blocks.bu.getDefaultState();
                }
                case 1: {
                    return Blocks.bt.getDefaultState();
                }
                case 2: {
                    return Blocks.bw.getDefaultState();
                }
                default: {
                    return Blocks.bv.getDefaultState();
                }
            }
        }
        else {
            if (random.nextInt(3) <= 0) {
                return Blocks.bo.getDefaultState();
            }
            final int integer5 = random.nextInt(4);
            switch (integer5) {
                case 0: {
                    return Blocks.bp.getDefaultState();
                }
                case 1: {
                    return Blocks.bs.getDefaultState();
                }
                case 2: {
                    return Blocks.bx.getDefaultState();
                }
                default: {
                    return Blocks.by.getDefaultState();
                }
            }
        }
    }
}
