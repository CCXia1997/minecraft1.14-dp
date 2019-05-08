package net.minecraft.datafixers.fixes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import javax.annotation.Nullable;
import java.util.Iterator;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import com.mojang.datafixers.Typed;
import org.apache.commons.lang3.StringUtils;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import java.util.Set;
import com.mojang.datafixers.DataFix;

public class StatsCounterFix extends DataFix
{
    private static final Set<String> SKIP;
    private static final Map<String, String> RENAMED_GENERAL_STATS;
    private static final Map<String, String> RENAMED_ITEM_STATS;
    private static final Map<String, String> RENAMED_ENTITY_STATS;
    private static final Map<String, String> RENAMED_ENTITIES;
    
    public StatsCounterFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    public TypeRewriteRule makeRule() {
        final Type<?> type1 = this.getOutputSchema().getType(TypeReferences.STATS);
        final Dynamic<?> dynamic3;
        final Map<Dynamic<?>, Dynamic<?>> map4;
        final Optional<? extends Map<? extends Dynamic<?>, ? extends Dynamic<?>>> optional5;
        final Iterator<Map.Entry<? extends Dynamic<?>, ? extends Dynamic<?>>> iterator;
        Map.Entry<? extends Dynamic<?>, ? extends Dynamic<?>> entry7;
        String string8;
        String string9;
        String string10;
        int integer11;
        String string11;
        String string12;
        String string13;
        String string14;
        Dynamic<?> dynamic4;
        Dynamic<?> dynamic5;
        final Type type2;
        return this.fixTypeEverywhereTyped("StatsCounterFix", this.getInputSchema().getType(TypeReferences.STATS), (Type)type1, typed -> {
            dynamic3 = typed.get(DSL.remainderFinder());
            map4 = Maps.newHashMap();
            optional5 = dynamic3.getMapValues();
            if (optional5.isPresent()) {
                ((Map)optional5.get()).entrySet().iterator();
                while (iterator.hasNext()) {
                    entry7 = iterator.next();
                    if (((Dynamic)entry7.getValue()).asNumber().isPresent()) {
                        string8 = ((Dynamic)entry7.getKey()).asString("");
                        if (StatsCounterFix.SKIP.contains(string8)) {
                            continue;
                        }
                        else {
                            if (StatsCounterFix.RENAMED_GENERAL_STATS.containsKey(string8)) {
                                string9 = "minecraft:custom";
                                string10 = StatsCounterFix.RENAMED_GENERAL_STATS.get(string8);
                            }
                            else {
                                integer11 = StringUtils.ordinalIndexOf((CharSequence)string8, (CharSequence)".", 2);
                                if (integer11 < 0) {
                                    continue;
                                }
                                else {
                                    string11 = string8.substring(0, integer11);
                                    if ("stat.mineBlock".equals(string11)) {
                                        string9 = "minecraft:mined";
                                        string10 = this.getBlock(string8.substring(integer11 + 1).replace('.', ':'));
                                    }
                                    else if (StatsCounterFix.RENAMED_ITEM_STATS.containsKey(string11)) {
                                        string9 = StatsCounterFix.RENAMED_ITEM_STATS.get(string11);
                                        string12 = string8.substring(integer11 + 1).replace('.', ':');
                                        string13 = this.getItem(string12);
                                        string10 = ((string13 == null) ? string12 : string13);
                                    }
                                    else if (StatsCounterFix.RENAMED_ENTITY_STATS.containsKey(string11)) {
                                        string9 = StatsCounterFix.RENAMED_ENTITY_STATS.get(string11);
                                        string14 = string8.substring(integer11 + 1).replace('.', ':');
                                        string10 = StatsCounterFix.RENAMED_ENTITIES.getOrDefault(string14, string14);
                                    }
                                    else {
                                        continue;
                                    }
                                }
                            }
                            dynamic4 = dynamic3.createString(string9);
                            dynamic5 = map4.computeIfAbsent(dynamic4, dynamic2 -> dynamic3.emptyMap());
                            map4.put(dynamic4, dynamic5.set(string10, (Dynamic)entry7.getValue()));
                        }
                    }
                }
            }
            return (Typed)((Optional)type2.readTyped(dynamic3.emptyMap().set("stats", dynamic3.createMap((Map)map4))).getSecond()).orElseThrow(() -> new IllegalStateException("Could not parse new stats object."));
        });
    }
    
    @Nullable
    protected String getItem(final String string) {
        return ItemInstanceTheFlatteningFix.getItem(string, 0);
    }
    
    protected String getBlock(final String string) {
        return BlockStateFlattening.lookup(string);
    }
    
    static {
        SKIP = ImmutableSet.<String>builder().add("stat.craftItem.minecraft.spawn_egg").add("stat.useItem.minecraft.spawn_egg").add("stat.breakItem.minecraft.spawn_egg").add("stat.pickup.minecraft.spawn_egg").add("stat.drop.minecraft.spawn_egg").build();
        RENAMED_GENERAL_STATS = ImmutableMap.<String, String>builder().put("stat.leaveGame", "minecraft:leave_game").put("stat.playOneMinute", "minecraft:play_one_minute").put("stat.timeSinceDeath", "minecraft:time_since_death").put("stat.sneakTime", "minecraft:sneak_time").put("stat.walkOneCm", "minecraft:walk_one_cm").put("stat.crouchOneCm", "minecraft:crouch_one_cm").put("stat.sprintOneCm", "minecraft:sprint_one_cm").put("stat.swimOneCm", "minecraft:swim_one_cm").put("stat.fallOneCm", "minecraft:fall_one_cm").put("stat.climbOneCm", "minecraft:climb_one_cm").put("stat.flyOneCm", "minecraft:fly_one_cm").put("stat.diveOneCm", "minecraft:dive_one_cm").put("stat.minecartOneCm", "minecraft:minecart_one_cm").put("stat.boatOneCm", "minecraft:boat_one_cm").put("stat.pigOneCm", "minecraft:pig_one_cm").put("stat.horseOneCm", "minecraft:horse_one_cm").put("stat.aviateOneCm", "minecraft:aviate_one_cm").put("stat.jump", "minecraft:jump").put("stat.drop", "minecraft:drop").put("stat.damageDealt", "minecraft:damage_dealt").put("stat.damageTaken", "minecraft:damage_taken").put("stat.deaths", "minecraft:deaths").put("stat.mobKills", "minecraft:mob_kills").put("stat.animalsBred", "minecraft:animals_bred").put("stat.playerKills", "minecraft:player_kills").put("stat.fishCaught", "minecraft:fish_caught").put("stat.talkedToVillager", "minecraft:talked_to_villager").put("stat.tradedWithVillager", "minecraft:traded_with_villager").put("stat.cakeSlicesEaten", "minecraft:eat_cake_slice").put("stat.cauldronFilled", "minecraft:fill_cauldron").put("stat.cauldronUsed", "minecraft:use_cauldron").put("stat.armorCleaned", "minecraft:clean_armor").put("stat.bannerCleaned", "minecraft:clean_banner").put("stat.brewingstandInteraction", "minecraft:interact_with_brewingstand").put("stat.beaconInteraction", "minecraft:interact_with_beacon").put("stat.dropperInspected", "minecraft:inspect_dropper").put("stat.hopperInspected", "minecraft:inspect_hopper").put("stat.dispenserInspected", "minecraft:inspect_dispenser").put("stat.noteblockPlayed", "minecraft:play_noteblock").put("stat.noteblockTuned", "minecraft:tune_noteblock").put("stat.flowerPotted", "minecraft:pot_flower").put("stat.trappedChestTriggered", "minecraft:trigger_trapped_chest").put("stat.enderchestOpened", "minecraft:open_enderchest").put("stat.itemEnchanted", "minecraft:enchant_item").put("stat.recordPlayed", "minecraft:play_record").put("stat.furnaceInteraction", "minecraft:interact_with_furnace").put("stat.craftingTableInteraction", "minecraft:interact_with_crafting_table").put("stat.chestOpened", "minecraft:open_chest").put("stat.sleepInBed", "minecraft:sleep_in_bed").put("stat.shulkerBoxOpened", "minecraft:open_shulker_box").build();
        RENAMED_ITEM_STATS = ImmutableMap.<String, String>builder().put("stat.craftItem", "minecraft:crafted").put("stat.useItem", "minecraft:used").put("stat.breakItem", "minecraft:broken").put("stat.pickup", "minecraft:picked_up").put("stat.drop", "minecraft:dropped").build();
        RENAMED_ENTITY_STATS = ImmutableMap.<String, String>builder().put("stat.entityKilledBy", "minecraft:killed_by").put("stat.killEntity", "minecraft:killed").build();
        RENAMED_ENTITIES = ImmutableMap.<String, String>builder().put("Bat", "minecraft:bat").put("Blaze", "minecraft:blaze").put("CaveSpider", "minecraft:cave_spider").put("Chicken", "minecraft:chicken").put("Cow", "minecraft:cow").put("Creeper", "minecraft:creeper").put("Donkey", "minecraft:donkey").put("ElderGuardian", "minecraft:elder_guardian").put("Enderman", "minecraft:enderman").put("Endermite", "minecraft:endermite").put("EvocationIllager", "minecraft:evocation_illager").put("Ghast", "minecraft:ghast").put("Guardian", "minecraft:guardian").put("Horse", "minecraft:horse").put("Husk", "minecraft:husk").put("Llama", "minecraft:llama").put("LavaSlime", "minecraft:magma_cube").put("MushroomCow", "minecraft:mooshroom").put("Mule", "minecraft:mule").put("Ozelot", "minecraft:ocelot").put("Parrot", "minecraft:parrot").put("Pig", "minecraft:pig").put("PolarBear", "minecraft:polar_bear").put("Rabbit", "minecraft:rabbit").put("Sheep", "minecraft:sheep").put("Shulker", "minecraft:shulker").put("Silverfish", "minecraft:silverfish").put("SkeletonHorse", "minecraft:skeleton_horse").put("Skeleton", "minecraft:skeleton").put("Slime", "minecraft:slime").put("Spider", "minecraft:spider").put("Squid", "minecraft:squid").put("Stray", "minecraft:stray").put("Vex", "minecraft:vex").put("Villager", "minecraft:villager").put("VindicationIllager", "minecraft:vindication_illager").put("Witch", "minecraft:witch").put("WitherSkeleton", "minecraft:wither_skeleton").put("Wolf", "minecraft:wolf").put("ZombieHorse", "minecraft:zombie_horse").put("PigZombie", "minecraft:zombie_pigman").put("ZombieVillager", "minecraft:zombie_villager").put("Zombie", "minecraft:zombie").build();
    }
}
