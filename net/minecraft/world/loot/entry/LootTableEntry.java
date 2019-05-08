package net.minecraft.world.loot.entry;

import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.loot.context.LootContextType;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.item.ItemStack;
import java.util.function.Consumer;
import net.minecraft.world.loot.function.LootFunction;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.util.Identifier;

public class LootTableEntry extends LeafEntry
{
    private final Identifier id;
    
    private LootTableEntry(final Identifier id, final int weight, final int quality, final LootCondition[] conditions, final LootFunction[] functions) {
        super(weight, quality, conditions, functions);
        this.id = id;
    }
    
    public void drop(final Consumer<ItemStack> itemDropper, final LootContext context) {
        final LootSupplier lootSupplier3 = context.getLootManager().getSupplier(this.id);
        lootSupplier3.drop(context, itemDropper);
    }
    
    @Override
    public void check(final LootTableReporter reporter, final Function<Identifier, LootSupplier> supplierGetter, final Set<Identifier> parentLootTables, final LootContextType contextType) {
        if (parentLootTables.contains(this.id)) {
            reporter.report("Table " + this.id + " is recursively called");
            return;
        }
        super.check(reporter, supplierGetter, parentLootTables, contextType);
        final LootSupplier lootSupplier5 = supplierGetter.apply(this.id);
        if (lootSupplier5 == null) {
            reporter.report("Unknown loot table called " + this.id);
        }
        else {
            final Set<Identifier> set6 = ImmutableSet.<Identifier>builder().addAll(parentLootTables).add(this.id).build();
            lootSupplier5.check(reporter.makeChild("->{" + this.id + "}"), supplierGetter, set6, contextType);
        }
    }
    
    public static Builder<?> builder(final Identifier id) {
        return LeafEntry.builder((weight, quality, conditions, functions) -> new LootTableEntry(id, weight, quality, conditions, functions));
    }
    
    public static class Serializer extends LeafEntry.Serializer<LootTableEntry>
    {
        public Serializer() {
            super(new Identifier("loot_table"), LootTableEntry.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final LootTableEntry entry, final JsonSerializationContext context) {
            super.toJson(json, entry, context);
            json.addProperty("name", entry.id.toString());
        }
        
        @Override
        protected LootTableEntry fromJson(final JsonObject entryJson, final JsonDeserializationContext context, final int weight, final int quality, final LootCondition[] conditions, final LootFunction[] functions) {
            final Identifier identifier7 = new Identifier(JsonHelper.getString(entryJson, "name"));
            return new LootTableEntry(identifier7, weight, quality, conditions, functions, null);
        }
    }
}
