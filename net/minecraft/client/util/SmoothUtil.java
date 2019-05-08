package net.minecraft.client.util;

import net.minecraft.util.math.MathHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SmoothUtil
{
    private double a;
    private double b;
    private double c;
    
    public double smooth(final double double1, final double double3) {
        this.a += double1;
        double double4 = this.a - this.b;
        final double double5 = MathHelper.lerp(0.5, this.c, double4);
        final double double6 = Math.signum(double4);
        if (double6 * double4 > double6 * this.c) {
            double4 = double5;
        }
        this.c = double5;
        this.b += double4 * double3;
        return double4 * double3;
    }
    
    public void clear() {
        this.a = 0.0;
        this.b = 0.0;
        this.c = 0.0;
    }
}
