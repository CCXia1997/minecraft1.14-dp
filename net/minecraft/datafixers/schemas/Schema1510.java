package net.minecraft.datafixers.schemas;

import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.function.Supplier;
import java.util.Map;
import com.mojang.datafixers.schemas.Schema;

public class Schema1510 extends SchemaIdentifierNormalize
{
    public Schema1510(final int integer, final Schema schema) {
        super(integer, schema);
    }
    
    public Map<String, Supplier<TypeTemplate>> registerEntities(final Schema schema) {
        final Map<String, Supplier<TypeTemplate>> map2 = (Map<String, Supplier<TypeTemplate>>)super.registerEntities(schema);
        map2.put("minecraft:command_block_minecart", map2.remove("minecraft:commandblock_minecart"));
        map2.put("minecraft:end_crystal", map2.remove("minecraft:ender_crystal"));
        map2.put("minecraft:snow_golem", map2.remove("minecraft:snowman"));
        map2.put("minecraft:evoker", map2.remove("minecraft:evocation_illager"));
        map2.put("minecraft:evoker_fangs", map2.remove("minecraft:evocation_fangs"));
        map2.put("minecraft:illusioner", map2.remove("minecraft:illusion_illager"));
        map2.put("minecraft:vindicator", map2.remove("minecraft:vindication_illager"));
        map2.put("minecraft:iron_golem", map2.remove("minecraft:villager_golem"));
        map2.put("minecraft:experience_orb", map2.remove("minecraft:xp_orb"));
        map2.put("minecraft:experience_bottle", map2.remove("minecraft:xp_bottle"));
        map2.put("minecraft:eye_of_ender", map2.remove("minecraft:eye_of_ender_signal"));
        map2.put("minecraft:firework_rocket", map2.remove("minecraft:fireworks_rocket"));
        return map2;
    }
}
