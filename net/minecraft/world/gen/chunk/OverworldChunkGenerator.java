package net.minecraft.world.gen.chunk;

import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.gen.feature.Feature;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.EntityCategory;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.level.LevelGeneratorType;
import java.util.Random;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.CatSpawner;
import net.minecraft.world.gen.PillagerSpawner;
import net.minecraft.world.gen.PhantomSpawner;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;

public class OverworldChunkGenerator extends SurfaceChunkGenerator<OverworldChunkGeneratorConfig>
{
    private static final float[] BIOME_WEIGHT_TABLE;
    private final OctavePerlinNoiseSampler noiseSampler;
    private final boolean amplified;
    private final PhantomSpawner phantomSpawner;
    private final PillagerSpawner pillagerSpawner;
    private final CatSpawner m;
    
    public OverworldChunkGenerator(final IWorld iWorld, final BiomeSource biomeSource, final OverworldChunkGeneratorConfig overworldChunkGeneratorConfig) {
        super(iWorld, biomeSource, 4, 8, 256, overworldChunkGeneratorConfig, true);
        this.phantomSpawner = new PhantomSpawner();
        this.pillagerSpawner = new PillagerSpawner();
        this.m = new CatSpawner();
        this.random.consume(2620);
        this.noiseSampler = new OctavePerlinNoiseSampler(this.random, 16);
        this.amplified = (iWorld.getLevelProperties().getGeneratorType() == LevelGeneratorType.AMPLIFIED);
    }
    
    @Override
    public void populateEntities(final ChunkRegion chunkRegion) {
        final int integer2 = chunkRegion.getCenterChunkX();
        final int integer3 = chunkRegion.getCenterChunkZ();
        final Biome biome4 = chunkRegion.getChunk(integer2, integer3).getBiomeArray()[0];
        final ChunkRandom chunkRandom5 = new ChunkRandom();
        chunkRandom5.setSeed(chunkRegion.getSeed(), integer2 << 4, integer3 << 4);
        SpawnHelper.populateEntities(chunkRegion, biome4, integer2, integer3, chunkRandom5);
    }
    
    @Override
    protected void sampleNoiseColumn(final double[] buffer, final int x, final int z) {
        final double double4 = 684.4119873046875;
        final double double5 = 684.4119873046875;
        final double double6 = 8.555149841308594;
        final double double7 = 4.277574920654297;
        final int integer12 = -10;
        final int integer13 = 3;
        this.sampleNoiseColumn(buffer, x, z, 684.4119873046875, 684.4119873046875, 8.555149841308594, 4.277574920654297, 3, -10);
    }
    
    @Override
    protected double computeNoiseFalloff(final double depth, final double scale, final int y) {
        final double double6 = 8.5;
        double double7 = (y - (8.5 + depth * 8.5 / 8.0 * 4.0)) * 12.0 * 128.0 / 256.0 / scale;
        if (double7 < 0.0) {
            double7 *= 4.0;
        }
        return double7;
    }
    
    @Override
    protected double[] computeNoiseRange(final int x, final int z) {
        final double[] arr3 = new double[2];
        float float4 = 0.0f;
        float float5 = 0.0f;
        float float6 = 0.0f;
        final int integer7 = 2;
        final float float7 = this.biomeSource.getBiomeForNoiseGen(x, z).getDepth();
        for (int integer8 = -2; integer8 <= 2; ++integer8) {
            for (int integer9 = -2; integer9 <= 2; ++integer9) {
                final Biome biome11 = this.biomeSource.getBiomeForNoiseGen(x + integer8, z + integer9);
                float float8 = biome11.getDepth();
                float float9 = biome11.getScale();
                if (this.amplified && float8 > 0.0f) {
                    float8 = 1.0f + float8 * 2.0f;
                    float9 = 1.0f + float9 * 4.0f;
                }
                float float10 = OverworldChunkGenerator.BIOME_WEIGHT_TABLE[integer8 + 2 + (integer9 + 2) * 5] / (float8 + 2.0f);
                if (biome11.getDepth() > float7) {
                    float10 /= 2.0f;
                }
                float4 += float9 * float10;
                float5 += float8 * float10;
                float6 += float10;
            }
        }
        float4 /= float6;
        float5 /= float6;
        float4 = float4 * 0.9f + 0.1f;
        float5 = (float5 * 4.0f - 1.0f) / 8.0f;
        arr3[0] = float5 + this.c(x, z);
        arr3[1] = float4;
        return arr3;
    }
    
    private double c(final int integer1, final int integer2) {
        double double3 = this.noiseSampler.sample(integer1 * 200, 10.0, integer2 * 200, 1.0, 0.0, true) / 8000.0;
        if (double3 < 0.0) {
            double3 = -double3 * 0.3;
        }
        double3 = double3 * 3.0 - 2.0;
        if (double3 < 0.0) {
            double3 /= 28.0;
        }
        else {
            if (double3 > 1.0) {
                double3 = 1.0;
            }
            double3 /= 40.0;
        }
        return double3;
    }
    
    @Override
    public List<Biome.SpawnEntry> getEntitySpawnList(final EntityCategory entityCategory, final BlockPos blockPos) {
        if (Feature.SWAMP_HUT.c(this.world, blockPos)) {
            if (entityCategory == EntityCategory.a) {
                return Feature.SWAMP_HUT.getMonsterSpawns();
            }
            if (entityCategory == EntityCategory.b) {
                return Feature.SWAMP_HUT.getCreatureSpawns();
            }
        }
        else if (entityCategory == EntityCategory.a) {
            if (Feature.PILLAGER_OUTPOST.isApproximatelyInsideStructure(this.world, blockPos)) {
                return Feature.PILLAGER_OUTPOST.getMonsterSpawns();
            }
            if (Feature.OCEAN_MONUMENT.isApproximatelyInsideStructure(this.world, blockPos)) {
                return Feature.OCEAN_MONUMENT.getMonsterSpawns();
            }
        }
        return super.getEntitySpawnList(entityCategory, blockPos);
    }
    
    @Override
    public void spawnEntities(final ServerWorld serverWorld, final boolean spawnMonsters, final boolean spawnAnimals) {
        this.phantomSpawner.spawn(serverWorld, spawnMonsters, spawnAnimals);
        this.pillagerSpawner.spawn(serverWorld, spawnMonsters, spawnAnimals);
        this.m.spawn(serverWorld, spawnMonsters, spawnAnimals);
    }
    
    @Override
    public int getSpawnHeight() {
        return this.world.getSeaLevel() + 1;
    }
    
    @Override
    public int getSeaLevel() {
        return 63;
    }
    
    static {
        int integer2;
        int integer3;
        float float4;
        BIOME_WEIGHT_TABLE = SystemUtil.<float[]>consume(new float[25], arr -> {
            for (integer2 = -2; integer2 <= 2; ++integer2) {
                for (integer3 = -2; integer3 <= 2; ++integer3) {
                    float4 = 10.0f / MathHelper.sqrt(integer2 * integer2 + integer3 * integer3 + 0.2f);
                    arr[integer2 + 2 + (integer3 + 2) * 5] = float4;
                }
            }
        });
    }
}
