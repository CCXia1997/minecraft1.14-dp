package net.minecraft.block.sapling;

import net.minecraft.world.gen.feature.MegaJungleTreeFeature;
import javax.annotation.Nullable;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.gen.feature.OakTreeFeature;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import java.util.Random;

public class JungleSaplingGenerator extends LargeTreeSaplingGenerator
{
    @Nullable
    @Override
    protected AbstractTreeFeature<DefaultFeatureConfig> createTreeFeature(final Random random) {
        return new OakTreeFeature(DefaultFeatureConfig::deserialize, true, 4 + random.nextInt(7), Blocks.L.getDefaultState(), Blocks.aj.getDefaultState(), false);
    }
    
    @Nullable
    @Override
    protected AbstractTreeFeature<DefaultFeatureConfig> createLargeTreeFeature(final Random random) {
        return new MegaJungleTreeFeature(DefaultFeatureConfig::deserialize, true, 10, 20, Blocks.L.getDefaultState(), Blocks.aj.getDefaultState());
    }
}
