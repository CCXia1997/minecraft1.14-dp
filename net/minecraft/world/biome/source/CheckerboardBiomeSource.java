package net.minecraft.world.biome.source;

import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import java.util.Set;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import java.util.List;
import net.minecraft.world.biome.Biome;

public class CheckerboardBiomeSource extends BiomeSource
{
    private final Biome[] biomes;
    private final int gridSize;
    
    public CheckerboardBiomeSource(final CheckerboardBiomeSourceConfig checkerboardBiomeSourceConfig) {
        this.biomes = checkerboardBiomeSourceConfig.getBiomes();
        this.gridSize = checkerboardBiomeSourceConfig.getSize() + 4;
    }
    
    @Override
    public Biome getBiome(final int x, final int z) {
        return this.biomes[Math.abs(((x >> this.gridSize) + (z >> this.gridSize)) % this.biomes.length)];
    }
    
    @Override
    public Biome[] sampleBiomes(final int x, final int z, final int width, final int height, final boolean boolean5) {
        final Biome[] arr6 = new Biome[width * height];
        for (int integer7 = 0; integer7 < height; ++integer7) {
            for (int integer8 = 0; integer8 < width; ++integer8) {
                final int integer9 = Math.abs(((x + integer7 >> this.gridSize) + (z + integer8 >> this.gridSize)) % this.biomes.length);
                final Biome biome10 = this.biomes[integer9];
                arr6[integer7 * width + integer8] = biome10;
            }
        }
        return arr6;
    }
    
    @Nullable
    @Override
    public BlockPos locateBiome(final int x, final int z, final int radius, final List<Biome> biomes, final Random random) {
        return null;
    }
    
    @Override
    public boolean hasStructureFeature(final StructureFeature<?> feature) {
        final Biome[] biomes;
        final int length;
        int i = 0;
        Biome biome5;
        return this.structureFeatures.computeIfAbsent(feature, structureFeature -> {
            biomes = this.biomes;
            length = biomes.length;
            while (i < length) {
                biome5 = biomes[i];
                if (biome5.<FeatureConfig>hasStructureFeature(structureFeature)) {
                    return true;
                }
                else {
                    ++i;
                }
            }
            return false;
        });
    }
    
    @Override
    public Set<BlockState> getTopMaterials() {
        if (this.topMaterials.isEmpty()) {
            for (final Biome biome4 : this.biomes) {
                this.topMaterials.add(biome4.getSurfaceConfig().getTopMaterial());
            }
        }
        return this.topMaterials;
    }
    
    @Override
    public Set<Biome> getBiomesInArea(final int x, final int z, final int radius) {
        return Sets.<Biome>newHashSet(this.biomes);
    }
}
