package net.minecraft.data.server.recipe;

import javax.annotation.Nullable;
import net.minecraft.recipe.RecipeSerializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import java.util.Iterator;
import java.util.Set;
import com.google.common.collect.Sets;
import net.minecraft.advancement.CriteriaMerger;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import java.util.function.Consumer;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.tag.Tag;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.recipe.Ingredient;
import java.util.Map;
import java.util.List;
import net.minecraft.item.Item;
import org.apache.logging.log4j.Logger;

public class ShapedRecipeJsonFactory
{
    private static final Logger LOGGER;
    private final Item output;
    private final int outputCount;
    private final List<String> pattern;
    private final Map<Character, Ingredient> inputs;
    private final Advancement.Task builder;
    private String group;
    
    public ShapedRecipeJsonFactory(final ItemProvider output, final int outputCount) {
        this.pattern = Lists.newArrayList();
        this.inputs = Maps.newLinkedHashMap();
        this.builder = Advancement.Task.create();
        this.output = output.getItem();
        this.outputCount = outputCount;
    }
    
    public static ShapedRecipeJsonFactory create(final ItemProvider output) {
        return create(output, 1);
    }
    
    public static ShapedRecipeJsonFactory create(final ItemProvider output, final int outputCount) {
        return new ShapedRecipeJsonFactory(output, outputCount);
    }
    
    public ShapedRecipeJsonFactory input(final Character c, final Tag<Item> tag) {
        return this.input(c, Ingredient.fromTag(tag));
    }
    
    public ShapedRecipeJsonFactory input(final Character c, final ItemProvider itemProvider) {
        return this.input(c, Ingredient.ofItems(itemProvider));
    }
    
    public ShapedRecipeJsonFactory input(final Character c, final Ingredient ingredient) {
        if (this.inputs.containsKey(c)) {
            throw new IllegalArgumentException("Symbol '" + c + "' is already defined!");
        }
        if (c == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        }
        this.inputs.put(c, ingredient);
        return this;
    }
    
    public ShapedRecipeJsonFactory pattern(final String patternStr) {
        if (!this.pattern.isEmpty() && patternStr.length() != this.pattern.get(0).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        }
        this.pattern.add(patternStr);
        return this;
    }
    
    public ShapedRecipeJsonFactory criterion(final String criterionName, final CriterionConditions conditions) {
        this.builder.criterion(criterionName, conditions);
        return this;
    }
    
    public ShapedRecipeJsonFactory group(final String group) {
        this.group = group;
        return this;
    }
    
    public void offerTo(final Consumer<RecipeJsonProvider> exporter) {
        this.offerTo(exporter, Registry.ITEM.getId(this.output));
    }
    
    public void offerTo(final Consumer<RecipeJsonProvider> exporter, final String recipeIdStr) {
        final Identifier identifier3 = Registry.ITEM.getId(this.output);
        if (new Identifier(recipeIdStr).equals(identifier3)) {
            throw new IllegalStateException("Shaped Recipe " + recipeIdStr + " should remove its 'save' argument");
        }
        this.offerTo(exporter, new Identifier(recipeIdStr));
    }
    
    public void offerTo(final Consumer<RecipeJsonProvider> exporter, final Identifier recipeId) {
        this.validate(recipeId);
        this.builder.parent(new Identifier("recipes/root")).criterion("has_the_recipe", new RecipeUnlockedCriterion.Conditions(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(CriteriaMerger.OR);
        exporter.accept(new ShapedRecipeJsonProvider(recipeId, this.output, this.outputCount, (this.group == null) ? "" : this.group, this.pattern, this.inputs, this.builder, new Identifier(recipeId.getNamespace(), "recipes/" + this.output.getItemGroup().getName() + "/" + recipeId.getPath())));
    }
    
    private void validate(final Identifier recipeId) {
        if (this.pattern.isEmpty()) {
            throw new IllegalStateException("No pattern is defined for shaped recipe " + recipeId + "!");
        }
        final Set<Character> set2 = Sets.newHashSet(this.inputs.keySet());
        set2.remove(' ');
        for (final String string4 : this.pattern) {
            for (int integer5 = 0; integer5 < string4.length(); ++integer5) {
                final char character6 = string4.charAt(integer5);
                if (!this.inputs.containsKey(character6) && character6 != ' ') {
                    throw new IllegalStateException("Pattern in recipe " + recipeId + " uses undefined symbol '" + character6 + "'");
                }
                set2.remove(character6);
            }
        }
        if (!set2.isEmpty()) {
            throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + recipeId);
        }
        if (this.pattern.size() == 1 && this.pattern.get(0).length() == 1) {
            throw new IllegalStateException("Shaped recipe " + recipeId + " only takes in a single item - should it be a shapeless recipe instead?");
        }
        if (this.builder.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    class ShapedRecipeJsonProvider implements RecipeJsonProvider
    {
        private final Identifier recipeId;
        private final Item output;
        private final int resultCount;
        private final String group;
        private final List<String> pattern;
        private final Map<Character, Ingredient> inputs;
        private final Advancement.Task builder;
        private final Identifier advancementId;
        
        public ShapedRecipeJsonProvider(final Identifier recipeId, final Item output, final int outputCount, final String group, final List<String> pattern, final Map<Character, Ingredient> inputs, final Advancement.Task builder, final Identifier advancementId) {
            this.recipeId = recipeId;
            this.output = output;
            this.resultCount = outputCount;
            this.group = group;
            this.pattern = pattern;
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
            for (final String string4 : this.pattern) {
                jsonArray2.add(string4);
            }
            json.add("pattern", jsonArray2);
            final JsonObject jsonObject3 = new JsonObject();
            for (final Map.Entry<Character, Ingredient> entry5 : this.inputs.entrySet()) {
                jsonObject3.add(String.valueOf(entry5.getKey()), entry5.getValue().toJson());
            }
            json.add("key", jsonObject3);
            final JsonObject jsonObject4 = new JsonObject();
            jsonObject4.addProperty("item", Registry.ITEM.getId(this.output).toString());
            if (this.resultCount > 1) {
                jsonObject4.addProperty("count", this.resultCount);
            }
            json.add("result", jsonObject4);
        }
        
        @Override
        public RecipeSerializer<?> getSerializer() {
            return RecipeSerializer.SHAPED;
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
