package net.minecraft.datafixers.schemas;

import java.util.function.Supplier;
import java.util.Map;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.schemas.Schema;

public class Schema100 extends Schema
{
    public Schema100(final int integer, final Schema schema) {
        super(integer, schema);
    }
    
    protected static TypeTemplate a(final Schema schema) {
        return DSL.optionalFields("ArmorItems", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "HandItems", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
    }
    
    protected static void a(final Schema schema, final Map<String, Supplier<TypeTemplate>> map, final String string) {
        schema.register((Map)map, string, () -> a(schema));
    }
    
    public Map<String, Supplier<TypeTemplate>> registerEntities(final Schema schema) {
        final Map<String, Supplier<TypeTemplate>> map2 = (Map<String, Supplier<TypeTemplate>>)super.registerEntities(schema);
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
        a(schema, map2, "Shulker");
        schema.registerSimple((Map)map2, "AreaEffectCloud");
        schema.registerSimple((Map)map2, "ShulkerBullet");
        return map2;
    }
    
    public void registerTypes(final Schema schema, final Map<String, Supplier<TypeTemplate>> map2, final Map<String, Supplier<TypeTemplate>> map3) {
        super.registerTypes(schema, (Map)map2, (Map)map3);
        schema.registerType(false, TypeReferences.STRUCTURE, () -> DSL.optionalFields("entities", DSL.list(DSL.optionalFields("nbt", TypeReferences.ENTITY_TREE.in(schema))), "blocks", DSL.list(DSL.optionalFields("nbt", TypeReferences.BLOCK_ENTITY.in(schema))), "palette", DSL.list(TypeReferences.BLOCK_STATE.in(schema))));
        schema.registerType(false, TypeReferences.BLOCK_STATE, (Supplier)DSL::remainder);
    }
}
