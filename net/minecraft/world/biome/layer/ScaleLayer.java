package net.minecraft.world.biome.layer;

public enum ScaleLayer implements ParentedLayer
{
    a, 
    b {
        @Override
        protected int a(final LayerSampleContext<?> layerSampleContext, final int integer2, final int integer3, final int integer4, final int integer5) {
            return layerSampleContext.choose(integer2, integer3, integer4, integer5);
        }
    };
    
    @Override
    public int transformX(final int x) {
        return x >> 1;
    }
    
    @Override
    public int transformZ(final int y) {
        return y >> 1;
    }
    
    @Override
    public int sample(final LayerSampleContext<?> context, final LayerSampler parent, final int x, final int z) {
        final int integer5 = parent.sample(this.transformX(x), this.transformZ(z));
        context.initSeed(x >> 1 << 1, z >> 1 << 1);
        final int integer6 = x & 0x1;
        final int integer7 = z & 0x1;
        if (integer6 == 0 && integer7 == 0) {
            return integer5;
        }
        final int integer8 = parent.sample(this.transformX(x), this.transformZ(z + 1));
        final int integer9 = context.choose(integer5, integer8);
        if (integer6 == 0 && integer7 == 1) {
            return integer9;
        }
        final int integer10 = parent.sample(this.transformX(x + 1), this.transformZ(z));
        final int integer11 = context.choose(integer5, integer10);
        if (integer6 == 1 && integer7 == 0) {
            return integer11;
        }
        final int integer12 = parent.sample(this.transformX(x + 1), this.transformZ(z + 1));
        return this.a(context, integer5, integer10, integer8, integer12);
    }
    
    protected int a(final LayerSampleContext<?> layerSampleContext, final int integer2, final int integer3, final int integer4, final int integer5) {
        if (integer3 == integer4 && integer4 == integer5) {
            return integer3;
        }
        if (integer2 == integer3 && integer2 == integer4) {
            return integer2;
        }
        if (integer2 == integer3 && integer2 == integer5) {
            return integer2;
        }
        if (integer2 == integer4 && integer2 == integer5) {
            return integer2;
        }
        if (integer2 == integer3 && integer4 != integer5) {
            return integer2;
        }
        if (integer2 == integer4 && integer3 != integer5) {
            return integer2;
        }
        if (integer2 == integer5 && integer3 != integer4) {
            return integer2;
        }
        if (integer3 == integer4 && integer2 != integer5) {
            return integer3;
        }
        if (integer3 == integer5 && integer2 != integer4) {
            return integer3;
        }
        if (integer4 == integer5 && integer2 != integer3) {
            return integer4;
        }
        return layerSampleContext.choose(integer2, integer3, integer4, integer5);
    }
}
