package net.minecraft.block.sapling;

import javax.annotation.Nullable;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.gen.feature.SavannaTreeFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import java.util.Random;

public class AcaciaSaplingGenerator extends SaplingGenerator
{
    @Nullable
    @Override
    protected AbstractTreeFeature<DefaultFeatureConfig> createTreeFeature(final Random random) {
        return new SavannaTreeFeature(DefaultFeatureConfig::deserialize, true);
    }
}
