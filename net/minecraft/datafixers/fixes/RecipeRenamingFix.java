package net.minecraft.datafixers.fixes;

import com.google.common.collect.ImmutableMap;
import java.util.function.Function;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.types.Type;
import java.util.Objects;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import com.mojang.datafixers.DataFix;

public class RecipeRenamingFix extends DataFix
{
    private static final Map<String, String> recipes;
    
    public RecipeRenamingFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    protected TypeRewriteRule makeRule() {
        final Type<Pair<String, String>> type1 = (Type<Pair<String, String>>)DSL.named(TypeReferences.RECIPE.typeName(), DSL.namespacedString());
        if (!Objects.equals(type1, this.getInputSchema().getType(TypeReferences.RECIPE))) {
            throw new IllegalStateException("Recipe type is not what was expected.");
        }
        return this.fixTypeEverywhere("Recipes renamening fix", (Type)type1, dynamicOps -> pair -> pair.mapSecond(string -> RecipeRenamingFix.recipes.getOrDefault(string, string)));
    }
    
    static {
        recipes = ImmutableMap.<String, String>builder().put("minecraft:acacia_bark", "minecraft:acacia_wood").put("minecraft:birch_bark", "minecraft:birch_wood").put("minecraft:dark_oak_bark", "minecraft:dark_oak_wood").put("minecraft:jungle_bark", "minecraft:jungle_wood").put("minecraft:oak_bark", "minecraft:oak_wood").put("minecraft:spruce_bark", "minecraft:spruce_wood").build();
    }
}
