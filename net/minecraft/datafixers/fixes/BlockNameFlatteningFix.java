package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.types.Type;
import java.util.function.Function;
import net.minecraft.datafixers.schemas.SchemaIdentifierNormalize;
import java.util.Objects;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class BlockNameFlatteningFix extends DataFix
{
    public BlockNameFlatteningFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    public TypeRewriteRule makeRule() {
        final Type<?> type1 = this.getInputSchema().getType(TypeReferences.BLOCK_NAME);
        final Type<?> type2 = this.getOutputSchema().getType(TypeReferences.BLOCK_NAME);
        final Type<Pair<String, Either<Integer, String>>> type3 = (Type<Pair<String, Either<Integer, String>>>)DSL.named(TypeReferences.BLOCK_NAME.typeName(), DSL.or(DSL.intType(), DSL.namespacedString()));
        final Type<Pair<String, String>> type4 = (Type<Pair<String, String>>)DSL.named(TypeReferences.BLOCK_NAME.typeName(), DSL.namespacedString());
        if (!Objects.equals(type1, type3) || !Objects.equals(type2, type4)) {
            throw new IllegalStateException("Expected and actual types don't match.");
        }
        return this.fixTypeEverywhere("BlockNameFlatteningFix", (Type)type3, (Type)type4, dynamicOps -> pair -> pair.mapSecond(either -> (String)either.map((Function)BlockStateFlattening::lookup, string -> BlockStateFlattening.lookup(SchemaIdentifierNormalize.normalize(string)))));
    }
}
