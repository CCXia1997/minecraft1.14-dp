package net.minecraft.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;

public class LecternContainer extends Container
{
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    
    public LecternContainer(final int syncId) {
        this(syncId, new BasicInventory(1), new ArrayPropertyDelegate(1));
    }
    
    public LecternContainer(final int syncId, final Inventory inventory, final PropertyDelegate propertyDelegate) {
        super(ContainerType.LECTERN, syncId);
        Container.checkContainerSize(inventory, 1);
        Container.checkContainerDataCount(propertyDelegate, 1);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.addSlot(new Slot(inventory, 0, 0, 0) {
            @Override
            public void markDirty() {
                super.markDirty();
                LecternContainer.this.onContentChanged(this.inventory);
            }
        });
        this.addProperties(propertyDelegate);
    }
    
    @Override
    public boolean onButtonClick(final PlayerEntity player, final int id) {
        if (id >= 100) {
            final int integer3 = id - 100;
            this.setProperties(0, integer3);
            return true;
        }
        switch (id) {
            case 2: {
                final int integer3 = this.propertyDelegate.get(0);
                this.setProperties(0, integer3 + 1);
                return true;
            }
            case 1: {
                final int integer3 = this.propertyDelegate.get(0);
                this.setProperties(0, integer3 - 1);
                return true;
            }
            case 3: {
                if (!player.canModifyWorld()) {
                    return false;
                }
                final ItemStack itemStack3 = this.inventory.removeInvStack(0);
                this.inventory.markDirty();
                if (!player.inventory.insertStack(itemStack3)) {
                    player.dropItem(itemStack3, false);
                }
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public void setProperties(final int pos, final int propertyId) {
        super.setProperties(pos, propertyId);
        this.sendContentUpdates();
    }
    
    @Override
    public boolean canUse(final PlayerEntity player) {
        return this.inventory.canPlayerUseInv(player);
    }
    
    @Environment(EnvType.CLIENT)
    public ItemStack getBookItem() {
        return this.inventory.getInvStack(0);
    }
    
    @Environment(EnvType.CLIENT)
    public int getPage() {
        return this.propertyDelegate.get(0);
    }
}
