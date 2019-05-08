package net.minecraft.world.gen.feature;

import net.minecraft.world.ViewableWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class TaigaGrassFeature extends Feature<DefaultFeatureConfig>
{
    public TaigaGrassFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    public BlockState getGrass(final Random random) {
        return (random.nextInt(5) > 0) ? Blocks.aR.getDefaultState() : Blocks.aQ.getDefaultState();
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, BlockPos pos, final DefaultFeatureConfig config) {
        final BlockState blockState6 = this.getGrass(random);
        for (BlockState blockState7 = world.getBlockState(pos); (blockState7.isAir() || blockState7.matches(BlockTags.C)) && pos.getY() > 0; pos = pos.down(), blockState7 = world.getBlockState(pos)) {}
        int integer8 = 0;
        for (int integer9 = 0; integer9 < 128; ++integer9) {
            final BlockPos blockPos10 = pos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
            if (world.isAir(blockPos10) && blockState6.canPlaceAt(world, blockPos10)) {
                world.setBlockState(blockPos10, blockState6, 2);
                ++integer8;
            }
        }
        return integer8 > 0;
    }
}
