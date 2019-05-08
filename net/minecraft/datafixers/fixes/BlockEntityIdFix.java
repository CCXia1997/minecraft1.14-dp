package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DataFixUtils;
import com.google.common.collect.Maps;
import java.util.HashMap;
import com.mojang.datafixers.util.Pair;
import java.util.function.Function;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.types.Type;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import com.mojang.datafixers.DataFix;

public class BlockEntityIdFix extends DataFix
{
    private static final Map<String, String> RENAMED_BLOCK_ENTITIES;
    
    public BlockEntityIdFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    public TypeRewriteRule makeRule() {
        final Type<?> type1 = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
        final Type<?> type2 = this.getOutputSchema().getType(TypeReferences.ITEM_STACK);
        final TaggedChoice.TaggedChoiceType<String> taggedChoiceType3 = (TaggedChoice.TaggedChoiceType<String>)this.getInputSchema().findChoiceType(TypeReferences.BLOCK_ENTITY);
        final TaggedChoice.TaggedChoiceType<String> taggedChoiceType4 = (TaggedChoice.TaggedChoiceType<String>)this.getOutputSchema().findChoiceType(TypeReferences.BLOCK_ENTITY);
        return TypeRewriteRule.seq(this.convertUnchecked("item stack block entity name hook converter", (Type)type1, (Type)type2), this.fixTypeEverywhere("BlockEntityIdFix", (Type)taggedChoiceType3, (Type)taggedChoiceType4, dynamicOps -> pair -> pair.mapFirst(string -> BlockEntityIdFix.RENAMED_BLOCK_ENTITIES.getOrDefault(string, string))));
    }
    
    static {
        RENAMED_BLOCK_ENTITIES = (Map)DataFixUtils.make(Maps.newHashMap(), hashMap -> {
            hashMap.put("Airportal", "minecraft:end_portal");
            hashMap.put("Banner", "minecraft:banner");
            hashMap.put("Beacon", "minecraft:beacon");
            hashMap.put("Cauldron", "minecraft:brewing_stand");
            hashMap.put("Chest", "minecraft:chest");
            hashMap.put("Comparator", "minecraft:comparator");
            hashMap.put("Control", "minecraft:command_block");
            hashMap.put("DLDetector", "minecraft:daylight_detector");
            hashMap.put("Dropper", "minecraft:dropper");
            hashMap.put("EnchantTable", "minecraft:enchanting_table");
            hashMap.put("EndGateway", "minecraft:end_gateway");
            hashMap.put("EnderChest", "minecraft:ender_chest");
            hashMap.put("FlowerPot", "minecraft:flower_pot");
            hashMap.put("Furnace", "minecraft:furnace");
            hashMap.put("Hopper", "minecraft:hopper");
            hashMap.put("MobSpawner", "minecraft:mob_spawner");
            hashMap.put("Music", "minecraft:noteblock");
            hashMap.put("Piston", "minecraft:piston");
            hashMap.put("RecordPlayer", "minecraft:jukebox");
            hashMap.put("Sign", "minecraft:sign");
            hashMap.put("Skull", "minecraft:skull");
            hashMap.put("Structure", "minecraft:structure_block");
            hashMap.put("Trap", "minecraft:dispenser");
        });
    }
}
