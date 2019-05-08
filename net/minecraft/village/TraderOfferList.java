package net.minecraft.village;

import java.util.AbstractList;
import net.minecraft.nbt.Tag;
import net.minecraft.util.PacketByteBuf;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import java.util.ArrayList;

public class TraderOfferList extends ArrayList<TradeOffer>
{
    public TraderOfferList() {
    }
    
    public TraderOfferList(final CompoundTag compoundTag) {
        final ListTag listTag2 = compoundTag.getList("Recipes", 10);
        for (int integer3 = 0; integer3 < listTag2.size(); ++integer3) {
            this.add(new TradeOffer(listTag2.getCompoundTag(integer3)));
        }
    }
    
    @Nullable
    public TradeOffer getValidRecipe(final ItemStack firstBuyItem, final ItemStack secondBuyItem, final int index) {
        if (index <= 0 || index >= this.size()) {
            for (int integer4 = 0; integer4 < this.size(); ++integer4) {
                final TradeOffer tradeOffer5 = this.get(integer4);
                if (tradeOffer5.matchesBuyItems(firstBuyItem, secondBuyItem)) {
                    return tradeOffer5;
                }
            }
            return null;
        }
        final TradeOffer tradeOffer6 = this.get(index);
        if (tradeOffer6.matchesBuyItems(firstBuyItem, secondBuyItem)) {
            return tradeOffer6;
        }
        return null;
    }
    
    public void toPacket(final PacketByteBuf buffer) {
        buffer.writeByte((byte)(this.size() & 0xFF));
        for (int integer2 = 0; integer2 < this.size(); ++integer2) {
            final TradeOffer tradeOffer3 = this.get(integer2);
            buffer.writeItemStack(tradeOffer3.getAdjustedFirstBuyItem());
            buffer.writeItemStack(tradeOffer3.getMutableSellItem());
            final ItemStack itemStack4 = tradeOffer3.getSecondBuyItem();
            buffer.writeBoolean(!itemStack4.isEmpty());
            if (!itemStack4.isEmpty()) {
                buffer.writeItemStack(itemStack4);
            }
            buffer.writeBoolean(tradeOffer3.isDisabled());
            buffer.writeInt(tradeOffer3.getUses());
            buffer.writeInt(tradeOffer3.getMaxUses());
            buffer.writeInt(tradeOffer3.getTraderExperience());
            buffer.writeInt(tradeOffer3.getSpecialPrice());
            buffer.writeFloat(tradeOffer3.getPriceMultiplier());
        }
    }
    
    public static TraderOfferList fromPacket(final PacketByteBuf byteBuf) {
        final TraderOfferList traderOfferList2 = new TraderOfferList();
        for (int integer3 = byteBuf.readByte() & 0xFF, integer4 = 0; integer4 < integer3; ++integer4) {
            final ItemStack itemStack5 = byteBuf.readItemStack();
            final ItemStack itemStack6 = byteBuf.readItemStack();
            ItemStack itemStack7 = ItemStack.EMPTY;
            if (byteBuf.readBoolean()) {
                itemStack7 = byteBuf.readItemStack();
            }
            final boolean boolean8 = byteBuf.readBoolean();
            final int integer5 = byteBuf.readInt();
            final int integer6 = byteBuf.readInt();
            final int integer7 = byteBuf.readInt();
            final int integer8 = byteBuf.readInt();
            final float float13 = byteBuf.readFloat();
            final TradeOffer tradeOffer14 = new TradeOffer(itemStack5, itemStack7, itemStack6, integer5, integer6, integer7, float13);
            if (boolean8) {
                tradeOffer14.clearUses();
            }
            tradeOffer14.setSpecialPrice(integer8);
            traderOfferList2.add(tradeOffer14);
        }
        return traderOfferList2;
    }
    
    public CompoundTag toTag() {
        final CompoundTag compoundTag1 = new CompoundTag();
        final ListTag listTag2 = new ListTag();
        for (int integer3 = 0; integer3 < this.size(); ++integer3) {
            final TradeOffer tradeOffer4 = this.get(integer3);
            ((AbstractList<CompoundTag>)listTag2).add(tradeOffer4.toTag());
        }
        compoundTag1.put("Recipes", listTag2);
        return compoundTag1;
    }
}
