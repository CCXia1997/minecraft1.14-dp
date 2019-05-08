package net.minecraft.world.loot.entry;

import org.apache.commons.lang3.ArrayUtils;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.world.loot.condition.LootCondition;
import com.google.gson.JsonParseException;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import com.google.common.collect.Maps;
import net.minecraft.util.Identifier;
import java.util.Map;

public class LootEntries
{
    private static final Map<Identifier, LootEntry.Serializer<?>> idSerializers;
    private static final Map<Class<?>, LootEntry.Serializer<?>> classSerializers;
    
    private static void register(final LootEntry.Serializer<?> serializer) {
        LootEntries.idSerializers.put(serializer.getIdentifier(), serializer);
        LootEntries.classSerializers.put(serializer.getType(), serializer);
    }
    
    static {
        idSerializers = Maps.newHashMap();
        classSerializers = Maps.newHashMap();
        register(CombinedEntry.<AlternativeEntry>createSerializer(new Identifier("alternatives"), AlternativeEntry.class, AlternativeEntry::new));
        register(CombinedEntry.<SequenceEntry>createSerializer(new Identifier("sequence"), SequenceEntry.class, SequenceEntry::new));
        register(CombinedEntry.<GroupEntry>createSerializer(new Identifier("group"), GroupEntry.class, GroupEntry::new));
        register(new EmptyEntry.Serializer());
        register(new ItemEntry.Serializer());
        register(new LootTableEntry.Serializer());
        register(new DynamicEntry.Serializer());
        register(new TagEntry.Serializer());
    }
    
    public static class Serializer implements JsonDeserializer<LootEntry>, JsonSerializer<LootEntry>
    {
        public LootEntry a(final JsonElement json, final Type unused, final JsonDeserializationContext context) {
            final JsonObject jsonObject4 = JsonHelper.asObject(json, "entry");
            final Identifier identifier5 = new Identifier(JsonHelper.getString(jsonObject4, "type"));
            final LootEntry.Serializer<?> serializer6 = LootEntries.idSerializers.get(identifier5);
            if (serializer6 == null) {
                throw new JsonParseException("Unknown item type: " + identifier5);
            }
            final LootCondition[] arr7 = JsonHelper.<LootCondition[]>deserialize(jsonObject4, "conditions", new LootCondition[0], context, LootCondition[].class);
            return (LootEntry)serializer6.fromJson(jsonObject4, context, arr7);
        }
        
        public JsonElement a(final LootEntry entry, final Type unused, final JsonSerializationContext context) {
            final JsonObject jsonObject4 = new JsonObject();
            final LootEntry.Serializer<LootEntry> serializer5 = getSerializer(entry.getClass());
            jsonObject4.addProperty("type", serializer5.getIdentifier().toString());
            if (!ArrayUtils.isEmpty((Object[])entry.conditions)) {
                jsonObject4.add("conditions", context.serialize(entry.conditions));
            }
            serializer5.toJson(jsonObject4, entry, context);
            return jsonObject4;
        }
        
        private static LootEntry.Serializer<LootEntry> getSerializer(final Class<?> clazz) {
            final LootEntry.Serializer<?> serializer2 = LootEntries.classSerializers.get(clazz);
            if (serializer2 == null) {
                throw new JsonParseException("Unknown item type: " + clazz);
            }
            return (LootEntry.Serializer<LootEntry>)serializer2;
        }
    }
}
