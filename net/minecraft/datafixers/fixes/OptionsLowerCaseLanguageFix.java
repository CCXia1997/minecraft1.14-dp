package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import java.util.Optional;
import java.util.Locale;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class OptionsLowerCaseLanguageFix extends DataFix
{
    public OptionsLowerCaseLanguageFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    public TypeRewriteRule makeRule() {
        final Optional optional2;
        return this.fixTypeEverywhereTyped("OptionsLowerCaseLanguageFix", this.getInputSchema().getType(TypeReferences.OPTIONS), typed -> typed.update(DSL.remainderFinder(), dynamic -> {
            optional2 = dynamic.get("lang").asString();
            if (optional2.isPresent()) {
                return dynamic.set("lang", dynamic.createString(optional2.get().toLowerCase(Locale.ROOT)));
            }
            else {
                return dynamic;
            }
        }));
    }
}
