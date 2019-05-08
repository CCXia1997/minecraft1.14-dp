package net.minecraft.data.server.recipe;

import javax.annotation.Nullable;
import com.google.gson.JsonObject;
import net.minecraft.recipe.cooking.CookingRecipe;
import net.minecraft.advancement.CriteriaMerger;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import java.util.function.Consumer;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.item.ItemProvider;
import net.minecraft.recipe.cooking.CookingRecipeSerializer;
import net.minecraft.advancement.Advancement;
import net.minecraft.recipe.Ingredient;
import net.minecraft.item.Item;

public class CookingRecipeJsonFactory
{
    private final Item output;
    private final Ingredient input;
    private final float exp;
    private final int time;
    private final Advancement.Task builder;
    private String group;
    private final CookingRecipeSerializer<?> serializer;
    
    private CookingRecipeJsonFactory(final ItemProvider ouptut, final Ingredient input, final float exp, final int time, final CookingRecipeSerializer<?> serializer) {
        this.builder = Advancement.Task.create();
        this.output = ouptut.getItem();
        this.input = input;
        this.exp = exp;
        this.time = time;
        this.serializer = serializer;
    }
    
    public static CookingRecipeJsonFactory create(final Ingredient input, final ItemProvider output, final float exp, final int time, final CookingRecipeSerializer<?> serializer) {
        return new CookingRecipeJsonFactory(output, input, exp, time, serializer);
    }
    
    public static CookingRecipeJsonFactory createBlasting(final Ingredient input, final ItemProvider output, final float exp, final int time) {
        return create(input, output, exp, time, RecipeSerializer.BLASTING);
    }
    
    public static CookingRecipeJsonFactory createSmelting(final Ingredient input, final ItemProvider output, final float exp, final int time) {
        return create(input, output, exp, time, RecipeSerializer.SMELTING);
    }
    
    public CookingRecipeJsonFactory criterion(final String criterionName, final CriterionConditions conditions) {
        this.builder.criterion(criterionName, conditions);
        return this;
    }
    
    public void offerTo(final Consumer<RecipeJsonProvider> exporter) {
        this.offerTo(exporter, Registry.ITEM.getId(this.output));
    }
    
    public void offerTo(final Consumer<RecipeJsonProvider> exporter, final String recipeIdStr) {
        final Identifier identifier3 = Registry.ITEM.getId(this.output);
        final Identifier identifier4 = new Identifier(recipeIdStr);
        if (identifier4.equals(identifier3)) {
            throw new IllegalStateException("Recipe " + identifier4 + " should remove its 'save' argument");
        }
        this.offerTo(exporter, identifier4);
    }
    
    public void offerTo(final Consumer<RecipeJsonProvider> exporter, final Identifier recipeId) {
        this.validate(recipeId);
        this.builder.parent(new Identifier("recipes/root")).criterion("has_the_recipe", new RecipeUnlockedCriterion.Conditions(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(CriteriaMerger.OR);
        exporter.accept(new CookingRecipeJsonProvider(recipeId, (this.group == null) ? "" : this.group, this.input, this.output, this.exp, this.time, this.builder, new Identifier(recipeId.getNamespace(), "recipes/" + this.output.getItemGroup().getName() + "/" + recipeId.getPath()), this.serializer));
    }
    
    private void validate(final Identifier recipeId) {
        if (this.builder.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
    }
    
    public static class CookingRecipeJsonProvider implements RecipeJsonProvider
    {
        private final Identifier recipeId;
        private final String group;
        private final Ingredient ingredient;
        private final Item result;
        private final float experience;
        private final int cookingTime;
        private final Advancement.Task builder;
        private final Identifier advancementId;
        private final RecipeSerializer<? extends CookingRecipe> cookingRecipeSerializer;
        
        public CookingRecipeJsonProvider(final Identifier recipeId, final String group, final Ingredient input, final Item output, final float exp, final int time, final Advancement.Task builder, final Identifier advancementId, final RecipeSerializer<? extends CookingRecipe> serializer) {
            this.recipeId = recipeId;
            this.group = group;
            this.ingredient = input;
            this.result = output;
            this.experience = exp;
            this.cookingTime = time;
            this.builder = builder;
            this.advancementId = advancementId;
            this.cookingRecipeSerializer = serializer;
        }
        
        @Override
        public void serialize(final JsonObject json) {
            if (!this.group.isEmpty()) {
                json.addProperty("group", this.group);
            }
            json.add("ingredient", this.ingredient.toJson());
            json.addProperty("result", Registry.ITEM.getId(this.result).toString());
            json.addProperty("experience", this.experience);
            json.addProperty("cookingtime", this.cookingTime);
        }
        
        @Override
        public RecipeSerializer<?> getSerializer() {
            return this.cookingRecipeSerializer;
        }
        
        @Override
        public Identifier getRecipeId() {
            return this.recipeId;
        }
        
        @Nullable
        @Override
        public JsonObject toAdvancementJson() {
            return this.builder.toJson();
        }
        
        @Nullable
        @Override
        public Identifier getAdvancementId() {
            return this.advancementId;
        }
    }
}
