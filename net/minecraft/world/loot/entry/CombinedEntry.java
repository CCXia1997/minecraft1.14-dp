package net.minecraft.world.loot.entry;

import net.minecraft.util.JsonHelper;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.world.loot.LootChoice;
import java.util.function.Consumer;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextType;
import java.util.Set;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.util.Identifier;
import java.util.function.Function;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.world.loot.condition.LootCondition;

public abstract class CombinedEntry extends LootEntry
{
    protected final LootEntry[] children;
    private final LootChoiceProvider predicate;
    
    protected CombinedEntry(final LootEntry[] children, final LootCondition[] conditions) {
        super(conditions);
        this.children = children;
        this.predicate = this.combine(children);
    }
    
    @Override
    public void check(final LootTableReporter reporter, final Function<Identifier, LootSupplier> supplierGetter, final Set<Identifier> parentLootTables, final LootContextType contextType) {
        super.check(reporter, supplierGetter, parentLootTables, contextType);
        if (this.children.length == 0) {
            reporter.report("Empty children list");
        }
        for (int integer5 = 0; integer5 < this.children.length; ++integer5) {
            this.children[integer5].check(reporter.makeChild(".entry[" + integer5 + "]"), supplierGetter, parentLootTables, contextType);
        }
    }
    
    protected abstract LootChoiceProvider combine(final LootChoiceProvider[] arg1);
    
    @Override
    public final boolean expand(final LootContext lootContext, final Consumer<LootChoice> consumer) {
        return this.test(lootContext) && this.predicate.expand(lootContext, consumer);
    }
    
    public static <T extends CombinedEntry> Serializer<T> createSerializer(final Identifier id, final Class<T> type, final Factory<T> entry) {
        return new Serializer<T>(id, type) {
            @Override
            protected T fromJson(final JsonObject json, final JsonDeserializationContext context, final LootEntry[] children, final LootCondition[] conditions) {
                return entry.create(children, conditions);
            }
        };
    }
    
    public abstract static class Serializer<T extends CombinedEntry> extends LootEntry.Serializer<T>
    {
        public Serializer(final Identifier id, final Class<T> type) {
            super(id, type);
        }
        
        @Override
        public void toJson(final JsonObject json, final T entry, final JsonSerializationContext context) {
            json.add("children", context.serialize(entry.children));
        }
        
        @Override
        public final T fromJson(final JsonObject json, final JsonDeserializationContext context, final LootCondition[] conditions) {
            final LootEntry[] arr4 = JsonHelper.<LootEntry[]>deserialize(json, "children", context, LootEntry[].class);
            return this.fromJson(json, context, arr4, conditions);
        }
        
        protected abstract T fromJson(final JsonObject arg1, final JsonDeserializationContext arg2, final LootEntry[] arg3, final LootCondition[] arg4);
    }
    
    @FunctionalInterface
    public interface Factory<T extends CombinedEntry>
    {
        T create(final LootEntry[] arg1, final LootCondition[] arg2);
    }
}
