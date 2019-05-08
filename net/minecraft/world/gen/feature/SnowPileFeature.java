package net.minecraft.world.gen.feature;

import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class SnowPileFeature extends AbstractPileFeature
{
    public SnowPileFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    protected BlockState getPileBlockState(final IWorld world) {
        return Blocks.cC.getDefaultState();
    }
}
