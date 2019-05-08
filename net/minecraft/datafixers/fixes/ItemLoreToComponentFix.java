package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.Typed;
import net.minecraft.text.TextComponent;
import net.minecraft.text.StringTextComponent;
import java.util.stream.Stream;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.OpticFinder;
import java.util.Optional;
import com.mojang.datafixers.DataFixUtils;
import java.util.function.Function;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class ItemLoreToComponentFix extends DataFix
{
    public ItemLoreToComponentFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    protected TypeRewriteRule makeRule() {
        final Type<?> type1 = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
        final OpticFinder<?> opticFinder2 = type1.findField("tag");
        return this.fixTypeEverywhereTyped("Item Lore componentize", (Type)type1, typed -> typed.updateTyped((OpticFinder)opticFinder2, typed -> typed.update(DSL.remainderFinder(), dynamic -> dynamic.update("display", dynamic -> dynamic.update("Lore", dynamic -> (Dynamic)DataFixUtils.orElse((Optional)((Dynamic)dynamic).asStreamOpt().map(ItemLoreToComponentFix::a).map(dynamic::createList), dynamic))))));
    }
    
    private static <T> Stream<Dynamic<T>> a(final Stream<Dynamic<T>> stream) {
        return stream.<Dynamic<T>>map(dynamic -> (Dynamic)DataFixUtils.orElse((Optional)((Dynamic)dynamic).asString().map(ItemLoreToComponentFix::a).map(dynamic::createString), dynamic));
    }
    
    private static String a(final String string) {
        return TextComponent.Serializer.toJsonString(new StringTextComponent(string));
    }
}
