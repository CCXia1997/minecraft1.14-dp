package net.minecraft.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class MetricsData
{
    private final long[] samples;
    private int startIndex;
    private int sampleCount;
    private int writeIndex;
    
    public MetricsData() {
        this.samples = new long[240];
    }
    
    public void pushSample(final long time) {
        this.samples[this.writeIndex] = time;
        ++this.writeIndex;
        if (this.writeIndex == 240) {
            this.writeIndex = 0;
        }
        if (this.sampleCount < 240) {
            this.startIndex = 0;
            ++this.sampleCount;
        }
        else {
            this.startIndex = this.wrapIndex(this.writeIndex + 1);
        }
    }
    
    @Environment(EnvType.CLIENT)
    public int a(final long long1, final int integer3, final int integer4) {
        final double double5 = long1 / (double)(1000000000L / integer4);
        return (int)(double5 * integer3);
    }
    
    @Environment(EnvType.CLIENT)
    public int getStartIndex() {
        return this.startIndex;
    }
    
    @Environment(EnvType.CLIENT)
    public int getCurrentIndex() {
        return this.writeIndex;
    }
    
    public int wrapIndex(final int index) {
        return index % 240;
    }
    
    @Environment(EnvType.CLIENT)
    public long[] getSamples() {
        return this.samples;
    }
}
