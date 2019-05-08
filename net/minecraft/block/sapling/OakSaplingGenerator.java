package net.minecraft.block.sapling;

import javax.annotation.Nullable;
import net.minecraft.world.gen.feature.OakTreeFeature;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.gen.feature.LargeOakTreeFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import java.util.Random;

public class OakSaplingGenerator extends SaplingGenerator
{
    @Nullable
    @Override
    protected AbstractTreeFeature<DefaultFeatureConfig> createTreeFeature(final Random random) {
        return (random.nextInt(10) == 0) ? new LargeOakTreeFeature(DefaultFeatureConfig::deserialize, true) : new OakTreeFeature(DefaultFeatureConfig::deserialize, true);
    }
}
