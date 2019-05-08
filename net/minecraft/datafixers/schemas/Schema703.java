package net.minecraft.datafixers.schemas;

import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.function.Supplier;
import java.util.Map;
import com.mojang.datafixers.schemas.Schema;

public class Schema703 extends Schema
{
    public Schema703(final int integer, final Schema schema) {
        super(integer, schema);
    }
    
    public Map<String, Supplier<TypeTemplate>> registerEntities(final Schema schema) {
        final Map<String, Supplier<TypeTemplate>> map2 = (Map<String, Supplier<TypeTemplate>>)super.registerEntities(schema);
        map2.remove("EntityHorse");
        schema.register((Map)map2, "Horse", () -> DSL.optionalFields("ArmorItem", TypeReferences.ITEM_STACK.in(schema), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.a(schema)));
        schema.register((Map)map2, "Donkey", () -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.a(schema)));
        schema.register((Map)map2, "Mule", () -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.a(schema)));
        schema.register((Map)map2, "ZombieHorse", () -> DSL.optionalFields("SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.a(schema)));
        schema.register((Map)map2, "SkeletonHorse", () -> DSL.optionalFields("SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.a(schema)));
        return map2;
    }
}
