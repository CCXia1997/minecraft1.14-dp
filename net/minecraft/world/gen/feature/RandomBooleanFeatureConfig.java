package net.minecraft.world.gen.feature;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class RandomBooleanFeatureConfig implements FeatureConfig
{
    public final ConfiguredFeature<?> featureTrue;
    public final ConfiguredFeature<?> featureFalse;
    
    public RandomBooleanFeatureConfig(final ConfiguredFeature<?> featureTrue, final ConfiguredFeature<?> featureFalse) {
        this.featureTrue = featureTrue;
        this.featureFalse = featureFalse;
    }
    
    public RandomBooleanFeatureConfig(final Feature<?> featureTrue, final FeatureConfig featureConfigTrue, final Feature<?> featureFalse, final FeatureConfig featureConfig4) {
        this(RandomBooleanFeatureConfig.configure(featureTrue, featureConfigTrue), RandomBooleanFeatureConfig.configure(featureFalse, featureConfig4));
    }
    
    private static <FC extends FeatureConfig> ConfiguredFeature<FC> configure(final Feature<FC> feature, final FeatureConfig config) {
        return new ConfiguredFeature<FC>(feature, (FC)config);
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("feature_true"), this.featureTrue.<T>serialize(ops).getValue(), ops.createString("feature_false"), this.featureFalse.<T>serialize(ops).getValue())));
    }
    
    public static <T> RandomBooleanFeatureConfig deserialize(final Dynamic<T> dynamic) {
        final ConfiguredFeature<?> configuredFeature2 = ConfiguredFeature.deserialize((com.mojang.datafixers.Dynamic<Object>)dynamic.get("feature_true").orElseEmptyMap());
        final ConfiguredFeature<?> configuredFeature3 = ConfiguredFeature.deserialize((com.mojang.datafixers.Dynamic<Object>)dynamic.get("feature_false").orElseEmptyMap());
        return new RandomBooleanFeatureConfig(configuredFeature2, configuredFeature3);
    }
}
