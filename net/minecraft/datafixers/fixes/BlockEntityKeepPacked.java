package net.minecraft.datafixers.fixes;

import java.util.function.Function;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.Dynamic;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.schemas.Schema;

public class BlockEntityKeepPacked extends ChoiceFix
{
    public BlockEntityKeepPacked(final Schema schema, final boolean boolean2) {
        super(schema, boolean2, "BlockEntityKeepPacked", TypeReferences.BLOCK_ENTITY, "DUMMY");
    }
    
    private static Dynamic<?> keepPacked(final Dynamic<?> dynamic) {
        return dynamic.set("keepPacked", dynamic.createBoolean(true));
    }
    
    @Override
    protected Typed<?> transform(final Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), (Function)BlockEntityKeepPacked::keepPacked);
    }
}
