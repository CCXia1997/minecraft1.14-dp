package net.minecraft.entity.vehicle;

import net.minecraft.container.HopperContainer;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.item.ItemProvider;
import net.minecraft.entity.damage.DamageSource;
import java.util.List;
import net.minecraft.inventory.Inventory;
import java.util.function.Predicate;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.entity.ItemEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.entity.Hopper;

public class HopperMinecartEntity extends StorageMinecartEntity implements Hopper
{
    private boolean enabled;
    private int transferCooldown;
    private final BlockPos currentBlockPos;
    
    public HopperMinecartEntity(final EntityType<? extends HopperMinecartEntity> type, final World world) {
        super(type, world);
        this.enabled = true;
        this.transferCooldown = -1;
        this.currentBlockPos = BlockPos.ORIGIN;
    }
    
    public HopperMinecartEntity(final World world, final double double2, final double double4, final double double6) {
        super(EntityType.HOPPER_MINECART, double2, double4, double6, world);
        this.enabled = true;
        this.transferCooldown = -1;
        this.currentBlockPos = BlockPos.ORIGIN;
    }
    
    @Override
    public Type getMinecartType() {
        return Type.f;
    }
    
    @Override
    public BlockState getDefaultContainedBlock() {
        return Blocks.fq.getDefaultState();
    }
    
    @Override
    public int getDefaultBlockOffset() {
        return 1;
    }
    
    @Override
    public int getInvSize() {
        return 5;
    }
    
    @Override
    public void onActivatorRail(final int x, final int y, final int z, final boolean boolean4) {
        final boolean boolean5 = !boolean4;
        if (boolean5 != this.isEnabled()) {
            this.setEnabled(boolean5);
        }
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public void setEnabled(final boolean boolean1) {
        this.enabled = boolean1;
    }
    
    @Override
    public World getWorld() {
        return this.world;
    }
    
    @Override
    public double getHopperX() {
        return this.x;
    }
    
    @Override
    public double getHopperY() {
        return this.y + 0.5;
    }
    
    @Override
    public double getHopperZ() {
        return this.z;
    }
    
    @Override
    public void tick() {
        super.tick();
        if (!this.world.isClient && this.isAlive() && this.isEnabled()) {
            final BlockPos blockPos1 = new BlockPos(this);
            if (blockPos1.equals(this.currentBlockPos)) {
                --this.transferCooldown;
            }
            else {
                this.setTransferCooldown(0);
            }
            if (!this.isCoolingDown()) {
                this.setTransferCooldown(0);
                if (this.canOperate()) {
                    this.setTransferCooldown(4);
                    this.markDirty();
                }
            }
        }
    }
    
    public boolean canOperate() {
        if (HopperBlockEntity.extract(this)) {
            return true;
        }
        final List<ItemEntity> list1 = this.world.<ItemEntity>getEntities(ItemEntity.class, this.getBoundingBox().expand(0.25, 0.0, 0.25), EntityPredicates.VALID_ENTITY);
        if (!list1.isEmpty()) {
            HopperBlockEntity.extract(this, list1.get(0));
        }
        return false;
    }
    
    @Override
    public void dropItems(final DamageSource damageSource) {
        super.dropItems(damageSource);
        if (this.world.getGameRules().getBoolean("doEntityDrops")) {
            this.dropItem(Blocks.fq);
        }
    }
    
    @Override
    protected void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("TransferCooldown", this.transferCooldown);
        tag.putBoolean("Enabled", this.enabled);
    }
    
    @Override
    protected void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.transferCooldown = tag.getInt("TransferCooldown");
        this.enabled = (!tag.containsKey("Enabled") || tag.getBoolean("Enabled"));
    }
    
    public void setTransferCooldown(final int integer) {
        this.transferCooldown = integer;
    }
    
    public boolean isCoolingDown() {
        return this.transferCooldown > 0;
    }
    
    public Container a(final int integer, final PlayerInventory playerInventory) {
        return new HopperContainer(integer, playerInventory, this);
    }
}
