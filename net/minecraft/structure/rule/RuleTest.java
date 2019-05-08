package net.minecraft.structure.rule;

import com.mojang.datafixers.Dynamic;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.DynamicDeserializer;

public interface RuleTest extends DynamicDeserializer<AbstractRuleTest>
{
    public static final RuleTest b = register("always_true", dynamic -> AlwaysTrueRuleTest.INSTANCE);
    public static final RuleTest c = register("block_match", BlockMatchRuleTest::new);
    public static final RuleTest d = register("blockstate_match", BlockStateMatchRuleTest::new);
    public static final RuleTest e = register("tag_match", TagMatchRuleTest::new);
    public static final RuleTest f = register("random_block_match", RandomBlockMatchRuleTest::new);
    public static final RuleTest g = register("random_blockstate_match", RandomBlockStateMatchRuleTest::new);
    
    default RuleTest register(final String id, final RuleTest test) {
        return Registry.<RuleTest>register(Registry.RULE_TEST, id, test);
    }
}
