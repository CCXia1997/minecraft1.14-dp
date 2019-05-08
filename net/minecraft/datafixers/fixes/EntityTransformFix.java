package net.minecraft.datafixers.fixes;

import java.util.function.Function;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.types.Type;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public abstract class EntityTransformFix extends DataFix
{
    protected final String name;
    
    public EntityTransformFix(final String name, final Schema oldSchema, final boolean boolean3) {
        super(oldSchema, boolean3);
        this.name = name;
    }
    
    public TypeRewriteRule makeRule() {
        final TaggedChoice.TaggedChoiceType<String> taggedChoiceType1 = (TaggedChoice.TaggedChoiceType<String>)this.getInputSchema().findChoiceType(TypeReferences.ENTITY);
        final TaggedChoice.TaggedChoiceType<String> taggedChoiceType2 = (TaggedChoice.TaggedChoiceType<String>)this.getOutputSchema().findChoiceType(TypeReferences.ENTITY);
        final String string5;
        final TaggedChoice.TaggedChoiceType taggedChoiceType4;
        final Type<?> type6;
        final Pair<String, Typed<?>> pair2;
        final TaggedChoice.TaggedChoiceType taggedChoiceType5;
        final Type<?> type7;
        final IllegalStateException ex;
        return this.fixTypeEverywhere(this.name, (Type)taggedChoiceType1, (Type)taggedChoiceType2, dynamicOps -> pair -> {
            string5 = (String)pair.getFirst();
            type6 = taggedChoiceType4.types().get(string5);
            pair2 = this.transform(string5, this.makeTyped(pair.getSecond(), dynamicOps, type6));
            type7 = taggedChoiceType5.types().get(pair2.getFirst());
            if (!type7.equals(((Typed)pair2.getSecond()).getType(), true, true)) {
                new IllegalStateException(String.format("Dynamic type check failed: %s not equal to %s", type7, ((Typed)pair2.getSecond()).getType()));
                throw ex;
            }
            else {
                return Pair.of(pair2.getFirst(), ((Typed)pair2.getSecond()).getValue());
            }
        });
    }
    
    private <A> Typed<A> makeTyped(final Object object, final DynamicOps<?> dynamicOps, final Type<A> type) {
        return (Typed<A>)new Typed((Type)type, (DynamicOps)dynamicOps, object);
    }
    
    protected abstract Pair<String, Typed<?>> transform(final String arg1, final Typed<?> arg2);
}
