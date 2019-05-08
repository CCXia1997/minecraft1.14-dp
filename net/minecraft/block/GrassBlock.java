package net.minecraft.block;

import java.util.List;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.FlowerFeature;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class GrassBlock extends SpreadableBlock implements Fertilizable
{
    public GrassBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public boolean isFertilizable(final BlockView world, final BlockPos pos, final BlockState state, final boolean boolean4) {
        return world.getBlockState(pos.up()).isAir();
    }
    
    @Override
    public boolean canGrow(final World world, final Random random, final BlockPos pos, final BlockState state) {
        return true;
    }
    
    @Override
    public void grow(final World world, final Random random, final BlockPos pos, final BlockState state) {
        final BlockPos blockPos5 = pos.up();
        final BlockState blockState6 = Blocks.aQ.getDefaultState();
        int integer7 = 0;
    Label_0276_Outer:
        while (integer7 < 128) {
            BlockPos blockPos6 = blockPos5;
            int integer8 = 0;
            while (true) {
                while (integer8 < integer7 / 16) {
                    blockPos6 = blockPos6.add(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                    if (world.getBlockState(blockPos6.down()).getBlock() == this) {
                        if (!Block.isShapeFullCube(world.getBlockState(blockPos6).getCollisionShape(world, blockPos6))) {
                            ++integer8;
                            continue Label_0276_Outer;
                        }
                    }
                    ++integer7;
                    continue Label_0276_Outer;
                }
                final BlockState blockState7 = world.getBlockState(blockPos6);
                if (blockState7.getBlock() == blockState6.getBlock() && random.nextInt(10) == 0) {
                    ((Fertilizable)blockState6.getBlock()).grow(world, random, blockPos6, blockState7);
                }
                if (!blockState7.isAir()) {
                    continue;
                }
                BlockState blockState8;
                if (random.nextInt(8) == 0) {
                    final List<ConfiguredFeature<?>> list11 = world.getBiome(blockPos6).getFlowerFeatures();
                    if (list11.isEmpty()) {
                        continue;
                    }
                    blockState8 = ((FlowerFeature)((DecoratedFeatureConfig)list11.get(0).config).feature.feature).getFlowerToPlace(random, blockPos6);
                }
                else {
                    blockState8 = blockState6;
                }
                if (blockState8.canPlaceAt(world, blockPos6)) {
                    world.setBlockState(blockPos6, blockState8, 3);
                }
                continue;
            }
        }
    }
    
    @Override
    public boolean isFullBoundsCubeForCulling(final BlockState state) {
        return true;
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.MIPPED_CUTOUT;
    }
}
