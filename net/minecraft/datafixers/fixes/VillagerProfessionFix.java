package net.minecraft.datafixers.fixes;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.schemas.Schema;

public class VillagerProfessionFix extends ChoiceFix
{
    public VillagerProfessionFix(final Schema outputSchema, final String string) {
        super(outputSchema, false, "Villager profession data fix (" + string + ")", TypeReferences.ENTITY, string);
    }
    
    @Override
    protected Typed<?> transform(final Typed<?> typed) {
        final Dynamic<?> dynamic2 = typed.get(DSL.remainderFinder());
        return typed.set(DSL.remainderFinder(), dynamic2.remove("Profession").remove("Career").remove("CareerLevel").set("VillagerData", dynamic2.createMap((Map)ImmutableMap.<Dynamic, Object>of(dynamic2.createString("type"), dynamic2.createString("minecraft:plains"), dynamic2.createString("profession"), dynamic2.createString(a(dynamic2.get("Profession").asInt(0), dynamic2.get("Career").asInt(0))), dynamic2.createString("level"), DataFixUtils.orElse(dynamic2.get("CareerLevel").get(), (Object)dynamic2.createInt(1))))));
    }
    
    private static String a(final int professionId, final int careerId) {
        if (professionId == 0) {
            if (careerId == 2) {
                return "minecraft:fisherman";
            }
            if (careerId == 3) {
                return "minecraft:shepherd";
            }
            if (careerId == 4) {
                return "minecraft:fletcher";
            }
            return "minecraft:farmer";
        }
        else if (professionId == 1) {
            if (careerId == 2) {
                return "minecraft:cartographer";
            }
            return "minecraft:librarian";
        }
        else {
            if (professionId == 2) {
                return "minecraft:cleric";
            }
            if (professionId == 3) {
                if (careerId == 2) {
                    return "minecraft:weaponsmith";
                }
                if (careerId == 3) {
                    return "minecraft:toolsmith";
                }
                return "minecraft:armorer";
            }
            else if (professionId == 4) {
                if (careerId == 2) {
                    return "minecraft:leatherworker";
                }
                return "minecraft:butcher";
            }
            else {
                if (professionId == 5) {
                    return "minecraft:nitwit";
                }
                return "minecraft:none";
            }
        }
    }
}
