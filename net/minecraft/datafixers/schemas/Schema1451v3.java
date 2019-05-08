package net.minecraft.datafixers.schemas;

import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.function.Supplier;
import java.util.Map;
import com.mojang.datafixers.schemas.Schema;

public class Schema1451v3 extends SchemaIdentifierNormalize
{
    public Schema1451v3(final int integer, final Schema schema) {
        super(integer, schema);
    }
    
    public Map<String, Supplier<TypeTemplate>> registerEntities(final Schema schema) {
        final Map<String, Supplier<TypeTemplate>> map2 = (Map<String, Supplier<TypeTemplate>>)super.registerEntities(schema);
        schema.registerSimple((Map)map2, "minecraft:egg");
        schema.registerSimple((Map)map2, "minecraft:ender_pearl");
        schema.registerSimple((Map)map2, "minecraft:fireball");
        schema.register((Map)map2, "minecraft:potion", string -> DSL.optionalFields("Potion", TypeReferences.ITEM_STACK.in(schema)));
        schema.registerSimple((Map)map2, "minecraft:small_fireball");
        schema.registerSimple((Map)map2, "minecraft:snowball");
        schema.registerSimple((Map)map2, "minecraft:wither_skull");
        schema.registerSimple((Map)map2, "minecraft:xp_bottle");
        schema.register((Map)map2, "minecraft:arrow", () -> DSL.optionalFields("inBlockState", TypeReferences.BLOCK_STATE.in(schema)));
        schema.register((Map)map2, "minecraft:enderman", () -> DSL.optionalFields("carriedBlockState", TypeReferences.BLOCK_STATE.in(schema), Schema100.a(schema)));
        schema.register((Map)map2, "minecraft:falling_block", () -> DSL.optionalFields("BlockState", TypeReferences.BLOCK_STATE.in(schema), "TileEntityData", TypeReferences.BLOCK_ENTITY.in(schema)));
        schema.register((Map)map2, "minecraft:spectral_arrow", () -> DSL.optionalFields("inBlockState", TypeReferences.BLOCK_STATE.in(schema)));
        schema.register((Map)map2, "minecraft:chest_minecart", () -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))));
        schema.register((Map)map2, "minecraft:commandblock_minecart", () -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema)));
        schema.register((Map)map2, "minecraft:furnace_minecart", () -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema)));
        schema.register((Map)map2, "minecraft:hopper_minecart", () -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))));
        schema.register((Map)map2, "minecraft:minecart", () -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema)));
        schema.register((Map)map2, "minecraft:spawner_minecart", () -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema), TypeReferences.UNTAGGED_SPAWNER.in(schema)));
        schema.register((Map)map2, "minecraft:tnt_minecart", () -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema)));
        return map2;
    }
}
