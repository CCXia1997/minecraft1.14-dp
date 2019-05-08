package net.minecraft.world.biome.source;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.util.math.MathHelper;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import com.google.common.collect.Sets;
import java.util.Set;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.world.chunk.ChunkPos;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Random;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.util.math.noise.SimplexNoiseSampler;

public class TheEndBiomeSource extends BiomeSource
{
    private final SimplexNoiseSampler noise;
    private final ChunkRandom random;
    private final Biome[] biomes;
    
    public TheEndBiomeSource(final TheEndBiomeSourceConfig theEndBiomeSourceConfig) {
        this.biomes = new Biome[] { Biomes.k, Biomes.R, Biomes.Q, Biomes.P, Biomes.S };
        (this.random = new ChunkRandom(theEndBiomeSourceConfig.a())).consume(17292);
        this.noise = new SimplexNoiseSampler(this.random);
    }
    
    @Override
    public Biome getBiome(final int x, final int z) {
        final int integer3 = x >> 4;
        final int integer4 = z >> 4;
        if (integer3 * (long)integer3 + integer4 * (long)integer4 <= 4096L) {
            return Biomes.k;
        }
        final float float5 = this.c(integer3 * 2 + 1, integer4 * 2 + 1);
        if (float5 > 40.0f) {
            return Biomes.R;
        }
        if (float5 >= 0.0f) {
            return Biomes.Q;
        }
        if (float5 < -20.0f) {
            return Biomes.P;
        }
        return Biomes.S;
    }
    
    @Override
    public Biome[] sampleBiomes(final int x, final int z, final int width, final int height, final boolean boolean5) {
        final Biome[] arr6 = new Biome[width * height];
        final Long2ObjectMap<Biome> long2ObjectMap7 = (Long2ObjectMap<Biome>)new Long2ObjectOpenHashMap();
        for (int integer8 = 0; integer8 < width; ++integer8) {
            for (int integer9 = 0; integer9 < height; ++integer9) {
                final int integer10 = integer8 + x;
                final int integer11 = integer9 + z;
                final long long12 = ChunkPos.toLong(integer10, integer11);
                Biome biome14 = (Biome)long2ObjectMap7.get(long12);
                if (biome14 == null) {
                    biome14 = this.getBiome(integer10, integer11);
                    long2ObjectMap7.put(long12, biome14);
                }
                arr6[integer8 + integer9 * width] = biome14;
            }
        }
        return arr6;
    }
    
    @Override
    public Set<Biome> getBiomesInArea(final int x, final int z, final int radius) {
        final int integer4 = x - radius >> 2;
        final int integer5 = z - radius >> 2;
        final int integer6 = x + radius >> 2;
        final int integer7 = z + radius >> 2;
        final int integer8 = integer6 - integer4 + 1;
        final int integer9 = integer7 - integer5 + 1;
        return Sets.<Biome>newHashSet(this.sampleBiomes(integer4, integer5, integer8, integer9));
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
        final Biome[] arr12 = this.sampleBiomes(integer6, integer7, integer10, integer11);
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
    public float c(final int integer1, final int integer2) {
        final int integer3 = integer1 / 2;
        final int integer4 = integer2 / 2;
        final int integer5 = integer1 % 2;
        final int integer6 = integer2 % 2;
        float float7 = 100.0f - MathHelper.sqrt((float)(integer1 * integer1 + integer2 * integer2)) * 8.0f;
        float7 = MathHelper.clamp(float7, -100.0f, 80.0f);
        for (int integer7 = -12; integer7 <= 12; ++integer7) {
            for (int integer8 = -12; integer8 <= 12; ++integer8) {
                final long long10 = integer3 + integer7;
                final long long11 = integer4 + integer8;
                if (long10 * long10 + long11 * long11 > 4096L && this.noise.sample((double)long10, (double)long11) < -0.8999999761581421) {
                    final float float8 = (MathHelper.abs((float)long10) * 3439.0f + MathHelper.abs((float)long11) * 147.0f) % 13.0f + 9.0f;
                    final float float9 = (float)(integer5 - integer7 * 2);
                    final float float10 = (float)(integer6 - integer8 * 2);
                    float float11 = 100.0f - MathHelper.sqrt(float9 * float9 + float10 * float10) * float8;
                    float11 = MathHelper.clamp(float11, -100.0f, 80.0f);
                    float7 = Math.max(float7, float11);
                }
            }
        }
        return float7;
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
