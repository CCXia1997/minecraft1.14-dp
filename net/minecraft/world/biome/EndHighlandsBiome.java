package net.minecraft.world.biome;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityCategory;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.EndGatewayFeatureConfig;
import net.minecraft.world.dimension.TheEndDimension;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public class EndHighlandsBiome extends Biome
{
    public EndHighlandsBiome() {
        super(new Settings().<TernarySurfaceConfig>configureSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.END_CONFIG).precipitation(Precipitation.NONE).category(Category.THE_END).depth(0.1f).scale(0.2f).temperature(0.5f).downfall(0.5f).waterColor(4159204).waterFogColor(329011).parent(null));
        this.<DefaultFeatureConfig>addStructureFeature(Feature.END_CITY, FeatureConfig.DEFAULT);
        this.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, Biome.<EndGatewayFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.aB, EndGatewayFeatureConfig.createConfig(TheEndDimension.SPAWN_POINT, true), Decorator.L, DecoratorConfig.DEFAULT));
        this.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, Biome.<DefaultFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.END_CITY, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT));
        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.aA, FeatureConfig.DEFAULT, Decorator.K, DecoratorConfig.DEFAULT));
        this.addSpawn(EntityCategory.a, new SpawnEntry(EntityType.ENDERMAN, 10, 4, 4));
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public int getSkyColor(final float temperature) {
        return 0;
    }
}
