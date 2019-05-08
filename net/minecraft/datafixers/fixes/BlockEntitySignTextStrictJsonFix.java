package net.minecraft.datafixers.fixes;

import java.util.Iterator;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializer;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.google.gson.JsonParseException;
import net.minecraft.util.JsonHelper;
import net.minecraft.text.TextComponent;
import net.minecraft.text.StringTextComponent;
import org.apache.commons.lang3.StringUtils;
import com.mojang.datafixers.Dynamic;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.schemas.Schema;
import com.google.gson.Gson;

public class BlockEntitySignTextStrictJsonFix extends ChoiceFix
{
    public static final Gson GSON;
    
    public BlockEntitySignTextStrictJsonFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2, "BlockEntitySignTextStrictJsonFix", TypeReferences.BLOCK_ENTITY, "Sign");
    }
    
    private Dynamic<?> a(final Dynamic<?> dynamic, final String string) {
        final String string2 = dynamic.get(string).asString("");
        TextComponent textComponent4 = null;
        if ("null".equals(string2) || StringUtils.isEmpty((CharSequence)string2)) {
            textComponent4 = new StringTextComponent("");
        }
        else {
            if (string2.charAt(0) != '\"' || string2.charAt(string2.length() - 1) != '\"') {
                if (string2.charAt(0) != '{' || string2.charAt(string2.length() - 1) != '}') {
                    textComponent4 = new StringTextComponent(string2);
                    return dynamic.set(string, dynamic.createString(TextComponent.Serializer.toJsonString(textComponent4)));
                }
            }
            try {
                textComponent4 = JsonHelper.<TextComponent>deserialize(BlockEntitySignTextStrictJsonFix.GSON, string2, TextComponent.class, true);
                if (textComponent4 == null) {
                    textComponent4 = new StringTextComponent("");
                }
            }
            catch (JsonParseException ex) {}
            if (textComponent4 == null) {
                try {
                    textComponent4 = TextComponent.Serializer.fromJsonString(string2);
                }
                catch (JsonParseException ex2) {}
            }
            if (textComponent4 == null) {
                try {
                    textComponent4 = TextComponent.Serializer.fromLenientJsonString(string2);
                }
                catch (JsonParseException ex3) {}
            }
            if (textComponent4 == null) {
                textComponent4 = new StringTextComponent(string2);
            }
        }
        return dynamic.set(string, dynamic.createString(TextComponent.Serializer.toJsonString(textComponent4)));
    }
    
    @Override
    protected Typed<?> transform(final Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), dynamic -> {
            dynamic = this.a(dynamic, "Text1");
            dynamic = this.a(dynamic, "Text2");
            dynamic = this.a(dynamic, "Text3");
            dynamic = this.a(dynamic, "Text4");
            return dynamic;
        });
    }
    
    static {
        GSON = new GsonBuilder().registerTypeAdapter(TextComponent.class, new JsonDeserializer<TextComponent>() {
            public TextComponent a(final JsonElement functionJson, final Type unused, final JsonDeserializationContext context) throws JsonParseException {
                if (functionJson.isJsonPrimitive()) {
                    return new StringTextComponent(functionJson.getAsString());
                }
                if (functionJson.isJsonArray()) {
                    final JsonArray jsonArray4 = functionJson.getAsJsonArray();
                    TextComponent textComponent5 = null;
                    for (final JsonElement jsonElement7 : jsonArray4) {
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
        }).create();
    }
}
