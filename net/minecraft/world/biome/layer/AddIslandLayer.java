package net.minecraft.world.biome.layer;

public enum AddIslandLayer implements CrossSamplingLayer
{
    a;
    
    @Override
    public int sample(final LayerRandomnessSource context, final int n, final int e, final int s, final int w, final int center) {
        if (BiomeLayers.isShallowOcean(center) && BiomeLayers.isShallowOcean(n) && BiomeLayers.isShallowOcean(e) && BiomeLayers.isShallowOcean(w) && BiomeLayers.isShallowOcean(s) && context.nextInt(2) == 0) {
            return 1;
        }
        return center;
    }
}
