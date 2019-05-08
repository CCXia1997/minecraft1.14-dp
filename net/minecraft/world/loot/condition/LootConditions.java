package net.minecraft.world.loot.condition;

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
import java.util.function.Predicate;
import net.minecraft.util.Identifier;
import java.util.Map;

public class LootConditions
{
    private static final Map<Identifier, LootCondition.Factory<?>> byId;
    private static final Map<Class<? extends LootCondition>, LootCondition.Factory<?>> byClass;
    
    public static <T extends LootCondition> void register(final LootCondition.Factory<? extends T> condition) {
        final Identifier identifier2 = condition.getId();
        final Class<T> class3 = (Class<T>)condition.getConditionClass();
        if (LootConditions.byId.containsKey(identifier2)) {
            throw new IllegalArgumentException("Can't re-register item condition name " + identifier2);
        }
        if (LootConditions.byClass.containsKey(class3)) {
            throw new IllegalArgumentException("Can't re-register item condition class " + class3.getName());
        }
        LootConditions.byId.put(identifier2, condition);
        LootConditions.byClass.put(class3, condition);
    }
    
    public static LootCondition.Factory<?> get(final Identifier id) {
        final LootCondition.Factory<?> factory2 = LootConditions.byId.get(id);
        if (factory2 == null) {
            throw new IllegalArgumentException("Unknown loot item condition '" + id + "'");
        }
        return factory2;
    }
    
    public static <T extends LootCondition> LootCondition.Factory<T> getFactory(final T condition) {
        final LootCondition.Factory<T> factory2 = (LootCondition.Factory<T>)LootConditions.byClass.get(condition.getClass());
        if (factory2 == null) {
            throw new IllegalArgumentException("Unknown loot item condition " + condition);
        }
        return factory2;
    }
    
    public static <T> Predicate<T> joinAnd(final Predicate<T>[] predicates) {
        switch (predicates.length) {
            case 0: {
                return predicates -> true;
            }
            case 1: {
                return predicates[0];
            }
            case 2: {
                return predicates[0].and(predicates[1]);
            }
            default: {
                final int length;
                int i = 0;
                Predicate<Object> predicate6;
                return operand -> {
                    length = predicates.length;
                    while (i < length) {
                        predicate6 = (Predicate<Object>)predicates[i];
                        if (!predicate6.test(operand)) {
                            return false;
                        }
                        else {
                            ++i;
                        }
                    }
                    return true;
                };
            }
        }
    }
    
    public static <T> Predicate<T> joinOr(final Predicate<T>[] predicates) {
        switch (predicates.length) {
            case 0: {
                return predicates -> false;
            }
            case 1: {
                return predicates[0];
            }
            case 2: {
                return predicates[0].or(predicates[1]);
            }
            default: {
                final int length;
                int i = 0;
                Predicate<Object> predicate6;
                return operand -> {
                    length = predicates.length;
                    while (i < length) {
                        predicate6 = (Predicate<Object>)predicates[i];
                        if (predicate6.test(operand)) {
                            return true;
                        }
                        else {
                            ++i;
                        }
                    }
                    return false;
                };
            }
        }
    }
    
    static {
        byId = Maps.newHashMap();
        byClass = Maps.newHashMap();
        LootConditions.<LootCondition>register(new InvertedLootCondition.Factory());
        LootConditions.<LootCondition>register(new AlternativeLootCondition.Factory());
        LootConditions.<LootCondition>register(new RandomChanceLootCondition.Factory());
        LootConditions.<LootCondition>register(new RandomChanceWithLootingLootCondition.Factory());
        LootConditions.<LootCondition>register(new EntityPropertiesLootCondition.Factory());
        LootConditions.<LootCondition>register(new KilledByPlayerLootCondition.Factory());
        LootConditions.<LootCondition>register(new EntityScoresLootCondition.Factory());
        LootConditions.<LootCondition>register(new BlockStatePropertyLootCondition.Factory());
        LootConditions.<LootCondition>register(new MatchToolLootCondition.Factory());
        LootConditions.<LootCondition>register(new TableBonusLootCondition.Factory());
        LootConditions.<LootCondition>register(new SurvivesExplosionLootCondition.Factory());
        LootConditions.<LootCondition>register(new DamageSourcePropertiesLootCondition.Factory());
        LootConditions.<LootCondition>register(new LocationCheckLootCondition.Factory());
        LootConditions.<LootCondition>register(new WeatherCheckLootCondition.Factory());
    }
    
    public static class Factory implements JsonDeserializer<LootCondition>, JsonSerializer<LootCondition>
    {
        public LootCondition a(final JsonElement json, final Type unused, final JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonObject4 = JsonHelper.asObject(json, "condition");
            final Identifier identifier5 = new Identifier(JsonHelper.getString(jsonObject4, "condition"));
            LootCondition.Factory<?> factory6;
            try {
                factory6 = LootConditions.get(identifier5);
            }
            catch (IllegalArgumentException illegalArgumentException7) {
                throw new JsonSyntaxException("Unknown condition '" + identifier5 + "'");
            }
            return (LootCondition)factory6.fromJson(jsonObject4, context);
        }
        
        public JsonElement a(final LootCondition condition, final Type unused, final JsonSerializationContext context) {
            final LootCondition.Factory<LootCondition> factory4 = LootConditions.<LootCondition>getFactory(condition);
            final JsonObject jsonObject5 = new JsonObject();
            jsonObject5.addProperty("condition", factory4.getId().toString());
            factory4.toJson(jsonObject5, condition, context);
            return jsonObject5;
        }
    }
}
