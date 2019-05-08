package net.minecraft.datafixers.fixes;

import net.minecraft.util.SystemUtil;
import java.util.HashMap;
import java.util.Collections;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Iterator;
import java.util.stream.Collector;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.google.common.collect.Lists;
import java.util.Locale;
import com.google.common.collect.Maps;
import com.google.common.base.Splitter;
import com.google.gson.JsonElement;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import com.mojang.datafixers.Typed;
import net.minecraft.util.JsonHelper;
import com.mojang.datafixers.types.JsonOps;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import com.mojang.datafixers.DataFix;

public class LevelDataGeneratorOptionsFix extends DataFix
{
    static final Map<String, String> NUMERICAL_IDS_TO_BIOME_IDS;
    
    public LevelDataGeneratorOptionsFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    protected TypeRewriteRule makeRule() {
        final Type<?> type1 = this.getOutputSchema().getType(TypeReferences.LEVEL);
        final Dynamic<?> dynamic3;
        final Optional<String> optional4;
        String string6;
        Dynamic<?> dynamic4;
        Dynamic<JsonElement> dynamic5;
        final Type type2;
        return this.fixTypeEverywhereTyped("LevelDataGeneratorOptionsFix", this.getInputSchema().getType(TypeReferences.LEVEL), (Type)type1, typed -> {
            dynamic3 = typed.write();
            optional4 = (Optional<String>)dynamic3.get("generatorOptions").asString();
            if ("flat".equalsIgnoreCase(dynamic3.get("generatorName").asString(""))) {
                string6 = optional4.orElse("");
                dynamic4 = dynamic3.set("generatorOptions", (Dynamic)LevelDataGeneratorOptionsFix.a(string6, (com.mojang.datafixers.types.DynamicOps<Object>)dynamic3.getOps()));
            }
            else if ("buffet".equalsIgnoreCase(dynamic3.get("generatorName").asString("")) && optional4.isPresent()) {
                dynamic5 = (Dynamic<JsonElement>)new Dynamic((DynamicOps)JsonOps.INSTANCE, JsonHelper.deserialize(optional4.get(), true));
                dynamic4 = dynamic3.set("generatorOptions", dynamic5.convert(dynamic3.getOps()));
            }
            else {
                dynamic4 = dynamic3;
            }
            return (Typed)((Optional)type2.readTyped((Dynamic)dynamic4).getSecond()).orElseThrow(() -> new IllegalStateException("Could not read new level type."));
        });
    }
    
    private static <T> Dynamic<T> a(final String string, final DynamicOps<T> dynamicOps) {
        final Iterator<String> iterator3 = Splitter.on(';').split(string).iterator();
        String string2 = "minecraft:plains";
        final Map<String, Map<String, String>> map6 = Maps.newHashMap();
        List<Pair<Integer, String>> list4;
        if (!string.isEmpty() && iterator3.hasNext()) {
            list4 = b(iterator3.next());
            if (!list4.isEmpty()) {
                if (iterator3.hasNext()) {
                    string2 = LevelDataGeneratorOptionsFix.NUMERICAL_IDS_TO_BIOME_IDS.getOrDefault(iterator3.next(), "minecraft:plains");
                }
                if (iterator3.hasNext()) {
                    final String[] split;
                    final String[] arr7 = split = iterator3.next().toLowerCase(Locale.ROOT).split(",");
                    for (final String string3 : split) {
                        final String[] arr8 = string3.split("\\(", 2);
                        if (!arr8[0].isEmpty()) {
                            map6.put(arr8[0], Maps.newHashMap());
                            if (arr8.length > 1 && arr8[1].endsWith(")") && arr8[1].length() > 1) {
                                final String[] split2;
                                final String[] arr9 = split2 = arr8[1].substring(0, arr8[1].length() - 1).split(" ");
                                for (final String string4 : split2) {
                                    final String[] arr10 = string4.split("=", 2);
                                    if (arr10.length == 2) {
                                        map6.get(arr8[0]).put(arr10[0], arr10[1]);
                                    }
                                }
                            }
                        }
                    }
                }
                else {
                    map6.put("village", Maps.newHashMap());
                }
            }
        }
        else {
            list4 = Lists.newArrayList();
            list4.add((Pair<Integer, String>)Pair.of(1, "minecraft:bedrock"));
            list4.add((Pair<Integer, String>)Pair.of(2, "minecraft:dirt"));
            list4.add((Pair<Integer, String>)Pair.of(1, "minecraft:grass_block"));
            map6.put("village", Maps.newHashMap());
        }
        final T object7 = (T)dynamicOps.createList((Stream)list4.stream().map(pair -> dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("height"), dynamicOps.createInt((int)pair.getFirst()), dynamicOps.createString("block"), dynamicOps.createString((String)pair.getSecond())))));
        final T object8 = (T)dynamicOps.createMap((Map)map6.entrySet().stream().map(entry -> Pair.of(dynamicOps.createString(entry.getKey().toLowerCase(Locale.ROOT)), dynamicOps.createMap((Map)((Map)entry.getValue()).entrySet().stream().map(entry -> Pair.of(dynamicOps.createString((String)entry.getKey()), dynamicOps.createString((String)entry.getValue()))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("layers"), object7, dynamicOps.createString("biome"), dynamicOps.createString(string2), dynamicOps.createString("structures"), object8)));
    }
    
    @Nullable
    private static Pair<Integer, String> a(final String string) {
        final String[] arr2 = string.split("\\*", 2);
        int integer3 = 0;
        Label_0030: {
            if (arr2.length == 2) {
                try {
                    integer3 = Integer.parseInt(arr2[0]);
                    break Label_0030;
                }
                catch (NumberFormatException numberFormatException4) {
                    return null;
                }
            }
            integer3 = 1;
        }
        final String string2 = arr2[arr2.length - 1];
        return (Pair<Integer, String>)Pair.of(integer3, string2);
    }
    
    private static List<Pair<Integer, String>> b(final String string) {
        final List<Pair<Integer, String>> list2 = Lists.newArrayList();
        final String[] split;
        final String[] arr3 = split = string.split(",");
        for (final String string2 : split) {
            final Pair<Integer, String> pair8 = a(string2);
            if (pair8 == null) {
                return Collections.<Pair<Integer, String>>emptyList();
            }
            list2.add(pair8);
        }
        return list2;
    }
    
    static {
        NUMERICAL_IDS_TO_BIOME_IDS = SystemUtil.<Map<String, String>>consume(Maps.newHashMap(), hashMap -> {
            hashMap.put("0", "minecraft:ocean");
            hashMap.put("1", "minecraft:plains");
            hashMap.put("2", "minecraft:desert");
            hashMap.put("3", "minecraft:mountains");
            hashMap.put("4", "minecraft:forest");
            hashMap.put("5", "minecraft:taiga");
            hashMap.put("6", "minecraft:swamp");
            hashMap.put("7", "minecraft:river");
            hashMap.put("8", "minecraft:nether");
            hashMap.put("9", "minecraft:the_end");
            hashMap.put("10", "minecraft:frozen_ocean");
            hashMap.put("11", "minecraft:frozen_river");
            hashMap.put("12", "minecraft:snowy_tundra");
            hashMap.put("13", "minecraft:snowy_mountains");
            hashMap.put("14", "minecraft:mushroom_fields");
            hashMap.put("15", "minecraft:mushroom_field_shore");
            hashMap.put("16", "minecraft:beach");
            hashMap.put("17", "minecraft:desert_hills");
            hashMap.put("18", "minecraft:wooded_hills");
            hashMap.put("19", "minecraft:taiga_hills");
            hashMap.put("20", "minecraft:mountain_edge");
            hashMap.put("21", "minecraft:jungle");
            hashMap.put("22", "minecraft:jungle_hills");
            hashMap.put("23", "minecraft:jungle_edge");
            hashMap.put("24", "minecraft:deep_ocean");
            hashMap.put("25", "minecraft:stone_shore");
            hashMap.put("26", "minecraft:snowy_beach");
            hashMap.put("27", "minecraft:birch_forest");
            hashMap.put("28", "minecraft:birch_forest_hills");
            hashMap.put("29", "minecraft:dark_forest");
            hashMap.put("30", "minecraft:snowy_taiga");
            hashMap.put("31", "minecraft:snowy_taiga_hills");
            hashMap.put("32", "minecraft:giant_tree_taiga");
            hashMap.put("33", "minecraft:giant_tree_taiga_hills");
            hashMap.put("34", "minecraft:wooded_mountains");
            hashMap.put("35", "minecraft:savanna");
            hashMap.put("36", "minecraft:savanna_plateau");
            hashMap.put("37", "minecraft:badlands");
            hashMap.put("38", "minecraft:wooded_badlands_plateau");
            hashMap.put("39", "minecraft:badlands_plateau");
            hashMap.put("40", "minecraft:small_end_islands");
            hashMap.put("41", "minecraft:end_midlands");
            hashMap.put("42", "minecraft:end_highlands");
            hashMap.put("43", "minecraft:end_barrens");
            hashMap.put("44", "minecraft:warm_ocean");
            hashMap.put("45", "minecraft:lukewarm_ocean");
            hashMap.put("46", "minecraft:cold_ocean");
            hashMap.put("47", "minecraft:deep_warm_ocean");
            hashMap.put("48", "minecraft:deep_lukewarm_ocean");
            hashMap.put("49", "minecraft:deep_cold_ocean");
            hashMap.put("50", "minecraft:deep_frozen_ocean");
            hashMap.put("127", "minecraft:the_void");
            hashMap.put("129", "minecraft:sunflower_plains");
            hashMap.put("130", "minecraft:desert_lakes");
            hashMap.put("131", "minecraft:gravelly_mountains");
            hashMap.put("132", "minecraft:flower_forest");
            hashMap.put("133", "minecraft:taiga_mountains");
            hashMap.put("134", "minecraft:swamp_hills");
            hashMap.put("140", "minecraft:ice_spikes");
            hashMap.put("149", "minecraft:modified_jungle");
            hashMap.put("151", "minecraft:modified_jungle_edge");
            hashMap.put("155", "minecraft:tall_birch_forest");
            hashMap.put("156", "minecraft:tall_birch_hills");
            hashMap.put("157", "minecraft:dark_forest_hills");
            hashMap.put("158", "minecraft:snowy_taiga_mountains");
            hashMap.put("160", "minecraft:giant_spruce_taiga");
            hashMap.put("161", "minecraft:giant_spruce_taiga_hills");
            hashMap.put("162", "minecraft:modified_gravelly_mountains");
            hashMap.put("163", "minecraft:shattered_savanna");
            hashMap.put("164", "minecraft:shattered_savanna_plateau");
            hashMap.put("165", "minecraft:eroded_badlands");
            hashMap.put("166", "minecraft:modified_wooded_badlands_plateau");
            hashMap.put("167", "minecraft:modified_badlands_plateau");
        });
    }
}
