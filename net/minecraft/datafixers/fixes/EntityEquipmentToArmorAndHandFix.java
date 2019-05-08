package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.Typed;
import java.util.Iterator;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.util.Unit;
import com.mojang.datafixers.util.Pair;
import java.util.stream.Stream;
import com.google.common.collect.Lists;
import java.util.Optional;
import java.util.List;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.types.Type;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class EntityEquipmentToArmorAndHandFix extends DataFix
{
    public EntityEquipmentToArmorAndHandFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    public TypeRewriteRule makeRule() {
        return this.a((com.mojang.datafixers.types.Type<Object>)this.getInputSchema().getTypeRaw(TypeReferences.ITEM_STACK));
    }
    
    private <IS> TypeRewriteRule a(final Type<IS> type) {
        final Type<Pair<Either<List<IS>, Unit>, Dynamic<?>>> type2 = (Type<Pair<Either<List<IS>, Unit>, Dynamic<?>>>)DSL.and(DSL.optional((Type)DSL.field("Equipment", (Type)DSL.list((Type)type))), DSL.remainderType());
        final Type<Pair<Either<List<IS>, Unit>, Pair<Either<List<IS>, Unit>, Dynamic<?>>>> type3 = (Type<Pair<Either<List<IS>, Unit>, Pair<Either<List<IS>, Unit>, Dynamic<?>>>>)DSL.and(DSL.optional((Type)DSL.field("ArmorItems", (Type)DSL.list((Type)type))), DSL.optional((Type)DSL.field("HandItems", (Type)DSL.list((Type)type))), DSL.remainderType());
        final OpticFinder<Pair<Either<List<IS>, Unit>, Dynamic<?>>> opticFinder4 = (OpticFinder<Pair<Either<List<IS>, Unit>, Dynamic<?>>>)DSL.typeFinder((Type)type2);
        final OpticFinder<List<IS>> opticFinder5 = (OpticFinder<List<IS>>)DSL.fieldFinder("Equipment", (Type)DSL.list((Type)type));
        Either<List<Object>, Unit> either6;
        Either<List<Object>, Unit> either7;
        Dynamic<?> dynamic8;
        final OpticFinder opticFinder6;
        final Optional<List<Object>> optional9;
        List<Object> list10;
        Object object11;
        List<Object> list11;
        int integer13;
        final Dynamic<?> dynamic9;
        final Optional<? extends Stream<? extends Dynamic<?>>> optional10;
        Iterator<? extends Dynamic<?>> iterator12;
        float float13;
        Dynamic<?> dynamic10;
        Dynamic<?> dynamic11;
        final OpticFinder opticFinder7;
        final Type type5;
        return this.fixTypeEverywhereTyped("EntityEquipmentToArmorAndHandFix", this.getInputSchema().getType(TypeReferences.ENTITY), this.getOutputSchema().getType(TypeReferences.ENTITY), typed -> {
            either6 = (Either<List<Object>, Unit>)Either.right(DSL.unit());
            either7 = (Either<List<Object>, Unit>)Either.right(DSL.unit());
            dynamic8 = typed.getOrCreate(DSL.remainderFinder());
            optional9 = (Optional<List<Object>>)typed.getOptional(opticFinder6);
            if (optional9.isPresent()) {
                list10 = optional9.get();
                object11 = ((Optional)type.read(dynamic8.emptyMap()).getSecond()).<Throwable>orElseThrow(() -> new IllegalStateException("Could not parse newly created empty itemstack."));
                if (!list10.isEmpty()) {
                    either6 = (Either<List<Object>, Unit>)Either.left(Lists.newArrayList(list10.get(0), object11));
                }
                if (list10.size() > 1) {
                    list11 = Lists.newArrayList(object11, object11, object11, object11);
                    for (integer13 = 1; integer13 < Math.min(list10.size(), 5); ++integer13) {
                        list11.set(integer13 - 1, list10.get(integer13));
                    }
                    either7 = (Either<List<Object>, Unit>)Either.left(list11);
                }
            }
            dynamic9 = dynamic8;
            optional10 = dynamic8.get("DropChances").asStreamOpt();
            if (optional10.isPresent()) {
                iterator12 = Stream.concat(optional10.get(), Stream.generate(() -> dynamic9.createInt(0))).iterator();
                float13 = ((Dynamic)iterator12.next()).asFloat(0.0f);
                if (!dynamic8.get("HandDropChances").get().isPresent()) {
                    dynamic10 = dynamic8.emptyMap().merge(dynamic8.createFloat(float13)).merge(dynamic8.createFloat(0.0f));
                    dynamic8 = dynamic8.set("HandDropChances", (Dynamic)dynamic10);
                }
                if (!dynamic8.get("ArmorDropChances").get().isPresent()) {
                    dynamic11 = dynamic8.emptyMap().merge(dynamic8.createFloat(((Dynamic)iterator12.next()).asFloat(0.0f))).merge(dynamic8.createFloat(((Dynamic)iterator12.next()).asFloat(0.0f))).merge(dynamic8.createFloat(((Dynamic)iterator12.next()).asFloat(0.0f))).merge(dynamic8.createFloat(((Dynamic)iterator12.next()).asFloat(0.0f)));
                    dynamic8 = dynamic8.set("ArmorDropChances", (Dynamic)dynamic11);
                }
                dynamic8 = dynamic8.remove("DropChances");
            }
            return typed.set(opticFinder7, type5, Pair.of((Object)either6, (Object)Pair.of((Object)either7, (Object)dynamic8)));
        });
    }
}
