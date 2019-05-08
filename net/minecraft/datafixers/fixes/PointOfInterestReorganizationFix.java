package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.types.DynamicOps;
import java.util.Optional;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.types.Type;
import java.util.function.Function;
import java.util.Objects;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class PointOfInterestReorganizationFix extends DataFix
{
    public PointOfInterestReorganizationFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    protected TypeRewriteRule makeRule() {
        final Type<Pair<String, Dynamic<?>>> type1 = (Type<Pair<String, Dynamic<?>>>)DSL.named(TypeReferences.POI_CHUNK.typeName(), DSL.remainderType());
        if (!Objects.equals(type1, this.getInputSchema().getType(TypeReferences.POI_CHUNK))) {
            throw new IllegalStateException("Poi type is not what was expected.");
        }
        return this.fixTypeEverywhere("POI reorganization", (Type)type1, dynamicOps -> pair -> pair.mapSecond((Function)PointOfInterestReorganizationFix::reorganize));
    }
    
    private static <T> Dynamic<T> reorganize(Dynamic<T> dynamic) {
        final Map<Dynamic<T>, Dynamic<T>> map2 = Maps.newHashMap();
        for (int integer3 = 0; integer3 < 16; ++integer3) {
            final String string4 = String.valueOf(integer3);
            final Optional<Dynamic<T>> optional5 = (Optional<Dynamic<T>>)dynamic.get(string4).get();
            if (optional5.isPresent()) {
                final Dynamic<T> dynamic2 = optional5.get();
                final Dynamic<T> dynamic3 = (Dynamic<T>)dynamic.createMap((Map)ImmutableMap.<Dynamic, Dynamic<T>>of(dynamic.createString("Records"), dynamic2));
                map2.put((Dynamic<T>)dynamic.createInt(integer3), dynamic3);
                dynamic = (Dynamic<T>)dynamic.remove(string4);
            }
        }
        return (Dynamic<T>)dynamic.set("Sections", dynamic.createMap((Map)map2));
    }
}
