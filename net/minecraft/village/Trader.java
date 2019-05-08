package net.minecraft.village;

import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import java.util.OptionalInt;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.client.network.ClientDummyContainerProvider;
import net.minecraft.container.MerchantContainer;
import net.minecraft.text.TextComponent;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;

public interface Trader
{
    void setCurrentCustomer(@Nullable final PlayerEntity arg1);
    
    @Nullable
    PlayerEntity getCurrentCustomer();
    
    TraderOfferList getOffers();
    
    @Environment(EnvType.CLIENT)
    void setOffersFromServer(@Nullable final TraderOfferList arg1);
    
    void trade(final TradeOffer arg1);
    
    void onSellingItem(final ItemStack arg1);
    
    World getTraderWorld();
    
    int getExperience();
    
    void setExperienceFromServer(final int arg1);
    
    boolean isLevelledTrader();
    
    default void sendOffers(final PlayerEntity playerEntity, final TextComponent textComponent, final int integer) {
        final OptionalInt optionalInt4 = playerEntity.openContainer(new ClientDummyContainerProvider((integer, playerInventory, playerEntity) -> new MerchantContainer(integer, playerInventory, this), textComponent));
        if (optionalInt4.isPresent()) {
            final TraderOfferList traderOfferList5 = this.getOffers();
            if (!traderOfferList5.isEmpty()) {
                playerEntity.sendTradeOffers(optionalInt4.getAsInt(), traderOfferList5, integer, this.getExperience(), this.isLevelledTrader());
            }
        }
    }
}
