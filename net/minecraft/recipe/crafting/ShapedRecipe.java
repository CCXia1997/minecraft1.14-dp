package net.minecraft.recipe.crafting;

import net.minecraft.recipe.Recipe;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemProvider;
import com.google.gson.JsonParseException;
import net.minecraft.util.registry.Registry;
import net.minecraft.item.Item;
import java.util.Iterator;
import com.google.gson.JsonElement;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonArray;
import com.google.common.annotations.VisibleForTesting;
import java.util.Set;
import com.google.gson.JsonSyntaxException;
import com.google.common.collect.Sets;
import java.util.Map;
import net.minecraft.world.World;
import net.minecraft.inventory.CraftingInventory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.DefaultedList;

public class ShapedRecipe implements CraftingRecipe
{
    private final int width;
    private final int height;
    private final DefaultedList<Ingredient> inputs;
    private final ItemStack output;
    private final Identifier id;
    private final String group;
    
    public ShapedRecipe(final Identifier id, final String group, final int width, final int height, final DefaultedList<Ingredient> ingredients, final ItemStack output) {
        this.id = id;
        this.group = group;
        this.width = width;
        this.height = height;
        this.inputs = ingredients;
        this.output = output;
    }
    
    @Override
    public Identifier getId() {
        return this.id;
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SHAPED;
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
        return this.inputs;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean fits(final int width, final int height) {
        return width >= this.width && height >= this.height;
    }
    
    @Override
    public boolean matches(final CraftingInventory inv, final World world) {
        for (int integer3 = 0; integer3 <= inv.getWidth() - this.width; ++integer3) {
            for (int integer4 = 0; integer4 <= inv.getHeight() - this.height; ++integer4) {
                if (this.matchesSmall(inv, integer3, integer4, true)) {
                    return true;
                }
                if (this.matchesSmall(inv, integer3, integer4, false)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean matchesSmall(final CraftingInventory inv, final int offsetX, final int offsetY, final boolean boolean4) {
        for (int integer5 = 0; integer5 < inv.getWidth(); ++integer5) {
            for (int integer6 = 0; integer6 < inv.getHeight(); ++integer6) {
                final int integer7 = integer5 - offsetX;
                final int integer8 = integer6 - offsetY;
                Ingredient ingredient9 = Ingredient.EMPTY;
                if (integer7 >= 0 && integer8 >= 0 && integer7 < this.width && integer8 < this.height) {
                    if (boolean4) {
                        ingredient9 = this.inputs.get(this.width - integer7 - 1 + integer8 * this.width);
                    }
                    else {
                        ingredient9 = this.inputs.get(integer7 + integer8 * this.width);
                    }
                }
                if (!ingredient9.a(inv.getInvStack(integer5 + integer6 * inv.getWidth()))) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public ItemStack craft(final CraftingInventory inv) {
        return this.getOutput().copy();
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    private static DefaultedList<Ingredient> getIngredients(final String[] pattern, final Map<String, Ingredient> key, final int width, final int height) {
        final DefaultedList<Ingredient> defaultedList5 = DefaultedList.<Ingredient>create(width * height, Ingredient.EMPTY);
        final Set<String> set6 = Sets.newHashSet(key.keySet());
        set6.remove(" ");
        for (int integer7 = 0; integer7 < pattern.length; ++integer7) {
            for (int integer8 = 0; integer8 < pattern[integer7].length(); ++integer8) {
                final String string9 = pattern[integer7].substring(integer8, integer8 + 1);
                final Ingredient ingredient10 = key.get(string9);
                if (ingredient10 == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + string9 + "' but it's not defined in the key");
                }
                set6.remove(string9);
                defaultedList5.set(integer8 + width * integer7, ingredient10);
            }
        }
        if (!set6.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set6);
        }
        return defaultedList5;
    }
    
    @VisibleForTesting
    static String[] combinePattern(final String... lines) {
        int integer2 = Integer.MAX_VALUE;
        int integer3 = 0;
        int integer4 = 0;
        int integer5 = 0;
        for (int integer6 = 0; integer6 < lines.length; ++integer6) {
            final String string7 = lines[integer6];
            integer2 = Math.min(integer2, findNextIngredient(string7));
            final int integer7 = findNextIngredientReverse(string7);
            integer3 = Math.max(integer3, integer7);
            if (integer7 < 0) {
                if (integer4 == integer6) {
                    ++integer4;
                }
                ++integer5;
            }
            else {
                integer5 = 0;
            }
        }
        if (lines.length == integer5) {
            return new String[0];
        }
        final String[] arr6 = new String[lines.length - integer5 - integer4];
        for (int integer8 = 0; integer8 < arr6.length; ++integer8) {
            arr6[integer8] = lines[integer8 + integer4].substring(integer2, integer3 + 1);
        }
        return arr6;
    }
    
    private static int findNextIngredient(final String pattern) {
        int integer2;
        for (integer2 = 0; integer2 < pattern.length() && pattern.charAt(integer2) == ' '; ++integer2) {}
        return integer2;
    }
    
    private static int findNextIngredientReverse(final String pattern) {
        int integer2;
        for (integer2 = pattern.length() - 1; integer2 >= 0 && pattern.charAt(integer2) == ' '; --integer2) {}
        return integer2;
    }
    
    private static String[] getPattern(final JsonArray json) {
        final String[] arr2 = new String[json.size()];
        if (arr2.length > 3) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
        }
        if (arr2.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        }
        for (int integer3 = 0; integer3 < arr2.length; ++integer3) {
            final String string4 = JsonHelper.asString(json.get(integer3), "pattern[" + integer3 + "]");
            if (string4.length() > 3) {
                throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
            }
            if (integer3 > 0 && arr2[0].length() != string4.length()) {
                throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
            }
            arr2[integer3] = string4;
        }
        return arr2;
    }
    
    private static Map<String, Ingredient> getComponents(final JsonObject json) {
        final Map<String, Ingredient> map2 = Maps.newHashMap();
        for (final Map.Entry<String, JsonElement> entry4 : json.entrySet()) {
            if (entry4.getKey().length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + entry4.getKey() + "' is an invalid symbol (must be 1 character only).");
            }
            if (" ".equals(entry4.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }
            map2.put(entry4.getKey(), Ingredient.fromJson(entry4.getValue()));
        }
        map2.put(" ", Ingredient.EMPTY);
        return map2;
    }
    
    public static ItemStack getItemStack(final JsonObject json) {
        final String string2 = JsonHelper.getString(json, "item");
        final Object o;
        final String s;
        final Item item3 = Registry.ITEM.getOrEmpty(new Identifier(string2)).<Throwable>orElseThrow(() -> {
            new JsonSyntaxException("Unknown item '" + s + "'");
            return o;
        });
        if (json.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        }
        final int integer4 = JsonHelper.getInt(json, "count", 1);
        return new ItemStack(item3, integer4);
    }
    
    public static class Serializer implements RecipeSerializer<ShapedRecipe>
    {
        @Override
        public ShapedRecipe read(final Identifier id, final JsonObject json) {
            final String string3 = JsonHelper.getString(json, "group", "");
            final Map<String, Ingredient> map4 = getComponents(JsonHelper.getObject(json, "key"));
            final String[] arr5 = ShapedRecipe.combinePattern(getPattern(JsonHelper.getArray(json, "pattern")));
            final int integer6 = arr5[0].length();
            final int integer7 = arr5.length;
            final DefaultedList<Ingredient> defaultedList8 = getIngredients(arr5, map4, integer6, integer7);
            final ItemStack itemStack9 = ShapedRecipe.getItemStack(JsonHelper.getObject(json, "result"));
            return new ShapedRecipe(id, string3, integer6, integer7, defaultedList8, itemStack9);
        }
        
        @Override
        public ShapedRecipe read(final Identifier id, final PacketByteBuf buf) {
            final int integer3 = buf.readVarInt();
            final int integer4 = buf.readVarInt();
            final String string5 = buf.readString(32767);
            final DefaultedList<Ingredient> defaultedList6 = DefaultedList.<Ingredient>create(integer3 * integer4, Ingredient.EMPTY);
            for (int integer5 = 0; integer5 < defaultedList6.size(); ++integer5) {
                defaultedList6.set(integer5, Ingredient.fromPacket(buf));
            }
            final ItemStack itemStack7 = buf.readItemStack();
            return new ShapedRecipe(id, string5, integer3, integer4, defaultedList6, itemStack7);
        }
        
        @Override
        public void write(final PacketByteBuf buf, final ShapedRecipe recipe) {
            buf.writeVarInt(recipe.width);
            buf.writeVarInt(recipe.height);
            buf.writeString(recipe.group);
            for (final Ingredient ingredient4 : recipe.inputs) {
                ingredient4.write(buf);
            }
            buf.writeItemStack(recipe.output);
        }
    }
}
