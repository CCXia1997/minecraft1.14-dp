package net.minecraft.datafixers.fixes;

import java.util.function.Function;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.Dynamic;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.schemas.Schema;

public class EntityWolfColorFix extends ChoiceFix
{
    public EntityWolfColorFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2, "EntityWolfColorFix", TypeReferences.ENTITY, "minecraft:wolf");
    }
    
    public Dynamic<?> a(final Dynamic<?> dynamic) {
        return dynamic.update("CollarColor", dynamic -> dynamic.createByte((byte)(15 - dynamic.asInt(0))));
    }
    
    @Override
    protected Typed<?> transform(final Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), (Function)this::a);
    }
}
