package net.minecraft.container;

import net.minecraft.item.Item;
import javax.annotation.Nullable;
import net.minecraft.entity.effect.StatusEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;

public class BeaconContainer extends Container
{
    private final Inventory paymentInv;
    private final SlotPayment paymentSlot;
    private final BlockContext context;
    private final PropertyDelegate propertyDelegate;
    
    public BeaconContainer(final int syncId, final Inventory inventory) {
        this(syncId, inventory, new ArrayPropertyDelegate(3), BlockContext.EMPTY);
    }
    
    public BeaconContainer(final int syncId, final Inventory inventory, final PropertyDelegate propertyDelegate, final BlockContext blockContext) {
        super(ContainerType.BEACON, syncId);
        this.paymentInv = new BasicInventory(1) {
            @Override
            public boolean isValidInvStack(final int slot, final ItemStack itemStack) {
                return itemStack.getItem() == Items.nF || itemStack.getItem() == Items.jj || itemStack.getItem() == Items.jl || itemStack.getItem() == Items.jk;
            }
            
            @Override
            public int getInvMaxStackAmount() {
                return 1;
            }
        };
        Container.checkContainerDataCount(propertyDelegate, 3);
        this.propertyDelegate = propertyDelegate;
        this.context = blockContext;
        this.addSlot(this.paymentSlot = new SlotPayment(this.paymentInv, 0, 136, 110));
        this.addProperties(propertyDelegate);
        final int integer5 = 36;
        final int integer6 = 137;
        for (int integer7 = 0; integer7 < 3; ++integer7) {
            for (int integer8 = 0; integer8 < 9; ++integer8) {
                this.addSlot(new Slot(inventory, integer8 + integer7 * 9 + 9, 36 + integer8 * 18, 137 + integer7 * 18));
            }
        }
        for (int integer7 = 0; integer7 < 9; ++integer7) {
            this.addSlot(new Slot(inventory, integer7, 36 + integer7 * 18, 195));
        }
    }
    
    @Override
    public void close(final PlayerEntity player) {
        super.close(player);
        if (player.world.isClient) {
            return;
        }
        final ItemStack itemStack2 = this.paymentSlot.takeStack(this.paymentSlot.getMaxStackAmount());
        if (!itemStack2.isEmpty()) {
            player.dropItem(itemStack2, false);
        }
    }
    
    @Override
    public boolean canUse(final PlayerEntity player) {
        return Container.canUse(this.context, player, Blocks.ek);
    }
    
    @Override
    public void setProperties(final int pos, final int propertyId) {
        super.setProperties(pos, propertyId);
        this.sendContentUpdates();
    }
    
    @Override
    public ItemStack transferSlot(final PlayerEntity player, final int invSlot) {
        ItemStack itemStack3 = ItemStack.EMPTY;
        final Slot slot4 = this.slotList.get(invSlot);
        if (slot4 != null && slot4.hasStack()) {
            final ItemStack itemStack4 = slot4.getStack();
            itemStack3 = itemStack4.copy();
            if (invSlot == 0) {
                if (!this.insertItem(itemStack4, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
                slot4.onStackChanged(itemStack4, itemStack3);
            }
            else if (!this.paymentSlot.hasStack() && this.paymentSlot.canInsert(itemStack4) && itemStack4.getAmount() == 1) {
                if (!this.insertItem(itemStack4, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (invSlot >= 1 && invSlot < 28) {
                if (!this.insertItem(itemStack4, 28, 37, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (invSlot >= 28 && invSlot < 37) {
                if (!this.insertItem(itemStack4, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.insertItem(itemStack4, 1, 37, false)) {
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
    
    @Environment(EnvType.CLIENT)
    public int getProperties() {
        return this.propertyDelegate.get(0);
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public StatusEffect getPrimaryEffect() {
        return StatusEffect.byRawId(this.propertyDelegate.get(1));
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public StatusEffect getSecondaryEffect() {
        return StatusEffect.byRawId(this.propertyDelegate.get(2));
    }
    
    public void setEffects(final int primaryEffectId, final int secondaryEffectId) {
        if (this.paymentSlot.hasStack()) {
            this.propertyDelegate.set(1, primaryEffectId);
            this.propertyDelegate.set(2, secondaryEffectId);
            this.paymentSlot.takeStack(1);
        }
    }
    
    @Environment(EnvType.CLIENT)
    public boolean hasPayment() {
        return !this.paymentInv.getInvStack(0).isEmpty();
    }
    
    class SlotPayment extends Slot
    {
        public SlotPayment(final Inventory inventory, final int integer3, final int integer4, final int integer5) {
            super(inventory, integer3, integer4, integer5);
        }
        
        @Override
        public boolean canInsert(final ItemStack stack) {
            final Item item2 = stack.getItem();
            return item2 == Items.nF || item2 == Items.jj || item2 == Items.jl || item2 == Items.jk;
        }
        
        @Override
        public int getMaxStackAmount() {
            return 1;
        }
    }
}
