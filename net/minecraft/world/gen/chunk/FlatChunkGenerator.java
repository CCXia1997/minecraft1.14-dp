package net.minecraft.world.gen.chunk;

import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import java.util.Locale;
import net.minecraft.world.World;
import javax.annotation.Nullable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.block.BlockState;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.FillLayerFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import com.google.common.collect.Lists;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.CatSpawner;
import net.minecraft.world.gen.PhantomSpawner;
import net.minecraft.world.biome.Biome;

public class FlatChunkGenerator extends ChunkGenerator<FlatChunkGeneratorConfig>
{
    private final Biome biome;
    private final PhantomSpawner phantomSpawner;
    private final CatSpawner catSpawner;
    
    public FlatChunkGenerator(final IWorld world, final BiomeSource biomeSource, final FlatChunkGeneratorConfig config) {
        super(world, biomeSource, config);
        this.phantomSpawner = new PhantomSpawner();
        this.catSpawner = new CatSpawner();
        this.biome = this.getBiome();
    }
    
    private Biome getBiome() {
        final Biome biome1 = ((FlatChunkGeneratorConfig)this.config).getBiome();
        final FlatChunkGeneratorBiome flatChunkGeneratorBiome2 = new FlatChunkGeneratorBiome(biome1.getSurfaceBuilder(), biome1.getPrecipitation(), biome1.getCategory(), biome1.getDepth(), biome1.getScale(), biome1.getTemperature(), biome1.getRainfall(), biome1.getWaterColor(), biome1.getWaterFogColor(), biome1.getParent());
        final Map<String, Map<String, String>> map3 = ((FlatChunkGeneratorConfig)this.config).getStructures();
        for (final String string5 : map3.keySet()) {
            final ConfiguredFeature<?>[] arr6 = FlatChunkGeneratorConfig.STRUCTURE_TO_FEATURES.get(string5);
            if (arr6 == null) {
                continue;
            }
            for (final ConfiguredFeature<?> configuredFeature10 : arr6) {
                flatChunkGeneratorBiome2.addFeature(FlatChunkGeneratorConfig.FEATURE_TO_GENERATION_STEP.get(configuredFeature10), configuredFeature10);
                final ConfiguredFeature<?> configuredFeature11 = ((DecoratedFeatureConfig)configuredFeature10.config).feature;
                if (configuredFeature11.feature instanceof StructureFeature) {
                    final StructureFeature<FeatureConfig> structureFeature12 = (StructureFeature<FeatureConfig>)(StructureFeature)configuredFeature11.feature;
                    final FeatureConfig featureConfig13 = biome1.<FeatureConfig>getStructureFeatureConfig(structureFeature12);
                    flatChunkGeneratorBiome2.<FeatureConfig>addStructureFeature(structureFeature12, (featureConfig13 != null) ? featureConfig13 : FlatChunkGeneratorConfig.FEATURE_TO_FEATURE_CONFIG.get(configuredFeature10));
                }
            }
        }
        final boolean boolean4 = (!((FlatChunkGeneratorConfig)this.config).hasNoTerrain() || biome1 == Biomes.aa) && map3.containsKey("decoration");
        if (boolean4) {
            final List<GenerationStep.Feature> list5 = Lists.newArrayList();
            list5.add(GenerationStep.Feature.UNDERGROUND_STRUCTURES);
            list5.add(GenerationStep.Feature.SURFACE_STRUCTURES);
            for (final GenerationStep.Feature feature9 : GenerationStep.Feature.values()) {
                if (!list5.contains(feature9)) {
                    for (final ConfiguredFeature<?> configuredFeature11 : biome1.getFeaturesForStep(feature9)) {
                        flatChunkGeneratorBiome2.addFeature(feature9, configuredFeature11);
                    }
                }
            }
        }
        final BlockState[] arr7 = ((FlatChunkGeneratorConfig)this.config).getLayerBlocks();
        for (int integer6 = 0; integer6 < arr7.length; ++integer6) {
            final BlockState blockState7 = arr7[integer6];
            if (blockState7 != null && !Heightmap.Type.e.getBlockPredicate().test(blockState7)) {
                ((FlatChunkGeneratorConfig)this.config).a(integer6);
                flatChunkGeneratorBiome2.addFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, Biome.<FillLayerFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.aN, new FillLayerFeatureConfig(integer6, blockState7), Decorator.NOPE, DecoratorConfig.DEFAULT));
            }
        }
        return flatChunkGeneratorBiome2;
    }
    
    @Override
    public void buildSurface(final Chunk chunk) {
    }
    
    @Override
    public int getSpawnHeight() {
        final Chunk chunk1 = this.world.getChunk(0, 0);
        return chunk1.sampleHeightmap(Heightmap.Type.e, 8, 8);
    }
    
    @Override
    protected Biome getDecorationBiome(final Chunk chunk) {
        return this.biome;
    }
    
    @Override
    protected Biome getDecorationBiome(final ChunkRegion chunkRegion, final BlockPos blockPos) {
        return this.biome;
    }
    
    @Override
    public void populateNoise(final IWorld world, final Chunk chunk) {
        final BlockState[] arr3 = ((FlatChunkGeneratorConfig)this.config).getLayerBlocks();
        final BlockPos.Mutable mutable4 = new BlockPos.Mutable();
        final Heightmap heightmap5 = chunk.getHeightmap(Heightmap.Type.c);
        final Heightmap heightmap6 = chunk.getHeightmap(Heightmap.Type.a);
        for (int integer7 = 0; integer7 < arr3.length; ++integer7) {
            final BlockState blockState8 = arr3[integer7];
            if (blockState8 != null) {
                for (int integer8 = 0; integer8 < 16; ++integer8) {
                    for (int integer9 = 0; integer9 < 16; ++integer9) {
                        chunk.setBlockState(mutable4.set(integer8, integer7, integer9), blockState8, false);
                        heightmap5.trackUpdate(integer8, integer7, integer9, blockState8);
                        heightmap6.trackUpdate(integer8, integer7, integer9, blockState8);
                    }
                }
            }
        }
    }
    
    @Override
    public int getHeightOnGround(final int x, final int z, final Heightmap.Type heightmapType) {
        final BlockState[] arr4 = ((FlatChunkGeneratorConfig)this.config).getLayerBlocks();
        for (int integer5 = arr4.length - 1; integer5 >= 0; --integer5) {
            final BlockState blockState6 = arr4[integer5];
            if (blockState6 != null) {
                if (heightmapType.getBlockPredicate().test(blockState6)) {
                    return integer5 + 1;
                }
            }
        }
        return 0;
    }
    
    @Override
    public void spawnEntities(final ServerWorld serverWorld, final boolean spawnMonsters, final boolean spawnAnimals) {
        this.phantomSpawner.spawn(serverWorld, spawnMonsters, spawnAnimals);
        this.catSpawner.spawn(serverWorld, spawnMonsters, spawnAnimals);
    }
    
    @Override
    public boolean hasStructure(final Biome biome, final StructureFeature<? extends FeatureConfig> structureFeature) {
        return this.biome.hasStructureFeature(structureFeature);
    }
    
    @Nullable
    @Override
    public <C extends FeatureConfig> C getStructureConfig(final Biome biome, final StructureFeature<C> structureFeature) {
        return this.biome.<C>getStructureFeatureConfig(structureFeature);
    }
    
    @Nullable
    @Override
    public BlockPos locateStructure(final World world, final String id, final BlockPos center, final int radius, final boolean skipExistingChunks) {
        if (!((FlatChunkGeneratorConfig)this.config).getStructures().keySet().contains(id.toLowerCase(Locale.ROOT))) {
            return null;
        }
        return super.locateStructure(world, id, center, radius, skipExistingChunks);
    }
    
    class FlatChunkGeneratorBiome extends Biome
    {
        protected FlatChunkGeneratorBiome(final ConfiguredSurfaceBuilder<?> configuredSurfaceBuilder, final Precipitation precipitation, final Category category, final float float5, final float float6, final float float7, final float float8, final int integer9, final int integer10, @Nullable final String string) {
            super(new Settings().surfaceBuilder(configuredSurfaceBuilder).precipitation(precipitation).category(category).depth(float5).scale(float6).temperature(float7).downfall(float8).waterColor(integer9).waterFogColor(integer10).parent(string));
        }
    }
}
