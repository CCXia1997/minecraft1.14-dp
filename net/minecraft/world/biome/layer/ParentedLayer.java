package net.minecraft.world.biome.layer;

public interface ParentedLayer extends CoordinateTransformer
{
    default <R extends LayerSampler> LayerFactory<R> create(final LayerSampleContext<R> context, final LayerFactory<R> parent) {
        final LayerSampler layerSampler3;
        final LayerSampler arg2;
        return () -> {
            layerSampler3 = parent.make();
            return context.createSampler((integer3, integer4) -> {
                context.initSeed(integer3, integer4);
                return this.sample(context, arg2, integer3, integer4);
            }, (R)layerSampler3);
        };
    }
    
    int sample(final LayerSampleContext<?> arg1, final LayerSampler arg2, final int arg3, final int arg4);
}
