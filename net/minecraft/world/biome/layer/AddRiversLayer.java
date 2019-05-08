package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.Biomes;
import net.minecraft.util.registry.Registry;

public enum AddRiversLayer implements MergingLayer, IdentityCoordinateTransformer
{
    a;
    
    private static final int FROZEN_RIVER_ID;
    private static final int SNOWY_TUNDRA_ID;
    private static final int MUSHROOM_FIELDS_ID;
    private static final int MUSHROOM_FIELD_SHORE_ID;
    private static final int RIVER_ID;
    
    @Override
    public int sample(final LayerRandomnessSource context, final LayerSampler sampler1, final LayerSampler sampler2, final int x, final int z) {
        final int integer6 = sampler1.sample(this.transformX(x), this.transformZ(z));
        final int integer7 = sampler2.sample(this.transformX(x), this.transformZ(z));
        if (BiomeLayers.isOcean(integer6)) {
            return integer6;
        }
        if (integer7 != AddRiversLayer.RIVER_ID) {
            return integer6;
        }
        if (integer6 == AddRiversLayer.SNOWY_TUNDRA_ID) {
            return AddRiversLayer.FROZEN_RIVER_ID;
        }
        if (integer6 == AddRiversLayer.MUSHROOM_FIELDS_ID || integer6 == AddRiversLayer.MUSHROOM_FIELD_SHORE_ID) {
            return AddRiversLayer.MUSHROOM_FIELD_SHORE_ID;
        }
        return integer7 & 0xFF;
    }
    
    static {
        FROZEN_RIVER_ID = Registry.BIOME.getRawId(Biomes.m);
        SNOWY_TUNDRA_ID = Registry.BIOME.getRawId(Biomes.n);
        MUSHROOM_FIELDS_ID = Registry.BIOME.getRawId(Biomes.p);
        MUSHROOM_FIELD_SHORE_ID = Registry.BIOME.getRawId(Biomes.q);
        RIVER_ID = Registry.BIOME.getRawId(Biomes.i);
    }
}
