package net.minecraft.world.gen.chunk;

import net.minecraft.util.SystemUtil;
import net.minecraft.world.gen.decorator.LakeDecoratorConfig;
import net.minecraft.world.gen.feature.LakeFeatureConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.Feature;
import org.apache.logging.log4j.LogManager;
import net.minecraft.world.gen.feature.PillagerOutpostFeatureConfig;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.VillageFeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;
import java.util.HashMap;
import net.minecraft.world.biome.Biomes;
import com.google.common.base.Splitter;
import java.util.Collection;
import java.util.stream.Collector;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.mojang.datafixers.util.Pair;
import java.util.stream.Stream;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Collections;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import java.util.Locale;
import java.util.Iterator;
import net.minecraft.block.Blocks;
import javax.annotation.Nullable;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.block.Block;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import java.util.List;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.GenerationStep;
import java.util.Map;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import org.apache.logging.log4j.Logger;

public class FlatChunkGeneratorConfig extends ChunkGeneratorConfig
{
    private static final Logger LOGGER;
    private static final ConfiguredFeature<?> MINESHAFT;
    private static final ConfiguredFeature<?> VILLAGE;
    private static final ConfiguredFeature<?> STRONGHOLD;
    private static final ConfiguredFeature<?> SWAMP_HUT;
    private static final ConfiguredFeature<?> DESERT_PYRAMID;
    private static final ConfiguredFeature<?> JUNGLE_TEMPLE;
    private static final ConfiguredFeature<?> IGLOO;
    private static final ConfiguredFeature<?> SHIPWRECK;
    private static final ConfiguredFeature<?> OCEAN_MONUMENT;
    private static final ConfiguredFeature<?> WATER_LAKE;
    private static final ConfiguredFeature<?> LAVA_LAKE;
    private static final ConfiguredFeature<?> END_CITY;
    private static final ConfiguredFeature<?> WOODLAND_MANSION;
    private static final ConfiguredFeature<?> NETHER_BRIDGE;
    private static final ConfiguredFeature<?> OCEAN_RUIN;
    private static final ConfiguredFeature<?> M;
    public static final Map<ConfiguredFeature<?>, GenerationStep.Feature> FEATURE_TO_GENERATION_STEP;
    public static final Map<String, ConfiguredFeature<?>[]> STRUCTURE_TO_FEATURES;
    public static final Map<ConfiguredFeature<?>, FeatureConfig> FEATURE_TO_FEATURE_CONFIG;
    private final List<FlatChunkGeneratorLayer> layers;
    private final Map<String, Map<String, String>> structures;
    private Biome biome;
    private final BlockState[] layerBlocks;
    private boolean hasNoTerrain;
    private int groundHeight;
    
    public FlatChunkGeneratorConfig() {
        this.layers = Lists.newArrayList();
        this.structures = Maps.newHashMap();
        this.layerBlocks = new BlockState[256];
    }
    
    @Nullable
    public static Block parseBlock(final String string) {
        try {
            final Identifier identifier2 = new Identifier(string);
            return Registry.BLOCK.getOrEmpty(identifier2).orElse(null);
        }
        catch (IllegalArgumentException illegalArgumentException2) {
            FlatChunkGeneratorConfig.LOGGER.warn("Invalid blockstate: {}", string, illegalArgumentException2);
            return null;
        }
    }
    
    public Biome getBiome() {
        return this.biome;
    }
    
    public void setBiome(final Biome biome) {
        this.biome = biome;
    }
    
    public Map<String, Map<String, String>> getStructures() {
        return this.structures;
    }
    
    public List<FlatChunkGeneratorLayer> getLayers() {
        return this.layers;
    }
    
    public void updateLayerBlocks() {
        int integer1 = 0;
        for (final FlatChunkGeneratorLayer flatChunkGeneratorLayer3 : this.layers) {
            flatChunkGeneratorLayer3.setStartY(integer1);
            integer1 += flatChunkGeneratorLayer3.getThickness();
        }
        this.groundHeight = 0;
        this.hasNoTerrain = true;
        integer1 = 0;
        for (final FlatChunkGeneratorLayer flatChunkGeneratorLayer3 : this.layers) {
            for (int integer2 = flatChunkGeneratorLayer3.getStartY(); integer2 < flatChunkGeneratorLayer3.getStartY() + flatChunkGeneratorLayer3.getThickness(); ++integer2) {
                final BlockState blockState5 = flatChunkGeneratorLayer3.getBlockState();
                if (blockState5.getBlock() != Blocks.AIR) {
                    this.hasNoTerrain = false;
                    this.layerBlocks[integer2] = blockState5;
                }
            }
            if (flatChunkGeneratorLayer3.getBlockState().getBlock() == Blocks.AIR) {
                integer1 += flatChunkGeneratorLayer3.getThickness();
            }
            else {
                this.groundHeight += flatChunkGeneratorLayer3.getThickness() + integer1;
                integer1 = 0;
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder stringBuilder1 = new StringBuilder();
        for (int integer2 = 0; integer2 < this.layers.size(); ++integer2) {
            if (integer2 > 0) {
                stringBuilder1.append(",");
            }
            stringBuilder1.append(this.layers.get(integer2));
        }
        stringBuilder1.append(";");
        stringBuilder1.append(Registry.BIOME.getId(this.biome));
        stringBuilder1.append(";");
        if (!this.structures.isEmpty()) {
            int integer2 = 0;
            for (final Map.Entry<String, Map<String, String>> entry4 : this.structures.entrySet()) {
                if (integer2++ > 0) {
                    stringBuilder1.append(",");
                }
                stringBuilder1.append(entry4.getKey().toLowerCase(Locale.ROOT));
                final Map<String, String> map5 = entry4.getValue();
                if (!map5.isEmpty()) {
                    stringBuilder1.append("(");
                    int integer3 = 0;
                    for (final Map.Entry<String, String> entry5 : map5.entrySet()) {
                        if (integer3++ > 0) {
                            stringBuilder1.append(" ");
                        }
                        stringBuilder1.append(entry5.getKey());
                        stringBuilder1.append("=");
                        stringBuilder1.append(entry5.getValue());
                    }
                    stringBuilder1.append(")");
                }
            }
        }
        return stringBuilder1.toString();
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    private static FlatChunkGeneratorLayer parseLayerString(final String string, final int startY) {
        final String[] arr3 = string.split("\\*", 2);
        int integer4 = 0;
        Block block5 = null;
        Label_0056: {
            if (arr3.length == 2) {
                try {
                    integer4 = MathHelper.clamp(Integer.parseInt(arr3[0]), 0, 256 - startY);
                    break Label_0056;
                }
                catch (NumberFormatException numberFormatException5) {
                    FlatChunkGeneratorConfig.LOGGER.error("Error while parsing flat world string => {}", numberFormatException5.getMessage());
                    return null;
                }
            }
            integer4 = 1;
            try {
                block5 = parseBlock(arr3[arr3.length - 1]);
            }
            catch (Exception exception6) {
                FlatChunkGeneratorConfig.LOGGER.error("Error while parsing flat world string => {}", exception6.getMessage());
                return null;
            }
        }
        if (block5 == null) {
            FlatChunkGeneratorConfig.LOGGER.error("Error while parsing flat world string => Unknown block, {}", arr3[arr3.length - 1]);
            return null;
        }
        final FlatChunkGeneratorLayer flatChunkGeneratorLayer6 = new FlatChunkGeneratorLayer(integer4, block5);
        flatChunkGeneratorLayer6.setStartY(startY);
        return flatChunkGeneratorLayer6;
    }
    
    @Environment(EnvType.CLIENT)
    private static List<FlatChunkGeneratorLayer> parseLayersString(final String string) {
        final List<FlatChunkGeneratorLayer> list2 = Lists.newArrayList();
        final String[] arr3 = string.split(",");
        int integer4 = 0;
        for (final String string2 : arr3) {
            final FlatChunkGeneratorLayer flatChunkGeneratorLayer9 = parseLayerString(string2, integer4);
            if (flatChunkGeneratorLayer9 == null) {
                return Collections.<FlatChunkGeneratorLayer>emptyList();
            }
            list2.add(flatChunkGeneratorLayer9);
            integer4 += flatChunkGeneratorLayer9.getThickness();
        }
        return list2;
    }
    
    @Environment(EnvType.CLIENT)
    public <T> Dynamic<T> toDynamic(final DynamicOps<T> dynamicOps) {
        final T object2 = (T)dynamicOps.createList((Stream)this.layers.stream().map(flatChunkGeneratorLayer -> dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("height"), dynamicOps.createInt(flatChunkGeneratorLayer.getThickness()), dynamicOps.createString("block"), dynamicOps.createString(Registry.BLOCK.getId(flatChunkGeneratorLayer.getBlockState().getBlock()).toString())))));
        final T object3 = (T)dynamicOps.createMap((Map)this.structures.entrySet().stream().map(entry -> Pair.of(dynamicOps.createString(entry.getKey().toLowerCase(Locale.ROOT)), dynamicOps.createMap((Map)((Map)entry.getValue()).entrySet().stream().map(entry -> Pair.of(dynamicOps.createString((String)entry.getKey()), dynamicOps.createString((String)entry.getValue()))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("layers"), object2, dynamicOps.createString("biome"), dynamicOps.createString(Registry.BIOME.getId(this.biome).toString()), dynamicOps.createString("structures"), object3)));
    }
    
    public static FlatChunkGeneratorConfig fromDynamic(final Dynamic<?> dynamic) {
        final FlatChunkGeneratorConfig flatChunkGeneratorConfig2 = ChunkGeneratorType.e.createSettings();
        final List<Pair<Integer, Block>> list3 = (List<Pair<Integer, Block>>)dynamic.get("layers").asList(dynamic -> Pair.of(dynamic.get("height").asInt(1), parseBlock(dynamic.get("block").asString(""))));
        if (list3.stream().anyMatch(pair -> pair.getSecond() == null)) {
            return getDefaultConfig();
        }
        final List<FlatChunkGeneratorLayer> list4 = list3.stream().map(pair -> new FlatChunkGeneratorLayer((int)pair.getFirst(), (Block)pair.getSecond())).collect(Collectors.toList());
        if (list4.isEmpty()) {
            return getDefaultConfig();
        }
        flatChunkGeneratorConfig2.getLayers().addAll(list4);
        flatChunkGeneratorConfig2.updateLayerBlocks();
        flatChunkGeneratorConfig2.setBiome(Registry.BIOME.get(new Identifier(dynamic.get("biome").asString(""))));
        dynamic.get("structures").flatMap((Function)Dynamic::getMapValues).ifPresent(map -> map.keySet().forEach(dynamic -> dynamic.asString().map(string -> flatChunkGeneratorConfig2.getStructures().put(string, Maps.newHashMap()))));
        return flatChunkGeneratorConfig2;
    }
    
    @Environment(EnvType.CLIENT)
    public static FlatChunkGeneratorConfig fromString(final String string) {
        final Iterator<String> iterator2 = Splitter.on(';').split(string).iterator();
        if (!iterator2.hasNext()) {
            return getDefaultConfig();
        }
        final FlatChunkGeneratorConfig flatChunkGeneratorConfig3 = ChunkGeneratorType.e.createSettings();
        final List<FlatChunkGeneratorLayer> list4 = parseLayersString(iterator2.next());
        if (list4.isEmpty()) {
            return getDefaultConfig();
        }
        flatChunkGeneratorConfig3.getLayers().addAll(list4);
        flatChunkGeneratorConfig3.updateLayerBlocks();
        final Biome biome5 = iterator2.hasNext() ? Registry.BIOME.get(new Identifier(iterator2.next())) : null;
        flatChunkGeneratorConfig3.setBiome((biome5 == null) ? Biomes.c : biome5);
        if (iterator2.hasNext()) {
            final String[] split;
            final String[] arr6 = split = iterator2.next().toLowerCase(Locale.ROOT).split(",");
            for (final String string2 : split) {
                final String[] arr7 = string2.split("\\(", 2);
                if (!arr7[0].isEmpty()) {
                    flatChunkGeneratorConfig3.addStructure(arr7[0]);
                    if (arr7.length > 1 && arr7[1].endsWith(")") && arr7[1].length() > 1) {
                        final String[] split2;
                        final String[] arr8 = split2 = arr7[1].substring(0, arr7[1].length() - 1).split(" ");
                        for (final String string3 : split2) {
                            final String[] arr9 = string3.split("=", 2);
                            if (arr9.length == 2) {
                                flatChunkGeneratorConfig3.a(arr7[0], arr9[0], arr9[1]);
                            }
                        }
                    }
                }
            }
        }
        else {
            flatChunkGeneratorConfig3.getStructures().put("village", Maps.newHashMap());
        }
        return flatChunkGeneratorConfig3;
    }
    
    @Environment(EnvType.CLIENT)
    private void addStructure(final String id) {
        final Map<String, String> map2 = Maps.newHashMap();
        this.structures.put(id, map2);
    }
    
    @Environment(EnvType.CLIENT)
    private void a(final String string1, final String string2, final String string3) {
        this.structures.get(string1).put(string2, string3);
        if ("village".equals(string1) && "distance".equals(string2)) {
            this.villageDistance = MathHelper.parseInt(string3, this.villageDistance, 9);
        }
        if ("biome_1".equals(string1) && "distance".equals(string2)) {
            this.templeDistance = MathHelper.parseInt(string3, this.templeDistance, 9);
        }
        if ("stronghold".equals(string1)) {
            if ("distance".equals(string2)) {
                this.strongholdDistance = MathHelper.parseInt(string3, this.strongholdDistance, 1);
            }
            else if ("count".equals(string2)) {
                this.strongholdCount = MathHelper.parseInt(string3, this.strongholdCount, 1);
            }
            else if ("spread".equals(string2)) {
                this.strongholdSpread = MathHelper.parseInt(string3, this.strongholdSpread, 1);
            }
        }
        if ("oceanmonument".equals(string1)) {
            if ("separation".equals(string2)) {
                this.oceanMonumentSeparation = MathHelper.parseInt(string3, this.oceanMonumentSeparation, 1);
            }
            else if ("spacing".equals(string2)) {
                this.oceanMonumentSpacing = MathHelper.parseInt(string3, this.oceanMonumentSpacing, 1);
            }
        }
        if ("endcity".equals(string1) && "distance".equals(string2)) {
            this.endCityDistance = MathHelper.parseInt(string3, this.endCityDistance, 1);
        }
        if ("mansion".equals(string1) && "distance".equals(string2)) {
            this.mansionDistance = MathHelper.parseInt(string3, this.mansionDistance, 1);
        }
    }
    
    public static FlatChunkGeneratorConfig getDefaultConfig() {
        final FlatChunkGeneratorConfig flatChunkGeneratorConfig1 = ChunkGeneratorType.e.createSettings();
        flatChunkGeneratorConfig1.setBiome(Biomes.c);
        flatChunkGeneratorConfig1.getLayers().add(new FlatChunkGeneratorLayer(1, Blocks.z));
        flatChunkGeneratorConfig1.getLayers().add(new FlatChunkGeneratorLayer(2, Blocks.j));
        flatChunkGeneratorConfig1.getLayers().add(new FlatChunkGeneratorLayer(1, Blocks.i));
        flatChunkGeneratorConfig1.updateLayerBlocks();
        flatChunkGeneratorConfig1.getStructures().put("village", Maps.newHashMap());
        return flatChunkGeneratorConfig1;
    }
    
    public boolean hasNoTerrain() {
        return this.hasNoTerrain;
    }
    
    public BlockState[] getLayerBlocks() {
        return this.layerBlocks;
    }
    
    public void a(final int integer) {
        this.layerBlocks[integer] = null;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        MINESHAFT = Biome.<MineshaftFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.MINESHAFT, new MineshaftFeatureConfig(0.004, MineshaftFeature.Type.NORMAL), Decorator.NOPE, DecoratorConfig.DEFAULT);
        VILLAGE = Biome.<VillageFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.VILLAGE, new VillageFeatureConfig("village/plains/town_centers", 6), Decorator.NOPE, DecoratorConfig.DEFAULT);
        STRONGHOLD = Biome.<DefaultFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.STRONGHOLD, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT);
        SWAMP_HUT = Biome.<DefaultFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.SWAMP_HUT, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT);
        DESERT_PYRAMID = Biome.<DefaultFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.DESERT_PYRAMID, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT);
        JUNGLE_TEMPLE = Biome.<DefaultFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.JUNGLE_TEMPLE, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT);
        IGLOO = Biome.<DefaultFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.IGLOO, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT);
        SHIPWRECK = Biome.<ShipwreckFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.SHIPWRECK, new ShipwreckFeatureConfig(false), Decorator.NOPE, DecoratorConfig.DEFAULT);
        OCEAN_MONUMENT = Biome.<DefaultFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.OCEAN_MONUMENT, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT);
        WATER_LAKE = Biome.<LakeFeatureConfig, LakeDecoratorConfig>configureFeature(Feature.aq, new LakeFeatureConfig(Blocks.A.getDefaultState()), Decorator.E, new LakeDecoratorConfig(4));
        LAVA_LAKE = Biome.<LakeFeatureConfig, LakeDecoratorConfig>configureFeature(Feature.aq, new LakeFeatureConfig(Blocks.B.getDefaultState()), Decorator.D, new LakeDecoratorConfig(80));
        END_CITY = Biome.<DefaultFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.END_CITY, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT);
        WOODLAND_MANSION = Biome.<DefaultFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.WOODLAND_MANSION, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT);
        NETHER_BRIDGE = Biome.<DefaultFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.NETHER_BRIDGE, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT);
        OCEAN_RUIN = Biome.<OceanRuinFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.OCEAN_RUIN, new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.COLD, 0.3f, 0.1f), Decorator.NOPE, DecoratorConfig.DEFAULT);
        M = Biome.<PillagerOutpostFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.PILLAGER_OUTPOST, new PillagerOutpostFeatureConfig(0.004), Decorator.NOPE, DecoratorConfig.DEFAULT);
        FEATURE_TO_GENERATION_STEP = SystemUtil.<Map<ConfiguredFeature<?>, GenerationStep.Feature>>consume(Maps.newHashMap(), hashMap -> {
            hashMap.put(FlatChunkGeneratorConfig.MINESHAFT, GenerationStep.Feature.UNDERGROUND_STRUCTURES);
            hashMap.put(FlatChunkGeneratorConfig.VILLAGE, GenerationStep.Feature.SURFACE_STRUCTURES);
            hashMap.put(FlatChunkGeneratorConfig.STRONGHOLD, GenerationStep.Feature.UNDERGROUND_STRUCTURES);
            hashMap.put(FlatChunkGeneratorConfig.SWAMP_HUT, GenerationStep.Feature.SURFACE_STRUCTURES);
            hashMap.put(FlatChunkGeneratorConfig.DESERT_PYRAMID, GenerationStep.Feature.SURFACE_STRUCTURES);
            hashMap.put(FlatChunkGeneratorConfig.JUNGLE_TEMPLE, GenerationStep.Feature.SURFACE_STRUCTURES);
            hashMap.put(FlatChunkGeneratorConfig.IGLOO, GenerationStep.Feature.SURFACE_STRUCTURES);
            hashMap.put(FlatChunkGeneratorConfig.SHIPWRECK, GenerationStep.Feature.SURFACE_STRUCTURES);
            hashMap.put(FlatChunkGeneratorConfig.OCEAN_RUIN, GenerationStep.Feature.SURFACE_STRUCTURES);
            hashMap.put(FlatChunkGeneratorConfig.WATER_LAKE, GenerationStep.Feature.LOCAL_MODIFICATIONS);
            hashMap.put(FlatChunkGeneratorConfig.LAVA_LAKE, GenerationStep.Feature.LOCAL_MODIFICATIONS);
            hashMap.put(FlatChunkGeneratorConfig.END_CITY, GenerationStep.Feature.SURFACE_STRUCTURES);
            hashMap.put(FlatChunkGeneratorConfig.WOODLAND_MANSION, GenerationStep.Feature.SURFACE_STRUCTURES);
            hashMap.put(FlatChunkGeneratorConfig.NETHER_BRIDGE, GenerationStep.Feature.UNDERGROUND_STRUCTURES);
            hashMap.put(FlatChunkGeneratorConfig.OCEAN_MONUMENT, GenerationStep.Feature.SURFACE_STRUCTURES);
            hashMap.put(FlatChunkGeneratorConfig.M, GenerationStep.Feature.SURFACE_STRUCTURES);
            return;
        });
        STRUCTURE_TO_FEATURES = SystemUtil.<Map<String, ConfiguredFeature<?>[]>>consume(Maps.newHashMap(), hashMap -> {
            hashMap.put("mineshaft", new ConfiguredFeature[] { FlatChunkGeneratorConfig.MINESHAFT });
            hashMap.put("village", new ConfiguredFeature[] { FlatChunkGeneratorConfig.VILLAGE });
            hashMap.put("stronghold", new ConfiguredFeature[] { FlatChunkGeneratorConfig.STRONGHOLD });
            hashMap.put("biome_1", new ConfiguredFeature[] { FlatChunkGeneratorConfig.SWAMP_HUT, FlatChunkGeneratorConfig.DESERT_PYRAMID, FlatChunkGeneratorConfig.JUNGLE_TEMPLE, FlatChunkGeneratorConfig.IGLOO, FlatChunkGeneratorConfig.OCEAN_RUIN, FlatChunkGeneratorConfig.SHIPWRECK });
            hashMap.put("oceanmonument", new ConfiguredFeature[] { FlatChunkGeneratorConfig.OCEAN_MONUMENT });
            hashMap.put("lake", new ConfiguredFeature[] { FlatChunkGeneratorConfig.WATER_LAKE });
            hashMap.put("lava_lake", new ConfiguredFeature[] { FlatChunkGeneratorConfig.LAVA_LAKE });
            hashMap.put("endcity", new ConfiguredFeature[] { FlatChunkGeneratorConfig.END_CITY });
            hashMap.put("mansion", new ConfiguredFeature[] { FlatChunkGeneratorConfig.WOODLAND_MANSION });
            hashMap.put("fortress", new ConfiguredFeature[] { FlatChunkGeneratorConfig.NETHER_BRIDGE });
            hashMap.put("pillager_outpost", new ConfiguredFeature[] { FlatChunkGeneratorConfig.M });
            return;
        });
        FEATURE_TO_FEATURE_CONFIG = SystemUtil.<Map<ConfiguredFeature<?>, FeatureConfig>>consume(Maps.newHashMap(), hashMap -> {
            hashMap.put(FlatChunkGeneratorConfig.MINESHAFT, new MineshaftFeatureConfig(0.004, MineshaftFeature.Type.NORMAL));
            hashMap.put(FlatChunkGeneratorConfig.VILLAGE, (MineshaftFeatureConfig)new VillageFeatureConfig("village/plains/town_centers", 6));
            hashMap.put(FlatChunkGeneratorConfig.STRONGHOLD, (MineshaftFeatureConfig)FeatureConfig.DEFAULT);
            hashMap.put(FlatChunkGeneratorConfig.SWAMP_HUT, (MineshaftFeatureConfig)FeatureConfig.DEFAULT);
            hashMap.put(FlatChunkGeneratorConfig.DESERT_PYRAMID, (MineshaftFeatureConfig)FeatureConfig.DEFAULT);
            hashMap.put(FlatChunkGeneratorConfig.JUNGLE_TEMPLE, (MineshaftFeatureConfig)FeatureConfig.DEFAULT);
            hashMap.put(FlatChunkGeneratorConfig.IGLOO, (MineshaftFeatureConfig)FeatureConfig.DEFAULT);
            hashMap.put(FlatChunkGeneratorConfig.OCEAN_RUIN, (MineshaftFeatureConfig)new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.COLD, 0.3f, 0.9f));
            hashMap.put(FlatChunkGeneratorConfig.SHIPWRECK, (MineshaftFeatureConfig)new ShipwreckFeatureConfig(false));
            hashMap.put(FlatChunkGeneratorConfig.OCEAN_MONUMENT, (MineshaftFeatureConfig)FeatureConfig.DEFAULT);
            hashMap.put(FlatChunkGeneratorConfig.END_CITY, (MineshaftFeatureConfig)FeatureConfig.DEFAULT);
            hashMap.put(FlatChunkGeneratorConfig.WOODLAND_MANSION, (MineshaftFeatureConfig)FeatureConfig.DEFAULT);
            hashMap.put(FlatChunkGeneratorConfig.NETHER_BRIDGE, (MineshaftFeatureConfig)FeatureConfig.DEFAULT);
            hashMap.put(FlatChunkGeneratorConfig.M, (MineshaftFeatureConfig)new PillagerOutpostFeatureConfig(0.004));
        });
    }
}
