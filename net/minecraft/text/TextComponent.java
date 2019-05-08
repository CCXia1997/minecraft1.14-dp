package net.minecraft.text;

import net.minecraft.util.SystemUtil;
import com.google.gson.TypeAdapterFactory;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.Reader;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.StringReader;
import javax.annotation.Nullable;
import com.google.gson.JsonPrimitive;
import java.util.Map;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonParseException;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import java.lang.reflect.Field;
import com.google.gson.Gson;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.List;
import java.util.Iterator;
import com.mojang.brigadier.Message;

public interface TextComponent extends Message, Iterable<TextComponent>
{
    TextComponent setStyle(final Style arg1);
    
    Style getStyle();
    
    default TextComponent append(final String string) {
        return this.append(new StringTextComponent(string));
    }
    
    TextComponent append(final TextComponent arg1);
    
    String getText();
    
    default String getString() {
        final StringBuilder stringBuilder1 = new StringBuilder();
        this.stream().forEach(textComponent -> stringBuilder1.append(textComponent.getText()));
        return stringBuilder1.toString();
    }
    
    default String getStringTruncated(final int integer) {
        final StringBuilder stringBuilder2 = new StringBuilder();
        final Iterator<TextComponent> iterator3 = this.stream().iterator();
        while (iterator3.hasNext()) {
            final int integer2 = integer - stringBuilder2.length();
            if (integer2 <= 0) {
                break;
            }
            final String string5 = iterator3.next().getText();
            stringBuilder2.append((string5.length() <= integer2) ? string5 : string5.substring(0, integer2));
        }
        return stringBuilder2.toString();
    }
    
    default String getFormattedText() {
        final StringBuilder stringBuilder1 = new StringBuilder();
        String string2 = "";
        for (final TextComponent textComponent4 : this.stream()) {
            final String string3 = textComponent4.getText();
            if (!string3.isEmpty()) {
                final String string4 = textComponent4.getStyle().getFormatString();
                if (!string4.equals(string2)) {
                    if (!string2.isEmpty()) {
                        stringBuilder1.append(TextFormat.RESET);
                    }
                    stringBuilder1.append(string4);
                    string2 = string4;
                }
                stringBuilder1.append(string3);
            }
        }
        if (!string2.isEmpty()) {
            stringBuilder1.append(TextFormat.RESET);
        }
        return stringBuilder1.toString();
    }
    
    List<TextComponent> getSiblings();
    
    Stream<TextComponent> stream();
    
    default Stream<TextComponent> streamCopied() {
        return this.stream().<TextComponent>map(TextComponent::copyWithoutChildren);
    }
    
    default Iterator<TextComponent> iterator() {
        return this.streamCopied().iterator();
    }
    
    TextComponent copyShallow();
    
    default TextComponent copy() {
        final TextComponent textComponent1 = this.copyShallow();
        textComponent1.setStyle(this.getStyle().clone());
        for (final TextComponent textComponent2 : this.getSiblings()) {
            textComponent1.append(textComponent2.copy());
        }
        return textComponent1;
    }
    
    default TextComponent modifyStyle(final Consumer<Style> consumer) {
        consumer.accept(this.getStyle());
        return this;
    }
    
    default TextComponent applyFormat(final TextFormat... arr) {
        for (final TextFormat textFormat5 : arr) {
            this.applyFormat(textFormat5);
        }
        return this;
    }
    
    default TextComponent applyFormat(final TextFormat textFormat) {
        final Style style2 = this.getStyle();
        if (textFormat.isColor()) {
            style2.setColor(textFormat);
        }
        if (textFormat.isModifier()) {
            switch (textFormat) {
                case q: {
                    style2.setObfuscated(true);
                    break;
                }
                case r: {
                    style2.setBold(true);
                    break;
                }
                case s: {
                    style2.setStrikethrough(true);
                    break;
                }
                case t: {
                    style2.setUnderline(true);
                    break;
                }
                case u: {
                    style2.setItalic(true);
                    break;
                }
            }
        }
        return this;
    }
    
    default TextComponent copyWithoutChildren(final TextComponent textComponent) {
        final TextComponent textComponent2 = textComponent.copyShallow();
        textComponent2.setStyle(textComponent.getStyle().copy());
        return textComponent2;
    }
    
    public static class Serializer implements JsonDeserializer<TextComponent>, JsonSerializer<TextComponent>
    {
        private static final Gson GSON;
        private static final Field POS_FIELD;
        private static final Field LINE_START_FIELD;
        
        public TextComponent a(final JsonElement functionJson, final Type unused, final JsonDeserializationContext context) throws JsonParseException {
            if (functionJson.isJsonPrimitive()) {
                return new StringTextComponent(functionJson.getAsString());
            }
            if (functionJson.isJsonObject()) {
                final JsonObject jsonObject4 = functionJson.getAsJsonObject();
                TextComponent textComponent5;
                if (jsonObject4.has("text")) {
                    textComponent5 = new StringTextComponent(jsonObject4.get("text").getAsString());
                }
                else if (jsonObject4.has("translate")) {
                    final String string6 = jsonObject4.get("translate").getAsString();
                    if (jsonObject4.has("with")) {
                        final JsonArray jsonArray7 = jsonObject4.getAsJsonArray("with");
                        final Object[] arr8 = new Object[jsonArray7.size()];
                        for (int integer9 = 0; integer9 < arr8.length; ++integer9) {
                            arr8[integer9] = this.a(jsonArray7.get(integer9), unused, context);
                            if (arr8[integer9] instanceof StringTextComponent) {
                                final StringTextComponent stringTextComponent10 = (StringTextComponent)arr8[integer9];
                                if (stringTextComponent10.getStyle().isEmpty() && stringTextComponent10.getSiblings().isEmpty()) {
                                    arr8[integer9] = stringTextComponent10.getTextField();
                                }
                            }
                        }
                        textComponent5 = new TranslatableTextComponent(string6, arr8);
                    }
                    else {
                        textComponent5 = new TranslatableTextComponent(string6, new Object[0]);
                    }
                }
                else if (jsonObject4.has("score")) {
                    final JsonObject jsonObject5 = jsonObject4.getAsJsonObject("score");
                    if (!jsonObject5.has("name") || !jsonObject5.has("objective")) {
                        throw new JsonParseException("A score component needs a least a name and an objective");
                    }
                    textComponent5 = new ScoreTextComponent(JsonHelper.getString(jsonObject5, "name"), JsonHelper.getString(jsonObject5, "objective"));
                    if (jsonObject5.has("value")) {
                        ((ScoreTextComponent)textComponent5).setText(JsonHelper.getString(jsonObject5, "value"));
                    }
                }
                else if (jsonObject4.has("selector")) {
                    textComponent5 = new SelectorTextComponent(JsonHelper.getString(jsonObject4, "selector"));
                }
                else if (jsonObject4.has("keybind")) {
                    textComponent5 = new KeybindTextComponent(JsonHelper.getString(jsonObject4, "keybind"));
                }
                else {
                    if (!jsonObject4.has("nbt")) {
                        throw new JsonParseException("Don't know how to turn " + functionJson + " into a Component");
                    }
                    final String string6 = JsonHelper.getString(jsonObject4, "nbt");
                    final boolean boolean7 = JsonHelper.getBoolean(jsonObject4, "interpret", false);
                    if (jsonObject4.has("block")) {
                        textComponent5 = new NbtTextComponent.BlockPosArgument(string6, boolean7, JsonHelper.getString(jsonObject4, "block"));
                    }
                    else {
                        if (!jsonObject4.has("entity")) {
                            throw new JsonParseException("Don't know how to turn " + functionJson + " into a Component");
                        }
                        textComponent5 = new NbtTextComponent.EntityNbtTextComponent(string6, boolean7, JsonHelper.getString(jsonObject4, "entity"));
                    }
                }
                if (jsonObject4.has("extra")) {
                    final JsonArray jsonArray8 = jsonObject4.getAsJsonArray("extra");
                    if (jsonArray8.size() <= 0) {
                        throw new JsonParseException("Unexpected empty array of components");
                    }
                    for (int integer10 = 0; integer10 < jsonArray8.size(); ++integer10) {
                        textComponent5.append(this.a(jsonArray8.get(integer10), unused, context));
                    }
                }
                textComponent5.setStyle(context.<Style>deserialize(functionJson, Style.class));
                return textComponent5;
            }
            if (functionJson.isJsonArray()) {
                final JsonArray jsonArray9 = functionJson.getAsJsonArray();
                TextComponent textComponent5 = null;
                for (final JsonElement jsonElement7 : jsonArray9) {
                    final TextComponent textComponent6 = this.a(jsonElement7, jsonElement7.getClass(), context);
                    if (textComponent5 == null) {
                        textComponent5 = textComponent6;
                    }
                    else {
                        textComponent5.append(textComponent6);
                    }
                }
                return textComponent5;
            }
            throw new JsonParseException("Don't know how to turn " + functionJson + " into a Component");
        }
        
        private void addStyle(final Style style, final JsonObject json, final JsonSerializationContext context) {
            final JsonElement jsonElement4 = context.serialize(style);
            if (jsonElement4.isJsonObject()) {
                final JsonObject jsonObject5 = (JsonObject)jsonElement4;
                for (final Map.Entry<String, JsonElement> entry7 : jsonObject5.entrySet()) {
                    json.add(entry7.getKey(), entry7.getValue());
                }
            }
        }
        
        public JsonElement a(final TextComponent textComponent, final Type type, final JsonSerializationContext jsonSerializationContext) {
            final JsonObject jsonObject4 = new JsonObject();
            if (!textComponent.getStyle().isEmpty()) {
                this.addStyle(textComponent.getStyle(), jsonObject4, jsonSerializationContext);
            }
            if (!textComponent.getSiblings().isEmpty()) {
                final JsonArray jsonArray5 = new JsonArray();
                for (final TextComponent textComponent2 : textComponent.getSiblings()) {
                    jsonArray5.add(this.a(textComponent2, textComponent2.getClass(), jsonSerializationContext));
                }
                jsonObject4.add("extra", jsonArray5);
            }
            if (textComponent instanceof StringTextComponent) {
                jsonObject4.addProperty("text", ((StringTextComponent)textComponent).getTextField());
            }
            else if (textComponent instanceof TranslatableTextComponent) {
                final TranslatableTextComponent translatableTextComponent5 = (TranslatableTextComponent)textComponent;
                jsonObject4.addProperty("translate", translatableTextComponent5.getKey());
                if (translatableTextComponent5.getParams() != null && translatableTextComponent5.getParams().length > 0) {
                    final JsonArray jsonArray6 = new JsonArray();
                    for (final Object object10 : translatableTextComponent5.getParams()) {
                        if (object10 instanceof TextComponent) {
                            jsonArray6.add(this.a((TextComponent)object10, object10.getClass(), jsonSerializationContext));
                        }
                        else {
                            jsonArray6.add(new JsonPrimitive(String.valueOf(object10)));
                        }
                    }
                    jsonObject4.add("with", jsonArray6);
                }
            }
            else if (textComponent instanceof ScoreTextComponent) {
                final ScoreTextComponent scoreTextComponent5 = (ScoreTextComponent)textComponent;
                final JsonObject jsonObject5 = new JsonObject();
                jsonObject5.addProperty("name", scoreTextComponent5.getName());
                jsonObject5.addProperty("objective", scoreTextComponent5.getObjective());
                jsonObject5.addProperty("value", scoreTextComponent5.getText());
                jsonObject4.add("score", jsonObject5);
            }
            else if (textComponent instanceof SelectorTextComponent) {
                final SelectorTextComponent selectorTextComponent5 = (SelectorTextComponent)textComponent;
                jsonObject4.addProperty("selector", selectorTextComponent5.getPattern());
            }
            else if (textComponent instanceof KeybindTextComponent) {
                final KeybindTextComponent keybindTextComponent5 = (KeybindTextComponent)textComponent;
                jsonObject4.addProperty("keybind", keybindTextComponent5.getKeybind());
            }
            else {
                if (!(textComponent instanceof NbtTextComponent)) {
                    throw new IllegalArgumentException("Don't know how to serialize " + textComponent + " as a Component");
                }
                final NbtTextComponent nbtTextComponent5 = (NbtTextComponent)textComponent;
                jsonObject4.addProperty("nbt", nbtTextComponent5.getPath());
                jsonObject4.addProperty("interpret", nbtTextComponent5.isComponentJson());
                if (textComponent instanceof NbtTextComponent.BlockPosArgument) {
                    final NbtTextComponent.BlockPosArgument blockPosArgument6 = (NbtTextComponent.BlockPosArgument)textComponent;
                    jsonObject4.addProperty("block", blockPosArgument6.getPos());
                }
                else {
                    if (!(textComponent instanceof NbtTextComponent.EntityNbtTextComponent)) {
                        throw new IllegalArgumentException("Don't know how to serialize " + textComponent + " as a Component");
                    }
                    final NbtTextComponent.EntityNbtTextComponent entityNbtTextComponent6 = (NbtTextComponent.EntityNbtTextComponent)textComponent;
                    jsonObject4.addProperty("entity", entityNbtTextComponent6.getSelector());
                }
            }
            return jsonObject4;
        }
        
        public static String toJsonString(final TextComponent textComponent) {
            return Serializer.GSON.toJson(textComponent);
        }
        
        public static JsonElement toJson(final TextComponent textComponent) {
            return Serializer.GSON.toJsonTree(textComponent);
        }
        
        @Nullable
        public static TextComponent fromJsonString(final String string) {
            return JsonHelper.<TextComponent>deserialize(Serializer.GSON, string, TextComponent.class, false);
        }
        
        @Nullable
        public static TextComponent fromJson(final JsonElement jsonElement) {
            return Serializer.GSON.<TextComponent>fromJson(jsonElement, TextComponent.class);
        }
        
        @Nullable
        public static TextComponent fromLenientJsonString(final String string) {
            return JsonHelper.<TextComponent>deserialize(Serializer.GSON, string, TextComponent.class, true);
        }
        
        public static TextComponent fromJsonString(final StringReader stringReader) {
            try {
                final JsonReader jsonReader2 = new JsonReader(new java.io.StringReader(stringReader.getRemaining()));
                jsonReader2.setLenient(false);
                final TextComponent textComponent3 = Serializer.GSON.<TextComponent>getAdapter(TextComponent.class).read(jsonReader2);
                stringReader.setCursor(stringReader.getCursor() + getReaderPosition(jsonReader2));
                return textComponent3;
            }
            catch (IOException iOException2) {
                throw new JsonParseException(iOException2);
            }
        }
        
        private static int getReaderPosition(final JsonReader jsonReader) {
            try {
                return Serializer.POS_FIELD.getInt(jsonReader) - Serializer.LINE_START_FIELD.getInt(jsonReader) + 1;
            }
            catch (IllegalAccessException illegalAccessException2) {
                throw new IllegalStateException("Couldn't read position of JsonReader", illegalAccessException2);
            }
        }
        
        static {
            final GsonBuilder gsonBuilder1;
            GSON = SystemUtil.<Gson>get(() -> {
                gsonBuilder1 = new GsonBuilder();
                gsonBuilder1.disableHtmlEscaping();
                gsonBuilder1.registerTypeHierarchyAdapter(TextComponent.class, new Serializer());
                gsonBuilder1.registerTypeHierarchyAdapter(Style.class, new Style.Serializer());
                gsonBuilder1.registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory());
                return gsonBuilder1.create();
            });
            Field field1;
            POS_FIELD = SystemUtil.<Field>get(() -> {
                try {
                    new JsonReader(new java.io.StringReader(""));
                    field1 = JsonReader.class.getDeclaredField("pos");
                    field1.setAccessible(true);
                    return field1;
                }
                catch (NoSuchFieldException noSuchFieldException1) {
                    throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", noSuchFieldException1);
                }
            });
            Field field2;
            LINE_START_FIELD = SystemUtil.<Field>get(() -> {
                try {
                    new JsonReader(new java.io.StringReader(""));
                    field2 = JsonReader.class.getDeclaredField("lineStart");
                    field2.setAccessible(true);
                    return field2;
                }
                catch (NoSuchFieldException noSuchFieldException2) {
                    throw new IllegalStateException("Couldn't get field 'lineStart' for JsonReader", noSuchFieldException2);
                }
            });
        }
    }
}
