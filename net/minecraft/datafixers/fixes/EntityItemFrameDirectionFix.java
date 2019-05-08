package net.minecraft.datafixers.fixes;

import java.util.function.Function;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.Dynamic;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.schemas.Schema;

public class EntityItemFrameDirectionFix extends ChoiceFix
{
    public EntityItemFrameDirectionFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2, "EntityItemFrameDirectionFix", TypeReferences.ENTITY, "minecraft:item_frame");
    }
    
    public Dynamic<?> a(final Dynamic<?> dynamic) {
        return dynamic.set("Facing", dynamic.createByte(a(dynamic.get("Facing").asByte((byte)0))));
    }
    
    @Override
    protected Typed<?> transform(final Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), (Function)this::a);
    }
    
    private static byte a(final byte byte1) {
        switch (byte1) {
            default: {
                return 2;
            }
            case 0: {
                return 3;
            }
            case 1: {
                return 4;
            }
            case 3: {
                return 5;
            }
        }
    }
}
