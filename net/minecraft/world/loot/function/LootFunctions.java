package net.minecraft.world.loot.function;

import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonParseException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import com.google.common.collect.Maps;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.item.ItemStack;
import java.util.function.BiFunction;
import net.minecraft.util.Identifier;
import java.util.Map;

public class LootFunctions
{
    private static final Map<Identifier, LootFunction.Factory<?>> byId;
    private static final Map<Class<? extends LootFunction>, LootFunction.Factory<?>> byClass;
    public static final BiFunction<ItemStack, LootContext, ItemStack> NOOP;
    
    public static <T extends LootFunction> void register(final LootFunction.Factory<? extends T> function) {
        final Identifier identifier2 = function.getId();
        final Class<T> class3 = (Class<T>)function.getFunctionClass();
        if (LootFunctions.byId.containsKey(identifier2)) {
            throw new IllegalArgumentException("Can't re-register item function name " + identifier2);
        }
        if (LootFunctions.byClass.containsKey(class3)) {
            throw new IllegalArgumentException("Can't re-register item function class " + class3.getName());
        }
        LootFunctions.byId.put(identifier2, function);
        LootFunctions.byClass.put(class3, function);
    }
    
    public static LootFunction.Factory<?> get(final Identifier id) {
        final LootFunction.Factory<?> factory2 = LootFunctions.byId.get(id);
        if (factory2 == null) {
            throw new IllegalArgumentException("Unknown loot item function '" + id + "'");
        }
        return factory2;
    }
    
    public static <T extends LootFunction> LootFunction.Factory<T> getFactory(final T function) {
        final LootFunction.Factory<T> factory2 = (LootFunction.Factory<T>)LootFunctions.byClass.get(function.getClass());
        if (factory2 == null) {
            throw new IllegalArgumentException("Unknown loot item function " + function);
        }
        return factory2;
    }
    
    public static BiFunction<ItemStack, LootContext, ItemStack> join(final BiFunction<ItemStack, LootContext, ItemStack>[] lootFunctions) {
        switch (lootFunctions.length) {
            case 0: {
                return LootFunctions.NOOP;
            }
            case 1: {
                return lootFunctions[0];
            }
            case 2: {
                final BiFunction<ItemStack, LootContext, ItemStack> biFunction2 = lootFunctions[0];
                final BiFunction<ItemStack, LootContext, ItemStack> biFunction3 = lootFunctions[1];
                return (BiFunction<ItemStack, LootContext, ItemStack>)((stack, context) -> biFunction3.apply(biFunction2.apply(stack, context), context));
            }
            default: {
                int length;
                int i = 0;
                BiFunction<ItemStack, LootContext, ItemStack> biFunction4;
                return (BiFunction<ItemStack, LootContext, ItemStack>)((stack, context) -> {
                    for (length = lootFunctions.length; i < length; ++i) {
                        biFunction4 = lootFunctions[i];
                        stack = biFunction4.apply(stack, context);
                    }
                    return stack;
                });
            }
        }
    }
    
    static {
        byId = Maps.newHashMap();
        byClass = Maps.newHashMap();
        NOOP = ((stack, context) -> stack);
        LootFunctions.<LootFunction>register(new SetCountLootFunction.Factory());
        LootFunctions.<LootFunction>register(new EnchantWithLevelsLootFunction.Factory());
        LootFunctions.<LootFunction>register(new EnchantRandomlyLootFunction.Factory());
        LootFunctions.<LootFunction>register(new SetNbtLootFunction.Builder());
        LootFunctions.<LootFunction>register(new FurnaceSmeltLootFunction.Factory());
        LootFunctions.<LootFunction>register(new LootingEnchantLootFunction.Factory());
        LootFunctions.<LootFunction>register(new SetDamageLootFunction.Factory());
        LootFunctions.<LootFunction>register(new SetAttributesLootFunction.Factory());
        LootFunctions.<LootFunction>register(new SetNameLootFunction.Factory());
        LootFunctions.<LootFunction>register(new ExplorationMapLootFunction.Factory());
        LootFunctions.<LootFunction>register(new SetStewEffectLootFunction.Factory());
        LootFunctions.<LootFunction>register(new CopyNameLootFunction.Factory());
        LootFunctions.<LootFunction>register(new SetContentsLootFunction.Factory());
        LootFunctions.<LootFunction>register(new LimitCountLootFunction.Factory());
        LootFunctions.<LootFunction>register(new ApplyBonusLootFunction.Factory());
        LootFunctions.<LootFunction>register(new SetLootTableLootFunction.Factory());
        LootFunctions.<LootFunction>register(new ExplosionDecayLootFunction.Factory());
        LootFunctions.<LootFunction>register(new SetLoreLootFunction.Factory());
        LootFunctions.<LootFunction>register(new FillPlayerHeadLootFunction.Factory());
        LootFunctions.<LootFunction>register(new CopyNbtLootFunction.Factory());
    }
    
    public static class Factory implements JsonDeserializer<LootFunction>, JsonSerializer<LootFunction>
    {
        public LootFunction a(final JsonElement json, final Type unused, final JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonObject4 = JsonHelper.asObject(json, "function");
            final Identifier identifier5 = new Identifier(JsonHelper.getString(jsonObject4, "function"));
            LootFunction.Factory<?> factory6;
            try {
                factory6 = LootFunctions.get(identifier5);
            }
            catch (IllegalArgumentException illegalArgumentException7) {
                throw new JsonSyntaxException("Unknown function '" + identifier5 + "'");
            }
            return (LootFunction)factory6.fromJson(jsonObject4, context);
        }
        
        public JsonElement a(final LootFunction function, final Type unused, final JsonSerializationContext context) {
            final LootFunction.Factory<LootFunction> factory4 = LootFunctions.<LootFunction>getFactory(function);
            final JsonObject jsonObject5 = new JsonObject();
            jsonObject5.addProperty("function", factory4.getId().toString());
            factory4.toJson(jsonObject5, function, context);
            return jsonObject5;
        }
    }
}
