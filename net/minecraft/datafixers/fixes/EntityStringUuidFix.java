package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import java.util.Optional;
import java.util.UUID;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class EntityStringUuidFix extends DataFix
{
    public EntityStringUuidFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    public TypeRewriteRule makeRule() {
        final Optional optional2;
        UUID uUID3;
        return this.fixTypeEverywhereTyped("EntityStringUuidFix", this.getInputSchema().getType(TypeReferences.ENTITY), typed -> typed.update(DSL.remainderFinder(), dynamic -> {
            optional2 = dynamic.get("UUID").asString();
            if (optional2.isPresent()) {
                uUID3 = UUID.fromString(optional2.get());
                return dynamic.remove("UUID").set("UUIDMost", dynamic.createLong(uUID3.getMostSignificantBits())).set("UUIDLeast", dynamic.createLong(uUID3.getLeastSignificantBits()));
            }
            else {
                return dynamic;
            }
        }));
    }
}
