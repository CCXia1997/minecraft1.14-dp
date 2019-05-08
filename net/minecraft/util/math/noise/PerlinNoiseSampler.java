package net.minecraft.util.math.noise;

import net.minecraft.util.math.MathHelper;
import java.util.Random;

public final class PerlinNoiseSampler
{
    private final byte[] d;
    public final double originX;
    public final double originY;
    public final double originZ;
    
    public PerlinNoiseSampler(final Random random) {
        this.originX = random.nextDouble() * 256.0;
        this.originY = random.nextDouble() * 256.0;
        this.originZ = random.nextDouble() * 256.0;
        this.d = new byte[256];
        for (int integer2 = 0; integer2 < 256; ++integer2) {
            this.d[integer2] = (byte)integer2;
        }
        for (int integer2 = 0; integer2 < 256; ++integer2) {
            final int integer3 = random.nextInt(256 - integer2);
            final byte byte4 = this.d[integer2];
            this.d[integer2] = this.d[integer2 + integer3];
            this.d[integer2 + integer3] = byte4;
        }
    }
    
    public double sample(final double x, final double y, final double z, final double double7, final double double9) {
        final double double10 = x + this.originX;
        final double double11 = y + this.originY;
        final double double12 = z + this.originZ;
        final int integer17 = MathHelper.floor(double10);
        final int integer18 = MathHelper.floor(double11);
        final int integer19 = MathHelper.floor(double12);
        final double double13 = double10 - integer17;
        final double double14 = double11 - integer18;
        final double double15 = double12 - integer19;
        final double double16 = MathHelper.perlinFade(double13);
        final double double17 = MathHelper.perlinFade(double14);
        final double double18 = MathHelper.perlinFade(double15);
        double double20;
        if (double7 != 0.0) {
            final double double19 = Math.min(double9, double14);
            double20 = MathHelper.floor(double19 / double7) * double7;
        }
        else {
            double20 = 0.0;
        }
        return this.a(integer17, integer18, integer19, double13, double14 - double20, double15, double16, double17, double18);
    }
    
    private static double a(final int integer, final double double2, final double double4, final double double6) {
        final int integer2 = integer & 0xF;
        return SimplexNoiseSampler.dot(SimplexNoiseSampler.gradients[integer2], double2, double4, double6);
    }
    
    private int a(final int integer) {
        return this.d[integer & 0xFF] & 0xFF;
    }
    
    public double a(final int integer1, final int integer2, final int integer3, final double double4, final double double6, final double double8, final double double10, final double double12, final double double14) {
        final int integer4 = this.a(integer1) + integer2;
        final int integer5 = this.a(integer4) + integer3;
        final int integer6 = this.a(integer4 + 1) + integer3;
        final int integer7 = this.a(integer1 + 1) + integer2;
        final int integer8 = this.a(integer7) + integer3;
        final int integer9 = this.a(integer7 + 1) + integer3;
        final double double15 = a(this.a(integer5), double4, double6, double8);
        final double double16 = a(this.a(integer8), double4 - 1.0, double6, double8);
        final double double17 = a(this.a(integer6), double4, double6 - 1.0, double8);
        final double double18 = a(this.a(integer9), double4 - 1.0, double6 - 1.0, double8);
        final double double19 = a(this.a(integer5 + 1), double4, double6, double8 - 1.0);
        final double double20 = a(this.a(integer8 + 1), double4 - 1.0, double6, double8 - 1.0);
        final double double21 = a(this.a(integer6 + 1), double4, double6 - 1.0, double8 - 1.0);
        final double double22 = a(this.a(integer9 + 1), double4 - 1.0, double6 - 1.0, double8 - 1.0);
        return MathHelper.lerp3(double10, double12, double14, double15, double16, double17, double18, double19, double20, double21, double22);
    }
}
