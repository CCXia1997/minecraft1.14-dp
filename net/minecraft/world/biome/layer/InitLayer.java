package net.minecraft.world.biome.layer;

public interface InitLayer
{
    default <R extends LayerSampler> LayerFactory<R> create(final LayerSampleContext<R> context) {
        return () -> context.createSampler((integer2, integer3) -> {
            context.initSeed(integer2, integer3);
            return this.sample(context, integer2, integer3);
        });
    }
    
    int sample(final LayerRandomnessSource arg1, final int arg2, final int arg3);
}
