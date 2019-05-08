package net.minecraft.world.gen.feature;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Property;
import net.minecraft.block.KelpBlock;
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

public class KelpFeature extends Feature<DefaultFeatureConfig>
{
    public KelpFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final DefaultFeatureConfig config) {
        int integer6 = 0;
        final int integer7 = world.getTop(Heightmap.Type.d, pos.getX(), pos.getZ());
        BlockPos blockPos8 = new BlockPos(pos.getX(), integer7, pos.getZ());
        if (world.getBlockState(blockPos8).getBlock() == Blocks.A) {
            final BlockState blockState9 = Blocks.jU.getDefaultState();
            final BlockState blockState10 = Blocks.jV.getDefaultState();
            for (int integer8 = 1 + random.nextInt(10), integer9 = 0; integer9 <= integer8; ++integer9) {
                if (world.getBlockState(blockPos8).getBlock() == Blocks.A && world.getBlockState(blockPos8.up()).getBlock() == Blocks.A && blockState10.canPlaceAt(world, blockPos8)) {
                    if (integer9 == integer8) {
                        world.setBlockState(blockPos8, ((AbstractPropertyContainer<O, BlockState>)blockState9).<Comparable, Integer>with((Property<Comparable>)KelpBlock.AGE, random.nextInt(23)), 2);
                        ++integer6;
                    }
                    else {
                        world.setBlockState(blockPos8, blockState10, 2);
                    }
                }
                else if (integer9 > 0) {
                    final BlockPos blockPos9 = blockPos8.down();
                    if (blockState9.canPlaceAt(world, blockPos9) && world.getBlockState(blockPos9.down()).getBlock() != Blocks.jU) {
                        world.setBlockState(blockPos9, ((AbstractPropertyContainer<O, BlockState>)blockState9).<Comparable, Integer>with((Property<Comparable>)KelpBlock.AGE, random.nextInt(23)), 2);
                        ++integer6;
                        break;
                    }
                    break;
                }
                blockPos8 = blockPos8.up();
            }
        }
        return integer6 > 0;
    }
}
