package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.Biomes;
import net.minecraft.util.registry.Registry;

public enum AddBambooJungleLayer implements SouthEastSamplingLayer
{
    INSTANCE;
    
    private static final int JUNGLE_ID;
    private static final int BAMBOO_JUNGLE_ID;
    
    @Override
    public int sample(final LayerRandomnessSource context, final int se) {
        if (context.nextInt(10) == 0 && se == AddBambooJungleLayer.JUNGLE_ID) {
            return AddBambooJungleLayer.BAMBOO_JUNGLE_ID;
        }
        return se;
    }
    
    static {
        JUNGLE_ID = Registry.BIOME.getRawId(Biomes.w);
        BAMBOO_JUNGLE_ID = Registry.BIOME.getRawId(Biomes.aw);
    }
}
