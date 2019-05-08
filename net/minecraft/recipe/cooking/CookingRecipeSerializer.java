package net.minecraft.recipe.cooking;

import net.minecraft.recipe.Recipe;
import net.minecraft.util.PacketByteBuf;
import com.google.gson.JsonElement;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import net.minecraft.item.ItemProvider;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.recipe.RecipeSerializer;

public class CookingRecipeSerializer<T extends CookingRecipe> implements RecipeSerializer<T>
{
    private final int cookingTime;
    private final RecipeFactory<T> recipeFactory;
    
    public CookingRecipeSerializer(final RecipeFactory<T> recipeFactory, final int cookingTime) {
        this.cookingTime = cookingTime;
        this.recipeFactory = recipeFactory;
    }
    
    @Override
    public T read(final Identifier id, final JsonObject json) {
        final String string3 = JsonHelper.getString(json, "group", "");
        final JsonElement jsonElement4 = JsonHelper.hasArray(json, "ingredient") ? JsonHelper.getArray(json, "ingredient") : JsonHelper.getObject(json, "ingredient");
        final Ingredient ingredient5 = Ingredient.fromJson(jsonElement4);
        final String string4 = JsonHelper.getString(json, "result");
        final Identifier identifier7 = new Identifier(string4);
        final Object o;
        final String s;
        final ItemStack itemStack8 = new ItemStack(Registry.ITEM.getOrEmpty(identifier7).<Throwable>orElseThrow(() -> {
            new IllegalStateException("Item: " + s + " does not exist");
            return o;
        }));
        final float float9 = JsonHelper.getFloat(json, "experience", 0.0f);
        final int integer10 = JsonHelper.getInt(json, "cookingtime", this.cookingTime);
        return this.recipeFactory.create(id, string3, ingredient5, itemStack8, float9, integer10);
    }
    
    @Override
    public T read(final Identifier id, final PacketByteBuf buf) {
        final String string3 = buf.readString(32767);
        final Ingredient ingredient4 = Ingredient.fromPacket(buf);
        final ItemStack itemStack5 = buf.readItemStack();
        final float float6 = buf.readFloat();
        final int integer7 = buf.readVarInt();
        return this.recipeFactory.create(id, string3, ingredient4, itemStack5, float6, integer7);
    }
    
    @Override
    public void write(final PacketByteBuf buf, final T recipe) {
        buf.writeString(recipe.group);
        recipe.input.write(buf);
        buf.writeItemStack(recipe.output);
        buf.writeFloat(recipe.experience);
        buf.writeVarInt(recipe.cookTime);
    }
    
    interface RecipeFactory<T extends CookingRecipe>
    {
        T create(final Identifier arg1, final String arg2, final Ingredient arg3, final ItemStack arg4, final float arg5, final int arg6);
    }
}
