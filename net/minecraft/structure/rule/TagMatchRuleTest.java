package net.minecraft.structure.rule;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.tag.BlockTags;
import com.mojang.datafixers.Dynamic;
import net.minecraft.block.Block;
import net.minecraft.tag.Tag;

public class TagMatchRuleTest extends AbstractRuleTest
{
    private final Tag<Block> tag;
    
    public TagMatchRuleTest(final Tag<Block> tag) {
        this.tag = tag;
    }
    
    public <T> TagMatchRuleTest(final Dynamic<T> dynamic) {
        this(BlockTags.getContainer().get(new Identifier(dynamic.get("tag").asString(""))));
    }
    
    @Override
    public boolean test(final BlockState blockState, final Random random) {
        return blockState.matches(this.tag);
    }
    
    @Override
    protected RuleTest getRuleTest() {
        return RuleTest.e;
    }
    
    @Override
    protected <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("tag"), dynamicOps.createString(this.tag.getId().toString()))));
    }
}
