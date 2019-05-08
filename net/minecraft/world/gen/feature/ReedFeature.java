package net.minecraft.world.gen.feature;

import net.minecraft.world.ViewableWorld;
import net.minecraft.block.Blocks;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class ReedFeature extends Feature<DefaultFeatureConfig>
{
    public ReedFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final DefaultFeatureConfig config) {
        int integer6 = 0;
        for (int integer7 = 0; integer7 < 20; ++integer7) {
            final BlockPos blockPos8 = pos.add(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
            if (world.isAir(blockPos8)) {
                final BlockPos blockPos9 = blockPos8.down();
                if (world.getFluidState(blockPos9.west()).matches(FluidTags.a) || world.getFluidState(blockPos9.east()).matches(FluidTags.a) || world.getFluidState(blockPos9.north()).matches(FluidTags.a) || world.getFluidState(blockPos9.south()).matches(FluidTags.a)) {
                    for (int integer8 = 2 + random.nextInt(random.nextInt(3) + 1), integer9 = 0; integer9 < integer8; ++integer9) {
                        if (Blocks.cF.getDefaultState().canPlaceAt(world, blockPos8)) {
                            world.setBlockState(blockPos8.up(integer9), Blocks.cF.getDefaultState(), 2);
                            ++integer6;
                        }
                    }
                }
            }
        }
        return integer6 > 0;
    }
}
