package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.Biomes;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.level.LevelGeneratorType;
import java.util.function.LongFunction;

public class BiomeLayers
{
    protected static final int WARM_OCEAN_ID;
    protected static final int LUKEWARM_OCEAN_ID;
    protected static final int OCEAN_ID;
    protected static final int COLD_OCEAN_ID;
    protected static final int FROZEN_OCEAN_ID;
    protected static final int DEEP_WARM_OCEAN_ID;
    protected static final int DEEP_LUKEWARM_OCEAN_ID;
    protected static final int DEEP_OCEAN_ID;
    protected static final int DEEP_COLD_OCEAN_ID;
    protected static final int DEEP_FROZEN_OCEAN_ID;
    
    private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> stack(final long seed, final ParentedLayer layer, final LayerFactory<T> parent, final int count, final LongFunction<C> contextProvider) {
        LayerFactory<T> layerFactory7 = parent;
        for (int integer8 = 0; integer8 < count; ++integer8) {
            layerFactory7 = layer.<T>create(contextProvider.apply(seed + integer8), layerFactory7);
        }
        return layerFactory7;
    }
    
    public static <T extends LayerSampler, C extends LayerSampleContext<T>> ImmutableList<LayerFactory<T>> build(final LevelGeneratorType generatorType, final OverworldChunkGeneratorConfig settings, final LongFunction<C> contextProvider) {
        LayerFactory<T> layerFactory4 = ContinentLayer.a.<T>create((LayerSampleContext<T>)contextProvider.apply(1L));
        layerFactory4 = ScaleLayer.b.<T>create(contextProvider.apply(2000L), layerFactory4);
        layerFactory4 = IncreaseEdgeCurvatureLayer.INSTANCE.<T>create(contextProvider.apply(1L), layerFactory4);
        layerFactory4 = ScaleLayer.a.<T>create(contextProvider.apply(2001L), layerFactory4);
        layerFactory4 = IncreaseEdgeCurvatureLayer.INSTANCE.<T>create(contextProvider.apply(2L), layerFactory4);
        layerFactory4 = IncreaseEdgeCurvatureLayer.INSTANCE.<T>create(contextProvider.apply(50L), layerFactory4);
        layerFactory4 = IncreaseEdgeCurvatureLayer.INSTANCE.<T>create(contextProvider.apply(70L), layerFactory4);
        layerFactory4 = AddIslandLayer.a.<T>create(contextProvider.apply(2L), layerFactory4);
        LayerFactory<T> layerFactory5 = OceanTemperatureLayer.a.<T>create((LayerSampleContext<T>)contextProvider.apply(2L));
        layerFactory5 = BiomeLayers.<T, C>stack(2001L, ScaleLayer.a, layerFactory5, 6, contextProvider);
        layerFactory4 = AddColdClimatesLayer.a.<T>create(contextProvider.apply(2L), layerFactory4);
        layerFactory4 = IncreaseEdgeCurvatureLayer.INSTANCE.<T>create(contextProvider.apply(3L), layerFactory4);
        layerFactory4 = AddClimateLayers.AddTemperateBiomesLayer.a.<T>create(contextProvider.apply(2L), layerFactory4);
        layerFactory4 = AddClimateLayers.AddCoolBiomesLayer.a.<T>create(contextProvider.apply(2L), layerFactory4);
        layerFactory4 = AddClimateLayers.AddSpecialBiomesLayer.a.<T>create(contextProvider.apply(3L), layerFactory4);
        layerFactory4 = ScaleLayer.a.<T>create(contextProvider.apply(2002L), layerFactory4);
        layerFactory4 = ScaleLayer.a.<T>create(contextProvider.apply(2003L), layerFactory4);
        layerFactory4 = IncreaseEdgeCurvatureLayer.INSTANCE.<T>create(contextProvider.apply(4L), layerFactory4);
        layerFactory4 = AddMushroomIslandLayer.INSTANCE.<T>create(contextProvider.apply(5L), layerFactory4);
        layerFactory4 = AddDeepOceanLayer.a.<T>create(contextProvider.apply(4L), layerFactory4);
        layerFactory4 = BiomeLayers.<T, C>stack(1000L, ScaleLayer.a, layerFactory4, 0, contextProvider);
        int integer7;
        int integer6 = integer7 = 4;
        if (settings != null) {
            integer6 = settings.getBiomeSize();
            integer7 = settings.getRiverSize();
        }
        if (generatorType == LevelGeneratorType.LARGE_BIOMES) {
            integer6 = 6;
        }
        LayerFactory<T> layerFactory6 = layerFactory4;
        layerFactory6 = BiomeLayers.<T, C>stack(1000L, ScaleLayer.a, layerFactory6, 0, contextProvider);
        layerFactory6 = SimpleLandNoiseLayer.a.<T>create(contextProvider.apply(100L), layerFactory6);
        LayerFactory<T> layerFactory7 = layerFactory4;
        layerFactory7 = new SetBaseBiomesLayer(generatorType, settings).<T>create(contextProvider.apply(200L), layerFactory7);
        layerFactory7 = AddBambooJungleLayer.INSTANCE.<T>create(contextProvider.apply(1001L), layerFactory7);
        layerFactory7 = BiomeLayers.<T, C>stack(1000L, ScaleLayer.a, layerFactory7, 2, contextProvider);
        layerFactory7 = EaseBiomeEdgeLayer.INSTANCE.<T>create(contextProvider.apply(1000L), layerFactory7);
        LayerFactory<T> layerFactory8 = layerFactory6;
        layerFactory8 = BiomeLayers.<T, C>stack(1000L, ScaleLayer.a, layerFactory8, 2, contextProvider);
        layerFactory7 = AddHillsLayer.a.<T>create(contextProvider.apply(1000L), layerFactory7, layerFactory8);
        layerFactory6 = BiomeLayers.<T, C>stack(1000L, ScaleLayer.a, layerFactory6, 2, contextProvider);
        layerFactory6 = BiomeLayers.<T, C>stack(1000L, ScaleLayer.a, layerFactory6, integer7, contextProvider);
        layerFactory6 = NoiseToRiverLayer.INSTANCE.<T>create(contextProvider.apply(1L), layerFactory6);
        layerFactory6 = SmoothenShorelineLayer.a.<T>create(contextProvider.apply(1000L), layerFactory6);
        layerFactory7 = AddSunflowerPlainsLayer.INSTANCE.<T>create(contextProvider.apply(1001L), layerFactory7);
        for (int integer8 = 0; integer8 < integer6; ++integer8) {
            layerFactory7 = ScaleLayer.a.<T>create(contextProvider.apply(1000 + integer8), layerFactory7);
            if (integer8 == 0) {
                layerFactory7 = IncreaseEdgeCurvatureLayer.INSTANCE.<T>create(contextProvider.apply(3L), layerFactory7);
            }
            if (integer8 == 1 || integer6 == 1) {
                layerFactory7 = AddEdgeBiomesLayer.INSTANCE.<T>create(contextProvider.apply(1000L), layerFactory7);
            }
        }
        layerFactory7 = SmoothenShorelineLayer.a.<T>create(contextProvider.apply(1000L), layerFactory7);
        layerFactory7 = AddRiversLayer.a.<T>create(contextProvider.apply(100L), layerFactory7, layerFactory6);
        final LayerFactory<T> layerFactory9;
        layerFactory7 = (layerFactory9 = ApplyOceanTemperatureLayer.a.<T>create(contextProvider.apply(100L), layerFactory7, layerFactory5));
        final LayerFactory<T> layerFactory10 = CellScaleLayer.a.<T>create(contextProvider.apply(10L), layerFactory7);
        return ImmutableList.<LayerFactory<T>>of(layerFactory7, layerFactory10, layerFactory9);
    }
    
    public static BiomeLayerSampler[] build(final long seed, final LevelGeneratorType generatorType, final OverworldChunkGeneratorConfig settings) {
        final int integer5 = 25;
        final ImmutableList<LayerFactory<CachingLayerSampler>> immutableList6 = BiomeLayers.<CachingLayerSampler, CachingLayerContext>build(generatorType, settings, long3 -> new CachingLayerContext(25, seed, long3));
        final BiomeLayerSampler biomeLayerSampler7 = new BiomeLayerSampler(immutableList6.get(0));
        final BiomeLayerSampler biomeLayerSampler8 = new BiomeLayerSampler(immutableList6.get(1));
        final BiomeLayerSampler biomeLayerSampler9 = new BiomeLayerSampler(immutableList6.get(2));
        return new BiomeLayerSampler[] { biomeLayerSampler7, biomeLayerSampler8, biomeLayerSampler9 };
    }
    
    public static boolean areSimilar(final int id1, final int id2) {
        if (id1 == id2) {
            return true;
        }
        final Biome biome3 = Registry.BIOME.get(id1);
        final Biome biome4 = Registry.BIOME.get(id2);
        if (biome3 == null || biome4 == null) {
            return false;
        }
        if (biome3 == Biomes.N || biome3 == Biomes.O) {
            return biome4 == Biomes.N || biome4 == Biomes.O;
        }
        return (biome3.getCategory() != Biome.Category.NONE && biome4.getCategory() != Biome.Category.NONE && biome3.getCategory() == biome4.getCategory()) || biome3 == biome4;
    }
    
    protected static boolean isOcean(final int id) {
        return id == BiomeLayers.WARM_OCEAN_ID || id == BiomeLayers.LUKEWARM_OCEAN_ID || id == BiomeLayers.OCEAN_ID || id == BiomeLayers.COLD_OCEAN_ID || id == BiomeLayers.FROZEN_OCEAN_ID || id == BiomeLayers.DEEP_WARM_OCEAN_ID || id == BiomeLayers.DEEP_LUKEWARM_OCEAN_ID || id == BiomeLayers.DEEP_OCEAN_ID || id == BiomeLayers.DEEP_COLD_OCEAN_ID || id == BiomeLayers.DEEP_FROZEN_OCEAN_ID;
    }
    
    protected static boolean isShallowOcean(final int id) {
        return id == BiomeLayers.WARM_OCEAN_ID || id == BiomeLayers.LUKEWARM_OCEAN_ID || id == BiomeLayers.OCEAN_ID || id == BiomeLayers.COLD_OCEAN_ID || id == BiomeLayers.FROZEN_OCEAN_ID;
    }
    
    static {
        WARM_OCEAN_ID = Registry.BIOME.getRawId(Biomes.T);
        LUKEWARM_OCEAN_ID = Registry.BIOME.getRawId(Biomes.U);
        OCEAN_ID = Registry.BIOME.getRawId(Biomes.a);
        COLD_OCEAN_ID = Registry.BIOME.getRawId(Biomes.V);
        FROZEN_OCEAN_ID = Registry.BIOME.getRawId(Biomes.l);
        DEEP_WARM_OCEAN_ID = Registry.BIOME.getRawId(Biomes.W);
        DEEP_LUKEWARM_OCEAN_ID = Registry.BIOME.getRawId(Biomes.X);
        DEEP_OCEAN_ID = Registry.BIOME.getRawId(Biomes.z);
        DEEP_COLD_OCEAN_ID = Registry.BIOME.getRawId(Biomes.Y);
        DEEP_FROZEN_OCEAN_ID = Registry.BIOME.getRawId(Biomes.Z);
    }
}
