package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DataFixUtils;
import java.util.Optional;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.Dynamic;
import java.util.Objects;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class ItemPotionFix extends DataFix
{
    private static final String[] ID_TO_POTIONS;
    
    public ItemPotionFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    public TypeRewriteRule makeRule() {
        final Type<?> type1 = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
        final OpticFinder<Pair<String, String>> opticFinder2 = (OpticFinder<Pair<String, String>>)DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
        final OpticFinder<?> opticFinder3 = type1.findField("tag");
        final OpticFinder opticFinder4;
        final Optional<Pair<String, String>> optional4;
        Dynamic<?> dynamic5;
        final OpticFinder opticFinder5;
        Optional<? extends Typed<?>> optional5;
        short short7;
        Typed<?> typed2;
        Dynamic<?> dynamic6;
        Optional<String> optional6;
        String string11;
        Typed<?> typed3;
        return this.fixTypeEverywhereTyped("ItemPotionFix", (Type)type1, typed -> {
            optional4 = (Optional<Pair<String, String>>)typed.getOptional(opticFinder4);
            if (optional4.isPresent() && Objects.equals(optional4.get().getSecond(), "minecraft:potion")) {
                dynamic5 = typed.get(DSL.remainderFinder());
                optional5 = typed.getOptionalTyped(opticFinder5);
                short7 = dynamic5.get("Damage").asShort((short)0);
                if (optional5.isPresent()) {
                    typed2 = typed;
                    dynamic6 = ((Typed)optional5.get()).get(DSL.remainderFinder());
                    optional6 = (Optional<String>)dynamic6.get("Potion").asString();
                    if (!optional6.isPresent()) {
                        string11 = ItemPotionFix.ID_TO_POTIONS[short7 & 0x7F];
                        typed3 = ((Typed)optional5.get()).set(DSL.remainderFinder(), dynamic6.set("Potion", dynamic6.createString((string11 == null) ? "minecraft:water" : string11)));
                        typed2 = typed2.set(opticFinder5, (Typed)typed3);
                        if ((short7 & 0x4000) == 0x4000) {
                            typed2 = typed2.set(opticFinder4, Pair.of((Object)TypeReferences.ITEM_NAME.typeName(), (Object)"minecraft:splash_potion"));
                        }
                    }
                    if (short7 != 0) {
                        dynamic5 = dynamic5.set("Damage", dynamic5.createShort((short)0));
                    }
                    return typed2.set(DSL.remainderFinder(), dynamic5);
                }
            }
            return typed;
        });
    }
    
    static {
        ID_TO_POTIONS = (String[])DataFixUtils.make(new String[128], arr -> {
            arr[0] = "minecraft:water";
            arr[1] = "minecraft:regeneration";
            arr[2] = "minecraft:swiftness";
            arr[3] = "minecraft:fire_resistance";
            arr[4] = "minecraft:poison";
            arr[5] = "minecraft:healing";
            arr[6] = "minecraft:night_vision";
            arr[7] = null;
            arr[8] = "minecraft:weakness";
            arr[9] = "minecraft:strength";
            arr[10] = "minecraft:slowness";
            arr[11] = "minecraft:leaping";
            arr[12] = "minecraft:harming";
            arr[13] = "minecraft:water_breathing";
            arr[14] = "minecraft:invisibility";
            arr[15] = null;
            arr[16] = "minecraft:awkward";
            arr[17] = "minecraft:regeneration";
            arr[18] = "minecraft:swiftness";
            arr[19] = "minecraft:fire_resistance";
            arr[20] = "minecraft:poison";
            arr[21] = "minecraft:healing";
            arr[22] = "minecraft:night_vision";
            arr[23] = null;
            arr[24] = "minecraft:weakness";
            arr[25] = "minecraft:strength";
            arr[26] = "minecraft:slowness";
            arr[27] = "minecraft:leaping";
            arr[28] = "minecraft:harming";
            arr[29] = "minecraft:water_breathing";
            arr[30] = "minecraft:invisibility";
            arr[31] = null;
            arr[32] = "minecraft:thick";
            arr[33] = "minecraft:strong_regeneration";
            arr[34] = "minecraft:strong_swiftness";
            arr[35] = "minecraft:fire_resistance";
            arr[36] = "minecraft:strong_poison";
            arr[37] = "minecraft:strong_healing";
            arr[38] = "minecraft:night_vision";
            arr[39] = null;
            arr[40] = "minecraft:weakness";
            arr[41] = "minecraft:strong_strength";
            arr[42] = "minecraft:slowness";
            arr[43] = "minecraft:strong_leaping";
            arr[44] = "minecraft:strong_harming";
            arr[45] = "minecraft:water_breathing";
            arr[46] = "minecraft:invisibility";
            arr[48] = (arr[47] = null);
            arr[49] = "minecraft:strong_regeneration";
            arr[50] = "minecraft:strong_swiftness";
            arr[51] = "minecraft:fire_resistance";
            arr[52] = "minecraft:strong_poison";
            arr[53] = "minecraft:strong_healing";
            arr[54] = "minecraft:night_vision";
            arr[55] = null;
            arr[56] = "minecraft:weakness";
            arr[57] = "minecraft:strong_strength";
            arr[58] = "minecraft:slowness";
            arr[59] = "minecraft:strong_leaping";
            arr[60] = "minecraft:strong_harming";
            arr[61] = "minecraft:water_breathing";
            arr[62] = "minecraft:invisibility";
            arr[63] = null;
            arr[64] = "minecraft:mundane";
            arr[65] = "minecraft:long_regeneration";
            arr[66] = "minecraft:long_swiftness";
            arr[67] = "minecraft:long_fire_resistance";
            arr[68] = "minecraft:long_poison";
            arr[69] = "minecraft:healing";
            arr[70] = "minecraft:long_night_vision";
            arr[71] = null;
            arr[72] = "minecraft:long_weakness";
            arr[73] = "minecraft:long_strength";
            arr[74] = "minecraft:long_slowness";
            arr[75] = "minecraft:long_leaping";
            arr[76] = "minecraft:harming";
            arr[77] = "minecraft:long_water_breathing";
            arr[78] = "minecraft:long_invisibility";
            arr[79] = null;
            arr[80] = "minecraft:awkward";
            arr[81] = "minecraft:long_regeneration";
            arr[82] = "minecraft:long_swiftness";
            arr[83] = "minecraft:long_fire_resistance";
            arr[84] = "minecraft:long_poison";
            arr[85] = "minecraft:healing";
            arr[86] = "minecraft:long_night_vision";
            arr[87] = null;
            arr[88] = "minecraft:long_weakness";
            arr[89] = "minecraft:long_strength";
            arr[90] = "minecraft:long_slowness";
            arr[91] = "minecraft:long_leaping";
            arr[92] = "minecraft:harming";
            arr[93] = "minecraft:long_water_breathing";
            arr[94] = "minecraft:long_invisibility";
            arr[95] = null;
            arr[96] = "minecraft:thick";
            arr[97] = "minecraft:regeneration";
            arr[98] = "minecraft:swiftness";
            arr[99] = "minecraft:long_fire_resistance";
            arr[100] = "minecraft:poison";
            arr[101] = "minecraft:strong_healing";
            arr[102] = "minecraft:long_night_vision";
            arr[103] = null;
            arr[104] = "minecraft:long_weakness";
            arr[105] = "minecraft:strength";
            arr[106] = "minecraft:long_slowness";
            arr[107] = "minecraft:leaping";
            arr[108] = "minecraft:strong_harming";
            arr[109] = "minecraft:long_water_breathing";
            arr[110] = "minecraft:long_invisibility";
            arr[112] = (arr[111] = null);
            arr[113] = "minecraft:regeneration";
            arr[114] = "minecraft:swiftness";
            arr[115] = "minecraft:long_fire_resistance";
            arr[116] = "minecraft:poison";
            arr[117] = "minecraft:strong_healing";
            arr[118] = "minecraft:long_night_vision";
            arr[119] = null;
            arr[120] = "minecraft:long_weakness";
            arr[121] = "minecraft:strength";
            arr[122] = "minecraft:long_slowness";
            arr[123] = "minecraft:leaping";
            arr[124] = "minecraft:strong_harming";
            arr[125] = "minecraft:long_water_breathing";
            arr[126] = "minecraft:long_invisibility";
            arr[127] = null;
        });
    }
}
