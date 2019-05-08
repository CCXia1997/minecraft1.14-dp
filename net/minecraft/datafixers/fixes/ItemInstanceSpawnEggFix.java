package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DataFixUtils;
import com.google.common.collect.Maps;
import java.util.HashMap;
import com.mojang.datafixers.Typed;
import java.util.Optional;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.types.Type;
import java.util.Objects;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import com.mojang.datafixers.DataFix;

public class ItemInstanceSpawnEggFix extends DataFix
{
    private static final Map<String, String> ENTITY_SPAWN_EGGS;
    
    public ItemInstanceSpawnEggFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    public TypeRewriteRule makeRule() {
        final Type<?> type1 = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
        final OpticFinder<Pair<String, String>> opticFinder2 = (OpticFinder<Pair<String, String>>)DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
        final OpticFinder<String> opticFinder3 = (OpticFinder<String>)DSL.fieldFinder("id", DSL.namespacedString());
        final OpticFinder<?> opticFinder4 = type1.findField("tag");
        final OpticFinder<?> opticFinder5 = opticFinder4.type().findField("EntityTag");
        final OpticFinder opticFinder6;
        final Optional<Pair<String, String>> optional6;
        final OpticFinder opticFinder7;
        Typed<?> typed2;
        final OpticFinder opticFinder8;
        Typed<?> typed3;
        final OpticFinder opticFinder9;
        Optional<String> optional7;
        return this.fixTypeEverywhereTyped("ItemInstanceSpawnEggFix", (Type)type1, typed -> {
            optional6 = (Optional<Pair<String, String>>)typed.getOptional(opticFinder6);
            if (optional6.isPresent() && Objects.equals(optional6.get().getSecond(), "minecraft:spawn_egg")) {
                typed2 = typed.getOrCreateTyped(opticFinder7);
                typed3 = typed2.getOrCreateTyped(opticFinder8);
                optional7 = (Optional<String>)typed3.getOptional(opticFinder9);
                if (optional7.isPresent()) {
                    return typed.set(opticFinder6, Pair.of((Object)TypeReferences.ITEM_NAME.typeName(), (Object)ItemInstanceSpawnEggFix.ENTITY_SPAWN_EGGS.getOrDefault(optional7.get(), "minecraft:pig_spawn_egg")));
                }
            }
            return typed;
        });
    }
    
    static {
        ENTITY_SPAWN_EGGS = (Map)DataFixUtils.make(Maps.newHashMap(), hashMap -> {
            hashMap.put("minecraft:bat", "minecraft:bat_spawn_egg");
            hashMap.put("minecraft:blaze", "minecraft:blaze_spawn_egg");
            hashMap.put("minecraft:cave_spider", "minecraft:cave_spider_spawn_egg");
            hashMap.put("minecraft:chicken", "minecraft:chicken_spawn_egg");
            hashMap.put("minecraft:cow", "minecraft:cow_spawn_egg");
            hashMap.put("minecraft:creeper", "minecraft:creeper_spawn_egg");
            hashMap.put("minecraft:donkey", "minecraft:donkey_spawn_egg");
            hashMap.put("minecraft:elder_guardian", "minecraft:elder_guardian_spawn_egg");
            hashMap.put("minecraft:enderman", "minecraft:enderman_spawn_egg");
            hashMap.put("minecraft:endermite", "minecraft:endermite_spawn_egg");
            hashMap.put("minecraft:evocation_illager", "minecraft:evocation_illager_spawn_egg");
            hashMap.put("minecraft:ghast", "minecraft:ghast_spawn_egg");
            hashMap.put("minecraft:guardian", "minecraft:guardian_spawn_egg");
            hashMap.put("minecraft:horse", "minecraft:horse_spawn_egg");
            hashMap.put("minecraft:husk", "minecraft:husk_spawn_egg");
            hashMap.put("minecraft:llama", "minecraft:llama_spawn_egg");
            hashMap.put("minecraft:magma_cube", "minecraft:magma_cube_spawn_egg");
            hashMap.put("minecraft:mooshroom", "minecraft:mooshroom_spawn_egg");
            hashMap.put("minecraft:mule", "minecraft:mule_spawn_egg");
            hashMap.put("minecraft:ocelot", "minecraft:ocelot_spawn_egg");
            hashMap.put("minecraft:pufferfish", "minecraft:pufferfish_spawn_egg");
            hashMap.put("minecraft:parrot", "minecraft:parrot_spawn_egg");
            hashMap.put("minecraft:pig", "minecraft:pig_spawn_egg");
            hashMap.put("minecraft:polar_bear", "minecraft:polar_bear_spawn_egg");
            hashMap.put("minecraft:rabbit", "minecraft:rabbit_spawn_egg");
            hashMap.put("minecraft:sheep", "minecraft:sheep_spawn_egg");
            hashMap.put("minecraft:shulker", "minecraft:shulker_spawn_egg");
            hashMap.put("minecraft:silverfish", "minecraft:silverfish_spawn_egg");
            hashMap.put("minecraft:skeleton", "minecraft:skeleton_spawn_egg");
            hashMap.put("minecraft:skeleton_horse", "minecraft:skeleton_horse_spawn_egg");
            hashMap.put("minecraft:slime", "minecraft:slime_spawn_egg");
            hashMap.put("minecraft:spider", "minecraft:spider_spawn_egg");
            hashMap.put("minecraft:squid", "minecraft:squid_spawn_egg");
            hashMap.put("minecraft:stray", "minecraft:stray_spawn_egg");
            hashMap.put("minecraft:turtle", "minecraft:turtle_spawn_egg");
            hashMap.put("minecraft:vex", "minecraft:vex_spawn_egg");
            hashMap.put("minecraft:villager", "minecraft:villager_spawn_egg");
            hashMap.put("minecraft:vindication_illager", "minecraft:vindication_illager_spawn_egg");
            hashMap.put("minecraft:witch", "minecraft:witch_spawn_egg");
            hashMap.put("minecraft:wither_skeleton", "minecraft:wither_skeleton_spawn_egg");
            hashMap.put("minecraft:wolf", "minecraft:wolf_spawn_egg");
            hashMap.put("minecraft:zombie", "minecraft:zombie_spawn_egg");
            hashMap.put("minecraft:zombie_horse", "minecraft:zombie_horse_spawn_egg");
            hashMap.put("minecraft:zombie_pigman", "minecraft:zombie_pigman_spawn_egg");
            hashMap.put("minecraft:zombie_villager", "minecraft:zombie_villager_spawn_egg");
        });
    }
}
