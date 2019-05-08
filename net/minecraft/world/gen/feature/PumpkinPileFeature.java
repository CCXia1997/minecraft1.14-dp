package net.minecraft.world.gen.feature;

import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class PumpkinPileFeature extends AbstractPileFeature
{
    public PumpkinPileFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    protected BlockState getPileBlockState(final IWorld world) {
        if (world.getRandom().nextFloat() < 0.95f) {
            return Blocks.cI.getDefaultState();
        }
        return Blocks.cO.getDefaultState();
    }
}
