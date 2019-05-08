package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.Biomes;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public enum EaseBiomeEdgeLayer implements CrossSamplingLayer
{
    INSTANCE;
    
    private static final int DESERT_ID;
    private static final int MOUNTAINS_ID;
    private static final int WOODED_MOUNTAINS_ID;
    private static final int SNOWY_TUNDRA_ID;
    private static final int JUNGLE_ID;
    private static final int BAMBOO_JUNGLE_ID;
    private static final int JUNGLE_EDGE_ID;
    private static final int BALDANDS_ID;
    private static final int BADLANDS_PLATEAU_ID;
    private static final int WOODED_BADLANDS_PLATEAU_ID;
    private static final int PLAINS_ID;
    private static final int GIANT_TREE_TAIGA_ID;
    private static final int MOUNTAIN_EDGE_ID;
    private static final int SWAMP_ID;
    private static final int TAIGA_ID;
    private static final int SNOWY_TAIGA_ID;
    
    @Override
    public int sample(final LayerRandomnessSource context, final int n, final int e, final int s, final int w, final int center) {
        final int[] arr7 = { 0 };
        if (this.a(arr7, n, e, s, w, center, EaseBiomeEdgeLayer.MOUNTAINS_ID, EaseBiomeEdgeLayer.MOUNTAIN_EDGE_ID) || this.b(arr7, n, e, s, w, center, EaseBiomeEdgeLayer.WOODED_BADLANDS_PLATEAU_ID, EaseBiomeEdgeLayer.BALDANDS_ID) || this.b(arr7, n, e, s, w, center, EaseBiomeEdgeLayer.BADLANDS_PLATEAU_ID, EaseBiomeEdgeLayer.BALDANDS_ID) || this.b(arr7, n, e, s, w, center, EaseBiomeEdgeLayer.GIANT_TREE_TAIGA_ID, EaseBiomeEdgeLayer.TAIGA_ID)) {
            return arr7[0];
        }
        if (center == EaseBiomeEdgeLayer.DESERT_ID && (n == EaseBiomeEdgeLayer.SNOWY_TUNDRA_ID || e == EaseBiomeEdgeLayer.SNOWY_TUNDRA_ID || w == EaseBiomeEdgeLayer.SNOWY_TUNDRA_ID || s == EaseBiomeEdgeLayer.SNOWY_TUNDRA_ID)) {
            return EaseBiomeEdgeLayer.WOODED_MOUNTAINS_ID;
        }
        if (center == EaseBiomeEdgeLayer.SWAMP_ID) {
            if (n == EaseBiomeEdgeLayer.DESERT_ID || e == EaseBiomeEdgeLayer.DESERT_ID || w == EaseBiomeEdgeLayer.DESERT_ID || s == EaseBiomeEdgeLayer.DESERT_ID || n == EaseBiomeEdgeLayer.SNOWY_TAIGA_ID || e == EaseBiomeEdgeLayer.SNOWY_TAIGA_ID || w == EaseBiomeEdgeLayer.SNOWY_TAIGA_ID || s == EaseBiomeEdgeLayer.SNOWY_TAIGA_ID || n == EaseBiomeEdgeLayer.SNOWY_TUNDRA_ID || e == EaseBiomeEdgeLayer.SNOWY_TUNDRA_ID || w == EaseBiomeEdgeLayer.SNOWY_TUNDRA_ID || s == EaseBiomeEdgeLayer.SNOWY_TUNDRA_ID) {
                return EaseBiomeEdgeLayer.PLAINS_ID;
            }
            if (n == EaseBiomeEdgeLayer.JUNGLE_ID || s == EaseBiomeEdgeLayer.JUNGLE_ID || e == EaseBiomeEdgeLayer.JUNGLE_ID || w == EaseBiomeEdgeLayer.JUNGLE_ID || n == EaseBiomeEdgeLayer.BAMBOO_JUNGLE_ID || s == EaseBiomeEdgeLayer.BAMBOO_JUNGLE_ID || e == EaseBiomeEdgeLayer.BAMBOO_JUNGLE_ID || w == EaseBiomeEdgeLayer.BAMBOO_JUNGLE_ID) {
                return EaseBiomeEdgeLayer.JUNGLE_EDGE_ID;
            }
        }
        return center;
    }
    
    private boolean a(final int[] arr, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final int integer8) {
        if (!BiomeLayers.areSimilar(integer6, integer7)) {
            return false;
        }
        if (this.a(integer2, integer7) && this.a(integer3, integer7) && this.a(integer5, integer7) && this.a(integer4, integer7)) {
            arr[0] = integer6;
        }
        else {
            arr[0] = integer8;
        }
        return true;
    }
    
    private boolean b(final int[] arr, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final int integer8) {
        if (integer6 != integer7) {
            return false;
        }
        if (BiomeLayers.areSimilar(integer2, integer7) && BiomeLayers.areSimilar(integer3, integer7) && BiomeLayers.areSimilar(integer5, integer7) && BiomeLayers.areSimilar(integer4, integer7)) {
            arr[0] = integer6;
        }
        else {
            arr[0] = integer8;
        }
        return true;
    }
    
    private boolean a(final int integer1, final int integer2) {
        if (BiomeLayers.areSimilar(integer1, integer2)) {
            return true;
        }
        final Biome biome3 = Registry.BIOME.get(integer1);
        final Biome biome4 = Registry.BIOME.get(integer2);
        if (biome3 != null && biome4 != null) {
            final Biome.TemperatureGroup temperatureGroup5 = biome3.getTemperatureGroup();
            final Biome.TemperatureGroup temperatureGroup6 = biome4.getTemperatureGroup();
            return temperatureGroup5 == temperatureGroup6 || temperatureGroup5 == Biome.TemperatureGroup.MEDIUM || temperatureGroup6 == Biome.TemperatureGroup.MEDIUM;
        }
        return false;
    }
    
    static {
        DESERT_ID = Registry.BIOME.getRawId(Biomes.d);
        MOUNTAINS_ID = Registry.BIOME.getRawId(Biomes.e);
        WOODED_MOUNTAINS_ID = Registry.BIOME.getRawId(Biomes.J);
        SNOWY_TUNDRA_ID = Registry.BIOME.getRawId(Biomes.n);
        JUNGLE_ID = Registry.BIOME.getRawId(Biomes.w);
        BAMBOO_JUNGLE_ID = Registry.BIOME.getRawId(Biomes.aw);
        JUNGLE_EDGE_ID = Registry.BIOME.getRawId(Biomes.y);
        BALDANDS_ID = Registry.BIOME.getRawId(Biomes.M);
        BADLANDS_PLATEAU_ID = Registry.BIOME.getRawId(Biomes.O);
        WOODED_BADLANDS_PLATEAU_ID = Registry.BIOME.getRawId(Biomes.N);
        PLAINS_ID = Registry.BIOME.getRawId(Biomes.c);
        GIANT_TREE_TAIGA_ID = Registry.BIOME.getRawId(Biomes.H);
        MOUNTAIN_EDGE_ID = Registry.BIOME.getRawId(Biomes.v);
        SWAMP_ID = Registry.BIOME.getRawId(Biomes.h);
        TAIGA_ID = Registry.BIOME.getRawId(Biomes.g);
        SNOWY_TAIGA_ID = Registry.BIOME.getRawId(Biomes.F);
    }
}
