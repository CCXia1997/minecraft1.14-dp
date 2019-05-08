package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import java.util.Optional;
import java.util.stream.Stream;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class MobSpawnerEntityIdentifiersFix extends DataFix
{
    public MobSpawnerEntityIdentifiersFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    private Dynamic<?> a(Dynamic<?> dynamic) {
        if (!"MobSpawner".equals(dynamic.get("id").asString(""))) {
            return dynamic;
        }
        final Optional<String> optional2 = (Optional<String>)dynamic.get("EntityId").asString();
        if (optional2.isPresent()) {
            Dynamic<?> dynamic2 = DataFixUtils.orElse(dynamic.get("SpawnData").get(), dynamic.emptyMap());
            dynamic2 = dynamic2.set("id", dynamic2.createString(optional2.get().isEmpty() ? "Pig" : optional2.get()));
            dynamic = dynamic.set("SpawnData", (Dynamic)dynamic2);
            dynamic = dynamic.remove("EntityId");
        }
        final Optional<? extends Stream<? extends Dynamic<?>>> optional3 = dynamic.get("SpawnPotentials").asStreamOpt();
        if (optional3.isPresent()) {
            final Optional<String> optional4;
            Dynamic<?> dynamic3;
            dynamic = dynamic.set("SpawnPotentials", dynamic.createList((Stream)((Stream)optional3.get()).map(dynamic -> {
                optional4 = (Optional<String>)dynamic.get("Type").asString();
                if (optional4.isPresent()) {
                    dynamic3 = ((Dynamic)DataFixUtils.orElse(dynamic.get("Properties").get(), dynamic.emptyMap())).set("id", dynamic.createString((String)optional4.get()));
                    return dynamic.set("Entity", (Dynamic)dynamic3).remove("Type").remove("Properties");
                }
                else {
                    return dynamic;
                }
            })));
        }
        return dynamic;
    }
    
    public TypeRewriteRule makeRule() {
        final Type<?> type1 = this.getOutputSchema().getType(TypeReferences.UNTAGGED_SPAWNER);
        final Dynamic<?> dynamic3;
        final Dynamic<?> dynamic4;
        final Type type2;
        final Pair<?, ? extends Optional<? extends Typed<?>>> pair4;
        return this.fixTypeEverywhereTyped("MobSpawnerEntityIdentifiersFix", this.getInputSchema().getType(TypeReferences.UNTAGGED_SPAWNER), (Type)type1, typed -> {
            dynamic3 = typed.get(DSL.remainderFinder());
            dynamic4 = dynamic3.set("id", dynamic3.createString("MobSpawner"));
            pair4 = type2.readTyped((Dynamic)this.a(dynamic4));
            if (!((Optional)pair4.getSecond()).isPresent()) {
                return typed;
            }
            else {
                return (Typed)((Optional)pair4.getSecond()).get();
            }
        });
    }
}
