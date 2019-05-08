package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.Typed;
import java.util.Optional;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.Dynamic;
import java.util.Objects;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class ItemInstanceMapIdFix extends DataFix
{
    public ItemInstanceMapIdFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    public TypeRewriteRule makeRule() {
        final Type<?> type1 = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
        final OpticFinder<Pair<String, String>> opticFinder2 = (OpticFinder<Pair<String, String>>)DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
        final OpticFinder<?> opticFinder3 = type1.findField("tag");
        final OpticFinder opticFinder4;
        final Optional<Pair<String, String>> optional4;
        Dynamic<?> dynamic5;
        final OpticFinder opticFinder5;
        Typed<?> typed2;
        Dynamic<?> dynamic6;
        Dynamic<?> dynamic7;
        return this.fixTypeEverywhereTyped("ItemInstanceMapIdFix", (Type)type1, typed -> {
            optional4 = (Optional<Pair<String, String>>)typed.getOptional(opticFinder4);
            if (optional4.isPresent() && Objects.equals(optional4.get().getSecond(), "minecraft:filled_map")) {
                dynamic5 = typed.get(DSL.remainderFinder());
                typed2 = typed.getOrCreateTyped(opticFinder5);
                dynamic6 = typed2.get(DSL.remainderFinder());
                dynamic7 = dynamic6.set("map", dynamic6.createInt(dynamic5.get("Damage").asInt(0)));
                return typed.set(opticFinder5, typed2.set(DSL.remainderFinder(), dynamic7));
            }
            else {
                return typed;
            }
        });
    }
}
