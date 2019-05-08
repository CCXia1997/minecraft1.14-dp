package net.minecraft.village;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import java.util.List;
import net.minecraft.inventory.Inventories;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.inventory.Inventory;

public class TraderInventory implements Inventory
{
    private final Trader trader;
    private final DefaultedList<ItemStack> inventory;
    @Nullable
    private TradeOffer traderRecipe;
    private int recipeIndex;
    private int traderRewardedExperience;
    
    public TraderInventory(final Trader trader) {
        this.inventory = DefaultedList.<ItemStack>create(3, ItemStack.EMPTY);
        this.trader = trader;
    }
    
    @Override
    public int getInvSize() {
        return this.inventory.size();
    }
    
    @Override
    public boolean isInvEmpty() {
        for (final ItemStack itemStack2 : this.inventory) {
            if (!itemStack2.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public ItemStack getInvStack(final int slot) {
        return this.inventory.get(slot);
    }
    
    @Override
    public ItemStack takeInvStack(final int slot, final int integer2) {
        final ItemStack itemStack3 = this.inventory.get(slot);
        if (slot == 2 && !itemStack3.isEmpty()) {
            return Inventories.splitStack(this.inventory, slot, itemStack3.getAmount());
        }
        final ItemStack itemStack4 = Inventories.splitStack(this.inventory, slot, integer2);
        if (!itemStack4.isEmpty() && this.needRecipeUpdate(slot)) {
            this.updateRecipes();
        }
        return itemStack4;
    }
    
    private boolean needRecipeUpdate(final int slot) {
        return slot == 0 || slot == 1;
    }
    
    @Override
    public ItemStack removeInvStack(final int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }
    
    @Override
    public void setInvStack(final int slot, final ItemStack itemStack) {
        this.inventory.set(slot, itemStack);
        if (!itemStack.isEmpty() && itemStack.getAmount() > this.getInvMaxStackAmount()) {
            itemStack.setAmount(this.getInvMaxStackAmount());
        }
        if (this.needRecipeUpdate(slot)) {
            this.updateRecipes();
        }
    }
    
    @Override
    public boolean canPlayerUseInv(final PlayerEntity playerEntity) {
        return this.trader.getCurrentCustomer() == playerEntity;
    }
    
    @Override
    public void markDirty() {
        this.updateRecipes();
    }
    
    public void updateRecipes() {
        this.traderRecipe = null;
        ItemStack itemStack1;
        ItemStack itemStack2;
        if (this.inventory.get(0).isEmpty()) {
            itemStack1 = this.inventory.get(1);
            itemStack2 = ItemStack.EMPTY;
        }
        else {
            itemStack1 = this.inventory.get(0);
            itemStack2 = this.inventory.get(1);
        }
        if (itemStack1.isEmpty()) {
            this.setInvStack(2, ItemStack.EMPTY);
            this.traderRewardedExperience = 0;
            return;
        }
        final TraderOfferList traderOfferList3 = this.trader.getOffers();
        if (!traderOfferList3.isEmpty()) {
            TradeOffer tradeOffer4 = traderOfferList3.getValidRecipe(itemStack1, itemStack2, this.recipeIndex);
            if (tradeOffer4 == null || tradeOffer4.isDisabled()) {
                this.traderRecipe = tradeOffer4;
                tradeOffer4 = traderOfferList3.getValidRecipe(itemStack2, itemStack1, this.recipeIndex);
            }
            if (tradeOffer4 != null && !tradeOffer4.isDisabled()) {
                this.traderRecipe = tradeOffer4;
                this.setInvStack(2, tradeOffer4.getSellItem());
                this.traderRewardedExperience = tradeOffer4.getTraderExperience();
            }
            else {
                this.setInvStack(2, ItemStack.EMPTY);
                this.traderRewardedExperience = 0;
            }
        }
        this.trader.onSellingItem(this.getInvStack(2));
    }
    
    @Nullable
    public TradeOffer getTradeOffer() {
        return this.traderRecipe;
    }
    
    public void setRecipeIndex(final int index) {
        this.recipeIndex = index;
        this.updateRecipes();
    }
    
    @Override
    public void clear() {
        this.inventory.clear();
    }
    
    @Environment(EnvType.CLIENT)
    public int getTraderRewardedExperience() {
        return this.traderRewardedExperience;
    }
}
