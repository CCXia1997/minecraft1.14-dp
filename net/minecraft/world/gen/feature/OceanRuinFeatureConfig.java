package net.minecraft.world.gen.feature;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class OceanRuinFeatureConfig implements FeatureConfig
{
    public final OceanRuinFeature.BiomeType biomeType;
    public final float largeProbability;
    public final float clusterProbability;
    
    public OceanRuinFeatureConfig(final OceanRuinFeature.BiomeType biomeType, final float largeProbability, final float clusterProbability) {
        this.biomeType = biomeType;
        this.largeProbability = largeProbability;
        this.clusterProbability = clusterProbability;
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("biome_temp"), ops.createString(this.biomeType.getName()), ops.createString("large_probability"), ops.createFloat(this.largeProbability), ops.createString("cluster_probability"), ops.createFloat(this.clusterProbability))));
    }
    
    public static <T> OceanRuinFeatureConfig deserialize(final Dynamic<T> dynamic) {
        final OceanRuinFeature.BiomeType biomeType2 = OceanRuinFeature.BiomeType.byName(dynamic.get("biome_temp").asString(""));
        final float float3 = dynamic.get("large_probability").asFloat(0.0f);
        final float float4 = dynamic.get("cluster_probability").asFloat(0.0f);
        return new OceanRuinFeatureConfig(biomeType2, float3, float4);
    }
}
