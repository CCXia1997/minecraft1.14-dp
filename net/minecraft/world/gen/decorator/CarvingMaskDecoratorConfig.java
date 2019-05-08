package net.minecraft.world.gen.decorator;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.GenerationStep;

public class CarvingMaskDecoratorConfig implements DecoratorConfig
{
    protected final GenerationStep.Carver step;
    protected final float probability;
    
    public CarvingMaskDecoratorConfig(final GenerationStep.Carver step, final float probability) {
        this.step = step;
        this.probability = probability;
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("step"), ops.createString(this.step.toString()), ops.createString("probability"), ops.createFloat(this.probability))));
    }
    
    public static CarvingMaskDecoratorConfig deserialize(final Dynamic<?> dynamic) {
        final GenerationStep.Carver carver2 = GenerationStep.Carver.valueOf(dynamic.get("step").asString(""));
        final float float3 = dynamic.get("probability").asFloat(0.0f);
        return new CarvingMaskDecoratorConfig(carver2, float3);
    }
}
