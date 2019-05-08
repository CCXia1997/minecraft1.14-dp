package net.minecraft.village;

import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;

public class SimpleTrader implements Trader
{
    private final TraderInventory traderInventory;
    private final PlayerEntity player;
    private TraderOfferList recipeList;
    private int experience;
    
    public SimpleTrader(final PlayerEntity playerEntity) {
        this.recipeList = new TraderOfferList();
        this.player = playerEntity;
        this.traderInventory = new TraderInventory(this);
    }
    
    @Nullable
    @Override
    public PlayerEntity getCurrentCustomer() {
        return this.player;
    }
    
    @Override
    public void setCurrentCustomer(@Nullable final PlayerEntity customer) {
    }
    
    @Override
    public TraderOfferList getOffers() {
        return this.recipeList;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void setOffersFromServer(@Nullable final TraderOfferList traderOfferList) {
        this.recipeList = traderOfferList;
    }
    
    @Override
    public void trade(final TradeOffer tradeOffer) {
        tradeOffer.use();
    }
    
    @Override
    public void onSellingItem(final ItemStack itemStack) {
    }
    
    @Override
    public World getTraderWorld() {
        return this.player.world;
    }
    
    @Override
    public int getExperience() {
        return this.experience;
    }
    
    @Override
    public void setExperienceFromServer(final int experience) {
        this.experience = experience;
    }
    
    @Override
    public boolean isLevelledTrader() {
        return true;
    }
}
