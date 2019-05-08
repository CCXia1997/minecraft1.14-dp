package net.minecraft.util;

import com.google.gson.GsonBuilder;
import java.io.StringReader;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import java.io.Reader;
import com.google.gson.JsonPrimitive;
import org.apache.commons.lang3.StringUtils;
import java.lang.reflect.Type;
import com.google.gson.JsonDeserializationContext;
import javax.annotation.Nullable;
import com.google.gson.JsonArray;
import net.minecraft.util.registry.Registry;
import net.minecraft.item.Item;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.Gson;

public class JsonHelper
{
    private static final Gson GSON;
    
    public static boolean hasString(final JsonObject object, final String element) {
        return hasPrimitive(object, element) && object.getAsJsonPrimitive(element).isString();
    }
    
    @Environment(EnvType.CLIENT)
    public static boolean isString(final JsonElement element) {
        return element.isJsonPrimitive() && element.getAsJsonPrimitive().isString();
    }
    
    public static boolean isNumber(final JsonElement element) {
        return element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber();
    }
    
    @Environment(EnvType.CLIENT)
    public static boolean hasBoolean(final JsonObject object, final String element) {
        return hasPrimitive(object, element) && object.getAsJsonPrimitive(element).isBoolean();
    }
    
    public static boolean hasArray(final JsonObject object, final String element) {
        return hasElement(object, element) && object.get(element).isJsonArray();
    }
    
    public static boolean hasPrimitive(final JsonObject object, final String element) {
        return hasElement(object, element) && object.get(element).isJsonPrimitive();
    }
    
    public static boolean hasElement(final JsonObject object, final String lement) {
        return object != null && object.get(lement) != null;
    }
    
    public static String asString(final JsonElement element, final String name) {
        if (element.isJsonPrimitive()) {
            return element.getAsString();
        }
        throw new JsonSyntaxException("Expected " + name + " to be a string, was " + getType(element));
    }
    
    public static String getString(final JsonObject object, final String element) {
        if (object.has(element)) {
            return asString(object.get(element), element);
        }
        throw new JsonSyntaxException("Missing " + element + ", expected to find a string");
    }
    
    public static String getString(final JsonObject object, final String element, final String defaultStr) {
        if (object.has(element)) {
            return asString(object.get(element), element);
        }
        return defaultStr;
    }
    
    public static Item asItem(final JsonElement element, final String name) {
        if (element.isJsonPrimitive()) {
            final String string3 = element.getAsString();
            final Object o;
            final String s;
            return Registry.ITEM.getOrEmpty(new Identifier(string3)).<Throwable>orElseThrow(() -> {
                new JsonSyntaxException("Expected " + name + " to be an item, was unknown string '" + s + "'");
                return o;
            });
        }
        throw new JsonSyntaxException("Expected " + name + " to be an item, was " + getType(element));
    }
    
    public static Item getItem(final JsonObject object, final String key) {
        if (object.has(key)) {
            return asItem(object.get(key), key);
        }
        throw new JsonSyntaxException("Missing " + key + ", expected to find an item");
    }
    
    public static boolean asBoolean(final JsonElement element, final String name) {
        if (element.isJsonPrimitive()) {
            return element.getAsBoolean();
        }
        throw new JsonSyntaxException("Expected " + name + " to be a Boolean, was " + getType(element));
    }
    
    public static boolean getBoolean(final JsonObject object, final String element) {
        if (object.has(element)) {
            return asBoolean(object.get(element), element);
        }
        throw new JsonSyntaxException("Missing " + element + ", expected to find a Boolean");
    }
    
    public static boolean getBoolean(final JsonObject object, final String element, final boolean defaultBoolean) {
        if (object.has(element)) {
            return asBoolean(object.get(element), element);
        }
        return defaultBoolean;
    }
    
    public static float asFloat(final JsonElement element, final String name) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsFloat();
        }
        throw new JsonSyntaxException("Expected " + name + " to be a Float, was " + getType(element));
    }
    
    public static float getFloat(final JsonObject object, final String element) {
        if (object.has(element)) {
            return asFloat(object.get(element), element);
        }
        throw new JsonSyntaxException("Missing " + element + ", expected to find a Float");
    }
    
    public static float getFloat(final JsonObject object, final String element, final float defaultFloat) {
        if (object.has(element)) {
            return asFloat(object.get(element), element);
        }
        return defaultFloat;
    }
    
    public static long asLong(final JsonElement element, final String name) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsLong();
        }
        throw new JsonSyntaxException("Expected " + name + " to be a Long, was " + getType(element));
    }
    
    public static long getLong(final JsonObject object, final String element, final long defaultLong) {
        if (object.has(element)) {
            return asLong(object.get(element), element);
        }
        return defaultLong;
    }
    
    public static int asInt(final JsonElement element, final String name) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsInt();
        }
        throw new JsonSyntaxException("Expected " + name + " to be a Int, was " + getType(element));
    }
    
    public static int getInt(final JsonObject object, final String element) {
        if (object.has(element)) {
            return asInt(object.get(element), element);
        }
        throw new JsonSyntaxException("Missing " + element + ", expected to find a Int");
    }
    
    public static int getInt(final JsonObject object, final String element, final int defaultInt) {
        if (object.has(element)) {
            return asInt(object.get(element), element);
        }
        return defaultInt;
    }
    
    public static byte asByte(final JsonElement element, final String name) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsByte();
        }
        throw new JsonSyntaxException("Expected " + name + " to be a Byte, was " + getType(element));
    }
    
    public static byte getByte(final JsonObject object, final String element, final byte defaultByte) {
        if (object.has(element)) {
            return asByte(object.get(element), element);
        }
        return defaultByte;
    }
    
    public static JsonObject asObject(final JsonElement element, final String name) {
        if (element.isJsonObject()) {
            return element.getAsJsonObject();
        }
        throw new JsonSyntaxException("Expected " + name + " to be a JsonObject, was " + getType(element));
    }
    
    public static JsonObject getObject(final JsonObject object, final String element) {
        if (object.has(element)) {
            return asObject(object.get(element), element);
        }
        throw new JsonSyntaxException("Missing " + element + ", expected to find a JsonObject");
    }
    
    public static JsonObject getObject(final JsonObject object, final String element, final JsonObject defaultObject) {
        if (object.has(element)) {
            return asObject(object.get(element), element);
        }
        return defaultObject;
    }
    
    public static JsonArray asArray(final JsonElement element, final String name) {
        if (element.isJsonArray()) {
            return element.getAsJsonArray();
        }
        throw new JsonSyntaxException("Expected " + name + " to be a JsonArray, was " + getType(element));
    }
    
    public static JsonArray getArray(final JsonObject object, final String element) {
        if (object.has(element)) {
            return asArray(object.get(element), element);
        }
        throw new JsonSyntaxException("Missing " + element + ", expected to find a JsonArray");
    }
    
    public static JsonArray getArray(final JsonObject object, final String name, @Nullable final JsonArray defaultArray) {
        if (object.has(name)) {
            return asArray(object.get(name), name);
        }
        return defaultArray;
    }
    
    public static <T> T deserialize(@Nullable final JsonElement element, final String name, final JsonDeserializationContext context, final Class<? extends T> type) {
        if (element != null) {
            return context.<T>deserialize(element, type);
        }
        throw new JsonSyntaxException("Missing " + name);
    }
    
    public static <T> T deserialize(final JsonObject object, final String element, final JsonDeserializationContext context, final Class<? extends T> type) {
        if (object.has(element)) {
            return JsonHelper.<T>deserialize(object.get(element), element, context, type);
        }
        throw new JsonSyntaxException("Missing " + element);
    }
    
    public static <T> T deserialize(final JsonObject object, final String element, final T defaultValue, final JsonDeserializationContext context, final Class<? extends T> type) {
        if (object.has(element)) {
            return JsonHelper.<T>deserialize(object.get(element), element, context, type);
        }
        return defaultValue;
    }
    
    public static String getType(final JsonElement element) {
        final String string2 = StringUtils.abbreviateMiddle(String.valueOf(element), "...", 10);
        if (element == null) {
            return "null (missing)";
        }
        if (element.isJsonNull()) {
            return "null (json)";
        }
        if (element.isJsonArray()) {
            return "an array (" + string2 + ")";
        }
        if (element.isJsonObject()) {
            return "an object (" + string2 + ")";
        }
        if (element.isJsonPrimitive()) {
            final JsonPrimitive jsonPrimitive3 = element.getAsJsonPrimitive();
            if (jsonPrimitive3.isNumber()) {
                return "a number (" + string2 + ")";
            }
            if (jsonPrimitive3.isBoolean()) {
                return "a boolean (" + string2 + ")";
            }
        }
        return string2;
    }
    
    @Nullable
    public static <T> T deserialize(final Gson gson, final Reader reader, final Class<T> type, final boolean lenient) {
        try {
            final JsonReader jsonReader5 = new JsonReader(reader);
            jsonReader5.setLenient(lenient);
            return gson.<T>getAdapter(type).read(jsonReader5);
        }
        catch (IOException iOException5) {
            throw new JsonParseException(iOException5);
        }
    }
    
    @Nullable
    public static <T> T deserialize(final Gson gson, final Reader reader, final Type type, final boolean lenient) {
        try {
            final JsonReader jsonReader5 = new JsonReader(reader);
            jsonReader5.setLenient(lenient);
            return (T)gson.getAdapter(TypeToken.get(type)).read(jsonReader5);
        }
        catch (IOException iOException5) {
            throw new JsonParseException(iOException5);
        }
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public static <T> T deserialize(final Gson gson, final String content, final Type type, final boolean lenient) {
        return JsonHelper.<T>deserialize(gson, new StringReader(content), type, lenient);
    }
    
    @Nullable
    public static <T> T deserialize(final Gson gson, final String content, final Class<T> class3, final boolean lenient) {
        return JsonHelper.<T>deserialize(gson, new StringReader(content), class3, lenient);
    }
    
    @Nullable
    public static <T> T deserialize(final Gson gson, final Reader reader, final Type type) {
        return JsonHelper.<T>deserialize(gson, reader, type, false);
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public static <T> T deserialize(final Gson gson, final String content, final Type type) {
        return JsonHelper.<T>deserialize(gson, content, type, false);
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public static <T> T deserialize(final Gson gson, final Reader reader, final Class<T> class3) {
        return JsonHelper.<T>deserialize(gson, reader, class3, false);
    }
    
    @Nullable
    public static <T> T deserialize(final Gson gson, final String content, final Class<T> class3) {
        return JsonHelper.<T>deserialize(gson, content, class3, false);
    }
    
    public static JsonObject deserialize(final String content, final boolean lenient) {
        return deserialize(new StringReader(content), lenient);
    }
    
    public static JsonObject deserialize(final Reader reader, final boolean lenient) {
        return JsonHelper.<JsonObject>deserialize(JsonHelper.GSON, reader, JsonObject.class, lenient);
    }
    
    public static JsonObject deserialize(final String content) {
        return deserialize(content, false);
    }
    
    public static JsonObject deserialize(final Reader reader) {
        return deserialize(reader, false);
    }
    
    static {
        GSON = new GsonBuilder().create();
    }
}
