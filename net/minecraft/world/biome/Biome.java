package net.minecraft.world.biome;

import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.util.WeightedPicker;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Arrays;
import com.google.common.collect.Sets;
import org.apache.logging.log4j.LogManager;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;
import net.minecraft.util.SystemUtil;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.client.render.block.FoliageColorHandler;
import net.minecraft.client.render.block.GrassColorHandler;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.LightType;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.math.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.FlowerFeature;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.util.registry.Registry;
import net.minecraft.entity.EntityCategory;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import java.util.List;
import net.minecraft.world.gen.GenerationStep;
import java.util.Map;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import javax.annotation.Nullable;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.IdList;
import java.util.Set;
import org.apache.logging.log4j.Logger;

public abstract class Biome
{
    public static final Logger LOGGER;
    public static final Set<Biome> BIOMES;
    public static final IdList<Biome> PARENT_BIOME_ID_MAP;
    protected static final OctaveSimplexNoiseSampler TEMPERATURE_NOISE;
    public static final OctaveSimplexNoiseSampler FOLIAGE_NOISE;
    @Nullable
    protected String translationKey;
    protected final float depth;
    protected final float scale;
    protected final float temperature;
    protected final float downfall;
    protected final int waterColor;
    protected final int waterFogColor;
    @Nullable
    protected final String parent;
    protected final ConfiguredSurfaceBuilder<?> surfaceBuilder;
    protected final Category category;
    protected final Precipitation precipitation;
    protected final Map<GenerationStep.Carver, List<ConfiguredCarver<?>>> carvers;
    protected final Map<GenerationStep.Feature, List<ConfiguredFeature<?>>> features;
    protected final List<ConfiguredFeature<?>> flowerFeatures;
    protected final Map<StructureFeature<?>, FeatureConfig> structureFeatures;
    private final Map<EntityCategory, List<SpawnEntry>> spawns;
    
    @Nullable
    public static Biome getParentBiome(final Biome biome) {
        return Biome.PARENT_BIOME_ID_MAP.get(Registry.BIOME.getRawId(biome));
    }
    
    public static <C extends CarverConfig> ConfiguredCarver<C> configureCarver(final Carver<C> carver, final C config) {
        return new ConfiguredCarver<C>(carver, config);
    }
    
    public static <F extends FeatureConfig, D extends DecoratorConfig> ConfiguredFeature<?> configureFeature(final Feature<F> feature, final F featureConfig, final Decorator<D> decorator, final D decoratorConfig) {
        final Feature<DecoratedFeatureConfig> feature2 = (feature instanceof FlowerFeature) ? Feature.aL : Feature.aK;
        return new ConfiguredFeature<>(feature2, new DecoratedFeatureConfig((Feature<F>)feature, (F)featureConfig, (Decorator<D>)decorator, (D)decoratorConfig));
    }
    
    protected Biome(final Settings settings) {
        this.carvers = Maps.newHashMap();
        this.features = Maps.newHashMap();
        this.flowerFeatures = Lists.newArrayList();
        this.structureFeatures = Maps.newHashMap();
        this.spawns = Maps.newHashMap();
        if (settings.surfaceBuilder == null || settings.precipitation == null || settings.category == null || settings.depth == null || settings.scale == null || settings.temperature == null || settings.downfall == null || settings.waterColor == null || settings.waterFogColor == null) {
            throw new IllegalStateException("You are missing parameters to build a proper biome for " + this.getClass().getSimpleName() + "\n" + settings);
        }
        this.surfaceBuilder = settings.surfaceBuilder;
        this.precipitation = settings.precipitation;
        this.category = settings.category;
        this.depth = settings.depth;
        this.scale = settings.scale;
        this.temperature = settings.temperature;
        this.downfall = settings.downfall;
        this.waterColor = settings.waterColor;
        this.waterFogColor = settings.waterFogColor;
        this.parent = settings.parent;
        for (final GenerationStep.Feature feature5 : GenerationStep.Feature.values()) {
            this.features.put(feature5, Lists.newArrayList());
        }
        for (final EntityCategory entityCategory5 : EntityCategory.values()) {
            this.spawns.put(entityCategory5, Lists.newArrayList());
        }
    }
    
    public boolean hasParent() {
        return this.parent != null;
    }
    
    @Environment(EnvType.CLIENT)
    public int getSkyColor(float temperature) {
        temperature /= 3.0f;
        temperature = MathHelper.clamp(temperature, -1.0f, 1.0f);
        return MathHelper.hsvToRgb(0.62222224f - temperature * 0.05f, 0.5f + temperature * 0.1f, 1.0f);
    }
    
    protected void addSpawn(final EntityCategory type, final SpawnEntry spawnEntry) {
        this.spawns.get(type).add(spawnEntry);
    }
    
    public List<SpawnEntry> getEntitySpawnList(final EntityCategory entityCategory) {
        return this.spawns.get(entityCategory);
    }
    
    public Precipitation getPrecipitation() {
        return this.precipitation;
    }
    
    public boolean hasHighHumidity() {
        return this.getRainfall() > 0.85f;
    }
    
    public float getMaxSpawnLimit() {
        return 0.1f;
    }
    
    public float getTemperature(final BlockPos blockPos) {
        if (blockPos.getY() > 64) {
            final float float2 = (float)(Biome.TEMPERATURE_NOISE.sample(blockPos.getX() / 8.0f, blockPos.getZ() / 8.0f) * 4.0);
            return this.getTemperature() - (float2 + blockPos.getY() - 64.0f) * 0.05f / 30.0f;
        }
        return this.getTemperature();
    }
    
    public boolean canSetSnow(final ViewableWorld world, final BlockPos blockPos) {
        return this.canSetSnow(world, blockPos, true);
    }
    
    public boolean canSetSnow(final ViewableWorld world, final BlockPos pos, final boolean boolean3) {
        if (this.getTemperature(pos) >= 0.15f) {
            return false;
        }
        if (pos.getY() >= 0 && pos.getY() < 256 && world.getLightLevel(LightType.BLOCK, pos) < 10) {
            final BlockState blockState4 = world.getBlockState(pos);
            final FluidState fluidState5 = world.getFluidState(pos);
            if (fluidState5.getFluid() == Fluids.WATER && blockState4.getBlock() instanceof FluidBlock) {
                if (!boolean3) {
                    return true;
                }
                final boolean boolean4 = world.isWaterAt(pos.west()) && world.isWaterAt(pos.east()) && world.isWaterAt(pos.north()) && world.isWaterAt(pos.south());
                if (!boolean4) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean canSetIce(final ViewableWorld world, final BlockPos blockPos) {
        if (this.getTemperature(blockPos) >= 0.15f) {
            return false;
        }
        if (blockPos.getY() >= 0 && blockPos.getY() < 256 && world.getLightLevel(LightType.BLOCK, blockPos) < 10) {
            final BlockState blockState3 = world.getBlockState(blockPos);
            if (blockState3.isAir() && Blocks.cA.getDefaultState().canPlaceAt(world, blockPos)) {
                return true;
            }
        }
        return false;
    }
    
    public void addFeature(final GenerationStep.Feature step, final ConfiguredFeature<?> configuredFeature) {
        if (configuredFeature.feature == Feature.aL) {
            this.flowerFeatures.add(configuredFeature);
        }
        this.features.get(step).add(configuredFeature);
    }
    
    public <C extends CarverConfig> void addCarver(final GenerationStep.Carver step, final ConfiguredCarver<C> configuredCarver) {
        this.carvers.computeIfAbsent(step, carver -> Lists.newArrayList()).add(configuredCarver);
    }
    
    public List<ConfiguredCarver<?>> getCarversForStep(final GenerationStep.Carver carver) {
        return this.carvers.computeIfAbsent(carver, carver -> Lists.newArrayList());
    }
    
    public <C extends FeatureConfig> void addStructureFeature(final StructureFeature<C> structureFeature, final C featureConfig) {
        this.structureFeatures.put(structureFeature, featureConfig);
    }
    
    public <C extends FeatureConfig> boolean hasStructureFeature(final StructureFeature<C> structureFeature) {
        return this.structureFeatures.containsKey(structureFeature);
    }
    
    @Nullable
    public <C extends FeatureConfig> C getStructureFeatureConfig(final StructureFeature<C> structureFeature) {
        return (C)this.structureFeatures.get(structureFeature);
    }
    
    public List<ConfiguredFeature<?>> getFlowerFeatures() {
        return this.flowerFeatures;
    }
    
    public List<ConfiguredFeature<?>> getFeaturesForStep(final GenerationStep.Feature feature) {
        return this.features.get(feature);
    }
    
    public void generateFeatureStep(final GenerationStep.Feature featureStep, final ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, final IWorld world, final long long4, final ChunkRandom chunkRandom, final BlockPos blockPos7) {
        int integer8 = 0;
        for (final ConfiguredFeature<?> configuredFeature10 : this.features.get(featureStep)) {
            chunkRandom.setFeatureSeed(long4, integer8, featureStep.ordinal());
            configuredFeature10.generate(world, chunkGenerator, chunkRandom, blockPos7);
            ++integer8;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public int getGrassColorAt(final BlockPos blockPos) {
        final double double2 = MathHelper.clamp(this.getTemperature(blockPos), 0.0f, 1.0f);
        final double double3 = MathHelper.clamp(this.getRainfall(), 0.0f, 1.0f);
        return GrassColorHandler.getColor(double2, double3);
    }
    
    @Environment(EnvType.CLIENT)
    public int getFoliageColorAt(final BlockPos blockPos) {
        final double double2 = MathHelper.clamp(this.getTemperature(blockPos), 0.0f, 1.0f);
        final double double3 = MathHelper.clamp(this.getRainfall(), 0.0f, 1.0f);
        return FoliageColorHandler.getColor(double2, double3);
    }
    
    public void buildSurface(final Random random, final Chunk chunk, final int x, final int z, final int worldHeight, final double noise, final BlockState defaultBlock, final BlockState defaultFluid, final int seaLevel, final long seed) {
        this.surfaceBuilder.initSeed(seed);
        this.surfaceBuilder.generate(random, chunk, this, x, z, worldHeight, noise, defaultBlock, defaultFluid, seaLevel, seed);
    }
    
    public TemperatureGroup getTemperatureGroup() {
        if (this.category == Category.OCEAN) {
            return TemperatureGroup.OCEAN;
        }
        if (this.getTemperature() < 0.2) {
            return TemperatureGroup.COLD;
        }
        if (this.getTemperature() < 1.0) {
            return TemperatureGroup.MEDIUM;
        }
        return TemperatureGroup.WARM;
    }
    
    public final float getDepth() {
        return this.depth;
    }
    
    public final float getRainfall() {
        return this.downfall;
    }
    
    @Environment(EnvType.CLIENT)
    public TextComponent getTextComponent() {
        return new TranslatableTextComponent(this.getTranslationKey(), new Object[0]);
    }
    
    public String getTranslationKey() {
        if (this.translationKey == null) {
            this.translationKey = SystemUtil.createTranslationKey("biome", Registry.BIOME.getId(this));
        }
        return this.translationKey;
    }
    
    public final float getScale() {
        return this.scale;
    }
    
    public final float getTemperature() {
        return this.temperature;
    }
    
    public final int getWaterColor() {
        return this.waterColor;
    }
    
    public final int getWaterFogColor() {
        return this.waterFogColor;
    }
    
    public final Category getCategory() {
        return this.category;
    }
    
    public ConfiguredSurfaceBuilder<?> getSurfaceBuilder() {
        return this.surfaceBuilder;
    }
    
    public SurfaceConfig getSurfaceConfig() {
        return (SurfaceConfig)this.surfaceBuilder.getConfig();
    }
    
    @Nullable
    public String getParent() {
        return this.parent;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        BIOMES = Sets.newHashSet();
        PARENT_BIOME_ID_MAP = new IdList<Biome>();
        TEMPERATURE_NOISE = new OctaveSimplexNoiseSampler(new Random(1234L), 1);
        FOLIAGE_NOISE = new OctaveSimplexNoiseSampler(new Random(2345L), 1);
    }
    
    public enum TemperatureGroup
    {
        OCEAN("ocean"), 
        COLD("cold"), 
        MEDIUM("medium"), 
        WARM("warm");
        
        private static final Map<String, TemperatureGroup> NAME_MAP;
        private final String name;
        
        private TemperatureGroup(final String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
        
        static {
            NAME_MAP = Arrays.<TemperatureGroup>stream(values()).collect(Collectors.toMap(TemperatureGroup::getName, temperatureGroup -> temperatureGroup));
        }
    }
    
    public enum Category
    {
        NONE("none"), 
        TAIGA("taiga"), 
        EXTREME_HILLS("extreme_hills"), 
        JUNGLE("jungle"), 
        MESA("mesa"), 
        PLAINS("plains"), 
        SAVANNA("savanna"), 
        ICY("icy"), 
        THE_END("the_end"), 
        BEACH("beach"), 
        FOREST("forest"), 
        OCEAN("ocean"), 
        DESERT("desert"), 
        RIVER("river"), 
        SWAMP("swamp"), 
        MUSHROOM("mushroom"), 
        NETHER("nether");
        
        private static final Map<String, Category> NAME_MAP;
        private final String name;
        
        private Category(final String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
        
        static {
            NAME_MAP = Arrays.<Category>stream(values()).collect(Collectors.toMap(Category::getName, category -> category));
        }
    }
    
    public enum Precipitation
    {
        NONE("none"), 
        RAIN("rain"), 
        SNOW("snow");
        
        private static final Map<String, Precipitation> NAME_MAP;
        private final String name;
        
        private Precipitation(final String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
        
        static {
            NAME_MAP = Arrays.<Precipitation>stream(values()).collect(Collectors.toMap(Precipitation::getName, precipitation -> precipitation));
        }
    }
    
    public static class SpawnEntry extends WeightedPicker.Entry
    {
        public final EntityType<?> type;
        public final int minGroupSize;
        public final int maxGroupSize;
        
        public SpawnEntry(final EntityType<?> type, final int weight, final int minGroupSize, final int maxGroupSize) {
            super(weight);
            this.type = type;
            this.minGroupSize = minGroupSize;
            this.maxGroupSize = maxGroupSize;
        }
        
        @Override
        public String toString() {
            return EntityType.getId(this.type) + "*(" + this.minGroupSize + "-" + this.maxGroupSize + "):" + this.weight;
        }
    }
    
    public static class Settings
    {
        @Nullable
        private ConfiguredSurfaceBuilder<?> surfaceBuilder;
        @Nullable
        private Precipitation precipitation;
        @Nullable
        private Category category;
        @Nullable
        private Float depth;
        @Nullable
        private Float scale;
        @Nullable
        private Float temperature;
        @Nullable
        private Float downfall;
        @Nullable
        private Integer waterColor;
        @Nullable
        private Integer waterFogColor;
        @Nullable
        private String parent;
        
        public <SC extends SurfaceConfig> Settings configureSurfaceBuilder(final SurfaceBuilder<SC> surfaceBuilder, final SC surfaceConfig) {
            this.surfaceBuilder = new ConfiguredSurfaceBuilder<>(surfaceBuilder, surfaceConfig);
            return this;
        }
        
        public Settings surfaceBuilder(final ConfiguredSurfaceBuilder<?> configuredSurfaceBuilder) {
            this.surfaceBuilder = configuredSurfaceBuilder;
            return this;
        }
        
        public Settings precipitation(final Precipitation precipitation) {
            this.precipitation = precipitation;
            return this;
        }
        
        public Settings category(final Category category) {
            this.category = category;
            return this;
        }
        
        public Settings depth(final float float1) {
            this.depth = float1;
            return this;
        }
        
        public Settings scale(final float float1) {
            this.scale = float1;
            return this;
        }
        
        public Settings temperature(final float float1) {
            this.temperature = float1;
            return this;
        }
        
        public Settings downfall(final float float1) {
            this.downfall = float1;
            return this;
        }
        
        public Settings waterColor(final int integer) {
            this.waterColor = integer;
            return this;
        }
        
        public Settings waterFogColor(final int integer) {
            this.waterFogColor = integer;
            return this;
        }
        
        public Settings parent(@Nullable final String string) {
            this.parent = string;
            return this;
        }
        
        @Override
        public String toString() {
            return "BiomeBuilder{\nsurfaceBuilder=" + this.surfaceBuilder + ",\nprecipitation=" + this.precipitation + ",\nbiomeCategory=" + this.category + ",\ndepth=" + this.depth + ",\nscale=" + this.scale + ",\ntemperature=" + this.temperature + ",\ndownfall=" + this.downfall + ",\nwaterColor=" + this.waterColor + ",\nwaterFogColor=" + this.waterFogColor + ",\nparent='" + this.parent + '\'' + "\n" + '}';
        }
    }
}
