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

public class RandomRandomFeatureConfig implements FeatureConfig
{
    public final List<ConfiguredFeature<?>> features;
    public final int count;
    
    public RandomRandomFeatureConfig(final List<ConfiguredFeature<?>> features, final int count) {
        this.features = features;
        this.count = count;
    }
    
    public RandomRandomFeatureConfig(final Feature<?>[] features, final FeatureConfig[] configs, final int count) {
        this(IntStream.range(0, features.length).mapToObj(integer -> RandomRandomFeatureConfig.configure(features[integer], configs[integer])).collect(Collectors.toList()), count);
    }
    
    private static <FC extends FeatureConfig> ConfiguredFeature<?> configure(final Feature<FC> feature, final FeatureConfig config) {
        return new ConfiguredFeature<>(feature, config);
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("features"), ops.createList((Stream)this.features.stream().map(configuredFeature -> configuredFeature.<T>serialize(ops).getValue())), ops.createString("count"), ops.createInt(this.count))));
    }
    
    public static <T> RandomRandomFeatureConfig deserialize(final Dynamic<T> dynamic) {
        final List<ConfiguredFeature<?>> list2 = (List<ConfiguredFeature<?>>)dynamic.get("features").asList((Function)ConfiguredFeature::deserialize);
        final int integer3 = dynamic.get("count").asInt(0);
        return new RandomRandomFeatureConfig(list2, integer3);
    }
}
