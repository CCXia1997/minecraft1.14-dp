package net.minecraft.block;

import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.IWorld;
import java.util.Iterator;
import net.minecraft.world.ViewableWorld;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.util.shape.VoxelShape;

public class MushroomPlantBlock extends PlantBlock implements Fertilizable
{
    protected static final VoxelShape SHAPE;
    
    public MushroomPlantBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return MushroomPlantBlock.SHAPE;
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, BlockPos pos, final Random random) {
        if (random.nextInt(25) == 0) {
            int integer5 = 5;
            final int integer6 = 4;
            for (final BlockPos blockPos8 : BlockPos.iterate(pos.add(-4, -1, -4), pos.add(4, 1, 4))) {
                if (world.getBlockState(blockPos8).getBlock() == this && --integer5 <= 0) {
                    return;
                }
            }
            BlockPos blockPos9 = pos.add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
            for (int integer7 = 0; integer7 < 4; ++integer7) {
                if (world.isAir(blockPos9) && state.canPlaceAt(world, blockPos9)) {
                    pos = blockPos9;
                }
                blockPos9 = pos.add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
            }
            if (world.isAir(blockPos9) && state.canPlaceAt(world, blockPos9)) {
                world.setBlockState(blockPos9, state, 2);
            }
        }
    }
    
    @Override
    protected boolean canPlantOnTop(final BlockState floor, final BlockView view, final BlockPos pos) {
        return floor.isFullOpaque(view, pos);
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        final BlockPos blockPos4 = pos.down();
        final BlockState blockState5 = world.getBlockState(blockPos4);
        final Block block6 = blockState5.getBlock();
        return block6 == Blocks.dL || block6 == Blocks.l || (world.getLightLevel(pos, 0) < 13 && this.canPlantOnTop(blockState5, world, blockPos4));
    }
    
    public boolean trySpawningBigMushroom(final IWorld world, final BlockPos pos, final BlockState state, final Random random) {
        world.clearBlockState(pos, false);
        Feature<DefaultFeatureConfig> feature5 = null;
        if (this == Blocks.bB) {
            feature5 = Feature.T;
        }
        else if (this == Blocks.bC) {
            feature5 = Feature.S;
        }
        if (feature5 != null && feature5.generate(world, world.getChunkManager().getChunkGenerator(), random, pos, FeatureConfig.DEFAULT)) {
            return true;
        }
        world.setBlockState(pos, state, 3);
        return false;
    }
    
    @Override
    public boolean isFertilizable(final BlockView world, final BlockPos pos, final BlockState state, final boolean boolean4) {
        return true;
    }
    
    @Override
    public boolean canGrow(final World world, final Random random, final BlockPos pos, final BlockState state) {
        return random.nextFloat() < 0.4;
    }
    
    @Override
    public void grow(final World world, final Random random, final BlockPos pos, final BlockState state) {
        this.trySpawningBigMushroom(world, pos, state, random);
    }
    
    @Override
    public boolean shouldPostProcess(final BlockState state, final BlockView view, final BlockPos pos) {
        return true;
    }
    
    static {
        SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);
    }
}
