package net.minecraft.datafixers.fixes;

import java.util.function.Function;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.DataFixUtils;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.util.Unit;
import java.util.List;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.Objects;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.types.Type;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class EntityRidingToPassengerFix extends DataFix
{
    public EntityRidingToPassengerFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    public TypeRewriteRule makeRule() {
        final Schema schema1 = this.getInputSchema();
        final Schema schema2 = this.getOutputSchema();
        final Type<?> type3 = schema1.getTypeRaw(TypeReferences.ENTITY_TREE);
        final Type<?> type4 = schema2.getTypeRaw(TypeReferences.ENTITY_TREE);
        final Type<?> type5 = schema1.getTypeRaw(TypeReferences.ENTITY);
        return this.a(schema1, schema2, type3, type4, type5);
    }
    
    private <OldEntityTree, NewEntityTree, Entity> TypeRewriteRule a(final Schema schema1, final Schema schema2, final Type<OldEntityTree> type3, final Type<NewEntityTree> type4, final Type<Entity> type5) {
        final Type<Pair<String, Pair<Either<OldEntityTree, Unit>, Entity>>> type6 = (Type<Pair<String, Pair<Either<OldEntityTree, Unit>, Entity>>>)DSL.named(TypeReferences.ENTITY_TREE.typeName(), DSL.and(DSL.optional((Type)DSL.field("Riding", (Type)type3)), (Type)type5));
        final Type<Pair<String, Pair<Either<List<NewEntityTree>, Unit>, Entity>>> type7 = (Type<Pair<String, Pair<Either<List<NewEntityTree>, Unit>, Entity>>>)DSL.named(TypeReferences.ENTITY_TREE.typeName(), DSL.and(DSL.optional((Type)DSL.field("Passengers", (Type)DSL.list((Type)type4))), (Type)type5));
        final Type<?> type8 = schema1.getType(TypeReferences.ENTITY_TREE);
        final Type<?> type9 = schema2.getType(TypeReferences.ENTITY_TREE);
        if (!Objects.equals(type8, type6)) {
            throw new IllegalStateException("Old entity type is not what was expected.");
        }
        if (!type9.equals(type7, true, true)) {
            throw new IllegalStateException("New entity type is not what was expected.");
        }
        final OpticFinder<Pair<String, Pair<Either<OldEntityTree, Unit>, Entity>>> opticFinder10 = (OpticFinder<Pair<String, Pair<Either<OldEntityTree, Unit>, Entity>>>)DSL.typeFinder((Type)type6);
        final OpticFinder<Pair<String, Pair<Either<List<NewEntityTree>, Unit>, Entity>>> opticFinder11 = (OpticFinder<Pair<String, Pair<Either<List<NewEntityTree>, Unit>, Entity>>>)DSL.typeFinder((Type)type7);
        final OpticFinder<NewEntityTree> opticFinder12 = (OpticFinder<NewEntityTree>)DSL.typeFinder((Type)type4);
        final Type<?> type10 = schema1.getType(TypeReferences.PLAYER);
        final Type<?> type11 = schema2.getType(TypeReferences.PLAYER);
        Optional<Pair<String, Pair<Either<List<Object>, Unit>, Object>>> optional8;
        Pair<String, Pair<Either<Object, Unit>, Object>> pair2;
        Typed<Object> typed6;
        final OpticFinder opticFinder13;
        final OpticFinder opticFinder14;
        Object object7;
        Either<List<Object>, Unit> either10;
        Optional<Object> optional9;
        final OpticFinder opticFinder15;
        return TypeRewriteRule.seq(this.fixTypeEverywhere("EntityRidingToPassengerFix", (Type)type6, (Type)type7, dynamicOps -> pair -> {
            optional8 = Optional.<Pair<String, Pair<Either<List<Object>, Unit>, Object>>>empty();
            pair2 = pair;
            while (true) {
                either10 = (Either<List<Object>, Unit>)DataFixUtils.orElse((Optional)optional8.map(pair -> {
                    typed6 = (Typed<Object>)type4.pointTyped(dynamicOps).orElseThrow(() -> new IllegalStateException("Could not create new entity tree"));
                    object7 = typed6.set(opticFinder13, pair).getOptional(opticFinder14).orElseThrow(() -> new IllegalStateException("Should always have an entity tree here"));
                    return Either.left(ImmutableList.of(object7));
                }), Either.right((Object)DSL.unit()));
                optional8 = Optional.<Pair<String, Pair<Either<List<Object>, Unit>, Object>>>of(Pair.of(TypeReferences.ENTITY_TREE.typeName(), Pair.of((Object)either10, ((Pair)pair2.getSecond()).getSecond())));
                optional9 = (Optional<Object>)((Either)((Pair)pair2.getSecond()).getFirst()).left();
                if (!optional9.isPresent()) {
                    break;
                }
                else {
                    pair2 = (Pair<String, Pair<Either<Object, Unit>, Object>>)new Typed((Type)type3, dynamicOps, optional9.get()).getOptional(opticFinder15).orElseThrow(() -> new IllegalStateException("Should always have an entity here"));
                }
            }
            return optional8.orElseThrow(() -> new IllegalStateException("Should always have an entity tree here"));
        }), this.writeAndRead("player RootVehicle injecter", (Type)type10, (Type)type11));
    }
}
