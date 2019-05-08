package net.minecraft.recipe;

import net.minecraft.util.PacketByteBuf;
import net.minecraft.item.ItemProvider;
import net.minecraft.util.registry.Registry;
import net.minecraft.item.Item;
import com.google.gson.JsonElement;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonObject;
import net.minecraft.util.DefaultedList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Inventory;

public abstract class CuttingRecipe implements Recipe<Inventory>
{
    protected final Ingredient ingredient;
    protected final ItemStack result;
    private final RecipeType<?> type;
    private final RecipeSerializer<?> serializer;
    protected final Identifier id;
    protected final String group;
    
    public CuttingRecipe(final RecipeType<?> type, final RecipeSerializer<?> serializer, final Identifier id, final String group, final Ingredient ingredient, final ItemStack result) {
        this.type = type;
        this.serializer = serializer;
        this.id = id;
        this.group = group;
        this.ingredient = ingredient;
        this.result = result;
    }
    
    @Override
    public RecipeType<?> getType() {
        return this.type;
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return this.serializer;
    }
    
    @Override
    public Identifier getId() {
        return this.id;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public String getGroup() {
        return this.group;
    }
    
    @Override
    public ItemStack getOutput() {
        return this.result;
    }
    
    @Override
    public DefaultedList<Ingredient> getPreviewInputs() {
        final DefaultedList<Ingredient> defaultedList1 = DefaultedList.<Ingredient>create();
        defaultedList1.add(this.ingredient);
        return defaultedList1;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean fits(final int width, final int height) {
        return true;
    }
    
    @Override
    public ItemStack craft(final Inventory inv) {
        return this.result.copy();
    }
    
    public static class Serializer<T extends CuttingRecipe> implements RecipeSerializer<T>
    {
        final a<T> t;
        
        protected Serializer(final a<T> a) {
            this.t = a;
        }
        
        @Override
        public T read(final Identifier id, final JsonObject json) {
            final String string3 = JsonHelper.getString(json, "group", "");
            Ingredient ingredient4;
            if (JsonHelper.hasArray(json, "ingredient")) {
                ingredient4 = Ingredient.fromJson(JsonHelper.getArray(json, "ingredient"));
            }
            else {
                ingredient4 = Ingredient.fromJson(JsonHelper.getObject(json, "ingredient"));
            }
            final String string4 = JsonHelper.getString(json, "result");
            final int integer6 = JsonHelper.getInt(json, "count");
            final ItemStack itemStack7 = new ItemStack(Registry.ITEM.get(new Identifier(string4)), integer6);
            return this.t.create(id, string3, ingredient4, itemStack7);
        }
        
        @Override
        public T read(final Identifier id, final PacketByteBuf buf) {
            final String string3 = buf.readString(32767);
            final Ingredient ingredient4 = Ingredient.fromPacket(buf);
            final ItemStack itemStack5 = buf.readItemStack();
            return this.t.create(id, string3, ingredient4, itemStack5);
        }
        
        @Override
        public void write(final PacketByteBuf buf, final T recipe) {
            buf.writeString(recipe.group);
            recipe.ingredient.write(buf);
            buf.writeItemStack(recipe.result);
        }
        
        interface a<T extends CuttingRecipe>
        {
            T create(final Identifier arg1, final String arg2, final Ingredient arg3, final ItemStack arg4);
        }
    }
}
