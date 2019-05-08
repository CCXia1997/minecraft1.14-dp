package net.minecraft.world.loot.entry;

import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import org.apache.commons.lang3.ArrayUtils;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.world.loot.FunctionConsumerBuilder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.loot.context.LootContextType;
import java.util.Set;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.util.Identifier;
import java.util.function.Function;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.world.loot.function.LootFunctions;
import java.util.function.Consumer;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.LootChoice;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.item.ItemStack;
import java.util.function.BiFunction;
import net.minecraft.world.loot.function.LootFunction;

public abstract class LeafEntry extends LootEntry
{
    protected final int weight;
    protected final int quality;
    protected final LootFunction[] functions;
    private final BiFunction<ItemStack, LootContext, ItemStack> compiledFunctions;
    private final LootChoice choice;
    
    protected LeafEntry(final int weight, final int quality, final LootCondition[] conditions, final LootFunction[] functions) {
        super(conditions);
        this.choice = new Choice() {
            @Override
            public void drop(final Consumer<ItemStack> itemDropper, final LootContext context) {
                LeafEntry.this.drop(LootFunction.apply(LeafEntry.this.compiledFunctions, itemDropper, context), context);
            }
        };
        this.weight = weight;
        this.quality = quality;
        this.functions = functions;
        this.compiledFunctions = LootFunctions.join(functions);
    }
    
    @Override
    public void check(final LootTableReporter reporter, final Function<Identifier, LootSupplier> supplierGetter, final Set<Identifier> parentLootTables, final LootContextType contextType) {
        super.check(reporter, supplierGetter, parentLootTables, contextType);
        for (int integer5 = 0; integer5 < this.functions.length; ++integer5) {
            this.functions[integer5].check(reporter.makeChild(".functions[" + integer5 + "]"), supplierGetter, parentLootTables, contextType);
        }
    }
    
    protected abstract void drop(final Consumer<ItemStack> arg1, final LootContext arg2);
    
    @Override
    public boolean expand(final LootContext lootContext, final Consumer<LootChoice> consumer) {
        if (this.test(lootContext)) {
            consumer.accept(this.choice);
            return true;
        }
        return false;
    }
    
    public static Builder<?> builder(final Factory factory) {
        return new BasicBuilder(factory);
    }
    
    public abstract class Choice implements LootChoice
    {
        protected Choice() {
        }
        
        @Override
        public int getWeight(final float luck) {
            return Math.max(MathHelper.floor(LeafEntry.this.weight + LeafEntry.this.quality * luck), 0);
        }
    }
    
    public abstract static class Builder<T extends Builder<T>> extends LootEntry.Builder<T> implements FunctionConsumerBuilder<T>
    {
        protected int weight;
        protected int quality;
        private final List<LootFunction> functions;
        
        public Builder() {
            this.weight = 1;
            this.quality = 0;
            this.functions = Lists.newArrayList();
        }
        
        @Override
        public T withFunction(final LootFunction.Builder lootFunctionBuilder) {
            this.functions.add(lootFunctionBuilder.build());
            return this.getThisBuilder();
        }
        
        protected LootFunction[] getFunctions() {
            return this.functions.<LootFunction>toArray(new LootFunction[0]);
        }
        
        public T setWeight(final int weight) {
            this.weight = weight;
            return this.getThisBuilder();
        }
        
        public T setQuality(final int quality) {
            this.quality = quality;
            return this.getThisBuilder();
        }
    }
    
    static class BasicBuilder extends Builder<BasicBuilder>
    {
        private final Factory factory;
        
        public BasicBuilder(final Factory factory) {
            this.factory = factory;
        }
        
        @Override
        protected BasicBuilder getThisBuilder() {
            return this;
        }
        
        @Override
        public LootEntry build() {
            return this.factory.build(this.weight, this.quality, this.getConditions(), this.getFunctions());
        }
    }
    
    public abstract static class Serializer<T extends LeafEntry> extends LootEntry.Serializer<T>
    {
        public Serializer(final Identifier id, final Class<T> type) {
            super(id, type);
        }
        
        @Override
        public void toJson(final JsonObject json, final T entry, final JsonSerializationContext context) {
            if (entry.weight != 1) {
                json.addProperty("weight", entry.weight);
            }
            if (entry.quality != 0) {
                json.addProperty("quality", entry.quality);
            }
            if (!ArrayUtils.isEmpty((Object[])entry.functions)) {
                json.add("functions", context.serialize(entry.functions));
            }
        }
        
        @Override
        public final T fromJson(final JsonObject json, final JsonDeserializationContext context, final LootCondition[] conditions) {
            final int integer4 = JsonHelper.getInt(json, "weight", 1);
            final int integer5 = JsonHelper.getInt(json, "quality", 0);
            final LootFunction[] arr6 = JsonHelper.<LootFunction[]>deserialize(json, "functions", new LootFunction[0], context, LootFunction[].class);
            return this.fromJson(json, context, integer4, integer5, conditions, arr6);
        }
        
        protected abstract T fromJson(final JsonObject arg1, final JsonDeserializationContext arg2, final int arg3, final int arg4, final LootCondition[] arg5, final LootFunction[] arg6);
    }
    
    @FunctionalInterface
    public interface Factory
    {
        LeafEntry build(final int arg1, final int arg2, final LootCondition[] arg3, final LootFunction[] arg4);
    }
}
