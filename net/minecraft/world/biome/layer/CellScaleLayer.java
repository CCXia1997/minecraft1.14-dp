package net.minecraft.world.biome.layer;

public enum CellScaleLayer implements ParentedLayer
{
    a;
    
    @Override
    public int sample(final LayerSampleContext<?> context, final LayerSampler parent, final int x, final int z) {
        final int integer5 = x - 2;
        final int integer6 = z - 2;
        final int integer7 = integer5 >> 2;
        final int integer8 = integer6 >> 2;
        final int integer9 = integer7 << 2;
        final int integer10 = integer8 << 2;
        context.initSeed(integer9, integer10);
        final double double11 = (context.nextInt(1024) / 1024.0 - 0.5) * 3.6;
        final double double12 = (context.nextInt(1024) / 1024.0 - 0.5) * 3.6;
        context.initSeed(integer9 + 4, integer10);
        final double double13 = (context.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
        final double double14 = (context.nextInt(1024) / 1024.0 - 0.5) * 3.6;
        context.initSeed(integer9, integer10 + 4);
        final double double15 = (context.nextInt(1024) / 1024.0 - 0.5) * 3.6;
        final double double16 = (context.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
        context.initSeed(integer9 + 4, integer10 + 4);
        final double double17 = (context.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
        final double double18 = (context.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
        final int integer11 = integer5 & 0x3;
        final int integer12 = integer6 & 0x3;
        final double double19 = (integer12 - double12) * (integer12 - double12) + (integer11 - double11) * (integer11 - double11);
        final double double20 = (integer12 - double14) * (integer12 - double14) + (integer11 - double13) * (integer11 - double13);
        final double double21 = (integer12 - double16) * (integer12 - double16) + (integer11 - double15) * (integer11 - double15);
        final double double22 = (integer12 - double18) * (integer12 - double18) + (integer11 - double17) * (integer11 - double17);
        if (double19 < double20 && double19 < double21 && double19 < double22) {
            return parent.sample(this.transformX(integer9), this.transformZ(integer10));
        }
        if (double20 < double19 && double20 < double21 && double20 < double22) {
            return parent.sample(this.transformX(integer9 + 4), this.transformZ(integer10)) & 0xFF;
        }
        if (double21 < double19 && double21 < double20 && double21 < double22) {
            return parent.sample(this.transformX(integer9), this.transformZ(integer10 + 4));
        }
        return parent.sample(this.transformX(integer9 + 4), this.transformZ(integer10 + 4)) & 0xFF;
    }
    
    @Override
    public int transformX(final int x) {
        return x >> 2;
    }
    
    @Override
    public int transformZ(final int y) {
        return y >> 2;
    }
}
