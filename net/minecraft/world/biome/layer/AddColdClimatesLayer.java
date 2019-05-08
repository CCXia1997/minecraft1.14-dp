package net.minecraft.world.biome.layer;

public enum AddColdClimatesLayer implements SouthEastSamplingLayer
{
    a;
    
    @Override
    public int sample(final LayerRandomnessSource context, final int se) {
        if (BiomeLayers.isShallowOcean(se)) {
            return se;
        }
        final int integer3 = context.nextInt(6);
        if (integer3 == 0) {
            return 4;
        }
        if (integer3 == 1) {
            return 3;
        }
        return 1;
    }
}
