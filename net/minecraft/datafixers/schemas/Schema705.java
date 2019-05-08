package net.minecraft.datafixers.schemas;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.function.Supplier;
import java.util.Map;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.Hook;

public class Schema705 extends SchemaIdentifierNormalize
{
    protected static final Hook.HookFunction a;
    
    public Schema705(final int integer, final Schema schema) {
        super(integer, schema);
    }
    
    protected static void a(final Schema schema, final Map<String, Supplier<TypeTemplate>> map, final String string) {
        schema.register((Map)map, string, () -> Schema100.a(schema));
    }
    
    protected static void b(final Schema schema, final Map<String, Supplier<TypeTemplate>> map, final String string) {
        schema.register((Map)map, string, () -> DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema)));
    }
    
    public Map<String, Supplier<TypeTemplate>> registerEntities(final Schema schema) {
        final Map<String, Supplier<TypeTemplate>> map2 = Maps.newHashMap();
        schema.registerSimple((Map)map2, "minecraft:area_effect_cloud");
        a(schema, map2, "minecraft:armor_stand");
        schema.register((Map)map2, "minecraft:arrow", string -> DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema)));
        a(schema, map2, "minecraft:bat");
        a(schema, map2, "minecraft:blaze");
        schema.registerSimple((Map)map2, "minecraft:boat");
        a(schema, map2, "minecraft:cave_spider");
        schema.register((Map)map2, "minecraft:chest_minecart", string -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))));
        a(schema, map2, "minecraft:chicken");
        schema.register((Map)map2, "minecraft:commandblock_minecart", string -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema)));
        a(schema, map2, "minecraft:cow");
        a(schema, map2, "minecraft:creeper");
        schema.register((Map)map2, "minecraft:donkey", string -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.a(schema)));
        schema.registerSimple((Map)map2, "minecraft:dragon_fireball");
        b(schema, map2, "minecraft:egg");
        a(schema, map2, "minecraft:elder_guardian");
        schema.registerSimple((Map)map2, "minecraft:ender_crystal");
        a(schema, map2, "minecraft:ender_dragon");
        schema.register((Map)map2, "minecraft:enderman", string -> DSL.optionalFields("carried", TypeReferences.BLOCK_NAME.in(schema), Schema100.a(schema)));
        a(schema, map2, "minecraft:endermite");
        b(schema, map2, "minecraft:ender_pearl");
        schema.registerSimple((Map)map2, "minecraft:eye_of_ender_signal");
        schema.register((Map)map2, "minecraft:falling_block", string -> DSL.optionalFields("Block", TypeReferences.BLOCK_NAME.in(schema), "TileEntityData", TypeReferences.BLOCK_ENTITY.in(schema)));
        b(schema, map2, "minecraft:fireball");
        schema.register((Map)map2, "minecraft:fireworks_rocket", string -> DSL.optionalFields("FireworksItem", TypeReferences.ITEM_STACK.in(schema)));
        schema.register((Map)map2, "minecraft:furnace_minecart", string -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema)));
        a(schema, map2, "minecraft:ghast");
        a(schema, map2, "minecraft:giant");
        a(schema, map2, "minecraft:guardian");
        schema.register((Map)map2, "minecraft:hopper_minecart", string -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))));
        schema.register((Map)map2, "minecraft:horse", string -> DSL.optionalFields("ArmorItem", TypeReferences.ITEM_STACK.in(schema), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.a(schema)));
        a(schema, map2, "minecraft:husk");
        schema.register((Map)map2, "minecraft:item", string -> DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(schema)));
        schema.register((Map)map2, "minecraft:item_frame", string -> DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(schema)));
        schema.registerSimple((Map)map2, "minecraft:leash_knot");
        a(schema, map2, "minecraft:magma_cube");
        schema.register((Map)map2, "minecraft:minecart", string -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema)));
        a(schema, map2, "minecraft:mooshroom");
        schema.register((Map)map2, "minecraft:mule", string -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.a(schema)));
        a(schema, map2, "minecraft:ocelot");
        schema.registerSimple((Map)map2, "minecraft:painting");
        schema.registerSimple((Map)map2, "minecraft:parrot");
        a(schema, map2, "minecraft:pig");
        a(schema, map2, "minecraft:polar_bear");
        schema.register((Map)map2, "minecraft:potion", string -> DSL.optionalFields("Potion", TypeReferences.ITEM_STACK.in(schema), "inTile", TypeReferences.BLOCK_NAME.in(schema)));
        a(schema, map2, "minecraft:rabbit");
        a(schema, map2, "minecraft:sheep");
        a(schema, map2, "minecraft:shulker");
        schema.registerSimple((Map)map2, "minecraft:shulker_bullet");
        a(schema, map2, "minecraft:silverfish");
        a(schema, map2, "minecraft:skeleton");
        schema.register((Map)map2, "minecraft:skeleton_horse", string -> DSL.optionalFields("SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.a(schema)));
        a(schema, map2, "minecraft:slime");
        b(schema, map2, "minecraft:small_fireball");
        b(schema, map2, "minecraft:snowball");
        a(schema, map2, "minecraft:snowman");
        schema.register((Map)map2, "minecraft:spawner_minecart", string -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema), TypeReferences.UNTAGGED_SPAWNER.in(schema)));
        schema.register((Map)map2, "minecraft:spectral_arrow", string -> DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema)));
        a(schema, map2, "minecraft:spider");
        a(schema, map2, "minecraft:squid");
        a(schema, map2, "minecraft:stray");
        schema.registerSimple((Map)map2, "minecraft:tnt");
        schema.register((Map)map2, "minecraft:tnt_minecart", string -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema)));
        schema.register((Map)map2, "minecraft:villager", string -> DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", TypeReferences.ITEM_STACK.in(schema), "buyB", TypeReferences.ITEM_STACK.in(schema), "sell", TypeReferences.ITEM_STACK.in(schema)))), Schema100.a(schema)));
        a(schema, map2, "minecraft:villager_golem");
        a(schema, map2, "minecraft:witch");
        a(schema, map2, "minecraft:wither");
        a(schema, map2, "minecraft:wither_skeleton");
        b(schema, map2, "minecraft:wither_skull");
        a(schema, map2, "minecraft:wolf");
        b(schema, map2, "minecraft:xp_bottle");
        schema.registerSimple((Map)map2, "minecraft:xp_orb");
        a(schema, map2, "minecraft:zombie");
        schema.register((Map)map2, "minecraft:zombie_horse", string -> DSL.optionalFields("SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.a(schema)));
        a(schema, map2, "minecraft:zombie_pigman");
        a(schema, map2, "minecraft:zombie_villager");
        schema.registerSimple((Map)map2, "minecraft:evocation_fangs");
        a(schema, map2, "minecraft:evocation_illager");
        schema.registerSimple((Map)map2, "minecraft:illusion_illager");
        schema.register((Map)map2, "minecraft:llama", string -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), "DecorItem", TypeReferences.ITEM_STACK.in(schema), Schema100.a(schema)));
        schema.registerSimple((Map)map2, "minecraft:llama_spit");
        a(schema, map2, "minecraft:vex");
        a(schema, map2, "minecraft:vindication_illager");
        return map2;
    }
    
    public void registerTypes(final Schema schema, final Map<String, Supplier<TypeTemplate>> map2, final Map<String, Supplier<TypeTemplate>> map3) {
        super.registerTypes(schema, (Map)map2, (Map)map3);
        schema.registerType(true, TypeReferences.ENTITY, () -> DSL.taggedChoiceLazy("id", DSL.namespacedString(), (Map)map2));
        schema.registerType(true, TypeReferences.ITEM_STACK, () -> DSL.hook(DSL.optionalFields("id", TypeReferences.ITEM_NAME.in(schema), "tag", DSL.optionalFields("EntityTag", TypeReferences.ENTITY_TREE.in(schema), "BlockEntityTag", TypeReferences.BLOCK_ENTITY.in(schema), "CanDestroy", DSL.list(TypeReferences.BLOCK_NAME.in(schema)), "CanPlaceOn", DSL.list(TypeReferences.BLOCK_NAME.in(schema)))), Schema705.a, Hook.HookFunction.IDENTITY));
    }
    
    static {
        a = (Hook.HookFunction)new Hook.HookFunction() {
            public <T> T apply(final DynamicOps<T> dynamicOps, final T object) {
                return Schema99.<T>a((com.mojang.datafixers.Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, object), Schema704.a, "minecraft:armor_stand");
            }
        };
    }
}
