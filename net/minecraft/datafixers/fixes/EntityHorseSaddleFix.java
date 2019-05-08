package net.minecraft.datafixers.fixes;

import java.util.Optional;
import com.mojang.datafixers.util.Pair;
import java.util.function.Supplier;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.schemas.Schema;

public class EntityHorseSaddleFix extends ChoiceFix
{
    public EntityHorseSaddleFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2, "EntityHorseSaddleFix", TypeReferences.ENTITY, "EntityHorse");
    }
    
    @Override
    protected Typed<?> transform(final Typed<?> typed) {
        final OpticFinder<Pair<String, String>> opticFinder2 = (OpticFinder<Pair<String, String>>)DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
        final Type<?> type3 = this.getInputSchema().getTypeRaw(TypeReferences.ITEM_STACK);
        final OpticFinder<?> opticFinder3 = DSL.fieldFinder("SaddleItem", (Type)type3);
        final Optional<? extends Typed<?>> optional5 = typed.getOptionalTyped((OpticFinder)opticFinder3);
        final Dynamic<?> dynamic6 = typed.get(DSL.remainderFinder());
        if (!optional5.isPresent() && dynamic6.get("Saddle").asBoolean(false)) {
            Typed<?> typed2 = type3.pointTyped(typed.getOps()).<Throwable>orElseThrow(IllegalStateException::new);
            typed2 = typed2.set((OpticFinder)opticFinder2, Pair.of((Object)TypeReferences.ITEM_NAME.typeName(), (Object)"minecraft:saddle"));
            Dynamic<?> dynamic7 = dynamic6.emptyMap();
            dynamic7 = dynamic7.set("Count", dynamic7.createByte((byte)1));
            dynamic7 = dynamic7.set("Damage", dynamic7.createShort((short)0));
            typed2 = typed2.set(DSL.remainderFinder(), dynamic7);
            dynamic6.remove("Saddle");
            return typed.set((OpticFinder)opticFinder3, (Typed)typed2).set(DSL.remainderFinder(), dynamic6);
        }
        return typed;
    }
}
