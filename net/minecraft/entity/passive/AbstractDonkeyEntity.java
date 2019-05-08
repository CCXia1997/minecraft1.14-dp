package net.minecraft.entity.passive;

import java.util.AbstractList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.item.ItemProvider;
import net.minecraft.block.Blocks;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;

public abstract class AbstractDonkeyEntity extends HorseBaseEntity
{
    private static final TrackedData<Boolean> CHEST;
    
    protected AbstractDonkeyEntity(final EntityType<? extends AbstractDonkeyEntity> type, final World world) {
        super(type, world);
        this.bH = false;
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Boolean>startTracking(AbstractDonkeyEntity.CHEST, false);
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(this.getChildHealthBonus());
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.17499999701976776);
        this.getAttributeInstance(AbstractDonkeyEntity.JUMP_STRENGTH).setBaseValue(0.5);
    }
    
    public boolean hasChest() {
        return this.dataTracker.<Boolean>get(AbstractDonkeyEntity.CHEST);
    }
    
    public void setHasChest(final boolean boolean1) {
        this.dataTracker.<Boolean>set(AbstractDonkeyEntity.CHEST, boolean1);
    }
    
    @Override
    protected int getInventorySize() {
        if (this.hasChest()) {
            return 17;
        }
        return super.getInventorySize();
    }
    
    @Override
    public double getMountedHeightOffset() {
        return super.getMountedHeightOffset() - 0.25;
    }
    
    @Override
    protected SoundEvent getAngrySound() {
        super.getAngrySound();
        return SoundEvents.bW;
    }
    
    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.hasChest()) {
            if (!this.world.isClient) {
                this.dropItem(Blocks.bP);
            }
            this.setHasChest(false);
        }
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putBoolean("ChestedHorse", this.hasChest());
        if (this.hasChest()) {
            final ListTag listTag2 = new ListTag();
            for (int integer3 = 2; integer3 < this.items.getInvSize(); ++integer3) {
                final ItemStack itemStack4 = this.items.getInvStack(integer3);
                if (!itemStack4.isEmpty()) {
                    final CompoundTag compoundTag5 = new CompoundTag();
                    compoundTag5.putByte("Slot", (byte)integer3);
                    itemStack4.toTag(compoundTag5);
                    ((AbstractList<CompoundTag>)listTag2).add(compoundTag5);
                }
            }
            tag.put("Items", listTag2);
        }
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setHasChest(tag.getBoolean("ChestedHorse"));
        if (this.hasChest()) {
            final ListTag listTag2 = tag.getList("Items", 10);
            this.em();
            for (int integer3 = 0; integer3 < listTag2.size(); ++integer3) {
                final CompoundTag compoundTag4 = listTag2.getCompoundTag(integer3);
                final int integer4 = compoundTag4.getByte("Slot") & 0xFF;
                if (integer4 >= 2 && integer4 < this.items.getInvSize()) {
                    this.items.setInvStack(integer4, ItemStack.fromTag(compoundTag4));
                }
            }
        }
        this.updateSaddle();
    }
    
    @Override
    public boolean equip(final int slot, final ItemStack item) {
        if (slot == 499) {
            if (this.hasChest() && item.isEmpty()) {
                this.setHasChest(false);
                this.em();
                return true;
            }
            if (!this.hasChest() && item.getItem() == Blocks.bP.getItem()) {
                this.setHasChest(true);
                this.em();
                return true;
            }
        }
        return super.equip(slot, item);
    }
    
    @Override
    public boolean interactMob(final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack3 = player.getStackInHand(hand);
        if (itemStack3.getItem() instanceof SpawnEggItem) {
            return super.interactMob(player, hand);
        }
        if (!this.isChild()) {
            if (this.isTame() && player.isSneaking()) {
                this.openInventory(player);
                return true;
            }
            if (this.hasPassengers()) {
                return super.interactMob(player, hand);
            }
        }
        if (!itemStack3.isEmpty()) {
            boolean boolean4 = this.receiveFood(player, itemStack3);
            if (!boolean4) {
                if (!this.isTame() || itemStack3.getItem() == Items.or) {
                    if (itemStack3.interactWithEntity(player, this, hand)) {
                        return true;
                    }
                    this.playAngrySound();
                    return true;
                }
                else {
                    if (!this.hasChest() && itemStack3.getItem() == Blocks.bP.getItem()) {
                        this.setHasChest(true);
                        this.playAddChestSound();
                        boolean4 = true;
                        this.em();
                    }
                    if (!this.isChild() && !this.isSaddled() && itemStack3.getItem() == Items.kB) {
                        this.openInventory(player);
                        return true;
                    }
                }
            }
            if (boolean4) {
                if (!player.abilities.creativeMode) {
                    itemStack3.subtractAmount(1);
                }
                return true;
            }
        }
        if (this.isChild()) {
            return super.interactMob(player, hand);
        }
        this.putPlayerOnBack(player);
        return true;
    }
    
    protected void playAddChestSound() {
        this.playSound(SoundEvents.bX, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
    }
    
    public int dZ() {
        return 5;
    }
    
    static {
        CHEST = DataTracker.<Boolean>registerData(AbstractDonkeyEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }
}
