package net.minecraft.util.math.noise;

import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class OctavePerlinNoiseSampler implements NoiseSampler
{
    private final PerlinNoiseSampler[] octaveSamplers;
    
    public OctavePerlinNoiseSampler(final Random random, final int octaveCount) {
        this.octaveSamplers = new PerlinNoiseSampler[octaveCount];
        for (int integer3 = 0; integer3 < octaveCount; ++integer3) {
            this.octaveSamplers[integer3] = new PerlinNoiseSampler(random);
        }
    }
    
    public double sample(final double double1, final double double3, final double double5) {
        return this.sample(double1, double3, double5, 0.0, 0.0, false);
    }
    
    public double sample(final double x, final double y, final double double5, final double double7, final double double9, final boolean boolean11) {
        double double10 = 0.0;
        double double11 = 1.0;
        for (final PerlinNoiseSampler perlinNoiseSampler19 : this.octaveSamplers) {
            double10 += perlinNoiseSampler19.sample(maintainPrecision(x * double11), boolean11 ? (-perlinNoiseSampler19.originY) : maintainPrecision(y * double11), maintainPrecision(double5 * double11), double7 * double11, double9 * double11) / double11;
            double11 /= 2.0;
        }
        return double10;
    }
    
    public PerlinNoiseSampler getOctave(final int octave) {
        return this.octaveSamplers[octave];
    }
    
    public static double maintainPrecision(final double double1) {
        return double1 - MathHelper.lfloor(double1 / 3.3554432E7 + 0.5) * 3.3554432E7;
    }
    
    @Override
    public double sample(final double x, final double y, final double double5, final double double7) {
        return this.sample(x, y, 0.0, double5, double7, false);
    }
}
