package net.minecraft.datafixers.schemas;

import com.mojang.datafixers.types.templates.Hook;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.function.Supplier;
import java.util.Map;
import com.mojang.datafixers.schemas.Schema;

public class Schema1460 extends SchemaIdentifierNormalize
{
    public Schema1460(final int integer, final Schema schema) {
        super(integer, schema);
    }
    
    protected static void a(final Schema schema, final Map<String, Supplier<TypeTemplate>> map, final String string) {
        schema.register((Map)map, string, () -> Schema100.a(schema));
    }
    
    protected static void b(final Schema schema, final Map<String, Supplier<TypeTemplate>> map, final String string) {
        schema.register((Map)map, string, () -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))));
    }
    
    public Map<String, Supplier<TypeTemplate>> registerEntities(final Schema schema) {
        final Map<String, Supplier<TypeTemplate>> map2 = Maps.newHashMap();
        schema.registerSimple((Map)map2, "minecraft:area_effect_cloud");
        a(schema, map2, "minecraft:armor_stand");
        schema.register((Map)map2, "minecraft:arrow", string -> DSL.optionalFields("inBlockState", TypeReferences.BLOCK_STATE.in(schema)));
        a(schema, map2, "minecraft:bat");
        a(schema, map2, "minecraft:blaze");
        schema.registerSimple((Map)map2, "minecraft:boat");
        a(schema, map2, "minecraft:cave_spider");
        schema.register((Map)map2, "minecraft:chest_minecart", string -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))));
        a(schema, map2, "minecraft:chicken");
        schema.register((Map)map2, "minecraft:commandblock_minecart", string -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema)));
        a(schema, map2, "minecraft:cow");
        a(schema, map2, "minecraft:creeper");
        schema.register((Map)map2, "minecraft:donkey", string -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.a(schema)));
        schema.registerSimple((Map)map2, "minecraft:dragon_fireball");
        schema.registerSimple((Map)map2, "minecraft:egg");
        a(schema, map2, "minecraft:elder_guardian");
        schema.registerSimple((Map)map2, "minecraft:ender_crystal");
        a(schema, map2, "minecraft:ender_dragon");
        schema.register((Map)map2, "minecraft:enderman", string -> DSL.optionalFields("carriedBlockState", TypeReferences.BLOCK_STATE.in(schema), Schema100.a(schema)));
        a(schema, map2, "minecraft:endermite");
        schema.registerSimple((Map)map2, "minecraft:ender_pearl");
        schema.registerSimple((Map)map2, "minecraft:evocation_fangs");
        a(schema, map2, "minecraft:evocation_illager");
        schema.registerSimple((Map)map2, "minecraft:eye_of_ender_signal");
        schema.register((Map)map2, "minecraft:falling_block", string -> DSL.optionalFields("BlockState", TypeReferences.BLOCK_STATE.in(schema), "TileEntityData", TypeReferences.BLOCK_ENTITY.in(schema)));
        schema.registerSimple((Map)map2, "minecraft:fireball");
        schema.register((Map)map2, "minecraft:fireworks_rocket", string -> DSL.optionalFields("FireworksItem", TypeReferences.ITEM_STACK.in(schema)));
        schema.register((Map)map2, "minecraft:furnace_minecart", string -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema)));
        a(schema, map2, "minecraft:ghast");
        a(schema, map2, "minecraft:giant");
        a(schema, map2, "minecraft:guardian");
        schema.register((Map)map2, "minecraft:hopper_minecart", string -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))));
        schema.register((Map)map2, "minecraft:horse", string -> DSL.optionalFields("ArmorItem", TypeReferences.ITEM_STACK.in(schema), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.a(schema)));
        a(schema, map2, "minecraft:husk");
        schema.registerSimple((Map)map2, "minecraft:illusion_illager");
        schema.register((Map)map2, "minecraft:item", string -> DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(schema)));
        schema.register((Map)map2, "minecraft:item_frame", string -> DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(schema)));
        schema.registerSimple((Map)map2, "minecraft:leash_knot");
        schema.register((Map)map2, "minecraft:llama", string -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), "DecorItem", TypeReferences.ITEM_STACK.in(schema), Schema100.a(schema)));
        schema.registerSimple((Map)map2, "minecraft:llama_spit");
        a(schema, map2, "minecraft:magma_cube");
        schema.register((Map)map2, "minecraft:minecart", string -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema)));
        a(schema, map2, "minecraft:mooshroom");
        schema.register((Map)map2, "minecraft:mule", string -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.a(schema)));
        a(schema, map2, "minecraft:ocelot");
        schema.registerSimple((Map)map2, "minecraft:painting");
        schema.registerSimple((Map)map2, "minecraft:parrot");
        a(schema, map2, "minecraft:pig");
        a(schema, map2, "minecraft:polar_bear");
        schema.register((Map)map2, "minecraft:potion", string -> DSL.optionalFields("Potion", TypeReferences.ITEM_STACK.in(schema)));
        a(schema, map2, "minecraft:rabbit");
        a(schema, map2, "minecraft:sheep");
        a(schema, map2, "minecraft:shulker");
        schema.registerSimple((Map)map2, "minecraft:shulker_bullet");
        a(schema, map2, "minecraft:silverfish");
        a(schema, map2, "minecraft:skeleton");
        schema.register((Map)map2, "minecraft:skeleton_horse", string -> DSL.optionalFields("SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.a(schema)));
        a(schema, map2, "minecraft:slime");
        schema.registerSimple((Map)map2, "minecraft:small_fireball");
        schema.registerSimple((Map)map2, "minecraft:snowball");
        a(schema, map2, "minecraft:snowman");
        schema.register((Map)map2, "minecraft:spawner_minecart", string -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema), TypeReferences.UNTAGGED_SPAWNER.in(schema)));
        schema.register((Map)map2, "minecraft:spectral_arrow", string -> DSL.optionalFields("inBlockState", TypeReferences.BLOCK_STATE.in(schema)));
        a(schema, map2, "minecraft:spider");
        a(schema, map2, "minecraft:squid");
        a(schema, map2, "minecraft:stray");
        schema.registerSimple((Map)map2, "minecraft:tnt");
        schema.register((Map)map2, "minecraft:tnt_minecart", string -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema)));
        a(schema, map2, "minecraft:vex");
        schema.register((Map)map2, "minecraft:villager", string -> DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", TypeReferences.ITEM_STACK.in(schema), "buyB", TypeReferences.ITEM_STACK.in(schema), "sell", TypeReferences.ITEM_STACK.in(schema)))), Schema100.a(schema)));
        a(schema, map2, "minecraft:villager_golem");
        a(schema, map2, "minecraft:vindication_illager");
        a(schema, map2, "minecraft:witch");
        a(schema, map2, "minecraft:wither");
        a(schema, map2, "minecraft:wither_skeleton");
        schema.registerSimple((Map)map2, "minecraft:wither_skull");
        a(schema, map2, "minecraft:wolf");
        schema.registerSimple((Map)map2, "minecraft:xp_bottle");
        schema.registerSimple((Map)map2, "minecraft:xp_orb");
        a(schema, map2, "minecraft:zombie");
        schema.register((Map)map2, "minecraft:zombie_horse", string -> DSL.optionalFields("SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.a(schema)));
        a(schema, map2, "minecraft:zombie_pigman");
        a(schema, map2, "minecraft:zombie_villager");
        return map2;
    }
    
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(final Schema schema) {
        final Map<String, Supplier<TypeTemplate>> map2 = Maps.newHashMap();
        b(schema, map2, "minecraft:furnace");
        b(schema, map2, "minecraft:chest");
        b(schema, map2, "minecraft:trapped_chest");
        schema.registerSimple((Map)map2, "minecraft:ender_chest");
        schema.register((Map)map2, "minecraft:jukebox", string -> DSL.optionalFields("RecordItem", TypeReferences.ITEM_STACK.in(schema)));
        b(schema, map2, "minecraft:dispenser");
        b(schema, map2, "minecraft:dropper");
        schema.registerSimple((Map)map2, "minecraft:sign");
        schema.register((Map)map2, "minecraft:mob_spawner", string -> TypeReferences.UNTAGGED_SPAWNER.in(schema));
        schema.register((Map)map2, "minecraft:piston", string -> DSL.optionalFields("blockState", TypeReferences.BLOCK_STATE.in(schema)));
        b(schema, map2, "minecraft:brewing_stand");
        schema.registerSimple((Map)map2, "minecraft:enchanting_table");
        schema.registerSimple((Map)map2, "minecraft:end_portal");
        schema.registerSimple((Map)map2, "minecraft:beacon");
        schema.registerSimple((Map)map2, "minecraft:skull");
        schema.registerSimple((Map)map2, "minecraft:daylight_detector");
        b(schema, map2, "minecraft:hopper");
        schema.registerSimple((Map)map2, "minecraft:comparator");
        schema.registerSimple((Map)map2, "minecraft:banner");
        schema.registerSimple((Map)map2, "minecraft:structure_block");
        schema.registerSimple((Map)map2, "minecraft:end_gateway");
        schema.registerSimple((Map)map2, "minecraft:command_block");
        b(schema, map2, "minecraft:shulker_box");
        schema.registerSimple((Map)map2, "minecraft:bed");
        return map2;
    }
    
    public void registerTypes(final Schema schema, final Map<String, Supplier<TypeTemplate>> map2, final Map<String, Supplier<TypeTemplate>> map3) {
        schema.registerType(false, TypeReferences.LEVEL, (Supplier)DSL::remainder);
        schema.registerType(false, TypeReferences.RECIPE, () -> DSL.constType(DSL.namespacedString()));
        schema.registerType(false, TypeReferences.PLAYER, () -> DSL.optionalFields("RootVehicle", DSL.optionalFields("Entity", TypeReferences.ENTITY_TREE.in(schema)), "Inventory", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "EnderItems", DSL.list(TypeReferences.ITEM_STACK.in(schema)), DSL.optionalFields("ShoulderEntityLeft", TypeReferences.ENTITY_TREE.in(schema), "ShoulderEntityRight", TypeReferences.ENTITY_TREE.in(schema), "recipeBook", DSL.optionalFields("recipes", DSL.list(TypeReferences.RECIPE.in(schema)), "toBeDisplayed", DSL.list(TypeReferences.RECIPE.in(schema))))));
        schema.registerType(false, TypeReferences.CHUNK, () -> DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(TypeReferences.ENTITY_TREE.in(schema)), "TileEntities", DSL.list(TypeReferences.BLOCK_ENTITY.in(schema)), "TileTicks", DSL.list(DSL.fields("i", TypeReferences.BLOCK_NAME.in(schema))), "Sections", DSL.list(DSL.optionalFields("Palette", DSL.list(TypeReferences.BLOCK_STATE.in(schema)))))));
        schema.registerType(true, TypeReferences.BLOCK_ENTITY, () -> DSL.taggedChoiceLazy("id", DSL.namespacedString(), (Map)map3));
        schema.registerType(true, TypeReferences.ENTITY_TREE, () -> DSL.optionalFields("Passengers", DSL.list(TypeReferences.ENTITY_TREE.in(schema)), TypeReferences.ENTITY.in(schema)));
        schema.registerType(true, TypeReferences.ENTITY, () -> DSL.taggedChoiceLazy("id", DSL.namespacedString(), (Map)map2));
        schema.registerType(true, TypeReferences.ITEM_STACK, () -> DSL.hook(DSL.optionalFields("id", TypeReferences.ITEM_NAME.in(schema), "tag", DSL.optionalFields("EntityTag", TypeReferences.ENTITY_TREE.in(schema), "BlockEntityTag", TypeReferences.BLOCK_ENTITY.in(schema), "CanDestroy", DSL.list(TypeReferences.BLOCK_NAME.in(schema)), "CanPlaceOn", DSL.list(TypeReferences.BLOCK_NAME.in(schema)))), Schema705.a, Hook.HookFunction.IDENTITY));
        schema.registerType(false, TypeReferences.HOTBAR, () -> DSL.compoundList(DSL.list(TypeReferences.ITEM_STACK.in(schema))));
        schema.registerType(false, TypeReferences.OPTIONS, (Supplier)DSL::remainder);
        schema.registerType(false, TypeReferences.STRUCTURE, () -> DSL.optionalFields("entities", DSL.list(DSL.optionalFields("nbt", TypeReferences.ENTITY_TREE.in(schema))), "blocks", DSL.list(DSL.optionalFields("nbt", TypeReferences.BLOCK_ENTITY.in(schema))), "palette", DSL.list(TypeReferences.BLOCK_STATE.in(schema))));
        schema.registerType(false, TypeReferences.BLOCK_NAME, () -> DSL.constType(DSL.namespacedString()));
        schema.registerType(false, TypeReferences.ITEM_NAME, () -> DSL.constType(DSL.namespacedString()));
        schema.registerType(false, TypeReferences.BLOCK_STATE, (Supplier)DSL::remainder);
        final Supplier<TypeTemplate> supplier4 = () -> DSL.compoundList(TypeReferences.ITEM_NAME.in(schema), DSL.constType(DSL.intType()));
        final Supplier<TypeTemplate> supplier5;
        schema.registerType(false, TypeReferences.STATS, () -> DSL.optionalFields("stats", DSL.optionalFields("minecraft:mined", DSL.compoundList(TypeReferences.BLOCK_NAME.in(schema), DSL.constType(DSL.intType())), "minecraft:crafted", (TypeTemplate)supplier5.get(), "minecraft:used", (TypeTemplate)supplier5.get(), "minecraft:broken", (TypeTemplate)supplier5.get(), "minecraft:picked_up", (TypeTemplate)supplier5.get(), DSL.optionalFields("minecraft:dropped", (TypeTemplate)supplier5.get(), "minecraft:killed", DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema), DSL.constType(DSL.intType())), "minecraft:killed_by", DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema), DSL.constType(DSL.intType())), "minecraft:custom", DSL.compoundList(DSL.constType(DSL.namespacedString()), DSL.constType(DSL.intType()))))));
        schema.registerType(false, TypeReferences.SAVED_DATA, () -> DSL.optionalFields("data", DSL.optionalFields("Features", DSL.compoundList(TypeReferences.STRUCTURE_FEATURE.in(schema)), "Objectives", DSL.list(TypeReferences.OBJECTIVE.in(schema)), "Teams", DSL.list(TypeReferences.TEAM.in(schema)))));
        schema.registerType(false, TypeReferences.STRUCTURE_FEATURE, () -> DSL.optionalFields("Children", DSL.list(DSL.optionalFields("CA", TypeReferences.BLOCK_STATE.in(schema), "CB", TypeReferences.BLOCK_STATE.in(schema), "CC", TypeReferences.BLOCK_STATE.in(schema), "CD", TypeReferences.BLOCK_STATE.in(schema)))));
        schema.registerType(false, TypeReferences.OBJECTIVE, (Supplier)DSL::remainder);
        schema.registerType(false, TypeReferences.TEAM, (Supplier)DSL::remainder);
        schema.registerType(true, TypeReferences.UNTAGGED_SPAWNER, () -> DSL.optionalFields("SpawnPotentials", DSL.list(DSL.fields("Entity", TypeReferences.ENTITY_TREE.in(schema))), "SpawnData", TypeReferences.ENTITY_TREE.in(schema)));
        schema.registerType(false, TypeReferences.ADVANCEMENTS, () -> DSL.optionalFields("minecraft:adventure/adventuring_time", DSL.optionalFields("criteria", DSL.compoundList(TypeReferences.BIOME.in(schema), DSL.constType(DSL.string()))), "minecraft:adventure/kill_a_mob", DSL.optionalFields("criteria", DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema), DSL.constType(DSL.string()))), "minecraft:adventure/kill_all_mobs", DSL.optionalFields("criteria", DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema), DSL.constType(DSL.string()))), "minecraft:husbandry/bred_all_animals", DSL.optionalFields("criteria", DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema), DSL.constType(DSL.string())))));
        schema.registerType(false, TypeReferences.BIOME, () -> DSL.constType(DSL.namespacedString()));
        schema.registerType(false, TypeReferences.ENTITY_NAME, () -> DSL.constType(DSL.namespacedString()));
        schema.registerType(false, TypeReferences.POI_CHUNK, (Supplier)DSL::remainder);
    }
}
