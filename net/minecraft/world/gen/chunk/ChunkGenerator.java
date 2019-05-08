package net.minecraft.world.gen.chunk;

import net.minecraft.world.Heightmap;
import net.minecraft.client.network.DebugRendererInfoManager;
import java.util.Map;
import java.util.Iterator;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.StructureManager;
import net.minecraft.entity.EntityCategory;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.server.world.ServerWorld;
import javax.annotation.Nullable;
import java.util.Locale;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.World;
import java.util.ListIterator;
import java.util.List;
import java.util.BitSet;
import java.util.Random;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.ChunkRegion;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.IWorld;

public abstract class ChunkGenerator<C extends ChunkGeneratorConfig>
{
    protected final IWorld world;
    protected final long seed;
    protected final BiomeSource biomeSource;
    protected final C config;
    
    public ChunkGenerator(final IWorld world, final BiomeSource biomeSource, final C config) {
        this.world = world;
        this.seed = world.getSeed();
        this.biomeSource = biomeSource;
        this.config = config;
    }
    
    public void populateBiomes(final Chunk chunk) {
        final ChunkPos chunkPos2 = chunk.getPos();
        final int integer3 = chunkPos2.x;
        final int integer4 = chunkPos2.z;
        final Biome[] arr5 = this.biomeSource.sampleBiomes(integer3 * 16, integer4 * 16, 16, 16);
        chunk.setBiomeArray(arr5);
    }
    
    protected Biome getDecorationBiome(final Chunk chunk) {
        return chunk.getBiome(BlockPos.ORIGIN);
    }
    
    protected Biome getDecorationBiome(final ChunkRegion chunkRegion, final BlockPos blockPos) {
        return this.biomeSource.getBiome(blockPos);
    }
    
    public void carve(final Chunk chunk, final GenerationStep.Carver carverStep) {
        final ChunkRandom chunkRandom3 = new ChunkRandom();
        final int integer4 = 8;
        final ChunkPos chunkPos5 = chunk.getPos();
        final int integer5 = chunkPos5.x;
        final int integer6 = chunkPos5.z;
        final BitSet bitSet8 = chunk.getCarvingMask(carverStep);
        for (int integer7 = integer5 - 8; integer7 <= integer5 + 8; ++integer7) {
            for (int integer8 = integer6 - 8; integer8 <= integer6 + 8; ++integer8) {
                final List<ConfiguredCarver<?>> list11 = this.getDecorationBiome(chunk).getCarversForStep(carverStep);
                final ListIterator<ConfiguredCarver<?>> listIterator12 = list11.listIterator();
                while (listIterator12.hasNext()) {
                    final int integer9 = listIterator12.nextIndex();
                    final ConfiguredCarver<?> configuredCarver14 = listIterator12.next();
                    chunkRandom3.setStructureSeed(this.seed + integer9, integer7, integer8);
                    if (configuredCarver14.shouldCarve(chunkRandom3, integer7, integer8)) {
                        configuredCarver14.carve(chunk, chunkRandom3, this.getSeaLevel(), integer7, integer8, integer5, integer6, bitSet8);
                    }
                }
            }
        }
    }
    
    @Nullable
    public BlockPos locateStructure(final World world, final String id, final BlockPos center, final int radius, final boolean skipExistingChunks) {
        final StructureFeature<?> structureFeature6 = Feature.STRUCTURES.get(id.toLowerCase(Locale.ROOT));
        if (structureFeature6 != null) {
            return structureFeature6.locateStructure(world, this, center, radius, skipExistingChunks);
        }
        return null;
    }
    
    public void generateFeatures(final ChunkRegion chunkRegion) {
        final int integer2 = chunkRegion.getCenterChunkX();
        final int integer3 = chunkRegion.getCenterChunkZ();
        final int integer4 = integer2 * 16;
        final int integer5 = integer3 * 16;
        final BlockPos blockPos6 = new BlockPos(integer4, 0, integer5);
        final Biome biome7 = this.getDecorationBiome(chunkRegion, blockPos6.add(8, 8, 8));
        final ChunkRandom chunkRandom8 = new ChunkRandom();
        final long long9 = chunkRandom8.setSeed(chunkRegion.getSeed(), integer4, integer5);
        for (final GenerationStep.Feature feature14 : GenerationStep.Feature.values()) {
            biome7.generateFeatureStep(feature14, this, chunkRegion, long9, chunkRandom8, blockPos6);
        }
    }
    
    public abstract void buildSurface(final Chunk arg1);
    
    public void populateEntities(final ChunkRegion chunkRegion) {
    }
    
    public C getConfig() {
        return this.config;
    }
    
    public abstract int getSpawnHeight();
    
    public void spawnEntities(final ServerWorld serverWorld, final boolean spawnMonsters, final boolean spawnAnimals) {
    }
    
    public boolean hasStructure(final Biome biome, final StructureFeature<? extends FeatureConfig> structureFeature) {
        return biome.hasStructureFeature(structureFeature);
    }
    
    @Nullable
    public <C extends FeatureConfig> C getStructureConfig(final Biome biome, final StructureFeature<C> structureFeature) {
        return biome.<C>getStructureFeatureConfig(structureFeature);
    }
    
    public BiomeSource getBiomeSource() {
        return this.biomeSource;
    }
    
    public long getSeed() {
        return this.seed;
    }
    
    public int getMaxY() {
        return 256;
    }
    
    public List<Biome.SpawnEntry> getEntitySpawnList(final EntityCategory entityCategory, final BlockPos blockPos) {
        return this.world.getBiome(blockPos).getEntitySpawnList(entityCategory);
    }
    
    public void setStructureStarts(final Chunk chunk, final ChunkGenerator<?> chunkGenerator, final StructureManager structureManager) {
        for (final StructureFeature<?> structureFeature5 : Feature.STRUCTURES.values()) {
            if (!chunkGenerator.getBiomeSource().hasStructureFeature(structureFeature5)) {
                continue;
            }
            final ChunkRandom chunkRandom6 = new ChunkRandom();
            final ChunkPos chunkPos7 = chunk.getPos();
            StructureStart structureStart8 = StructureStart.DEFAULT;
            if (structureFeature5.shouldStartAt(chunkGenerator, chunkRandom6, chunkPos7.x, chunkPos7.z)) {
                final Biome biome9 = this.getBiomeSource().getBiome(new BlockPos(chunkPos7.getStartX() + 9, 0, chunkPos7.getStartZ() + 9));
                final StructureStart structureStart9 = structureFeature5.getStructureStartFactory().create(structureFeature5, chunkPos7.x, chunkPos7.z, biome9, MutableIntBoundingBox.empty(), 0, chunkGenerator.getSeed());
                structureStart9.initialize(this, structureManager, chunkPos7.x, chunkPos7.z, biome9);
                structureStart8 = (structureStart9.hasChildren() ? structureStart9 : StructureStart.DEFAULT);
            }
            chunk.setStructureStart(structureFeature5.getName(), structureStart8);
        }
    }
    
    public void addStructureReferences(final IWorld world, final Chunk chunk) {
        final int integer3 = 8;
        final int integer4 = chunk.getPos().x;
        final int integer5 = chunk.getPos().z;
        final int integer6 = integer4 << 4;
        final int integer7 = integer5 << 4;
        for (int integer8 = integer4 - 8; integer8 <= integer4 + 8; ++integer8) {
            for (int integer9 = integer5 - 8; integer9 <= integer5 + 8; ++integer9) {
                final long long10 = ChunkPos.toLong(integer8, integer9);
                for (final Map.Entry<String, StructureStart> entry13 : world.getChunk(integer8, integer9).getStructureStarts().entrySet()) {
                    final StructureStart structureStart14 = entry13.getValue();
                    if (structureStart14 != StructureStart.DEFAULT && structureStart14.getBoundingBox().intersectsXZ(integer6, integer7, integer6 + 15, integer7 + 15)) {
                        chunk.addStructureReference(entry13.getKey(), long10);
                        DebugRendererInfoManager.sendStructureStart(world, structureStart14);
                    }
                }
            }
        }
    }
    
    public abstract void populateNoise(final IWorld arg1, final Chunk arg2);
    
    public int getSeaLevel() {
        return 63;
    }
    
    public abstract int getHeightOnGround(final int arg1, final int arg2, final Heightmap.Type arg3);
    
    public int b(final int integer1, final int integer2, final Heightmap.Type type) {
        return this.getHeightOnGround(integer1, integer2, type);
    }
    
    public int getHeightInGround(final int integer1, final int integer2, final Heightmap.Type type) {
        return this.getHeightOnGround(integer1, integer2, type) - 1;
    }
}
