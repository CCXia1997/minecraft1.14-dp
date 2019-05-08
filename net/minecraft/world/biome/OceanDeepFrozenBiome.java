package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityCategory;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;

public class OceanDeepFrozenBiome extends Biome
{
    protected static final OctaveSimplexNoiseSampler u;
    
    public OceanDeepFrozenBiome() {
        super(new Settings().<TernarySurfaceConfig>configureSurfaceBuilder(SurfaceBuilder.FROZEN_OCEAN, SurfaceBuilder.GRASS_CONFIG).precipitation(Precipitation.RAIN).category(Category.OCEAN).depth(-1.8f).scale(0.1f).temperature(0.5f).downfall(0.5f).waterColor(3750089).waterFogColor(329011).parent(null));
        this.<OceanRuinFeatureConfig>addStructureFeature(Feature.OCEAN_RUIN, new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.COLD, 0.3f, 0.9f));
        this.<DefaultFeatureConfig>addStructureFeature(Feature.OCEAN_MONUMENT, FeatureConfig.DEFAULT);
        this.<MineshaftFeatureConfig>addStructureFeature(Feature.MINESHAFT, new MineshaftFeatureConfig(0.004, MineshaftFeature.Type.NORMAL));
        this.<ShipwreckFeatureConfig>addStructureFeature(Feature.SHIPWRECK, new ShipwreckFeatureConfig(false));
        DefaultBiomeFeatures.addOceanCarvers(this);
        DefaultBiomeFeatures.addDefaultStructures(this);
        DefaultBiomeFeatures.addDefaultLakes(this);
        DefaultBiomeFeatures.addIcebergs(this);
        DefaultBiomeFeatures.addDungeons(this);
        DefaultBiomeFeatures.addBlueIce(this);
        DefaultBiomeFeatures.addMineables(this);
        DefaultBiomeFeatures.addDefaultOres(this);
        DefaultBiomeFeatures.addDefaultDisks(this);
        DefaultBiomeFeatures.addWaterBiomeOakTrees(this);
        DefaultBiomeFeatures.addDefaultFlowers(this);
        DefaultBiomeFeatures.addDefaultGrass(this);
        DefaultBiomeFeatures.addDefaultMushrooms(this);
        DefaultBiomeFeatures.addDefaultVegetation(this);
        DefaultBiomeFeatures.addSprings(this);
        DefaultBiomeFeatures.addFrozenTopLayer(this);
        this.addSpawn(EntityCategory.d, new SpawnEntry(EntityType.SQUID, 1, 1, 4));
        this.addSpawn(EntityCategory.d, new SpawnEntry(EntityType.SALMON, 15, 1, 5));
        this.addSpawn(EntityCategory.b, new SpawnEntry(EntityType.POLAR_BEAR, 1, 1, 2));
        this.addSpawn(EntityCategory.c, new SpawnEntry(EntityType.BAT, 10, 8, 8));
        this.addSpawn(EntityCategory.a, new SpawnEntry(EntityType.SPIDER, 100, 4, 4));
        this.addSpawn(EntityCategory.a, new SpawnEntry(EntityType.ZOMBIE, 95, 4, 4));
        this.addSpawn(EntityCategory.a, new SpawnEntry(EntityType.DROWNED, 5, 1, 1));
        this.addSpawn(EntityCategory.a, new SpawnEntry(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
        this.addSpawn(EntityCategory.a, new SpawnEntry(EntityType.SKELETON, 100, 4, 4));
        this.addSpawn(EntityCategory.a, new SpawnEntry(EntityType.CREEPER, 100, 4, 4));
        this.addSpawn(EntityCategory.a, new SpawnEntry(EntityType.SLIME, 100, 4, 4));
        this.addSpawn(EntityCategory.a, new SpawnEntry(EntityType.ENDERMAN, 10, 1, 4));
        this.addSpawn(EntityCategory.a, new SpawnEntry(EntityType.WITCH, 5, 1, 1));
    }
    
    @Override
    public float getTemperature(final BlockPos blockPos) {
        float float2 = this.getTemperature();
        final double double3 = OceanDeepFrozenBiome.u.sample(blockPos.getX() * 0.05, blockPos.getZ() * 0.05);
        final double double4 = OceanDeepFrozenBiome.FOLIAGE_NOISE.sample(blockPos.getX() * 0.2, blockPos.getZ() * 0.2);
        final double double5 = double3 + double4;
        if (double5 < 0.3) {
            final double double6 = OceanDeepFrozenBiome.FOLIAGE_NOISE.sample(blockPos.getX() * 0.09, blockPos.getZ() * 0.09);
            if (double6 < 0.8) {
                float2 = 0.2f;
            }
        }
        if (blockPos.getY() > 64) {
            final float float3 = (float)(OceanDeepFrozenBiome.TEMPERATURE_NOISE.sample(blockPos.getX() / 8.0f, blockPos.getZ() / 8.0f) * 4.0);
            return float2 - (float3 + blockPos.getY() - 64.0f) * 0.05f / 30.0f;
        }
        return float2;
    }
    
    static {
        u = new OctaveSimplexNoiseSampler(new Random(3456L), 3);
    }
}
