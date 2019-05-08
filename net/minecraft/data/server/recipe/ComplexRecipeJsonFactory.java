package net.minecraft.data.server.recipe;

import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.minecraft.recipe.RecipeSerializer;
import com.google.gson.JsonObject;
import java.util.function.Consumer;
import net.minecraft.recipe.SpecialRecipeSerializer;

public class ComplexRecipeJsonFactory
{
    private final SpecialRecipeSerializer<?> serializer;
    
    public ComplexRecipeJsonFactory(final SpecialRecipeSerializer<?> serializer) {
        this.serializer = serializer;
    }
    
    public static ComplexRecipeJsonFactory create(final SpecialRecipeSerializer<?> serializer) {
        return new ComplexRecipeJsonFactory(serializer);
    }
    
    public void offerTo(final Consumer<RecipeJsonProvider> exporter, final String recipeId) {
        exporter.accept(new RecipeJsonProvider() {
            @Override
            public void serialize(final JsonObject json) {
            }
            
            @Override
            public RecipeSerializer<?> getSerializer() {
                return ComplexRecipeJsonFactory.this.serializer;
            }
            
            @Override
            public Identifier getRecipeId() {
                return new Identifier(recipeId);
            }
            
            @Nullable
            @Override
            public JsonObject toAdvancementJson() {
                return null;
            }
            
            @Override
            public Identifier getAdvancementId() {
                return new Identifier("");
            }
        });
    }
}
