package net.minecraft.world.gen.feature;

import net.minecraft.block.BlockState;
import net.minecraft.world.BlockView;
import net.minecraft.block.Block;
import net.minecraft.util.math.Direction;
import net.minecraft.block.Blocks;
import java.util.Iterator;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public abstract class AbstractPileFeature extends Feature<DefaultFeatureConfig>
{
    public AbstractPileFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final DefaultFeatureConfig config) {
        if (pos.getY() < 5) {
            return false;
        }
        final int integer6 = 2 + random.nextInt(2);
        final int integer7 = 2 + random.nextInt(2);
        for (final BlockPos blockPos9 : BlockPos.iterate(pos.add(-integer6, 0, -integer7), pos.add(integer6, 1, integer7))) {
            final int integer8 = pos.getX() - blockPos9.getX();
            final int integer9 = pos.getZ() - blockPos9.getZ();
            if (integer8 * integer8 + integer9 * integer9 <= random.nextFloat() * 10.0f - random.nextFloat() * 6.0f) {
                this.addPileBlock(world, blockPos9, random);
            }
            else {
                if (random.nextFloat() >= 0.031) {
                    continue;
                }
                this.addPileBlock(world, blockPos9, random);
            }
        }
        return true;
    }
    
    private boolean canPlacePileBlock(final IWorld world, final BlockPos pos, final Random random) {
        final BlockPos blockPos4 = pos.down();
        final BlockState blockState5 = world.getBlockState(blockPos4);
        if (blockState5.getBlock() == Blocks.iw) {
            return random.nextBoolean();
        }
        return Block.isSolidFullSquare(blockState5, world, blockPos4, Direction.UP);
    }
    
    private void addPileBlock(final IWorld world, final BlockPos pos, final Random random) {
        if (world.isAir(pos) && this.canPlacePileBlock(world, pos, random)) {
            world.setBlockState(pos, this.getPileBlockState(world), 4);
        }
    }
    
    protected abstract BlockState getPileBlockState(final IWorld arg1);
}
