package net.minecraft.world.loot.condition;

import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.world.loot.context.LootContextType;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.util.Identifier;
import java.util.function.Function;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.world.loot.context.LootContextParameter;
import java.util.Set;
import net.minecraft.world.loot.context.LootContext;

public class InvertedLootCondition implements LootCondition
{
    private final LootCondition term;
    
    private InvertedLootCondition(final LootCondition term) {
        this.term = term;
    }
    
    public final boolean a(final LootContext context) {
        return !this.term.test(context);
    }
    
    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return this.term.getRequiredParameters();
    }
    
    @Override
    public void check(final LootTableReporter reporter, final Function<Identifier, LootSupplier> supplierGetter, final Set<Identifier> parentLootTables, final LootContextType contextType) {
        super.check(reporter, supplierGetter, parentLootTables, contextType);
        this.term.check(reporter, supplierGetter, parentLootTables, contextType);
    }
    
    public static Builder builder(final Builder term) {
        final InvertedLootCondition invertedLootCondition2 = new InvertedLootCondition(term.build());
        return () -> invertedLootCondition2;
    }
    
    public static class Factory extends LootCondition.Factory<InvertedLootCondition>
    {
        public Factory() {
            super(new Identifier("inverted"), InvertedLootCondition.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final InvertedLootCondition condition, final JsonSerializationContext context) {
            json.add("term", context.serialize(condition.term));
        }
        
        @Override
        public InvertedLootCondition fromJson(final JsonObject json, final JsonDeserializationContext context) {
            final LootCondition lootCondition3 = JsonHelper.<LootCondition>deserialize(json, "term", context, LootCondition.class);
            return new InvertedLootCondition(lootCondition3, null);
        }
    }
}
