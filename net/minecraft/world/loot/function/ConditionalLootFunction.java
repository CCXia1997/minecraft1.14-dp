package net.minecraft.world.loot.function;

import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import org.apache.commons.lang3.ArrayUtils;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.world.loot.ConditionConsumerBuilder;
import net.minecraft.world.loot.context.LootContextType;
import java.util.Set;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.util.Identifier;
import java.util.function.Function;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.item.ItemStack;
import net.minecraft.world.loot.condition.LootConditions;
import net.minecraft.world.loot.context.LootContext;
import java.util.function.Predicate;
import net.minecraft.world.loot.condition.LootCondition;

public abstract class ConditionalLootFunction implements LootFunction
{
    protected final LootCondition[] conditions;
    private final Predicate<LootContext> predicate;
    
    protected ConditionalLootFunction(final LootCondition[] conditions) {
        this.conditions = conditions;
        this.predicate = LootConditions.<LootContext>joinAnd(conditions);
    }
    
    public final ItemStack b(final ItemStack itemStack, final LootContext context) {
        return this.predicate.test(context) ? this.process(itemStack, context) : itemStack;
    }
    
    protected abstract ItemStack process(final ItemStack arg1, final LootContext arg2);
    
    @Override
    public void check(final LootTableReporter reporter, final Function<Identifier, LootSupplier> supplierGetter, final Set<Identifier> parentLootTables, final LootContextType contextType) {
        super.check(reporter, supplierGetter, parentLootTables, contextType);
        for (int integer5 = 0; integer5 < this.conditions.length; ++integer5) {
            this.conditions[integer5].check(reporter.makeChild(".conditions[" + integer5 + "]"), supplierGetter, parentLootTables, contextType);
        }
    }
    
    protected static Builder<?> builder(final Function<LootCondition[], LootFunction> joiner) {
        return new Joiner(joiner);
    }
    
    public abstract static class Builder<T extends Builder<T>> implements LootFunction.Builder, ConditionConsumerBuilder<T>
    {
        private final List<LootCondition> conditionList;
        
        public Builder() {
            this.conditionList = Lists.newArrayList();
        }
        
        @Override
        public T withCondition(final LootCondition.Builder builder) {
            this.conditionList.add(builder.build());
            return this.getThisBuilder();
        }
        
        @Override
        public final T getThis() {
            return this.getThisBuilder();
        }
        
        protected abstract T getThisBuilder();
        
        protected LootCondition[] getConditions() {
            return this.conditionList.<LootCondition>toArray(new LootCondition[0]);
        }
    }
    
    static final class Joiner extends Builder<Joiner>
    {
        private final Function<LootCondition[], LootFunction> joiner;
        
        public Joiner(final Function<LootCondition[], LootFunction> joiner) {
            this.joiner = joiner;
        }
        
        @Override
        protected Joiner getThisBuilder() {
            return this;
        }
        
        @Override
        public LootFunction build() {
            return this.joiner.apply(this.getConditions());
        }
    }
    
    public abstract static class Factory<T extends ConditionalLootFunction> extends LootFunction.Factory<T>
    {
        public Factory(final Identifier id, final Class<T> clazz) {
            super(id, clazz);
        }
        
        @Override
        public void toJson(final JsonObject json, final T function, final JsonSerializationContext context) {
            if (!ArrayUtils.isEmpty((Object[])function.conditions)) {
                json.add("conditions", context.serialize(function.conditions));
            }
        }
        
        @Override
        public final T fromJson(final JsonObject json, final JsonDeserializationContext context) {
            final LootCondition[] arr3 = JsonHelper.<LootCondition[]>deserialize(json, "conditions", new LootCondition[0], context, LootCondition[].class);
            return this.fromJson(json, context, arr3);
        }
        
        public abstract T fromJson(final JsonObject arg1, final JsonDeserializationContext arg2, final LootCondition[] arg3);
    }
}
