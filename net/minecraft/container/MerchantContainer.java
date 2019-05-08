package net.minecraft.container;

import net.minecraft.village.TraderOfferList;
import net.minecraft.village.TradeOffer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.village.SimpleTrader;
import net.minecraft.entity.player.PlayerInventory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.village.TraderInventory;
import net.minecraft.village.Trader;

public class MerchantContainer extends Container
{
    private final Trader trader;
    private final TraderInventory traderInventory;
    @Environment(EnvType.CLIENT)
    private int levelProgress;
    @Environment(EnvType.CLIENT)
    private boolean levelled;
    
    public MerchantContainer(final int syncId, final PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleTrader(playerInventory.player));
    }
    
    public MerchantContainer(final int syncId, final PlayerInventory playerInventory, final Trader trader) {
        super(ContainerType.MERCHANT, syncId);
        this.trader = trader;
        this.traderInventory = new TraderInventory(trader);
        this.addSlot(new Slot(this.traderInventory, 0, 136, 37));
        this.addSlot(new Slot(this.traderInventory, 1, 162, 37));
        this.addSlot(new TradeOutputSlot(playerInventory.player, trader, this.traderInventory, 2, 220, 37));
        for (int integer4 = 0; integer4 < 3; ++integer4) {
            for (int integer5 = 0; integer5 < 9; ++integer5) {
                this.addSlot(new Slot(playerInventory, integer5 + integer4 * 9 + 9, 108 + integer5 * 18, 84 + integer4 * 18));
            }
        }
        for (int integer4 = 0; integer4 < 9; ++integer4) {
            this.addSlot(new Slot(playerInventory, integer4, 108 + integer4 * 18, 142));
        }
    }
    
    @Environment(EnvType.CLIENT)
    public void setCanLevel(final boolean canLevel) {
        this.levelled = canLevel;
    }
    
    @Override
    public void onContentChanged(final Inventory inventory) {
        this.traderInventory.updateRecipes();
        super.onContentChanged(inventory);
    }
    
    public void setRecipeIndex(final int integer) {
        this.traderInventory.setRecipeIndex(integer);
    }
    
    @Override
    public boolean canUse(final PlayerEntity player) {
        return this.trader.getCurrentCustomer() == player;
    }
    
    @Environment(EnvType.CLIENT)
    public int getExperience() {
        return this.trader.getExperience();
    }
    
    @Environment(EnvType.CLIENT)
    public int getTraderRewardedExperience() {
        return this.traderInventory.getTraderRewardedExperience();
    }
    
    @Environment(EnvType.CLIENT)
    public void setExperienceFromServer(final int integer) {
        this.trader.setExperienceFromServer(integer);
    }
    
    @Environment(EnvType.CLIENT)
    public int getLevelProgress() {
        return this.levelProgress;
    }
    
    @Environment(EnvType.CLIENT)
    public void setLevelProgress(final int integer) {
        this.levelProgress = integer;
    }
    
    @Override
    public boolean canInsertIntoSlot(final ItemStack stack, final Slot slot) {
        return false;
    }
    
    @Override
    public ItemStack transferSlot(final PlayerEntity player, final int invSlot) {
        ItemStack itemStack3 = ItemStack.EMPTY;
        final Slot slot4 = this.slotList.get(invSlot);
        if (slot4 != null && slot4.hasStack()) {
            final ItemStack itemStack4 = slot4.getStack();
            itemStack3 = itemStack4.copy();
            if (invSlot == 2) {
                if (!this.insertItem(itemStack4, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot4.onStackChanged(itemStack4, itemStack3);
            }
            else if (invSlot == 0 || invSlot == 1) {
                if (!this.insertItem(itemStack4, 3, 39, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (invSlot >= 3 && invSlot < 30) {
                if (!this.insertItem(itemStack4, 30, 39, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (invSlot >= 30 && invSlot < 39 && !this.insertItem(itemStack4, 3, 30, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack4.isEmpty()) {
                slot4.setStack(ItemStack.EMPTY);
            }
            else {
                slot4.markDirty();
            }
            if (itemStack4.getAmount() == itemStack3.getAmount()) {
                return ItemStack.EMPTY;
            }
            slot4.onTakeItem(player, itemStack4);
        }
        return itemStack3;
    }
    
    @Override
    public void close(final PlayerEntity player) {
        super.close(player);
        this.trader.setCurrentCustomer(null);
        if (this.trader.getTraderWorld().isClient) {
            return;
        }
        if (!player.isAlive() || (player instanceof ServerPlayerEntity && ((ServerPlayerEntity)player).o())) {
            ItemStack itemStack2 = this.traderInventory.removeInvStack(0);
            if (!itemStack2.isEmpty()) {
                player.dropItem(itemStack2, false);
            }
            itemStack2 = this.traderInventory.removeInvStack(1);
            if (!itemStack2.isEmpty()) {
                player.dropItem(itemStack2, false);
            }
        }
        else {
            player.inventory.offerOrDrop(player.world, this.traderInventory.removeInvStack(0));
            player.inventory.offerOrDrop(player.world, this.traderInventory.removeInvStack(1));
        }
    }
    
    public void switchTo(final int recipeIndex) {
        if (this.getRecipes().size() <= recipeIndex) {
            return;
        }
        final ItemStack itemStack2 = this.traderInventory.getInvStack(0);
        if (!itemStack2.isEmpty()) {
            if (!this.insertItem(itemStack2, 3, 39, true)) {
                return;
            }
            this.traderInventory.setInvStack(0, itemStack2);
        }
        final ItemStack itemStack3 = this.traderInventory.getInvStack(1);
        if (!itemStack3.isEmpty()) {
            if (!this.insertItem(itemStack3, 3, 39, true)) {
                return;
            }
            this.traderInventory.setInvStack(1, itemStack3);
        }
        if (this.traderInventory.getInvStack(0).isEmpty() && this.traderInventory.getInvStack(1).isEmpty()) {
            final ItemStack itemStack4 = this.getRecipes().get(recipeIndex).getAdjustedFirstBuyItem();
            this.autofill(0, itemStack4);
            final ItemStack itemStack5 = this.getRecipes().get(recipeIndex).getSecondBuyItem();
            this.autofill(1, itemStack5);
        }
    }
    
    private void autofill(final int slot, final ItemStack stack) {
        if (!stack.isEmpty()) {
            for (int integer3 = 3; integer3 < 39; ++integer3) {
                final ItemStack itemStack4 = this.slotList.get(integer3).getStack();
                if (!itemStack4.isEmpty() && this.equals(stack, itemStack4)) {
                    final ItemStack itemStack5 = this.traderInventory.getInvStack(slot);
                    final int integer4 = itemStack5.isEmpty() ? 0 : itemStack5.getAmount();
                    final int integer5 = Math.min(64 - integer4, itemStack4.getAmount());
                    final ItemStack itemStack6 = itemStack4.copy();
                    final int integer6 = integer4 + integer5;
                    itemStack4.subtractAmount(integer5);
                    itemStack6.setAmount(integer6);
                    this.traderInventory.setInvStack(slot, itemStack6);
                    if (integer6 >= 64) {
                        break;
                    }
                }
            }
        }
    }
    
    private boolean equals(final ItemStack itemStack, final ItemStack otherItemStack) {
        return itemStack.getItem() == otherItemStack.getItem() && ItemStack.areTagsEqual(itemStack, otherItemStack);
    }
    
    @Environment(EnvType.CLIENT)
    public void setOffers(final TraderOfferList traderOfferList) {
        this.trader.setOffersFromServer(traderOfferList);
    }
    
    public TraderOfferList getRecipes() {
        return this.trader.getOffers();
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isLevelled() {
        return this.levelled;
    }
}
