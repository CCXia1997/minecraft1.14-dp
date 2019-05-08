package net.minecraft.world.biome.layer;

public enum IncreaseEdgeCurvatureLayer implements DiagonalCrossSamplingLayer
{
    INSTANCE;
    
    @Override
    public int sample(final LayerRandomnessSource context, final int sw, final int se, final int ne, final int nw, final int center) {
        if (!BiomeLayers.isShallowOcean(center) || (BiomeLayers.isShallowOcean(nw) && BiomeLayers.isShallowOcean(ne) && BiomeLayers.isShallowOcean(sw) && BiomeLayers.isShallowOcean(se))) {
            if (!BiomeLayers.isShallowOcean(center) && (BiomeLayers.isShallowOcean(nw) || BiomeLayers.isShallowOcean(sw) || BiomeLayers.isShallowOcean(ne) || BiomeLayers.isShallowOcean(se)) && context.nextInt(5) == 0) {
                if (BiomeLayers.isShallowOcean(nw)) {
                    return (center == 4) ? 4 : nw;
                }
                if (BiomeLayers.isShallowOcean(sw)) {
                    return (center == 4) ? 4 : sw;
                }
                if (BiomeLayers.isShallowOcean(ne)) {
                    return (center == 4) ? 4 : ne;
                }
                if (BiomeLayers.isShallowOcean(se)) {
                    return (center == 4) ? 4 : se;
                }
            }
            return center;
        }
        int integer7 = 1;
        int integer8 = 1;
        if (!BiomeLayers.isShallowOcean(nw) && context.nextInt(integer7++) == 0) {
            integer8 = nw;
        }
        if (!BiomeLayers.isShallowOcean(ne) && context.nextInt(integer7++) == 0) {
            integer8 = ne;
        }
        if (!BiomeLayers.isShallowOcean(sw) && context.nextInt(integer7++) == 0) {
            integer8 = sw;
        }
        if (!BiomeLayers.isShallowOcean(se) && context.nextInt(integer7++) == 0) {
            integer8 = se;
        }
        if (context.nextInt(3) == 0) {
            return integer8;
        }
        return (integer8 == 4) ? 4 : center;
    }
}
