package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.Typed;
import java.util.Optional;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class ItemWaterPotionFix extends DataFix
{
    public ItemWaterPotionFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    public TypeRewriteRule makeRule() {
        final Type<?> type1 = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
        final OpticFinder<Pair<String, String>> opticFinder2 = (OpticFinder<Pair<String, String>>)DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
        final OpticFinder<?> opticFinder3 = type1.findField("tag");
        final OpticFinder opticFinder4;
        final Optional<Pair<String, String>> optional4;
        String string5;
        final OpticFinder opticFinder5;
        Typed<?> typed2;
        Dynamic<?> dynamic7;
        return this.fixTypeEverywhereTyped("ItemWaterPotionFix", (Type)type1, typed -> {
            optional4 = (Optional<Pair<String, String>>)typed.getOptional(opticFinder4);
            if (optional4.isPresent()) {
                string5 = (String)optional4.get().getSecond();
                if ("minecraft:potion".equals(string5) || "minecraft:splash_potion".equals(string5) || "minecraft:lingering_potion".equals(string5) || "minecraft:tipped_arrow".equals(string5)) {
                    typed2 = typed.getOrCreateTyped(opticFinder5);
                    dynamic7 = typed2.get(DSL.remainderFinder());
                    if (!dynamic7.get("Potion").asString().isPresent()) {
                        dynamic7 = dynamic7.set("Potion", dynamic7.createString("minecraft:water"));
                    }
                    return typed.set(opticFinder5, typed2.set(DSL.remainderFinder(), dynamic7));
                }
            }
            return typed;
        });
    }
}
