package net.minecraft.block.entity;

import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.DefaultedList;
import java.util.List;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.inventory.Inventory;
import net.minecraft.world.loot.context.LootContextTypes;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.BlockView;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;

public abstract class LootableContainerBlockEntity extends LockableContainerBlockEntity
{
    @Nullable
    protected Identifier lootTableId;
    protected long lootTableSeed;
    
    protected LootableContainerBlockEntity(final BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
    }
    
    public static void setLootTable(final BlockView world, final Random random, final BlockPos pos, final Identifier id) {
        final BlockEntity blockEntity5 = world.getBlockEntity(pos);
        if (blockEntity5 instanceof LootableContainerBlockEntity) {
            ((LootableContainerBlockEntity)blockEntity5).setLootTable(id, random.nextLong());
        }
    }
    
    protected boolean deserializeLootTable(final CompoundTag compoundTag) {
        if (compoundTag.containsKey("LootTable", 8)) {
            this.lootTableId = new Identifier(compoundTag.getString("LootTable"));
            this.lootTableSeed = compoundTag.getLong("LootTableSeed");
            return true;
        }
        return false;
    }
    
    protected boolean serializeLootTable(final CompoundTag compoundTag) {
        if (this.lootTableId == null) {
            return false;
        }
        compoundTag.putString("LootTable", this.lootTableId.toString());
        if (this.lootTableSeed != 0L) {
            compoundTag.putLong("LootTableSeed", this.lootTableSeed);
        }
        return true;
    }
    
    public void checkLootInteraction(@Nullable final PlayerEntity playerEntity) {
        if (this.lootTableId != null && this.world.getServer() != null) {
            final LootSupplier lootSupplier2 = this.world.getServer().getLootManager().getSupplier(this.lootTableId);
            this.lootTableId = null;
            final LootContext.Builder builder3 = new LootContext.Builder((ServerWorld)this.world).<BlockPos>put(LootContextParameters.f, new BlockPos(this.pos)).setRandom(this.lootTableSeed);
            if (playerEntity != null) {
                builder3.setLuck(playerEntity.getLuck()).<Entity>put(LootContextParameters.a, playerEntity);
            }
            lootSupplier2.supplyInventory(this, builder3.build(LootContextTypes.CHEST));
        }
    }
    
    public void setLootTable(final Identifier id, final long long2) {
        this.lootTableId = id;
        this.lootTableSeed = long2;
    }
    
    @Override
    public ItemStack getInvStack(final int slot) {
        this.checkLootInteraction(null);
        return this.getInvStackList().get(slot);
    }
    
    @Override
    public ItemStack takeInvStack(final int slot, final int integer2) {
        this.checkLootInteraction(null);
        final ItemStack itemStack3 = Inventories.splitStack(this.getInvStackList(), slot, integer2);
        if (!itemStack3.isEmpty()) {
            this.markDirty();
        }
        return itemStack3;
    }
    
    @Override
    public ItemStack removeInvStack(final int slot) {
        this.checkLootInteraction(null);
        return Inventories.removeStack(this.getInvStackList(), slot);
    }
    
    @Override
    public void setInvStack(final int slot, final ItemStack itemStack) {
        this.checkLootInteraction(null);
        this.getInvStackList().set(slot, itemStack);
        if (itemStack.getAmount() > this.getInvMaxStackAmount()) {
            itemStack.setAmount(this.getInvMaxStackAmount());
        }
        this.markDirty();
    }
    
    @Override
    public boolean canPlayerUseInv(final PlayerEntity playerEntity) {
        return this.world.getBlockEntity(this.pos) == this && playerEntity.squaredDistanceTo(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64.0;
    }
    
    @Override
    public void clear() {
        this.getInvStackList().clear();
    }
    
    protected abstract DefaultedList<ItemStack> getInvStackList();
    
    protected abstract void setInvStackList(final DefaultedList<ItemStack> arg1);
    
    @Override
    public boolean checkUnlocked(final PlayerEntity player) {
        return super.checkUnlocked(player) && (this.lootTableId == null || !player.isSpectator());
    }
    
    @Nullable
    @Override
    public Container createMenu(final int syncId, final PlayerInventory playerInventory, final PlayerEntity playerEntity) {
        if (this.checkUnlocked(playerEntity)) {
            this.checkLootInteraction(playerInventory.player);
            return this.createContainer(syncId, playerInventory);
        }
        return null;
    }
}
