package net.minecraft.world.gen.feature;

import net.minecraft.util.registry.Registry;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;

public class DecoratedFeatureConfig implements FeatureConfig
{
    public final ConfiguredFeature<?> feature;
    public final ConfiguredDecorator<?> decorator;
    
    public DecoratedFeatureConfig(final ConfiguredFeature<?> feature, final ConfiguredDecorator<?> decorator) {
        this.feature = feature;
        this.decorator = decorator;
    }
    
    public <F extends FeatureConfig, D extends DecoratorConfig> DecoratedFeatureConfig(final Feature<F> feature, final F featureConfig, final Decorator<D> decorator, final D decoratorConfig) {
        this(new ConfiguredFeature<>(feature, featureConfig), new ConfiguredDecorator<>(decorator, decoratorConfig));
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("feature"), this.feature.<T>serialize(ops).getValue(), ops.createString("decorator"), this.decorator.<T>serialize(ops).getValue())));
    }
    
    @Override
    public String toString() {
        return String.format("< %s [%s | %s] >", this.getClass().getSimpleName(), Registry.FEATURE.getId(this.feature.feature), Registry.DECORATOR.getId(this.decorator.decorator));
    }
    
    public static <T> DecoratedFeatureConfig deserialize(final Dynamic<T> dynamic) {
        final ConfiguredFeature<?> configuredFeature2 = ConfiguredFeature.deserialize((com.mojang.datafixers.Dynamic<Object>)dynamic.get("feature").orElseEmptyMap());
        final ConfiguredDecorator<?> configuredDecorator3 = ConfiguredDecorator.deserialize((com.mojang.datafixers.Dynamic<Object>)dynamic.get("decorator").orElseEmptyMap());
        return new DecoratedFeatureConfig(configuredFeature2, configuredDecorator3);
    }
}
