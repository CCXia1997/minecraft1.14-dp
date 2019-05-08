package net.minecraft.world.loot;

import org.apache.commons.lang3.ArrayUtils;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonParseException;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import net.minecraft.world.loot.context.LootContextType;
import java.util.Set;
import net.minecraft.util.Identifier;
import java.util.function.Function;
import net.minecraft.util.math.MathHelper;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.mutable.MutableInt;
import com.google.common.collect.Lists;
import java.util.function.Consumer;
import net.minecraft.world.loot.function.LootFunctions;
import net.minecraft.world.loot.condition.LootConditions;
import net.minecraft.item.ItemStack;
import java.util.function.BiFunction;
import net.minecraft.world.loot.function.LootFunction;
import net.minecraft.world.loot.context.LootContext;
import java.util.function.Predicate;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.entry.LootEntry;

public class LootPool
{
    private final LootEntry[] entries;
    private final LootCondition[] conditions;
    private final Predicate<LootContext> predicate;
    private final LootFunction[] functions;
    private final BiFunction<ItemStack, LootContext, ItemStack> javaFunctions;
    private final LootTableRange rollsRange;
    private final UniformLootTableRange bonusRollsRange;
    
    private LootPool(final LootEntry[] entries, final LootCondition[] conditions, final LootFunction[] functions, final LootTableRange rollsRange, final UniformLootTableRange bonusRollsRange) {
        this.entries = entries;
        this.conditions = conditions;
        this.predicate = LootConditions.<LootContext>joinAnd(conditions);
        this.functions = functions;
        this.javaFunctions = LootFunctions.join(functions);
        this.rollsRange = rollsRange;
        this.bonusRollsRange = bonusRollsRange;
    }
    
    private void supplyOnce(final Consumer<ItemStack> itemDropper, final LootContext context) {
        final Random random3 = context.getRandom();
        final List<LootChoice> list4 = Lists.newArrayList();
        final MutableInt mutableInt5 = new MutableInt();
        for (final LootEntry lootEntry9 : this.entries) {
            final int integer5;
            final List<LootChoice> list5;
            final MutableInt mutableInt6;
            lootEntry9.expand(context, choice -> {
                integer5 = choice.getWeight(context.getLuck());
                if (integer5 > 0) {
                    list5.add(choice);
                    mutableInt6.add(integer5);
                }
                return;
            });
        }
        final int integer6 = list4.size();
        if (mutableInt5.intValue() == 0 || integer6 == 0) {
            return;
        }
        if (integer6 == 1) {
            list4.get(0).drop(itemDropper, context);
            return;
        }
        int integer7 = random3.nextInt(mutableInt5.intValue());
        for (final LootChoice lootChoice9 : list4) {
            integer7 -= lootChoice9.getWeight(context.getLuck());
            if (integer7 < 0) {
                lootChoice9.drop(itemDropper, context);
            }
        }
    }
    
    public void drop(final Consumer<ItemStack> itemDropper, final LootContext context) {
        if (!this.predicate.test(context)) {
            return;
        }
        final Consumer<ItemStack> consumer3 = LootFunction.apply(this.javaFunctions, itemDropper, context);
        final Random random4 = context.getRandom();
        for (int integer5 = this.rollsRange.next(random4) + MathHelper.floor(this.bonusRollsRange.nextFloat(random4) * context.getLuck()), integer6 = 0; integer6 < integer5; ++integer6) {
            this.supplyOnce(consumer3, context);
        }
    }
    
    public void check(final LootTableReporter reporter, final Function<Identifier, LootSupplier> supplierGetter, final Set<Identifier> parentLootTables, final LootContextType contextType) {
        for (int integer5 = 0; integer5 < this.conditions.length; ++integer5) {
            this.conditions[integer5].check(reporter.makeChild(".condition[" + integer5 + "]"), supplierGetter, parentLootTables, contextType);
        }
        for (int integer5 = 0; integer5 < this.functions.length; ++integer5) {
            this.functions[integer5].check(reporter.makeChild(".functions[" + integer5 + "]"), supplierGetter, parentLootTables, contextType);
        }
        for (int integer5 = 0; integer5 < this.entries.length; ++integer5) {
            this.entries[integer5].check(reporter.makeChild(".entries[" + integer5 + "]"), supplierGetter, parentLootTables, contextType);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder implements FunctionConsumerBuilder<Builder>, ConditionConsumerBuilder<Builder>
    {
        private final List<LootEntry> entries;
        private final List<LootCondition> conditions;
        private final List<LootFunction> functions;
        private LootTableRange rollsRange;
        private UniformLootTableRange bonusRollsRange;
        
        public Builder() {
            this.entries = Lists.newArrayList();
            this.conditions = Lists.newArrayList();
            this.functions = Lists.newArrayList();
            this.rollsRange = new UniformLootTableRange(1.0f);
            this.bonusRollsRange = new UniformLootTableRange(0.0f, 0.0f);
        }
        
        public Builder withRolls(final LootTableRange rollsRange) {
            this.rollsRange = rollsRange;
            return this;
        }
        
        @Override
        public Builder getThis() {
            return this;
        }
        
        public Builder withEntry(final LootEntry.Builder<?> entryBuilder) {
            this.entries.add(entryBuilder.build());
            return this;
        }
        
        @Override
        public Builder withCondition(final LootCondition.Builder builder) {
            this.conditions.add(builder.build());
            return this;
        }
        
        @Override
        public Builder withFunction(final LootFunction.Builder lootFunctionBuilder) {
            this.functions.add(lootFunctionBuilder.build());
            return this;
        }
        
        public LootPool build() {
            if (this.rollsRange == null) {
                throw new IllegalArgumentException("Rolls not set");
            }
            return new LootPool(this.entries.<LootEntry>toArray(new LootEntry[0]), this.conditions.<LootCondition>toArray(new LootCondition[0]), this.functions.<LootFunction>toArray(new LootFunction[0]), this.rollsRange, this.bonusRollsRange, null);
        }
    }
    
    public static class Serializer implements JsonDeserializer<LootPool>, JsonSerializer<LootPool>
    {
        public LootPool a(final JsonElement json, final Type unused, final JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonObject4 = JsonHelper.asObject(json, "loot pool");
            final LootEntry[] arr5 = JsonHelper.<LootEntry[]>deserialize(jsonObject4, "entries", context, LootEntry[].class);
            final LootCondition[] arr6 = JsonHelper.<LootCondition[]>deserialize(jsonObject4, "conditions", new LootCondition[0], context, LootCondition[].class);
            final LootFunction[] arr7 = JsonHelper.<LootFunction[]>deserialize(jsonObject4, "functions", new LootFunction[0], context, LootFunction[].class);
            final LootTableRange lootTableRange8 = LootTableRanges.deserialize(jsonObject4.get("rolls"), context);
            final UniformLootTableRange uniformLootTableRange9 = JsonHelper.<UniformLootTableRange>deserialize(jsonObject4, "bonus_rolls", new UniformLootTableRange(0.0f, 0.0f), context, UniformLootTableRange.class);
            return new LootPool(arr5, arr6, arr7, lootTableRange8, uniformLootTableRange9, null);
        }
        
        public JsonElement a(final LootPool entry, final Type unused, final JsonSerializationContext context) {
            final JsonObject jsonObject4 = new JsonObject();
            jsonObject4.add("rolls", LootTableRanges.serialize(entry.rollsRange, context));
            jsonObject4.add("entries", context.serialize(entry.entries));
            if (entry.bonusRollsRange.getMinValue() != 0.0f && entry.bonusRollsRange.getMaxValue() != 0.0f) {
                jsonObject4.add("bonus_rolls", context.serialize(entry.bonusRollsRange));
            }
            if (!ArrayUtils.isEmpty((Object[])entry.conditions)) {
                jsonObject4.add("conditions", context.serialize(entry.conditions));
            }
            if (!ArrayUtils.isEmpty((Object[])entry.functions)) {
                jsonObject4.add("functions", context.serialize(entry.functions));
            }
            return jsonObject4;
        }
    }
}
