package net.minecraft.world.loot.entry;

import com.google.gson.JsonDeserializationContext;
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
import net.minecraft.world.loot.condition.LootConditions;
import net.minecraft.world.loot.context.LootContext;
import java.util.function.Predicate;
import net.minecraft.world.loot.condition.LootCondition;

public abstract class LootEntry implements LootChoiceProvider
{
    protected final LootCondition[] conditions;
    private final Predicate<LootContext> conditionPredicate;
    
    protected LootEntry(final LootCondition[] conditions) {
        this.conditions = conditions;
        this.conditionPredicate = LootConditions.<LootContext>joinAnd(conditions);
    }
    
    public void check(final LootTableReporter reporter, final Function<Identifier, LootSupplier> supplierGetter, final Set<Identifier> parentLootTables, final LootContextType contextType) {
        for (int integer5 = 0; integer5 < this.conditions.length; ++integer5) {
            this.conditions[integer5].check(reporter.makeChild(".condition[" + integer5 + "]"), supplierGetter, parentLootTables, contextType);
        }
    }
    
    protected final boolean test(final LootContext context) {
        return this.conditionPredicate.test(context);
    }
    
    public abstract static class Builder<T extends Builder<T>> implements ConditionConsumerBuilder<T>
    {
        private final List<LootCondition> children;
        
        public Builder() {
            this.children = Lists.newArrayList();
        }
        
        protected abstract T getThisBuilder();
        
        @Override
        public T withCondition(final LootCondition.Builder builder) {
            this.children.add(builder.build());
            return this.getThisBuilder();
        }
        
        @Override
        public final T getThis() {
            return this.getThisBuilder();
        }
        
        protected LootCondition[] getConditions() {
            return this.children.<LootCondition>toArray(new LootCondition[0]);
        }
        
        public AlternativeEntry.Builder withChild(final Builder<?> builder) {
            return new AlternativeEntry.Builder(new Builder[] { this, builder });
        }
        
        public abstract LootEntry build();
    }
    
    public abstract static class Serializer<T extends LootEntry>
    {
        private final Identifier id;
        private final Class<T> type;
        
        protected Serializer(final Identifier id, final Class<T> type) {
            this.id = id;
            this.type = type;
        }
        
        public Identifier getIdentifier() {
            return this.id;
        }
        
        public Class<T> getType() {
            return this.type;
        }
        
        public abstract void toJson(final JsonObject arg1, final T arg2, final JsonSerializationContext arg3);
        
        public abstract T fromJson(final JsonObject arg1, final JsonDeserializationContext arg2, final LootCondition[] arg3);
    }
}
