package net.minecraft.world.biome.layer;

public interface MergingLayer extends CoordinateTransformer
{
    default <R extends LayerSampler> LayerFactory<R> create(final LayerSampleContext<R> context, final LayerFactory<R> layer1, final LayerFactory<R> layer2) {
        final LayerSampler layerSampler4;
        final LayerSampler layerSampler5;
        final LayerSampler arg2;
        final LayerSampler arg3;
        return () -> {
            layerSampler4 = layer1.make();
            layerSampler5 = layer2.make();
            return context.createSampler((integer4, integer5) -> {
                context.initSeed(integer4, integer5);
                return this.sample(context, arg2, arg3, integer4, integer5);
            }, (R)layerSampler4, (R)layerSampler5);
        };
    }
    
    int sample(final LayerRandomnessSource arg1, final LayerSampler arg2, final LayerSampler arg3, final int arg4, final int arg5);
}
