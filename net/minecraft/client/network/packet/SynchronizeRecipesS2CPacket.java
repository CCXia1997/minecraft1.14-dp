package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.recipe.RecipeSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Iterator;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import com.google.common.collect.Lists;
import java.util.Collection;
import net.minecraft.recipe.Recipe;
import java.util.List;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class SynchronizeRecipesS2CPacket implements Packet<ClientPlayPacketListener>
{
    private List<Recipe<?>> recipes;
    
    public SynchronizeRecipesS2CPacket() {
    }
    
    public SynchronizeRecipesS2CPacket(final Collection<Recipe<?>> collection) {
        this.recipes = Lists.newArrayList(collection);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onSynchronizeRecipes(this);
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.recipes = Lists.newArrayList();
        for (int integer2 = buf.readVarInt(), integer3 = 0; integer3 < integer2; ++integer3) {
            this.recipes.add(readRecipe(buf));
        }
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.recipes.size());
        for (final Recipe<?> recipe3 : this.recipes) {
            SynchronizeRecipesS2CPacket.<Recipe<?>>writeRecipe(recipe3, buf);
        }
    }
    
    @Environment(EnvType.CLIENT)
    public List<Recipe<?>> getRecipes() {
        return this.recipes;
    }
    
    public static Recipe<?> readRecipe(final PacketByteBuf buf) {
        final Identifier identifier2 = buf.readIdentifier();
        final Identifier identifier3 = buf.readIdentifier();
        final Object o;
        final Object o2;
        return Registry.RECIPE_SERIALIZER.getOrEmpty(identifier2).<Throwable>orElseThrow(() -> {
            new IllegalArgumentException("Unknown recipe serializer " + o2);
            return o;
        }).read(identifier3, buf);
    }
    
    public static <T extends Recipe<?>> void writeRecipe(final T recipe, final PacketByteBuf buf) {
        buf.writeIdentifier(Registry.RECIPE_SERIALIZER.getId(recipe.getSerializer()));
        buf.writeIdentifier(recipe.getId());
        recipe.getSerializer().write(buf, recipe);
    }
}
