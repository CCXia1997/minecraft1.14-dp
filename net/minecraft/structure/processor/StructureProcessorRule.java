package net.minecraft.structure.processor;

import net.minecraft.nbt.Tag;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.util.DynamicDeserializer;
import net.minecraft.util.registry.Registry;
import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.datafixers.NbtOps;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.block.BlockState;
import net.minecraft.structure.rule.AbstractRuleTest;

public class StructureProcessorRule
{
    private final AbstractRuleTest inputPredicate;
    private final AbstractRuleTest locationPredicate;
    private final BlockState outputState;
    @Nullable
    private final CompoundTag tag;
    
    public StructureProcessorRule(final AbstractRuleTest abstractRuleTest1, final AbstractRuleTest abstractRuleTest2, final BlockState blockState) {
        this(abstractRuleTest1, abstractRuleTest2, blockState, null);
    }
    
    public StructureProcessorRule(final AbstractRuleTest abstractRuleTest1, final AbstractRuleTest abstractRuleTest2, final BlockState blockState, @Nullable final CompoundTag compoundTag) {
        this.inputPredicate = abstractRuleTest1;
        this.locationPredicate = abstractRuleTest2;
        this.outputState = blockState;
        this.tag = compoundTag;
    }
    
    public boolean test(final BlockState input, final BlockState location, final Random random) {
        return this.inputPredicate.test(input, random) && this.locationPredicate.test(location, random);
    }
    
    public BlockState getOutputState() {
        return this.outputState;
    }
    
    @Nullable
    public CompoundTag getTag() {
        return this.tag;
    }
    
    public <T> Dynamic<T> a(final DynamicOps<T> dynamicOps) {
        final T object2 = (T)dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("input_predicate"), this.inputPredicate.<T>b(dynamicOps).getValue(), dynamicOps.createString("location_predicate"), this.locationPredicate.<T>b(dynamicOps).getValue(), dynamicOps.createString("output_state"), BlockState.<T>serialize(dynamicOps, this.outputState).getValue()));
        if (this.tag == null) {
            return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, object2);
        }
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.mergeInto(object2, dynamicOps.createString("output_nbt"), new Dynamic((DynamicOps)NbtOps.INSTANCE, this.tag).convert((DynamicOps)dynamicOps).getValue()));
    }
    
    public static <T> StructureProcessorRule a(final Dynamic<T> dynamic) {
        final Dynamic<T> dynamic2 = (Dynamic<T>)dynamic.get("input_predicate").orElseEmptyMap();
        final Dynamic<T> dynamic3 = (Dynamic<T>)dynamic.get("location_predicate").orElseEmptyMap();
        final AbstractRuleTest abstractRuleTest4 = DynamicDeserializer.<T, AlwaysTrueRuleTest, RuleTest>deserialize(dynamic2, Registry.RULE_TEST, "predicate_type", AlwaysTrueRuleTest.INSTANCE);
        final AbstractRuleTest abstractRuleTest5 = DynamicDeserializer.<T, AlwaysTrueRuleTest, RuleTest>deserialize(dynamic3, Registry.RULE_TEST, "predicate_type", AlwaysTrueRuleTest.INSTANCE);
        final BlockState blockState6 = BlockState.deserialize((com.mojang.datafixers.Dynamic<Object>)dynamic.get("output_state").orElseEmptyMap());
        final CompoundTag compoundTag7 = dynamic.get("output_nbt").map(dynamic -> (Tag)dynamic.convert((DynamicOps)NbtOps.INSTANCE).getValue()).orElse(null);
        return new StructureProcessorRule(abstractRuleTest4, abstractRuleTest5, blockState6, compoundTag7);
    }
}
