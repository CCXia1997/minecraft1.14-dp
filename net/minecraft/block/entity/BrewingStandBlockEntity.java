package net.minecraft.block.entity;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.inventory.Inventory;
import net.minecraft.container.BrewingStandContainer;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import javax.annotation.Nullable;
import net.minecraft.util.math.Direction;
import net.minecraft.entity.player.PlayerEntity;
import java.util.List;
import net.minecraft.inventory.Inventories;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ItemScatterer;
import net.minecraft.item.ItemProvider;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.state.property.Property;
import net.minecraft.block.BlockState;
import net.minecraft.block.BrewingStandBlock;
import java.util.Arrays;
import net.minecraft.item.Items;
import java.util.Iterator;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.container.PropertyDelegate;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.inventory.SidedInventory;

public class BrewingStandBlockEntity extends LockableContainerBlockEntity implements SidedInventory, Tickable
{
    private static final int[] TOP_SLOTS;
    private static final int[] BOTTOM_SLOTS;
    private static final int[] SIDE_SLOTS;
    private DefaultedList<ItemStack> inventory;
    private int brewTime;
    private boolean[] slotsEmptyLastTick;
    private Item itemBrewing;
    private int fuel;
    protected final PropertyDelegate propertyDelegate;
    
    public BrewingStandBlockEntity() {
        super(BlockEntityType.BREWING_STAND);
        this.inventory = DefaultedList.<ItemStack>create(5, ItemStack.EMPTY);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(final int key) {
                switch (key) {
                    case 0: {
                        return BrewingStandBlockEntity.this.brewTime;
                    }
                    case 1: {
                        return BrewingStandBlockEntity.this.fuel;
                    }
                    default: {
                        return 0;
                    }
                }
            }
            
            @Override
            public void set(final int key, final int value) {
                switch (key) {
                    case 0: {
                        BrewingStandBlockEntity.this.brewTime = value;
                        break;
                    }
                    case 1: {
                        BrewingStandBlockEntity.this.fuel = value;
                        break;
                    }
                }
            }
            
            @Override
            public int size() {
                return 2;
            }
        };
    }
    
    @Override
    protected TextComponent getContainerName() {
        return new TranslatableTextComponent("container.brewing", new Object[0]);
    }
    
    @Override
    public int getInvSize() {
        return this.inventory.size();
    }
    
    @Override
    public boolean isInvEmpty() {
        for (final ItemStack itemStack2 : this.inventory) {
            if (!itemStack2.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void tick() {
        final ItemStack itemStack1 = this.inventory.get(4);
        if (this.fuel <= 0 && itemStack1.getItem() == Items.mp) {
            this.fuel = 20;
            itemStack1.subtractAmount(1);
            this.markDirty();
        }
        final boolean boolean2 = this.canCraft();
        final boolean boolean3 = this.brewTime > 0;
        final ItemStack itemStack2 = this.inventory.get(3);
        if (boolean3) {
            --this.brewTime;
            final boolean boolean4 = this.brewTime == 0;
            if (boolean4 && boolean2) {
                this.craft();
                this.markDirty();
            }
            else if (!boolean2) {
                this.brewTime = 0;
                this.markDirty();
            }
            else if (this.itemBrewing != itemStack2.getItem()) {
                this.brewTime = 0;
                this.markDirty();
            }
        }
        else if (boolean2 && this.fuel > 0) {
            --this.fuel;
            this.brewTime = 400;
            this.itemBrewing = itemStack2.getItem();
            this.markDirty();
        }
        if (!this.world.isClient) {
            final boolean[] arr5 = this.getSlotsEmpty();
            if (!Arrays.equals(arr5, this.slotsEmptyLastTick)) {
                this.slotsEmptyLastTick = arr5;
                BlockState blockState6 = this.world.getBlockState(this.getPos());
                if (!(blockState6.getBlock() instanceof BrewingStandBlock)) {
                    return;
                }
                for (int integer7 = 0; integer7 < BrewingStandBlock.BOTTLE_PROPERTIES.length; ++integer7) {
                    blockState6 = ((AbstractPropertyContainer<O, BlockState>)blockState6).<Comparable, Boolean>with((Property<Comparable>)BrewingStandBlock.BOTTLE_PROPERTIES[integer7], arr5[integer7]);
                }
                this.world.setBlockState(this.pos, blockState6, 2);
            }
        }
    }
    
    public boolean[] getSlotsEmpty() {
        final boolean[] arr1 = new boolean[3];
        for (int integer2 = 0; integer2 < 3; ++integer2) {
            if (!this.inventory.get(integer2).isEmpty()) {
                arr1[integer2] = true;
            }
        }
        return arr1;
    }
    
    private boolean canCraft() {
        final ItemStack itemStack1 = this.inventory.get(3);
        if (itemStack1.isEmpty()) {
            return false;
        }
        if (!BrewingRecipeRegistry.isValidIngredient(itemStack1)) {
            return false;
        }
        for (int integer2 = 0; integer2 < 3; ++integer2) {
            final ItemStack itemStack2 = this.inventory.get(integer2);
            if (!itemStack2.isEmpty()) {
                if (BrewingRecipeRegistry.hasRecipe(itemStack2, itemStack1)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private void craft() {
        ItemStack itemStack1 = this.inventory.get(3);
        for (int integer2 = 0; integer2 < 3; ++integer2) {
            this.inventory.set(integer2, BrewingRecipeRegistry.craft(itemStack1, this.inventory.get(integer2)));
        }
        itemStack1.subtractAmount(1);
        final BlockPos blockPos2 = this.getPos();
        if (itemStack1.getItem().hasRecipeRemainder()) {
            final ItemStack itemStack2 = new ItemStack(itemStack1.getItem().getRecipeRemainder());
            if (itemStack1.isEmpty()) {
                itemStack1 = itemStack2;
            }
            else if (!this.world.isClient) {
                ItemScatterer.spawn(this.world, blockPos2.getX(), blockPos2.getY(), blockPos2.getZ(), itemStack2);
            }
        }
        this.inventory.set(3, itemStack1);
        this.world.playLevelEvent(1035, blockPos2, 0);
    }
    
    @Override
    public void fromTag(final CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        Inventories.fromTag(compoundTag, this.inventory = DefaultedList.<ItemStack>create(this.getInvSize(), ItemStack.EMPTY));
        this.brewTime = compoundTag.getShort("BrewTime");
        this.fuel = compoundTag.getByte("Fuel");
    }
    
    @Override
    public CompoundTag toTag(final CompoundTag compoundTag) {
        super.toTag(compoundTag);
        compoundTag.putShort("BrewTime", (short)this.brewTime);
        Inventories.toTag(compoundTag, this.inventory);
        compoundTag.putByte("Fuel", (byte)this.fuel);
        return compoundTag;
    }
    
    @Override
    public ItemStack getInvStack(final int slot) {
        if (slot >= 0 && slot < this.inventory.size()) {
            return this.inventory.get(slot);
        }
        return ItemStack.EMPTY;
    }
    
    @Override
    public ItemStack takeInvStack(final int slot, final int integer2) {
        return Inventories.splitStack(this.inventory, slot, integer2);
    }
    
    @Override
    public ItemStack removeInvStack(final int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }
    
    @Override
    public void setInvStack(final int slot, final ItemStack itemStack) {
        if (slot >= 0 && slot < this.inventory.size()) {
            this.inventory.set(slot, itemStack);
        }
    }
    
    @Override
    public boolean canPlayerUseInv(final PlayerEntity playerEntity) {
        return this.world.getBlockEntity(this.pos) == this && playerEntity.squaredDistanceTo(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64.0;
    }
    
    @Override
    public boolean isValidInvStack(final int slot, final ItemStack itemStack) {
        if (slot == 3) {
            return BrewingRecipeRegistry.isValidIngredient(itemStack);
        }
        final Item item3 = itemStack.getItem();
        if (slot == 4) {
            return item3 == Items.mp;
        }
        return (item3 == Items.ml || item3 == Items.oS || item3 == Items.oV || item3 == Items.mm) && this.getInvStack(slot).isEmpty();
    }
    
    @Override
    public int[] getInvAvailableSlots(final Direction side) {
        if (side == Direction.UP) {
            return BrewingStandBlockEntity.TOP_SLOTS;
        }
        if (side == Direction.DOWN) {
            return BrewingStandBlockEntity.BOTTOM_SLOTS;
        }
        return BrewingStandBlockEntity.SIDE_SLOTS;
    }
    
    @Override
    public boolean canInsertInvStack(final int slot, final ItemStack stack, @Nullable final Direction direction) {
        return this.isValidInvStack(slot, stack);
    }
    
    @Override
    public boolean canExtractInvStack(final int slot, final ItemStack stack, final Direction direction) {
        return slot != 3 || stack.getItem() == Items.mm;
    }
    
    @Override
    public void clear() {
        this.inventory.clear();
    }
    
    @Override
    protected Container createContainer(final int integer, final PlayerInventory playerInventory) {
        return new BrewingStandContainer(integer, playerInventory, this, this.propertyDelegate);
    }
    
    static {
        TOP_SLOTS = new int[] { 3 };
        BOTTOM_SLOTS = new int[] { 0, 1, 2, 3 };
        SIDE_SLOTS = new int[] { 0, 1, 2, 4 };
    }
}
