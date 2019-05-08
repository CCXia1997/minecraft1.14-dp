package net.minecraft.structure.rule;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import com.mojang.datafixers.Dynamic;
import net.minecraft.block.Block;

public class RandomBlockMatchRuleTest extends AbstractRuleTest
{
    private final Block block;
    private final float probability;
    
    public RandomBlockMatchRuleTest(final Block block, final float probability) {
        this.block = block;
        this.probability = probability;
    }
    
    public <T> RandomBlockMatchRuleTest(final Dynamic<T> dynamic) {
        this(Registry.BLOCK.get(new Identifier(dynamic.get("block").asString(""))), dynamic.get("probability").asFloat(1.0f));
    }
    
    @Override
    public boolean test(final BlockState blockState, final Random random) {
        return blockState.getBlock() == this.block && random.nextFloat() < this.probability;
    }
    
    @Override
    protected RuleTest getRuleTest() {
        return RuleTest.f;
    }
    
    @Override
    protected <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("block"), dynamicOps.createString(Registry.BLOCK.getId(this.block).toString()), dynamicOps.createString("probability"), dynamicOps.createFloat(this.probability))));
    }
}
