package net.minecraft.datafixers.schemas;

import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.function.Supplier;
import java.util.Map;
import com.mojang.datafixers.schemas.Schema;

public class Schema1909 extends SchemaIdentifierNormalize
{
    public Schema1909(final int integer, final Schema schema) {
        super(integer, schema);
    }
    
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(final Schema schema) {
        final Map<String, Supplier<TypeTemplate>> map2 = (Map<String, Supplier<TypeTemplate>>)super.registerBlockEntities(schema);
        schema.registerSimple((Map)map2, "minecraft:jigsaw");
        return map2;
    }
}
