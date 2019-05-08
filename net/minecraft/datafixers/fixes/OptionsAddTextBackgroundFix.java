package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.Typed;
import java.util.Optional;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class OptionsAddTextBackgroundFix extends DataFix
{
    public OptionsAddTextBackgroundFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("OptionsAddTextBackgroundFix", this.getInputSchema().getType(TypeReferences.OPTIONS), typed -> typed.update(DSL.remainderFinder(), dynamic -> (Dynamic)DataFixUtils.orElse((Optional)dynamic.get("chatOpacity").asString().map(string -> dynamic.set("textBackgroundOpacity", dynamic.createDouble(this.a(string)))), dynamic)));
    }
    
    private double a(final String string) {
        try {
            final double double2 = 0.9 * Double.parseDouble(string) + 0.1;
            return double2 / 2.0;
        }
        catch (NumberFormatException numberFormatException2) {
            return 0.5;
        }
    }
}
