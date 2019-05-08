package net.minecraft.util.math.noise;

import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class SimplexNoiseSampler
{
    protected static final int[][] gradients;
    private static final double sqrt3;
    private static final double f;
    private static final double g;
    private final int[] h;
    public final double b;
    public final double c;
    public final double d;
    
    public SimplexNoiseSampler(final Random random) {
        this.h = new int[512];
        this.b = random.nextDouble() * 256.0;
        this.c = random.nextDouble() * 256.0;
        this.d = random.nextDouble() * 256.0;
        for (int integer2 = 0; integer2 < 256; ++integer2) {
            this.h[integer2] = integer2;
        }
        for (int integer2 = 0; integer2 < 256; ++integer2) {
            final int integer3 = random.nextInt(256 - integer2);
            final int integer4 = this.h[integer2];
            this.h[integer2] = this.h[integer3 + integer2];
            this.h[integer3 + integer2] = integer4;
        }
    }
    
    private int a(final int integer) {
        return this.h[integer & 0xFF];
    }
    
    protected static double dot(final int[] gArr, final double x, final double y, final double z) {
        return gArr[0] * x + gArr[1] * y + gArr[2] * z;
    }
    
    private double a(final int integer, final double double2, final double double4, final double double6, final double double8) {
        double double9 = double8 - double2 * double2 - double4 * double4 - double6 * double6;
        double double10;
        if (double9 < 0.0) {
            double10 = 0.0;
        }
        else {
            double9 *= double9;
            double10 = double9 * double9 * dot(SimplexNoiseSampler.gradients[integer], double2, double4, double6);
        }
        return double10;
    }
    
    public double sample(final double x, final double y) {
        final double double5 = (x + y) * SimplexNoiseSampler.f;
        final int integer7 = MathHelper.floor(x + double5);
        final int integer8 = MathHelper.floor(y + double5);
        final double double6 = (integer7 + integer8) * SimplexNoiseSampler.g;
        final double double7 = integer7 - double6;
        final double double8 = integer8 - double6;
        final double double9 = x - double7;
        final double double10 = y - double8;
        int integer9;
        int integer10;
        if (double9 > double10) {
            integer9 = 1;
            integer10 = 0;
        }
        else {
            integer9 = 0;
            integer10 = 1;
        }
        final double double11 = double9 - integer9 + SimplexNoiseSampler.g;
        final double double12 = double10 - integer10 + SimplexNoiseSampler.g;
        final double double13 = double9 - 1.0 + 2.0 * SimplexNoiseSampler.g;
        final double double14 = double10 - 1.0 + 2.0 * SimplexNoiseSampler.g;
        final int integer11 = integer7 & 0xFF;
        final int integer12 = integer8 & 0xFF;
        final int integer13 = this.a(integer11 + this.a(integer12)) % 12;
        final int integer14 = this.a(integer11 + integer9 + this.a(integer12 + integer10)) % 12;
        final int integer15 = this.a(integer11 + 1 + this.a(integer12 + 1)) % 12;
        final double double15 = this.a(integer13, double9, double10, 0.0, 0.5);
        final double double16 = this.a(integer14, double11, double12, 0.0, 0.5);
        final double double17 = this.a(integer15, double13, double14, 0.0, 0.5);
        return 70.0 * (double15 + double16 + double17);
    }
    
    static {
        gradients = new int[][] { { 1, 1, 0 }, { -1, 1, 0 }, { 1, -1, 0 }, { -1, -1, 0 }, { 1, 0, 1 }, { -1, 0, 1 }, { 1, 0, -1 }, { -1, 0, -1 }, { 0, 1, 1 }, { 0, -1, 1 }, { 0, 1, -1 }, { 0, -1, -1 }, { 1, 1, 0 }, { 0, -1, 1 }, { -1, 1, 0 }, { 0, -1, -1 } };
        sqrt3 = Math.sqrt(3.0);
        f = 0.5 * (SimplexNoiseSampler.sqrt3 - 1.0);
        g = (3.0 - SimplexNoiseSampler.sqrt3) / 6.0;
    }
}
