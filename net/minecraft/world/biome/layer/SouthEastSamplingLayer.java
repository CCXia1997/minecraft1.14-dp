package net.minecraft.world.biome.layer;

public interface SouthEastSamplingLayer extends ParentedLayer, NorthWestCoordinateTransformer
{
    int sample(final LayerRandomnessSource arg1, final int arg2);
    
    default int sample(final LayerSampleContext<?> context, final LayerSampler parent, final int x, final int z) {
        final int integer5 = parent.sample(this.transformX(x + 1), this.transformZ(z + 1));
        return this.sample(context, integer5);
    }
}
