package net.minecraft.village;

import net.minecraft.util.TagHelper;
import net.minecraft.nbt.Tag;
import net.minecraft.util.math.MathHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.item.ItemStack;

public class TradeOffer
{
    private final ItemStack firstBuyItem;
    private final ItemStack secondBuyItem;
    private final ItemStack sellItem;
    private int uses;
    private final int maxUses;
    private boolean rewardingPlayerExperience;
    private int specialPrice;
    private int demandBonus;
    private float priceMultiplier;
    private int traderExperience;
    
    public TradeOffer(final CompoundTag compoundTag) {
        this.rewardingPlayerExperience = true;
        this.traderExperience = 1;
        this.firstBuyItem = ItemStack.fromTag(compoundTag.getCompound("buy"));
        this.secondBuyItem = ItemStack.fromTag(compoundTag.getCompound("buyB"));
        this.sellItem = ItemStack.fromTag(compoundTag.getCompound("sell"));
        this.uses = compoundTag.getInt("uses");
        if (compoundTag.containsKey("maxUses", 99)) {
            this.maxUses = compoundTag.getInt("maxUses");
        }
        else {
            this.maxUses = 4;
        }
        if (compoundTag.containsKey("rewardExp", 1)) {
            this.rewardingPlayerExperience = compoundTag.getBoolean("rewardExp");
        }
        if (compoundTag.containsKey("xp", 3)) {
            this.traderExperience = compoundTag.getInt("xp");
        }
        if (compoundTag.containsKey("priceMultiplier", 5)) {
            this.priceMultiplier = compoundTag.getFloat("priceMultiplier");
        }
        this.specialPrice = compoundTag.getInt("specialPrice");
        this.demandBonus = compoundTag.getInt("demand");
    }
    
    public TradeOffer(final ItemStack buyItem, final ItemStack sellItem, final int maxUses, final int rewardedExp, final float priceMultiplier) {
        this(buyItem, ItemStack.EMPTY, sellItem, maxUses, rewardedExp, priceMultiplier);
    }
    
    public TradeOffer(final ItemStack firstBuyItem, final ItemStack secondBuyItem, final ItemStack sellItem, final int maxUses, final int rewardedExp, final float priceMultiplier) {
        this(firstBuyItem, secondBuyItem, sellItem, 0, maxUses, rewardedExp, priceMultiplier);
    }
    
    public TradeOffer(final ItemStack firstBuyItem, final ItemStack secondBuyItem, final ItemStack sellItem, final int uses, final int maxUses, final int rewardedExp, final float priceMultiplier) {
        this.rewardingPlayerExperience = true;
        this.traderExperience = 1;
        this.firstBuyItem = firstBuyItem;
        this.secondBuyItem = secondBuyItem;
        this.sellItem = sellItem;
        this.uses = uses;
        this.maxUses = maxUses;
        this.traderExperience = rewardedExp;
        this.priceMultiplier = priceMultiplier;
    }
    
    public ItemStack getOriginalFirstBuyItem() {
        return this.firstBuyItem;
    }
    
    public ItemStack getAdjustedFirstBuyItem() {
        final int integer1 = this.firstBuyItem.getAmount();
        final ItemStack itemStack2 = this.firstBuyItem.copy();
        final int integer2 = Math.max(0, MathHelper.floor(integer1 * this.demandBonus * this.priceMultiplier));
        itemStack2.setAmount(MathHelper.clamp(integer1 + integer2 + this.specialPrice, 1, this.firstBuyItem.getItem().getMaxAmount()));
        return itemStack2;
    }
    
    public ItemStack getSecondBuyItem() {
        return this.secondBuyItem;
    }
    
    public ItemStack getMutableSellItem() {
        return this.sellItem;
    }
    
    public void updatePriceOnDemand() {
        this.demandBonus = this.demandBonus + this.uses - (this.maxUses - this.uses);
    }
    
    public ItemStack getSellItem() {
        return this.sellItem.copy();
    }
    
    public int getUses() {
        return this.uses;
    }
    
    public void resetUses() {
        this.uses = 0;
    }
    
    public int getMaxUses() {
        return this.maxUses;
    }
    
    public void use() {
        ++this.uses;
    }
    
    public void increaseSpecialPrice(final int integer) {
        this.specialPrice += integer;
    }
    
    public void clearSpecialPrice() {
        this.specialPrice = 0;
    }
    
    public int getSpecialPrice() {
        return this.specialPrice;
    }
    
    public void setSpecialPrice(final int integer) {
        this.specialPrice = integer;
    }
    
    public float getPriceMultiplier() {
        return this.priceMultiplier;
    }
    
    public int getTraderExperience() {
        return this.traderExperience;
    }
    
    public boolean isDisabled() {
        return this.uses >= this.maxUses;
    }
    
    public void clearUses() {
        this.uses = this.maxUses;
    }
    
    public boolean shouldRewardPlayerExperience() {
        return this.rewardingPlayerExperience;
    }
    
    public CompoundTag toTag() {
        final CompoundTag compoundTag1 = new CompoundTag();
        compoundTag1.put("buy", this.firstBuyItem.toTag(new CompoundTag()));
        compoundTag1.put("sell", this.sellItem.toTag(new CompoundTag()));
        compoundTag1.put("buyB", this.secondBuyItem.toTag(new CompoundTag()));
        compoundTag1.putInt("uses", this.uses);
        compoundTag1.putInt("maxUses", this.maxUses);
        compoundTag1.putBoolean("rewardExp", this.rewardingPlayerExperience);
        compoundTag1.putInt("xp", this.traderExperience);
        compoundTag1.putFloat("priceMultiplier", this.priceMultiplier);
        compoundTag1.putInt("specialPrice", this.specialPrice);
        compoundTag1.putInt("demand", this.demandBonus);
        return compoundTag1;
    }
    
    public boolean matchesBuyItems(final ItemStack first, final ItemStack second) {
        return this.acceptsBuy(first, this.getAdjustedFirstBuyItem()) && first.getAmount() >= this.getAdjustedFirstBuyItem().getAmount() && this.acceptsBuy(second, this.secondBuyItem) && second.getAmount() >= this.secondBuyItem.getAmount();
    }
    
    private boolean acceptsBuy(final ItemStack given, final ItemStack sample) {
        if (sample.isEmpty() && given.isEmpty()) {
            return true;
        }
        final ItemStack itemStack3 = given.copy();
        if (itemStack3.getItem().canDamage()) {
            itemStack3.setDamage(itemStack3.getDamage());
        }
        return ItemStack.areEqualIgnoreTags(itemStack3, sample) && (!sample.hasTag() || (itemStack3.hasTag() && TagHelper.areTagsEqual(sample.getTag(), itemStack3.getTag(), false)));
    }
    
    public boolean depleteBuyItems(final ItemStack itemStack1, final ItemStack itemStack2) {
        if (!this.matchesBuyItems(itemStack1, itemStack2)) {
            return false;
        }
        itemStack1.subtractAmount(this.getAdjustedFirstBuyItem().getAmount());
        if (!this.getSecondBuyItem().isEmpty()) {
            itemStack2.subtractAmount(this.getSecondBuyItem().getAmount());
        }
        return true;
    }
}
