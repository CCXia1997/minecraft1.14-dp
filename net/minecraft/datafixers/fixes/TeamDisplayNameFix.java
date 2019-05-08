package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import com.mojang.datafixers.DataFixUtils;
import java.util.function.Function;
import net.minecraft.text.TextComponent;
import net.minecraft.text.StringTextComponent;
import com.mojang.datafixers.Dynamic;
import java.util.Objects;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class TeamDisplayNameFix extends DataFix
{
    public TeamDisplayNameFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    protected TypeRewriteRule makeRule() {
        final Type<Pair<String, Dynamic<?>>> type1 = (Type<Pair<String, Dynamic<?>>>)DSL.named(TypeReferences.TEAM.typeName(), DSL.remainderType());
        if (!Objects.equals(type1, this.getInputSchema().getType(TypeReferences.TEAM))) {
            throw new IllegalStateException("Team type is not what was expected.");
        }
        return this.fixTypeEverywhere("TeamDisplayNameFix", (Type)type1, dynamicOps -> pair -> pair.mapSecond(dynamic -> ((Dynamic)dynamic).update("DisplayName", dynamic2 -> (Dynamic)DataFixUtils.orElse((Optional)dynamic2.asString().map(string -> TextComponent.Serializer.toJsonString(new StringTextComponent(string))).map(dynamic::createString), dynamic2))));
    }
}
