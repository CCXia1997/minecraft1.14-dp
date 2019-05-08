package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.Typed;
import java.util.Objects;
import java.util.Optional;
import com.mojang.datafixers.Dynamic;
import java.util.stream.Stream;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class EntityRedundantChanceTagsFix extends DataFix
{
    public EntityRedundantChanceTagsFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    public TypeRewriteRule makeRule() {
        final Dynamic<?> dynamic2;
        return this.fixTypeEverywhereTyped("EntityRedundantChanceTagsFix", this.getInputSchema().getType(TypeReferences.ENTITY), typed -> typed.update(DSL.remainderFinder(), dynamic -> {
            dynamic2 = dynamic;
            if (Objects.equals(dynamic.get("HandDropChances"), Optional.<Dynamic>of(dynamic.createList((Stream)Stream.<Dynamic>generate(() -> dynamic2.createFloat(0.0f)).limit(2L))))) {
                dynamic = dynamic.remove("HandDropChances");
            }
            if (Objects.equals(dynamic.get("ArmorDropChances"), Optional.<Dynamic>of(dynamic.createList((Stream)Stream.<Dynamic>generate(() -> dynamic2.createFloat(0.0f)).limit(4L))))) {
                dynamic = dynamic.remove("ArmorDropChances");
            }
            return dynamic;
        }));
    }
}
