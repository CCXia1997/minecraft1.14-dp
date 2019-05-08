package net.minecraft.world.biome.layer;

public interface IdentitySamplingLayer extends ParentedLayer, IdentityCoordinateTransformer
{
    int sample(final LayerRandomnessSource arg1, final int arg2);
    
    default int sample(final LayerSampleContext<?> context, final LayerSampler parent, final int x, final int z) {
        return this.sample(context, parent.sample(this.transformX(x), this.transformZ(z)));
    }
}
