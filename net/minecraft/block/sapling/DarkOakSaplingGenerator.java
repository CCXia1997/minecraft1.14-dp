package net.minecraft.block.sapling;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.gen.feature.DarkOakTreeFeature;
import javax.annotation.Nullable;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import java.util.Random;

public class DarkOakSaplingGenerator extends LargeTreeSaplingGenerator
{
    @Nullable
    @Override
    protected AbstractTreeFeature<DefaultFeatureConfig> createTreeFeature(final Random random) {
        return null;
    }
    
    @Nullable
    @Override
    protected AbstractTreeFeature<DefaultFeatureConfig> createLargeTreeFeature(final Random random) {
        return new DarkOakTreeFeature(DefaultFeatureConfig::deserialize, true);
    }
}
