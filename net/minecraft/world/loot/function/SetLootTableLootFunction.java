package net.minecraft.world.loot.function;

import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.loot.context.LootContextType;
import java.util.Set;
import net.minecraft.world.loot.LootSupplier;
import java.util.function.Function;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.item.ItemStack;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.util.Identifier;

public class SetLootTableLootFunction extends ConditionalLootFunction
{
    private final Identifier id;
    private final long seed;
    
    private SetLootTableLootFunction(final LootCondition[] conditions, final Identifier id, final long seed) {
        super(conditions);
        this.id = id;
        this.seed = seed;
    }
    
    public ItemStack process(final ItemStack stack, final LootContext context) {
        if (stack.isEmpty()) {
            return stack;
        }
        final CompoundTag compoundTag3 = new CompoundTag();
        compoundTag3.putString("LootTable", this.id.toString());
        if (this.seed != 0L) {
            compoundTag3.putLong("LootTableSeed", this.seed);
        }
        stack.getOrCreateTag().put("BlockEntityTag", compoundTag3);
        return stack;
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
    
    public static class Factory extends ConditionalLootFunction.Factory<SetLootTableLootFunction>
    {
        protected Factory() {
            super(new Identifier("set_loot_table"), SetLootTableLootFunction.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final SetLootTableLootFunction function, final JsonSerializationContext context) {
            super.toJson(json, function, context);
            json.addProperty("name", function.id.toString());
            if (function.seed != 0L) {
                json.addProperty("seed", function.seed);
            }
        }
        
        @Override
        public SetLootTableLootFunction fromJson(final JsonObject json, final JsonDeserializationContext context, final LootCondition[] conditions) {
            final Identifier identifier4 = new Identifier(JsonHelper.getString(json, "name"));
            final long long5 = JsonHelper.getLong(json, "seed", 0L);
            return new SetLootTableLootFunction(conditions, identifier4, long5, null);
        }
    }
}
