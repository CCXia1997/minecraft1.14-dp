package net.minecraft.data.server.recipe;

import javax.annotation.Nullable;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import com.google.gson.JsonObject;

public interface RecipeJsonProvider
{
    void serialize(final JsonObject arg1);
    
    default JsonObject toJson() {
        final JsonObject jsonObject1 = new JsonObject();
        jsonObject1.addProperty("type", Registry.RECIPE_SERIALIZER.getId(this.getSerializer()).toString());
        this.serialize(jsonObject1);
        return jsonObject1;
    }
    
    Identifier getRecipeId();
    
    RecipeSerializer<?> getSerializer();
    
    @Nullable
    JsonObject toAdvancementJson();
    
    @Nullable
    Identifier getAdvancementId();
}
