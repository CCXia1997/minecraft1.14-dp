package net.minecraft.datafixers.schemas;

import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.function.Supplier;
import java.util.Map;
import com.mojang.datafixers.schemas.Schema;

public class Schema1470 extends SchemaIdentifierNormalize
{
    public Schema1470(final int integer, final Schema schema) {
        super(integer, schema);
    }
    
    protected static void a(final Schema schema, final Map<String, Supplier<TypeTemplate>> map, final String string) {
        schema.register((Map)map, string, () -> Schema100.a(schema));
    }
    
    public Map<String, Supplier<TypeTemplate>> registerEntities(final Schema schema) {
        final Map<String, Supplier<TypeTemplate>> map2 = (Map<String, Supplier<TypeTemplate>>)super.registerEntities(schema);
        a(schema, map2, "minecraft:turtle");
        a(schema, map2, "minecraft:cod_mob");
        a(schema, map2, "minecraft:tropical_fish");
        a(schema, map2, "minecraft:salmon_mob");
        a(schema, map2, "minecraft:puffer_fish");
        a(schema, map2, "minecraft:phantom");
        a(schema, map2, "minecraft:dolphin");
        a(schema, map2, "minecraft:drowned");
        schema.register((Map)map2, "minecraft:trident", string -> DSL.optionalFields("inBlockState", TypeReferences.BLOCK_STATE.in(schema)));
        return map2;
    }
}
