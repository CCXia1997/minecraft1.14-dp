package net.minecraft.entity.passive;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.entity.Entity;
import com.google.common.collect.Sets;
import net.minecraft.village.TradeOffers;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.village.TradeOffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.village.TraderOfferList;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.village.Trader;
import net.minecraft.entity.Npc;

public abstract class AbstractTraderEntity extends PassiveEntity implements Npc, Trader
{
    @Nullable
    private PlayerEntity customer;
    @Nullable
    protected TraderOfferList offers;
    private final BasicInventory inventory;
    private int headRollingTimeLeft;
    
    public AbstractTraderEntity(final EntityType<? extends AbstractTraderEntity> type, final World world) {
        super(type, world);
        this.inventory = new BasicInventory(8);
    }
    
    @Override
    public int getExperience() {
        return 0;
    }
    
    @Override
    protected float getActiveEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        if (this.isChild()) {
            return 0.81f;
        }
        return 1.62f;
    }
    
    @Override
    public void setCurrentCustomer(@Nullable final PlayerEntity customer) {
        this.customer = customer;
    }
    
    @Nullable
    @Override
    public PlayerEntity getCurrentCustomer() {
        return this.customer;
    }
    
    public boolean hasCustomer() {
        return this.customer != null;
    }
    
    @Override
    public TraderOfferList getOffers() {
        if (this.offers == null) {
            this.offers = new TraderOfferList();
            this.fillRecipes();
        }
        return this.offers;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void setOffersFromServer(@Nullable final TraderOfferList traderOfferList) {
    }
    
    @Override
    public void setExperienceFromServer(final int experience) {
    }
    
    @Override
    public void trade(final TradeOffer tradeOffer) {
        tradeOffer.use();
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
        this.playSound(this.getYesSound(), this.getSoundVolume(), this.getSoundPitch());
        this.afterUsing(tradeOffer);
        if (this.customer instanceof ServerPlayerEntity) {
            Criterions.VILLAGER_TRADE.handle((ServerPlayerEntity)this.customer, this, tradeOffer.getMutableSellItem());
        }
    }
    
    protected abstract void afterUsing(final TradeOffer arg1);
    
    @Override
    public boolean isLevelledTrader() {
        return true;
    }
    
    @Override
    public void onSellingItem(final ItemStack itemStack) {
        if (!this.world.isClient && this.ambientSoundChance > -this.getMinAmbientSoundDelay() + 20) {
            this.ambientSoundChance = -this.getMinAmbientSoundDelay();
            this.playSound(this.getTradingSound(!itemStack.isEmpty()), this.getSoundVolume(), this.getSoundPitch());
        }
    }
    
    protected SoundEvent getYesSound() {
        return SoundEvents.mA;
    }
    
    protected SoundEvent getTradingSound(final boolean sold) {
        return sold ? SoundEvents.mA : SoundEvents.my;
    }
    
    public void playCelebrateSound() {
        this.playSound(SoundEvents.mv, this.getSoundVolume(), this.getSoundPitch());
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        final TraderOfferList traderOfferList2 = this.getOffers();
        if (!traderOfferList2.isEmpty()) {
            tag.put("Offers", traderOfferList2.toTag());
        }
        final ListTag listTag3 = new ListTag();
        for (int integer4 = 0; integer4 < this.inventory.getInvSize(); ++integer4) {
            final ItemStack itemStack5 = this.inventory.getInvStack(integer4);
            if (!itemStack5.isEmpty()) {
                ((AbstractList<CompoundTag>)listTag3).add(itemStack5.toTag(new CompoundTag()));
            }
        }
        tag.put("Inventory", listTag3);
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.containsKey("Offers", 10)) {
            this.offers = new TraderOfferList(tag.getCompound("Offers"));
        }
        final ListTag listTag2 = tag.getList("Inventory", 10);
        for (int integer3 = 0; integer3 < listTag2.size(); ++integer3) {
            final ItemStack itemStack4 = ItemStack.fromTag(listTag2.getCompoundTag(integer3));
            if (!itemStack4.isEmpty()) {
                this.inventory.add(itemStack4);
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    protected void produceParticles(final ParticleParameters particleParameters) {
        for (int integer2 = 0; integer2 < 5; ++integer2) {
            final double double3 = this.random.nextGaussian() * 0.02;
            final double double4 = this.random.nextGaussian() * 0.02;
            final double double5 = this.random.nextGaussian() * 0.02;
            this.world.addParticle(particleParameters, this.x + this.random.nextFloat() * this.getWidth() * 2.0f - this.getWidth(), this.y + 1.0 + this.random.nextFloat() * this.getHeight(), this.z + this.random.nextFloat() * this.getWidth() * 2.0f - this.getWidth(), double3, double4, double5);
        }
    }
    
    @Override
    public boolean canBeLeashedBy(final PlayerEntity player) {
        return false;
    }
    
    public int getHeadRollingTimeLeft() {
        return this.headRollingTimeLeft;
    }
    
    public void setHeadRollingTimeLeft(final int integer) {
        this.headRollingTimeLeft = integer;
    }
    
    public BasicInventory getInventory() {
        return this.inventory;
    }
    
    @Override
    public boolean equip(final int slot, final ItemStack item) {
        if (super.equip(slot, item)) {
            return true;
        }
        final int integer3 = slot - 300;
        if (integer3 >= 0 && integer3 < this.inventory.getInvSize()) {
            this.inventory.setInvStack(integer3, item);
            return true;
        }
        return false;
    }
    
    @Override
    public World getTraderWorld() {
        return this.world;
    }
    
    protected abstract void fillRecipes();
    
    protected void fillRecipesFromPool(final TraderOfferList recipeList, final TradeOffers.Factory[] pool, final int count) {
        final Set<Integer> set4 = Sets.newHashSet();
        if (pool.length > count) {
            while (set4.size() < count) {
                set4.add(this.random.nextInt(pool.length));
            }
        }
        else {
            for (int integer5 = 0; integer5 < pool.length; ++integer5) {
                set4.add(integer5);
            }
        }
        for (final Integer integer6 : set4) {
            final TradeOffers.Factory factory7 = pool[integer6];
            final TradeOffer tradeOffer8 = factory7.create(this, this.random);
            if (tradeOffer8 != null) {
                recipeList.add(tradeOffer8);
            }
        }
    }
}
