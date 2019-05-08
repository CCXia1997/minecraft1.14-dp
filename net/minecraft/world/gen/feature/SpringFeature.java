package net.minecraft.world.gen.feature;

import net.minecraft.block.BlockState;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class SpringFeature extends Feature<SpringFeatureConfig>
{
    public SpringFeature(final Function<Dynamic<?>, ? extends SpringFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final SpringFeatureConfig config) {
        if (!Block.isNaturalStone(world.getBlockState(pos.up()).getBlock())) {
            return false;
        }
        if (!Block.isNaturalStone(world.getBlockState(pos.down()).getBlock())) {
            return false;
        }
        final BlockState blockState6 = world.getBlockState(pos);
        if (!blockState6.isAir() && !Block.isNaturalStone(blockState6.getBlock())) {
            return false;
        }
        int integer7 = 0;
        int integer8 = 0;
        if (Block.isNaturalStone(world.getBlockState(pos.west()).getBlock())) {
            ++integer8;
        }
        if (Block.isNaturalStone(world.getBlockState(pos.east()).getBlock())) {
            ++integer8;
        }
        if (Block.isNaturalStone(world.getBlockState(pos.north()).getBlock())) {
            ++integer8;
        }
        if (Block.isNaturalStone(world.getBlockState(pos.south()).getBlock())) {
            ++integer8;
        }
        int integer9 = 0;
        if (world.isAir(pos.west())) {
            ++integer9;
        }
        if (world.isAir(pos.east())) {
            ++integer9;
        }
        if (world.isAir(pos.north())) {
            ++integer9;
        }
        if (world.isAir(pos.south())) {
            ++integer9;
        }
        if (integer8 == 3 && integer9 == 1) {
            world.setBlockState(pos, config.state.getBlockState(), 2);
            world.getFluidTickScheduler().schedule(pos, config.state.getFluid(), 0);
            ++integer7;
        }
        return integer7 > 0;
    }
}
