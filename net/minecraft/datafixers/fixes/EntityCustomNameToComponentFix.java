package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.Typed;
import net.minecraft.text.TextComponent;
import net.minecraft.text.StringTextComponent;
import com.mojang.datafixers.Dynamic;
import java.util.Optional;
import java.util.Objects;
import com.mojang.datafixers.OpticFinder;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class EntityCustomNameToComponentFix extends DataFix
{
    public EntityCustomNameToComponentFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    public TypeRewriteRule makeRule() {
        final OpticFinder<String> opticFinder1 = (OpticFinder<String>)DSL.fieldFinder("id", DSL.namespacedString());
        final Optional<String> optional4;
        return this.fixTypeEverywhereTyped("EntityCustomNameToComponentFix", this.getInputSchema().getType(TypeReferences.ENTITY), typed -> typed.update(DSL.remainderFinder(), dynamic -> {
            optional4 = (Optional<String>)typed.getOptional((OpticFinder)opticFinder1);
            if (optional4.isPresent() && Objects.equals(optional4.get(), "minecraft:commandblock_minecart")) {
                return dynamic;
            }
            else {
                return fixCustomName(dynamic);
            }
        }));
    }
    
    public static Dynamic<?> fixCustomName(final Dynamic<?> dynamic) {
        final String string2 = dynamic.get("CustomName").asString("");
        if (string2.isEmpty()) {
            return dynamic.remove("CustomName");
        }
        return dynamic.set("CustomName", dynamic.createString(TextComponent.Serializer.toJsonString(new StringTextComponent(string2))));
    }
}
