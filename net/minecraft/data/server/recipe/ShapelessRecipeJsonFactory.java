package net.minecraft.data.server.recipe;

import javax.annotation.Nullable;
import net.minecraft.recipe.RecipeSerializer;
import java.util.Iterator;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import net.minecraft.advancement.CriteriaMerger;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import java.util.function.Consumer;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.tag.Tag;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.recipe.Ingredient;
import java.util.List;
import net.minecraft.item.Item;
import org.apache.logging.log4j.Logger;

public class ShapelessRecipeJsonFactory
{
    private static final Logger LOGGER;
    private final Item output;
    private final int outputCount;
    private final List<Ingredient> inputs;
    private final Advancement.Task builder;
    private String group;
    
    public ShapelessRecipeJsonFactory(final ItemProvider itemProvider, final int outputCount) {
        this.inputs = Lists.newArrayList();
        this.builder = Advancement.Task.create();
        this.output = itemProvider.getItem();
        this.outputCount = outputCount;
    }
    
    public static ShapelessRecipeJsonFactory create(final ItemProvider output) {
        return new ShapelessRecipeJsonFactory(output, 1);
    }
    
    public static ShapelessRecipeJsonFactory create(final ItemProvider output, final int outputCount) {
        return new ShapelessRecipeJsonFactory(output, outputCount);
    }
    
    public ShapelessRecipeJsonFactory input(final Tag<Item> tag) {
        return this.input(Ingredient.fromTag(tag));
    }
    
    public ShapelessRecipeJsonFactory input(final ItemProvider itemProvider) {
        return this.input(itemProvider, 1);
    }
    
    public ShapelessRecipeJsonFactory input(final ItemProvider itemProvider, final int size) {
        for (int integer3 = 0; integer3 < size; ++integer3) {
            this.input(Ingredient.ofItems(itemProvider));
        }
        return this;
    }
    
    public ShapelessRecipeJsonFactory input(final Ingredient ingredient) {
        return this.input(ingredient, 1);
    }
    
    public ShapelessRecipeJsonFactory input(final Ingredient ingredient, final int size) {
        for (int integer3 = 0; integer3 < size; ++integer3) {
            this.inputs.add(ingredient);
        }
        return this;
    }
    
    public ShapelessRecipeJsonFactory criterion(final String criterionName, final CriterionConditions conditions) {
        this.builder.criterion(criterionName, conditions);
        return this;
    }
    
    public ShapelessRecipeJsonFactory group(final String group) {
        this.group = group;
        return this;
    }
    
    public void offerTo(final Consumer<RecipeJsonProvider> exporter) {
        this.offerTo(exporter, Registry.ITEM.getId(this.output));
    }
    
    public void offerTo(final Consumer<RecipeJsonProvider> exporter, final String recipeIdStr) {
        final Identifier identifier3 = Registry.ITEM.getId(this.output);
        if (new Identifier(recipeIdStr).equals(identifier3)) {
            throw new IllegalStateException("Shapeless Recipe " + recipeIdStr + " should remove its 'save' argument");
        }
        this.offerTo(exporter, new Identifier(recipeIdStr));
    }
    
    public void offerTo(final Consumer<RecipeJsonProvider> exporter, final Identifier recipeId) {
        this.validate(recipeId);
        this.builder.parent(new Identifier("recipes/root")).criterion("has_the_recipe", new RecipeUnlockedCriterion.Conditions(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(CriteriaMerger.OR);
        exporter.accept(new ShapelessRecipeJsonProvider(recipeId, this.output, this.outputCount, (this.group == null) ? "" : this.group, this.inputs, this.builder, new Identifier(recipeId.getNamespace(), "recipes/" + this.output.getItemGroup().getName() + "/" + recipeId.getPath())));
    }
    
    private void validate(final Identifier recipeId) {
        if (this.builder.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public static class ShapelessRecipeJsonProvider implements RecipeJsonProvider
    {
        private final Identifier recipeId;
        private final Item output;
        private final int count;
        private final String group;
        private final List<Ingredient> inputs;
        private final Advancement.Task builder;
        private final Identifier advancementId;
        
        public ShapelessRecipeJsonProvider(final Identifier recipeId, final Item output, final int outputCount, final String group, final List<Ingredient> inputs, final Advancement.Task builder, final Identifier advancementId) {
            this.recipeId = recipeId;
            this.output = output;
            this.count = outputCount;
            this.group = group;
            this.inputs = inputs;
            this.builder = builder;
            this.advancementId = advancementId;
        }
        
        @Override
        public void serialize(final JsonObject json) {
            if (!this.group.isEmpty()) {
                json.addProperty("group", this.group);
            }
            final JsonArray jsonArray2 = new JsonArray();
            for (final Ingredient ingredient4 : this.inputs) {
                jsonArray2.add(ingredient4.toJson());
            }
            json.add("ingredients", jsonArray2);
            final JsonObject jsonObject3 = new JsonObject();
            jsonObject3.addProperty("item", Registry.ITEM.getId(this.output).toString());
            if (this.count > 1) {
                jsonObject3.addProperty("count", this.count);
            }
            json.add("result", jsonObject3);
        }
        
        @Override
        public RecipeSerializer<?> getSerializer() {
            return RecipeSerializer.SHAPELESS;
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
