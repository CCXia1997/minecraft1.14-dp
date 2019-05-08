package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.schemas.Schema;

public class ColorlessShulkerEntityFix extends ChoiceFix
{
    public ColorlessShulkerEntityFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2, "Colorless shulker entity fix", TypeReferences.ENTITY, "minecraft:shulker");
    }
    
    @Override
    protected Typed<?> transform(final Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), dynamic -> {
            if (dynamic.get("Color").asInt(0) == 10) {
                return dynamic.set("Color", dynamic.createByte((byte)16));
            }
            else {
                return dynamic;
            }
        });
    }
}
