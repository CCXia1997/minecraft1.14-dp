package net.minecraft.world.gen.feature;

import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.world.ViewableWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.DeadBushBlock;

public class DeadBushFeature extends Feature<DefaultFeatureConfig>
{
    private static final DeadBushBlock DEAD_BUSH;
    
    public DeadBushFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, BlockPos pos, final DefaultFeatureConfig config) {
        for (BlockState blockState6 = world.getBlockState(pos); (blockState6.isAir() || blockState6.matches(BlockTags.C)) && pos.getY() > 0; pos = pos.down(), blockState6 = world.getBlockState(pos)) {}
        final BlockState blockState7 = DeadBushFeature.DEAD_BUSH.getDefaultState();
        for (int integer8 = 0; integer8 < 4; ++integer8) {
            final BlockPos blockPos9 = pos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
            if (world.isAir(blockPos9) && blockState7.canPlaceAt(world, blockPos9)) {
                world.setBlockState(blockPos9, blockState7, 2);
            }
        }
        return true;
    }
    
    static {
        DEAD_BUSH = (DeadBushBlock)Blocks.aS;
    }
}
