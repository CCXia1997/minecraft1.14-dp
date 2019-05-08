package net.minecraft.entity.vehicle;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.context.LootContextTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.container.Container;
import net.minecraft.util.Hand;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.dimension.DimensionType;
import java.util.List;
import net.minecraft.inventory.Inventories;
import net.minecraft.entity.player.PlayerEntity;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.util.ItemScatterer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.inventory.Inventory;

public abstract class StorageMinecartEntity extends AbstractMinecartEntity implements Inventory, NameableContainerProvider
{
    private DefaultedList<ItemStack> inventory;
    private boolean c;
    @Nullable
    private Identifier lootTableId;
    private long lootSeed;
    
    protected StorageMinecartEntity(final EntityType<?> type, final World world) {
        super(type, world);
        this.inventory = DefaultedList.<ItemStack>create(36, ItemStack.EMPTY);
        this.c = true;
    }
    
    protected StorageMinecartEntity(final EntityType<?> entityType, final double double2, final double double4, final double double6, final World world8) {
        super(entityType, world8, double2, double4, double6);
        this.inventory = DefaultedList.<ItemStack>create(36, ItemStack.EMPTY);
        this.c = true;
    }
    
    @Override
    public void dropItems(final DamageSource damageSource) {
        super.dropItems(damageSource);
        if (this.world.getGameRules().getBoolean("doEntityDrops")) {
            ItemScatterer.spawn(this.world, this, this);
        }
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
        this.d((PlayerEntity)null);
        return this.inventory.get(slot);
    }
    
    @Override
    public ItemStack takeInvStack(final int slot, final int integer2) {
        this.d((PlayerEntity)null);
        return Inventories.splitStack(this.inventory, slot, integer2);
    }
    
    @Override
    public ItemStack removeInvStack(final int slot) {
        this.d((PlayerEntity)null);
        final ItemStack itemStack2 = this.inventory.get(slot);
        if (itemStack2.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.inventory.set(slot, ItemStack.EMPTY);
        return itemStack2;
    }
    
    @Override
    public void setInvStack(final int slot, final ItemStack itemStack) {
        this.d((PlayerEntity)null);
        this.inventory.set(slot, itemStack);
        if (!itemStack.isEmpty() && itemStack.getAmount() > this.getInvMaxStackAmount()) {
            itemStack.setAmount(this.getInvMaxStackAmount());
        }
    }
    
    @Override
    public boolean equip(final int slot, final ItemStack item) {
        if (slot >= 0 && slot < this.getInvSize()) {
            this.setInvStack(slot, item);
            return true;
        }
        return false;
    }
    
    @Override
    public void markDirty() {
    }
    
    @Override
    public boolean canPlayerUseInv(final PlayerEntity playerEntity) {
        return !this.removed && playerEntity.squaredDistanceTo(this) <= 64.0;
    }
    
    @Nullable
    @Override
    public Entity changeDimension(final DimensionType newDimension) {
        this.c = false;
        return super.changeDimension(newDimension);
    }
    
    @Override
    public void remove() {
        if (!this.world.isClient && this.c) {
            ItemScatterer.spawn(this.world, this, this);
        }
        super.remove();
    }
    
    @Override
    protected void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        if (this.lootTableId != null) {
            tag.putString("LootTable", this.lootTableId.toString());
            if (this.lootSeed != 0L) {
                tag.putLong("LootTableSeed", this.lootSeed);
            }
        }
        else {
            Inventories.toTag(tag, this.inventory);
        }
    }
    
    @Override
    protected void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.inventory = DefaultedList.<ItemStack>create(this.getInvSize(), ItemStack.EMPTY);
        if (tag.containsKey("LootTable", 8)) {
            this.lootTableId = new Identifier(tag.getString("LootTable"));
            this.lootSeed = tag.getLong("LootTableSeed");
        }
        else {
            Inventories.fromTag(tag, this.inventory);
        }
    }
    
    @Override
    public boolean interact(final PlayerEntity player, final Hand hand) {
        player.openContainer(this);
        return true;
    }
    
    @Override
    protected void k() {
        float float1 = 0.98f;
        if (this.lootTableId == null) {
            final int integer2 = 15 - Container.calculateComparatorOutput(this);
            float1 += integer2 * 0.001f;
        }
        this.setVelocity(this.getVelocity().multiply(float1, 0.0, float1));
    }
    
    public void d(@Nullable final PlayerEntity playerEntity) {
        if (this.lootTableId != null && this.world.getServer() != null) {
            final LootSupplier lootSupplier2 = this.world.getServer().getLootManager().getSupplier(this.lootTableId);
            this.lootTableId = null;
            final LootContext.Builder builder3 = new LootContext.Builder((ServerWorld)this.world).<BlockPos>put(LootContextParameters.f, new BlockPos(this)).setRandom(this.lootSeed);
            if (playerEntity != null) {
                builder3.setLuck(playerEntity.getLuck()).<Entity>put(LootContextParameters.a, playerEntity);
            }
            lootSupplier2.supplyInventory(this, builder3.build(LootContextTypes.CHEST));
        }
    }
    
    @Override
    public void clear() {
        this.d((PlayerEntity)null);
        this.inventory.clear();
    }
    
    public void setLootTable(final Identifier id, final long long2) {
        this.lootTableId = id;
        this.lootSeed = long2;
    }
    
    @Nullable
    @Override
    public Container createMenu(final int syncId, final PlayerInventory playerInventory, final PlayerEntity playerEntity) {
        if (this.lootTableId == null || !playerEntity.isSpectator()) {
            this.d(playerInventory.player);
            return this.a(syncId, playerInventory);
        }
        return null;
    }
    
    protected abstract Container a(final int arg1, final PlayerInventory arg2);
}
