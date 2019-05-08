package net.minecraft.world.biome.layer;

public class AddClimateLayers
{
    public enum AddTemperateBiomesLayer implements CrossSamplingLayer
    {
        a;
        
        @Override
        public int sample(final LayerRandomnessSource context, final int n, final int e, final int s, final int w, final int center) {
            if (center == 1 && (n == 3 || e == 3 || w == 3 || s == 3 || n == 4 || e == 4 || w == 4 || s == 4)) {
                return 2;
            }
            return center;
        }
    }
    
    public enum AddCoolBiomesLayer implements CrossSamplingLayer
    {
        a;
        
        @Override
        public int sample(final LayerRandomnessSource context, final int n, final int e, final int s, final int w, final int center) {
            if (center == 4 && (n == 1 || e == 1 || w == 1 || s == 1 || n == 2 || e == 2 || w == 2 || s == 2)) {
                return 3;
            }
            return center;
        }
    }
    
    public enum AddSpecialBiomesLayer implements IdentitySamplingLayer
    {
        a;
        
        @Override
        public int sample(final LayerRandomnessSource context, int value) {
            if (!BiomeLayers.isShallowOcean(value) && context.nextInt(13) == 0) {
                value |= (1 + context.nextInt(15) << 8 & 0xF00);
            }
            return value;
        }
    }
}
