package net.minecraft.datafixers.schemas;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.DataFixUtils;
import java.util.HashMap;
import com.google.common.collect.Maps;
import java.util.Objects;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.function.Supplier;
import com.mojang.datafixers.types.templates.Hook;
import java.util.Map;
import com.mojang.datafixers.schemas.Schema;

public class Schema704 extends Schema
{
    protected static final Map<String, String> a;
    protected static final Hook.HookFunction b;
    
    public Schema704(final int integer, final Schema schema) {
        super(integer, schema);
    }
    
    protected static void a(final Schema schema, final Map<String, Supplier<TypeTemplate>> map, final String string) {
        schema.register((Map)map, string, () -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))));
    }
    
    public Type<?> getChoiceType(final DSL.TypeReference typeReference, final String string) {
        if (Objects.equals(typeReference.typeName(), TypeReferences.BLOCK_ENTITY.typeName())) {
            return super.getChoiceType(typeReference, SchemaIdentifierNormalize.normalize(string));
        }
        return super.getChoiceType(typeReference, string);
    }
    
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(final Schema schema) {
        final Map<String, Supplier<TypeTemplate>> map2 = Maps.newHashMap();
        a(schema, map2, "minecraft:furnace");
        a(schema, map2, "minecraft:chest");
        schema.registerSimple((Map)map2, "minecraft:ender_chest");
        schema.register((Map)map2, "minecraft:jukebox", string -> DSL.optionalFields("RecordItem", TypeReferences.ITEM_STACK.in(schema)));
        a(schema, map2, "minecraft:dispenser");
        a(schema, map2, "minecraft:dropper");
        schema.registerSimple((Map)map2, "minecraft:sign");
        schema.register((Map)map2, "minecraft:mob_spawner", string -> TypeReferences.UNTAGGED_SPAWNER.in(schema));
        schema.registerSimple((Map)map2, "minecraft:noteblock");
        schema.registerSimple((Map)map2, "minecraft:piston");
        a(schema, map2, "minecraft:brewing_stand");
        schema.registerSimple((Map)map2, "minecraft:enchanting_table");
        schema.registerSimple((Map)map2, "minecraft:end_portal");
        schema.registerSimple((Map)map2, "minecraft:beacon");
        schema.registerSimple((Map)map2, "minecraft:skull");
        schema.registerSimple((Map)map2, "minecraft:daylight_detector");
        a(schema, map2, "minecraft:hopper");
        schema.registerSimple((Map)map2, "minecraft:comparator");
        schema.register((Map)map2, "minecraft:flower_pot", string -> DSL.optionalFields("Item", DSL.or(DSL.constType(DSL.intType()), TypeReferences.ITEM_NAME.in(schema))));
        schema.registerSimple((Map)map2, "minecraft:banner");
        schema.registerSimple((Map)map2, "minecraft:structure_block");
        schema.registerSimple((Map)map2, "minecraft:end_gateway");
        schema.registerSimple((Map)map2, "minecraft:command_block");
        return map2;
    }
    
    public void registerTypes(final Schema schema, final Map<String, Supplier<TypeTemplate>> map2, final Map<String, Supplier<TypeTemplate>> map3) {
        super.registerTypes(schema, (Map)map2, (Map)map3);
        schema.registerType(false, TypeReferences.BLOCK_ENTITY, () -> DSL.taggedChoiceLazy("id", DSL.namespacedString(), (Map)map3));
        schema.registerType(true, TypeReferences.ITEM_STACK, () -> DSL.hook(DSL.optionalFields("id", TypeReferences.ITEM_NAME.in(schema), "tag", DSL.optionalFields("EntityTag", TypeReferences.ENTITY_TREE.in(schema), "BlockEntityTag", TypeReferences.BLOCK_ENTITY.in(schema), "CanDestroy", DSL.list(TypeReferences.BLOCK_NAME.in(schema)), "CanPlaceOn", DSL.list(TypeReferences.BLOCK_NAME.in(schema)))), Schema704.b, Hook.HookFunction.IDENTITY));
    }
    
    static {
        a = (Map)DataFixUtils.make(Maps.newHashMap(), hashMap -> {
            hashMap.put("minecraft:furnace", "minecraft:furnace");
            hashMap.put("minecraft:lit_furnace", "minecraft:furnace");
            hashMap.put("minecraft:chest", "minecraft:chest");
            hashMap.put("minecraft:trapped_chest", "minecraft:chest");
            hashMap.put("minecraft:ender_chest", "minecraft:ender_chest");
            hashMap.put("minecraft:jukebox", "minecraft:jukebox");
            hashMap.put("minecraft:dispenser", "minecraft:dispenser");
            hashMap.put("minecraft:dropper", "minecraft:dropper");
            hashMap.put("minecraft:sign", "minecraft:sign");
            hashMap.put("minecraft:mob_spawner", "minecraft:mob_spawner");
            hashMap.put("minecraft:noteblock", "minecraft:noteblock");
            hashMap.put("minecraft:brewing_stand", "minecraft:brewing_stand");
            hashMap.put("minecraft:enhanting_table", "minecraft:enchanting_table");
            hashMap.put("minecraft:command_block", "minecraft:command_block");
            hashMap.put("minecraft:beacon", "minecraft:beacon");
            hashMap.put("minecraft:skull", "minecraft:skull");
            hashMap.put("minecraft:daylight_detector", "minecraft:daylight_detector");
            hashMap.put("minecraft:hopper", "minecraft:hopper");
            hashMap.put("minecraft:banner", "minecraft:banner");
            hashMap.put("minecraft:flower_pot", "minecraft:flower_pot");
            hashMap.put("minecraft:repeating_command_block", "minecraft:command_block");
            hashMap.put("minecraft:chain_command_block", "minecraft:command_block");
            hashMap.put("minecraft:shulker_box", "minecraft:shulker_box");
            hashMap.put("minecraft:white_shulker_box", "minecraft:shulker_box");
            hashMap.put("minecraft:orange_shulker_box", "minecraft:shulker_box");
            hashMap.put("minecraft:magenta_shulker_box", "minecraft:shulker_box");
            hashMap.put("minecraft:light_blue_shulker_box", "minecraft:shulker_box");
            hashMap.put("minecraft:yellow_shulker_box", "minecraft:shulker_box");
            hashMap.put("minecraft:lime_shulker_box", "minecraft:shulker_box");
            hashMap.put("minecraft:pink_shulker_box", "minecraft:shulker_box");
            hashMap.put("minecraft:gray_shulker_box", "minecraft:shulker_box");
            hashMap.put("minecraft:silver_shulker_box", "minecraft:shulker_box");
            hashMap.put("minecraft:cyan_shulker_box", "minecraft:shulker_box");
            hashMap.put("minecraft:purple_shulker_box", "minecraft:shulker_box");
            hashMap.put("minecraft:blue_shulker_box", "minecraft:shulker_box");
            hashMap.put("minecraft:brown_shulker_box", "minecraft:shulker_box");
            hashMap.put("minecraft:green_shulker_box", "minecraft:shulker_box");
            hashMap.put("minecraft:red_shulker_box", "minecraft:shulker_box");
            hashMap.put("minecraft:black_shulker_box", "minecraft:shulker_box");
            hashMap.put("minecraft:bed", "minecraft:bed");
            hashMap.put("minecraft:light_gray_shulker_box", "minecraft:shulker_box");
            hashMap.put("minecraft:banner", "minecraft:banner");
            hashMap.put("minecraft:white_banner", "minecraft:banner");
            hashMap.put("minecraft:orange_banner", "minecraft:banner");
            hashMap.put("minecraft:magenta_banner", "minecraft:banner");
            hashMap.put("minecraft:light_blue_banner", "minecraft:banner");
            hashMap.put("minecraft:yellow_banner", "minecraft:banner");
            hashMap.put("minecraft:lime_banner", "minecraft:banner");
            hashMap.put("minecraft:pink_banner", "minecraft:banner");
            hashMap.put("minecraft:gray_banner", "minecraft:banner");
            hashMap.put("minecraft:silver_banner", "minecraft:banner");
            hashMap.put("minecraft:cyan_banner", "minecraft:banner");
            hashMap.put("minecraft:purple_banner", "minecraft:banner");
            hashMap.put("minecraft:blue_banner", "minecraft:banner");
            hashMap.put("minecraft:brown_banner", "minecraft:banner");
            hashMap.put("minecraft:green_banner", "minecraft:banner");
            hashMap.put("minecraft:red_banner", "minecraft:banner");
            hashMap.put("minecraft:black_banner", "minecraft:banner");
            hashMap.put("minecraft:standing_sign", "minecraft:sign");
            hashMap.put("minecraft:wall_sign", "minecraft:sign");
            hashMap.put("minecraft:piston_head", "minecraft:piston");
            hashMap.put("minecraft:daylight_detector_inverted", "minecraft:daylight_detector");
            hashMap.put("minecraft:unpowered_comparator", "minecraft:comparator");
            hashMap.put("minecraft:powered_comparator", "minecraft:comparator");
            hashMap.put("minecraft:wall_banner", "minecraft:banner");
            hashMap.put("minecraft:standing_banner", "minecraft:banner");
            hashMap.put("minecraft:structure_block", "minecraft:structure_block");
            hashMap.put("minecraft:end_portal", "minecraft:end_portal");
            hashMap.put("minecraft:end_gateway", "minecraft:end_gateway");
            hashMap.put("minecraft:sign", "minecraft:sign");
            hashMap.put("minecraft:shield", "minecraft:banner");
            return;
        });
        b = (Hook.HookFunction)new Hook.HookFunction() {
            public <T> T apply(final DynamicOps<T> dynamicOps, final T object) {
                return Schema99.<T>a((com.mojang.datafixers.Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, object), Schema704.a, "ArmorStand");
            }
        };
    }
}
