package net.minecraft.world.loot;

import java.util.Collections;
import com.google.common.collect.Sets;
import net.minecraft.util.Identifier;
import java.util.Set;

public class LootTables
{
    private static final Set<Identifier> LOOT_TABLES;
    private static final Set<Identifier> LOOT_TABLES_READ_ONLY;
    public static final Identifier EMPTY;
    public static final Identifier CHEST_SPAWN_BONUS;
    public static final Identifier CHEST_END_CITY_TREASURE;
    public static final Identifier CHEST_SIMPLE_DUNGEON;
    public static final Identifier CHEST_VILLAGE_BLACKSMITH;
    public static final Identifier f;
    public static final Identifier g;
    public static final Identifier h;
    public static final Identifier i;
    public static final Identifier j;
    public static final Identifier k;
    public static final Identifier l;
    public static final Identifier m;
    public static final Identifier n;
    public static final Identifier o;
    public static final Identifier p;
    public static final Identifier q;
    public static final Identifier r;
    public static final Identifier s;
    public static final Identifier t;
    public static final Identifier CHEST_ABANDONED_MINESHAFT;
    public static final Identifier CHEST_NETHER_BRIDGE;
    public static final Identifier CHEST_STRONGHOLD_LIBRARY;
    public static final Identifier CHEST_STRONGHOLD_CROSSING;
    public static final Identifier CHEST_STRONGHOLD_CORRIDOR;
    public static final Identifier CHEST_DESERT_PYRAMID;
    public static final Identifier CHEST_JUNGLE_TEMPLE;
    public static final Identifier DISPENSER_JUNGLE_TEMPLE;
    public static final Identifier CHEST_IGLOO;
    public static final Identifier CHEST_WOODLAND_MANSION;
    public static final Identifier E;
    public static final Identifier F;
    public static final Identifier G;
    public static final Identifier H;
    public static final Identifier I;
    public static final Identifier J;
    public static final Identifier K;
    public static final Identifier ENTITY_SHEEP_WHITE;
    public static final Identifier ENTITY_SHEEP_ORANGE;
    public static final Identifier ENTITY_SHEEP_MAGENTA;
    public static final Identifier ENTITY_SHEEP_LIGHT_BLUE;
    public static final Identifier ENTITY_SHEEP_YELLOW;
    public static final Identifier ENTITY_SHEEP_LIME;
    public static final Identifier ENTITY_SHEEP_PINK;
    public static final Identifier ENTITY_SHEEP_GRAY;
    public static final Identifier ENTITY_SHEEP_LIGHT_GRAY;
    public static final Identifier ENTITY_SHEEP_CYAN;
    public static final Identifier ENTITY_SHEEP_PURPLE;
    public static final Identifier ENTITY_SHEEP_BLUE;
    public static final Identifier ENTITY_SHEEP_BROWN;
    public static final Identifier ENTITY_SHEEP_GREEN;
    public static final Identifier ENTITY_SHEEP_RED;
    public static final Identifier ENTITY_SHEEP_BLACK;
    public static final Identifier GAMEPLAY_FISHING;
    public static final Identifier GAMEPLAY_FISHING_JUNK;
    public static final Identifier GAMEPLAY_FISHING_TREASURE;
    public static final Identifier GAMEPLAY_FISHING_FISH;
    public static final Identifier GAMEPLAY_CAT_MORNING_GIFT;
    public static final Identifier ag;
    public static final Identifier ah;
    public static final Identifier ai;
    public static final Identifier aj;
    public static final Identifier ak;
    public static final Identifier al;
    public static final Identifier am;
    public static final Identifier an;
    public static final Identifier ao;
    public static final Identifier ap;
    public static final Identifier aq;
    public static final Identifier ar;
    public static final Identifier as;
    
    private static Identifier registerLootTable(final String id) {
        return registerLootTable(new Identifier(id));
    }
    
    private static Identifier registerLootTable(final Identifier id) {
        if (LootTables.LOOT_TABLES.add(id)) {
            return id;
        }
        throw new IllegalArgumentException(id + " is already a registered built-in loot table");
    }
    
    public static Set<Identifier> getAll() {
        return LootTables.LOOT_TABLES_READ_ONLY;
    }
    
    static {
        LOOT_TABLES = Sets.newHashSet();
        LOOT_TABLES_READ_ONLY = Collections.<Identifier>unmodifiableSet(LootTables.LOOT_TABLES);
        EMPTY = new Identifier("empty");
        CHEST_SPAWN_BONUS = registerLootTable("chests/spawn_bonus_chest");
        CHEST_END_CITY_TREASURE = registerLootTable("chests/end_city_treasure");
        CHEST_SIMPLE_DUNGEON = registerLootTable("chests/simple_dungeon");
        CHEST_VILLAGE_BLACKSMITH = registerLootTable("chests/village/village_weaponsmith");
        f = registerLootTable("chests/village/village_toolsmith");
        g = registerLootTable("chests/village/village_armorer");
        h = registerLootTable("chests/village/village_cartographer");
        i = registerLootTable("chests/village/village_mason");
        j = registerLootTable("chests/village/village_shepherd");
        k = registerLootTable("chests/village/village_butcher");
        l = registerLootTable("chests/village/village_fletcher");
        m = registerLootTable("chests/village/village_fisher");
        n = registerLootTable("chests/village/village_tannery");
        o = registerLootTable("chests/village/village_temple");
        p = registerLootTable("chests/village/village_desert_house");
        q = registerLootTable("chests/village/village_plains_house");
        r = registerLootTable("chests/village/village_taiga_house");
        s = registerLootTable("chests/village/village_snowy_house");
        t = registerLootTable("chests/village/village_savanna_house");
        CHEST_ABANDONED_MINESHAFT = registerLootTable("chests/abandoned_mineshaft");
        CHEST_NETHER_BRIDGE = registerLootTable("chests/nether_bridge");
        CHEST_STRONGHOLD_LIBRARY = registerLootTable("chests/stronghold_library");
        CHEST_STRONGHOLD_CROSSING = registerLootTable("chests/stronghold_crossing");
        CHEST_STRONGHOLD_CORRIDOR = registerLootTable("chests/stronghold_corridor");
        CHEST_DESERT_PYRAMID = registerLootTable("chests/desert_pyramid");
        CHEST_JUNGLE_TEMPLE = registerLootTable("chests/jungle_temple");
        DISPENSER_JUNGLE_TEMPLE = registerLootTable("chests/jungle_temple_dispenser");
        CHEST_IGLOO = registerLootTable("chests/igloo_chest");
        CHEST_WOODLAND_MANSION = registerLootTable("chests/woodland_mansion");
        E = registerLootTable("chests/underwater_ruin_small");
        F = registerLootTable("chests/underwater_ruin_big");
        G = registerLootTable("chests/buried_treasure");
        H = registerLootTable("chests/shipwreck_map");
        I = registerLootTable("chests/shipwreck_supply");
        J = registerLootTable("chests/shipwreck_treasure");
        K = registerLootTable("chests/pillager_outpost");
        ENTITY_SHEEP_WHITE = registerLootTable("entities/sheep/white");
        ENTITY_SHEEP_ORANGE = registerLootTable("entities/sheep/orange");
        ENTITY_SHEEP_MAGENTA = registerLootTable("entities/sheep/magenta");
        ENTITY_SHEEP_LIGHT_BLUE = registerLootTable("entities/sheep/light_blue");
        ENTITY_SHEEP_YELLOW = registerLootTable("entities/sheep/yellow");
        ENTITY_SHEEP_LIME = registerLootTable("entities/sheep/lime");
        ENTITY_SHEEP_PINK = registerLootTable("entities/sheep/pink");
        ENTITY_SHEEP_GRAY = registerLootTable("entities/sheep/gray");
        ENTITY_SHEEP_LIGHT_GRAY = registerLootTable("entities/sheep/light_gray");
        ENTITY_SHEEP_CYAN = registerLootTable("entities/sheep/cyan");
        ENTITY_SHEEP_PURPLE = registerLootTable("entities/sheep/purple");
        ENTITY_SHEEP_BLUE = registerLootTable("entities/sheep/blue");
        ENTITY_SHEEP_BROWN = registerLootTable("entities/sheep/brown");
        ENTITY_SHEEP_GREEN = registerLootTable("entities/sheep/green");
        ENTITY_SHEEP_RED = registerLootTable("entities/sheep/red");
        ENTITY_SHEEP_BLACK = registerLootTable("entities/sheep/black");
        GAMEPLAY_FISHING = registerLootTable("gameplay/fishing");
        GAMEPLAY_FISHING_JUNK = registerLootTable("gameplay/fishing/junk");
        GAMEPLAY_FISHING_TREASURE = registerLootTable("gameplay/fishing/treasure");
        GAMEPLAY_FISHING_FISH = registerLootTable("gameplay/fishing/fish");
        GAMEPLAY_CAT_MORNING_GIFT = registerLootTable("gameplay/cat_morning_gift");
        ag = registerLootTable("gameplay/hero_of_the_village/armorer_gift");
        ah = registerLootTable("gameplay/hero_of_the_village/butcher_gift");
        ai = registerLootTable("gameplay/hero_of_the_village/cartographer_gift");
        aj = registerLootTable("gameplay/hero_of_the_village/cleric_gift");
        ak = registerLootTable("gameplay/hero_of_the_village/farmer_gift");
        al = registerLootTable("gameplay/hero_of_the_village/fisherman_gift");
        am = registerLootTable("gameplay/hero_of_the_village/fletcher_gift");
        an = registerLootTable("gameplay/hero_of_the_village/leatherworker_gift");
        ao = registerLootTable("gameplay/hero_of_the_village/librarian_gift");
        ap = registerLootTable("gameplay/hero_of_the_village/mason_gift");
        aq = registerLootTable("gameplay/hero_of_the_village/shepherd_gift");
        ar = registerLootTable("gameplay/hero_of_the_village/toolsmith_gift");
        as = registerLootTable("gameplay/hero_of_the_village/weaponsmith_gift");
    }
}
