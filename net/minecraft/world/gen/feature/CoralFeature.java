package net.minecraft.world.gen.feature;

import net.minecraft.state.AbstractPropertyContainer;
import java.util.Iterator;
import net.minecraft.block.DeadCoralWallFanBlock;
import net.minecraft.util.math.Direction;
import net.minecraft.state.property.Property;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public abstract class CoralFeature extends Feature<DefaultFeatureConfig>
{
    public CoralFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final DefaultFeatureConfig config) {
        final BlockState blockState6 = BlockTags.N.getRandom(random).getDefaultState();
        return this.spawnCoral(world, random, pos, blockState6);
    }
    
    protected abstract boolean spawnCoral(final IWorld arg1, final Random arg2, final BlockPos arg3, final BlockState arg4);
    
    protected boolean spawnCoralPiece(final IWorld world, final Random random, final BlockPos pos, final BlockState state) {
        final BlockPos blockPos5 = pos.up();
        final BlockState blockState6 = world.getBlockState(pos);
        if ((blockState6.getBlock() != Blocks.A && !blockState6.matches(BlockTags.Q)) || world.getBlockState(blockPos5).getBlock() != Blocks.A) {
            return false;
        }
        world.setBlockState(pos, state, 3);
        if (random.nextFloat() < 0.25f) {
            world.setBlockState(blockPos5, BlockTags.Q.getRandom(random).getDefaultState(), 2);
        }
        else if (random.nextFloat() < 0.05f) {
            world.setBlockState(blockPos5, ((AbstractPropertyContainer<O, BlockState>)Blocks.kM.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)SeaPickleBlock.PICKLES, random.nextInt(4) + 1), 2);
        }
        for (final Direction direction8 : Direction.Type.HORIZONTAL) {
            if (random.nextFloat() < 0.2f) {
                final BlockPos blockPos6 = pos.offset(direction8);
                if (world.getBlockState(blockPos6).getBlock() != Blocks.A) {
                    continue;
                }
                final BlockState blockState7 = ((AbstractPropertyContainer<O, BlockState>)BlockTags.O.getRandom(random).getDefaultState()).<Comparable, Direction>with((Property<Comparable>)DeadCoralWallFanBlock.FACING, direction8);
                world.setBlockState(blockPos6, blockState7, 2);
            }
        }
        return true;
    }
}
