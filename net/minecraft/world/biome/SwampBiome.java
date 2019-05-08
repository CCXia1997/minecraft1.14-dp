package net.minecraft.world.biome;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityCategory;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.SeagrassFeatureConfig;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public final class SwampBiome extends Biome
{
    protected SwampBiome() {
        super(new Settings().<TernarySurfaceConfig>configureSurfaceBuilder(SurfaceBuilder.SWAMP, SurfaceBuilder.GRASS_CONFIG).precipitation(Precipitation.RAIN).category(Category.SWAMP).depth(-0.2f).scale(0.1f).temperature(0.8f).downfall(0.9f).waterColor(6388580).waterFogColor(2302743).parent(null));
        this.<DefaultFeatureConfig>addStructureFeature(Feature.SWAMP_HUT, FeatureConfig.DEFAULT);
        this.<MineshaftFeatureConfig>addStructureFeature(Feature.MINESHAFT, new MineshaftFeatureConfig(0.004, MineshaftFeature.Type.NORMAL));
        DefaultBiomeFeatures.addLandCarvers(this);
        DefaultBiomeFeatures.addDefaultStructures(this);
        DefaultBiomeFeatures.addDefaultLakes(this);
        DefaultBiomeFeatures.addDungeons(this);
        DefaultBiomeFeatures.addMineables(this);
        DefaultBiomeFeatures.addDefaultOres(this);
        DefaultBiomeFeatures.addClay(this);
        DefaultBiomeFeatures.addSwampFeatures(this);
        DefaultBiomeFeatures.addDefaultMushrooms(this);
        DefaultBiomeFeatures.addSwampVegetation(this);
        DefaultBiomeFeatures.addSprings(this);
        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<SeagrassFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.aC, new SeagrassFeatureConfig(64, 0.6), Decorator.v, DecoratorConfig.DEFAULT));
        DefaultBiomeFeatures.addFossils(this);
        DefaultBiomeFeatures.addFrozenTopLayer(this);
        this.addSpawn(EntityCategory.b, new SpawnEntry(EntityType.SHEEP, 12, 4, 4));
        this.addSpawn(EntityCategory.b, new SpawnEntry(EntityType.PIG, 10, 4, 4));
        this.addSpawn(EntityCategory.b, new SpawnEntry(EntityType.CHICKEN, 10, 4, 4));
        this.addSpawn(EntityCategory.b, new SpawnEntry(EntityType.COW, 8, 4, 4));
        this.addSpawn(EntityCategory.c, new SpawnEntry(EntityType.BAT, 10, 8, 8));
        this.addSpawn(EntityCategory.a, new SpawnEntry(EntityType.SPIDER, 100, 4, 4));
        this.addSpawn(EntityCategory.a, new SpawnEntry(EntityType.ZOMBIE, 95, 4, 4));
        this.addSpawn(EntityCategory.a, new SpawnEntry(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
        this.addSpawn(EntityCategory.a, new SpawnEntry(EntityType.SKELETON, 100, 4, 4));
        this.addSpawn(EntityCategory.a, new SpawnEntry(EntityType.CREEPER, 100, 4, 4));
        this.addSpawn(EntityCategory.a, new SpawnEntry(EntityType.SLIME, 100, 4, 4));
        this.addSpawn(EntityCategory.a, new SpawnEntry(EntityType.ENDERMAN, 10, 1, 4));
        this.addSpawn(EntityCategory.a, new SpawnEntry(EntityType.WITCH, 5, 1, 1));
        this.addSpawn(EntityCategory.a, new SpawnEntry(EntityType.SLIME, 1, 1, 1));
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public int getGrassColorAt(final BlockPos blockPos) {
        final double double2 = SwampBiome.FOLIAGE_NOISE.sample(blockPos.getX() * 0.0225, blockPos.getZ() * 0.0225);
        if (double2 < -0.1) {
            return 5011004;
        }
        return 6975545;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public int getFoliageColorAt(final BlockPos blockPos) {
        return 6975545;
    }
}
