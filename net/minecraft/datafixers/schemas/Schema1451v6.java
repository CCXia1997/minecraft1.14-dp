package net.minecraft.datafixers.schemas;

import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.function.Supplier;
import java.util.Map;
import com.mojang.datafixers.schemas.Schema;

public class Schema1451v6 extends SchemaIdentifierNormalize
{
    public Schema1451v6(final int integer, final Schema schema) {
        super(integer, schema);
    }
    
    public void registerTypes(final Schema schema, final Map<String, Supplier<TypeTemplate>> map2, final Map<String, Supplier<TypeTemplate>> map3) {
        super.registerTypes(schema, (Map)map2, (Map)map3);
        final Supplier<TypeTemplate> supplier4 = () -> DSL.compoundList(TypeReferences.ITEM_NAME.in(schema), DSL.constType(DSL.intType()));
        final Supplier<TypeTemplate> supplier5;
        schema.registerType(false, TypeReferences.STATS, () -> DSL.optionalFields("stats", DSL.optionalFields("minecraft:mined", DSL.compoundList(TypeReferences.BLOCK_NAME.in(schema), DSL.constType(DSL.intType())), "minecraft:crafted", (TypeTemplate)supplier5.get(), "minecraft:used", (TypeTemplate)supplier5.get(), "minecraft:broken", (TypeTemplate)supplier5.get(), "minecraft:picked_up", (TypeTemplate)supplier5.get(), DSL.optionalFields("minecraft:dropped", (TypeTemplate)supplier5.get(), "minecraft:killed", DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema), DSL.constType(DSL.intType())), "minecraft:killed_by", DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema), DSL.constType(DSL.intType())), "minecraft:custom", DSL.compoundList(DSL.constType(DSL.namespacedString()), DSL.constType(DSL.intType()))))));
    }
}
