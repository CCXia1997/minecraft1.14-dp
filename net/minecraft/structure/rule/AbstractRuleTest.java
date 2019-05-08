package net.minecraft.structure.rule;

import net.minecraft.util.registry.Registry;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;

public abstract class AbstractRuleTest
{
    public abstract boolean test(final BlockState arg1, final Random arg2);
    
    public <T> Dynamic<T> b(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.mergeInto(this.serialize((com.mojang.datafixers.types.DynamicOps<Object>)dynamicOps).getValue(), dynamicOps.createString("predicate_type"), dynamicOps.createString(Registry.RULE_TEST.getId(this.getRuleTest()).toString())));
    }
    
    protected abstract RuleTest getRuleTest();
    
    protected abstract <T> Dynamic<T> serialize(final DynamicOps<T> arg1);
}
