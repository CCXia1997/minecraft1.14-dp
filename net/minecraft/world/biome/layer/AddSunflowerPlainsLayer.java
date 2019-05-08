package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.Biomes;
import net.minecraft.util.registry.Registry;

public enum AddSunflowerPlainsLayer implements SouthEastSamplingLayer
{
    INSTANCE;
    
    private static final int PLAINS_ID;
    private static final int SUNFLOWER_PLAINS;
    
    @Override
    public int sample(final LayerRandomnessSource context, final int se) {
        if (context.nextInt(57) == 0 && se == AddSunflowerPlainsLayer.PLAINS_ID) {
            return AddSunflowerPlainsLayer.SUNFLOWER_PLAINS;
        }
        return se;
    }
    
    static {
        PLAINS_ID = Registry.BIOME.getRawId(Biomes.c);
        SUNFLOWER_PLAINS = Registry.BIOME.getRawId(Biomes.ab);
    }
}
