package net.minecraft.datafixers.fixes;

import java.util.stream.Stream;
import java.util.function.Predicate;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import com.mojang.datafixers.types.Type;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class IglooMetadataRemovalFix extends DataFix
{
    public IglooMetadataRemovalFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    protected TypeRewriteRule makeRule() {
        final Type<?> type1 = this.getInputSchema().getType(TypeReferences.STRUCTURE_FEATURE);
        final Type<?> type2 = this.getOutputSchema().getType(TypeReferences.STRUCTURE_FEATURE);
        return this.writeFixAndRead("IglooMetadataRemovalFix", (Type)type1, (Type)type2, (Function)IglooMetadataRemovalFix::a);
    }
    
    private static <T> Dynamic<T> a(final Dynamic<T> dynamic) {
        final boolean boolean2 = dynamic.get("Children").asStreamOpt().<Boolean>map(stream -> stream.allMatch(IglooMetadataRemovalFix::isIgloo)).orElse(false);
        if (boolean2) {
            return (Dynamic<T>)dynamic.set("id", dynamic.createString("Igloo")).remove("Children");
        }
        return (Dynamic<T>)dynamic.update("Children", (Function)IglooMetadataRemovalFix::removeIgloos);
    }
    
    private static <T> Dynamic<T> removeIgloos(final Dynamic<T> dynamic) {
        return dynamic.asStreamOpt().map(stream -> stream.filter(dynamic -> !isIgloo(dynamic))).<Dynamic<T>>map(dynamic::createList).orElse(dynamic);
    }
    
    private static boolean isIgloo(final Dynamic<?> dynamic) {
        return dynamic.get("id").asString("").equals("Iglu");
    }
}
