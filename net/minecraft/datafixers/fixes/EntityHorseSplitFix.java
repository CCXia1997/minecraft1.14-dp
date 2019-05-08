package net.minecraft.datafixers.fixes;

import java.util.Optional;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.types.Type;
import java.util.Objects;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public class EntityHorseSplitFix extends EntityTransformFix
{
    public EntityHorseSplitFix(final Schema schema, final boolean boolean2) {
        super("EntityHorseSplitFix", schema, boolean2);
    }
    
    @Override
    protected Pair<String, Typed<?>> transform(final String choice, final Typed<?> typed) {
        final Dynamic<?> dynamic3 = typed.get(DSL.remainderFinder());
        if (Objects.equals("EntityHorse", choice)) {
            final int integer5 = dynamic3.get("Type").asInt(0);
            String string4 = null;
            switch (integer5) {
                default: {
                    string4 = "Horse";
                    break;
                }
                case 1: {
                    string4 = "Donkey";
                    break;
                }
                case 2: {
                    string4 = "Mule";
                    break;
                }
                case 3: {
                    string4 = "ZombieHorse";
                    break;
                }
                case 4: {
                    string4 = "SkeletonHorse";
                    break;
                }
            }
            dynamic3.remove("Type");
            final Type<?> type6 = this.getOutputSchema().findChoiceType(TypeReferences.ENTITY).types().get(string4);
            return (Pair<String, Typed<?>>)Pair.of(string4, ((Optional)type6.readTyped(typed.write()).getSecond()).<Throwable>orElseThrow(() -> new IllegalStateException("Could not parse the new horse")));
        }
        return (Pair<String, Typed<?>>)Pair.of(choice, typed);
    }
}
