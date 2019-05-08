package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import java.util.Optional;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class MapIdFix extends DataFix
{
    public MapIdFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    protected TypeRewriteRule makeRule() {
        final Type<?> type1 = this.getInputSchema().getType(TypeReferences.SAVED_DATA);
        final OpticFinder<?> opticFinder2 = type1.findField("data");
        final Optional<? extends Typed<?>> optional3;
        return this.fixTypeEverywhereTyped("Map id fix", (Type)type1, typed -> {
            optional3 = typed.getOptionalTyped((OpticFinder)opticFinder2);
            if (optional3.isPresent()) {
                return typed;
            }
            else {
                return typed.update(DSL.remainderFinder(), dynamic -> dynamic.emptyMap().merge(dynamic.createString("data"), dynamic));
            }
        });
    }
}
