package net.minecraft.world.biome.layer;

public enum AddDeepOceanLayer implements CrossSamplingLayer
{
    a;
    
    @Override
    public int sample(final LayerRandomnessSource context, final int n, final int e, final int s, final int w, final int center) {
        if (BiomeLayers.isShallowOcean(center)) {
            int integer7 = 0;
            if (BiomeLayers.isShallowOcean(n)) {
                ++integer7;
            }
            if (BiomeLayers.isShallowOcean(e)) {
                ++integer7;
            }
            if (BiomeLayers.isShallowOcean(w)) {
                ++integer7;
            }
            if (BiomeLayers.isShallowOcean(s)) {
                ++integer7;
            }
            if (integer7 > 3) {
                if (center == BiomeLayers.WARM_OCEAN_ID) {
                    return BiomeLayers.DEEP_WARM_OCEAN_ID;
                }
                if (center == BiomeLayers.LUKEWARM_OCEAN_ID) {
                    return BiomeLayers.DEEP_LUKEWARM_OCEAN_ID;
                }
                if (center == BiomeLayers.OCEAN_ID) {
                    return BiomeLayers.DEEP_OCEAN_ID;
                }
                if (center == BiomeLayers.COLD_OCEAN_ID) {
                    return BiomeLayers.DEEP_COLD_OCEAN_ID;
                }
                if (center == BiomeLayers.FROZEN_OCEAN_ID) {
                    return BiomeLayers.DEEP_FROZEN_OCEAN_ID;
                }
                return BiomeLayers.DEEP_OCEAN_ID;
            }
        }
        return center;
    }
}
