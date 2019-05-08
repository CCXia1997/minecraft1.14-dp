package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public abstract class EntitySimpleTransformFix extends EntityTransformFix
{
    public EntitySimpleTransformFix(final String name, final Schema oldSchema, final boolean boolean3) {
        super(name, oldSchema, boolean3);
    }
    
    @Override
    protected Pair<String, Typed<?>> transform(final String choice, final Typed<?> typed) {
        final Pair<String, Dynamic<?>> pair3 = this.transform(choice, typed.getOrCreate(DSL.remainderFinder()));
        return (Pair<String, Typed<?>>)Pair.of(pair3.getFirst(), typed.set(DSL.remainderFinder(), pair3.getSecond()));
    }
    
    protected abstract Pair<String, Dynamic<?>> transform(final String arg1, final Dynamic<?> arg2);
}
