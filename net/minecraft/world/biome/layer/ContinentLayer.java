package net.minecraft.world.biome.layer;

public enum ContinentLayer implements InitLayer
{
    a;
    
    @Override
    public int sample(final LayerRandomnessSource context, final int x, final int y) {
        if (x == 0 && y == 0) {
            return 1;
        }
        return (context.nextInt(10) == 0) ? 1 : BiomeLayers.OCEAN_ID;
    }
}
