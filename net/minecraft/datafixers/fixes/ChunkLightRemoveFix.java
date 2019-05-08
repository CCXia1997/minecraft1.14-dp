package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class ChunkLightRemoveFix extends DataFix
{
    public ChunkLightRemoveFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    protected TypeRewriteRule makeRule() {
        final Type<?> type1 = this.getInputSchema().getType(TypeReferences.CHUNK);
        final Type<?> type2 = type1.findFieldType("Level");
        final OpticFinder<?> opticFinder3 = DSL.fieldFinder("Level", (Type)type2);
        return this.fixTypeEverywhereTyped("ChunkLightRemoveFix", (Type)type1, this.getOutputSchema().getType(TypeReferences.CHUNK), typed -> typed.updateTyped((OpticFinder)opticFinder3, typed -> typed.update(DSL.remainderFinder(), dynamic -> dynamic.remove("isLightOn"))));
    }
}
