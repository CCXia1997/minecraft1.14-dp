package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockState;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class JungleTreeFeature extends OakTreeFeature
{
    public JungleTreeFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, final boolean boolean2, final int height, final BlockState log, final BlockState leaves, final boolean hasVinesAndCocoa) {
        super(function, boolean2, height, log, leaves, hasVinesAndCocoa);
    }
    
    @Override
    protected int getTreeHeight(final Random random) {
        return this.height + random.nextInt(7);
    }
}
