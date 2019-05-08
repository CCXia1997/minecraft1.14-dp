package net.minecraft.datafixers.schemas;

import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.DataFixUtils;
import org.apache.logging.log4j.LogManager;
import java.util.HashMap;
import java.util.Objects;
import com.mojang.datafixers.Dynamic;
import com.google.common.collect.Maps;
import java.util.function.Supplier;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import com.mojang.datafixers.schemas.Schema;

public class Schema99 extends Schema
{
    private static final Logger b;
    private static final Map<String, String> c;
    protected static final Hook.HookFunction a;
    
    public Schema99(final int integer, final Schema schema) {
        super(integer, schema);
    }
    
    protected static TypeTemplate a(final Schema schema) {
        return DSL.optionalFields("Equipment", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
    }
    
    protected static void a(final Schema schema, final Map<String, Supplier<TypeTemplate>> map, final String string) {
        schema.register((Map)map, string, () -> a(schema));
    }
    
    protected static void b(final Schema schema, final Map<String, Supplier<TypeTemplate>> map, final String string) {
        schema.register((Map)map, string, () -> DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema)));
    }
    
    protected static void c(final Schema schema, final Map<String, Supplier<TypeTemplate>> map, final String string) {
        schema.register((Map)map, string, () -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema)));
    }
    
    protected static void d(final Schema schema, final Map<String, Supplier<TypeTemplate>> map, final String string) {
        schema.register((Map)map, string, () -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))));
    }
    
    public Map<String, Supplier<TypeTemplate>> registerEntities(final Schema schema) {
        final Map<String, Supplier<TypeTemplate>> map2 = Maps.newHashMap();
        schema.register((Map)map2, "Item", string -> DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(schema)));
        schema.registerSimple((Map)map2, "XPOrb");
        b(schema, map2, "ThrownEgg");
        schema.registerSimple((Map)map2, "LeashKnot");
        schema.registerSimple((Map)map2, "Painting");
        schema.register((Map)map2, "Arrow", string -> DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema)));
        schema.register((Map)map2, "TippedArrow", string -> DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema)));
        schema.register((Map)map2, "SpectralArrow", string -> DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema)));
        b(schema, map2, "Snowball");
        b(schema, map2, "Fireball");
        b(schema, map2, "SmallFireball");
        b(schema, map2, "ThrownEnderpearl");
        schema.registerSimple((Map)map2, "EyeOfEnderSignal");
        schema.register((Map)map2, "ThrownPotion", string -> DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema), "Potion", TypeReferences.ITEM_STACK.in(schema)));
        b(schema, map2, "ThrownExpBottle");
        schema.register((Map)map2, "ItemFrame", string -> DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(schema)));
        b(schema, map2, "WitherSkull");
        schema.registerSimple((Map)map2, "PrimedTnt");
        schema.register((Map)map2, "FallingSand", string -> DSL.optionalFields("Block", TypeReferences.BLOCK_NAME.in(schema), "TileEntityData", TypeReferences.BLOCK_ENTITY.in(schema)));
        schema.register((Map)map2, "FireworksRocketEntity", string -> DSL.optionalFields("FireworksItem", TypeReferences.ITEM_STACK.in(schema)));
        schema.registerSimple((Map)map2, "Boat");
        schema.register((Map)map2, "Minecart", () -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))));
        c(schema, map2, "MinecartRideable");
        schema.register((Map)map2, "MinecartChest", string -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))));
        c(schema, map2, "MinecartFurnace");
        c(schema, map2, "MinecartTNT");
        schema.register((Map)map2, "MinecartSpawner", () -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema), TypeReferences.UNTAGGED_SPAWNER.in(schema)));
        schema.register((Map)map2, "MinecartHopper", string -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))));
        c(schema, map2, "MinecartCommandBlock");
        a(schema, map2, "ArmorStand");
        a(schema, map2, "Creeper");
        a(schema, map2, "Skeleton");
        a(schema, map2, "Spider");
        a(schema, map2, "Giant");
        a(schema, map2, "Zombie");
        a(schema, map2, "Slime");
        a(schema, map2, "Ghast");
        a(schema, map2, "PigZombie");
        schema.register((Map)map2, "Enderman", string -> DSL.optionalFields("carried", TypeReferences.BLOCK_NAME.in(schema), a(schema)));
        a(schema, map2, "CaveSpider");
        a(schema, map2, "Silverfish");
        a(schema, map2, "Blaze");
        a(schema, map2, "LavaSlime");
        a(schema, map2, "EnderDragon");
        a(schema, map2, "WitherBoss");
        a(schema, map2, "Bat");
        a(schema, map2, "Witch");
        a(schema, map2, "Endermite");
        a(schema, map2, "Guardian");
        a(schema, map2, "Pig");
        a(schema, map2, "Sheep");
        a(schema, map2, "Cow");
        a(schema, map2, "Chicken");
        a(schema, map2, "Squid");
        a(schema, map2, "Wolf");
        a(schema, map2, "MushroomCow");
        a(schema, map2, "SnowMan");
        a(schema, map2, "Ozelot");
        a(schema, map2, "VillagerGolem");
        schema.register((Map)map2, "EntityHorse", string -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "ArmorItem", TypeReferences.ITEM_STACK.in(schema), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), a(schema)));
        a(schema, map2, "Rabbit");
        schema.register((Map)map2, "Villager", string -> DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", TypeReferences.ITEM_STACK.in(schema), "buyB", TypeReferences.ITEM_STACK.in(schema), "sell", TypeReferences.ITEM_STACK.in(schema)))), a(schema)));
        schema.registerSimple((Map)map2, "EnderCrystal");
        schema.registerSimple((Map)map2, "AreaEffectCloud");
        schema.registerSimple((Map)map2, "ShulkerBullet");
        a(schema, map2, "Shulker");
        return map2;
    }
    
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(final Schema schema) {
        final Map<String, Supplier<TypeTemplate>> map2 = Maps.newHashMap();
        d(schema, map2, "Furnace");
        d(schema, map2, "Chest");
        schema.registerSimple((Map)map2, "EnderChest");
        schema.register((Map)map2, "RecordPlayer", string -> DSL.optionalFields("RecordItem", TypeReferences.ITEM_STACK.in(schema)));
        d(schema, map2, "Trap");
        d(schema, map2, "Dropper");
        schema.registerSimple((Map)map2, "Sign");
        schema.register((Map)map2, "MobSpawner", string -> TypeReferences.UNTAGGED_SPAWNER.in(schema));
        schema.registerSimple((Map)map2, "Music");
        schema.registerSimple((Map)map2, "Piston");
        d(schema, map2, "Cauldron");
        schema.registerSimple((Map)map2, "EnchantTable");
        schema.registerSimple((Map)map2, "Airportal");
        schema.registerSimple((Map)map2, "Control");
        schema.registerSimple((Map)map2, "Beacon");
        schema.registerSimple((Map)map2, "Skull");
        schema.registerSimple((Map)map2, "DLDetector");
        d(schema, map2, "Hopper");
        schema.registerSimple((Map)map2, "Comparator");
        schema.register((Map)map2, "FlowerPot", string -> DSL.optionalFields("Item", DSL.or(DSL.constType(DSL.intType()), TypeReferences.ITEM_NAME.in(schema))));
        schema.registerSimple((Map)map2, "Banner");
        schema.registerSimple((Map)map2, "Structure");
        schema.registerSimple((Map)map2, "EndGateway");
        return map2;
    }
    
    public void registerTypes(final Schema schema, final Map<String, Supplier<TypeTemplate>> map2, final Map<String, Supplier<TypeTemplate>> map3) {
        schema.registerType(false, TypeReferences.LEVEL, (Supplier)DSL::remainder);
        schema.registerType(false, TypeReferences.PLAYER, () -> DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "EnderItems", DSL.list(TypeReferences.ITEM_STACK.in(schema))));
        schema.registerType(false, TypeReferences.CHUNK, () -> DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(TypeReferences.ENTITY_TREE.in(schema)), "TileEntities", DSL.list(TypeReferences.BLOCK_ENTITY.in(schema)), "TileTicks", DSL.list(DSL.fields("i", TypeReferences.BLOCK_NAME.in(schema))))));
        schema.registerType(true, TypeReferences.BLOCK_ENTITY, () -> DSL.taggedChoiceLazy("id", DSL.string(), (Map)map3));
        schema.registerType(true, TypeReferences.ENTITY_TREE, () -> DSL.optionalFields("Riding", TypeReferences.ENTITY_TREE.in(schema), TypeReferences.ENTITY.in(schema)));
        schema.registerType(false, TypeReferences.ENTITY_NAME, () -> DSL.constType(DSL.namespacedString()));
        schema.registerType(true, TypeReferences.ENTITY, () -> DSL.taggedChoiceLazy("id", DSL.string(), (Map)map2));
        schema.registerType(true, TypeReferences.ITEM_STACK, () -> DSL.hook(DSL.optionalFields("id", DSL.or(DSL.constType(DSL.intType()), TypeReferences.ITEM_NAME.in(schema)), "tag", DSL.optionalFields("EntityTag", TypeReferences.ENTITY_TREE.in(schema), "BlockEntityTag", TypeReferences.BLOCK_ENTITY.in(schema), "CanDestroy", DSL.list(TypeReferences.BLOCK_NAME.in(schema)), "CanPlaceOn", DSL.list(TypeReferences.BLOCK_NAME.in(schema)))), Schema99.a, Hook.HookFunction.IDENTITY));
        schema.registerType(false, TypeReferences.OPTIONS, (Supplier)DSL::remainder);
        schema.registerType(false, TypeReferences.BLOCK_NAME, () -> DSL.or(DSL.constType(DSL.intType()), DSL.constType(DSL.namespacedString())));
        schema.registerType(false, TypeReferences.ITEM_NAME, () -> DSL.constType(DSL.namespacedString()));
        schema.registerType(false, TypeReferences.STATS, (Supplier)DSL::remainder);
        schema.registerType(false, TypeReferences.SAVED_DATA, () -> DSL.optionalFields("data", DSL.optionalFields("Features", DSL.compoundList(TypeReferences.STRUCTURE_FEATURE.in(schema)), "Objectives", DSL.list(TypeReferences.OBJECTIVE.in(schema)), "Teams", DSL.list(TypeReferences.TEAM.in(schema)))));
        schema.registerType(false, TypeReferences.STRUCTURE_FEATURE, (Supplier)DSL::remainder);
        schema.registerType(false, TypeReferences.OBJECTIVE, (Supplier)DSL::remainder);
        schema.registerType(false, TypeReferences.TEAM, (Supplier)DSL::remainder);
        schema.registerType(true, TypeReferences.UNTAGGED_SPAWNER, (Supplier)DSL::remainder);
        schema.registerType(false, TypeReferences.POI_CHUNK, (Supplier)DSL::remainder);
    }
    
    protected static <T> T a(final Dynamic<T> dynamic, final Map<String, String> map, final String string) {
        final String string2;
        final String string3;
        final String string4;
        return (T)dynamic.update("tag", dynamic4 -> dynamic4.update("BlockEntityTag", dynamic3 -> {
            string2 = dynamic.get("id").asString("");
            string3 = map.get(SchemaIdentifierNormalize.normalize(string2));
            if (string3 == null) {
                Schema99.b.warn("Unable to resolve BlockEntity for ItemStack: {}", string2);
                return dynamic3;
            }
            else {
                return dynamic3.set("id", dynamic.createString(string3));
            }
        }).update("EntityTag", dynamic3 -> {
            string4 = dynamic.get("id").asString("");
            if (Objects.equals(SchemaIdentifierNormalize.normalize(string4), "minecraft:armor_stand")) {
                return dynamic3.set("id", dynamic.createString(string));
            }
            else {
                return dynamic3;
            }
        })).getValue();
    }
    
    static {
        b = LogManager.getLogger();
        c = (Map)DataFixUtils.make(Maps.newHashMap(), hashMap -> {
            hashMap.put("minecraft:furnace", "Furnace");
            hashMap.put("minecraft:lit_furnace", "Furnace");
            hashMap.put("minecraft:chest", "Chest");
            hashMap.put("minecraft:trapped_chest", "Chest");
            hashMap.put("minecraft:ender_chest", "EnderChest");
            hashMap.put("minecraft:jukebox", "RecordPlayer");
            hashMap.put("minecraft:dispenser", "Trap");
            hashMap.put("minecraft:dropper", "Dropper");
            hashMap.put("minecraft:sign", "Sign");
            hashMap.put("minecraft:mob_spawner", "MobSpawner");
            hashMap.put("minecraft:noteblock", "Music");
            hashMap.put("minecraft:brewing_stand", "Cauldron");
            hashMap.put("minecraft:enhanting_table", "EnchantTable");
            hashMap.put("minecraft:command_block", "CommandBlock");
            hashMap.put("minecraft:beacon", "Beacon");
            hashMap.put("minecraft:skull", "Skull");
            hashMap.put("minecraft:daylight_detector", "DLDetector");
            hashMap.put("minecraft:hopper", "Hopper");
            hashMap.put("minecraft:banner", "Banner");
            hashMap.put("minecraft:flower_pot", "FlowerPot");
            hashMap.put("minecraft:repeating_command_block", "CommandBlock");
            hashMap.put("minecraft:chain_command_block", "CommandBlock");
            hashMap.put("minecraft:standing_sign", "Sign");
            hashMap.put("minecraft:wall_sign", "Sign");
            hashMap.put("minecraft:piston_head", "Piston");
            hashMap.put("minecraft:daylight_detector_inverted", "DLDetector");
            hashMap.put("minecraft:unpowered_comparator", "Comparator");
            hashMap.put("minecraft:powered_comparator", "Comparator");
            hashMap.put("minecraft:wall_banner", "Banner");
            hashMap.put("minecraft:standing_banner", "Banner");
            hashMap.put("minecraft:structure_block", "Structure");
            hashMap.put("minecraft:end_portal", "Airportal");
            hashMap.put("minecraft:end_gateway", "EndGateway");
            hashMap.put("minecraft:shield", "Banner");
            return;
        });
        a = (Hook.HookFunction)new Hook.HookFunction() {
            public <T> T apply(final DynamicOps<T> dynamicOps, final T object) {
                return Schema99.<T>a((com.mojang.datafixers.Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, object), Schema99.c, "ArmorStand");
            }
        };
    }
}
