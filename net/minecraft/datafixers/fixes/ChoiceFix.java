package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.Typed;
import java.util.function.Function;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;

public abstract class ChoiceFix extends DataFix
{
    private final String name;
    private final String choiceName;
    private final DSL.TypeReference type;
    
    public ChoiceFix(final Schema outputSchema, final boolean changesType, final String name, final DSL.TypeReference type, final String string5) {
        super(outputSchema, changesType);
        this.name = name;
        this.type = type;
        this.choiceName = string5;
    }
    
    public TypeRewriteRule makeRule() {
        final OpticFinder<?> opticFinder1 = DSL.namedChoice(this.choiceName, this.getInputSchema().getChoiceType(this.type, this.choiceName));
        return this.fixTypeEverywhereTyped(this.name, this.getInputSchema().getType(this.type), this.getOutputSchema().getType(this.type), typed -> typed.updateTyped((OpticFinder)opticFinder1, this.getOutputSchema().getChoiceType(this.type, this.choiceName), (Function)this::transform));
    }
    
    protected abstract Typed<?> transform(final Typed<?> arg1);
}
