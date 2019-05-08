package net.minecraft.datafixers.schemas;

import java.util.function.Supplier;
import java.util.Map;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.schemas.Schema;

public class Schema1928 extends SchemaIdentifierNormalize
{
    public Schema1928(final int integer, final Schema schema) {
        super(integer, schema);
    }
    
    protected static TypeTemplate a(final Schema schema) {
        return DSL.optionalFields("ArmorItems", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "HandItems", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
    }
    
    protected static void a(final Schema schema, final Map<String, Supplier<TypeTemplate>> map, final String string) {
        schema.register((Map)map, string, () -> a(schema));
    }
    
    public Map<String, Supplier<TypeTemplate>> registerEntities(final Schema schema) {
        final Map<String, Supplier<TypeTemplate>> map2 = (Map<String, Supplier<TypeTemplate>>)super.registerEntities(schema);
        map2.remove("minecraft:illager_beast");
        a(schema, map2, "minecraft:ravager");
        return map2;
    }
}
