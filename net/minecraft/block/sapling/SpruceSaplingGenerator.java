package net.minecraft.block.sapling;

import net.minecraft.world.gen.feature.MegaPineTreeFeature;
import javax.annotation.Nullable;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.gen.feature.SpruceTreeFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import java.util.Random;

public class SpruceSaplingGenerator extends LargeTreeSaplingGenerator
{
    @Nullable
    @Override
    protected AbstractTreeFeature<DefaultFeatureConfig> createTreeFeature(final Random random) {
        return new SpruceTreeFeature(DefaultFeatureConfig::deserialize, true);
    }
    
    @Nullable
    @Override
    protected AbstractTreeFeature<DefaultFeatureConfig> createLargeTreeFeature(final Random random) {
        return new MegaPineTreeFeature(DefaultFeatureConfig::deserialize, false, random.nextBoolean());
    }
}
