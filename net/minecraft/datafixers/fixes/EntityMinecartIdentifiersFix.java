package net.minecraft.datafixers.fixes;

import com.google.common.collect.Lists;
import java.util.function.Function;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import java.util.function.Supplier;
import com.mojang.datafixers.Typed;
import java.util.Objects;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.List;
import com.mojang.datafixers.DataFix;

public class EntityMinecartIdentifiersFix extends DataFix
{
    private static final List<String> MINECARTS;
    
    public EntityMinecartIdentifiersFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    public TypeRewriteRule makeRule() {
        final TaggedChoice.TaggedChoiceType<String> taggedChoiceType1 = (TaggedChoice.TaggedChoiceType<String>)this.getInputSchema().findChoiceType(TypeReferences.ENTITY);
        final TaggedChoice.TaggedChoiceType<String> taggedChoiceType2 = (TaggedChoice.TaggedChoiceType<String>)this.getOutputSchema().findChoiceType(TypeReferences.ENTITY);
        final TaggedChoice.TaggedChoiceType taggedChoiceType4;
        Typed<? extends Pair<String, ?>> typed5;
        Dynamic<?> dynamic6;
        int integer8;
        String string7;
        final TaggedChoice.TaggedChoiceType taggedChoiceType5;
        return this.fixTypeEverywhere("EntityMinecartIdentifiersFix", (Type)taggedChoiceType1, (Type)taggedChoiceType2, dynamicOps -> pair -> {
            if (Objects.equals(pair.getFirst(), "Minecart")) {
                typed5 = taggedChoiceType4.point(dynamicOps, "Minecart", pair.getSecond()).orElseThrow(IllegalStateException::new);
                dynamic6 = typed5.getOrCreate(DSL.remainderFinder());
                integer8 = dynamic6.get("Type").asInt(0);
                if (integer8 > 0 && integer8 < EntityMinecartIdentifiersFix.MINECARTS.size()) {
                    string7 = EntityMinecartIdentifiersFix.MINECARTS.get(integer8);
                }
                else {
                    string7 = "MinecartRideable";
                }
                return Pair.of(string7, ((Optional)((Type)taggedChoiceType5.types().get(string7)).read(typed5.write()).getSecond()).orElseThrow(() -> new IllegalStateException("Could not read the new minecart.")));
            }
            else {
                return pair;
            }
        });
    }
    
    static {
        MINECARTS = Lists.<String>newArrayList("MinecartRideable", "MinecartChest", "MinecartFurnace");
    }
}
