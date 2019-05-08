package net.minecraft.world.gen.feature;

import net.minecraft.block.ChorusFlowerBlock;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class ChorusPlantFeature extends Feature<DefaultFeatureConfig>
{
    public ChorusPlantFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final DefaultFeatureConfig config) {
        if (world.isAir(pos.up()) && world.getBlockState(pos).getBlock() == Blocks.dW) {
            ChorusFlowerBlock.generate(world, pos.up(), random, 8);
            return true;
        }
        return false;
    }
}
