package net.minecraft.datafixers.fixes;

import java.util.function.Function;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Optional;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.types.Type;
import java.util.Objects;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import net.minecraft.scoreboard.ScoreboardCriterion;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class ObjectiveRenderTypeFix extends DataFix
{
    public ObjectiveRenderTypeFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    private static ScoreboardCriterion.RenderType a(final String string) {
        return string.equals("health") ? ScoreboardCriterion.RenderType.HEARTS : ScoreboardCriterion.RenderType.INTEGER;
    }
    
    protected TypeRewriteRule makeRule() {
        final Type<Pair<String, Dynamic<?>>> type1 = (Type<Pair<String, Dynamic<?>>>)DSL.named(TypeReferences.OBJECTIVE.typeName(), DSL.remainderType());
        if (!Objects.equals(type1, this.getInputSchema().getType(TypeReferences.OBJECTIVE))) {
            throw new IllegalStateException("Objective type is not what was expected.");
        }
        final Optional<String> optional2;
        String string3;
        ScoreboardCriterion.RenderType renderType4;
        return this.fixTypeEverywhere("ObjectiveRenderTypeFix", (Type)type1, dynamicOps -> pair -> pair.mapSecond(dynamic -> {
            optional2 = (Optional<String>)dynamic.get("RenderType").asString();
            if (!optional2.isPresent()) {
                string3 = dynamic.get("CriteriaName").asString("");
                renderType4 = a(string3);
                return dynamic.set("RenderType", dynamic.createString(renderType4.getName()));
            }
            else {
                return dynamic;
            }
        }));
    }
}
