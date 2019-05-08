package net.minecraft.world.biome.layer;

public interface CrossSamplingLayer extends ParentedLayer, NorthWestCoordinateTransformer
{
    int sample(final LayerRandomnessSource arg1, final int arg2, final int arg3, final int arg4, final int arg5, final int arg6);
    
    default int sample(final LayerSampleContext<?> context, final LayerSampler parent, final int x, final int z) {
        return this.sample(context, parent.sample(this.transformX(x + 1), this.transformZ(z + 0)), parent.sample(this.transformX(x + 2), this.transformZ(z + 1)), parent.sample(this.transformX(x + 1), this.transformZ(z + 2)), parent.sample(this.transformX(x + 0), this.transformZ(z + 1)), parent.sample(this.transformX(x + 1), this.transformZ(z + 1)));
    }
}
