package net.minecraft.datafixers.fixes;

import java.util.stream.Stream;
import javax.annotation.Nullable;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.Arrays;
import net.minecraft.util.PackedIntegerArray;
import java.util.function.Supplier;
import java.nio.ByteBuffer;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import java.util.Set;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Iterator;
import com.mojang.datafixers.DataFixUtils;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Objects;
import java.util.HashMap;
import java.util.function.Function;
import com.mojang.datafixers.types.Type;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import java.util.Optional;
import net.minecraft.util.Int2ObjectBiMap;
import com.mojang.datafixers.schemas.Schema;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Map;
import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import org.apache.logging.log4j.Logger;
import com.mojang.datafixers.DataFix;

public class ChunkPalettedStorageFix extends DataFix
{
    private static final Logger LOGGER;
    private static final BitSet b;
    private static final BitSet c;
    private static final Dynamic<?> pumpkin;
    private static final Dynamic<?> podzol;
    private static final Dynamic<?> snowyGrass;
    private static final Dynamic<?> snowyMycelium;
    private static final Dynamic<?> sunflowerUpper;
    private static final Dynamic<?> lilacUpper;
    private static final Dynamic<?> grassUpper;
    private static final Dynamic<?> fernUpper;
    private static final Dynamic<?> roseUpper;
    private static final Dynamic<?> peonyUpper;
    private static final Map<String, Dynamic<?>> flowerPot;
    private static final Map<String, Dynamic<?>> skull;
    private static final Map<String, Dynamic<?>> door;
    private static final Map<String, Dynamic<?>> noteblock;
    private static final Int2ObjectMap<String> colors;
    private static final Map<String, Dynamic<?>> bed;
    private static final Map<String, Dynamic<?>> banner;
    private static final Dynamic<?> air;
    
    public ChunkPalettedStorageFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    private static void buildSkull(final Map<String, Dynamic<?>> out, final int integer, final String mob, final String block) {
        out.put(integer + "north", BlockStateFlattening.parseState("{Name:'minecraft:" + mob + "_wall_" + block + "',Properties:{facing:'north'}}"));
        out.put(integer + "east", BlockStateFlattening.parseState("{Name:'minecraft:" + mob + "_wall_" + block + "',Properties:{facing:'east'}}"));
        out.put(integer + "south", BlockStateFlattening.parseState("{Name:'minecraft:" + mob + "_wall_" + block + "',Properties:{facing:'south'}}"));
        out.put(integer + "west", BlockStateFlattening.parseState("{Name:'minecraft:" + mob + "_wall_" + block + "',Properties:{facing:'west'}}"));
        for (int integer2 = 0; integer2 < 16; ++integer2) {
            out.put(integer + "" + integer2, BlockStateFlattening.parseState("{Name:'minecraft:" + mob + "_" + block + "',Properties:{rotation:'" + integer2 + "'}}"));
        }
    }
    
    private static void buildDoor(final Map<String, Dynamic<?>> out, final String name, final int integer) {
        out.put("minecraft:" + name + "eastlowerleftfalsefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
        out.put("minecraft:" + name + "eastlowerleftfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
        out.put("minecraft:" + name + "eastlowerlefttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
        out.put("minecraft:" + name + "eastlowerlefttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
        out.put("minecraft:" + name + "eastlowerrightfalsefalse", BlockStateFlattening.lookupState(integer));
        out.put("minecraft:" + name + "eastlowerrightfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
        out.put("minecraft:" + name + "eastlowerrighttruefalse", BlockStateFlattening.lookupState(integer + 4));
        out.put("minecraft:" + name + "eastlowerrighttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
        out.put("minecraft:" + name + "eastupperleftfalsefalse", BlockStateFlattening.lookupState(integer + 8));
        out.put("minecraft:" + name + "eastupperleftfalsetrue", BlockStateFlattening.lookupState(integer + 10));
        out.put("minecraft:" + name + "eastupperlefttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
        out.put("minecraft:" + name + "eastupperlefttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
        out.put("minecraft:" + name + "eastupperrightfalsefalse", BlockStateFlattening.lookupState(integer + 9));
        out.put("minecraft:" + name + "eastupperrightfalsetrue", BlockStateFlattening.lookupState(integer + 11));
        out.put("minecraft:" + name + "eastupperrighttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
        out.put("minecraft:" + name + "eastupperrighttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
        out.put("minecraft:" + name + "northlowerleftfalsefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
        out.put("minecraft:" + name + "northlowerleftfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
        out.put("minecraft:" + name + "northlowerlefttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
        out.put("minecraft:" + name + "northlowerlefttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
        out.put("minecraft:" + name + "northlowerrightfalsefalse", BlockStateFlattening.lookupState(integer + 3));
        out.put("minecraft:" + name + "northlowerrightfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
        out.put("minecraft:" + name + "northlowerrighttruefalse", BlockStateFlattening.lookupState(integer + 7));
        out.put("minecraft:" + name + "northlowerrighttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
        out.put("minecraft:" + name + "northupperleftfalsefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'false'}}"));
        out.put("minecraft:" + name + "northupperleftfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'true'}}"));
        out.put("minecraft:" + name + "northupperlefttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
        out.put("minecraft:" + name + "northupperlefttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
        out.put("minecraft:" + name + "northupperrightfalsefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'false'}}"));
        out.put("minecraft:" + name + "northupperrightfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'true'}}"));
        out.put("minecraft:" + name + "northupperrighttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
        out.put("minecraft:" + name + "northupperrighttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
        out.put("minecraft:" + name + "southlowerleftfalsefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
        out.put("minecraft:" + name + "southlowerleftfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
        out.put("minecraft:" + name + "southlowerlefttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
        out.put("minecraft:" + name + "southlowerlefttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
        out.put("minecraft:" + name + "southlowerrightfalsefalse", BlockStateFlattening.lookupState(integer + 1));
        out.put("minecraft:" + name + "southlowerrightfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
        out.put("minecraft:" + name + "southlowerrighttruefalse", BlockStateFlattening.lookupState(integer + 5));
        out.put("minecraft:" + name + "southlowerrighttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
        out.put("minecraft:" + name + "southupperleftfalsefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'false'}}"));
        out.put("minecraft:" + name + "southupperleftfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'true'}}"));
        out.put("minecraft:" + name + "southupperlefttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
        out.put("minecraft:" + name + "southupperlefttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
        out.put("minecraft:" + name + "southupperrightfalsefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'false'}}"));
        out.put("minecraft:" + name + "southupperrightfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'true'}}"));
        out.put("minecraft:" + name + "southupperrighttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
        out.put("minecraft:" + name + "southupperrighttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
        out.put("minecraft:" + name + "westlowerleftfalsefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
        out.put("minecraft:" + name + "westlowerleftfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
        out.put("minecraft:" + name + "westlowerlefttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
        out.put("minecraft:" + name + "westlowerlefttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
        out.put("minecraft:" + name + "westlowerrightfalsefalse", BlockStateFlattening.lookupState(integer + 2));
        out.put("minecraft:" + name + "westlowerrightfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
        out.put("minecraft:" + name + "westlowerrighttruefalse", BlockStateFlattening.lookupState(integer + 6));
        out.put("minecraft:" + name + "westlowerrighttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
        out.put("minecraft:" + name + "westupperleftfalsefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'false'}}"));
        out.put("minecraft:" + name + "westupperleftfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'true'}}"));
        out.put("minecraft:" + name + "westupperlefttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
        out.put("minecraft:" + name + "westupperlefttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
        out.put("minecraft:" + name + "westupperrightfalsefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'false'}}"));
        out.put("minecraft:" + name + "westupperrightfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'true'}}"));
        out.put("minecraft:" + name + "westupperrighttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
        out.put("minecraft:" + name + "westupperrighttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
    }
    
    private static void buildBed(final Map<String, Dynamic<?>> out, final int integer, final String string) {
        out.put("southfalsefoot" + integer, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'south',occupied:'false',part:'foot'}}"));
        out.put("westfalsefoot" + integer, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'west',occupied:'false',part:'foot'}}"));
        out.put("northfalsefoot" + integer, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'north',occupied:'false',part:'foot'}}"));
        out.put("eastfalsefoot" + integer, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'east',occupied:'false',part:'foot'}}"));
        out.put("southfalsehead" + integer, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'south',occupied:'false',part:'head'}}"));
        out.put("westfalsehead" + integer, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'west',occupied:'false',part:'head'}}"));
        out.put("northfalsehead" + integer, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'north',occupied:'false',part:'head'}}"));
        out.put("eastfalsehead" + integer, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'east',occupied:'false',part:'head'}}"));
        out.put("southtruehead" + integer, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'south',occupied:'true',part:'head'}}"));
        out.put("westtruehead" + integer, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'west',occupied:'true',part:'head'}}"));
        out.put("northtruehead" + integer, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'north',occupied:'true',part:'head'}}"));
        out.put("easttruehead" + integer, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'east',occupied:'true',part:'head'}}"));
    }
    
    private static void buildBanner(final Map<String, Dynamic<?>> out, final int integer, final String string) {
        for (int integer2 = 0; integer2 < 16; ++integer2) {
            out.put("" + integer2 + "_" + integer, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_banner',Properties:{rotation:'" + integer2 + "'}}"));
        }
        out.put("north_" + integer, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_wall_banner',Properties:{facing:'north'}}"));
        out.put("south_" + integer, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_wall_banner',Properties:{facing:'south'}}"));
        out.put("west_" + integer, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_wall_banner',Properties:{facing:'west'}}"));
        out.put("east_" + integer, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_wall_banner',Properties:{facing:'east'}}"));
    }
    
    public static String getName(final Dynamic<?> dynamic) {
        return dynamic.get("Name").asString("");
    }
    
    public static String getProperty(final Dynamic<?> dynamic, final String string) {
        return dynamic.get("Properties").get(string).asString("");
    }
    
    public static int addTo(final Int2ObjectBiMap<Dynamic<?>> int2ObjectBiMap, final Dynamic<?> dynamic) {
        int integer3 = int2ObjectBiMap.getId(dynamic);
        if (integer3 == -1) {
            integer3 = int2ObjectBiMap.add(dynamic);
        }
        return integer3;
    }
    
    private Dynamic<?> fixChunk(final Dynamic<?> dynamic) {
        final Optional<? extends Dynamic<?>> optional2 = dynamic.get("Level").get();
        if (optional2.isPresent() && ((Dynamic)optional2.get()).get("Sections").asStreamOpt().isPresent()) {
            return dynamic.set("Level", (Dynamic)new Level(optional2.get()).transform());
        }
        return dynamic;
    }
    
    public TypeRewriteRule makeRule() {
        final Type<?> type1 = this.getInputSchema().getType(TypeReferences.CHUNK);
        final Type<?> type2 = this.getOutputSchema().getType(TypeReferences.CHUNK);
        return this.writeFixAndRead("ChunkPalettedStorageFix", (Type)type1, (Type)type2, (Function)this::fixChunk);
    }
    
    public static int a(final boolean boolean1, final boolean boolean2, final boolean boolean3, final boolean boolean4) {
        int integer5 = 0;
        if (boolean3) {
            if (boolean2) {
                integer5 |= 0x2;
            }
            else if (boolean1) {
                integer5 |= 0x80;
            }
            else {
                integer5 |= 0x1;
            }
        }
        else if (boolean4) {
            if (boolean1) {
                integer5 |= 0x20;
            }
            else if (boolean2) {
                integer5 |= 0x8;
            }
            else {
                integer5 |= 0x10;
            }
        }
        else if (boolean2) {
            integer5 |= 0x4;
        }
        else if (boolean1) {
            integer5 |= 0x40;
        }
        return integer5;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        b = new BitSet(256);
        c = new BitSet(256);
        pumpkin = BlockStateFlattening.parseState("{Name:'minecraft:pumpkin'}");
        podzol = BlockStateFlattening.parseState("{Name:'minecraft:podzol',Properties:{snowy:'true'}}");
        snowyGrass = BlockStateFlattening.parseState("{Name:'minecraft:grass_block',Properties:{snowy:'true'}}");
        snowyMycelium = BlockStateFlattening.parseState("{Name:'minecraft:mycelium',Properties:{snowy:'true'}}");
        sunflowerUpper = BlockStateFlattening.parseState("{Name:'minecraft:sunflower',Properties:{half:'upper'}}");
        lilacUpper = BlockStateFlattening.parseState("{Name:'minecraft:lilac',Properties:{half:'upper'}}");
        grassUpper = BlockStateFlattening.parseState("{Name:'minecraft:tall_grass',Properties:{half:'upper'}}");
        fernUpper = BlockStateFlattening.parseState("{Name:'minecraft:large_fern',Properties:{half:'upper'}}");
        roseUpper = BlockStateFlattening.parseState("{Name:'minecraft:rose_bush',Properties:{half:'upper'}}");
        peonyUpper = BlockStateFlattening.parseState("{Name:'minecraft:peony',Properties:{half:'upper'}}");
        flowerPot = (Map)DataFixUtils.make(Maps.newHashMap(), hashMap -> {
            hashMap.put("minecraft:air0", BlockStateFlattening.parseState("{Name:'minecraft:flower_pot'}"));
            hashMap.put("minecraft:red_flower0", BlockStateFlattening.parseState("{Name:'minecraft:potted_poppy'}"));
            hashMap.put("minecraft:red_flower1", BlockStateFlattening.parseState("{Name:'minecraft:potted_blue_orchid'}"));
            hashMap.put("minecraft:red_flower2", BlockStateFlattening.parseState("{Name:'minecraft:potted_allium'}"));
            hashMap.put("minecraft:red_flower3", BlockStateFlattening.parseState("{Name:'minecraft:potted_azure_bluet'}"));
            hashMap.put("minecraft:red_flower4", BlockStateFlattening.parseState("{Name:'minecraft:potted_red_tulip'}"));
            hashMap.put("minecraft:red_flower5", BlockStateFlattening.parseState("{Name:'minecraft:potted_orange_tulip'}"));
            hashMap.put("minecraft:red_flower6", BlockStateFlattening.parseState("{Name:'minecraft:potted_white_tulip'}"));
            hashMap.put("minecraft:red_flower7", BlockStateFlattening.parseState("{Name:'minecraft:potted_pink_tulip'}"));
            hashMap.put("minecraft:red_flower8", BlockStateFlattening.parseState("{Name:'minecraft:potted_oxeye_daisy'}"));
            hashMap.put("minecraft:yellow_flower0", BlockStateFlattening.parseState("{Name:'minecraft:potted_dandelion'}"));
            hashMap.put("minecraft:sapling0", BlockStateFlattening.parseState("{Name:'minecraft:potted_oak_sapling'}"));
            hashMap.put("minecraft:sapling1", BlockStateFlattening.parseState("{Name:'minecraft:potted_spruce_sapling'}"));
            hashMap.put("minecraft:sapling2", BlockStateFlattening.parseState("{Name:'minecraft:potted_birch_sapling'}"));
            hashMap.put("minecraft:sapling3", BlockStateFlattening.parseState("{Name:'minecraft:potted_jungle_sapling'}"));
            hashMap.put("minecraft:sapling4", BlockStateFlattening.parseState("{Name:'minecraft:potted_acacia_sapling'}"));
            hashMap.put("minecraft:sapling5", BlockStateFlattening.parseState("{Name:'minecraft:potted_dark_oak_sapling'}"));
            hashMap.put("minecraft:red_mushroom0", BlockStateFlattening.parseState("{Name:'minecraft:potted_red_mushroom'}"));
            hashMap.put("minecraft:brown_mushroom0", BlockStateFlattening.parseState("{Name:'minecraft:potted_brown_mushroom'}"));
            hashMap.put("minecraft:deadbush0", BlockStateFlattening.parseState("{Name:'minecraft:potted_dead_bush'}"));
            hashMap.put("minecraft:tallgrass2", BlockStateFlattening.parseState("{Name:'minecraft:potted_fern'}"));
            hashMap.put("minecraft:cactus0", BlockStateFlattening.lookupState(2240));
            return;
        });
        skull = (Map)DataFixUtils.make(Maps.newHashMap(), hashMap -> {
            buildSkull(hashMap, 0, "skeleton", "skull");
            buildSkull(hashMap, 1, "wither_skeleton", "skull");
            buildSkull(hashMap, 2, "zombie", "head");
            buildSkull(hashMap, 3, "player", "head");
            buildSkull(hashMap, 4, "creeper", "head");
            buildSkull(hashMap, 5, "dragon", "head");
            return;
        });
        door = (Map)DataFixUtils.make(Maps.newHashMap(), hashMap -> {
            buildDoor(hashMap, "oak_door", 1024);
            buildDoor(hashMap, "iron_door", 1136);
            buildDoor(hashMap, "spruce_door", 3088);
            buildDoor(hashMap, "birch_door", 3104);
            buildDoor(hashMap, "jungle_door", 3120);
            buildDoor(hashMap, "acacia_door", 3136);
            buildDoor(hashMap, "dark_oak_door", 3152);
            return;
        });
        int integer2;
        noteblock = (Map)DataFixUtils.make(Maps.newHashMap(), hashMap -> {
            for (integer2 = 0; integer2 < 26; ++integer2) {
                hashMap.put("true" + integer2, BlockStateFlattening.parseState("{Name:'minecraft:note_block',Properties:{powered:'true',note:'" + integer2 + "'}}"));
                hashMap.put("false" + integer2, BlockStateFlattening.parseState("{Name:'minecraft:note_block',Properties:{powered:'false',note:'" + integer2 + "'}}"));
            }
            return;
        });
        colors = (Int2ObjectMap)DataFixUtils.make(new Int2ObjectOpenHashMap(), int2ObjectOpenHashMap -> {
            int2ObjectOpenHashMap.put(0, "white");
            int2ObjectOpenHashMap.put(1, "orange");
            int2ObjectOpenHashMap.put(2, "magenta");
            int2ObjectOpenHashMap.put(3, "light_blue");
            int2ObjectOpenHashMap.put(4, "yellow");
            int2ObjectOpenHashMap.put(5, "lime");
            int2ObjectOpenHashMap.put(6, "pink");
            int2ObjectOpenHashMap.put(7, "gray");
            int2ObjectOpenHashMap.put(8, "light_gray");
            int2ObjectOpenHashMap.put(9, "cyan");
            int2ObjectOpenHashMap.put(10, "purple");
            int2ObjectOpenHashMap.put(11, "blue");
            int2ObjectOpenHashMap.put(12, "brown");
            int2ObjectOpenHashMap.put(13, "green");
            int2ObjectOpenHashMap.put(14, "red");
            int2ObjectOpenHashMap.put(15, "black");
            return;
        });
        final Iterator<Int2ObjectMap.Entry> iterator;
        Int2ObjectMap.Entry entry3;
        bed = (Map)DataFixUtils.make(Maps.newHashMap(), hashMap -> {
            ChunkPalettedStorageFix.colors.int2ObjectEntrySet().iterator();
            while (iterator.hasNext()) {
                entry3 = iterator.next();
                if (!Objects.equals(entry3.getValue(), "red")) {
                    buildBed(hashMap, entry3.getIntKey(), (String)entry3.getValue());
                }
            }
            return;
        });
        final Iterator<Int2ObjectMap.Entry> iterator2;
        Int2ObjectMap.Entry entry4;
        banner = (Map)DataFixUtils.make(Maps.newHashMap(), hashMap -> {
            ChunkPalettedStorageFix.colors.int2ObjectEntrySet().iterator();
            while (iterator2.hasNext()) {
                entry4 = iterator2.next();
                if (!Objects.equals(entry4.getValue(), "white")) {
                    buildBanner(hashMap, 15 - entry4.getIntKey(), (String)entry4.getValue());
                }
            }
            return;
        });
        ChunkPalettedStorageFix.c.set(2);
        ChunkPalettedStorageFix.c.set(3);
        ChunkPalettedStorageFix.c.set(110);
        ChunkPalettedStorageFix.c.set(140);
        ChunkPalettedStorageFix.c.set(144);
        ChunkPalettedStorageFix.c.set(25);
        ChunkPalettedStorageFix.c.set(86);
        ChunkPalettedStorageFix.c.set(26);
        ChunkPalettedStorageFix.c.set(176);
        ChunkPalettedStorageFix.c.set(177);
        ChunkPalettedStorageFix.c.set(175);
        ChunkPalettedStorageFix.c.set(64);
        ChunkPalettedStorageFix.c.set(71);
        ChunkPalettedStorageFix.c.set(193);
        ChunkPalettedStorageFix.c.set(194);
        ChunkPalettedStorageFix.c.set(195);
        ChunkPalettedStorageFix.c.set(196);
        ChunkPalettedStorageFix.c.set(197);
        ChunkPalettedStorageFix.b.set(54);
        ChunkPalettedStorageFix.b.set(146);
        ChunkPalettedStorageFix.b.set(25);
        ChunkPalettedStorageFix.b.set(26);
        ChunkPalettedStorageFix.b.set(51);
        ChunkPalettedStorageFix.b.set(53);
        ChunkPalettedStorageFix.b.set(67);
        ChunkPalettedStorageFix.b.set(108);
        ChunkPalettedStorageFix.b.set(109);
        ChunkPalettedStorageFix.b.set(114);
        ChunkPalettedStorageFix.b.set(128);
        ChunkPalettedStorageFix.b.set(134);
        ChunkPalettedStorageFix.b.set(135);
        ChunkPalettedStorageFix.b.set(136);
        ChunkPalettedStorageFix.b.set(156);
        ChunkPalettedStorageFix.b.set(163);
        ChunkPalettedStorageFix.b.set(164);
        ChunkPalettedStorageFix.b.set(180);
        ChunkPalettedStorageFix.b.set(203);
        ChunkPalettedStorageFix.b.set(55);
        ChunkPalettedStorageFix.b.set(85);
        ChunkPalettedStorageFix.b.set(113);
        ChunkPalettedStorageFix.b.set(188);
        ChunkPalettedStorageFix.b.set(189);
        ChunkPalettedStorageFix.b.set(190);
        ChunkPalettedStorageFix.b.set(191);
        ChunkPalettedStorageFix.b.set(192);
        ChunkPalettedStorageFix.b.set(93);
        ChunkPalettedStorageFix.b.set(94);
        ChunkPalettedStorageFix.b.set(101);
        ChunkPalettedStorageFix.b.set(102);
        ChunkPalettedStorageFix.b.set(160);
        ChunkPalettedStorageFix.b.set(106);
        ChunkPalettedStorageFix.b.set(107);
        ChunkPalettedStorageFix.b.set(183);
        ChunkPalettedStorageFix.b.set(184);
        ChunkPalettedStorageFix.b.set(185);
        ChunkPalettedStorageFix.b.set(186);
        ChunkPalettedStorageFix.b.set(187);
        ChunkPalettedStorageFix.b.set(132);
        ChunkPalettedStorageFix.b.set(139);
        ChunkPalettedStorageFix.b.set(199);
        air = BlockStateFlattening.lookupState(0);
    }
    
    static class Section
    {
        private final Int2ObjectBiMap<Dynamic<?>> paletteMap;
        private Dynamic<?> paletteData;
        private final Dynamic<?> section;
        private final boolean hasBlocks;
        private final Int2ObjectMap<IntList> seenIds;
        private final IntList innerPositions;
        public final int y;
        private final Set<Dynamic<?>> seenStates;
        private final int[] states;
        
        public Section(final Dynamic<?> dynamic) {
            this.paletteMap = new Int2ObjectBiMap<Dynamic<?>>(32);
            this.seenIds = (Int2ObjectMap<IntList>)new Int2ObjectLinkedOpenHashMap();
            this.innerPositions = (IntList)new IntArrayList();
            this.seenStates = Sets.<Dynamic<?>>newIdentityHashSet();
            this.states = new int[4096];
            this.paletteData = dynamic.emptyList();
            this.section = dynamic;
            this.y = dynamic.get("Y").asInt(0);
            this.hasBlocks = dynamic.get("Blocks").get().isPresent();
        }
        
        public Dynamic<?> getBlock(final int integer) {
            if (integer < 0 || integer > 4095) {
                return ChunkPalettedStorageFix.air;
            }
            final Dynamic<?> dynamic2 = this.paletteMap.get(this.states[integer]);
            return (dynamic2 == null) ? ChunkPalettedStorageFix.air : dynamic2;
        }
        
        public void setBlock(final int pos, final Dynamic<?> dynamic) {
            if (this.seenStates.add(dynamic)) {
                this.paletteData = this.paletteData.merge("%%FILTER_ME%%".equals(ChunkPalettedStorageFix.getName(dynamic)) ? ChunkPalettedStorageFix.air : dynamic);
            }
            this.states[pos] = ChunkPalettedStorageFix.addTo(this.paletteMap, dynamic);
        }
        
        public int visit(int integer) {
            if (!this.hasBlocks) {
                return integer;
            }
            final ByteBuffer byteBuffer2 = this.section.get("Blocks").asByteBufferOpt().get();
            final ChunkNibbleArray chunkNibbleArray3 = this.section.get("Data").asByteBufferOpt().<ChunkNibbleArray>map(byteBuffer -> new ChunkNibbleArray(DataFixUtils.toArray(byteBuffer))).orElseGet(ChunkNibbleArray::new);
            final ChunkNibbleArray chunkNibbleArray4 = this.section.get("Add").asByteBufferOpt().<ChunkNibbleArray>map(byteBuffer -> new ChunkNibbleArray(DataFixUtils.toArray(byteBuffer))).orElseGet(ChunkNibbleArray::new);
            this.seenStates.add(ChunkPalettedStorageFix.air);
            ChunkPalettedStorageFix.addTo(this.paletteMap, ChunkPalettedStorageFix.air);
            this.paletteData = this.paletteData.merge(ChunkPalettedStorageFix.air);
            for (int integer2 = 0; integer2 < 4096; ++integer2) {
                final int integer3 = integer2 & 0xF;
                final int integer4 = integer2 >> 8 & 0xF;
                final int integer5 = integer2 >> 4 & 0xF;
                final int integer6 = chunkNibbleArray4.get(integer3, integer4, integer5) << 12 | (byteBuffer2.get(integer2) & 0xFF) << 4 | chunkNibbleArray3.get(integer3, integer4, integer5);
                if (ChunkPalettedStorageFix.c.get(integer6 >> 4)) {
                    this.addBlockAt(integer6 >> 4, integer2);
                }
                if (ChunkPalettedStorageFix.b.get(integer6 >> 4)) {
                    final int integer7 = ChunkPalettedStorageFix.a(integer3 == 0, integer3 == 15, integer5 == 0, integer5 == 15);
                    if (integer7 == 0) {
                        this.innerPositions.add(integer2);
                    }
                    else {
                        integer |= integer7;
                    }
                }
                this.setBlock(integer2, BlockStateFlattening.lookupState(integer6));
            }
            return integer;
        }
        
        private void addBlockAt(final int integer1, final int integer2) {
            IntList intList3 = (IntList)this.seenIds.get(integer1);
            if (intList3 == null) {
                intList3 = (IntList)new IntArrayList();
                this.seenIds.put(integer1, intList3);
            }
            intList3.add(integer2);
        }
        
        public Dynamic<?> transform() {
            Dynamic<?> dynamic1 = this.section;
            if (!this.hasBlocks) {
                return dynamic1;
            }
            dynamic1 = dynamic1.set("Palette", (Dynamic)this.paletteData);
            final int integer2 = Math.max(4, DataFixUtils.ceillog2(this.seenStates.size()));
            final PackedIntegerArray packedIntegerArray3 = new PackedIntegerArray(integer2, 4096);
            for (int integer3 = 0; integer3 < this.states.length; ++integer3) {
                packedIntegerArray3.set(integer3, this.states[integer3]);
            }
            dynamic1 = dynamic1.set("BlockStates", dynamic1.createLongList(Arrays.stream(packedIntegerArray3.getStorage())));
            dynamic1 = dynamic1.remove("Blocks");
            dynamic1 = dynamic1.remove("Data");
            dynamic1 = dynamic1.remove("Add");
            return dynamic1;
        }
    }
    
    static final class Level
    {
        private int sides;
        private final Section[] sections;
        private final Dynamic<?> level;
        private final int xPos;
        private final int yPos;
        private final Int2ObjectMap<Dynamic<?>> blockEntities;
        
        public Level(final Dynamic<?> dynamic) {
            this.sections = new Section[16];
            this.blockEntities = (Int2ObjectMap<Dynamic<?>>)new Int2ObjectLinkedOpenHashMap(16);
            this.level = dynamic;
            this.xPos = dynamic.get("xPos").asInt(0) << 4;
            this.yPos = dynamic.get("zPos").asInt(0) << 4;
            final int integer2;
            final int integer3;
            final int integer4;
            final int integer5;
            dynamic.get("TileEntities").asStreamOpt().ifPresent(stream -> stream.forEach(dynamic -> {
                integer2 = (dynamic.get("x").asInt(0) - this.xPos & 0xF);
                integer3 = dynamic.get("y").asInt(0);
                integer4 = (dynamic.get("z").asInt(0) - this.yPos & 0xF);
                integer5 = (integer3 << 8 | integer4 << 4 | integer2);
                if (this.blockEntities.put(integer5, dynamic) != null) {
                    ChunkPalettedStorageFix.LOGGER.warn("In chunk: {}x{} found a duplicate block entity at position: [{}, {}, {}]", this.xPos, this.yPos, integer2, integer3, integer4);
                }
            }));
            final boolean boolean2 = dynamic.get("convertedFromAlphaFormat").asBoolean(false);
            final Section section2;
            dynamic.get("Sections").asStreamOpt().ifPresent(stream -> stream.forEach(dynamic -> {
                section2 = new Section(dynamic);
                this.sides = section2.visit(this.sides);
                this.sections[section2.y] = section2;
            }));
            for (final Section section3 : this.sections) {
                if (section3 != null) {
                    for (final Map.Entry<Integer, IntList> entry8 : section3.seenIds.entrySet()) {
                        final int integer6 = section3.y << 12;
                        switch (entry8.getKey()) {
                            case 2: {
                                for (int integer7 : entry8.getValue()) {
                                    integer7 |= integer6;
                                    final Dynamic<?> dynamic2 = this.getBlock(integer7);
                                    if ("minecraft:grass_block".equals(ChunkPalettedStorageFix.getName(dynamic2))) {
                                        final String string13 = ChunkPalettedStorageFix.getName(this.getBlock(adjacentTo(integer7, Facing.UP)));
                                        if (!"minecraft:snow".equals(string13) && !"minecraft:snow_layer".equals(string13)) {
                                            continue;
                                        }
                                        this.setBlock(integer7, ChunkPalettedStorageFix.snowyGrass);
                                    }
                                }
                                continue;
                            }
                            case 3: {
                                for (int integer7 : entry8.getValue()) {
                                    integer7 |= integer6;
                                    final Dynamic<?> dynamic2 = this.getBlock(integer7);
                                    if ("minecraft:podzol".equals(ChunkPalettedStorageFix.getName(dynamic2))) {
                                        final String string13 = ChunkPalettedStorageFix.getName(this.getBlock(adjacentTo(integer7, Facing.UP)));
                                        if (!"minecraft:snow".equals(string13) && !"minecraft:snow_layer".equals(string13)) {
                                            continue;
                                        }
                                        this.setBlock(integer7, ChunkPalettedStorageFix.podzol);
                                    }
                                }
                                continue;
                            }
                            case 110: {
                                for (int integer7 : entry8.getValue()) {
                                    integer7 |= integer6;
                                    final Dynamic<?> dynamic2 = this.getBlock(integer7);
                                    if ("minecraft:mycelium".equals(ChunkPalettedStorageFix.getName(dynamic2))) {
                                        final String string13 = ChunkPalettedStorageFix.getName(this.getBlock(adjacentTo(integer7, Facing.UP)));
                                        if (!"minecraft:snow".equals(string13) && !"minecraft:snow_layer".equals(string13)) {
                                            continue;
                                        }
                                        this.setBlock(integer7, ChunkPalettedStorageFix.snowyMycelium);
                                    }
                                }
                                continue;
                            }
                            case 25: {
                                for (int integer7 : entry8.getValue()) {
                                    integer7 |= integer6;
                                    final Dynamic<?> dynamic2 = this.removeBlockEntity(integer7);
                                    if (dynamic2 != null) {
                                        final String string13 = Boolean.toString(dynamic2.get("powered").asBoolean(false)) + (byte)Math.min(Math.max(dynamic2.get("note").asInt(0), 0), 24);
                                        this.setBlock(integer7, ChunkPalettedStorageFix.noteblock.getOrDefault(string13, ChunkPalettedStorageFix.noteblock.get("false0")));
                                    }
                                }
                                continue;
                            }
                            case 26: {
                                for (int integer7 : entry8.getValue()) {
                                    integer7 |= integer6;
                                    final Dynamic<?> dynamic2 = this.getBlockEntity(integer7);
                                    final Dynamic<?> dynamic3 = this.getBlock(integer7);
                                    if (dynamic2 != null) {
                                        final int integer8 = dynamic2.get("color").asInt(0);
                                        if (integer8 == 14 || integer8 < 0 || integer8 >= 16) {
                                            continue;
                                        }
                                        final String string14 = ChunkPalettedStorageFix.getProperty(dynamic3, "facing") + ChunkPalettedStorageFix.getProperty(dynamic3, "occupied") + ChunkPalettedStorageFix.getProperty(dynamic3, "part") + integer8;
                                        if (!ChunkPalettedStorageFix.bed.containsKey(string14)) {
                                            continue;
                                        }
                                        this.setBlock(integer7, ChunkPalettedStorageFix.bed.get(string14));
                                    }
                                }
                                continue;
                            }
                            case 176:
                            case 177: {
                                for (int integer7 : entry8.getValue()) {
                                    integer7 |= integer6;
                                    final Dynamic<?> dynamic2 = this.getBlockEntity(integer7);
                                    final Dynamic<?> dynamic3 = this.getBlock(integer7);
                                    if (dynamic2 != null) {
                                        final int integer8 = dynamic2.get("Base").asInt(0);
                                        if (integer8 == 15 || integer8 < 0 || integer8 >= 16) {
                                            continue;
                                        }
                                        final String string14 = ChunkPalettedStorageFix.getProperty(dynamic3, (entry8.getKey() == 176) ? "rotation" : "facing") + "_" + integer8;
                                        if (!ChunkPalettedStorageFix.banner.containsKey(string14)) {
                                            continue;
                                        }
                                        this.setBlock(integer7, ChunkPalettedStorageFix.banner.get(string14));
                                    }
                                }
                                continue;
                            }
                            case 86: {
                                for (int integer7 : entry8.getValue()) {
                                    integer7 |= integer6;
                                    final Dynamic<?> dynamic2 = this.getBlock(integer7);
                                    if ("minecraft:carved_pumpkin".equals(ChunkPalettedStorageFix.getName(dynamic2))) {
                                        final String string13 = ChunkPalettedStorageFix.getName(this.getBlock(adjacentTo(integer7, Facing.DOWN)));
                                        if (!"minecraft:grass_block".equals(string13) && !"minecraft:dirt".equals(string13)) {
                                            continue;
                                        }
                                        this.setBlock(integer7, ChunkPalettedStorageFix.pumpkin);
                                    }
                                }
                                continue;
                            }
                            case 140: {
                                for (int integer7 : entry8.getValue()) {
                                    integer7 |= integer6;
                                    final Dynamic<?> dynamic2 = this.removeBlockEntity(integer7);
                                    if (dynamic2 != null) {
                                        final String string13 = dynamic2.get("Item").asString("") + dynamic2.get("Data").asInt(0);
                                        this.setBlock(integer7, ChunkPalettedStorageFix.flowerPot.getOrDefault(string13, ChunkPalettedStorageFix.flowerPot.get("minecraft:air0")));
                                    }
                                }
                                continue;
                            }
                            case 144: {
                                for (int integer7 : entry8.getValue()) {
                                    integer7 |= integer6;
                                    final Dynamic<?> dynamic2 = this.getBlockEntity(integer7);
                                    if (dynamic2 != null) {
                                        final String string13 = String.valueOf(dynamic2.get("SkullType").asInt(0));
                                        final String string15 = ChunkPalettedStorageFix.getProperty(this.getBlock(integer7), "facing");
                                        String string14;
                                        if ("up".equals(string15) || "down".equals(string15)) {
                                            string14 = string13 + String.valueOf(dynamic2.get("Rot").asInt(0));
                                        }
                                        else {
                                            string14 = string13 + string15;
                                        }
                                        dynamic2.remove("SkullType");
                                        dynamic2.remove("facing");
                                        dynamic2.remove("Rot");
                                        this.setBlock(integer7, ChunkPalettedStorageFix.skull.getOrDefault(string14, ChunkPalettedStorageFix.skull.get("0north")));
                                    }
                                }
                                continue;
                            }
                            case 64:
                            case 71:
                            case 193:
                            case 194:
                            case 195:
                            case 196:
                            case 197: {
                                for (int integer7 : entry8.getValue()) {
                                    integer7 |= integer6;
                                    final Dynamic<?> dynamic2 = this.getBlock(integer7);
                                    if (ChunkPalettedStorageFix.getName(dynamic2).endsWith("_door")) {
                                        final Dynamic<?> dynamic3 = this.getBlock(integer7);
                                        if (!"lower".equals(ChunkPalettedStorageFix.getProperty(dynamic3, "half"))) {
                                            continue;
                                        }
                                        final int integer8 = adjacentTo(integer7, Facing.UP);
                                        final Dynamic<?> dynamic4 = this.getBlock(integer8);
                                        final String string16 = ChunkPalettedStorageFix.getName(dynamic3);
                                        if (!string16.equals(ChunkPalettedStorageFix.getName(dynamic4))) {
                                            continue;
                                        }
                                        final String string17 = ChunkPalettedStorageFix.getProperty(dynamic3, "facing");
                                        final String string18 = ChunkPalettedStorageFix.getProperty(dynamic3, "open");
                                        final String string19 = boolean2 ? "left" : ChunkPalettedStorageFix.getProperty(dynamic4, "hinge");
                                        final String string20 = boolean2 ? "false" : ChunkPalettedStorageFix.getProperty(dynamic4, "powered");
                                        this.setBlock(integer7, ChunkPalettedStorageFix.door.get(string16 + string17 + "lower" + string19 + string18 + string20));
                                        this.setBlock(integer8, ChunkPalettedStorageFix.door.get(string16 + string17 + "upper" + string19 + string18 + string20));
                                    }
                                }
                                continue;
                            }
                            case 175: {
                                for (int integer7 : entry8.getValue()) {
                                    integer7 |= integer6;
                                    final Dynamic<?> dynamic2 = this.getBlock(integer7);
                                    if ("upper".equals(ChunkPalettedStorageFix.getProperty(dynamic2, "half"))) {
                                        final Dynamic<?> dynamic3 = this.getBlock(adjacentTo(integer7, Facing.DOWN));
                                        final String string15 = ChunkPalettedStorageFix.getName(dynamic3);
                                        if ("minecraft:sunflower".equals(string15)) {
                                            this.setBlock(integer7, ChunkPalettedStorageFix.sunflowerUpper);
                                        }
                                        else if ("minecraft:lilac".equals(string15)) {
                                            this.setBlock(integer7, ChunkPalettedStorageFix.lilacUpper);
                                        }
                                        else if ("minecraft:tall_grass".equals(string15)) {
                                            this.setBlock(integer7, ChunkPalettedStorageFix.grassUpper);
                                        }
                                        else if ("minecraft:large_fern".equals(string15)) {
                                            this.setBlock(integer7, ChunkPalettedStorageFix.fernUpper);
                                        }
                                        else if ("minecraft:rose_bush".equals(string15)) {
                                            this.setBlock(integer7, ChunkPalettedStorageFix.roseUpper);
                                        }
                                        else {
                                            if (!"minecraft:peony".equals(string15)) {
                                                continue;
                                            }
                                            this.setBlock(integer7, ChunkPalettedStorageFix.peonyUpper);
                                        }
                                    }
                                }
                                continue;
                            }
                        }
                    }
                }
            }
        }
        
        @Nullable
        private Dynamic<?> getBlockEntity(final int integer) {
            return this.blockEntities.get(integer);
        }
        
        @Nullable
        private Dynamic<?> removeBlockEntity(final int integer) {
            return this.blockEntities.remove(integer);
        }
        
        public static int adjacentTo(final int integer, final Facing direction) {
            switch (direction.getAxis()) {
                case X: {
                    final int integer2 = (integer & 0xF) + direction.getDirection().getOffset();
                    return (integer2 < 0 || integer2 > 15) ? -1 : ((integer & 0xFFFFFFF0) | integer2);
                }
                case Y: {
                    final int integer3 = (integer >> 8) + direction.getDirection().getOffset();
                    return (integer3 < 0 || integer3 > 255) ? -1 : ((integer & 0xFF) | integer3 << 8);
                }
                case Z: {
                    final int integer4 = (integer >> 4 & 0xF) + direction.getDirection().getOffset();
                    return (integer4 < 0 || integer4 > 15) ? -1 : ((integer & 0xFFFFFF0F) | integer4 << 4);
                }
                default: {
                    return -1;
                }
            }
        }
        
        private void setBlock(final int integer, final Dynamic<?> dynamic) {
            if (integer < 0 || integer > 65535) {
                return;
            }
            final Section section3 = this.getSection(integer);
            if (section3 == null) {
                return;
            }
            section3.setBlock(integer & 0xFFF, dynamic);
        }
        
        @Nullable
        private Section getSection(final int integer) {
            final int integer2 = integer >> 12;
            return (integer2 < this.sections.length) ? this.sections[integer2] : null;
        }
        
        public Dynamic<?> getBlock(final int integer) {
            if (integer < 0 || integer > 65535) {
                return ChunkPalettedStorageFix.air;
            }
            final Section section2 = this.getSection(integer);
            if (section2 == null) {
                return ChunkPalettedStorageFix.air;
            }
            return section2.getBlock(integer & 0xFFF);
        }
        
        public Dynamic<?> transform() {
            Dynamic<?> dynamic1 = this.level;
            if (this.blockEntities.isEmpty()) {
                dynamic1 = dynamic1.remove("TileEntities");
            }
            else {
                dynamic1 = dynamic1.set("TileEntities", dynamic1.createList(this.blockEntities.values().stream()));
            }
            Dynamic<?> dynamic2 = dynamic1.emptyMap();
            Dynamic<?> dynamic3 = dynamic1.emptyList();
            for (final Section section7 : this.sections) {
                if (section7 != null) {
                    dynamic3 = dynamic3.merge((Dynamic)section7.transform());
                    dynamic2 = dynamic2.set(String.valueOf(section7.y), dynamic2.createIntList(Arrays.stream(section7.innerPositions.toIntArray())));
                }
            }
            Dynamic<?> dynamic4 = dynamic1.emptyMap();
            dynamic4 = dynamic4.set("Sides", dynamic4.createByte((byte)this.sides));
            dynamic4 = dynamic4.set("Indices", (Dynamic)dynamic2);
            return dynamic1.set("UpgradeData", (Dynamic)dynamic4).set("Sections", (Dynamic)dynamic3);
        }
    }
    
    static class ChunkNibbleArray
    {
        private final byte[] contents;
        
        public ChunkNibbleArray() {
            this.contents = new byte[2048];
        }
        
        public ChunkNibbleArray(final byte[] arr) {
            this.contents = arr;
            if (arr.length != 2048) {
                throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + arr.length);
            }
        }
        
        public int get(final int x, final int y, final int integer3) {
            final int integer4 = this.b(y << 8 | integer3 << 4 | x);
            if (this.a(y << 8 | integer3 << 4 | x)) {
                return this.contents[integer4] & 0xF;
            }
            return this.contents[integer4] >> 4 & 0xF;
        }
        
        private boolean a(final int integer) {
            return (integer & 0x1) == 0x0;
        }
        
        private int b(final int integer) {
            return integer >> 1;
        }
    }
    
    public enum Facing
    {
        DOWN(Direction.NEGATIVE, Axis.Y), 
        UP(Direction.POSITIVE, Axis.Y), 
        NORTH(Direction.NEGATIVE, Axis.Z), 
        SOUTH(Direction.POSITIVE, Axis.Z), 
        WEST(Direction.NEGATIVE, Axis.X), 
        EAST(Direction.POSITIVE, Axis.X);
        
        private final Axis axis;
        private final Direction direction;
        
        private Facing(final Direction direction, final Axis axis) {
            this.axis = axis;
            this.direction = direction;
        }
        
        public Direction getDirection() {
            return this.direction;
        }
        
        public Axis getAxis() {
            return this.axis;
        }
        
        public enum Axis
        {
            X, 
            Y, 
            Z;
        }
        
        public enum Direction
        {
            POSITIVE(1), 
            NEGATIVE(-1);
            
            private final int offset;
            
            private Direction(final int integer1) {
                this.offset = integer1;
            }
            
            public int getOffset() {
                return this.offset;
            }
        }
    }
}
