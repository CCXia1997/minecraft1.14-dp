package net.minecraft.world.gen.feature;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Property;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.TallSeagrassBlock;
import net.minecraft.block.BlockState;
import net.minecraft.world.ViewableWorld;
import net.minecraft.block.Blocks;
import net.minecraft.world.Heightmap;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class SeagrassFeature extends Feature<SeagrassFeatureConfig>
{
    public SeagrassFeature(final Function<Dynamic<?>, ? extends SeagrassFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final SeagrassFeatureConfig config) {
        int integer6 = 0;
        for (int integer7 = 0; integer7 < config.count; ++integer7) {
            final int integer8 = random.nextInt(8) - random.nextInt(8);
            final int integer9 = random.nextInt(8) - random.nextInt(8);
            final int integer10 = world.getTop(Heightmap.Type.d, pos.getX() + integer8, pos.getZ() + integer9);
            final BlockPos blockPos11 = new BlockPos(pos.getX() + integer8, integer10, pos.getZ() + integer9);
            if (world.getBlockState(blockPos11).getBlock() == Blocks.A) {
                final boolean boolean12 = random.nextDouble() < config.tallSeagrassProbability;
                final BlockState blockState13 = boolean12 ? Blocks.aU.getDefaultState() : Blocks.aT.getDefaultState();
                if (blockState13.canPlaceAt(world, blockPos11)) {
                    if (boolean12) {
                        final BlockState blockState14 = ((AbstractPropertyContainer<O, BlockState>)blockState13).<DoubleBlockHalf, DoubleBlockHalf>with(TallSeagrassBlock.HALF, DoubleBlockHalf.a);
                        final BlockPos blockPos12 = blockPos11.up();
                        if (world.getBlockState(blockPos12).getBlock() == Blocks.A) {
                            world.setBlockState(blockPos11, blockState13, 2);
                            world.setBlockState(blockPos12, blockState14, 2);
                        }
                    }
                    else {
                        world.setBlockState(blockPos11, blockState13, 2);
                    }
                    ++integer6;
                }
            }
        }
        return integer6 > 0;
    }
}
