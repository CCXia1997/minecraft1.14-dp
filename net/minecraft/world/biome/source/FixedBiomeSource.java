package net.minecraft.world.biome.source;

import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.world.gen.feature.StructureFeature;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import java.util.List;
import java.util.Arrays;
import net.minecraft.world.biome.Biome;

public class FixedBiomeSource extends BiomeSource
{
    private final Biome biome;
    
    public FixedBiomeSource(final FixedBiomeSourceConfig fixedBiomeSourceConfig) {
        this.biome = fixedBiomeSourceConfig.getBiome();
    }
    
    @Override
    public Biome getBiome(final int x, final int z) {
        return this.biome;
    }
    
    @Override
    public Biome[] sampleBiomes(final int x, final int z, final int width, final int height, final boolean boolean5) {
        final Biome[] arr6 = new Biome[width * height];
        Arrays.fill(arr6, 0, width * height, this.biome);
        return arr6;
    }
    
    @Nullable
    @Override
    public BlockPos locateBiome(final int x, final int z, final int radius, final List<Biome> biomes, final Random random) {
        if (biomes.contains(this.biome)) {
            return new BlockPos(x - radius + random.nextInt(radius * 2 + 1), 0, z - radius + random.nextInt(radius * 2 + 1));
        }
        return null;
    }
    
    @Override
    public boolean hasStructureFeature(final StructureFeature<?> feature) {
        return this.structureFeatures.computeIfAbsent(feature, this.biome::hasStructureFeature);
    }
    
    @Override
    public Set<BlockState> getTopMaterials() {
        if (this.topMaterials.isEmpty()) {
            this.topMaterials.add(this.biome.getSurfaceConfig().getTopMaterial());
        }
        return this.topMaterials;
    }
    
    @Override
    public Set<Biome> getBiomesInArea(final int x, final int z, final int radius) {
        return Sets.<Biome>newHashSet(this.biome);
    }
}
