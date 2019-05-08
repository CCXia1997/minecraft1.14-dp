package net.minecraft.world.biome;

import net.minecraft.world.gen.decorator.NopeDecoratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public final class VoidBiome extends Biome
{
    public VoidBiome() {
        super(new Settings().<TernarySurfaceConfig>configureSurfaceBuilder(SurfaceBuilder.NOPE, SurfaceBuilder.STONE_CONFIG).precipitation(Precipitation.NONE).category(Category.NONE).depth(0.1f).scale(0.2f).temperature(0.5f).downfall(0.5f).waterColor(4159204).waterFogColor(329011).parent(null));
        this.addFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, Biome.<DefaultFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.M, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT));
    }
}
