package net.minecraft.datafixers.fixes;

import java.util.Optional;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import java.util.Objects;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class ItemShulkerBoxColorFix extends DataFix
{
    public static final String[] COLORED_SHULKER_BOX_IDS;
    
    public ItemShulkerBoxColorFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    public TypeRewriteRule makeRule() {
        final Type<?> type1 = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
        final OpticFinder<Pair<String, String>> opticFinder2 = (OpticFinder<Pair<String, String>>)DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
        final OpticFinder<?> opticFinder3 = type1.findField("tag");
        final OpticFinder<?> opticFinder4 = opticFinder3.type().findField("BlockEntityTag");
        final OpticFinder opticFinder5;
        final Optional<Pair<String, String>> optional5;
        final OpticFinder opticFinder6;
        Optional<? extends Typed<?>> optional6;
        Typed<?> typed2;
        final OpticFinder opticFinder7;
        Optional<? extends Typed<?>> optional7;
        Typed<?> typed3;
        Dynamic<?> dynamic10;
        int integer11;
        return this.fixTypeEverywhereTyped("ItemShulkerBoxColorFix", (Type)type1, typed -> {
            optional5 = (Optional<Pair<String, String>>)typed.getOptional(opticFinder5);
            if (optional5.isPresent() && Objects.equals(optional5.get().getSecond(), "minecraft:shulker_box")) {
                optional6 = typed.getOptionalTyped(opticFinder6);
                if (optional6.isPresent()) {
                    typed2 = optional6.get();
                    optional7 = typed2.getOptionalTyped(opticFinder7);
                    if (optional7.isPresent()) {
                        typed3 = optional7.get();
                        dynamic10 = typed3.get(DSL.remainderFinder());
                        integer11 = dynamic10.get("Color").asInt(0);
                        dynamic10.remove("Color");
                        return typed.set(opticFinder6, typed2.set(opticFinder7, typed3.set(DSL.remainderFinder(), dynamic10))).set(opticFinder5, Pair.of((Object)TypeReferences.ITEM_NAME.typeName(), (Object)ItemShulkerBoxColorFix.COLORED_SHULKER_BOX_IDS[integer11 % 16]));
                    }
                }
            }
            return typed;
        });
    }
    
    static {
        COLORED_SHULKER_BOX_IDS = new String[] { "minecraft:white_shulker_box", "minecraft:orange_shulker_box", "minecraft:magenta_shulker_box", "minecraft:light_blue_shulker_box", "minecraft:yellow_shulker_box", "minecraft:lime_shulker_box", "minecraft:pink_shulker_box", "minecraft:gray_shulker_box", "minecraft:silver_shulker_box", "minecraft:cyan_shulker_box", "minecraft:purple_shulker_box", "minecraft:blue_shulker_box", "minecraft:brown_shulker_box", "minecraft:green_shulker_box", "minecraft:red_shulker_box", "minecraft:black_shulker_box" };
    }
}
