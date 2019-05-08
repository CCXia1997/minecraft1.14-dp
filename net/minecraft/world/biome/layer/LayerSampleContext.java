package net.minecraft.world.biome.layer;

public interface LayerSampleContext<R extends LayerSampler> extends LayerRandomnessSource
{
    void initSeed(final long arg1, final long arg2);
    
    R createSampler(final LayerOperator arg1);
    
    default R createSampler(final LayerOperator operator, final R parent) {
        return this.createSampler(operator);
    }
    
    default R createSampler(final LayerOperator operator, final R layerSampler2, final R layerSampler3) {
        return this.createSampler(operator);
    }
    
    default int choose(final int a, final int b) {
        return (this.nextInt(2) == 0) ? a : b;
    }
    
    default int choose(final int a, final int b, final int c, final int d) {
        final int integer5 = this.nextInt(4);
        if (integer5 == 0) {
            return a;
        }
        if (integer5 == 1) {
            return b;
        }
        if (integer5 == 2) {
            return c;
        }
        return d;
    }
}
