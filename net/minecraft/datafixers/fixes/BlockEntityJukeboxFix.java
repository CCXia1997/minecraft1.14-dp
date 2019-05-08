package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.OpticFinder;
import java.util.Optional;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.schemas.Schema;

public class BlockEntityJukeboxFix extends ChoiceFix
{
    public BlockEntityJukeboxFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2, "BlockEntityJukeboxFix", TypeReferences.BLOCK_ENTITY, "minecraft:jukebox");
    }
    
    @Override
    protected Typed<?> transform(final Typed<?> typed) {
        final Type<?> type2 = this.getInputSchema().getChoiceType(TypeReferences.BLOCK_ENTITY, "minecraft:jukebox");
        final Type<?> type3 = type2.findFieldType("RecordItem");
        final OpticFinder<?> opticFinder4 = DSL.fieldFinder("RecordItem", (Type)type3);
        final Dynamic<?> dynamic5 = typed.get(DSL.remainderFinder());
        final int integer6 = dynamic5.get("Record").asInt(0);
        if (integer6 > 0) {
            dynamic5.remove("Record");
            final String string7 = ItemInstanceTheFlatteningFix.getItem(ItemIdFix.fromId(integer6), 0);
            if (string7 != null) {
                Dynamic<?> dynamic6 = dynamic5.emptyMap();
                dynamic6 = dynamic6.set("id", dynamic6.createString(string7));
                dynamic6 = dynamic6.set("Count", dynamic6.createByte((byte)1));
                return typed.set((OpticFinder)opticFinder4, (Typed)((Optional)type3.readTyped((Dynamic)dynamic6).getSecond()).<Throwable>orElseThrow(() -> new IllegalStateException("Could not create record item stack."))).set(DSL.remainderFinder(), dynamic5);
            }
        }
        return typed;
    }
}
