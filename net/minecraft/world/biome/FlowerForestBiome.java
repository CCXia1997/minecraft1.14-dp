package net.minecraft.world.biome;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityCategory;
import net.minecraft.world.gen.decorator.CountExtraChanceDecoratorConfig;
import net.minecraft.world.gen.feature.RandomFeatureConfig;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.RandomRandomFeatureConfig;
import net.minecraft.world.gen.feature.DoublePlantFeatureConfig;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public final class FlowerForestBiome extends Biome
{
    public FlowerForestBiome() {
        super(new Settings().<TernarySurfaceConfig>configureSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRASS_CONFIG).precipitation(Precipitation.RAIN).category(Category.FOREST).depth(0.1f).scale(0.4f).temperature(0.7f).downfall(0.8f).waterColor(4159204).waterFogColor(329011).parent("forest"));
        this.<MineshaftFeatureConfig>addStructureFeature(Feature.MINESHAFT, new MineshaftFeatureConfig(0.004, MineshaftFeature.Type.NORMAL));
        this.<DefaultFeatureConfig>addStructureFeature(Feature.STRONGHOLD, FeatureConfig.DEFAULT);
        DefaultBiomeFeatures.addLandCarvers(this);
        DefaultBiomeFeatures.addDefaultStructures(this);
        DefaultBiomeFeatures.addDefaultLakes(this);
        DefaultBiomeFeatures.addDungeons(this);
        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<RandomRandomFeatureConfig, CountDecoratorConfig>configureFeature(Feature.as, new RandomRandomFeatureConfig(new Feature[] { Feature.an, Feature.an, Feature.an, Feature.GENERAL_FOREST_FLOWER }, new FeatureConfig[] { new DoublePlantFeatureConfig(Blocks.gN.getDefaultState()), new DoublePlantFeatureConfig(Blocks.gO.getDefaultState()), new DoublePlantFeatureConfig(Blocks.gP.getDefaultState()), FeatureConfig.DEFAULT }, 2), Decorator.c, new CountDecoratorConfig(5)));
        DefaultBiomeFeatures.addMineables(this);
        DefaultBiomeFeatures.addDefaultOres(this);
        DefaultBiomeFeatures.addDefaultDisks(this);
        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<RandomFeatureConfig, CountExtraChanceDecoratorConfig>configureFeature(Feature.at, new RandomFeatureConfig(new Feature[] { Feature.r, Feature.q }, new FeatureConfig[] { FeatureConfig.DEFAULT, FeatureConfig.DEFAULT }, new float[] { 0.2f, 0.1f }, Feature.A, FeatureConfig.DEFAULT), Decorator.m, new CountExtraChanceDecoratorConfig(6, 0.1f, 1)));
        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, CountDecoratorConfig>configureFeature(Feature.FOREST_FLOWER, FeatureConfig.DEFAULT, Decorator.c, new CountDecoratorConfig(100)));
        DefaultBiomeFeatures.addDefaultGrass(this);
        DefaultBiomeFeatures.addDefaultMushrooms(this);
        DefaultBiomeFeatures.addDefaultVegetation(this);
        DefaultBiomeFeatures.addSprings(this);
        DefaultBiomeFeatures.addFrozenTopLayer(this);
        this.addSpawn(EntityCategory.b, new SpawnEntry(EntityType.SHEEP, 12, 4, 4));
        this.addSpawn(EntityCategory.b, new SpawnEntry(EntityType.PIG, 10, 4, 4));
        this.addSpawn(EntityCategory.b, new SpawnEntry(EntityType.CHICKEN, 10, 4, 4));
        this.addSpawn(EntityCategory.b, new SpawnEntry(EntityType.COW, 8, 4, 4));
        this.addSpawn(EntityCategory.b, new SpawnEntry(EntityType.RABBIT, 4, 2, 3));
        this.addSpawn(EntityCategory.c, new SpawnEntry(EntityType.BAT, 10, 8, 8));
        this.addSpawn(EntityCategory.a, new SpawnEntry(EntityType.SPIDER, 100, 4, 4));
        this.addSpawn(EntityCategory.a, new SpawnEntry(EntityType.ZOMBIE, 95, 4, 4));
        this.addSpawn(EntityCategory.a, new SpawnEntry(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
        this.addSpawn(EntityCategory.a, new SpawnEntry(EntityType.SKELETON, 100, 4, 4));
        this.addSpawn(EntityCategory.a, new SpawnEntry(EntityType.CREEPER, 100, 4, 4));
        this.addSpawn(EntityCategory.a, new SpawnEntry(EntityType.SLIME, 100, 4, 4));
        this.addSpawn(EntityCategory.a, new SpawnEntry(EntityType.ENDERMAN, 10, 1, 4));
        this.addSpawn(EntityCategory.a, new SpawnEntry(EntityType.WITCH, 5, 1, 1));
    }
}
