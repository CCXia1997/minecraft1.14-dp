package net.minecraft.datafixers.schemas;

import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.function.Supplier;
import java.util.Map;
import com.mojang.datafixers.schemas.Schema;

public class Schema1483 extends SchemaIdentifierNormalize
{
    public Schema1483(final int integer, final Schema schema) {
        super(integer, schema);
    }
    
    public Map<String, Supplier<TypeTemplate>> registerEntities(final Schema schema) {
        final Map<String, Supplier<TypeTemplate>> map2 = (Map<String, Supplier<TypeTemplate>>)super.registerEntities(schema);
        map2.put("minecraft:pufferfish", map2.remove("minecraft:puffer_fish"));
        return map2;
    }
}
