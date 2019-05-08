package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import java.util.Optional;
import java.util.Objects;
import com.mojang.datafixers.OpticFinder;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class BlockEntityCustomNameToComponentFix extends DataFix
{
    public BlockEntityCustomNameToComponentFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    public TypeRewriteRule makeRule() {
        final OpticFinder<String> opticFinder1 = (OpticFinder<String>)DSL.fieldFinder("id", DSL.namespacedString());
        final Optional<String> optional4;
        return this.fixTypeEverywhereTyped("BlockEntityCustomNameToComponentFix", this.getInputSchema().getType(TypeReferences.BLOCK_ENTITY), typed -> typed.update(DSL.remainderFinder(), dynamic -> {
            optional4 = (Optional<String>)typed.getOptional((OpticFinder)opticFinder1);
            if (optional4.isPresent() && Objects.equals(optional4.get(), "minecraft:command_block")) {
                return dynamic;
            }
            else {
                return EntityCustomNameToComponentFix.fixCustomName(dynamic);
            }
        }));
    }
}
