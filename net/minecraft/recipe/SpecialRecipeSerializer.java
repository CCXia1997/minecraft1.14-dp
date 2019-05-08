package net.minecraft.recipe;

import net.minecraft.util.PacketByteBuf;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import java.util.function.Function;

public class SpecialRecipeSerializer<T extends Recipe<?>> implements RecipeSerializer<T>
{
    private final Function<Identifier, T> id;
    
    public SpecialRecipeSerializer(final Function<Identifier, T> function) {
        this.id = function;
    }
    
    @Override
    public T read(final Identifier id, final JsonObject json) {
        return this.id.apply(id);
    }
    
    @Override
    public T read(final Identifier id, final PacketByteBuf buf) {
        return this.id.apply(id);
    }
    
    @Override
    public void write(final PacketByteBuf buf, final T recipe) {
    }
}
