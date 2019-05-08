package net.minecraft.datafixers.fixes;

import java.util.Objects;
import java.util.function.Function;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.types.templates.List;
import com.mojang.datafixers.Typed;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.schemas.Schema;

public class VillagerTradeFix extends ChoiceFix
{
    public VillagerTradeFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2, "Villager trade fix", TypeReferences.ENTITY, "minecraft:villager");
    }
    
    @Override
    protected Typed<?> transform(final Typed<?> typed) {
        final OpticFinder<?> opticFinder2 = typed.getType().findField("Offers");
        final OpticFinder<?> opticFinder3 = opticFinder2.type().findField("Recipes");
        final Type<?> type4 = opticFinder3.type();
        if (!(type4 instanceof List.ListType)) {
            throw new IllegalStateException("Recipes are expected to be a list.");
        }
        final List.ListType<?> listType5 = type4;
        final Type<?> type5 = listType5.getElement();
        final OpticFinder<?> opticFinder4 = DSL.typeFinder((Type)type5);
        final OpticFinder<?> opticFinder5 = type5.findField("buy");
        final OpticFinder<?> opticFinder6 = type5.findField("buyB");
        final OpticFinder<?> opticFinder7 = type5.findField("sell");
        final OpticFinder<Pair<String, String>> opticFinder8 = (OpticFinder<Pair<String, String>>)DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
        final Function<Typed<?>, Typed<?>> function12 = (Function<Typed<?>, Typed<?>>)(typed -> this.a(opticFinder8, typed));
        final OpticFinder opticFinder9;
        final Function function13;
        return typed.updateTyped((OpticFinder)opticFinder2, typed -> typed.updateTyped((OpticFinder)opticFinder3, typed -> typed.updateTyped((OpticFinder)opticFinder4, typed -> typed.updateTyped(opticFinder9, function13).updateTyped((OpticFinder)opticFinder6, function13).updateTyped((OpticFinder)opticFinder7, function13))));
    }
    
    private Typed<?> a(final OpticFinder<Pair<String, String>> opticFinder, final Typed<?> typed) {
        return typed.update((OpticFinder)opticFinder, pair -> pair.mapSecond(string -> Objects.equals(string, "minecraft:carved_pumpkin") ? "minecraft:pumpkin" : string));
    }
}
