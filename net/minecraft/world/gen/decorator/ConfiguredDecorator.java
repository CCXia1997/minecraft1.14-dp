package net.minecraft.world.gen.decorator;

import net.minecraft.util.Identifier;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.util.registry.Registry;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;

public class ConfiguredDecorator<DC extends DecoratorConfig>
{
    public final Decorator<DC> decorator;
    public final DC config;
    
    public ConfiguredDecorator(final Decorator<DC> decorator, final Dynamic<?> dynamic) {
        this((Decorator<DecoratorConfig>)decorator, decorator.deserialize(dynamic));
    }
    
    public ConfiguredDecorator(final Decorator<DC> decorator, final DC decoratorConfig) {
        this.decorator = decorator;
        this.config = decoratorConfig;
    }
    
    public <FC extends FeatureConfig> boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final ConfiguredFeature<FC> configuredFeature) {
        return this.decorator.<FC>generate(world, generator, random, pos, this.config, configuredFeature);
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("name"), dynamicOps.createString(Registry.DECORATOR.getId(this.decorator).toString()), dynamicOps.createString("config"), this.config.<T>serialize(dynamicOps).getValue())));
    }
    
    public static <T> ConfiguredDecorator<?> deserialize(final Dynamic<T> dynamic) {
        final Decorator<? extends DecoratorConfig> decorator2 = Registry.DECORATOR.get(new Identifier(dynamic.get("name").asString("")));
        return new ConfiguredDecorator<>(decorator2, dynamic.get("config").orElseEmptyMap());
    }
}
