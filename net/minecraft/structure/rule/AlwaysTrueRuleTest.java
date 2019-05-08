package net.minecraft.structure.rule;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;

public class AlwaysTrueRuleTest extends AbstractRuleTest
{
    public static final AlwaysTrueRuleTest INSTANCE;
    
    private AlwaysTrueRuleTest() {
    }
    
    @Override
    public boolean test(final BlockState blockState, final Random random) {
        return true;
    }
    
    @Override
    protected RuleTest getRuleTest() {
        return RuleTest.b;
    }
    
    @Override
    protected <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.emptyMap());
    }
    
    static {
        INSTANCE = new AlwaysTrueRuleTest();
    }
}
