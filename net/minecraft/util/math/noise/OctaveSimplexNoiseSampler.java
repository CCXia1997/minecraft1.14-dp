package net.minecraft.util.math.noise;

import java.util.Random;

public class OctaveSimplexNoiseSampler implements NoiseSampler
{
    private final SimplexNoiseSampler[] octaveSamplers;
    private final int octaveCount;
    
    public OctaveSimplexNoiseSampler(final Random random, final int octaveCount) {
        this.octaveCount = octaveCount;
        this.octaveSamplers = new SimplexNoiseSampler[octaveCount];
        for (int integer3 = 0; integer3 < octaveCount; ++integer3) {
            this.octaveSamplers[integer3] = new SimplexNoiseSampler(random);
        }
    }
    
    public double sample(final double x, final double y) {
        return this.sample(x, y, false);
    }
    
    public double sample(final double x, final double y, final boolean boolean5) {
        double double6 = 0.0;
        double double7 = 1.0;
        for (int integer10 = 0; integer10 < this.octaveCount; ++integer10) {
            double6 += this.octaveSamplers[integer10].sample(x * double7 + (boolean5 ? this.octaveSamplers[integer10].b : 0.0), y * double7 + (boolean5 ? this.octaveSamplers[integer10].c : 0.0)) / double7;
            double7 /= 2.0;
        }
        return double6;
    }
    
    @Override
    public double sample(final double x, final double y, final double double5, final double double7) {
        return this.sample(x, y, true) * 0.55;
    }
}
