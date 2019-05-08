package net.minecraft.datafixers.fixes;

import java.util.stream.Stream;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class SavedDataVillageCropFix extends DataFix
{
    public SavedDataVillageCropFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    public TypeRewriteRule makeRule() {
        return this.writeFixAndRead("SavedDataVillageCropFix", this.getInputSchema().getType(TypeReferences.STRUCTURE_FEATURE), this.getOutputSchema().getType(TypeReferences.STRUCTURE_FEATURE), (Function)this::a);
    }
    
    private <T> Dynamic<T> a(final Dynamic<T> dynamic) {
        return (Dynamic<T>)dynamic.update("Children", (Function)SavedDataVillageCropFix::b);
    }
    
    private static <T> Dynamic<T> b(final Dynamic<T> dynamic) {
        return dynamic.asStreamOpt().map(SavedDataVillageCropFix::a).<Dynamic<T>>map(dynamic::createList).orElse(dynamic);
    }
    
    private static Stream<? extends Dynamic<?>> a(final Stream<? extends Dynamic<?>> stream) {
        final String string2;
        return stream.map(dynamic -> {
            string2 = dynamic.get("id").asString("");
            if ("ViF".equals(string2)) {
                return SavedDataVillageCropFix.c(dynamic);
            }
            else if ("ViDF".equals(string2)) {
                return SavedDataVillageCropFix.d(dynamic);
            }
            else {
                return dynamic;
            }
        });
    }
    
    private static <T> Dynamic<T> c(Dynamic<T> dynamic) {
        dynamic = SavedDataVillageCropFix.<T>a(dynamic, "CA");
        return SavedDataVillageCropFix.<T>a(dynamic, "CB");
    }
    
    private static <T> Dynamic<T> d(Dynamic<T> dynamic) {
        dynamic = SavedDataVillageCropFix.<T>a(dynamic, "CA");
        dynamic = SavedDataVillageCropFix.<T>a(dynamic, "CB");
        dynamic = SavedDataVillageCropFix.<T>a(dynamic, "CC");
        return SavedDataVillageCropFix.<T>a(dynamic, "CD");
    }
    
    private static <T> Dynamic<T> a(final Dynamic<T> dynamic, final String string) {
        if (dynamic.get(string).asNumber().isPresent()) {
            return (Dynamic<T>)dynamic.set(string, (Dynamic)BlockStateFlattening.lookupState(dynamic.get(string).asInt(0) << 4));
        }
        return dynamic;
    }
}
