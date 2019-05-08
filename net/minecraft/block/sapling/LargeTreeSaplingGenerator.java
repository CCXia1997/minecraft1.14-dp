package net.minecraft.block.sapling;

import net.minecraft.block.Block;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.block.Blocks;
import javax.annotation.Nullable;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.BlockView;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public abstract class LargeTreeSaplingGenerator extends SaplingGenerator
{
    @Override
    public boolean generate(final IWorld world, final BlockPos pos, final BlockState state, final Random random) {
        for (int integer5 = 0; integer5 >= -1; --integer5) {
            for (int integer6 = 0; integer6 >= -1; --integer6) {
                if (canGenerateLargeTree(state, world, pos, integer5, integer6)) {
                    return this.generateLargeTree(world, pos, state, random, integer5, integer6);
                }
            }
        }
        return super.generate(world, pos, state, random);
    }
    
    @Nullable
    protected abstract AbstractTreeFeature<DefaultFeatureConfig> createLargeTreeFeature(final Random arg1);
    
    public boolean generateLargeTree(final IWorld iWorld, final BlockPos blockPos, final BlockState blockState, final Random random, final int integer5, final int integer6) {
        final AbstractTreeFeature<DefaultFeatureConfig> abstractTreeFeature7 = this.createLargeTreeFeature(random);
        if (abstractTreeFeature7 == null) {
            return false;
        }
        final BlockState blockState2 = Blocks.AIR.getDefaultState();
        iWorld.setBlockState(blockPos.add(integer5, 0, integer6), blockState2, 4);
        iWorld.setBlockState(blockPos.add(integer5 + 1, 0, integer6), blockState2, 4);
        iWorld.setBlockState(blockPos.add(integer5, 0, integer6 + 1), blockState2, 4);
        iWorld.setBlockState(blockPos.add(integer5 + 1, 0, integer6 + 1), blockState2, 4);
        if (abstractTreeFeature7.generate(iWorld, iWorld.getChunkManager().getChunkGenerator(), random, blockPos.add(integer5, 0, integer6), FeatureConfig.DEFAULT)) {
            return true;
        }
        iWorld.setBlockState(blockPos.add(integer5, 0, integer6), blockState, 4);
        iWorld.setBlockState(blockPos.add(integer5 + 1, 0, integer6), blockState, 4);
        iWorld.setBlockState(blockPos.add(integer5, 0, integer6 + 1), blockState, 4);
        iWorld.setBlockState(blockPos.add(integer5 + 1, 0, integer6 + 1), blockState, 4);
        return false;
    }
    
    public static boolean canGenerateLargeTree(final BlockState state, final BlockView world, final BlockPos pos, final int x, final int z) {
        final Block block6 = state.getBlock();
        return block6 == world.getBlockState(pos.add(x, 0, z)).getBlock() && block6 == world.getBlockState(pos.add(x + 1, 0, z)).getBlock() && block6 == world.getBlockState(pos.add(x, 0, z + 1)).getBlock() && block6 == world.getBlockState(pos.add(x + 1, 0, z + 1)).getBlock();
    }
}
