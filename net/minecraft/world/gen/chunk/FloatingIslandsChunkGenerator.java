package net.minecraft.world.gen.chunk;

import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.BlockPos;

public class FloatingIslandsChunkGenerator extends SurfaceChunkGenerator<FloatingIslandsChunkGeneratorConfig>
{
    private final BlockPos center;
    
    public FloatingIslandsChunkGenerator(final IWorld iWorld, final BiomeSource biomeSource, final FloatingIslandsChunkGeneratorConfig floatingIslandsChunkGeneratorConfig) {
        super(iWorld, biomeSource, 8, 4, 128, floatingIslandsChunkGeneratorConfig, true);
        this.center = floatingIslandsChunkGeneratorConfig.getCenter();
    }
    
    @Override
    protected void sampleNoiseColumn(final double[] buffer, final int x, final int z) {
        final double double4 = 1368.824;
        final double double5 = 684.412;
        final double double6 = 17.110300000000002;
        final double double7 = 4.277575000000001;
        final int integer12 = 64;
        final int integer13 = -3000;
        this.sampleNoiseColumn(buffer, x, z, 1368.824, 684.412, 17.110300000000002, 4.277575000000001, 64, -3000);
    }
    
    @Override
    protected double[] computeNoiseRange(final int x, final int z) {
        return new double[] { this.biomeSource.c(x, z), 0.0 };
    }
    
    @Override
    protected double computeNoiseFalloff(final double depth, final double scale, final int y) {
        return 8.0 - depth;
    }
    
    @Override
    protected double g() {
        return (int)super.g() / 2;
    }
    
    @Override
    protected double h() {
        return 8.0;
    }
    
    @Override
    public int getSpawnHeight() {
        return 50;
    }
    
    @Override
    public int getSeaLevel() {
        return 0;
    }
}
