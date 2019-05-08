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

public class RandomFeatureEntry<FC extends FeatureConfig>
{
    public final Feature<FC> feature;
    public final FC config;
    public final Float chance;
    
    public RandomFeatureEntry(final Feature<FC> feature, final FC config, final Float chance) {
        this.feature = feature;
        this.config = config;
        this.chance = chance;
    }
    
    public RandomFeatureEntry(final Feature<FC> feature, final Dynamic<?> dynamic, final float float3) {
        this((Feature<FeatureConfig>)feature, feature.deserializeConfig(dynamic), float3);
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("name"), dynamicOps.createString(Registry.FEATURE.getId(this.feature).toString()), dynamicOps.createString("config"), this.config.<T>serialize(dynamicOps).getValue(), dynamicOps.createString("chance"), dynamicOps.createFloat((float)this.chance))));
    }
    
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, final Random random, final BlockPos blockPos) {
        return this.feature.generate(world, chunkGenerator, random, blockPos, this.config);
    }
    
    public static <T> RandomFeatureEntry<?> deserialize(final Dynamic<T> dynamic) {
        final Feature<? extends FeatureConfig> feature2 = Registry.FEATURE.get(new Identifier(dynamic.get("name").asString("")));
        return new RandomFeatureEntry<>(feature2, dynamic.get("config").orElseEmptyMap(), dynamic.get("chance").asFloat(0.0f));
    }
}
