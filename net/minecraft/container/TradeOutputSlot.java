package net.minecraft.container;

import net.minecraft.village.TradeOffer;
import net.minecraft.stat.Stats;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.village.Trader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.village.TraderInventory;

public class TradeOutputSlot extends Slot
{
    private final TraderInventory traderInventory;
    private final PlayerEntity player;
    private int amount;
    private final Trader trader;
    
    public TradeOutputSlot(final PlayerEntity player, final Trader trader, final TraderInventory traderInventory, final int index, final int x, final int y) {
        super(traderInventory, index, x, y);
        this.player = player;
        this.trader = trader;
        this.traderInventory = traderInventory;
    }
    
    @Override
    public boolean canInsert(final ItemStack stack) {
        return false;
    }
    
    @Override
    public ItemStack takeStack(final int amount) {
        if (this.hasStack()) {
            this.amount += Math.min(amount, this.getStack().getAmount());
        }
        return super.takeStack(amount);
    }
    
    @Override
    protected void onCrafted(final ItemStack stack, final int amount) {
        this.amount += amount;
        this.onCrafted(stack);
    }
    
    @Override
    protected void onCrafted(final ItemStack stack) {
        stack.onCrafted(this.player.world, this.player, this.amount);
        this.amount = 0;
    }
    
    @Override
    public ItemStack onTakeItem(final PlayerEntity player, final ItemStack stack) {
        this.onCrafted(stack);
        final TradeOffer tradeOffer3 = this.traderInventory.getTradeOffer();
        if (tradeOffer3 != null) {
            final ItemStack itemStack4 = this.traderInventory.getInvStack(0);
            final ItemStack itemStack5 = this.traderInventory.getInvStack(1);
            if (tradeOffer3.depleteBuyItems(itemStack4, itemStack5) || tradeOffer3.depleteBuyItems(itemStack5, itemStack4)) {
                this.trader.trade(tradeOffer3);
                player.incrementStat(Stats.R);
                this.traderInventory.setInvStack(0, itemStack4);
                this.traderInventory.setInvStack(1, itemStack5);
            }
            this.trader.setExperienceFromServer(this.trader.getExperience() + tradeOffer3.getTraderExperience());
        }
        return stack;
    }
}
