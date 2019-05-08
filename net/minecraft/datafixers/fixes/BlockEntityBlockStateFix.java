package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.OpticFinder;
import java.util.Optional;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.schemas.Schema;

public class BlockEntityBlockStateFix extends ChoiceFix
{
    public BlockEntityBlockStateFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2, "BlockEntityBlockStateFix", TypeReferences.BLOCK_ENTITY, "minecraft:piston");
    }
    
    @Override
    protected Typed<?> transform(final Typed<?> typed) {
        final Type<?> type2 = this.getOutputSchema().getChoiceType(TypeReferences.BLOCK_ENTITY, "minecraft:piston");
        final Type<?> type3 = type2.findFieldType("blockState");
        final OpticFinder<?> opticFinder4 = DSL.fieldFinder("blockState", (Type)type3);
        Dynamic<?> dynamic5 = typed.get(DSL.remainderFinder());
        final int integer6 = dynamic5.get("blockId").asInt(0);
        dynamic5 = dynamic5.remove("blockId");
        final int integer7 = dynamic5.get("blockData").asInt(0) & 0xF;
        dynamic5 = dynamic5.remove("blockData");
        final Dynamic<?> dynamic6 = BlockStateFlattening.lookupState(integer6 << 4 | integer7);
        final Typed<?> typed2 = type2.pointTyped(typed.getOps()).<Throwable>orElseThrow(() -> new IllegalStateException("Could not create new piston block entity."));
        return typed2.set(DSL.remainderFinder(), dynamic5).set((OpticFinder)opticFinder4, (Typed)((Optional)type3.readTyped((Dynamic)dynamic6).getSecond()).<Throwable>orElseThrow(() -> new IllegalStateException("Could not parse newly created block state tag.")));
    }
}
