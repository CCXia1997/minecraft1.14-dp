package net.minecraft.world.gen.feature;

import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class SwampFlowerFeature extends FlowerFeature
{
    public SwampFlowerFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public BlockState getFlowerToPlace(final Random random, final BlockPos pos) {
        return Blocks.bq.getDefaultState();
    }
}
