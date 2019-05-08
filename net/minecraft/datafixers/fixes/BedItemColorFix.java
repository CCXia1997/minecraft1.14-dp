package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.Typed;
import java.util.Optional;
import com.mojang.datafixers.Dynamic;
import java.util.Objects;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class BedItemColorFix extends DataFix
{
    public BedItemColorFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    public TypeRewriteRule makeRule() {
        final OpticFinder<Pair<String, String>> opticFinder1 = (OpticFinder<Pair<String, String>>)DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
        final Optional<Pair<String, String>> optional3;
        Dynamic<?> dynamic4;
        return this.fixTypeEverywhereTyped("BedItemColorFix", this.getInputSchema().getType(TypeReferences.ITEM_STACK), typed -> {
            optional3 = (Optional<Pair<String, String>>)typed.getOptional((OpticFinder)opticFinder1);
            if (optional3.isPresent() && Objects.equals(optional3.get().getSecond(), "minecraft:bed")) {
                dynamic4 = typed.get(DSL.remainderFinder());
                if (dynamic4.get("Damage").asInt(0) == 0) {
                    return typed.set(DSL.remainderFinder(), dynamic4.set("Damage", dynamic4.createShort((short)14)));
                }
            }
            return typed;
        });
    }
}
