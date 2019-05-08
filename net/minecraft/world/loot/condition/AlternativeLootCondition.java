package net.minecraft.world.loot.condition;

import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.world.loot.context.LootContextType;
import java.util.Set;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.util.Identifier;
import java.util.function.Function;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.world.loot.context.LootContext;
import java.util.function.Predicate;

public class AlternativeLootCondition implements LootCondition
{
    private final LootCondition[] terms;
    private final Predicate<LootContext> predicate;
    
    private AlternativeLootCondition(final LootCondition[] terms) {
        this.terms = terms;
        this.predicate = LootConditions.<LootContext>joinOr(terms);
    }
    
    public final boolean a(final LootContext context) {
        return this.predicate.test(context);
    }
    
    @Override
    public void check(final LootTableReporter reporter, final Function<Identifier, LootSupplier> supplierGetter, final Set<Identifier> parentLootTables, final LootContextType contextType) {
        super.check(reporter, supplierGetter, parentLootTables, contextType);
        for (int integer5 = 0; integer5 < this.terms.length; ++integer5) {
            this.terms[integer5].check(reporter.makeChild(".term[" + integer5 + "]"), supplierGetter, parentLootTables, contextType);
        }
    }
    
    public static Builder builder(final LootCondition.Builder... terms) {
        return new Builder(terms);
    }
    
    public static class Builder implements LootCondition.Builder
    {
        private final List<LootCondition> terms;
        
        public Builder(final LootCondition.Builder... terms) {
            this.terms = Lists.newArrayList();
            for (final LootCondition.Builder builder5 : terms) {
                this.terms.add(builder5.build());
            }
        }
        
        @Override
        public Builder withCondition(final LootCondition.Builder condition) {
            this.terms.add(condition.build());
            return this;
        }
        
        @Override
        public LootCondition build() {
            return new AlternativeLootCondition(this.terms.<LootCondition>toArray(new LootCondition[0]), null);
        }
    }
    
    public static class Factory extends LootCondition.Factory<AlternativeLootCondition>
    {
        public Factory() {
            super(new Identifier("alternative"), AlternativeLootCondition.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final AlternativeLootCondition condition, final JsonSerializationContext context) {
            json.add("terms", context.serialize(condition.terms));
        }
        
        @Override
        public AlternativeLootCondition fromJson(final JsonObject json, final JsonDeserializationContext context) {
            final LootCondition[] arr3 = JsonHelper.<LootCondition[]>deserialize(json, "terms", context, LootCondition[].class);
            return new AlternativeLootCondition(arr3, null);
        }
    }
}
