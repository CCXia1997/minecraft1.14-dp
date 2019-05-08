package net.minecraft.block.sapling;

import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import javax.annotation.Nullable;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import java.util.Random;

public abstract class SaplingGenerator
{
    @Nullable
    protected abstract AbstractTreeFeature<DefaultFeatureConfig> createTreeFeature(final Random arg1);
    
    public boolean generate(final IWorld world, final BlockPos pos, final BlockState state, final Random random) {
        final AbstractTreeFeature<DefaultFeatureConfig> abstractTreeFeature5 = this.createTreeFeature(random);
        if (abstractTreeFeature5 == null) {
            return false;
        }
        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 4);
        if (abstractTreeFeature5.generate(world, world.getChunkManager().getChunkGenerator(), random, pos, FeatureConfig.DEFAULT)) {
            return true;
        }
        world.setBlockState(pos, state, 4);
        return false;
    }
}
