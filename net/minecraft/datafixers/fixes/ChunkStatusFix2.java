package net.minecraft.datafixers.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.OpticFinder;
import java.util.Objects;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import com.mojang.datafixers.DataFix;

public class ChunkStatusFix2 extends DataFix
{
    private static final Map<String, String> statusMap;
    
    public ChunkStatusFix2(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    protected TypeRewriteRule makeRule() {
        final Type<?> type1 = this.getInputSchema().getType(TypeReferences.CHUNK);
        final Type<?> type2 = type1.findFieldType("Level");
        final OpticFinder<?> opticFinder3 = DSL.fieldFinder("Level", (Type)type2);
        final Dynamic<?> dynamic2;
        final String string3;
        final String string4;
        return this.fixTypeEverywhereTyped("ChunkStatusFix2", (Type)type1, this.getOutputSchema().getType(TypeReferences.CHUNK), typed -> typed.updateTyped((OpticFinder)opticFinder3, typed -> {
            dynamic2 = typed.get(DSL.remainderFinder());
            string3 = dynamic2.get("Status").asString("empty");
            string4 = ChunkStatusFix2.statusMap.getOrDefault(string3, "empty");
            if (Objects.equals(string3, string4)) {
                return typed;
            }
            else {
                return typed.set(DSL.remainderFinder(), dynamic2.set("Status", dynamic2.createString(string4)));
            }
        }));
    }
    
    static {
        statusMap = ImmutableMap.<String, String>builder().put("structure_references", "empty").put("biomes", "empty").put("base", "surface").put("carved", "carvers").put("liquid_carved", "liquid_carvers").put("decorated", "features").put("lighted", "light").put("mobs_spawned", "spawn").put("finalized", "heightmaps").put("fullchunk", "full").build();
    }
}
