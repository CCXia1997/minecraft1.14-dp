package net.minecraft.world.biome.layer;

public enum ApplyOceanTemperatureLayer implements MergingLayer, IdentityCoordinateTransformer
{
    a;
    
    @Override
    public int sample(final LayerRandomnessSource context, final LayerSampler sampler1, final LayerSampler sampler2, final int x, final int z) {
        final int integer6 = sampler1.sample(this.transformX(x), this.transformZ(z));
        final int integer7 = sampler2.sample(this.transformX(x), this.transformZ(z));
        if (!BiomeLayers.isOcean(integer6)) {
            return integer6;
        }
        final int integer8 = 8;
        final int integer9 = 4;
        for (int integer10 = -8; integer10 <= 8; integer10 += 4) {
            for (int integer11 = -8; integer11 <= 8; integer11 += 4) {
                final int integer12 = sampler1.sample(this.transformX(x + integer10), this.transformZ(z + integer11));
                if (!BiomeLayers.isOcean(integer12)) {
                    if (integer7 == BiomeLayers.WARM_OCEAN_ID) {
                        return BiomeLayers.LUKEWARM_OCEAN_ID;
                    }
                    if (integer7 == BiomeLayers.FROZEN_OCEAN_ID) {
                        return BiomeLayers.COLD_OCEAN_ID;
                    }
                }
            }
        }
        if (integer6 == BiomeLayers.DEEP_OCEAN_ID) {
            if (integer7 == BiomeLayers.LUKEWARM_OCEAN_ID) {
                return BiomeLayers.DEEP_LUKEWARM_OCEAN_ID;
            }
            if (integer7 == BiomeLayers.OCEAN_ID) {
                return BiomeLayers.DEEP_OCEAN_ID;
            }
            if (integer7 == BiomeLayers.COLD_OCEAN_ID) {
                return BiomeLayers.DEEP_COLD_OCEAN_ID;
            }
            if (integer7 == BiomeLayers.FROZEN_OCEAN_ID) {
                return BiomeLayers.DEEP_FROZEN_OCEAN_ID;
            }
        }
        return integer7;
    }
}
