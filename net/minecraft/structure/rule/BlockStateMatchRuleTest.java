package net.minecraft.structure.rule;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import net.minecraft.block.BlockState;

public class BlockStateMatchRuleTest extends AbstractRuleTest
{
    private final BlockState blockState;
    
    public BlockStateMatchRuleTest(final BlockState blockState) {
        this.blockState = blockState;
    }
    
    public <T> BlockStateMatchRuleTest(final Dynamic<T> dynamic) {
        this(BlockState.deserialize((com.mojang.datafixers.Dynamic<Object>)dynamic.get("blockstate").orElseEmptyMap()));
    }
    
    @Override
    public boolean test(final BlockState blockState, final Random random) {
        return blockState == this.blockState;
    }
    
    @Override
    protected RuleTest getRuleTest() {
        return RuleTest.d;
    }
    
    @Override
    protected <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("blockstate"), BlockState.<T>serialize(dynamicOps, this.blockState).getValue())));
    }
}
