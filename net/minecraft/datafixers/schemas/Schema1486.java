package net.minecraft.datafixers.schemas;

import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.function.Supplier;
import java.util.Map;
import com.mojang.datafixers.schemas.Schema;

public class Schema1486 extends SchemaIdentifierNormalize
{
    public Schema1486(final int integer, final Schema schema) {
        super(integer, schema);
    }
    
    public Map<String, Supplier<TypeTemplate>> registerEntities(final Schema schema) {
        final Map<String, Supplier<TypeTemplate>> map2 = (Map<String, Supplier<TypeTemplate>>)super.registerEntities(schema);
        map2.put("minecraft:cod", map2.remove("minecraft:cod_mob"));
        map2.put("minecraft:salmon", map2.remove("minecraft:salmon_mob"));
        return map2;
    }
}
