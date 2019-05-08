package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.Typed;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.OpticFinder;
import java.util.function.Function;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import java.util.Optional;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.StringTextComponent;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class ItemCustomNameToComponentFix extends DataFix
{
    public ItemCustomNameToComponentFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    private Dynamic<?> a(final Dynamic<?> dynamic) {
        final Optional<? extends Dynamic<?>> optional2 = dynamic.get("display").get();
        if (optional2.isPresent()) {
            Dynamic<?> dynamic2 = optional2.get();
            final Optional<String> optional3 = (Optional<String>)dynamic2.get("Name").asString();
            if (optional3.isPresent()) {
                dynamic2 = dynamic2.set("Name", dynamic2.createString(TextComponent.Serializer.toJsonString(new StringTextComponent(optional3.get()))));
            }
            else {
                final Optional<String> optional4 = (Optional<String>)dynamic2.get("LocName").asString();
                if (optional4.isPresent()) {
                    dynamic2 = dynamic2.set("Name", dynamic2.createString(TextComponent.Serializer.toJsonString(new TranslatableTextComponent(optional4.get(), new Object[0]))));
                    dynamic2 = dynamic2.remove("LocName");
                }
            }
            return dynamic.set("display", (Dynamic)dynamic2);
        }
        return dynamic;
    }
    
    public TypeRewriteRule makeRule() {
        final Type<?> type1 = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
        final OpticFinder<?> opticFinder2 = type1.findField("tag");
        return this.fixTypeEverywhereTyped("ItemCustomNameToComponentFix", (Type)type1, typed -> typed.updateTyped((OpticFinder)opticFinder2, typed -> typed.update(DSL.remainderFinder(), (Function)this::a)));
    }
}
