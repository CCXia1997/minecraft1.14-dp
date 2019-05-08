package net.minecraft.world.gen.chunk;

import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.biome.Biome;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.EntityCategory;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.World;

public class CavesChunkGenerator extends SurfaceChunkGenerator<CavesChunkGeneratorConfig>
{
    private final double[] noiseFalloff;
    
    public CavesChunkGenerator(final World world, final BiomeSource biomeSource, final CavesChunkGeneratorConfig cavesChunkGeneratorConfig) {
        super(world, biomeSource, 4, 8, 128, cavesChunkGeneratorConfig, false);
        this.noiseFalloff = this.buidlNoiseFalloff();
    }
    
    @Override
    protected void sampleNoiseColumn(final double[] buffer, final int x, final int z) {
        final double double4 = 684.412;
        final double double5 = 2053.236;
        final double double6 = 8.555150000000001;
        final double double7 = 34.2206;
        final int integer12 = -10;
        final int integer13 = 3;
        this.sampleNoiseColumn(buffer, x, z, 684.412, 2053.236, 8.555150000000001, 34.2206, 3, -10);
    }
    
    @Override
    protected double[] computeNoiseRange(final int x, final int z) {
        return new double[] { 0.0, 0.0 };
    }
    
    @Override
    protected double computeNoiseFalloff(final double depth, final double scale, final int y) {
        return this.noiseFalloff[y];
    }
    
    private double[] buidlNoiseFalloff() {
        final double[] arr1 = new double[this.getNoiseSizeY()];
        for (int integer2 = 0; integer2 < this.getNoiseSizeY(); ++integer2) {
            arr1[integer2] = Math.cos(integer2 * 3.141592653589793 * 6.0 / this.getNoiseSizeY()) * 2.0;
            double double3 = integer2;
            if (integer2 > this.getNoiseSizeY() / 2) {
                double3 = this.getNoiseSizeY() - 1 - integer2;
            }
            if (double3 < 4.0) {
                double3 = 4.0 - double3;
                final double[] array = arr1;
                final int n = integer2;
                array[n] -= double3 * double3 * double3 * 10.0;
            }
        }
        return arr1;
    }
    
    @Override
    public List<Biome.SpawnEntry> getEntitySpawnList(final EntityCategory entityCategory, final BlockPos blockPos) {
        if (entityCategory == EntityCategory.a) {
            if (Feature.NETHER_BRIDGE.isInsideStructure(this.world, blockPos)) {
                return Feature.NETHER_BRIDGE.getMonsterSpawns();
            }
            if (Feature.NETHER_BRIDGE.isApproximatelyInsideStructure(this.world, blockPos) && this.world.getBlockState(blockPos.down()).getBlock() == Blocks.dN) {
                return Feature.NETHER_BRIDGE.getMonsterSpawns();
            }
        }
        return super.getEntitySpawnList(entityCategory, blockPos);
    }
    
    @Override
    public int getSpawnHeight() {
        return this.world.getSeaLevel() + 1;
    }
    
    @Override
    public int getMaxY() {
        return 128;
    }
    
    @Override
    public int getSeaLevel() {
        return 32;
    }
}
