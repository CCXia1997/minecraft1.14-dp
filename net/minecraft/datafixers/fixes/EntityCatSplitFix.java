package net.minecraft.datafixers.fixes;

import java.util.Objects;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;

public class EntityCatSplitFix extends EntitySimpleTransformFix
{
    public EntityCatSplitFix(final Schema schema, final boolean boolean2) {
        super("EntityCatSplitFix", schema, boolean2);
    }
    
    @Override
    protected Pair<String, Dynamic<?>> transform(final String choice, Dynamic<?> dynamic) {
        if (Objects.equals("minecraft:ocelot", choice)) {
            final int integer3 = dynamic.get("CatType").asInt(0);
            if (integer3 == 0) {
                final String string4 = dynamic.get("Owner").asString("");
                final String string5 = dynamic.get("OwnerUUID").asString("");
                if (string4.length() > 0 || string5.length() > 0) {
                    dynamic.set("Trusting", dynamic.createBoolean(true));
                }
            }
            else if (integer3 > 0 && integer3 < 4) {
                dynamic = dynamic.set("CatType", dynamic.createInt(integer3));
                dynamic = dynamic.set("OwnerUUID", dynamic.createString(dynamic.get("OwnerUUID").asString("")));
                return (Pair<String, Dynamic<?>>)Pair.of("minecraft:cat", dynamic);
            }
        }
        return (Pair<String, Dynamic<?>>)Pair.of(choice, dynamic);
    }
}
