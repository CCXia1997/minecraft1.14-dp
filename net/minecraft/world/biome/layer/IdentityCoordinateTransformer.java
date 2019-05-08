package net.minecraft.world.biome.layer;

public interface IdentityCoordinateTransformer extends CoordinateTransformer
{
    default int transformX(final int x) {
        return x;
    }
    
    default int transformZ(final int y) {
        return y;
    }
}
