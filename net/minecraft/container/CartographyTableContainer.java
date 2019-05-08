package net.minecraft.container;

import net.minecraft.item.map.MapState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;

public class CartographyTableContainer extends Container
{
    private final BlockContext context;
    private boolean currentlyTakingItem;
    public final Inventory inventory;
    private final CraftingResultInventory resultSlot;
    
    public CartographyTableContainer(final int syncId, final PlayerInventory inventory) {
        this(syncId, inventory, BlockContext.EMPTY);
    }
    
    public CartographyTableContainer(final int syncId, final PlayerInventory inventory, final BlockContext context) {
        super(ContainerType.CARTOGRAPHY, syncId);
        this.inventory = new BasicInventory(2) {
            @Override
            public void markDirty() {
                CartographyTableContainer.this.onContentChanged(this);
                super.markDirty();
            }
        };
        this.resultSlot = new CraftingResultInventory() {
            @Override
            public void markDirty() {
                CartographyTableContainer.this.onContentChanged(this);
                super.markDirty();
            }
        };
        this.context = context;
        this.addSlot(new Slot(this.inventory, 0, 15, 15) {
            @Override
            public boolean canInsert(final ItemStack stack) {
                return stack.getItem() == Items.lV;
            }
        });
        this.addSlot(new Slot(this.inventory, 1, 15, 52) {
            @Override
            public boolean canInsert(final ItemStack stack) {
                final Item item2 = stack.getItem();
                return item2 == Items.kR || item2 == Items.nM || item2 == Items.GLASS_PANE;
            }
        });
        this.addSlot(new Slot(this.resultSlot, 2, 145, 39) {
            @Override
            public boolean canInsert(final ItemStack stack) {
                return false;
            }
            
            @Override
            public ItemStack takeStack(final int amount) {
                final ItemStack itemStack2 = super.takeStack(amount);
                ItemStack itemStack4;
                final ItemStack itemStack5;
                final ItemStack itemStack3 = context.<ItemStack>run((world, blockPos) -> {
                    if (!CartographyTableContainer.this.currentlyTakingItem && CartographyTableContainer.this.inventory.getInvStack(1).getItem() == Items.GLASS_PANE) {
                        itemStack4 = FilledMapItem.createCopy(world, CartographyTableContainer.this.inventory.getInvStack(0));
                        if (itemStack4 != null) {
                            itemStack4.setAmount(1);
                            return itemStack4;
                        }
                    }
                    return itemStack5;
                }).orElse(itemStack2);
                CartographyTableContainer.this.inventory.takeInvStack(0, 1);
                CartographyTableContainer.this.inventory.takeInvStack(1, 1);
                return itemStack3;
            }
            
            @Override
            protected void onCrafted(final ItemStack stack, final int amount) {
                this.takeStack(amount);
                super.onCrafted(stack, amount);
            }
            
            @Override
            public ItemStack onTakeItem(final PlayerEntity player, final ItemStack stack) {
                stack.getItem().onCrafted(stack, player.world, player);
                context.run((world, blockPos) -> world.playSound(null, blockPos, SoundEvents.mk, SoundCategory.e, 1.0f, 1.0f));
                return super.onTakeItem(player, stack);
            }
        });
        for (int integer4 = 0; integer4 < 3; ++integer4) {
            for (int integer5 = 0; integer5 < 9; ++integer5) {
                this.addSlot(new Slot(inventory, integer5 + integer4 * 9 + 9, 8 + integer5 * 18, 84 + integer4 * 18));
            }
        }
        for (int integer4 = 0; integer4 < 9; ++integer4) {
            this.addSlot(new Slot(inventory, integer4, 8 + integer4 * 18, 142));
        }
    }
    
    @Override
    public boolean canUse(final PlayerEntity player) {
        return Container.canUse(this.context, player, Blocks.lN);
    }
    
    @Override
    public void onContentChanged(final Inventory inventory) {
        final ItemStack itemStack2 = this.inventory.getInvStack(0);
        final ItemStack itemStack3 = this.inventory.getInvStack(1);
        final ItemStack itemStack4 = this.resultSlot.getInvStack(2);
        if (!itemStack4.isEmpty() && (itemStack2.isEmpty() || itemStack3.isEmpty())) {
            this.resultSlot.removeInvStack(2);
        }
        else if (!itemStack2.isEmpty() && !itemStack3.isEmpty()) {
            this.updateResult(itemStack2, itemStack3, itemStack4);
        }
    }
    
    private void updateResult(final ItemStack map, final ItemStack item, final ItemStack oldResult) {
        final Item item2;
        final MapState mapState7;
        ItemStack itemStack8;
        this.context.run((world, blockPos) -> {
            item2 = item.getItem();
            mapState7 = FilledMapItem.getMapState(map, world);
            if (mapState7 != null) {
                if (item2 == Items.kR && !mapState7.locked && mapState7.scale < 4) {
                    itemStack8 = map.copy();
                    itemStack8.setAmount(1);
                    itemStack8.getOrCreateTag().putInt("map_scale_direction", 1);
                    this.sendContentUpdates();
                }
                else if (item2 == Items.GLASS_PANE && !mapState7.locked) {
                    itemStack8 = map.copy();
                    itemStack8.setAmount(1);
                    this.sendContentUpdates();
                }
                else if (item2 == Items.nM) {
                    itemStack8 = map.copy();
                    itemStack8.setAmount(2);
                    this.sendContentUpdates();
                }
                else {
                    this.resultSlot.removeInvStack(2);
                    this.sendContentUpdates();
                    return;
                }
                if (!ItemStack.areEqual(itemStack8, oldResult)) {
                    this.resultSlot.setInvStack(2, itemStack8);
                    this.sendContentUpdates();
                }
            }
        });
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
            ItemStack itemStack5;
            final ItemStack itemStack4 = itemStack5 = slot4.getStack();
            final Item item7 = itemStack5.getItem();
            itemStack3 = itemStack5.copy();
            if (invSlot == 2) {
                if (this.inventory.getInvStack(1).getItem() == Items.GLASS_PANE) {
                    final ItemStack itemStack6;
                    final ItemStack itemStack7;
                    itemStack5 = this.context.<ItemStack>run((world, blockPos) -> {
                        itemStack6 = FilledMapItem.createCopy(world, this.inventory.getInvStack(0));
                        if (itemStack6 != null) {
                            itemStack6.setAmount(1);
                            return itemStack6;
                        }
                        else {
                            return itemStack7;
                        }
                    }).orElse(itemStack5);
                }
                item7.onCrafted(itemStack5, player.world, player);
                if (!this.insertItem(itemStack5, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot4.onStackChanged(itemStack5, itemStack3);
            }
            else if (invSlot == 1 || invSlot == 0) {
                if (!this.insertItem(itemStack5, 3, 39, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (item7 == Items.lV) {
                if (!this.insertItem(itemStack5, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (item7 == Items.kR || item7 == Items.nM || item7 == Items.GLASS_PANE) {
                if (!this.insertItem(itemStack5, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (invSlot >= 3 && invSlot < 30) {
                if (!this.insertItem(itemStack5, 30, 39, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (invSlot >= 30 && invSlot < 39 && !this.insertItem(itemStack5, 3, 30, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack5.isEmpty()) {
                slot4.setStack(ItemStack.EMPTY);
            }
            slot4.markDirty();
            if (itemStack5.getAmount() == itemStack3.getAmount()) {
                return ItemStack.EMPTY;
            }
            this.currentlyTakingItem = true;
            slot4.onTakeItem(player, itemStack5);
            this.currentlyTakingItem = false;
            this.sendContentUpdates();
        }
        return itemStack3;
    }
    
    @Override
    public void close(final PlayerEntity player) {
        super.close(player);
        this.resultSlot.removeInvStack(2);
        this.context.run((world, blockPos) -> this.dropInventory(player, player.world, this.inventory));
    }
}
