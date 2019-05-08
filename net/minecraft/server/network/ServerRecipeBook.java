package net.minecraft.server.network;

import java.util.AbstractList;
import org.apache.logging.log4j.LogManager;
import java.util.Optional;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.util.Identifier;
import java.util.List;
import net.minecraft.client.network.packet.UnlockRecipesS2CPacket;
import net.minecraft.advancement.criterion.Criterions;
import com.google.common.collect.Lists;
import net.minecraft.recipe.Recipe;
import java.util.Collection;
import net.minecraft.recipe.RecipeManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.recipe.book.RecipeBook;

public class ServerRecipeBook extends RecipeBook
{
    private static final Logger LOGGER;
    private final RecipeManager manager;
    
    public ServerRecipeBook(final RecipeManager recipeManager) {
        this.manager = recipeManager;
    }
    
    public int unlockRecipes(final Collection<Recipe<?>> collection, final ServerPlayerEntity serverPlayerEntity) {
        final List<Identifier> list3 = Lists.newArrayList();
        int integer4 = 0;
        for (final Recipe<?> recipe6 : collection) {
            final Identifier identifier7 = recipe6.getId();
            if (!this.recipes.contains(identifier7) && !recipe6.isIgnoredInRecipeBook()) {
                this.add(identifier7);
                this.display(identifier7);
                list3.add(identifier7);
                Criterions.RECIPE_UNLOCKED.handle(serverPlayerEntity, recipe6);
                ++integer4;
            }
        }
        this.sendUnlockRecipesPacket(UnlockRecipesS2CPacket.Action.b, serverPlayerEntity, list3);
        return integer4;
    }
    
    public int lockRecipes(final Collection<Recipe<?>> collection, final ServerPlayerEntity serverPlayerEntity) {
        final List<Identifier> list3 = Lists.newArrayList();
        int integer4 = 0;
        for (final Recipe<?> recipe6 : collection) {
            final Identifier identifier7 = recipe6.getId();
            if (this.recipes.contains(identifier7)) {
                this.remove(identifier7);
                list3.add(identifier7);
                ++integer4;
            }
        }
        this.sendUnlockRecipesPacket(UnlockRecipesS2CPacket.Action.c, serverPlayerEntity, list3);
        return integer4;
    }
    
    private void sendUnlockRecipesPacket(final UnlockRecipesS2CPacket.Action action, final ServerPlayerEntity serverPlayerEntity, final List<Identifier> list) {
        serverPlayerEntity.networkHandler.sendPacket(new UnlockRecipesS2CPacket(action, list, Collections.emptyList(), this.guiOpen, this.filteringCraftable, this.furnaceGuiOpen, this.furnaceFilteringCraftable));
    }
    
    public CompoundTag toTag() {
        final CompoundTag compoundTag1 = new CompoundTag();
        compoundTag1.putBoolean("isGuiOpen", this.guiOpen);
        compoundTag1.putBoolean("isFilteringCraftable", this.filteringCraftable);
        compoundTag1.putBoolean("isFurnaceGuiOpen", this.furnaceGuiOpen);
        compoundTag1.putBoolean("isFurnaceFilteringCraftable", this.furnaceFilteringCraftable);
        final ListTag listTag2 = new ListTag();
        for (final Identifier identifier4 : this.recipes) {
            ((AbstractList<StringTag>)listTag2).add(new StringTag(identifier4.toString()));
        }
        compoundTag1.put("recipes", listTag2);
        final ListTag listTag3 = new ListTag();
        for (final Identifier identifier5 : this.toBeDisplayed) {
            ((AbstractList<StringTag>)listTag3).add(new StringTag(identifier5.toString()));
        }
        compoundTag1.put("toBeDisplayed", listTag3);
        return compoundTag1;
    }
    
    public void fromTag(final CompoundTag compoundTag) {
        this.guiOpen = compoundTag.getBoolean("isGuiOpen");
        this.filteringCraftable = compoundTag.getBoolean("isFilteringCraftable");
        this.furnaceGuiOpen = compoundTag.getBoolean("isFurnaceGuiOpen");
        this.furnaceFilteringCraftable = compoundTag.getBoolean("isFurnaceFilteringCraftable");
        final ListTag listTag2 = compoundTag.getList("recipes", 8);
        for (int integer3 = 0; integer3 < listTag2.size(); ++integer3) {
            final Identifier identifier4 = new Identifier(listTag2.getString(integer3));
            final Optional<? extends Recipe<?>> optional5 = this.manager.get(identifier4);
            if (!optional5.isPresent()) {
                ServerRecipeBook.LOGGER.error("Tried to load unrecognized recipe: {} removed now.", identifier4);
            }
            else {
                this.add(optional5.get());
            }
        }
        final ListTag listTag3 = compoundTag.getList("toBeDisplayed", 8);
        for (int integer4 = 0; integer4 < listTag3.size(); ++integer4) {
            final Identifier identifier5 = new Identifier(listTag3.getString(integer4));
            final Optional<? extends Recipe<?>> optional6 = this.manager.get(identifier5);
            if (!optional6.isPresent()) {
                ServerRecipeBook.LOGGER.error("Tried to load unrecognized recipe: {} removed now.", identifier5);
            }
            else {
                this.display(optional6.get());
            }
        }
    }
    
    public void sendInitRecipesPacket(final ServerPlayerEntity serverPlayerEntity) {
        serverPlayerEntity.networkHandler.sendPacket(new UnlockRecipesS2CPacket(UnlockRecipesS2CPacket.Action.a, this.recipes, this.toBeDisplayed, this.guiOpen, this.filteringCraftable, this.furnaceGuiOpen, this.furnaceFilteringCraftable));
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
