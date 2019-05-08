package net.minecraft.structure.rule;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import net.minecraft.block.BlockState;

public class RandomBlockStateMatchRuleTest extends AbstractRuleTest
{
    private final BlockState blockState;
    private final float probability;
    
    public RandomBlockStateMatchRuleTest(final BlockState blockState, final float probability) {
        this.blockState = blockState;
        this.probability = probability;
    }
    
    public <T> RandomBlockStateMatchRuleTest(final Dynamic<T> dynamic) {
        this(BlockState.deserialize((com.mojang.datafixers.Dynamic<Object>)dynamic.get("blockstate").orElseEmptyMap()), dynamic.get("probability").asFloat(1.0f));
    }
    
    @Override
    public boolean test(final BlockState blockState, final Random random) {
        return blockState == this.blockState && random.nextFloat() < this.probability;
    }
    
    @Override
    protected RuleTest getRuleTest() {
        return RuleTest.g;
    }
    
    @Override
    protected <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("blockstate"), BlockState.<T>serialize(dynamicOps, this.blockState).getValue(), dynamicOps.createString("probability"), dynamicOps.createFloat(this.probability))));
    }
}
