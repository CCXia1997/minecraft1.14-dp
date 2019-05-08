package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.types.Type;
import java.util.function.Function;
import java.util.Objects;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public abstract class FixItemName extends DataFix
{
    private final String name;
    
    public FixItemName(final Schema outputSchema, final String string) {
        super(outputSchema, false);
        this.name = string;
    }
    
    public TypeRewriteRule makeRule() {
        final Type<Pair<String, String>> type1 = (Type<Pair<String, String>>)DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString());
        if (!Objects.equals(this.getInputSchema().getType(TypeReferences.ITEM_NAME), type1)) {
            throw new IllegalStateException("item name type is not what was expected.");
        }
        return this.fixTypeEverywhere(this.name, (Type)type1, dynamicOps -> pair -> pair.mapSecond((Function)this::rename));
    }
    
    protected abstract String rename(final String arg1);
    
    public static DataFix create(final Schema outputSchema, final String name, final Function<String, String> rename) {
        return new FixItemName(outputSchema, name) {
            @Override
            protected String rename(final String string) {
                return rename.apply(string);
            }
        };
    }
}
