package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.Biomes;
import net.minecraft.util.registry.Registry;

public enum AddMushroomIslandLayer implements DiagonalCrossSamplingLayer
{
    INSTANCE;
    
    private static final int MUSHROOM_FIELDS_ID;
    
    @Override
    public int sample(final LayerRandomnessSource context, final int sw, final int se, final int ne, final int nw, final int center) {
        if (BiomeLayers.isShallowOcean(center) && BiomeLayers.isShallowOcean(nw) && BiomeLayers.isShallowOcean(sw) && BiomeLayers.isShallowOcean(ne) && BiomeLayers.isShallowOcean(se) && context.nextInt(100) == 0) {
            return AddMushroomIslandLayer.MUSHROOM_FIELDS_ID;
        }
        return center;
    }
    
    static {
        MUSHROOM_FIELDS_ID = Registry.BIOME.getRawId(Biomes.p);
    }
}
