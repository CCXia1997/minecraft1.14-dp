package net.minecraft.datafixers.fixes;

import java.util.Optional;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.types.Type;
import java.util.stream.Stream;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.Dynamic;
import java.util.Objects;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class ItemBannerColorFix extends DataFix
{
    public ItemBannerColorFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    public TypeRewriteRule makeRule() {
        final Type<?> type1 = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
        final OpticFinder<Pair<String, String>> opticFinder2 = (OpticFinder<Pair<String, String>>)DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
        final OpticFinder<?> opticFinder3 = type1.findField("tag");
        final OpticFinder<?> opticFinder4 = opticFinder3.type().findField("BlockEntityTag");
        final OpticFinder opticFinder5;
        final Optional<Pair<String, String>> optional5;
        Dynamic<?> dynamic6;
        final OpticFinder opticFinder6;
        Optional<? extends Typed<?>> optional6;
        Typed<?> typed2;
        final OpticFinder opticFinder7;
        Optional<? extends Typed<?>> optional7;
        Typed<?> typed3;
        Dynamic<?> dynamic7;
        Dynamic<?> dynamic8;
        Dynamic<?> dynamic9;
        Optional<? extends Dynamic<?>> optional8;
        Dynamic<?> dynamic10;
        return this.fixTypeEverywhereTyped("ItemBannerColorFix", (Type)type1, typed -> {
            optional5 = (Optional<Pair<String, String>>)typed.getOptional(opticFinder5);
            if (optional5.isPresent() && Objects.equals(optional5.get().getSecond(), "minecraft:banner")) {
                dynamic6 = typed.get(DSL.remainderFinder());
                optional6 = typed.getOptionalTyped(opticFinder6);
                if (optional6.isPresent()) {
                    typed2 = optional6.get();
                    optional7 = typed2.getOptionalTyped(opticFinder7);
                    if (optional7.isPresent()) {
                        typed3 = optional7.get();
                        dynamic7 = typed2.get(DSL.remainderFinder());
                        dynamic8 = typed3.getOrCreate(DSL.remainderFinder());
                        if (dynamic8.get("Base").asNumber().isPresent()) {
                            dynamic9 = dynamic6.set("Damage", dynamic6.createShort((short)(dynamic8.get("Base").asInt(0) & 0xF)));
                            optional8 = dynamic7.get("display").get();
                            if (optional8.isPresent()) {
                                dynamic10 = optional8.get();
                                if (Objects.equals(dynamic10, dynamic10.emptyMap().merge(dynamic10.createString("Lore"), dynamic10.createList((Stream)Stream.<Dynamic>of(dynamic10.createString("(+NBT")))))) {
                                    return typed.set(DSL.remainderFinder(), dynamic9);
                                }
                            }
                            dynamic8.remove("Base");
                            return typed.set(DSL.remainderFinder(), dynamic9).set(opticFinder6, typed2.set(opticFinder7, typed3.set(DSL.remainderFinder(), dynamic8)));
                        }
                    }
                }
                return typed.set(DSL.remainderFinder(), dynamic6);
            }
            else {
                return typed;
            }
        });
    }
}
