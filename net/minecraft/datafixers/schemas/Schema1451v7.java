package net.minecraft.datafixers.schemas;

import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.function.Supplier;
import java.util.Map;
import com.mojang.datafixers.schemas.Schema;

public class Schema1451v7 extends SchemaIdentifierNormalize
{
    public Schema1451v7(final int integer, final Schema schema) {
        super(integer, schema);
    }
    
    public void registerTypes(final Schema schema, final Map<String, Supplier<TypeTemplate>> map2, final Map<String, Supplier<TypeTemplate>> map3) {
        super.registerTypes(schema, (Map)map2, (Map)map3);
        schema.registerType(false, TypeReferences.STRUCTURE_FEATURE, () -> DSL.optionalFields("Children", DSL.list(DSL.optionalFields("CA", TypeReferences.BLOCK_STATE.in(schema), "CB", TypeReferences.BLOCK_STATE.in(schema), "CC", TypeReferences.BLOCK_STATE.in(schema), "CD", TypeReferences.BLOCK_STATE.in(schema)))));
    }
}
