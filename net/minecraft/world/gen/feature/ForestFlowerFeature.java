package net.minecraft.world.gen.feature;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.Block;

public class ForestFlowerFeature extends FlowerFeature
{
    private static final Block[] FLOWERS;
    
    public ForestFlowerFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public BlockState getFlowerToPlace(final Random random, final BlockPos pos) {
        final double double3 = MathHelper.clamp((1.0 + Biome.FOLIAGE_NOISE.sample(pos.getX() / 48.0, pos.getZ() / 48.0)) / 2.0, 0.0, 0.9999);
        final Block block5 = ForestFlowerFeature.FLOWERS[(int)(double3 * ForestFlowerFeature.FLOWERS.length)];
        if (block5 == Blocks.bq) {
            return Blocks.bp.getDefaultState();
        }
        return block5.getDefaultState();
    }
    
    static {
        FLOWERS = new Block[] { Blocks.bo, Blocks.bp, Blocks.bq, Blocks.br, Blocks.bs, Blocks.bt, Blocks.bu, Blocks.bv, Blocks.bw, Blocks.bx, Blocks.by, Blocks.bA };
    }
}
