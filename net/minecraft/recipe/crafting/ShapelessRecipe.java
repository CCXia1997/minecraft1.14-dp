package net.minecraft.recipe.crafting;

import java.util.Iterator;
import net.minecraft.util.PacketByteBuf;
import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonObject;
import net.minecraft.inventory.Inventory;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.world.World;
import net.minecraft.inventory.CraftingInventory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.DefaultedList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ShapelessRecipe implements CraftingRecipe
{
    private final Identifier id;
    private final String group;
    private final ItemStack output;
    private final DefaultedList<Ingredient> input;
    
    public ShapelessRecipe(final Identifier id, final String group, final ItemStack output, final DefaultedList<Ingredient> input) {
        this.id = id;
        this.group = group;
        this.output = output;
        this.input = input;
    }
    
    @Override
    public Identifier getId() {
        return this.id;
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SHAPELESS;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public String getGroup() {
        return this.group;
    }
    
    @Override
    public ItemStack getOutput() {
        return this.output;
    }
    
    @Override
    public DefaultedList<Ingredient> getPreviewInputs() {
        return this.input;
    }
    
    @Override
    public boolean matches(final CraftingInventory inv, final World world) {
        final RecipeFinder recipeFinder3 = new RecipeFinder();
        int integer4 = 0;
        for (int integer5 = 0; integer5 < inv.getInvSize(); ++integer5) {
            final ItemStack itemStack6 = inv.getInvStack(integer5);
            if (!itemStack6.isEmpty()) {
                ++integer4;
                recipeFinder3.a(itemStack6, 1);
            }
        }
        return integer4 == this.input.size() && recipeFinder3.findRecipe(this, null);
    }
    
    @Override
    public ItemStack craft(final CraftingInventory inv) {
        return this.output.copy();
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean fits(final int width, final int height) {
        return width * height >= this.input.size();
    }
    
    public static class Serializer implements RecipeSerializer<ShapelessRecipe>
    {
        @Override
        public ShapelessRecipe read(final Identifier id, final JsonObject json) {
            final String string3 = JsonHelper.getString(json, "group", "");
            final DefaultedList<Ingredient> defaultedList4 = getIngredients(JsonHelper.getArray(json, "ingredients"));
            if (defaultedList4.isEmpty()) {
                throw new JsonParseException("No ingredients for shapeless recipe");
            }
            if (defaultedList4.size() > 9) {
                throw new JsonParseException("Too many ingredients for shapeless recipe");
            }
            final ItemStack itemStack5 = ShapedRecipe.getItemStack(JsonHelper.getObject(json, "result"));
            return new ShapelessRecipe(id, string3, itemStack5, defaultedList4);
        }
        
        private static DefaultedList<Ingredient> getIngredients(final JsonArray json) {
            final DefaultedList<Ingredient> defaultedList2 = DefaultedList.<Ingredient>create();
            for (int integer3 = 0; integer3 < json.size(); ++integer3) {
                final Ingredient ingredient4 = Ingredient.fromJson(json.get(integer3));
                if (!ingredient4.isEmpty()) {
                    defaultedList2.add(ingredient4);
                }
            }
            return defaultedList2;
        }
        
        @Override
        public ShapelessRecipe read(final Identifier id, final PacketByteBuf buf) {
            final String string3 = buf.readString(32767);
            final int integer4 = buf.readVarInt();
            final DefaultedList<Ingredient> defaultedList5 = DefaultedList.<Ingredient>create(integer4, Ingredient.EMPTY);
            for (int integer5 = 0; integer5 < defaultedList5.size(); ++integer5) {
                defaultedList5.set(integer5, Ingredient.fromPacket(buf));
            }
            final ItemStack itemStack6 = buf.readItemStack();
            return new ShapelessRecipe(id, string3, itemStack6, defaultedList5);
        }
        
        @Override
        public void write(final PacketByteBuf buf, final ShapelessRecipe recipe) {
            buf.writeString(recipe.group);
            buf.writeVarInt(recipe.input.size());
            for (final Ingredient ingredient4 : recipe.input) {
                ingredient4.write(buf);
            }
            buf.writeItemStack(recipe.output);
        }
    }
}
