package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.Biomes;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public enum AddEdgeBiomesLayer implements CrossSamplingLayer
{
    INSTANCE;
    
    private static final int BEACH_ID;
    private static final int SNOWY_BEACH_ID;
    private static final int DESERT_ID;
    private static final int MOUNTAINS_ID;
    private static final int WOODED_MOUNTAINS_ID;
    private static final int FOREST_ID;
    private static final int JUNGLE_ID;
    private static final int JUNGLE_EDGE_ID;
    private static final int JUNGLE_HILLS_ID;
    private static final int BADLANDS_ID;
    private static final int WOODED_BADLANDS_PLATEAU_ID;
    private static final int BADLANDS_PLATEAU_ID;
    private static final int ERODED_BADLANDS_ID;
    private static final int MODIFIED_WOODED_BADLANDS_PLATEAU_ID;
    private static final int MODIFIED_BADLANDS_PLATEAU_ID;
    private static final int MUSHROOM_FIELDS_ID;
    private static final int MUSHROOM_FIELD_SHORE_ID;
    private static final int RIVER_ID;
    private static final int MOUNTAIN_EDGE_ID;
    private static final int STONE_SHORE_ID;
    private static final int SWAMP_ID;
    private static final int TAIGA_ID;
    
    @Override
    public int sample(final LayerRandomnessSource context, final int n, final int e, final int s, final int w, final int center) {
        final Biome biome7 = Registry.BIOME.get(center);
        if (center == AddEdgeBiomesLayer.MUSHROOM_FIELDS_ID) {
            if (BiomeLayers.isShallowOcean(n) || BiomeLayers.isShallowOcean(e) || BiomeLayers.isShallowOcean(s) || BiomeLayers.isShallowOcean(w)) {
                return AddEdgeBiomesLayer.MUSHROOM_FIELD_SHORE_ID;
            }
        }
        else if (biome7 != null && biome7.getCategory() == Biome.Category.JUNGLE) {
            if (!isWooded(n) || !isWooded(e) || !isWooded(s) || !isWooded(w)) {
                return AddEdgeBiomesLayer.JUNGLE_EDGE_ID;
            }
            if (BiomeLayers.isOcean(n) || BiomeLayers.isOcean(e) || BiomeLayers.isOcean(s) || BiomeLayers.isOcean(w)) {
                return AddEdgeBiomesLayer.BEACH_ID;
            }
        }
        else if (center == AddEdgeBiomesLayer.MOUNTAINS_ID || center == AddEdgeBiomesLayer.WOODED_MOUNTAINS_ID || center == AddEdgeBiomesLayer.MOUNTAIN_EDGE_ID) {
            if (!BiomeLayers.isOcean(center) && (BiomeLayers.isOcean(n) || BiomeLayers.isOcean(e) || BiomeLayers.isOcean(s) || BiomeLayers.isOcean(w))) {
                return AddEdgeBiomesLayer.STONE_SHORE_ID;
            }
        }
        else if (biome7 != null && biome7.getPrecipitation() == Biome.Precipitation.SNOW) {
            if (!BiomeLayers.isOcean(center) && (BiomeLayers.isOcean(n) || BiomeLayers.isOcean(e) || BiomeLayers.isOcean(s) || BiomeLayers.isOcean(w))) {
                return AddEdgeBiomesLayer.SNOWY_BEACH_ID;
            }
        }
        else if (center == AddEdgeBiomesLayer.BADLANDS_ID || center == AddEdgeBiomesLayer.WOODED_BADLANDS_PLATEAU_ID) {
            if (!BiomeLayers.isOcean(n) && !BiomeLayers.isOcean(e) && !BiomeLayers.isOcean(s) && !BiomeLayers.isOcean(w) && (!this.isBadlands(n) || !this.isBadlands(e) || !this.isBadlands(s) || !this.isBadlands(w))) {
                return AddEdgeBiomesLayer.DESERT_ID;
            }
        }
        else if (!BiomeLayers.isOcean(center) && center != AddEdgeBiomesLayer.RIVER_ID && center != AddEdgeBiomesLayer.SWAMP_ID && (BiomeLayers.isOcean(n) || BiomeLayers.isOcean(e) || BiomeLayers.isOcean(s) || BiomeLayers.isOcean(w))) {
            return AddEdgeBiomesLayer.BEACH_ID;
        }
        return center;
    }
    
    private static boolean isWooded(final int id) {
        return (Registry.BIOME.get(id) != null && Registry.BIOME.get(id).getCategory() == Biome.Category.JUNGLE) || id == AddEdgeBiomesLayer.JUNGLE_EDGE_ID || id == AddEdgeBiomesLayer.JUNGLE_ID || id == AddEdgeBiomesLayer.JUNGLE_HILLS_ID || id == AddEdgeBiomesLayer.FOREST_ID || id == AddEdgeBiomesLayer.TAIGA_ID || BiomeLayers.isOcean(id);
    }
    
    private boolean isBadlands(final int id) {
        return id == AddEdgeBiomesLayer.BADLANDS_ID || id == AddEdgeBiomesLayer.WOODED_BADLANDS_PLATEAU_ID || id == AddEdgeBiomesLayer.BADLANDS_PLATEAU_ID || id == AddEdgeBiomesLayer.ERODED_BADLANDS_ID || id == AddEdgeBiomesLayer.MODIFIED_WOODED_BADLANDS_PLATEAU_ID || id == AddEdgeBiomesLayer.MODIFIED_BADLANDS_PLATEAU_ID;
    }
    
    static {
        BEACH_ID = Registry.BIOME.getRawId(Biomes.r);
        SNOWY_BEACH_ID = Registry.BIOME.getRawId(Biomes.B);
        DESERT_ID = Registry.BIOME.getRawId(Biomes.d);
        MOUNTAINS_ID = Registry.BIOME.getRawId(Biomes.e);
        WOODED_MOUNTAINS_ID = Registry.BIOME.getRawId(Biomes.J);
        FOREST_ID = Registry.BIOME.getRawId(Biomes.f);
        JUNGLE_ID = Registry.BIOME.getRawId(Biomes.w);
        JUNGLE_EDGE_ID = Registry.BIOME.getRawId(Biomes.y);
        JUNGLE_HILLS_ID = Registry.BIOME.getRawId(Biomes.x);
        BADLANDS_ID = Registry.BIOME.getRawId(Biomes.M);
        WOODED_BADLANDS_PLATEAU_ID = Registry.BIOME.getRawId(Biomes.N);
        BADLANDS_PLATEAU_ID = Registry.BIOME.getRawId(Biomes.O);
        ERODED_BADLANDS_ID = Registry.BIOME.getRawId(Biomes.at);
        MODIFIED_WOODED_BADLANDS_PLATEAU_ID = Registry.BIOME.getRawId(Biomes.au);
        MODIFIED_BADLANDS_PLATEAU_ID = Registry.BIOME.getRawId(Biomes.av);
        MUSHROOM_FIELDS_ID = Registry.BIOME.getRawId(Biomes.p);
        MUSHROOM_FIELD_SHORE_ID = Registry.BIOME.getRawId(Biomes.q);
        RIVER_ID = Registry.BIOME.getRawId(Biomes.i);
        MOUNTAIN_EDGE_ID = Registry.BIOME.getRawId(Biomes.v);
        STONE_SHORE_ID = Registry.BIOME.getRawId(Biomes.A);
        SWAMP_ID = Registry.BIOME.getRawId(Biomes.h);
        TAIGA_ID = Registry.BIOME.getRawId(Biomes.g);
    }
}
