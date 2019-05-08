package net.minecraft.container;

import java.util.AbstractList;
import net.minecraft.util.DyeColor;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.Blocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BannerPatternItem;
import net.minecraft.item.DyeItem;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;

public class LoomContainer extends Container
{
    private final BlockContext context;
    private final Property selectedPattern;
    private Runnable inventoryChangeListener;
    private final Slot bannerSlot;
    private final Slot dyeSlot;
    private final Slot patternSlot;
    private final Slot outputSlot;
    private final Inventory inputInventory;
    private final Inventory outputInventory;
    
    public LoomContainer(final int syncId, final PlayerInventory playerInventory) {
        this(syncId, playerInventory, BlockContext.EMPTY);
    }
    
    public LoomContainer(final int syncId, final PlayerInventory playerInventory, final BlockContext blockContext) {
        super(ContainerType.LOOM, syncId);
        this.selectedPattern = Property.create();
        this.inventoryChangeListener = (() -> {});
        this.inputInventory = new BasicInventory(3) {
            @Override
            public void markDirty() {
                super.markDirty();
                LoomContainer.this.onContentChanged(this);
                LoomContainer.this.inventoryChangeListener.run();
            }
        };
        this.outputInventory = new BasicInventory(1) {
            @Override
            public void markDirty() {
                super.markDirty();
                LoomContainer.this.inventoryChangeListener.run();
            }
        };
        this.context = blockContext;
        this.bannerSlot = this.addSlot(new Slot(this.inputInventory, 0, 13, 26) {
            @Override
            public boolean canInsert(final ItemStack stack) {
                return stack.getItem() instanceof BannerItem;
            }
        });
        this.dyeSlot = this.addSlot(new Slot(this.inputInventory, 1, 33, 26) {
            @Override
            public boolean canInsert(final ItemStack stack) {
                return stack.getItem() instanceof DyeItem;
            }
        });
        this.patternSlot = this.addSlot(new Slot(this.inputInventory, 2, 23, 45) {
            @Override
            public boolean canInsert(final ItemStack stack) {
                return stack.getItem() instanceof BannerPatternItem;
            }
        });
        this.outputSlot = this.addSlot(new Slot(this.outputInventory, 0, 143, 58) {
            @Override
            public boolean canInsert(final ItemStack stack) {
                return false;
            }
            
            @Override
            public ItemStack onTakeItem(final PlayerEntity player, final ItemStack stack) {
                LoomContainer.this.bannerSlot.takeStack(1);
                LoomContainer.this.dyeSlot.takeStack(1);
                if (!LoomContainer.this.bannerSlot.hasStack() || !LoomContainer.this.dyeSlot.hasStack()) {
                    LoomContainer.this.selectedPattern.set(0);
                }
                blockContext.run((world, blockPos) -> world.playSound(null, blockPos, SoundEvents.mj, SoundCategory.e, 1.0f, 1.0f));
                return super.onTakeItem(player, stack);
            }
        });
        for (int integer4 = 0; integer4 < 3; ++integer4) {
            for (int integer5 = 0; integer5 < 9; ++integer5) {
                this.addSlot(new Slot(playerInventory, integer5 + integer4 * 9 + 9, 8 + integer5 * 18, 84 + integer4 * 18));
            }
        }
        for (int integer4 = 0; integer4 < 9; ++integer4) {
            this.addSlot(new Slot(playerInventory, integer4, 8 + integer4 * 18, 142));
        }
        this.addProperty(this.selectedPattern);
    }
    
    @Environment(EnvType.CLIENT)
    public int getSelectedPattern() {
        return this.selectedPattern.get();
    }
    
    @Override
    public boolean canUse(final PlayerEntity player) {
        return Container.canUse(this.context, player, Blocks.lJ);
    }
    
    @Override
    public boolean onButtonClick(final PlayerEntity player, final int id) {
        if (id > 0 && id <= BannerPattern.P) {
            this.selectedPattern.set(id);
            this.updateOutputSlot();
            return true;
        }
        return false;
    }
    
    @Override
    public void onContentChanged(final Inventory inventory) {
        final ItemStack itemStack2 = this.bannerSlot.getStack();
        final ItemStack itemStack3 = this.dyeSlot.getStack();
        final ItemStack itemStack4 = this.patternSlot.getStack();
        final ItemStack itemStack5 = this.outputSlot.getStack();
        if (!itemStack5.isEmpty() && (itemStack2.isEmpty() || itemStack3.isEmpty() || this.selectedPattern.get() <= 0 || (this.selectedPattern.get() >= BannerPattern.COUNT - 5 && itemStack4.isEmpty()))) {
            this.outputSlot.setStack(ItemStack.EMPTY);
            this.selectedPattern.set(0);
        }
        else if (!itemStack4.isEmpty() && itemStack4.getItem() instanceof BannerPatternItem) {
            final CompoundTag compoundTag6 = itemStack2.getOrCreateSubCompoundTag("BlockEntityTag");
            final boolean boolean7 = compoundTag6.containsKey("Patterns", 9) && !itemStack2.isEmpty() && compoundTag6.getList("Patterns", 10).size() >= 6;
            if (boolean7) {
                this.selectedPattern.set(0);
            }
            else {
                this.selectedPattern.set(((BannerPatternItem)itemStack4.getItem()).getPattern().ordinal());
            }
        }
        this.updateOutputSlot();
        this.sendContentUpdates();
    }
    
    @Environment(EnvType.CLIENT)
    public void setInventoryChangeListener(final Runnable inventoryChangeListener) {
        this.inventoryChangeListener = inventoryChangeListener;
    }
    
    @Override
    public ItemStack transferSlot(final PlayerEntity player, final int invSlot) {
        ItemStack itemStack3 = ItemStack.EMPTY;
        final Slot slot4 = this.slotList.get(invSlot);
        if (slot4 != null && slot4.hasStack()) {
            final ItemStack itemStack4 = slot4.getStack();
            itemStack3 = itemStack4.copy();
            if (invSlot == this.outputSlot.id) {
                if (!this.insertItem(itemStack4, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }
                slot4.onStackChanged(itemStack4, itemStack3);
            }
            else if (invSlot == this.dyeSlot.id || invSlot == this.bannerSlot.id || invSlot == this.patternSlot.id) {
                if (!this.insertItem(itemStack4, 4, 40, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (itemStack4.getItem() instanceof BannerItem) {
                if (!this.insertItem(itemStack4, this.bannerSlot.id, this.bannerSlot.id + 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (itemStack4.getItem() instanceof DyeItem) {
                if (!this.insertItem(itemStack4, this.dyeSlot.id, this.dyeSlot.id + 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (itemStack4.getItem() instanceof BannerPatternItem) {
                if (!this.insertItem(itemStack4, this.patternSlot.id, this.patternSlot.id + 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (invSlot >= 4 && invSlot < 31) {
                if (!this.insertItem(itemStack4, 31, 40, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (invSlot >= 31 && invSlot < 40 && !this.insertItem(itemStack4, 4, 31, false)) {
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
    
    @Override
    public void close(final PlayerEntity player) {
        super.close(player);
        this.context.run((world, blockPos) -> this.dropInventory(player, player.world, this.inputInventory));
    }
    
    private void updateOutputSlot() {
        if (this.selectedPattern.get() > 0) {
            final ItemStack itemStack1 = this.bannerSlot.getStack();
            final ItemStack itemStack2 = this.dyeSlot.getStack();
            ItemStack itemStack3 = ItemStack.EMPTY;
            if (!itemStack1.isEmpty() && !itemStack2.isEmpty()) {
                itemStack3 = itemStack1.copy();
                itemStack3.setAmount(1);
                final BannerPattern bannerPattern4 = BannerPattern.values()[this.selectedPattern.get()];
                final DyeColor dyeColor5 = ((DyeItem)itemStack2.getItem()).getColor();
                final CompoundTag compoundTag6 = itemStack3.getOrCreateSubCompoundTag("BlockEntityTag");
                ListTag listTag7;
                if (compoundTag6.containsKey("Patterns", 9)) {
                    listTag7 = compoundTag6.getList("Patterns", 10);
                }
                else {
                    listTag7 = new ListTag();
                    compoundTag6.put("Patterns", listTag7);
                }
                final CompoundTag compoundTag7 = new CompoundTag();
                compoundTag7.putString("Pattern", bannerPattern4.getId());
                compoundTag7.putInt("Color", dyeColor5.getId());
                ((AbstractList<CompoundTag>)listTag7).add(compoundTag7);
            }
            if (!ItemStack.areEqual(itemStack3, this.outputSlot.getStack())) {
                this.outputSlot.setStack(itemStack3);
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    public Slot getBannerSlot() {
        return this.bannerSlot;
    }
    
    @Environment(EnvType.CLIENT)
    public Slot getDyeSlot() {
        return this.dyeSlot;
    }
    
    @Environment(EnvType.CLIENT)
    public Slot getPatternSlot() {
        return this.patternSlot;
    }
    
    @Environment(EnvType.CLIENT)
    public Slot getOutputSlot() {
        return this.outputSlot;
    }
}
