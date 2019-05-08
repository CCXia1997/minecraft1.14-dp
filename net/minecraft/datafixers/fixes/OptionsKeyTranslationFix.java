package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.Typed;
import java.util.stream.Collector;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class OptionsKeyTranslationFix extends DataFix
{
    public OptionsKeyTranslationFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    public TypeRewriteRule makeRule() {
        String string3;
        return this.fixTypeEverywhereTyped("OptionsKeyTranslationFix", this.getInputSchema().getType(TypeReferences.OPTIONS), typed -> typed.update(DSL.remainderFinder(), dynamic -> dynamic.getMapValues().map(map -> dynamic.createMap((Map)map.entrySet().stream().map(entry -> {
            if (entry.getKey().asString("").startsWith("key_")) {
                string3 = ((Dynamic)entry.getValue()).asString("");
                if (!string3.startsWith("key.mouse") && !string3.startsWith("scancode.")) {
                    return Pair.of(entry.getKey(), dynamic.createString("key.keyboard." + string3.substring("key.".length())));
                }
            }
            return Pair.of(entry.getKey(), entry.getValue());
        }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)))).orElse(dynamic)));
    }
}
