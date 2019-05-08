package net.minecraft.datafixers.fixes;

import java.util.stream.Stream;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import java.util.Optional;
import com.mojang.datafixers.DataFixUtils;
import java.util.function.Function;
import com.google.gson.JsonParseException;
import net.minecraft.util.JsonHelper;
import net.minecraft.text.TextComponent;
import net.minecraft.text.StringTextComponent;
import org.apache.commons.lang3.StringUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class ItemWrittenBookPagesStrictJsonFix extends DataFix
{
    public ItemWrittenBookPagesStrictJsonFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    public Dynamic<?> a(final Dynamic<?> dynamic) {
        String string2;
        TextComponent textComponent3;
        return dynamic.update("pages", dynamic2 -> (Dynamic)DataFixUtils.orElse((Optional)dynamic2.asStreamOpt().map(stream -> stream.map(dynamic -> {
            if (!dynamic.asString().isPresent()) {
                return dynamic;
            }
            else {
                string2 = dynamic.asString("");
                textComponent3 = null;
                if ("null".equals(string2) || StringUtils.isEmpty((CharSequence)string2)) {
                    textComponent3 = new StringTextComponent("");
                }
                else {
                    if (string2.charAt(0) != 34 || string2.charAt(string2.length() - 1) != 34) {
                        if (string2.charAt(0) != 123 || string2.charAt(string2.length() - 1) != 125) {
                            textComponent3 = new StringTextComponent(string2);
                            return dynamic.createString(TextComponent.Serializer.toJsonString(textComponent3));
                        }
                    }
                    try {
                        textComponent3 = JsonHelper.<TextComponent>deserialize(BlockEntitySignTextStrictJsonFix.GSON, string2, TextComponent.class, (boolean)(1 != 0));
                        if (textComponent3 == null) {
                            textComponent3 = new StringTextComponent("");
                        }
                    }
                    catch (JsonParseException ex) {}
                    if (textComponent3 == null) {
                        try {
                            textComponent3 = TextComponent.Serializer.fromJsonString(string2);
                        }
                        catch (JsonParseException ex2) {}
                    }
                    if (textComponent3 == null) {
                        try {
                            textComponent3 = TextComponent.Serializer.fromLenientJsonString(string2);
                        }
                        catch (JsonParseException ex3) {}
                    }
                    if (textComponent3 == null) {
                        textComponent3 = new StringTextComponent(string2);
                    }
                }
                return dynamic.createString(TextComponent.Serializer.toJsonString(textComponent3));
            }
        })).map(dynamic::createList), dynamic.emptyList()));
    }
    
    public TypeRewriteRule makeRule() {
        final Type<?> type1 = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
        final OpticFinder<?> opticFinder2 = type1.findField("tag");
        return this.fixTypeEverywhereTyped("ItemWrittenBookPagesStrictJsonFix", (Type)type1, typed -> typed.updateTyped((OpticFinder)opticFinder2, typed -> typed.update(DSL.remainderFinder(), (Function)this::a)));
    }
}
