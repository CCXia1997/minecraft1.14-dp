package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.Typed;
import com.mojang.datafixers.OpticFinder;
import java.util.Objects;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class ChunkStatusFix extends DataFix
{
    public ChunkStatusFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    protected TypeRewriteRule makeRule() {
        final Type<?> type1 = this.getInputSchema().getType(TypeReferences.CHUNK);
        final Type<?> type2 = type1.findFieldType("Level");
        final OpticFinder<?> opticFinder3 = DSL.fieldFinder("Level", (Type)type2);
        Dynamic<?> dynamic2;
        final String string3;
        return this.fixTypeEverywhereTyped("ChunkStatusFix", (Type)type1, this.getOutputSchema().getType(TypeReferences.CHUNK), typed -> typed.updateTyped((OpticFinder)opticFinder3, typed -> {
            dynamic2 = typed.get(DSL.remainderFinder());
            string3 = dynamic2.get("Status").asString("empty");
            if (Objects.equals(string3, "postprocessed")) {
                dynamic2 = dynamic2.set("Status", dynamic2.createString("fullchunk"));
            }
            return typed.set(DSL.remainderFinder(), dynamic2);
        }));
    }
}
