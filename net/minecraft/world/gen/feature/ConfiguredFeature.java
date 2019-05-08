package net.minecraft.world.gen.feature;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.util.registry.Registry;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.Dynamic;

public class ConfiguredFeature<FC extends FeatureConfig>
{
    public final Feature<FC> feature;
    public final FC config;
    
    public ConfiguredFeature(final Feature<FC> feature, final FC config) {
        this.feature = feature;
        this.config = config;
    }
    
    public ConfiguredFeature(final Feature<FC> feature, final Dynamic<?> dynamic) {
        this((Feature<FeatureConfig>)feature, feature.deserializeConfig(dynamic));
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("name"), ops.createString(Registry.FEATURE.getId(this.feature).toString()), ops.createString("config"), this.config.<T>serialize(ops).getValue())));
    }
    
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos blockPos) {
        return this.feature.generate(world, generator, random, blockPos, this.config);
    }
    
    public static <T> ConfiguredFeature<?> deserialize(final Dynamic<T> dynamic) {
        final Feature<? extends FeatureConfig> feature2 = Registry.FEATURE.get(new Identifier(dynamic.get("name").asString("")));
        return new ConfiguredFeature<>(feature2, dynamic.get("config").orElseEmptyMap());
    }
}
