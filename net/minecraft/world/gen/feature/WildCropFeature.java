package net.minecraft.world.gen.feature;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.BlockState;

public class WildCropFeature extends Feature<DefaultFeatureConfig>
{
    protected final BlockState crop;
    
    public WildCropFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer, final BlockState crop) {
        super(configDeserializer);
        this.crop = crop;
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final DefaultFeatureConfig config) {
        int integer6 = 0;
        for (int integer7 = 0; integer7 < 64; ++integer7) {
            final BlockPos blockPos8 = pos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
            if (world.isAir(blockPos8) && world.getBlockState(blockPos8.down()).getBlock() == Blocks.i) {
                world.setBlockState(blockPos8, this.crop, 2);
                ++integer6;
            }
        }
        return integer6 > 0;
    }
}
