package net.minecraft.world.biome.layer;

public interface NorthWestCoordinateTransformer extends CoordinateTransformer
{
    default int transformX(final int x) {
        return x - 1;
    }
    
    default int transformZ(final int y) {
        return y - 1;
    }
}
