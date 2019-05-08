package net.minecraft.data.server.recipe;

import javax.annotation.Nullable;
import com.google.gson.JsonObject;
import net.minecraft.advancement.CriteriaMerger;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import java.util.function.Consumer;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.item.ItemProvider;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.advancement.Advancement;
import net.minecraft.recipe.Ingredient;
import net.minecraft.item.Item;

public class SingleItemRecipeJsonFactory
{
    private final Item output;
    private final Ingredient input;
    private final int count;
    private final Advancement.Task builder;
    private String group;
    private final RecipeSerializer<?> serializer;
    
    public SingleItemRecipeJsonFactory(final RecipeSerializer<?> serializer, final Ingredient input, final ItemProvider output, final int outputCount) {
        this.builder = Advancement.Task.create();
        this.serializer = serializer;
        this.output = output.getItem();
        this.input = input;
        this.count = outputCount;
    }
    
    public static SingleItemRecipeJsonFactory create(final Ingredient input, final ItemProvider output) {
        return new SingleItemRecipeJsonFactory(RecipeSerializer.s, input, output, 1);
    }
    
    public static SingleItemRecipeJsonFactory create(final Ingredient input, final ItemProvider output, final int outputCount) {
        return new SingleItemRecipeJsonFactory(RecipeSerializer.s, input, output, outputCount);
    }
    
    public SingleItemRecipeJsonFactory create(final String criterionName, final CriterionConditions conditions) {
        this.builder.criterion(criterionName, conditions);
        return this;
    }
    
    public void offerTo(final Consumer<RecipeJsonProvider> exporter, final String recipeIdStr) {
        final Identifier identifier3 = Registry.ITEM.getId(this.output);
        if (new Identifier(recipeIdStr).equals(identifier3)) {
            throw new IllegalStateException("Single Item Recipe " + recipeIdStr + " should remove its 'save' argument");
        }
        this.offerTo(exporter, new Identifier(recipeIdStr));
    }
    
    public void offerTo(final Consumer<RecipeJsonProvider> exporter, final Identifier recipeId) {
        this.validate(recipeId);
        this.builder.parent(new Identifier("recipes/root")).criterion("has_the_recipe", new RecipeUnlockedCriterion.Conditions(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(CriteriaMerger.OR);
        exporter.accept(new SingleItemRecipeJsonProvider(recipeId, this.serializer, (this.group == null) ? "" : this.group, this.input, this.output, this.count, this.builder, new Identifier(recipeId.getNamespace(), "recipes/" + this.output.getItemGroup().getName() + "/" + recipeId.getPath())));
    }
    
    private void validate(final Identifier recipeId) {
        if (this.builder.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
    }
    
    public static class SingleItemRecipeJsonProvider implements RecipeJsonProvider
    {
        private final Identifier recipeId;
        private final String group;
        private final Ingredient input;
        private final Item output;
        private final int count;
        private final Advancement.Task builder;
        private final Identifier advancementId;
        private final RecipeSerializer<?> serializer;
        
        public SingleItemRecipeJsonProvider(final Identifier recipeId, final RecipeSerializer<?> serializer, final String group, final Ingredient input, final Item output, final int outputCount, final Advancement.Task builder, final Identifier advancementId) {
            this.recipeId = recipeId;
            this.serializer = serializer;
            this.group = group;
            this.input = input;
            this.output = output;
            this.count = outputCount;
            this.builder = builder;
            this.advancementId = advancementId;
        }
        
        @Override
        public void serialize(final JsonObject json) {
            if (!this.group.isEmpty()) {
                json.addProperty("group", this.group);
            }
            json.add("ingredient", this.input.toJson());
            json.addProperty("result", Registry.ITEM.getId(this.output).toString());
            json.addProperty("count", this.count);
        }
        
        @Override
        public Identifier getRecipeId() {
            return this.recipeId;
        }
        
        @Override
        public RecipeSerializer<?> getSerializer() {
            return this.serializer;
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
