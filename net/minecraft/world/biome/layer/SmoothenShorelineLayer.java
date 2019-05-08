package net.minecraft.world.biome.layer;

public enum SmoothenShorelineLayer implements CrossSamplingLayer
{
    a;
    
    @Override
    public int sample(final LayerRandomnessSource context, final int n, final int e, final int s, final int w, final int center) {
        final boolean boolean7 = e == w;
        final boolean boolean8 = n == s;
        if (boolean7 != boolean8) {
            return boolean7 ? w : n;
        }
        if (boolean7) {
            return (context.nextInt(2) == 0) ? w : n;
        }
        return center;
    }
}
