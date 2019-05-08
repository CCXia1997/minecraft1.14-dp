package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.Typed;
import java.util.Objects;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import java.util.Optional;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class ItemOminousBannerRenameFix extends DataFix
{
    public ItemOminousBannerRenameFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    private Dynamic<?> a(final Dynamic<?> dynamic) {
        final Optional<? extends Dynamic<?>> optional2 = dynamic.get("display").get();
        if (optional2.isPresent()) {
            Dynamic<?> dynamic2 = optional2.get();
            final Optional<String> optional3 = (Optional<String>)dynamic2.get("Name").asString();
            if (optional3.isPresent()) {
                String string5 = optional3.get();
                string5 = string5.replace("\"translate\":\"block.minecraft.illager_banner\"", "\"translate\":\"block.minecraft.ominous_banner\"");
                dynamic2 = dynamic2.set("Name", dynamic2.createString(string5));
            }
            return dynamic.set("display", (Dynamic)dynamic2);
        }
        return dynamic;
    }
    
    public TypeRewriteRule makeRule() {
        final Type<?> type1 = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
        final OpticFinder<Pair<String, String>> opticFinder2 = (OpticFinder<Pair<String, String>>)DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
        final OpticFinder<?> opticFinder3 = type1.findField("tag");
        final OpticFinder opticFinder4;
        final Optional<Pair<String, String>> optional4;
        final OpticFinder opticFinder5;
        Optional<? extends Typed<?>> optional5;
        Typed<?> typed2;
        Dynamic<?> dynamic7;
        return this.fixTypeEverywhereTyped("OminousBannerRenameFix", (Type)type1, typed -> {
            optional4 = (Optional<Pair<String, String>>)typed.getOptional(opticFinder4);
            if (optional4.isPresent() && Objects.equals(optional4.get().getSecond(), "minecraft:white_banner")) {
                optional5 = typed.getOptionalTyped(opticFinder5);
                if (optional5.isPresent()) {
                    typed2 = optional5.get();
                    dynamic7 = typed2.get(DSL.remainderFinder());
                    return typed.set(opticFinder5, typed2.set(DSL.remainderFinder(), this.a(dynamic7)));
                }
            }
            return typed;
        });
    }
}
