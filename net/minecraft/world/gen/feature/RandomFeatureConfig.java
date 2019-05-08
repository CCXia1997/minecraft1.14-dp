package net.minecraft.world.gen.feature;

import java.util.function.Function;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import java.util.stream.Stream;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.List;

public class RandomFeatureConfig implements FeatureConfig
{
    public final List<RandomFeatureEntry<?>> features;
    public final ConfiguredFeature<?> defaultFeature;
    
    public RandomFeatureConfig(final List<RandomFeatureEntry<?>> features, final ConfiguredFeature<?> defaultFeature) {
        this.features = features;
        this.defaultFeature = defaultFeature;
    }
    
    public RandomFeatureConfig(final Feature<?>[] features, final FeatureConfig[] configs, final float[] chances, final Feature<?> defaultFeature, final FeatureConfig featureConfig) {
        this(IntStream.range(0, features.length).mapToObj(integer -> RandomFeatureConfig.makeEntry(features[integer], configs[integer], chances[integer])).collect(Collectors.toList()), RandomFeatureConfig.configure(defaultFeature, featureConfig));
    }
    
    private static <FC extends FeatureConfig> RandomFeatureEntry<FC> makeEntry(final Feature<FC> feature, final FeatureConfig config, final float chance) {
        return new RandomFeatureEntry<FC>(feature, (FC)config, chance);
    }
    
    private static <FC extends FeatureConfig> ConfiguredFeature<FC> configure(final Feature<FC> feature, final FeatureConfig config) {
        return new ConfiguredFeature<FC>(feature, (FC)config);
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        final T object2 = (T)ops.createList((Stream)this.features.stream().map(randomFeatureEntry -> randomFeatureEntry.<T>serialize(ops).getValue()));
        final T object3 = (T)this.defaultFeature.<T>serialize(ops).getValue();
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.<Object, T>of(ops.createString("features"), object2, ops.createString("default"), object3)));
    }
    
    public static <T> RandomFeatureConfig deserialize(final Dynamic<T> dynamic) {
        final List<RandomFeatureEntry<?>> list2 = (List<RandomFeatureEntry<?>>)dynamic.get("features").asList((Function)RandomFeatureEntry::deserialize);
        final ConfiguredFeature<?> configuredFeature3 = ConfiguredFeature.deserialize((com.mojang.datafixers.Dynamic<Object>)dynamic.get("default").orElseEmptyMap());
        return new RandomFeatureConfig(list2, configuredFeature3);
    }
}
