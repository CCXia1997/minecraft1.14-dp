package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.Biomes;
import net.minecraft.util.registry.Registry;

public enum NoiseToRiverLayer implements CrossSamplingLayer
{
    INSTANCE;
    
    public static final int RIVER_ID;
    
    @Override
    public int sample(final LayerRandomnessSource context, final int n, final int e, final int s, final int w, final int center) {
        final int integer7 = isValidForRiver(center);
        if (integer7 == isValidForRiver(w) && integer7 == isValidForRiver(n) && integer7 == isValidForRiver(e) && integer7 == isValidForRiver(s)) {
            return -1;
        }
        return NoiseToRiverLayer.RIVER_ID;
    }
    
    private static int isValidForRiver(final int value) {
        if (value >= 2) {
            return 2 + (value & 0x1);
        }
        return value;
    }
    
    static {
        RIVER_ID = Registry.BIOME.getRawId(Biomes.i);
    }
}
