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
import net.minecraft.world.loot.context.LootContextTypes;
import org.apache.logging.log4j.LogManager;
import java.util.Collections;
import java.util.Collection;
import net.minecraft.util.math.MathHelper;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.inventory.Inventory;
import java.util.Set;
import net.minecraft.util.Identifier;
import java.util.function.Function;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.loot.function.LootFunctions;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.item.ItemStack;
import java.util.function.BiFunction;
import net.minecraft.world.loot.function.LootFunction;
import net.minecraft.world.loot.context.LootContextType;
import org.apache.logging.log4j.Logger;

public class LootSupplier
{
    private static final Logger LOGGER;
    public static final LootSupplier EMPTY;
    public static final LootContextType GENERIC;
    private final LootContextType type;
    private final LootPool[] pools;
    private final LootFunction[] functions;
    private final BiFunction<ItemStack, LootContext, ItemStack> combinedFunction;
    
    private LootSupplier(final LootContextType contextType, final LootPool[] pools, final LootFunction[] functions) {
        this.type = contextType;
        this.pools = pools;
        this.functions = functions;
        this.combinedFunction = LootFunctions.join(functions);
    }
    
    public static Consumer<ItemStack> limitedConsumer(final Consumer<ItemStack> itemDropper) {
        int integer3;
        ItemStack itemStack4;
        return stack -> {
            if (stack.getAmount() < stack.getMaxAmount()) {
                itemDropper.accept(stack);
            }
            else {
                integer3 = stack.getAmount();
                while (integer3 > 0) {
                    itemStack4 = stack.copy();
                    itemStack4.setAmount(Math.min(stack.getMaxAmount(), integer3));
                    integer3 -= itemStack4.getAmount();
                    itemDropper.accept(itemStack4);
                }
            }
        };
    }
    
    public void drop(final LootContext context, final Consumer<ItemStack> itemDropper) {
        if (context.addDrop(this)) {
            final Consumer<ItemStack> consumer3 = LootFunction.apply(this.combinedFunction, itemDropper, context);
            for (final LootPool lootPool7 : this.pools) {
                lootPool7.drop(consumer3, context);
            }
            context.removeDrop(this);
        }
        else {
            LootSupplier.LOGGER.warn("Detected infinite loop in loot tables");
        }
    }
    
    public void dropLimited(final LootContext context, final Consumer<ItemStack> dropItemConsumer) {
        this.drop(context, limitedConsumer(dropItemConsumer));
    }
    
    public List<ItemStack> getDrops(final LootContext context) {
        final List<ItemStack> list2 = Lists.newArrayList();
        this.dropLimited(context, list2::add);
        return list2;
    }
    
    public LootContextType getType() {
        return this.type;
    }
    
    public void check(final LootTableReporter reporter, final Function<Identifier, LootSupplier> supplierGetter, final Set<Identifier> parentLootTables, final LootContextType contextType) {
        for (int integer5 = 0; integer5 < this.pools.length; ++integer5) {
            this.pools[integer5].check(reporter.makeChild(".pools[" + integer5 + "]"), supplierGetter, parentLootTables, contextType);
        }
        for (int integer5 = 0; integer5 < this.functions.length; ++integer5) {
            this.functions[integer5].check(reporter.makeChild(".functions[" + integer5 + "]"), supplierGetter, parentLootTables, contextType);
        }
    }
    
    public void supplyInventory(final Inventory inventory, final LootContext context) {
        final List<ItemStack> list3 = this.getDrops(context);
        final Random random4 = context.getRandom();
        final List<Integer> list4 = this.getFreeSlots(inventory, random4);
        this.shuffle(list3, list4.size(), random4);
        for (final ItemStack itemStack7 : list3) {
            if (list4.isEmpty()) {
                LootSupplier.LOGGER.warn("Tried to over-fill a container");
                return;
            }
            if (itemStack7.isEmpty()) {
                inventory.setInvStack(list4.remove(list4.size() - 1), ItemStack.EMPTY);
            }
            else {
                inventory.setInvStack(list4.remove(list4.size() - 1), itemStack7);
            }
        }
    }
    
    private void shuffle(final List<ItemStack> drops, final int freeSlots, final Random random) {
        final List<ItemStack> list4 = Lists.newArrayList();
        final Iterator<ItemStack> iterator5 = drops.iterator();
        while (iterator5.hasNext()) {
            final ItemStack itemStack6 = iterator5.next();
            if (itemStack6.isEmpty()) {
                iterator5.remove();
            }
            else {
                if (itemStack6.getAmount() <= 1) {
                    continue;
                }
                list4.add(itemStack6);
                iterator5.remove();
            }
        }
        while (freeSlots - drops.size() - list4.size() > 0 && !list4.isEmpty()) {
            final ItemStack itemStack7 = list4.remove(MathHelper.nextInt(random, 0, list4.size() - 1));
            final int integer6 = MathHelper.nextInt(random, 1, itemStack7.getAmount() / 2);
            final ItemStack itemStack8 = itemStack7.split(integer6);
            if (itemStack7.getAmount() > 1 && random.nextBoolean()) {
                list4.add(itemStack7);
            }
            else {
                drops.add(itemStack7);
            }
            if (itemStack8.getAmount() > 1 && random.nextBoolean()) {
                list4.add(itemStack8);
            }
            else {
                drops.add(itemStack8);
            }
        }
        drops.addAll(list4);
        Collections.shuffle(drops, random);
    }
    
    private List<Integer> getFreeSlots(final Inventory inventory, final Random random) {
        final List<Integer> list3 = Lists.newArrayList();
        for (int integer4 = 0; integer4 < inventory.getInvSize(); ++integer4) {
            if (inventory.getInvStack(integer4).isEmpty()) {
                list3.add(integer4);
            }
        }
        Collections.shuffle(list3, random);
        return list3;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    static {
        LOGGER = LogManager.getLogger();
        EMPTY = new LootSupplier(LootContextTypes.EMPTY, new LootPool[0], new LootFunction[0]);
        GENERIC = LootContextTypes.GENERIC;
    }
    
    public static class Builder implements FunctionConsumerBuilder<Builder>
    {
        private final List<LootPool> pools;
        private final List<LootFunction> functions;
        private LootContextType type;
        
        public Builder() {
            this.pools = Lists.newArrayList();
            this.functions = Lists.newArrayList();
            this.type = LootSupplier.GENERIC;
        }
        
        public Builder withPool(final LootPool.Builder poolBuilder) {
            this.pools.add(poolBuilder.build());
            return this;
        }
        
        public Builder withType(final LootContextType context) {
            this.type = context;
            return this;
        }
        
        @Override
        public Builder withFunction(final LootFunction.Builder lootFunctionBuilder) {
            this.functions.add(lootFunctionBuilder.build());
            return this;
        }
        
        @Override
        public Builder getThis() {
            return this;
        }
        
        public LootSupplier create() {
            return new LootSupplier(this.type, this.pools.<LootPool>toArray(new LootPool[0]), this.functions.<LootFunction>toArray(new LootFunction[0]), null);
        }
    }
    
    public static class Serializer implements JsonDeserializer<LootSupplier>, JsonSerializer<LootSupplier>
    {
        public LootSupplier a(final JsonElement json, final Type unused, final JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonObject4 = JsonHelper.asObject(json, "loot table");
            final LootPool[] arr5 = JsonHelper.<LootPool[]>deserialize(jsonObject4, "pools", new LootPool[0], context, LootPool[].class);
            LootContextType lootContextType6 = null;
            if (jsonObject4.has("type")) {
                final String string7 = JsonHelper.getString(jsonObject4, "type");
                lootContextType6 = LootContextTypes.get(new Identifier(string7));
            }
            final LootFunction[] arr6 = JsonHelper.<LootFunction[]>deserialize(jsonObject4, "functions", new LootFunction[0], context, LootFunction[].class);
            return new LootSupplier((lootContextType6 != null) ? lootContextType6 : LootContextTypes.GENERIC, arr5, arr6, null);
        }
        
        public JsonElement a(final LootSupplier supplier, final Type unused, final JsonSerializationContext context) {
            final JsonObject jsonObject4 = new JsonObject();
            if (supplier.type != LootSupplier.GENERIC) {
                final Identifier identifier5 = LootContextTypes.getId(supplier.type);
                if (identifier5 != null) {
                    jsonObject4.addProperty("type", identifier5.toString());
                }
                else {
                    LootSupplier.LOGGER.warn("Failed to find id for param set " + supplier.type);
                }
            }
            if (supplier.pools.length > 0) {
                jsonObject4.add("pools", context.serialize(supplier.pools));
            }
            if (!ArrayUtils.isEmpty((Object[])supplier.functions)) {
                jsonObject4.add("functions", context.serialize(supplier.functions));
            }
            return jsonObject4;
        }
    }
}
