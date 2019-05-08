package net.minecraft.datafixers.schemas;

import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.function.Supplier;
import java.util.Map;
import com.mojang.datafixers.schemas.Schema;

public class Schema1906 extends SchemaIdentifierNormalize
{
    public Schema1906(final int integer, final Schema schema) {
        super(integer, schema);
    }
    
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(final Schema schema) {
        final Map<String, Supplier<TypeTemplate>> map2 = (Map<String, Supplier<TypeTemplate>>)super.registerBlockEntities(schema);
        a(schema, map2, "minecraft:barrel");
        a(schema, map2, "minecraft:smoker");
        a(schema, map2, "minecraft:blast_furnace");
        schema.register((Map)map2, "minecraft:lectern", string -> DSL.optionalFields("Book", TypeReferences.ITEM_STACK.in(schema)));
        schema.registerSimple((Map)map2, "minecraft:bell");
        return map2;
    }
    
    protected static void a(final Schema schema, final Map<String, Supplier<TypeTemplate>> map, final String string) {
        schema.register((Map)map, string, () -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))));
    }
}
