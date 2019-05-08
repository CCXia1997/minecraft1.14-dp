package net.minecraft.block.sapling;

import javax.annotation.Nullable;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.gen.feature.BirchTreeFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import java.util.Random;

public class BirchSaplingGenerator extends SaplingGenerator
{
    @Nullable
    @Override
    protected AbstractTreeFeature<DefaultFeatureConfig> createTreeFeature(final Random random) {
        return new BirchTreeFeature(DefaultFeatureConfig::deserialize, true, false);
    }
}
