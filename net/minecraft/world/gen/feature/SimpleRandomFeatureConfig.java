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

public class SimpleRandomFeatureConfig implements FeatureConfig
{
    public final List<ConfiguredFeature<?>> features;
    
    public SimpleRandomFeatureConfig(final List<ConfiguredFeature<?>> features) {
        this.features = features;
    }
    
    public SimpleRandomFeatureConfig(final Feature<?>[] features, final FeatureConfig[] configs) {
        this(IntStream.range(0, features.length).mapToObj(integer -> SimpleRandomFeatureConfig.configure(features[integer], configs[integer])).collect(Collectors.toList()));
    }
    
    private static <FC extends FeatureConfig> ConfiguredFeature<FC> configure(final Feature<FC> feature, final FeatureConfig config) {
        return new ConfiguredFeature<FC>(feature, (FC)config);
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("features"), ops.createList((Stream)this.features.stream().map(configuredFeature -> configuredFeature.<T>serialize(ops).getValue())))));
    }
    
    public static <T> SimpleRandomFeatureConfig deserialize(final Dynamic<T> dynamic) {
        final List<ConfiguredFeature<?>> list2 = (List<ConfiguredFeature<?>>)dynamic.get("features").asList((Function)ConfiguredFeature::deserialize);
        return new SimpleRandomFeatureConfig(list2);
    }
}
