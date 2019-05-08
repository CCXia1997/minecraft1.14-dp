package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.schemas.Schema;

public class BlockEntityShulkerBoxColorFix extends ChoiceFix
{
    public BlockEntityShulkerBoxColorFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2, "BlockEntityShulkerBoxColorFix", TypeReferences.BLOCK_ENTITY, "minecraft:shulker_box");
    }
    
    @Override
    protected Typed<?> transform(final Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), dynamic -> dynamic.remove("Color"));
    }
}
