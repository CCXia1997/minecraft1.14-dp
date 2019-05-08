package net.minecraft.datafixers.fixes;

import java.util.function.Function;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.Dynamic;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.schemas.Schema;

public class CatTypeFix extends ChoiceFix
{
    public CatTypeFix(final Schema outputSchema, final boolean changesType) {
        super(outputSchema, changesType, "CatTypeFix", TypeReferences.ENTITY, "minecraft:cat");
    }
    
    public Dynamic<?> fixCatTypeData(final Dynamic<?> dynamic) {
        if (dynamic.get("CatType").asInt(0) == 9) {
            return dynamic.set("CatType", dynamic.createInt(10));
        }
        return dynamic;
    }
    
    @Override
    protected Typed<?> transform(final Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), (Function)this::fixCatTypeData);
    }
}
