package net.minecraft.world.gen.feature;

import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class GeneralForestFlowerFeature extends FlowerFeature
{
    public GeneralForestFlowerFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public BlockState getFlowerToPlace(final Random random, final BlockPos pos) {
        return Blocks.bA.getDefaultState();
    }
}
