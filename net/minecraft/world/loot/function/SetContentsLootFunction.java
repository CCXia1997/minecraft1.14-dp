package net.minecraft.world.loot.function;

import java.util.Arrays;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Lists;
import net.minecraft.world.loot.LootChoice;
import net.minecraft.world.loot.context.LootContextType;
import java.util.Set;
import net.minecraft.util.Identifier;
import java.util.function.Function;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.nbt.Tag;
import net.minecraft.inventory.Inventories;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.util.DefaultedList;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.item.ItemStack;
import java.util.Collection;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.entry.LootEntry;
import java.util.List;

public class SetContentsLootFunction extends ConditionalLootFunction
{
    private final List<LootEntry> entries;
    
    private SetContentsLootFunction(final LootCondition[] conditions, final List<LootEntry> entries) {
        super(conditions);
        this.entries = ImmutableList.copyOf(entries);
    }
    
    public ItemStack process(final ItemStack stack, final LootContext context) {
        if (stack.isEmpty()) {
            return stack;
        }
        final DefaultedList<ItemStack> defaultedList3 = DefaultedList.<ItemStack>create();
        this.entries.forEach(entry -> entry.expand(context, choice -> choice.drop(LootSupplier.limitedConsumer(defaultedList3::add), context)));
        final CompoundTag compoundTag4 = new CompoundTag();
        Inventories.toTag(compoundTag4, defaultedList3);
        final CompoundTag compoundTag5 = stack.getOrCreateTag();
        compoundTag5.put("BlockEntityTag", compoundTag4.copyFrom(compoundTag5.getCompound("BlockEntityTag")));
        return stack;
    }
    
    @Override
    public void check(final LootTableReporter reporter, final Function<Identifier, LootSupplier> supplierGetter, final Set<Identifier> parentLootTables, final LootContextType contextType) {
        super.check(reporter, supplierGetter, parentLootTables, contextType);
        for (int integer5 = 0; integer5 < this.entries.size(); ++integer5) {
            this.entries.get(integer5).check(reporter.makeChild(".entry[" + integer5 + "]"), supplierGetter, parentLootTables, contextType);
        }
    }
    
    public static Builer builder() {
        return new Builer();
    }
    
    public static class Builer extends Builder<Builer>
    {
        private final List<LootEntry> entries;
        
        public Builer() {
            this.entries = Lists.newArrayList();
        }
        
        @Override
        protected Builer getThisBuilder() {
            return this;
        }
        
        public Builer withEntry(final LootEntry.Builder<?> entryBuilder) {
            this.entries.add(entryBuilder.build());
            return this;
        }
        
        @Override
        public LootFunction build() {
            return new SetContentsLootFunction(this.getConditions(), this.entries, null);
        }
    }
    
    public static class Factory extends ConditionalLootFunction.Factory<SetContentsLootFunction>
    {
        protected Factory() {
            super(new Identifier("set_contents"), SetContentsLootFunction.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final SetContentsLootFunction function, final JsonSerializationContext context) {
            super.toJson(json, function, context);
            json.add("entries", context.serialize(function.entries));
        }
        
        @Override
        public SetContentsLootFunction fromJson(final JsonObject json, final JsonDeserializationContext context, final LootCondition[] conditions) {
            final LootEntry[] arr4 = JsonHelper.<LootEntry[]>deserialize(json, "entries", context, LootEntry[].class);
            return new SetContentsLootFunction(conditions, Arrays.<LootEntry>asList(arr4), null);
        }
    }
}
