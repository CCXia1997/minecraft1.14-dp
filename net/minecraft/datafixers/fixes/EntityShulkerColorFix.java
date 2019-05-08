package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import java.util.function.Function;
import com.mojang.datafixers.Dynamic;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.schemas.Schema;

public class EntityShulkerColorFix extends ChoiceFix
{
    public EntityShulkerColorFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2, "EntityShulkerColorFix", TypeReferences.ENTITY, "minecraft:shulker");
    }
    
    public Dynamic<?> a(final Dynamic<?> dynamic) {
        if (!dynamic.get("Color").map((Function)Dynamic::asNumber).isPresent()) {
            return dynamic.set("Color", dynamic.createByte((byte)10));
        }
        return dynamic;
    }
    
    @Override
    protected Typed<?> transform(final Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), (Function)this::a);
    }
}
