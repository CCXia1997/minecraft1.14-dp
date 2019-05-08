package net.minecraft.block.entity;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.util.math.Vec3i;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Direction;
import net.minecraft.sound.SoundEvent;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Property;
import net.minecraft.block.BarrelBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.container.GenericContainer;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;
import java.util.List;
import java.util.Iterator;
import net.minecraft.inventory.Inventories;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;

public class BarrelBlockEntity extends LootableContainerBlockEntity
{
    private DefaultedList<ItemStack> inventory;
    private int viewerCount;
    
    private BarrelBlockEntity(final BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
        this.inventory = DefaultedList.<ItemStack>create(27, ItemStack.EMPTY);
    }
    
    public BarrelBlockEntity() {
        this(BlockEntityType.BARREL);
    }
    
    @Override
    public CompoundTag toTag(final CompoundTag compoundTag) {
        super.toTag(compoundTag);
        if (!this.serializeLootTable(compoundTag)) {
            Inventories.toTag(compoundTag, this.inventory);
        }
        return compoundTag;
    }
    
    @Override
    public void fromTag(final CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        this.inventory = DefaultedList.<ItemStack>create(this.getInvSize(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(compoundTag)) {
            Inventories.fromTag(compoundTag, this.inventory);
        }
    }
    
    @Override
    public int getInvSize() {
        return 27;
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
    public ItemStack getInvStack(final int slot) {
        return this.inventory.get(slot);
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
        this.inventory.set(slot, itemStack);
        if (itemStack.getAmount() > this.getInvMaxStackAmount()) {
            itemStack.setAmount(this.getInvMaxStackAmount());
        }
    }
    
    @Override
    public void clear() {
        this.inventory.clear();
    }
    
    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        return this.inventory;
    }
    
    @Override
    protected void setInvStackList(final DefaultedList<ItemStack> list) {
        this.inventory = list;
    }
    
    @Override
    protected TextComponent getContainerName() {
        return new TranslatableTextComponent("container.barrel", new Object[0]);
    }
    
    @Override
    protected Container createContainer(final int integer, final PlayerInventory playerInventory) {
        return GenericContainer.createGeneric9x3(integer, playerInventory, this);
    }
    
    @Override
    public void onInvOpen(final PlayerEntity playerEntity) {
        if (!playerEntity.isSpectator()) {
            if (this.viewerCount < 0) {
                this.viewerCount = 0;
            }
            ++this.viewerCount;
            final BlockState blockState2 = this.getCachedState();
            final boolean boolean3 = blockState2.<Boolean>get((Property<Boolean>)BarrelBlock.OPEN);
            if (!boolean3) {
                this.playSound(blockState2, SoundEvents.O);
                this.setOpen(blockState2, true);
            }
            this.scheduleUpdate();
        }
    }
    
    private void scheduleUpdate() {
        this.world.getBlockTickScheduler().schedule(this.getPos(), this.getCachedState().getBlock(), 5);
    }
    
    public void tick() {
        final int integer1 = this.pos.getX();
        final int integer2 = this.pos.getY();
        final int integer3 = this.pos.getZ();
        this.viewerCount = ChestBlockEntity.countViewers(this.world, this, integer1, integer2, integer3);
        if (this.viewerCount > 0) {
            this.scheduleUpdate();
        }
        else {
            final BlockState blockState4 = this.getCachedState();
            if (blockState4.getBlock() != Blocks.lK) {
                this.invalidate();
                return;
            }
            final boolean boolean5 = blockState4.<Boolean>get((Property<Boolean>)BarrelBlock.OPEN);
            if (boolean5) {
                this.playSound(blockState4, SoundEvents.N);
                this.setOpen(blockState4, false);
            }
        }
    }
    
    @Override
    public void onInvClose(final PlayerEntity playerEntity) {
        if (!playerEntity.isSpectator()) {
            --this.viewerCount;
        }
    }
    
    private void setOpen(final BlockState state, final boolean open) {
        this.world.setBlockState(this.getPos(), ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)BarrelBlock.OPEN, open), 3);
    }
    
    private void playSound(final BlockState blockState, final SoundEvent soundEvent) {
        final Vec3i vec3i3 = blockState.<Direction>get((Property<Direction>)BarrelBlock.FACING).getVector();
        final double double4 = this.pos.getX() + 0.5 + vec3i3.getX() / 2.0;
        final double double5 = this.pos.getY() + 0.5 + vec3i3.getY() / 2.0;
        final double double6 = this.pos.getZ() + 0.5 + vec3i3.getZ() / 2.0;
        this.world.playSound(null, double4, double5, double6, soundEvent, SoundCategory.e, 0.5f, this.world.random.nextFloat() * 0.1f + 0.9f);
    }
}
