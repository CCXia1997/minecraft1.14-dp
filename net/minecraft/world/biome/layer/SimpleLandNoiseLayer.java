package net.minecraft.world.biome.layer;

public enum SimpleLandNoiseLayer implements IdentitySamplingLayer
{
    a;
    
    @Override
    public int sample(final LayerRandomnessSource context, final int value) {
        return BiomeLayers.isShallowOcean(value) ? value : (context.nextInt(299999) + 2);
    }
}
