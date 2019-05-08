package net.minecraft.world.biome.source;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import java.util.List;
import java.util.Collection;
import java.util.Collections;
import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.BiomeLayerSampler;

public class VanillaLayeredBiomeSource extends BiomeSource
{
    private final BiomeLayerSampler noiseLayer;
    private final BiomeLayerSampler biomeLayer;
    private final Biome[] biomes;
    
    public VanillaLayeredBiomeSource(final VanillaLayeredBiomeSourceConfig config) {
        this.biomes = new Biome[] { Biomes.a, Biomes.c, Biomes.d, Biomes.e, Biomes.f, Biomes.g, Biomes.h, Biomes.i, Biomes.l, Biomes.m, Biomes.n, Biomes.o, Biomes.p, Biomes.q, Biomes.r, Biomes.s, Biomes.t, Biomes.u, Biomes.v, Biomes.w, Biomes.x, Biomes.y, Biomes.z, Biomes.A, Biomes.B, Biomes.C, Biomes.D, Biomes.E, Biomes.F, Biomes.G, Biomes.H, Biomes.I, Biomes.J, Biomes.K, Biomes.L, Biomes.M, Biomes.N, Biomes.O, Biomes.T, Biomes.U, Biomes.V, Biomes.W, Biomes.X, Biomes.Y, Biomes.Z, Biomes.ab, Biomes.ac, Biomes.ad, Biomes.ae, Biomes.af, Biomes.ag, Biomes.ah, Biomes.ai, Biomes.aj, Biomes.ak, Biomes.al, Biomes.am, Biomes.an, Biomes.ao, Biomes.ap, Biomes.aq, Biomes.ar, Biomes.as, Biomes.at, Biomes.au, Biomes.av };
        final LevelProperties levelProperties2 = config.getLevelProperties();
        final OverworldChunkGeneratorConfig overworldChunkGeneratorConfig3 = config.getGeneratorSettings();
        final BiomeLayerSampler[] arr4 = BiomeLayers.build(levelProperties2.getSeed(), levelProperties2.getGeneratorType(), overworldChunkGeneratorConfig3);
        this.noiseLayer = arr4[0];
        this.biomeLayer = arr4[1];
    }
    
    @Override
    public Biome getBiome(final int x, final int z) {
        return this.biomeLayer.sample(x, z);
    }
    
    @Override
    public Biome getBiomeForNoiseGen(final int x, final int z) {
        return this.noiseLayer.sample(x, z);
    }
    
    @Override
    public Biome[] sampleBiomes(final int x, final int z, final int width, final int height, final boolean boolean5) {
        return this.biomeLayer.sample(x, z, width, height);
    }
    
    @Override
    public Set<Biome> getBiomesInArea(final int x, final int z, final int radius) {
        final int integer4 = x - radius >> 2;
        final int integer5 = z - radius >> 2;
        final int integer6 = x + radius >> 2;
        final int integer7 = z + radius >> 2;
        final int integer8 = integer6 - integer4 + 1;
        final int integer9 = integer7 - integer5 + 1;
        final Set<Biome> set10 = Sets.newHashSet();
        Collections.<Biome>addAll(set10, this.noiseLayer.sample(integer4, integer5, integer8, integer9));
        return set10;
    }
    
    @Nullable
    @Override
    public BlockPos locateBiome(final int x, final int z, final int radius, final List<Biome> biomes, final Random random) {
        final int integer6 = x - radius >> 2;
        final int integer7 = z - radius >> 2;
        final int integer8 = x + radius >> 2;
        final int integer9 = z + radius >> 2;
        final int integer10 = integer8 - integer6 + 1;
        final int integer11 = integer9 - integer7 + 1;
        final Biome[] arr12 = this.noiseLayer.sample(integer6, integer7, integer10, integer11);
        BlockPos blockPos13 = null;
        int integer12 = 0;
        for (int integer13 = 0; integer13 < integer10 * integer11; ++integer13) {
            final int integer14 = integer6 + integer13 % integer10 << 2;
            final int integer15 = integer7 + integer13 / integer10 << 2;
            if (biomes.contains(arr12[integer13])) {
                if (blockPos13 == null || random.nextInt(integer12 + 1) == 0) {
                    blockPos13 = new BlockPos(integer14, 0, integer15);
                }
                ++integer12;
            }
        }
        return blockPos13;
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
}
