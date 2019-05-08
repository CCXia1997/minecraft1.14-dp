package net.minecraft.world.gen.decorator;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class NoiseHeightmapDecoratorConfig implements DecoratorConfig
{
    public final double noiseLevel;
    public final int belowNoise;
    public final int aboveNoise;
    
    public NoiseHeightmapDecoratorConfig(final double noiseLevel, final int belowNoise, final int aboveNoise) {
        this.noiseLevel = noiseLevel;
        this.belowNoise = belowNoise;
        this.aboveNoise = aboveNoise;
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("noise_level"), ops.createDouble(this.noiseLevel), ops.createString("below_noise"), ops.createInt(this.belowNoise), ops.createString("above_noise"), ops.createInt(this.aboveNoise))));
    }
    
    public static NoiseHeightmapDecoratorConfig deserialize(final Dynamic<?> dynamic) {
        final double double2 = dynamic.get("noise_level").asDouble(0.0);
        final int integer4 = dynamic.get("below_noise").asInt(0);
        final int integer5 = dynamic.get("above_noise").asInt(0);
        return new NoiseHeightmapDecoratorConfig(double2, integer4, integer5);
    }
}
