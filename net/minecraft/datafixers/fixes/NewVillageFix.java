package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.Typed;
import net.minecraft.datafixers.schemas.SchemaIdentifierNormalize;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Objects;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.types.templates.CompoundList;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class NewVillageFix extends DataFix
{
    public NewVillageFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    protected TypeRewriteRule makeRule() {
        final CompoundList.CompoundListType<String, ?> compoundListType1 = DSL.compoundList(DSL.string(), this.getInputSchema().getType(TypeReferences.STRUCTURE_FEATURE));
        final OpticFinder<? extends List<? extends Pair<String, ?>>> opticFinder2 = compoundListType1.finder();
        return this.a(compoundListType1);
    }
    
    private <SF> TypeRewriteRule a(final CompoundList.CompoundListType<String, SF> compoundListType) {
        final Type<?> type2 = this.getInputSchema().getType(TypeReferences.CHUNK);
        final Type<?> type3 = this.getInputSchema().getType(TypeReferences.STRUCTURE_FEATURE);
        final OpticFinder<?> opticFinder4 = type2.findField("Level");
        final OpticFinder<?> opticFinder5 = opticFinder4.type().findField("Structures");
        final OpticFinder<?> opticFinder6 = opticFinder5.type().findField("Starts");
        final OpticFinder<List<Pair<String, SF>>> opticFinder7 = (OpticFinder<List<Pair<String, SF>>>)compoundListType.finder();
        final Optional<? extends Dynamic<?>> optional2;
        return TypeRewriteRule.seq(this.fixTypeEverywhereTyped("NewVillageFix", (Type)type2, typed -> typed.updateTyped((OpticFinder)opticFinder4, typed -> typed.updateTyped((OpticFinder)opticFinder5, typed -> typed.updateTyped((OpticFinder)opticFinder6, typed -> typed.update((OpticFinder)opticFinder7, list -> list.stream().filter(pair -> !Objects.equals(pair.getFirst(), "Village")).map(pair -> pair.mapFirst(string -> string.equals("New_Village") ? "Village" : string)).collect(Collectors.toList()))).update(DSL.remainderFinder(), dynamic -> dynamic.update("References", dynamic -> {
            optional2 = dynamic.get("New_Village").get();
            return ((Dynamic)DataFixUtils.orElse((Optional)optional2.map(dynamic2 -> dynamic.remove("New_Village").merge(dynamic.createString("Village"), dynamic2)), dynamic)).remove("Village");
        }))))), this.fixTypeEverywhereTyped("NewVillageStartFix", (Type)type3, typed -> typed.update(DSL.remainderFinder(), dynamic -> dynamic.update("id", dynamic -> Objects.equals(SchemaIdentifierNormalize.normalize(dynamic.asString("")), "minecraft:new_village") ? dynamic.createString("minecraft:village") : dynamic))));
    }
}
