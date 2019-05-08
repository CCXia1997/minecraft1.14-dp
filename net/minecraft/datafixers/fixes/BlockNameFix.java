package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import java.util.Optional;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.types.Type;
import java.util.function.Function;
import java.util.Objects;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public abstract class BlockNameFix extends DataFix
{
    private final String name;
    
    public BlockNameFix(final Schema oldSchema, final String string) {
        super(oldSchema, false);
        this.name = string;
    }
    
    public TypeRewriteRule makeRule() {
        final Type<?> type1 = this.getInputSchema().getType(TypeReferences.BLOCK_NAME);
        final Type<Pair<String, String>> type2 = (Type<Pair<String, String>>)DSL.named(TypeReferences.BLOCK_NAME.typeName(), DSL.namespacedString());
        if (!Objects.equals(type1, type2)) {
            throw new IllegalStateException("block type is not what was expected.");
        }
        final TypeRewriteRule typeRewriteRule3 = this.fixTypeEverywhere(this.name + " for block", (Type)type2, dynamicOps -> pair -> pair.mapSecond((Function)this::rename));
        final Optional<String> optional2;
        final TypeRewriteRule typeRewriteRule4 = this.fixTypeEverywhereTyped(this.name + " for block_state", this.getInputSchema().getType(TypeReferences.BLOCK_STATE), typed -> typed.update(DSL.remainderFinder(), dynamic -> {
            optional2 = (Optional<String>)dynamic.get("Name").asString();
            if (optional2.isPresent()) {
                return dynamic.set("Name", dynamic.createString(this.rename(optional2.get())));
            }
            else {
                return dynamic;
            }
        }));
        return TypeRewriteRule.seq(typeRewriteRule3, typeRewriteRule4);
    }
    
    protected abstract String rename(final String arg1);
    
    public static DataFix create(final Schema oldSchema, final String name, final Function<String, String> rename) {
        return new BlockNameFix(oldSchema, name) {
            @Override
            protected String rename(final String string) {
                return rename.apply(string);
            }
        };
    }
}
